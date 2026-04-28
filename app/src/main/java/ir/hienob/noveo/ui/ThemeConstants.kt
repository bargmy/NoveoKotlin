package ir.hienob.noveo.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun telegramColors(): TelegramThemeColors {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val colorScheme = MaterialTheme.colorScheme
    
    return if (!isDark) {
        // Light Theme - Standard Telegram Colors
        TelegramThemeColors(
            isDark = false,
            composerBlue = Color(0xFF229AF0),
            composerPanel = Color(0xFFF6F7F8),
            composerField = Color.White,
            composerIcon = Color(0xFF7A8591),
            composerHint = Color(0xFF7A8591).copy(alpha = 0.6f),
            composerDivider = Color(0x14000000),
            composerText = Color.Black,
            composerCursor = Color(0xFF459DE1),
            chatSurface = Color(0xFFEBEDF0),
            headerTitle = Color(0xFF333333),
            headerSubtitle = Color(0xFF797979),
            headerIcon = Color(0xFF6B7A8C),
            incomingBubble = Color.White,
            incomingBubbleSelected = Color(0xFFF2F2F2),
            outgoingBubble = Color(0xFFE2F7B7), 
            outgoingBubbleSelected = Color(0xFFD3ECA3),
            incomingText = Color(0xFF222222),
            incomingLink = Color(0xFF127ACA),
            incomingTime = Color(0xFF939599),
            outgoingText = Color(0xFF222222),
            outgoingTime = Color(0xFF66A060),
            replyIncoming = Color(0xFFD8E8F7),
            replyOutgoing = Color(0x80FFFFFF)
        )
    } else {
        // Dark Theme - Standard Telegram Colors
        TelegramThemeColors(
            isDark = true,
            composerBlue = Color(0xFF40A7E3),
            composerPanel = Color(0xFF1C242F),
            composerField = Color(0xFF1C242F),
            composerIcon = Color(0xFF7A8591),
            composerHint = Color(0xFF7A8591).copy(alpha = 0.6f),
            composerDivider = Color(0x20000000),
            composerText = Color.White,
            composerCursor = Color(0xFF40A7E3),
            chatSurface = Color(0xFF0E1621),
            headerTitle = Color.White,
            headerSubtitle = Color(0xFF8B959E),
            headerIcon = Color(0xFF8B959E),
            incomingBubble = Color(0xFF182533),
            incomingBubbleSelected = Color(0xFF2B3948),
            outgoingBubble = Color(0xFF2B5278),
            outgoingBubbleSelected = Color(0xFF3E618A),
            incomingText = Color.White,
            incomingLink = Color(0xFF64B5EF),
            incomingTime = Color(0xFF8B959E),
            outgoingText = Color.White,
            outgoingTime = Color(0xFFB1C3D5),
            replyIncoming = Color(0xFF1F2D3D),
            replyOutgoing = Color(0x1AFFFFFF)
        )
    }
}

data class TelegramThemeColors(
    val isDark: Boolean,
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
