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
import ir.hienob.noveo.data.SocketEvent
import ir.hienob.noveo.data.UserSummary
import ir.hienob.noveo.data.Wallet
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
    val wallet: Wallet? = null,
    val contacts: List<UserSummary> = emptyList(),
    val typingUsers: Map<String, Set<String>> = emptyMap() // chatId -> set of userIds
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
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                session = session,
                loading = true,
                connectionTitle = "Connecting...",
            )
            loadHome(session)
        }
    }

    fun dismissOnboarding() {
        _uiState.value = _uiState.value.copy(startupState = StartupState.Auth)
    }

    fun setAuthMode(signup: Boolean) {
        _uiState.value = _uiState.value.copy(authModeSignup = signup, error = null)
    }

    fun authenticate(handle: String, password: String) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = _uiState.value.copy(startupState = StartupState.Home, loading = true, connectionTitle = "Connecting...")
                val session = withContext(Dispatchers.IO) {
                    if (_uiState.value.authModeSignup) api.signup(handle, password) else api.login(handle, password)
                }
                sessionStore.write(session)
                loadHome(session)
            }.onFailure {
                _uiState.value = _uiState.value.copy(startupState = StartupState.Auth, loading = false, error = it.message ?: "Authentication failed")
            }
        }
    }

    fun logout() {
        socketJob?.cancel()
        sessionStore.clear()
        _uiState.value = AppUiState(startupState = StartupState.Auth)
    }

    fun backToChatList() {
        _uiState.value = _uiState.value.copy(selectedChatId = null, messages = emptyList())
    }

    fun openDirectChat(userId: String) {
        val state = _uiState.value
        val existingChat = state.chats.firstOrNull { chat ->
            chat.chatType == "private" && chat.memberIds.contains(userId)
        }
        if (existingChat != null) {
            openChat(existingChat.id)
            return
        }
        // If not exists, we can't open messages, but we can search for a temporary chatId pattern if server supports it
    }

    fun openChat(chatId: String) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedChatId = chatId, messages = emptyList(), loading = true)
            runCatching {
                val result = withContext(Dispatchers.IO) { api.getMessages(session, chatId) }
                _uiState.value = _uiState.value.copy(
                    usersById = _uiState.value.usersById + result.usersById,
                    messages = result.messages.sortedBy { it.timestamp },
                    loading = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(loading = false, error = it.message)
            }
        }
    }

    fun sendMessage(text: String) {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        if (text.isBlank()) return

        val tempId = "temp-${System.currentTimeMillis()}"
        val pendingMsg = ChatMessage(
            id = tempId,
            chatId = chatId,
            senderId = session.userId,
            senderName = _uiState.value.usersById[session.userId]?.username ?: "Me",
            content = MessageContent(text = text),
            timestamp = System.currentTimeMillis() / 1000,
            pending = true,
            clientTempId = tempId
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + pendingMsg
        )

        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.sendMessage(session, chatId, text, clientTempId = tempId) }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages.filter { it.id != tempId }
                )
            }
        }
    }

    fun sendTyping() {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { api.sendTyping(session, chatId) }
        }
    }

    fun markAsSeen(messageId: String) {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { api.markAsSeen(session, chatId, messageId) }
        }
    }

    private suspend fun loadHome(session: Session) {
        runCatching {
            val home = withContext(Dispatchers.IO) { api.getHomeData(session) }
            val wallet = withContext(Dispatchers.IO) { runCatching { api.getStarsOverview(session) }.getOrNull() }
            val contacts = withContext(Dispatchers.IO) { runCatching { api.getContacts(session) }.getOrDefault(emptyList()) }
            
            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                loading = false,
                session = session,
                usersById = home.usersById,
                chats = home.chats,
                wallet = wallet,
                contacts = contacts,
                connectionTitle = "Noveo",
            )
            observeSocket(session)
        }.onFailure {
            _uiState.value = _uiState.value.copy(loading = false, error = it.message, connectionTitle = "Connecting...")
        }
    }

    private fun observeSocket(session: Session) {
        socketJob?.cancel()
        socketJob = viewModelScope.launch {
            socket.connect(session) { _uiState.value.usersById }.collect { event ->
                when (event) {
                    is SocketEvent.NewMessage -> handleIncomingMessage(event.message)
                    is SocketEvent.MessageSent -> handleIncomingMessage(event.message)
                    is SocketEvent.Typing -> handleTyping(event.chatId, event.senderId)
                    is SocketEvent.MessageSeenUpdate -> handleSeenUpdate(event.chatId, event.messageId, event.userId)
                    is SocketEvent.UserListUpdate -> {
                        _uiState.value = _uiState.value.copy(
                            usersById = _uiState.value.usersById + event.usersById,
                            onlineUserIds = event.onlineIds
                        )
                    }
                    is SocketEvent.ChatUpdated -> {
                        // Could trigger a single chat refresh
                    }
                }
            }
        }
    }

    private fun handleIncomingMessage(msg: ChatMessage) {
        val state = _uiState.value
        val messages = state.messages.toMutableList()
        
        val index = if (msg.clientTempId != null) {
            messages.indexOfFirst { it.clientTempId == msg.clientTempId }
        } else {
            messages.indexOfFirst { it.id == msg.id }
        }

        if (index != -1) {
            messages[index] = msg
        } else if (msg.chatId == state.selectedChatId) {
            messages.add(msg)
            markAsSeen(msg.id)
        }

        val updatedChats = state.chats.map {
            if (it.id == msg.chatId) {
                it.copy(
                    lastMessagePreview = msg.content.previewText(),
                    unreadCount = if (msg.chatId == state.selectedChatId) 0 else it.unreadCount + 1
                )
            } else it
        }

        _uiState.value = state.copy(
            messages = messages.sortedBy { it.timestamp },
            chats = updatedChats
        )
    }

    private fun handleTyping(chatId: String, userId: String) {
        val currentTyping = _uiState.value.typingUsers[chatId].orEmpty()
        _uiState.value = _uiState.value.copy(
            typingUsers = _uiState.value.typingUsers + (chatId to (currentTyping + userId))
        )
        viewModelScope.launch {
            delay(3000)
            val stillTyping = _uiState.value.typingUsers[chatId].orEmpty() - userId
            _uiState.value = _uiState.value.copy(
                typingUsers = _uiState.value.typingUsers + (chatId to stillTyping)
            )
        }
    }

    private fun handleSeenUpdate(chatId: String, messageId: String, userId: String) {
        if (chatId != _uiState.value.selectedChatId) return
        val messages = _uiState.value.messages.map {
            if (it.id == messageId) {
                it.copy(seenBy = (it.seenBy + userId).distinct())
            } else it
        }
        _uiState.value = _uiState.value.copy(messages = messages)
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
                        usersById = _uiState.value.usersById + foundUsers.associateBy { it.id }
                    )
                }
            }
        }
    }

    fun updateProfile(username: String, bio: String) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.updateProfile(session, username, bio) }
                loadHome(session)
            }
        }
    }
}
