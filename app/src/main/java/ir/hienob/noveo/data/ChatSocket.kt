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
    data class NewMessage(val message: ChatMessage) : SocketEvent()
    data class MessageSent(val message: ChatMessage) : SocketEvent()
    data class Typing(val chatId: String, val senderId: String) : SocketEvent()
    data class MessageSeenUpdate(val chatId: String, val messageId: String, val userId: String) : SocketEvent()
    data class UserListUpdate(val usersById: Map<String, UserSummary>, val onlineIds: Set<String>) : SocketEvent()
    data class ChatUpdated(val chatId: String) : SocketEvent()
    data class HistoryUpdate(
        val chats: List<ChatSummary>,
        val users: Map<String, UserSummary>,
        val messagesByChat: Map<String, List<ChatMessage>>
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
        val sent = socket.send(payload.toString())
        if (!sent) {
            onSocketFrame?.invoke("!! SEND_FAILED: Socket is closed/closing")
        }
        return sent
    }

    private var onSocketFrame: ((String) -> Unit)? = null

    fun connect(
        session: Session,
        getKnownUsers: () -> Map<String, UserSummary>,
        onDebug: (String) -> Unit = {},
        onSocketFrame: (String) -> Unit = {}
    ): Flow<SocketEvent> = callbackFlow {
        this@ChatSocket.onSocketFrame = onSocketFrame
        val request = Request.Builder()
            .url("wss://noveo.ir:8443/ws")
            .header("Origin", origin)
            .build()

        val socket: WebSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                activeSocket = webSocket
                onSocketFrame("!! CONNECTED")
                onDebug("ws open")
                val reconnectPayload = JSONObject()
                    .put("type", "reconnect")
                    .put("userId", session.userId)
                    .put("token", session.token)
                    .put("sessionId", session.sessionId)
                
                onSocketFrame("TX $reconnectPayload")
                webSocket.send(reconnectPayload.toString())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onSocketFrame("RX $text")
                onDebug("ws raw=${text.truncateForDebug()}")
                runCatching {
                    val json = JSONObject(text)
                    val type = json.optString("type")
                    val knownUsers = getKnownUsers()
                    val payload = json.unwrapRealtimePayload()
                    onDebug("ws parsedType=$type")

                    when (type) {
                        "login_success" -> {
                            onDebug("ws action=logged_in")
                            // Server triggers sync automatically, no need to request it here
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
                    }
                }.onFailure {
                    onDebug("ws parseFailure=${it.message}")
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
                onDebug("ws failure=$message")
                channel.close(IllegalStateException(message, t))
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                activeSocket = null
                onDebug("ws closed code=$code reason=${reason.ifBlank { "<empty>" }}")
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

private fun String.truncateForDebug(maxLength: Int = 2000): String {
    if (length <= maxLength) return this
    return take(maxLength) + "...<truncated>"
}
