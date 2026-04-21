package ir.hienob.noveo.data

data class Session(
    val userId: String,
    val token: String,
    val sessionId: String = "",
    val expiresAt: Long = 0L
)

data class ChatSummary(
    val id: Long,
    val title: String,
    val lastMessage: String,
    val unreadCount: Int
)

data class ChatMessage(
    val id: Long,
    val chatId: Long,
    val sender: String,
    val text: String,
    val createdAt: String
)
