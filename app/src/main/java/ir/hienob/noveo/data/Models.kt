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
    val type: String = "",
    val caption: String = ""
)

data class MessageContent(
    val text: String = "",
    val file: MessageFileAttachment? = null,
    val pollQuestion: String? = null,
    val themeName: String? = null,
    val callLabel: String? = null,
    val forwardedLabel: String? = null
) {
    fun previewText(): String {
        return when {
            text.isNotBlank() -> text
            file != null -> file.caption.ifBlank { "[File] ${file.name.ifBlank { "Attachment" }}" }
            !pollQuestion.isNullOrBlank() -> "[Poll] $pollQuestion"
            !themeName.isNullOrBlank() -> "[Theme] $themeName"
            !callLabel.isNullOrBlank() -> callLabel
            !forwardedLabel.isNullOrBlank() -> forwardedLabel
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
    val isVerified: Boolean = false
)

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: MessageContent,
    val createdAt: Long = 0L
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
