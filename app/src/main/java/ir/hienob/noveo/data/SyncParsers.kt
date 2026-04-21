package ir.hienob.noveo.data

import org.json.JSONArray
import org.json.JSONObject

internal data class SyncSnapshot(
    val usersById: Map<String, UserSummary>,
    val onlineUserIds: Set<String>,
    val history: JSONObject
)

internal fun parseUsers(payload: JSONObject): Pair<Map<String, UserSummary>, Set<String>> {
    val onlineIds = mutableSetOf<String>()
    val onlineArray = payload.optJSONArray("online") ?: JSONArray()
    for (index in 0 until onlineArray.length()) {
        onlineArray.optString(index).takeIf { it.isNotBlank() }?.let(onlineIds::add)
    }

    val users = mutableMapOf<String, UserSummary>()
    val usersArray = payload.optJSONArray("users") ?: JSONArray()
    for (index in 0 until usersArray.length()) {
        val item = usersArray.optJSONObject(index) ?: continue
        val userId = item.optString("userId")
        if (userId.isBlank()) continue
        users[userId] = UserSummary(
            id = userId,
            username = item.optString("username", "Unknown"),
            avatarUrl = item.optString("avatarUrl").takeIf { it.isNotBlank() },
            handle = item.optString("handle").takeIf { it.isNotBlank() },
            bio = item.optString("bio"),
            isOnline = onlineIds.contains(userId),
            isVerified = item.optBoolean("isVerified", false)
        )
    }
    return users to onlineIds
}

internal fun parseChats(payload: JSONObject, usersById: Map<String, UserSummary>, selfUserId: String): List<ChatSummary> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    return buildList {
        for (index in 0 until chatsArray.length()) {
            val item = chatsArray.optJSONObject(index) ?: continue
            val chatId = item.optString("chatId").ifBlank { item.optString("id") }
            if (chatId.isBlank()) continue
            val memberIds = parseStringList(item.optJSONArray("members"))
            val messages = item.optJSONArray("messages") ?: JSONArray()
            val preview = if (messages.length() > 0) {
                parseMessageContent(messages.optJSONObject(messages.length() - 1)?.opt("content")).previewText()
            } else {
                ""
            }
            add(
                ChatSummary(
                    id = chatId,
                    chatType = item.optString("chatType", "private"),
                    title = resolveChatTitle(item, usersById, memberIds, selfUserId),
                    avatarUrl = resolveChatAvatar(item, usersById, memberIds, selfUserId),
                    lastMessagePreview = preview,
                    unreadCount = item.optInt("unreadCount", item.optInt("unread", 0)),
                    memberIds = memberIds,
                    handle = item.optString("handle").takeIf { it.isNotBlank() },
                    isVerified = item.optBoolean("isVerified", false)
                )
            )
        }
    }
}

internal fun parseMessagesForChat(payload: JSONObject, usersById: Map<String, UserSummary>, chatId: String): List<ChatMessage> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    for (index in 0 until chatsArray.length()) {
        val item = chatsArray.optJSONObject(index) ?: continue
        val itemChatId = item.optString("chatId").ifBlank { item.optString("id") }
        if (itemChatId != chatId) continue
        val messagesArray = item.optJSONArray("messages") ?: JSONArray()
        return buildList {
            for (messageIndex in 0 until messagesArray.length()) {
                val message = messagesArray.optJSONObject(messageIndex) ?: continue
                add(parseChatMessage(message, itemChatId, usersById))
            }
        }
    }
    return emptyList()
}

internal fun parseRealtimeMessage(payload: JSONObject, usersById: Map<String, UserSummary>): ChatMessage {
    val chatId = payload.optString("chatId")
    return parseChatMessage(payload, chatId, usersById)
}

private fun parseChatMessage(message: JSONObject, chatId: String, usersById: Map<String, UserSummary>): ChatMessage {
    val messageId = message.optString("messageId").ifBlank { message.optString("id") }
    val senderId = message.optString("senderId").ifBlank { message.optString("sender") }
    return ChatMessage(
        id = messageId,
        chatId = message.optString("chatId").ifBlank { chatId },
        senderId = senderId,
        senderName = resolveSenderName(senderId, message, usersById),
        content = parseMessageContent(message.opt("content")),
        createdAt = message.optLong("timestamp", message.optLong("createdAt", 0L))
    )
}

internal fun parseMessageContent(raw: Any?): MessageContent {
    val payload = when (raw) {
        is JSONObject -> raw
        is String -> {
            if (raw.isBlank()) return MessageContent()
            runCatching { JSONObject(raw) }.getOrNull() ?: return MessageContent(text = raw)
        }
        else -> return MessageContent(text = raw?.toString().orEmpty())
    }

    val fileObject = payload.optJSONObject("file")
    val file = fileObject?.let {
        MessageFileAttachment(
            url = it.optString("url"),
            name = it.optString("name"),
            type = it.optString("type"),
            caption = it.optString("caption")
        )
    }
    return MessageContent(
        text = payload.optString("text"),
        file = file,
        pollQuestion = payload.optJSONObject("poll")?.optString("question")?.takeIf { it.isNotBlank() },
        themeName = payload.optJSONObject("theme")?.optString("name")?.takeIf { it.isNotBlank() },
        callLabel = payload.optJSONObject("callLog")?.let {
            if (it.optString("status") == "missed") "Missed call" else "Call"
        },
        forwardedLabel = payload.optJSONObject("forwardedInfo")?.let { "Forwarded message" }
    )
}

private fun resolveChatTitle(chat: JSONObject, usersById: Map<String, UserSummary>, memberIds: List<String>, selfUserId: String): String {
    val explicit = chat.optString("chatName").trim()
    if (explicit.isNotBlank()) return explicit
    if (chat.optString("chatType") == "private") {
        return memberIds.firstOrNull { it != selfUserId }
            ?.let(usersById::get)
            ?.username
            ?: "Direct Message"
    }
    return "Chat"
}

private fun resolveChatAvatar(chat: JSONObject, usersById: Map<String, UserSummary>, memberIds: List<String>, selfUserId: String): String? {
    val explicit = chat.optString("avatarUrl").trim()
    if (explicit.isNotBlank()) return explicit
    if (chat.optString("chatType") == "private") {
        return memberIds.firstOrNull { it != selfUserId }
            ?.let(usersById::get)
            ?.avatarUrl
    }
    return null
}

private fun resolveSenderName(senderId: String, payload: JSONObject, usersById: Map<String, UserSummary>): String {
    if (senderId == "system") return "System"
    if (senderId == "anonymous") return "Anonymous"
    return usersById[senderId]?.username
        ?: payload.optString("senderName").takeIf { it.isNotBlank() }
        ?: payload.optString("sender").takeIf { it.isNotBlank() }
        ?: "Unknown"
}

private fun parseStringList(array: JSONArray?): List<String> {
    if (array == null) return emptyList()
    return buildList {
        for (index in 0 until array.length()) {
            array.optString(index).takeIf { it.isNotBlank() }?.let(::add)
        }
    }
}
