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
    data class ChatUpdated(val chatId: String) : SocketEvent() // Simplified
}

class ChatSocket(
    private val client: OkHttpClient = OkHttpClient(),
    private val origin: String = "https://noveo.ir"
) {

    fun connect(session: Session, getKnownUsers: () -> Map<String, UserSummary>): Flow<SocketEvent> = callbackFlow {
        val request = Request.Builder()
            .url("wss://noveo.ir:8443/ws")
            .header("Origin", origin)
            .build()

        val socket: WebSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(
                    JSONObject()
                        .put("type", "reconnect")
                        .put("userId", session.userId)
                        .put("token", session.token)
                        .put("sessionId", session.sessionId)
                        .toString()
                )
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = JSONObject(text)
                val type = json.optString("type")
                val knownUsers = getKnownUsers()
                
                when (type) {
                    "message", "new_message" -> trySend(SocketEvent.NewMessage(parseRealtimeMessage(json, knownUsers)))
                    "message_sent" -> trySend(SocketEvent.MessageSent(parseRealtimeMessage(json, knownUsers)))
                    "typing" -> trySend(SocketEvent.Typing(json.optString("chatId"), json.optString("senderId")))
                    "message_seen", "message_seen_update" -> {
                         trySend(SocketEvent.MessageSeenUpdate(
                             json.optString("chatId"),
                             json.optString("messageId"),
                             json.optString("userId")
                         ))
                    }
                    "user_list_update" -> {
                        val (users, online) = parseUsers(json)
                        trySend(SocketEvent.UserListUpdate(users, online))
                    }
                    "chat_updated" -> trySend(SocketEvent.ChatUpdated(json.optString("chatId")))
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                val code = response?.code
                val message = when (code) {
                    404 -> "Noveo realtime server was not found (HTTP 404)."
                    401, 403 -> "Noveo rejected the realtime connection (HTTP $code)."
                    else -> t.message ?: t.javaClass.simpleName
                }
                close(IllegalStateException(message, t))
            }
        })

        awaitClose { socket.cancel() }
    }
}
