package ir.hienob.noveo.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun telegramColors() = if (MaterialTheme.colorScheme.primary.luminance() < 0.5f) {
    // Light Theme
    TelegramThemeColors(
        composerBlue = Color(0xFF229AF0),
        composerPanel = Color(0xFFF6F7F8),
        composerField = Color.White,
        composerIcon = Color(0xFF7A8591),
        composerHint = Color(0xFF7A8591),
        composerDivider = Color(0x14000000),
        composerText = Color(0xFF000000),
        composerCursor = Color(0xFF459DE1),
        chatSurface = Color(0xFF91A8C0),
        headerTitle = Color(0xFF333333),
        headerSubtitle = Color(0xFF797979),
        headerIcon = Color(0xFF6B7A8C),
        incomingBubble = Color(0xFFFFFFFF),
        incomingBubbleSelected = Color(0xFFF2F2F2),
        outgoingBubble = Color(0xFFEFFDDE),
        outgoingBubbleSelected = Color(0xFFD9F7C5),
        incomingText = Color(0xFF222222),
        incomingLink = Color(0xFF127ACA),
        incomingTime = Color(0xFF939599),
        outgoingText = Color(0xFF222222),
        outgoingTime = Color(0xFF66A060),
        replyIncoming = Color(0xFFD8E8F7),
        replyOutgoing = Color(0x80FFFFFF)
    )
} else {
    // Dark Theme
    TelegramThemeColors(
        composerBlue = Color(0xFF3F96D8),
        composerPanel = Color(0xFF1C242D),
        composerField = Color(0xFF1C242D),
        composerIcon = Color(0xFFA1AAB3),
        composerHint = Color(0xFFA1AAB3),
        composerDivider = Color(0x28FFFFFF),
        composerText = Color(0xFFFFFFFF),
        composerCursor = Color(0xFF5288C1),
        chatSurface = Color(0xFF0E1621),
        headerTitle = Color(0xFFFFFFFF),
        headerSubtitle = Color(0xFFA1AAB3),
        headerIcon = Color(0xFFA1AAB3),
        incomingBubble = Color(0xFF182533),
        incomingBubbleSelected = Color(0xFF243343),
        outgoingBubble = Color(0xFF2B5278),
        outgoingBubbleSelected = Color(0xFF3E668D),
        incomingText = Color(0xFFFFFFFF),
        incomingLink = Color(0xFF64B5EF),
        incomingTime = Color(0xFFA1AAB3),
        outgoingText = Color(0xFFFFFFFF),
        outgoingTime = Color(0xFFA1AAB3),
        replyIncoming = Color(0xFF243343),
        replyOutgoing = Color(0x28FFFFFF)
    )
}

data class TelegramThemeColors(
    val composerBlue: Color,
    val composerPanel: Color,
    val composerField: Color,
    val composerIcon: Color,
    val composerHint: Color,
    val composerDivider: Color,
    val composerText: Color,
    val composerCursor: Color,
    val chatSurface: Color,
    val headerTitle: Color,
    val headerSubtitle: Color,
    val headerIcon: Color,
    val incomingBubble: Color,
    val incomingBubbleSelected: Color,
    val outgoingBubble: Color,
    val outgoingBubbleSelected: Color,
    val incomingText: Color,
    val incomingLink: Color,
    val incomingTime: Color,
    val outgoingText: Color,
    val outgoingTime: Color,
    val replyIncoming: Color,
    val replyOutgoing: Color
)

// Legacy constants for backward compatibility if needed during refactoring
val TelegramComposerBlue = Color(0xFF229AF0)
val TelegramComposerPanel = Color(0xFFF6F7F8)
val TelegramComposerField = Color.White
val TelegramComposerIcon = Color(0xFF7A8591)
val TelegramComposerHint = Color(0xFF7A8591)
val TelegramComposerDivider = Color(0x14000000)
val TelegramComposerText = Color(0xFF000000)
val TelegramComposerCursor = Color(0xFF459DE1)
val TelegramChatSurface = Color(0xFF91A8C0)
val TelegramHeaderTitle = Color(0xFF333333)
val TelegramHeaderSubtitle = Color(0xFF797979)
val TelegramHeaderIcon = Color(0xFF6B7A8C)
val TelegramIncomingBubble = Color(0xFFFFFFFF)
val TelegramIncomingBubbleSelected = Color(0xFFF2F2F2)
val TelegramOutgoingBubble = Color(0xFFEFFDDE)
val TelegramOutgoingBubbleSelected = Color(0xFFD9F7C5)
val TelegramIncomingText = Color(0xFF222222)
val TelegramIncomingLink = Color(0xFF127ACA)
val TelegramIncomingTime = Color(0xFF939599)
val TelegramOutgoingText = Color(0xFF222222)
val TelegramOutgoingTime = Color(0xFF66A060)
val TelegramReplyIncoming = Color(0xFFD8E8F7)
val TelegramReplyOutgoing = Color(0x80FFFFFF)
