package ir.hienob.noveo.data

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

data class VoiceChatState(
    val connectionState: VoiceConnectionState = VoiceConnectionState.IDLE,
    val currentChatId: String? = null,
    val currentCallId: String? = null,
    val currentRoomName: String? = null,
    val isMuted: Boolean = true,
    val isDeafened: Boolean = false,
    val isMinimized: Boolean = false,
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
    private val api: NoveoApi
) {
    companion object {
        @Volatile
        private var instance: VoiceChatManager? = null

        fun getInstance(context: Context, api: NoveoApi): VoiceChatManager {
            return instance ?: synchronized(this) {
                instance ?: VoiceChatManager(context.applicationContext, api).also { instance = it }
            }
        }
        
        fun getExisting(): VoiceChatManager? = instance
    }

    private val _state = MutableStateFlow(VoiceChatState())
    val state = _state.asStateFlow()

    fun joinCall(session: Session, chatId: String, callId: String? = null) {
        // No-op in Lite version
    }

    fun leaveCall() {
        // No-op in Lite version
    }

    fun toggleMute() {
        // No-op in Lite version
    }

    fun toggleMinimize() {
        // No-op in Lite version
    }

    fun toggleDeafen() {
        // No-op in Lite version
    }
}
