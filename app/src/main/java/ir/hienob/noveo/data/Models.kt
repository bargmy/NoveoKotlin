package ir.hienob.noveo.data

data class Session(
    val userId: String,
    val token: String,
    val sessionId: String = "",
    val expiresAt: Long = 0L
)

data class ProfileSkin(
    val mode: String = "",
    val primaryColor: String = "",
    val secondaryColor: String = "",
    val tertiaryColor: String = "",
    val gradientStops: Int = 2
)

data class Transaction(
    val id: String,
    val amountTenths: Int,
    val balanceAfterTenths: Int,
    val type: String,
    val description: String,
    val createdAt: Long,
    val relatedUserId: String? = null
)

data class Wallet(
    val balanceTenths: Int,
    val balanceLabel: String,
    val transactions: List<Transaction> = emptyList()
)

data class UserSummary(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val handle: String? = null,
    val bio: String = "",
    val isOnline: Boolean = false,
    val isVerified: Boolean = false,
    val profileSkin: ProfileSkin? = null,
    val starsBalance: Double = 0.0
)

data class MessageFileAttachment(
    val url: String = "",
    val name: String = "",
    val type: String = ""
) {
    fun isImage(): Boolean = type.startsWith("image/") || name.endsWith(".jpg", true) || name.endsWith(".png", true) || name.endsWith(".gif", true) || name.endsWith(".webp", true)
    fun isVideo(): Boolean = type.startsWith("video/") || name.endsWith(".mp4", true) || name.endsWith(".webm", true)
}

data class MessageContent(
    val text: String? = null,
    val file: MessageFileAttachment? = null,
    val poll: String? = null, // Simplified for now
    val theme: String? = null,
    val callLog: String? = null,
    val forwardedInfo: Boolean = false,
    val replyToId: String? = null
) {
    fun previewText(): String {
        return when {
            !text.isNullOrBlank() -> text
            file != null -> if (file.isImage()) "Photo" else if (file.isVideo()) "Video" else "File"
            !poll.isNullOrBlank() -> "Poll"
            !theme.isNullOrBlank() -> "Theme"
            !callLog.isNullOrBlank() -> "Call"
            forwardedInfo -> "Forwarded message"
            else -> ""
        }
    }
}

data class ChatSummary(
    val id: String,
    val chatType: String,
    val title: String,
    val avatarUrl: String? = null,
    val lastMessagePreview: String = "",
    val unreadCount: Int = 0,
    val memberIds: List<String> = emptyList(),
    val handle: String? = null,
    val isVerified: Boolean = false,
    val ownerId: String? = null,
    val canChat: Boolean = true,
    val hasMoreHistory: Boolean = false
)

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String = "User",
    val content: MessageContent,
    val timestamp: Long = 0L,
    val seenBy: List<String> = emptyList(),
    val pending: Boolean = false,
    val clientTempId: String? = null,
    val replyToId: String? = null,
    val editedAt: Long? = null
)

data class HomeData(
    val usersById: Map<String, UserSummary>,
    val onlineUserIds: Set<String>,
    val chats: List<ChatSummary>
)

data class MessageLoadResult(
    val usersById: Map<String, UserSummary>,
    val messages: List<ChatMessage>
)
