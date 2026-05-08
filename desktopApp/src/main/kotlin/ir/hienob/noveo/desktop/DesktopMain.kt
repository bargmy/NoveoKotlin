package ir.hienob.noveo.desktop

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ir.hienob.noveo.core.ui.NoveoHomeFrame
import ir.hienob.noveo.core.ui.NoveoHomeFrameState
import ir.hienob.noveo.core.ui.NoveoHomeMessage
import ir.hienob.noveo.core.ui.NoveoRootFrame
import ir.hienob.noveo.core.ui.NoveoRootFrameState
import ir.hienob.noveo.core.ui.NoveoStartupSurface
import ir.hienob.noveo.core.ui.NoveoThemePreset
import ir.hienob.noveo.core.ui.coreNoveoStrings
import ir.hienob.noveo.desktop.data.DesktopHomeSnapshot
import ir.hienob.noveo.desktop.data.DesktopNoveoApi
import ir.hienob.noveo.desktop.data.DesktopSession
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.util.Properties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun main() = application {
    val desktopState = DesktopStateHolder()
    Window(
        onCloseRequest = {
            desktopState.close()
            exitApplication()
        },
        title = "Noveo",
        state = rememberWindowState(size = DpSize(1180.dp, 760.dp)),
        resizable = true
    ) {
        val state by desktopState.state.collectAsState()
        val strings = coreNoveoStrings(state.root.languageCode)
        NoveoRootFrame(
            state = state.root,
            theme = NoveoThemePreset.SKY_LIGHT,
            strings = strings,
            onDismissOnboarding = desktopState::dismissOnboarding,
            onAuthMode = desktopState::setAuthMode,
            onStartRegisterCaptcha = { _, _ -> openNoveoWeb() },
            onAuthSubmit = desktopState::authenticate,
            onOpenRegistrationWeb = ::openNoveoWeb,
            homeContent = {
                NoveoHomeFrame(
                    state = state.home,
                    strings = strings,
                    onOpenChat = desktopState::openChat,
                    onBackToChats = desktopState::backToChats,
                    onSend = desktopState::sendMessage,
                    onTyping = desktopState::sendTyping,
                    onRefresh = desktopState::refreshHome,
                    onLogout = desktopState::logout
                )
            }
        )
    }
}

private data class DesktopUiState(
    val root: NoveoRootFrameState = NoveoRootFrameState(
        startupSurface = NoveoStartupSurface.Splash,
        connectionTitle = "Noveo"
    ),
    val home: NoveoHomeFrameState = NoveoHomeFrameState()
)

private class DesktopStateHolder {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val api = DesktopNoveoApi()
    private val sessionStore = DesktopSessionStore()
    private var session: DesktopSession? = null
    private var messagesByChat: Map<String, List<NoveoHomeMessage>> = emptyMap()

    private val _state = MutableStateFlow(DesktopUiState())
    val state = _state.asStateFlow()

    init {
        restoreSession()
    }

    fun close() {
        // Reserved for future websocket lifecycle cleanup.
    }

    fun dismissOnboarding() {
        _state.value = _state.value.copy(
            root = _state.value.root.copy(startupSurface = NoveoStartupSurface.Auth, error = null)
        )
    }

    fun setAuthMode(signup: Boolean) {
        _state.value = _state.value.copy(root = _state.value.root.copy(authModeSignup = signup, error = null))
    }

    fun authenticate(handle: String, password: String) {
        if (handle.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(root = _state.value.root.copy(error = "Enter your username and password."))
            return
        }
        _state.value = _state.value.copy(root = _state.value.root.copy(loading = true, error = null))
        scope.launch {
            runCatching {
                val signedIn = withContext(Dispatchers.IO) { api.login(handle.trim(), password) }
                session = signedIn
                sessionStore.write(signedIn)
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(signedIn) }
                applyHomeSnapshot(snapshot, selectedChatId = null)
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    root = _state.value.root.copy(
                        startupSurface = NoveoStartupSurface.Auth,
                        loading = false,
                        error = error.message ?: "Authentication failed"
                    )
                )
            }
        }
    }

    fun openChat(chatId: String) {
        _state.value = _state.value.copy(
            home = _state.value.home.copy(
                selectedChatId = chatId,
                messages = messagesByChat[chatId].orEmpty(),
                error = null
            )
        )
    }

    fun backToChats() {
        _state.value = _state.value.copy(home = _state.value.home.copy(selectedChatId = null, messages = emptyList()))
    }

    fun refreshHome() {
        val currentSession = session ?: return
        val selectedChatId = _state.value.home.selectedChatId
        _state.value = _state.value.copy(home = _state.value.home.copy(loading = true, error = null))
        scope.launch {
            runCatching {
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = selectedChatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(home = _state.value.home.copy(loading = false, error = error.message ?: "Refresh failed"))
            }
        }
    }

    fun sendTyping() {
        // Desktop typing events can be wired once the long-lived websocket bridge is extracted.
    }

    fun sendMessage(text: String) {
        val currentSession = session ?: return
        val chatId = _state.value.home.selectedChatId ?: return
        val pendingMessage = NoveoHomeMessage(
            id = "pending-${System.currentTimeMillis()}",
            senderId = currentSession.userId,
            senderName = "You",
            text = text,
            isOutgoing = true,
            pending = true
        )
        _state.value = _state.value.copy(
            home = _state.value.home.copy(
                messages = _state.value.home.messages + pendingMessage,
                isSendingMessage = true,
                error = null
            )
        )
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.sendMessage(currentSession, chatId, text) }
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(currentSession) }
                applyHomeSnapshot(snapshot, selectedChatId = chatId)
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    home = _state.value.home.copy(
                        isSendingMessage = false,
                        error = error.message ?: "Send failed"
                    )
                )
            }
        }
    }

    fun logout() {
        session = null
        messagesByChat = emptyMap()
        sessionStore.clear()
        _state.value = DesktopUiState(
            root = NoveoRootFrameState(startupSurface = NoveoStartupSurface.Auth, connectionTitle = "Noveo")
        )
    }

    private fun restoreSession() {
        scope.launch {
            val stored = sessionStore.read()
            if (stored == null) {
                _state.value = _state.value.copy(
                    root = _state.value.root.copy(startupSurface = NoveoStartupSurface.Onboarding, loading = false)
                )
                return@launch
            }
            session = stored
            _state.value = _state.value.copy(root = _state.value.root.copy(startupSurface = NoveoStartupSurface.Splash, loading = true))
            runCatching {
                val snapshot = withContext(Dispatchers.IO) { api.loadHome(stored) }
                applyHomeSnapshot(snapshot, selectedChatId = null)
            }.onFailure {
                sessionStore.clear()
                session = null
                _state.value = _state.value.copy(
                    root = _state.value.root.copy(
                        startupSurface = NoveoStartupSurface.Auth,
                        loading = false,
                        error = it.message ?: "Session restore failed"
                    )
                )
            }
        }
    }

    private fun applyHomeSnapshot(snapshot: DesktopHomeSnapshot, selectedChatId: String?) {
        session = snapshot.session
        messagesByChat = snapshot.messagesByChat
        val actualSelectedId = selectedChatId?.takeIf { id -> snapshot.chats.any { it.id == id } }
        _state.value = _state.value.copy(
            root = _state.value.root.copy(
                startupSurface = NoveoStartupSurface.Home,
                loading = false,
                error = null
            ),
            home = NoveoHomeFrameState(
                currentUserId = snapshot.session.userId,
                chats = snapshot.chats,
                selectedChatId = actualSelectedId,
                messages = actualSelectedId?.let { messagesByChat[it] }.orEmpty(),
                totalUnreadCount = snapshot.totalUnreadCount,
                loading = false,
                isSendingMessage = false
            )
        )
    }
}

private class DesktopSessionStore {
    private val file: File = File(System.getProperty("user.home"), ".noveo/session.properties")

    fun read(): DesktopSession? = runCatching {
        if (!file.exists()) return null
        val properties = Properties()
        file.inputStream().use(properties::load)
        val userId = properties.getProperty("userId").orEmpty()
        val token = properties.getProperty("token").orEmpty()
        if (userId.isBlank() || token.isBlank()) return null
        DesktopSession(
            userId = userId,
            token = token,
            sessionId = properties.getProperty("sessionId").orEmpty(),
            expiresAt = properties.getProperty("expiresAt")?.toLongOrNull() ?: 0L
        )
    }.getOrNull()

    fun write(session: DesktopSession) {
        file.parentFile?.mkdirs()
        val properties = Properties().apply {
            setProperty("userId", session.userId)
            setProperty("token", session.token)
            setProperty("sessionId", session.sessionId)
            setProperty("expiresAt", session.expiresAt.toString())
        }
        file.outputStream().use { properties.store(it, "Noveo desktop session") }
    }

    fun clear() {
        runCatching { file.delete() }
    }
}

private fun openNoveoWeb() {
    runCatching {
        if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(URI("https://web.noveo.ir"))
    }
}
