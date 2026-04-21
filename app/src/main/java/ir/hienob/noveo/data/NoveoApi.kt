package ir.hienob.noveo.data

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject

class NoveoApi(
    private val client: OkHttpClient = OkHttpClient(),
    private val wsUrl: String = "wss://noveo.ir/ws",
    private val origin: String = "https://localhost"
) {

    fun login(handle: String, password: String): Session = authOverSocket(
        payload = JSONObject()
            .put("type", "login_with_password")
            .put("username", handle)
            .put("password", password)
            .put("languageCode", "en")
    )

    fun signup(handle: String, password: String): Session = authOverSocket(
        payload = JSONObject()
            .put("type", "register")
            .put("username", handle)
            .put("password", password)
            .put("languageCode", "en")
    )

    fun getChats(session: Session): List<ChatSummary> {
        val history = requestChatHistory(session)
        return parseChats(history)
    }

    fun getMessages(session: Session, chatId: Long): List<ChatMessage> {
        val history = requestChatHistory(session)
        val chats = history.optJSONArray("chats") ?: JSONArray()
        for (i in 0 until chats.length()) {
            val chat = chats.getJSONObject(i)
            val id = chat.optLong("chatId", chat.optLong("id"))
            if (id == chatId) {
                val arr = chat.optJSONArray("messages") ?: JSONArray()
                return parseMessages(arr, chatId)
            }
        }
        return emptyList()
    }

    fun sendMessage(session: Session, chatId: Long, text: String) {
        val latch = CountDownLatch(1)
        val failure = AtomicReference<String?>(null)
        val completed = AtomicBoolean(false)

        val socket = client.newWebSocket(Request.Builder().url(wsUrl).header("Origin", origin).build(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnectPayload(session).toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> webSocket.send(JSONObject().put("type", "message").put("chatId", chatId).put("content", text).toString())
                    "new_message", "message_sent", "chat_history" -> {
                        if (completed.compareAndSet(false, true)) latch.countDown(); webSocket.close(1000, null)
                    }
                    "auth_failed" -> {
                        failure.set(msg.optString("message", "Authentication failed"))
                        if (completed.compareAndSet(false, true)) latch.countDown(); webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set("Socket failure: ${t.message ?: t.javaClass.simpleName}")
                if (completed.compareAndSet(false, true)) latch.countDown()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                if (completed.compareAndSet(false, true)) {
                    failure.set("Socket closed before ack: code=$code reason=$reason")
                    latch.countDown()
                }
            }
        })

        val done = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!done) error("Send timeout (no ack frame)")
        failure.get()?.let { error(it) }
    }

    private fun authOverSocket(payload: JSONObject): Session {
        val latch = CountDownLatch(1)
        val session = AtomicReference<Session?>(null)
        val failure = AtomicReference<String?>(null)
        val completed = AtomicBoolean(false)

        val socket = client.newWebSocket(Request.Builder().url(wsUrl).header("Origin", origin).build(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(payload.toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> {
                        val user = msg.optJSONObject("user")
                        val userId = user?.optString("userId").orEmpty()
                        val token = msg.optString("token")
                        if (userId.isBlank() || token.isBlank()) {
                            failure.set("Missing session token in login response")
                        } else {
                            session.set(
                                Session(
                                    userId = userId,
                                    token = token,
                                    sessionId = msg.optString("sessionId"),
                                    expiresAt = msg.optLong("expiresAt", 0L)
                                )
                            )
                        }
                        if (completed.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed" -> {
                        failure.set(msg.optString("message", "Authentication failed"))
                        if (completed.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set("Socket failure: ${t.message ?: t.javaClass.simpleName}")
                if (completed.compareAndSet(false, true)) latch.countDown()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                if (completed.compareAndSet(false, true)) {
                    failure.set("Socket closed before auth result: code=$code reason=$reason")
                    latch.countDown()
                }
            }
        })

        val done = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!done) error("Auth timeout (no login_success/auth_failed frame)")
        failure.get()?.let { error(it) }
        return session.get() ?: error("Authentication failed")
    }

    private fun requestChatHistory(session: Session): JSONObject {
        val latch = CountDownLatch(1)
        val history = AtomicReference<JSONObject?>(null)
        val failure = AtomicReference<String?>(null)
        val completed = AtomicBoolean(false)

        val socket = client.newWebSocket(Request.Builder().url(wsUrl).header("Origin", origin).build(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnectPayload(session).toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> webSocket.send(JSONObject().put("type", "resync_state").toString())
                    "chat_history" -> {
                        history.set(msg)
                        if (completed.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed" -> {
                        failure.set(msg.optString("message", "Authentication failed"))
                        if (completed.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set("Socket failure: ${t.message ?: t.javaClass.simpleName}")
                if (completed.compareAndSet(false, true)) latch.countDown()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                if (completed.compareAndSet(false, true)) {
                    failure.set("Socket closed before chat_history: code=$code reason=$reason")
                    latch.countDown()
                }
            }
        })

        val done = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!done) error("Chat history timeout (no chat_history frame)")
        failure.get()?.let { error(it) }
        return history.get() ?: JSONObject().put("chats", JSONArray())
    }

    private fun reconnectPayload(session: Session): JSONObject = JSONObject()
        .put("type", "reconnect")
        .put("userId", session.userId)
        .put("token", session.token)
        .put("sessionId", session.sessionId)

    private fun parseChats(payload: JSONObject): List<ChatSummary> {
        val array = payload.optJSONArray("chats") ?: JSONArray()
        return buildList {
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val messages = item.optJSONArray("messages") ?: JSONArray()
                val last = if (messages.length() > 0) messages.getJSONObject(messages.length() - 1) else null
                add(
                    ChatSummary(
                        id = item.optLong("chatId", item.optLong("id")),
                        title = item.optString("chatName", item.optString("title", "Chat")),
                        lastMessage = last?.optString("content", "") ?: "",
                        unreadCount = item.optInt("unreadCount", item.optInt("unread", 0))
                    )
                )
            }
        }
    }

    private fun parseMessages(array: JSONArray, chatId: Long): List<ChatMessage> = buildList {
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            add(
                ChatMessage(
                    id = item.optLong("messageId", item.optLong("id")),
                    chatId = item.optLong("chatId", chatId),
                    sender = item.optString("senderName", item.optString("sender", "")),
                    text = item.optString("content", item.optString("text", "")),
                    createdAt = item.optString("timestamp", item.optString("createdAt", ""))
                )
            )
        }
    }
}
