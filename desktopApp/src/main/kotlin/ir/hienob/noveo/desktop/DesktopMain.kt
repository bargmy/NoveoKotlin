package ir.hienob.noveo.desktop

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ir.hienob.noveo.core.ui.NoveoSharedChatShell
import ir.hienob.noveo.core.ui.NoveoShellChat
import ir.hienob.noveo.core.ui.NoveoShellMessage
import ir.hienob.noveo.core.ui.NoveoTheme
import ir.hienob.noveo.core.ui.NoveoThemePreset

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Noveo",
        state = rememberWindowState(size = DpSize(1200.dp, 800.dp)),
        resizable = true
    ) {
        NoveoTheme(theme = NoveoThemePreset.SKY_LIGHT) {
            NoveoSharedChatShell(
                chats = desktopShellChats,
                selectedChatTitle = "Noveo Android Shell",
                messages = desktopShellMessages,
                modifier = Modifier
            )
        }
    }
}

private val desktopShellChats = listOf(
    NoveoShellChat(
        id = "android-shell",
        title = "Noveo Android Shell",
        subtitle = "Shared chat shell is now used by desktop",
        time = "now",
        unreadCount = 2,
        isSelected = true
    ),
    NoveoShellChat(
        id = "saved",
        title = "Saved Messages",
        subtitle = "Your private notes and files",
        time = "12:42"
    ),
    NoveoShellChat(
        id = "updates",
        title = "Noveo Updates",
        subtitle = "Release notes and build status",
        time = "Mon"
    )
)

private val desktopShellMessages = listOf(
    NoveoShellMessage(
        id = "1",
        author = "Noveo",
        body = "Desktop no longer uses the placeholder screen.",
        time = "12:40"
    ),
    NoveoShellMessage(
        id = "2",
        author = "You",
        body = "Make it use the actual Noveo shell and keep the build stable.",
        time = "12:41",
        isOutgoing = true
    ),
    NoveoShellMessage(
        id = "3",
        author = "Noveo",
        body = "The chat rail, header, message bubbles, and composer now come from the shared Compose module used by Android and desktop.",
        time = "12:42"
    )
)
