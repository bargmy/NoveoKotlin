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
        onlineArray.optString(index).sanitizeServerString().takeIf { it.isNotBlank() }?.let(onlineIds::add)
    }

    val users = mutableMapOf<String, UserSummary>()
    val usersArray = payload.optJSONArray("users") ?: JSONArray()
    for (index in 0 until usersArray.length()) {
        val item = usersArray.optJSONObject(index) ?: continue
        val userId = item.optString("userId").sanitizeServerString()
        if (userId.isBlank()) continue
        users[userId] = UserSummary(
            id = userId,
            username = item.optString("username").sanitizeServerString().ifBlank { "Unknown" },
            avatarUrl = resolveAssetUrl(item, "avatarUrl", "avatar", "photo", "image")?.takeIf { it.isNotBlank() },
            handle = item.optString("handle").sanitizeServerString().takeIf { it.isNotBlank() },
            bio = item.optString("bio").sanitizeServerString(),
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
            val chatId = item.optString("chatId").sanitizeServerString()
                .ifBlank { item.optString("chat_id").sanitizeServerString() }
                .ifBlank { item.optString("id").sanitizeServerString() }
            if (chatId.isBlank()) continue
            val memberIds = parseStringList(item.optJSONArray("members"))
            val messages = item.optJSONArray("messages") ?: JSONArray()
            val preview = if (messages.length() > 0) {
                parseMessageContent(messages.optJSONObject(messages.length() - 1)?.opt("content")).previewText()
            } else {
                item.optString("lastMessagePreview").sanitizeServerString()
            }
            add(
                ChatSummary(
                    id = chatId,
                    chatType = item.optString("chatType").sanitizeServerString().ifBlank { item.optString("chat_type").sanitizeServerString() }.ifBlank { "private" },
                    title = resolveChatTitle(item, usersById, memberIds, selfUserId),
                    avatarUrl = resolveChatAvatar(item, usersById, memberIds, selfUserId),
                    lastMessagePreview = preview,
                    unreadCount = item.optInt("unreadCount", item.optInt("unread", 0)),
                    memberIds = memberIds,
                    handle = item.optString("handle").sanitizeServerString().takeIf { it.isNotBlank() },
                    isVerified = item.optBoolean("isVerified", item.optBoolean("is_verified", false))
                )
            )
        }
    }
}

internal fun parseMessagesForChat(payload: JSONObject, usersById: Map<String, UserSummary>, chatId: String): List<ChatMessage> {
    val chatsArray = payload.optJSONArray("chats") ?: JSONArray()
    for (index in 0 until chatsArray.length()) {
        val item = chatsArray.optJSONObject(index) ?: continue
        val itemChatId = item.optString("chatId").sanitizeServerString().ifBlank { item.optString("id").sanitizeServerString() }
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
    val chatId = payload.optString("chatId").sanitizeServerString()
    return parseChatMessage(payload, chatId, usersById)
}

private fun parseChatMessage(message: JSONObject, chatId: String, usersById: Map<String, UserSummary>): ChatMessage {
    val messageId = message.optString("messageId").sanitizeServerString()
        .ifBlank { message.optString("message_id").sanitizeServerString() }
        .ifBlank { message.optString("id").sanitizeServerString() }
    val senderId = message.optString("senderId").sanitizeServerString()
        .ifBlank { message.optString("sender_id").sanitizeServerString() }
        .ifBlank { message.optString("sender").sanitizeServerString() }
    return ChatMessage(
        id = messageId,
        chatId = message.optString("chatId").sanitizeServerString().ifBlank { chatId },
        senderId = senderId,
        senderName = resolveSenderName(senderId, message, usersById),
        content = parseMessageContent(message.opt("content")),
        createdAt = message.optLong("timestamp", message.optLong("createdAt", message.optLong("created_at", 0L)))
    )
}

internal fun parseMessageContent(raw: Any?): MessageContent {
    val payload = when (raw) {
        is JSONObject -> raw
        is String -> {
            val text = raw.sanitizeServerString()
            if (text.isBlank()) return MessageContent()
            runCatching { JSONObject(text) }.getOrNull() ?: return MessageContent(text = text)
        }
        else -> return MessageContent(text = raw?.toString().sanitizeServerString())
    }

    val fileObject = payload.optJSONObject("file")
        ?: payload.optJSONObject("attachment")
        ?: payload.optJSONObject("document")
        ?: payload.optJSONObject("media")
    val file = fileObject?.let {
        MessageFileAttachment(
            url = resolveAssetUrl(it, "url", "src", "path", "downloadUrl", "fileUrl").orEmpty(),
            name = it.optString("name").sanitizeServerString().ifBlank {
                it.optString("filename").sanitizeServerString().ifBlank { it.optString("title").sanitizeServerString() }
            },
            type = it.optString("type").sanitizeServerString().ifBlank {
                it.optString("mimeType").sanitizeServerString().ifBlank { it.optString("contentType").sanitizeServerString() }
            },
            caption = it.optString("caption").sanitizeServerString().ifBlank { payload.optString("caption").sanitizeServerString() }
        )
    }
    return MessageContent(
        text = payload.optString("text").sanitizeServerString(),
        file = file,
        pollQuestion = payload.optJSONObject("poll")?.optString("question")?.sanitizeServerString()?.takeIf { it.isNotBlank() },
        themeName = payload.optJSONObject("theme")?.optString("name")?.sanitizeServerString()?.takeIf { it.isNotBlank() },
        callLabel = payload.optJSONObject("callLog")?.let {
            if (it.optString("status").sanitizeServerString() == "missed") "Missed call" else "Call"
        },
        forwardedLabel = payload.optJSONObject("forwardedInfo")?.let { "Forwarded message" }
    )
}

private fun resolveChatTitle(chat: JSONObject, usersById: Map<String, UserSummary>, memberIds: List<String>, selfUserId: String): String {
    val explicit = chat.optString("chatName").sanitizeServerString()
    if (explicit.isNotBlank()) return explicit
    if (chat.optString("chatType").sanitizeServerString() == "private") {
        return memberIds.firstOrNull { it != selfUserId }
            ?.let(usersById::get)
            ?.username
            ?.sanitizeServerString()
            ?.ifBlank { "Direct Message" }
            ?: "Direct Message"
    }
    return "Chat"
}

private fun resolveChatAvatar(chat: JSONObject, usersById: Map<String, UserSummary>, memberIds: List<String>, selfUserId: String): String? {
    val explicit = resolveAssetUrl(chat, "avatarUrl", "avatar", "photo", "image")
    if (!explicit.isNullOrBlank()) return explicit
    if (chat.optString("chatType").sanitizeServerString() == "private") {
        return memberIds.firstOrNull { it != selfUserId }
            ?.let(usersById::get)
            ?.avatarUrl
            ?.sanitizeServerString()
            ?.takeIf { it.isNotBlank() }
    }
    return null
}

private fun resolveSenderName(senderId: String, payload: JSONObject, usersById: Map<String, UserSummary>): String {
    if (senderId == "system") return "System"
    if (senderId == "anonymous") return "Anonymous"
    return usersById[senderId]?.username?.sanitizeServerString()?.takeIf { it.isNotBlank() }
        ?: payload.optString("senderName").sanitizeServerString().takeIf { it.isNotBlank() }
        ?: payload.optString("sender").sanitizeServerString().takeIf { it.isNotBlank() }
        ?: "Unknown"
}

private fun resolveAssetUrl(source: JSONObject, vararg keys: String): String? {
    for (key in keys) {
        val direct = source.optString(key).sanitizeServerString()
        if (direct.isNotBlank()) return direct
        val nested = source.optJSONObject(key) ?: continue
        val candidates = listOf(
            nested.optString("url").sanitizeServerString(),
            nested.optString("src").sanitizeServerString(),
            nested.optString("path").sanitizeServerString(),
            nested.optString("downloadUrl").sanitizeServerString(),
            nested.optString("fileUrl").sanitizeServerString(),
            nested.optString("thumbUrl").sanitizeServerString(),
            nested.optString("thumbnailUrl").sanitizeServerString()
        )
        val resolved = candidates.firstOrNull { it.isNotBlank() }
        if (!resolved.isNullOrBlank()) return resolved
    }
    return null
}

private fun parseStringList(array: JSONArray?): List<String> {
    if (array == null) return emptyList()
    return buildList {
        for (index in 0 until array.length()) {
            array.optString(index).sanitizeServerString().takeIf { it.isNotBlank() }?.let(::add)
        }
    }
}

private fun String?.sanitizeServerString(): String {
    val value = this?.trim().orEmpty()
    return if (value.equals("null", ignoreCase = true) || value.equals("undefined", ignoreCase = true)) "" else value
}
