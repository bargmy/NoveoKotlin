package ir.hienob.noveo.desktop

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ir.hienob.noveo.core.ui.NoveoSharedChatShell
import ir.hienob.noveo.core.ui.NoveoShellChat
import ir.hienob.noveo.core.ui.NoveoTheme
import ir.hienob.noveo.core.ui.NoveoThemePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

fun main() = application {
    val desktopState = DesktopStateHolder()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Noveo",
        state = rememberWindowState(size = DpSize(1200.dp, 800.dp)),
        resizable = true
    ) {
        val state by desktopState.state.collectAsState()
        NoveoTheme(theme = NoveoThemePreset.SKY_LIGHT) {
            NoveoSharedChatShell(
                chats = state.chats,
                selectedChatTitle = state.selectedChatTitle,
                messages = state.messages
            )
        }
    }
}

private data class DesktopUiState(
    val chats: List<NoveoShellChat> = emptyList(),
    val selectedChatTitle: String = "",
    val messages: List<ir.hienob.noveo.core.ui.NoveoShellMessage> = emptyList()
)

private class DesktopStateHolder {
    private val _state = MutableStateFlow(DesktopUiState())
    val state = _state.asStateFlow()
}
