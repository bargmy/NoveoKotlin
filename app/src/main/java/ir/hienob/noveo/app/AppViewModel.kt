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
    val authModeSignup: Boolean = false
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
            _uiState.value = _uiState.value.copy(startupState = StartupState.Splash, loading = true, error = null)
            val session = sessionStore.read()
            if (session == null) {
                _uiState.value = _uiState.value.copy(startupState = StartupState.Onboarding, loading = false, selectedChatId = null, messages = emptyList())
                return@launch
            }
            loadHome(session)
        }
    }

    fun dismissOnboarding() {
        _uiState.value = _uiState.value.copy(startupState = StartupState.Auth, loading = false, error = null)
    }

    fun setAuthMode(signup: Boolean) {
        _uiState.value = _uiState.value.copy(authModeSignup = signup, error = null)
    }

    fun authenticate(handle: String, password: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = _uiState.value.copy(loading = true, error = null)
                val session = withContext(Dispatchers.IO) {
                    if (_uiState.value.authModeSignup) api.signup(handle, password) else api.login(handle, password)
                }
                sessionStore.write(session)
                loadHome(session)
            }.onFailure {
                _uiState.value = _uiState.value.copy(loading = false, error = it.message ?: "Authentication failed")
            }
        }
    }

    fun logout() {
        socketJob?.cancel()
        sessionStore.clear()
        _uiState.value = AppUiState(startupState = StartupState.Auth)
    }

    fun backToChatList() {
        _uiState.value = _uiState.value.copy(selectedChatId = null, messages = emptyList(), error = null, loading = false)
    }

    fun openDirectChat(userId: String) {
        val state = _uiState.value
        val existingChat = state.chats.firstOrNull { chat ->
            chat.chatType == "private" && chat.memberIds.contains(userId) && state.session?.userId?.let(chat.memberIds::contains) == true
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
                _uiState.value = _uiState.value.copy(loading = true, selectedChatId = chatId, error = null)
                val result = withContext(Dispatchers.IO) { api.getMessages(session, chatId) }
                _uiState.value = _uiState.value.copy(
                    usersById = _uiState.value.usersById + result.usersById,
                    messages = result.messages,
                    loading = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(loading = false, error = it.message ?: "Unable to load chat")
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
                val result = withContext(Dispatchers.IO) { api.getMessages(session, chatId) }
                _uiState.value = _uiState.value.copy(usersById = _uiState.value.usersById + result.usersById, messages = result.messages, error = null)
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message ?: "Unable to send")
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
                error = null
            )
            observeSocket(session)
        }.onFailure {
            sessionStore.clear()
            _uiState.value = _uiState.value.copy(startupState = StartupState.Auth, loading = false, error = it.message)
        }
    }

    private fun observeSocket(session: Session) {
        socketJob?.cancel()
        socketJob = viewModelScope.launch {
            socket.connect(session, _uiState.value.usersById).collect { incoming ->
                val state = _uiState.value
                val mergedUsers = if (incoming.senderId.isNotBlank() && !state.usersById.containsKey(incoming.senderId)) state.usersById else state.usersById
                if (incoming.chatId == state.selectedChatId) {
                    _uiState.value = state.copy(usersById = mergedUsers, messages = state.messages + incoming)
                }
            }
        }
    }
}
