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

class ChatSocket(
    private val client: OkHttpClient = OkHttpClient(),
    private val origin: String = "https://noveo.ir"
) {

    fun connect(session: Session): Flow<ChatMessage> = callbackFlow {
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
                if (json.optString("type") != "new_message") return
                trySend(
                    ChatMessage(
                        id = json.optLong("messageId", json.optLong("id")),
                        chatId = json.optLong("chatId"),
                        sender = json.optString("senderName", json.optString("sender")),
                        text = json.optString("content", json.optString("text")),
                        createdAt = json.optString("timestamp", json.optString("createdAt"))
                    )
                )
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                val code = response?.code
                val message = when (code) {
                    404 -> "Noveo realtime server was not found while opening the chat stream (HTTP 404). Check the websocket endpoint on the server."
                    401, 403 -> "Noveo rejected the realtime connection while opening the chat stream (HTTP $code)."
                    else -> t.message ?: t.javaClass.simpleName
                }
                close(IllegalStateException(message, t))
            }
        })

        awaitClose { socket.cancel() }
    }
}
