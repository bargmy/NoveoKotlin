package ir.hienob.noveo.data

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

sealed class SocketEvent {
    data class ConnectionState(val connected: Boolean, val detail: String? = null) : SocketEvent()
    data class NewMessage(val message: ChatMessage) : SocketEvent()
    data class MessageSent(val message: ChatMessage) : SocketEvent()
    data class Typing(val chatId: String, val senderId: String) : SocketEvent()
    data class MessageSeenUpdate(val chatId: String, val messageId: String, val userId: String) : SocketEvent()
    data class MessageReactionUpdate(val chatId: String, val messageId: String, val reactions: Map<String, List<String>>) : SocketEvent()
    data class MessageEditUpdate(val chatId: String, val message: ChatMessage) : SocketEvent()
    data class MessageDeleteUpdate(val chatId: String, val messageId: String) : SocketEvent()
    data class MessagePinUpdate(val chatId: String, val messageId: String, val isPinned: Boolean) : SocketEvent()
    data class UserListUpdate(val usersById: Map<String, UserSummary>, val onlineIds: Set<String>) : SocketEvent()
    data class ChatUpdated(val chatId: String) : SocketEvent()
    data class HistoryUpdate(
        val chats: List<ChatSummary>,
        val users: Map<String, UserSummary>,
        val messagesByChat: Map<String, List<ChatMessage>>
    ) : SocketEvent()
    data class OlderMessages(
        val chatId: String,
        val messages: List<ChatMessage>,
        val hasMoreHistory: Boolean
    ) : SocketEvent()
}

class ChatSocket(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(30, java.util.concurrent.TimeUnit.SECONDS)
        .build(),
    private val origin: String = "https://noveo.ir"
) {
    private var activeSocket: WebSocket? = null
    val isConnected: Boolean get() = activeSocket != null

    fun send(payload: JSONObject): Boolean {
        val socket = activeSocket
        if (socket == null) return false
        return socket.send(payload.toString())
    }

    fun connect(
        session: Session,
        getKnownUsers: () -> Map<String, UserSummary>
    ): Flow<SocketEvent> = callbackFlow {
        val request = Request.Builder()
            .url("wss://noveo.ir:8443/ws")
            .header("Origin", origin)
            .build()

        val socket: WebSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                activeSocket = webSocket
                trySend(SocketEvent.ConnectionState(connected = true))
                val reconnectPayload = JSONObject()
                    .put("type", "reconnect")
                    .put("userId", session.userId)
                    .put("token", session.token)
                    .put("sessionId", session.sessionId)
                
                webSocket.send(reconnectPayload.toString())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runCatching {
                    val json = JSONObject(text)
                    val type = json.optString("type")
                    val knownUsers = getKnownUsers()
                    val payload = json.unwrapRealtimePayload()

                    when (type) {
                        "login_success" -> {
                            val syncPayload = JSONObject().put("type", "resync_state")
                            webSocket.send(syncPayload.toString())
                        }
                        "message", "new_message" -> trySend(SocketEvent.NewMessage(parseRealtimeMessage(json, knownUsers)))
                        "message_sent" -> trySend(SocketEvent.MessageSent(parseRealtimeMessage(json, knownUsers)))
                        "typing" -> {
                            val chatId = payload.optString("chatId").sanitizeRealtimeField()
                                ?: json.optString("chatId").sanitizeRealtimeField()
                            val senderId = payload.optString("senderId").sanitizeRealtimeField()
                                ?: json.optString("senderId").sanitizeRealtimeField()
                            if (chatId != null && senderId != null) {
                                trySend(SocketEvent.Typing(chatId, senderId))
                            }
                        }
                        "message_seen", "message_seen_update" -> {
                            val chatId = payload.optString("chatId").sanitizeRealtimeField()
                                ?: json.optString("chatId").sanitizeRealtimeField()
                            val messageId = payload.optString("messageId").sanitizeRealtimeField()
                                ?: json.optString("messageId").sanitizeRealtimeField()
                            val userId = payload.optString("userId").sanitizeRealtimeField()
                                ?: json.optString("userId").sanitizeRealtimeField()
                            if (chatId != null && messageId != null && userId != null) {
                                trySend(SocketEvent.MessageSeenUpdate(chatId, messageId, userId))
                            }
                        }
                        "message_reaction", "reaction_update", "message_reactions_update" -> {
                            val chatId = payload.optString("chatId").sanitizeRealtimeField()
                            val messageId = payload.optString("messageId").sanitizeRealtimeField()
                            val reactionsObj = payload.optJSONObject("reactions")
                            if (chatId != null && messageId != null && reactionsObj != null) {
                                val reactions = mutableMapOf<String, List<String>>()
                                val keys = reactionsObj.keys()
                                while (keys.hasNext()) {
                                    val emoji = keys.next()
                                    val users = mutableListOf<String>()
                                    val usersArray = reactionsObj.optJSONArray(emoji)
                                    if (usersArray != null) {
                                        for (i in 0 until usersArray.length()) {
                                            usersArray.optString(i).sanitizeRealtimeField()?.let(users::add)
                                        }
                                    }
                                    reactions[emoji] = users
                                }
                                trySend(SocketEvent.MessageReactionUpdate(chatId, messageId, reactions))
                            }
                        }
                        "message_edit", "message_edited" -> {
                            val chatId = payload.optString("chatId").sanitizeRealtimeField()
                            if (chatId != null) {
                                trySend(SocketEvent.MessageEditUpdate(chatId, parseRealtimeMessage(json, knownUsers)))
                            }
                        }
                        "message_delete", "message_deleted" -> {
                            val chatId = payload.optString("chatId").sanitizeRealtimeField()
                            val messageId = payload.optString("messageId").sanitizeRealtimeField()
                            if (chatId != null && messageId != null) {
                                trySend(SocketEvent.MessageDeleteUpdate(chatId, messageId))
                            }
                        }
                        "message_pin", "pin_update" -> {
                            val chatId = payload.optString("chatId").sanitizeRealtimeField()
                            val messageId = payload.optString("messageId").sanitizeRealtimeField()
                            val isPinned = payload.optBoolean("isPinned", payload.optBoolean("pinned", false))
                            if (chatId != null && messageId != null) {
                                trySend(SocketEvent.MessagePinUpdate(chatId, messageId, isPinned))
                            }
                        }
                        "user_list_update" -> {
                            val (users, online) = parseUsers(json)
                            trySend(SocketEvent.UserListUpdate(users, online))
                        }
                        "chat_updated" -> {
                            payload.optString("chatId").sanitizeRealtimeField()
                                ?.let { trySend(SocketEvent.ChatUpdated(it)) }
                        }
                        "chat_history" -> {
                            val users = parseUsers(json).first
                            val combinedUsers = knownUsers + users
                            val chats = parseChats(json, combinedUsers, session.userId)
                            val messagesByChat = parseMessagesByChat(json, combinedUsers)
                            trySend(SocketEvent.HistoryUpdate(chats, users, messagesByChat))
                        }
                        "older_messages" -> {
                            val chatId = json.optString("chatId")
                            val users = parseUsers(json).first
                            val combinedUsers = knownUsers + users
                            val messages = parseChatMessageList(json.optJSONArray("messages"), chatId, combinedUsers)
                            val hasMore = json.optBoolean("hasMoreHistory", false)
                            trySend(SocketEvent.OlderMessages(chatId, messages, hasMore))
                        }
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                activeSocket = null
                val code = response?.code
                val message = when (code) {
                    404 -> "Noveo realtime server was not found (HTTP 404)."
                    401, 403 -> "Noveo rejected the realtime connection (HTTP $code)."
                    else -> t.message ?: t.javaClass.simpleName
                }
                trySend(SocketEvent.ConnectionState(connected = false, detail = message))
                channel.close(IllegalStateException(message, t))
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                activeSocket = null
                trySend(SocketEvent.ConnectionState(connected = false, detail = reason.takeIf { it.isNotBlank() }))
                channel.close()
            }
        })

        awaitClose { 
            activeSocket = null
            socket.cancel() 
        }
    }
}


private fun String?.sanitizeRealtimeField(): String? {
    val value = this?.trim().orEmpty()
    if (value.isBlank()) return null
    return value.takeUnless { it.equals("null", ignoreCase = true) }
}
