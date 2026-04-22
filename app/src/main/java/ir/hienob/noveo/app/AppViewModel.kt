package ir.hienob.noveo.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSocket
import ir.hienob.noveo.data.ChatSummary
import ir.hienob.noveo.data.NoveoApi
import ir.hienob.noveo.data.Session
import ir.hienob.noveo.data.SessionStore
import ir.hienob.noveo.data.UserSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface StartupState {
    data object Splash : StartupState
    data object Onboarding : StartupState
    data object Auth : StartupState
    data object Home : StartupState
}

data class AppUiState(
    val startupState: StartupState = StartupState.Splash,
    val loading: Boolean = false,
    val error: String? = null,
    val session: Session? = null,
    val usersById: Map<String, UserSummary> = emptyMap(),
    val chats: List<ChatSummary> = emptyList(),
    val selectedChatId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val authModeSignup: Boolean = false,
    val connectionTitle: String = "Noveo",
    val connectionDetail: String? = null,
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionStore = SessionStore(application)
    private val api = NoveoApi()
    private val socket = ChatSocket()

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var socketJob: Job? = null

    init {
        restoreSession()
    }

    fun restoreSession() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Splash,
                loading = true,
                error = null,
                connectionTitle = "Connecting…",
                connectionDetail = "Checking saved session"
            )
            val session = sessionStore.read()
            if (session == null) {
                _uiState.value = _uiState.value.copy(
                    startupState = StartupState.Onboarding,
                    loading = false,
                    selectedChatId = null,
                    messages = emptyList(),
                    connectionTitle = "Welcome to Noveo",
                    connectionDetail = "Let’s get you set up"
                )
                return@launch
            }
            loadHome(session)
        }
    }

    fun dismissOnboarding() {
        _uiState.value = _uiState.value.copy(
            startupState = StartupState.Auth,
            loading = false,
            error = null,
            connectionTitle = "Sign in",
            connectionDetail = "Enter your account details"
        )
    }

    fun setAuthMode(signup: Boolean) {
        _uiState.value = _uiState.value.copy(
            authModeSignup = signup,
            error = null,
            connectionTitle = if (signup) "Create account" else "Welcome back",
            connectionDetail = if (signup) "Join Noveo to start chatting" else "Sign in to continue"
        )
    }

    fun authenticate(handle: String, password: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = _uiState.value.copy(
                    loading = true,
                    error = null,
                    connectionTitle = if (_uiState.value.authModeSignup) "Creating account…" else "Signing in…",
                    connectionDetail = handle.ifBlank { null }
                )
                val session = withContext(Dispatchers.IO) {
                    if (_uiState.value.authModeSignup) api.signup(handle, password) else api.login(handle, password)
                }
                sessionStore.write(session)
                loadHome(session)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = it.message ?: "Authentication failed",
                    connectionTitle = "Connection failed",
                    connectionDetail = "Check your credentials and try again"
                )
            }
        }
    }

    fun logout() {
        socketJob?.cancel()
        sessionStore.clear()
        _uiState.value = AppUiState(
            startupState = StartupState.Auth,
            connectionTitle = "Signed out",
            connectionDetail = "Log in again to continue"
        )
    }

    fun backToChatList() {
        _uiState.value = _uiState.value.copy(
            selectedChatId = null,
            messages = emptyList(),
            error = null,
            loading = false,
            connectionTitle = "Noveo",
            connectionDetail = "Choose a chat"
        )
    }

    fun openDirectChat(userId: String) {
        val state = _uiState.value
        val existingChat = state.chats.firstOrNull { chat ->
            chat.chatType == "private" &&
                chat.memberIds.contains(userId) &&
                state.session?.userId?.let(chat.memberIds::contains) == true
        }
        if (existingChat != null) {
            openChat(existingChat.id)
            return
        }
        val name = state.usersById[userId]?.username?.ifBlank { "that contact" } ?: "that contact"
        _uiState.value = state.copy(
            error = "No existing chat with $name yet.",
            connectionTitle = "Chat unavailable",
            connectionDetail = "Start support for creating new direct chats next"
        )
    }

    fun openChat(chatId: String) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            runCatching {
                val title = _uiState.value.chats.firstOrNull { it.id == chatId }?.title ?: "Chat"
                _uiState.value = _uiState.value.copy(
                    loading = true,
                    selectedChatId = chatId,
                    error = null,
                    connectionTitle = title,
                    connectionDetail = "Loading conversation"
                )
                val result = withContext(Dispatchers.IO) { api.getMessages(session, chatId) }
                _uiState.value = _uiState.value.copy(
                    usersById = _uiState.value.usersById + result.usersById,
                    messages = result.messages.sortedBy { it.createdAt },
                    loading = false,
                    connectionTitle = title,
                    connectionDetail = "${result.messages.size} messages"
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = it.message ?: "Unable to load chat",
                    connectionTitle = "Failed to open chat",
                    connectionDetail = null
                )
            }
        }
    }

    fun sendMessage(text: String) {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            runCatching {
                api.sendMessage(session, chatId, text)
                refreshHomeAndSelectedChat(session, chatId)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    error = it.message ?: "Unable to send",
                    connectionTitle = "Send failed",
                    connectionDetail = "Message was not delivered"
                )
            }
        }
    }

    private suspend fun loadHome(session: Session) {
        runCatching {
            val home = withContext(Dispatchers.IO) { api.getHomeData(session) }
            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                loading = false,
                session = session,
                usersById = home.usersById,
                chats = home.chats,
                selectedChatId = null,
                messages = emptyList(),
                error = null,
                connectionTitle = home.currentUser.username.ifBlank { "Noveo" },
                connectionDetail = "${home.chats.size} chats • ${home.usersById.size} contacts"
            )
            observeSocket(session)
        }.onFailure {
            sessionStore.clear()
            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Auth,
                loading = false,
                error = it.message,
                connectionTitle = "Session expired",
                connectionDetail = "Please sign in again"
            )
        }
    }

    private suspend fun refreshHomeAndSelectedChat(
        session: Session,
        chatId: String? = _uiState.value.selectedChatId
    ) {
        val home = withContext(Dispatchers.IO) { api.getHomeData(session) }
        val refreshedMessages = if (chatId.isNullOrBlank()) {
            _uiState.value.messages
        } else {
            withContext(Dispatchers.IO) { api.getMessages(session, chatId) }.messages.sortedBy { it.createdAt }
        }
        val selectedTitle = home.chats.firstOrNull { it.id == chatId }?.title
        _uiState.value = _uiState.value.copy(
            usersById = home.usersById,
            chats = home.chats,
            messages = refreshedMessages,
            loading = false,
            error = null,
            connectionTitle = selectedTitle ?: home.currentUser.username.ifBlank { "Noveo" },
            connectionDetail = if (chatId == null) {
                "${home.chats.size} chats • ${home.usersById.size} contacts"
            } else {
                "${refreshedMessages.size} messages"
            }
        )
    }

    private fun observeSocket(session: Session) {
        socketJob?.cancel()
        socketJob = viewModelScope.launch {
            socket.connect(session, _uiState.value.usersById).collect { incoming ->
                val state = _uiState.value
                val updatedMessages = if (incoming.chatId == state.selectedChatId) {
                    (state.messages + incoming)
                        .distinctBy { message ->
                            message.id.ifBlank { "${message.chatId}-${message.createdAt}-${message.senderId}" }
                        }
                        .sortedBy { it.createdAt }
                } else {
                    state.messages
                }
                val updatedChats = state.chats.map { chat ->
                    if (chat.id != incoming.chatId) {
                        chat
                    } else {
                        chat.copy(
                            lastMessagePreview = incoming.content.previewText().ifBlank { chat.lastMessagePreview },
                            unreadCount = if (incoming.chatId == state.selectedChatId) 0 else chat.unreadCount + 1
                        )
                    }
                }
                val prioritizedChats = updatedChats.sortedByDescending { chat ->
                    if (chat.id == incoming.chatId) incoming.createdAt else 0L
                }
                _uiState.value = state.copy(
                    chats = prioritizedChats,
                    messages = updatedMessages,
                    error = null,
                    connectionTitle = prioritizedChats.firstOrNull { it.id == state.selectedChatId }?.title
                        ?: state.connectionTitle,
                    connectionDetail = if (state.selectedChatId == null) {
                        "${prioritizedChats.size} chats • ${state.usersById.size} contacts"
                    } else {
                        "${updatedMessages.size} messages"
                    }
                )
                refreshHomeAndSelectedChat(session, state.selectedChatId)
            }
        }
    }
}
