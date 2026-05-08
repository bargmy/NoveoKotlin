package ir.hienob.noveo.desktop.data

import ir.hienob.noveo.core.ui.NoveoHomeChat
import ir.hienob.noveo.core.ui.NoveoHomeMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

private const val CLIENT_VERSION = "desktop"

internal data class DesktopSession(
    val userId: String,
    val token: String,
    val sessionId: String = "",
    val expiresAt: Long = 0L
)

internal data class DesktopHomeSnapshot(
    val session: DesktopSession,
    val chats: List<NoveoHomeChat>,
    val messagesByChat: Map<String, List<NoveoHomeMessage>>,
    val totalUnreadCount: Int
)

internal class DesktopNoveoApi(
    private val client: OkHttpClient = OkHttpClient(),
    private val wsUrl: String = "wss://noveo.ir:8443/ws",
    private val origin: String = "https://noveo.ir"
) {
    fun login(handle: String, password: String): DesktopSession = auth(
        JSONObject()
            .put("type", "login_with_password")
            .put("username", handle)
            .put("password", password)
            .put("languageCode", Locale.getDefault().language.ifBlank { "en" })
            .put("clientInfo", clientInfoJson())
    )

    fun loadHome(session: DesktopSession): DesktopHomeSnapshot {
        val sync = sync(session)
        val chats = parseChats(sync.history, sync.usersById, session.userId)
        val messagesByChat = parseMessagesByChat(sync.history, sync.usersById, session.userId)
        return DesktopHomeSnapshot(
            session = session,
            chats = chats,
            messagesByChat = messagesByChat,
            totalUnreadCount = chats.sumOf { it.unreadCount }
        )
    }

    fun sendMessage(session: DesktopSession, chatId: String, text: String) {
        val latch = CountDownLatch(1)
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val content = JSONObject().put("text", text.takeIf { it.isNotBlank() }).toString()
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnect(session).toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> {
                        webSocket.send(
                            JSONObject()
                                .put("type", "message")
                                .put("chatId", chatId)
                                .put("content", content)
                                .toString()
                        )
                    }
                    "new_message", "message_sent", "chat_history" -> {
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed", "error" -> {
                        failure.set(msg.optString("message", "Unable to send"))
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set(fail(response, t, "sending message"))
                if (done.compareAndSet(false, true)) latch.countDown()
            }
        })
        val finished = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Send timeout")
        failure.get()?.let { error(it) }
    }

    private fun auth(payload: JSONObject): DesktopSession {
        val latch = CountDownLatch(1)
        val result = AtomicReference<DesktopSession?>(null)
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
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
                            failure.set("Missing session token")
                        } else {
                            result.set(DesktopSession(userId, token, msg.optString("sessionId"), msg.optLong("expiresAt", 0L)))
                        }
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed" -> {
                        failure.set(msg.optString("message", "Authentication failed"))
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set(fail(response, t, "authenticating"))
                if (done.compareAndSet(false, true)) latch.countDown()
            }
        })
        val finished = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Auth timeout")
        failure.get()?.let { error(it) }
        return result.get() ?: error("Authentication failed")
    }

    private fun sync(session: DesktopSession): DesktopSyncSnapshot {
        val latch = CountDownLatch(1)
        val history = AtomicReference<JSONObject?>(null)
        val users = AtomicReference<Map<String, DesktopUser>>(emptyMap())
        val online = AtomicReference<Set<String>>(emptySet())
        val failure = AtomicReference<String?>(null)
        val done = AtomicBoolean(false)
        val socket = client.newWebSocket(request(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(reconnect(session).toString())
            }

            override fun onMessage(webSocket: WebSocket, textMsg: String) {
                val msg = JSONObject(textMsg)
                when (msg.optString("type")) {
                    "login_success" -> webSocket.send(JSONObject().put("type", "resync_state").toString())
                    "user_list_update" -> {
                        val parsed = parseUsers(msg)
                        users.set(parsed.first)
                        online.set(parsed.second)
                    }
                    "chat_history" -> {
                        history.set(msg)
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                    "auth_failed" -> {
                        failure.set(msg.optString("message", "Authentication failed"))
                        if (done.compareAndSet(false, true)) latch.countDown()
                        webSocket.close(1000, null)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                failure.set(fail(response, t, "loading chats"))
                if (done.compareAndSet(false, true)) latch.countDown()
            }
        })
        val finished = latch.await(20, TimeUnit.SECONDS)
        socket.cancel()
        if (!finished) error("Sync timeout")
        failure.get()?.let { error(it) }
        return DesktopSyncSnapshot(users.get(), online.get(), history.get() ?: JSONObject().put("chats", JSONArray()))
    }

    private fun request(): Request = Request.Builder()
        .url(wsUrl)
        .header("Origin", origin)
        .header("User-Agent", "NoveoKotlin/$CLIENT_VERSION")
        .header("X-Noveo-Client", "kotlin-desktop")
        .header("X-Noveo-Version", CLIENT_VERSION)
        .build()

    private fun reconnect(session: DesktopSession): JSONObject = JSONObject()
        .put("type", "reconnect")
        .put("userId", session.userId)
        .put("token", session.token)
        .put("sessionId", session.sessionId)
        .put("clientInfo", clientInfoJson())

    private fun clientInfoJson(): JSONObject = JSONObject()
        .put("client", "kotlin")
        .put("platform", "desktop")
        .put("version", CLIENT_VERSION)

    private fun fail(response: Response?, t: Throwable, context: String): String {
        val code = response?.code
        return when (code) {
            404 -> "Noveo realtime server was not found while $context (HTTP 404)."
            401, 403 -> "Noveo rejected the realtime connection while $context (HTTP $code)."
            else -> "Socket failure while $context: ${t.message ?: t.javaClass.simpleName}"
        }
    }
}

private data class DesktopSyncSnapshot(
    val usersById: Map<String, DesktopUser>,
    val onlineUserIds: Set<String>,
    val history: JSONObject
)

private data class DesktopUser(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val handle: String? = null,
    val isOnline: Boolean = false,
    val isVerified: Boolean = false
)

private fun parseUsers(payload: JSONObject): Pair<Map<String, DesktopUser>, Set<String>> {
    val onlineIds = mutableSetOf<String>()
    val onlineArray = payload.optJSONArray("online") ?: JSONArray()
    for (index in 0 until onlineArray.length()) {
        onlineArray.optString(index).sanitizeServerString().takeIf { it.isNotBlank() }?.let(onlineIds::add)
    }

    val users = mutableMapOf<String, DesktopUser>()
    val usersArray = payload.optJSONArray("users") ?: JSONArray()
    for (index in 0 until usersArray.length()) {
        val item = usersArray.optJSONObject(index) ?: continue
        val userId = item.optString("userId").sanitizeServerString().ifBlank { item.optString("id").sanitizeServerString() }
        if (userId.isBlank()) continue
        users[userId] = DesktopUser(
            id = userId,
            username = item.optString("username").sanitizeServerString().ifBlank { item.optString("name").sanitizeServerString().ifBlank { "Unknown" } },
            avatarUrl = resolveAssetUrl(item, "avatarUrl", "avatar", "photo", "image"),
            handle = item.optString("handle").sanitizeServerString().takeIf { it.isNotBlank() },
            isOnline = onlineIds.contains(userId) || item.optBoolean("online", false),
            isVerified = item.optBoolean("isVerified", false)
        )
    }
    return users to onlineIds
}

private fun parseChats(payload: JSONObject, usersById: Map<String, DesktopUser>, selfUserId: String): List<NoveoHomeChat> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    return buildList {
        for (index in 0 until chatsArray.length()) {
            val item = chatsArray.optJSONObject(index) ?: continue
            val chatId = item.optString("chatId").sanitizeServerString()
                .ifBlank { item.optString("chat_id").sanitizeServerString() }
                .ifBlank { item.optString("id").sanitizeServerString() }
            if (chatId.isBlank()) continue
            val memberIds = parseStringList(item.optJSONArray("members"))
            val messages = item.optJSONArray("messages") ?: JSONArray()
            val unreadCount = (0 until messages.length()).count { messageIndex ->
                val msg = messages.optJSONObject(messageIndex) ?: return@count false
                val senderId = msg.optString("senderId").ifBlank { msg.optString("sender") }
                if (senderId == selfUserId) return@count false
                !parseStringList(msg.optJSONArray("seenBy")).contains(selfUserId)
            }
            val lastMsg = if (messages.length() > 0) messages.optJSONObject(messages.length() - 1) else null
            val preview = lastMsg?.let { parseMessageText(it.opt("content")) }.orEmpty()
            val timestamp = lastMsg?.optLong("timestamp", 0L) ?: 0L
            val chatType = item.optString("chatType", item.optString("type", "private")).sanitizeServerString()
            val ownerId = item.optString("ownerId").sanitizeServerString()
            val canChat = when (chatType) {
                "channel" -> ownerId == selfUserId
                "group" -> item.optJSONObject("permissions")?.optBoolean("canSendMessages", true) ?: true
                else -> true
            }
            add(
                NoveoHomeChat(
                    id = chatId,
                    title = resolveChatTitle(item, usersById, memberIds, selfUserId),
                    subtitle = preview,
                    time = formatTime(timestamp),
                    unreadCount = unreadCount,
                    isOnline = memberIds.any { usersById[it]?.isOnline == true && it != selfUserId },
                    isVerified = item.optBoolean("isVerified", false),
                    canChat = canChat
                )
            )
        }
    }.sortedByDescending { chat -> chatsArrayIndexTime(payload, chat.id) }
}

private fun chatsArrayIndexTime(payload: JSONObject, chatId: String): Long {
    val chatsArray = payload.optJSONArray("chats") ?: return 0L
    for (index in 0 until chatsArray.length()) {
        val item = chatsArray.optJSONObject(index) ?: continue
        val itemChatId = item.optString("chatId").sanitizeServerString()
            .ifBlank { item.optString("chat_id").sanitizeServerString() }
            .ifBlank { item.optString("id").sanitizeServerString() }
        if (itemChatId != chatId) continue
        val messages = item.optJSONArray("messages") ?: return 0L
        return if (messages.length() > 0) messages.optJSONObject(messages.length() - 1)?.optLong("timestamp", 0L) ?: 0L else 0L
    }
    return 0L
}

private fun parseMessagesByChat(payload: JSONObject, usersById: Map<String, DesktopUser>, selfUserId: String): Map<String, List<NoveoHomeMessage>> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    return buildMap {
        for (index in 0 until chatsArray.length()) {
            val item = chatsArray.optJSONObject(index) ?: continue
            val chatId = item.optString("chatId").sanitizeServerString()
                .ifBlank { item.optString("chat_id").sanitizeServerString() }
                .ifBlank { item.optString("id").sanitizeServerString() }
            if (chatId.isBlank()) continue
            val messagesArray = item.optJSONArray("messages") ?: JSONArray()
            val messages = buildList {
                for (messageIndex in 0 until messagesArray.length()) {
                    val message = messagesArray.optJSONObject(messageIndex) ?: continue
                    add(parseMessage(message, chatId, usersById, selfUserId))
                }
            }
            put(chatId, messages)
        }
    }
}

private fun parseMessage(message: JSONObject, chatId: String, usersById: Map<String, DesktopUser>, selfUserId: String): NoveoHomeMessage {
    val senderId = message.optString("senderId").sanitizeServerString().ifBlank { message.optString("sender").sanitizeServerString() }
    val messageId = message.optString("messageId").sanitizeServerString().ifBlank { message.optString("id").sanitizeServerString() }
    return NoveoHomeMessage(
        id = messageId.ifBlank { "${chatId}-${message.optLong("timestamp", 0L)}" },
        senderId = senderId,
        senderName = usersById[senderId]?.username ?: message.optString("senderName").sanitizeServerString().ifBlank { "Unknown" },
        text = parseMessageText(message.opt("content")).ifBlank { " " },
        time = formatTime(message.optLong("timestamp", message.optLong("createdAt", 0L))),
        isOutgoing = senderId == selfUserId,
        pending = message.optBoolean("pending", false),
        edited = message.optLong("editedAt", 0L) > 0,
        forwarded = message.optJSONObject("forwardedInfo") != null
    )
}

private fun parseMessageText(raw: Any?): String {
    val payload = when (raw) {
        is JSONObject -> raw
        is String -> {
            val text = raw.sanitizeServerString()
            if (text.startsWith("{") || text.startsWith("[")) {
                runCatching { JSONObject(text) }.getOrNull() ?: return text
            } else {
                return text
            }
        }
        else -> return raw?.toString().sanitizeServerString()
    }
    payload.optString("text").sanitizeServerString().takeIf { it.isNotBlank() }?.let { return it }
    payload.optJSONObject("file")?.let { file ->
        val type = file.optString("type")
        val name = file.optString("name").sanitizeServerString()
        return when {
            type.startsWith("image/", true) -> "Photo"
            type.startsWith("video/", true) -> "Video"
            type.startsWith("audio/", true) -> "Audio"
            name.isNotBlank() -> name
            else -> "File"
        }
    }
    payload.optJSONObject("poll")?.let { return "Poll" }
    payload.optJSONObject("callLog")?.let { return "Voice Call" }
    return ""
}

private fun resolveChatTitle(chat: JSONObject, usersById: Map<String, DesktopUser>, memberIds: List<String>, selfUserId: String): String {
    chat.optString("chatName").sanitizeServerString().takeIf { it.isNotBlank() }?.let { return it }
    val chatType = chat.optString("chatType", chat.optString("type", "private")).sanitizeServerString()
    if (chatType == "private") {
        if (memberIds.size == 1 && memberIds.firstOrNull() == selfUserId) return "Saved Messages"
        return memberIds.firstOrNull { it != selfUserId }
            ?.let(usersById::get)
            ?.username
            ?.takeIf { it.isNotBlank() }
            ?: "Direct Message"
    }
    return chat.optString("title").sanitizeServerString().ifBlank { "Chat" }
}

private fun resolveAssetUrl(source: JSONObject, vararg keys: String): String? {
    for (key in keys) {
        val direct = source.optString(key).sanitizeServerString()
        if (direct.isNotBlank()) return direct
        val nested = source.optJSONObject(key) ?: continue
        listOf("url", "src", "path", "downloadUrl", "fileUrl", "thumbUrl", "thumbnailUrl")
            .map { nested.optString(it).sanitizeServerString() }
            .firstOrNull { it.isNotBlank() }
            ?.let { return it }
    }
    return null
}

private fun parseStringList(array: JSONArray?): List<String> {
    if (array == null) return emptyList()
    return buildList {
        for (index in 0 until array.length()) array.optString(index).sanitizeServerString().takeIf { it.isNotBlank() }?.let(::add)
    }
}

private fun formatTime(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    val millis = if (timestamp < 10_000_000_000L) timestamp * 1000L else timestamp
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))
}

private fun String?.sanitizeServerString(): String {
    val value = this?.trim().orEmpty()
    val lower = value.lowercase()
    return if (lower == "null" || lower == "undefined" || lower == "none") "" else value
}
