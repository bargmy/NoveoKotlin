package ir.hienob.noveo.data

import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class ChatSocket(
    private val client: OkHttpClient = OkHttpClient(),
    private val wsUrl: String = "wss://noveo.ir:8443/ws",
    private val origin: String = "https://noveo.ir"
) {
    private var webSocket: WebSocket? = null
    
    private val _events = MutableSharedFlow<SocketEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<SocketEvent> = _events.asSharedFlow()

    private var currentSession: Session? = null

    sealed interface SocketEvent {
        data object Connected : SocketEvent
        data class Error(val message: String) : SocketEvent
        data class NewMessage(val message: ChatMessage) : SocketEvent
        data class UserUpdate(val users: Map<String, UserSummary>, val onlineIds: Set<String>) : SocketEvent
    }

    fun connect(session: Session) {
        currentSession = session
        val request = Request.Builder()
            .url(wsUrl)
            .header("Origin", origin)
            .build()

        webSocket?.cancel()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("ChatSocket", "Connected to Noveo WebSocket")
                _events.tryEmit(SocketEvent.Connected)
                webSocket.send(
                    JSONObject()
                        .put("type", "reconnect")
                        .put("userId", session.userId)
                        .put("token", session.token)
                        .put("sessionId", session.sessionId)
                        .toString()
                )
                webSocket.send(JSONObject().put("type", "resync_state").toString())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("ChatSocket", "Incoming: $text")
                val json = JSONObject(text)
                when (json.optString("type")) {
                    "new_message" -> {
                        // Note: We might not have full user info here if it's a new user,
                        // but AppViewModel can handle that by requesting resync or using its cache
                        _events.tryEmit(SocketEvent.NewMessage(parseRealtimeMessage(json, emptyMap())))
                    }
                    "user_list_update" -> {
                        val (users, online) = parseUsers(json)
                        _events.tryEmit(SocketEvent.UserUpdate(users, online))
                    }
                    "auth_failed", "error" -> {
                        _events.tryEmit(SocketEvent.Error(json.optString("message", "Realtime error")))
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                val code = response?.code
                val message = when (code) {
                    404 -> "Noveo realtime server not found (404)."
                    401, 403 -> "Noveo rejected connection ($code)."
                    else -> t.message ?: "Connection failure"
                }
                Log.e("ChatSocket", "Failure: $message", t)
                _events.tryEmit(SocketEvent.Error(message))
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("ChatSocket", "Closing: $code / $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("ChatSocket", "Closed: $code / $reason")
            }
        })
    }

    fun sendMessage(chatId: String, text: String): Boolean {
        val socket = webSocket ?: return false
        val payload = JSONObject()
            .put("type", "message")
            .put("chatId", chatId)
            .put("content", text)
        return socket.send(payload.toString())
    }

    fun disconnect() {
        webSocket?.close(1000, "Logout")
        webSocket = null
        currentSession = null
    }
}
