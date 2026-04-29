package ir.hienob.noveo.data

import android.content.Context
import io.livekit.android.LiveKit
import io.livekit.android.Room
import io.livekit.android.room.RoomListener
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.participant.RemoteParticipant
import io.livekit.android.room.track.LocalAudioTrack
import io.livekit.android.room.track.LocalAudioTrackOptions
import io.livekit.android.room.track.RemoteAudioTrack
import io.livekit.android.room.track.RemoteVideoTrack
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.TrackPublication
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import timber.log.Timber

data class VoiceChatState(
    val connectionState: VoiceConnectionState = VoiceConnectionState.IDLE,
    val currentChatId: String? = null,
    val currentCallId: String? = null,
    val currentRoomName: String? = null,
    val isMuted: Boolean = false,
    val isDeafened: Boolean = false,
    val activeSpeakers: List<String> = emptyList(),
    val participantIds: List<String> = emptyList(),
    val isScreenSharing: Boolean = false,
    val screenShareOwnerId: String? = null
)

enum class VoiceConnectionState {
    IDLE, CONNECTING, CONNECTED, RECONNECTING
}

class VoiceChatManager(
    private val context: Context,
    private val api: NoveoApi,
    private val scope: CoroutineScope
) {
    private var room: Room? = null
    private var localAudioTrack: LocalAudioTrack? = null
    
    private val _state = MutableStateFlow(VoiceChatState())
    val state = _state.asStateFlow()

    fun joinCall(session: Session, chatId: String, callId: String? = null) {
        scope.launch {
            if (_state.value.currentChatId == chatId && _state.value.connectionState == VoiceConnectionState.CONNECTED) return@launch
            
            if (_state.value.currentChatId != null) {
                leaveCall()
            }

            _state.value = _state.value.copy(
                connectionState = VoiceConnectionState.CONNECTING, 
                currentChatId = chatId,
                currentCallId = callId
            )

            try {
                val tokenPayload = withContext(Dispatchers.IO) {
                    api.getVoiceToken(session, chatId, callId)
                }

                val serverUrl = tokenPayload.getString("serverUrl")
                val token = tokenPayload.getString("participantToken")
                val roomName = tokenPayload.optString("roomName")
                val fetchedCallId = tokenPayload.optString("callId")

                val r = LiveKit.create(context)
                room = r
                
                r.listener = object : RoomListener {
                    override fun onDisconnect(room: Room, error: Exception?) {
                        _state.value = VoiceChatState(connectionState = VoiceConnectionState.IDLE)
                    }

                    override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
                        updateParticipants()
                    }

                    override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {
                        updateParticipants()
                    }

                    override fun onActiveSpeakersChanged(room: Room, speakers: List<Participant>) {
                        val activeIds = mutableListOf<String>()
                        for (s in speakers) {
                            s.identity?.value?.let { activeIds.add(it) }
                        }
                        _state.value = _state.value.copy(activeSpeakers = activeIds)
                    }

                    override fun onReconnecting(room: Room, error: Exception?) {
                        _state.value = _state.value.copy(connectionState = VoiceConnectionState.RECONNECTING)
                    }

                    override fun onReconnected(room: Room) {
                        _state.value = _state.value.copy(connectionState = VoiceConnectionState.CONNECTED)
                        updateParticipants()
                    }

                    override fun onTrackSubscribed(
                        track: Track,
                        publication: TrackPublication,
                        participant: RemoteParticipant
                    ) {
                        if (track is RemoteVideoTrack && publication.source == Track.Source.SCREEN_SHARE) {
                            _state.value = _state.value.copy(
                                isScreenSharing = true,
                                screenShareOwnerId = participant.identity?.value
                            )
                        }
                        updateParticipants()
                    }

                    override fun onTrackUnsubscribed(
                        track: Track,
                        publication: TrackPublication,
                        participant: RemoteParticipant
                    ) {
                        if (track is RemoteVideoTrack && publication.source == Track.Source.SCREEN_SHARE) {
                             if (_state.value.screenShareOwnerId == participant.identity?.value) {
                                 _state.value = _state.value.copy(
                                     isScreenSharing = false,
                                     screenShareOwnerId = null
                                 )
                             }
                        }
                        updateParticipants()
                    }
                }

                r.connect(serverUrl, token)
                
                _state.value = _state.value.copy(
                    connectionState = VoiceConnectionState.CONNECTED,
                    currentCallId = fetchedCallId,
                    currentRoomName = roomName,
                    currentChatId = chatId
                )

                // Enable audio by default (unmuted)
                applyMuteState(false)
                updateParticipants()

            } catch (e: Exception) {
                Timber.e(e, "VoiceChat: Failed to join call")
                _state.value = VoiceChatState(connectionState = VoiceConnectionState.IDLE)
            }
        }
    }

    fun leaveCall() {
        scope.launch {
            room?.disconnect()
            room = null
            localAudioTrack?.stop()
            localAudioTrack = null
            _state.value = VoiceChatState()
        }
    }

    fun toggleMute() {
        scope.launch {
            val nextMuted = !_state.value.isMuted
            applyMuteState(nextMuted)
        }
    }

    private suspend fun applyMuteState(muted: Boolean) {
        val r = room ?: return
        if (muted) {
            localAudioTrack?.let { 
                r.localParticipant.unpublishTrack(it)
                it.stop()
            }
            localAudioTrack = null
        } else {
            if (localAudioTrack == null) {
                val track = r.localParticipant.createAudioTrack("audio", LocalAudioTrackOptions())
                localAudioTrack = track
                r.localParticipant.publishTrack(track)
            }
        }
        _state.value = _state.value.copy(isMuted = muted)
    }

    fun toggleDeafen() {
        setDeafened(!_state.value.isDeafened)
    }

    private fun setDeafened(nextDeafened: Boolean) {
        // In LiveKit Android, deafening usually involves muting all remote audio tracks
        room?.participants?.values?.forEach { participant ->
            if (participant is RemoteParticipant) {
                participant.audioTrackPublications.forEach { pub ->
                    pub.track?.let { track ->
                        if (track is RemoteAudioTrack) {
                            // track.enabled = !nextDeafened
                        }
                    }
                }
            }
        }
        _state.value = _state.value.copy(isDeafened = nextDeafened)
    }

    private fun updateParticipants() {
        val r = room ?: return
        val participantsList = mutableListOf<String>()
        for (p in r.participants.values) {
            val identityValue = p.identity?.value?.toString()
            if (identityValue != null && !participantsList.contains(identityValue)) {
                participantsList.add(identityValue)
            }
        }
        _state.value = _state.value.copy(participantIds = participantsList)
    }
}
