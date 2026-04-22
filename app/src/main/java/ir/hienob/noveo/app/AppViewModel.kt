package ir.hienob.noveo.app


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSocket
import ir.hienob.noveo.data.ChatSummary
import ir.hienob.noveo.data.MessageContent
import ir.hienob.noveo.data.NoveoApi
import ir.hienob.noveo.data.Session
import ir.hienob.noveo.data.SessionStore
import ir.hienob.noveo.data.UserSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val onlineUserIds: Set<String> = emptySet(),
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
            val session = sessionStore.read()
            if (session == null) {
                _uiState.value = _uiState.value.copy(
                    startupState = StartupState.Onboarding,
                    loading = false,
                    selectedChatId = null,
                    messages = emptyList(),
                    connectionTitle = "Noveo",
                    connectionDetail = null,
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                session = session,
                loading = true,
                error = null,
                connectionTitle = "Connecting...",
                connectionDetail = null,
            )
            loadHome(session, preserveShell = true)
        }
    }

    fun dismissOnboarding() {
        _uiState.value = _uiState.value.copy(
            startupState = StartupState.Auth,
            loading = false,
            error = null,
            connectionTitle = "Noveo",
            connectionDetail = null,
        )
    }

    fun setAuthMode(signup: Boolean) {
        _uiState.value = _uiState.value.copy(authModeSignup = signup, error = null)
    }

    fun authenticate(handle: String, password: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = _uiState.value.copy(
                    startupState = StartupState.Home,
                    loading = true,
                    error = null,
                    connectionTitle = "Connecting...",
                    connectionDetail = null,
                )
                val session = withContext(Dispatchers.IO) {
                    if (_uiState.value.authModeSignup) api.signup(handle, password) else api.login(handle, password)
                }
                sessionStore.write(session)
                loadHome(session, preserveShell = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    startupState = StartupState.Auth,
                    loading = false,
                    error = it.message ?: "Authentication failed",
                    connectionTitle = "Noveo",
                    connectionDetail = null,
                )
            }
        }
    }

    fun logout() {
        socketJob?.cancel()
        sessionStore.clear()
        _uiState.value = AppUiState(startupState = StartupState.Auth)
    }

    fun backToChatList() {
        _uiState.value = _uiState.value.copy(
            selectedChatId = null,
            messages = emptyList(),
            error = null,
            loading = false,
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
        _uiState.value = state.copy(error = "No existing chat with $name yet.")
    }

    fun openChat(chatId: String) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            runCatching {
                _uiState.value = _uiState.value.copy(
                    loading = true,
                    selectedChatId = chatId,
                    error = null,
                    connectionTitle = "Updating...",
                )
                val result = withContext(Dispatchers.IO) { api.getMessages(session, chatId) }
                _uiState.value = _uiState.value.copy(
                    usersById = _uiState.value.usersById + result.usersById,
                    messages = result.messages.sortedWith(compareBy<ChatMessage> { it.createdAt }.thenBy { it.id }),
                    loading = false,
                    connectionTitle = "Noveo",
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = null,
                    connectionTitle = "Connecting...",
                    connectionDetail = it.message,
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
                    error = null,
                    connectionTitle = "Connecting...",
                    connectionDetail = it.message,
                )
            }
        }
    }

    fun searchPublicDirectory(query: String) {
        val session = _uiState.value.session ?: return
        val normalized = query.trim()
        if (normalized.length < 2) return
        viewModelScope.launch {
            runCatching {
                val foundUsers = withContext(Dispatchers.IO) { api.searchPublicUsers(session, normalized) }
                if (foundUsers.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        usersById = _uiState.value.usersById + foundUsers.associateBy { it.id },
                        error = null
                    )
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = null)
            }
        }
    }
    
    fun updateProfile(username: String, bio: String) {
        val session: Session = _uiState.value.session ?: return
        viewModelScope.launch {
            runCatching {
                _uiState.value = _uiState.value.copy(loading = true)
                withContext(Dispatchers.IO) {
                    api.updateProfile(session, username, bio)
                }
                // Refresh contacts and home data to get updated profile
                loadHome(session, preserveShell = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(loading = false, error = it.message)
            }
        }
    }

    private suspend fun loadHome(session: Session, preserveShell: Boolean) {
        _uiState.value = _uiState.value.copy(
            startupState = StartupState.Home,
            session = session,
            loading = true,
            connectionTitle = "Updating...",
            connectionDetail = null,
        )
        runCatching {
            val home = withContext(Dispatchers.IO) { api.getHomeData(session) }
            val selectedChatId = if (preserveShell) _uiState.value.selectedChatId else null
            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                loading = false,
                session = session,
                usersById = home.usersById,
                chats = home.chats,
                selectedChatId = selectedChatId,
                messages = if (selectedChatId == null) emptyList() else _uiState.value.messages,
                error = null,
                connectionTitle = "Noveo",
                connectionDetail = null,
            )
            observeSocket(session)
        }.onFailure {
            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                loading = false,
                session = session,
                error = it.message,
                connectionTitle = "Connecting...",
                connectionDetail = it.message,
            )
        }
    }

    private suspend fun refreshHomeAndSelectedChat(
        session: Session,
        chatId: String? = _uiState.value.selectedChatId,
    ) {
        runCatching {
            _uiState.value = _uiState.value.copy(connectionTitle = "Updating...")
            val home = withContext(Dispatchers.IO) { api.getHomeData(session) }
            val refreshedMessages = if (chatId.isNullOrBlank()) {
                _uiState.value.messages
            } else {
                withContext(Dispatchers.IO) { api.getMessages(session, chatId) }
                    .messages
                    .sortedWith(compareBy<ChatMessage> { it.createdAt }.thenBy { it.id })
            }
            _uiState.value = _uiState.value.copy(
                usersById = home.usersById,
                chats = home.chats,
                messages = refreshedMessages,
                loading = false,
                error = null,
                connectionTitle = "Noveo",
                connectionDetail = null,
            )
        }.onFailure {
            _uiState.value = _uiState.value.copy(
                loading = false,
                error = it.message,
                connectionTitle = "Connecting...",
                connectionDetail = it.message,
            )
        }
    }

    private fun observeSocket(session: Session) {
        socketJob?.cancel()
        socketJob = viewModelScope.launch {
            runCatching {
                socket.connect(session, _uiState.value.usersById).collect { incoming ->
                    val state = _uiState.value
                    val updatedMessages = if (incoming.chatId == state.selectedChatId) {
                        (state.messages + incoming)
                            .distinctBy { message ->
                                message.id.ifBlank { "${message.chatId}-${message.createdAt}-${message.senderId}" }
                            }
                            .sortedWith(compareBy<ChatMessage> { it.createdAt }.thenBy { it.id })
                    } else {
                        state.messages
                    }

                    val movedChat = state.chats.firstOrNull { it.id == incoming.chatId }?.copy(
                        lastMessagePreview = incoming.content.previewText().ifBlank {
                            state.chats.firstOrNull { it.id == incoming.chatId }?.lastMessagePreview.orEmpty()
                        },
                        unreadCount = if (incoming.chatId == state.selectedChatId) {
                            0
                        } else {
                            (state.chats.firstOrNull { it.id == incoming.chatId }?.unreadCount ?: 0) + 1
                        },
                    )
                    val remainingChats = state.chats.filterNot { it.id == incoming.chatId }
                    val updatedChats = listOfNotNull(movedChat) + remainingChats

                    _uiState.value = state.copy(
                        chats = updatedChats,
                        messages = updatedMessages,
                        error = null,
                        connectionTitle = "Noveo",
                        connectionDetail = null,
                    )
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    connectionTitle = "Connecting...",
                    connectionDetail = it.message,
                    error = null,
                    loading = false,
                )
            }
        }
    }
}
