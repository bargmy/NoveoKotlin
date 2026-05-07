package ir.hienob.noveo.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ir.hienob.noveo.core.ui.NoveoRootFrame
import ir.hienob.noveo.core.ui.NoveoRootFrameState
import ir.hienob.noveo.core.ui.NoveoStartupSurface
import ir.hienob.noveo.core.ui.NoveoThemePreset
import ir.hienob.noveo.core.ui.coreNoveoStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.awt.Desktop
import java.net.URI

fun main() = application {
    val desktopState = DesktopStateHolder()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Noveo",
        state = rememberWindowState(size = DpSize(1200.dp, 800.dp)),
        resizable = true
    ) {
        val state by desktopState.state.collectAsState()
        NoveoRootFrame(
            state = state,
            theme = NoveoThemePreset.SKY_LIGHT,
            strings = coreNoveoStrings(state.languageCode),
            onDismissOnboarding = desktopState::dismissOnboarding,
            onAuthMode = desktopState::setAuthMode,
            onStartRegisterCaptcha = { _, _ -> openNoveoWeb() },
            onAuthSubmit = desktopState::authenticate,
            onOpenRegistrationWeb = ::openNoveoWeb,
            homeContent = { DesktopHomeBridgeNotice() }
        )
    }
}

private class DesktopStateHolder {
    private val _state = MutableStateFlow(
        NoveoRootFrameState(
            startupSurface = NoveoStartupSurface.Onboarding,
            connectionTitle = "Noveo"
        )
    )
    val state = _state.asStateFlow()

    fun dismissOnboarding() {
        _state.value = _state.value.copy(startupSurface = NoveoStartupSurface.Auth, error = null)
    }

    fun setAuthMode(signup: Boolean) {
        _state.value = _state.value.copy(authModeSignup = signup, error = null)
    }

    fun authenticate(handle: String, password: String) {
        _state.value = _state.value.copy(
            loading = false,
            error = if (handle.isBlank() || password.isBlank()) {
                "Enter your username and password."
            } else {
                "Desktop data/session wiring is not complete yet. The previous separate desktop chat shell has been removed while desktop state is connected to the shared app UI."
            }
        )
    }
}

private fun openNoveoWeb() {
    runCatching {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI("https://web.noveo.ir"))
        }
    }
}

@androidx.compose.runtime.Composable
private fun DesktopHomeBridgeNotice() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Desktop is using the shared Noveo root. Chat/home is routed through the shared app frame while the remaining desktop data bridge is connected.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
