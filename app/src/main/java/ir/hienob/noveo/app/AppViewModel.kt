package ir.hienob.noveo.app


import android.app.Application
import android.util.Log
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
    val contacts: List<UserSummary> = emptyList(),
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

    init {
        restoreSession()
        observeSocket()
    }

    private fun observeSocket() {
        viewModelScope.launch {
            socket.events.collect { event ->
                when (event) {
                    is ChatSocket.SocketEvent.Connected -> {
                        _uiState.value = _uiState.value.copy(
                            connectionTitle = "Noveo",
                            connectionDetail = null
                        )
                    }
                    is ChatSocket.SocketEvent.Error -> {
                        // Only show connecting if we are not already connected or if it's a persistent error
                        _uiState.value = _uiState.value.copy(
                            connectionTitle = "Waiting for network...",
                            connectionDetail = event.message
                        )
                        _uiState.value.session?.let {
                            delay(3000)
                            socket.connect(it)
                        }
                    }
                    is ChatSocket.SocketEvent.NewMessage -> {
                        handleIncomingMessage(event.message)
                    }
                    is ChatSocket.SocketEvent.UserUpdate -> {
                        _uiState.value = _uiState.value.copy(
                            usersById = _uiState.value.usersById + event.users,
                            onlineUserIds = event.onlineIds
                        )
                    }
                }
            }
        }
    }

    private fun handleIncomingMessage(incoming: ChatMessage) {
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
            connectionTitle = "Noveo"
        )
    }

    fun restoreSession() {
        viewModelScope.launch {
            val session = sessionStore.read()
            if (session == null) {
                _uiState.value = _uiState.value.copy(
                    startupState = StartupState.Onboarding,
                    loading = false
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                session = session,
                loading = true,
                connectionTitle = "Connecting..."
            )
            socket.connect(session)
            loadHomeData(session)
        }
    }

    private suspend fun loadHomeData(session: Session) {
        runCatching {
            val home = withContext(Dispatchers.IO) { api.getHomeData(session) }
            val contacts = withContext(Dispatchers.IO) { api.getContacts(session) }
            _uiState.value = _uiState.value.copy(
                loading = false,
                usersById = _uiState.value.usersById + home.usersById + contacts.associateBy { it.id },
                onlineUserIds = _uiState.value.onlineUserIds + home.onlineUserIds,
                chats = home.chats,
                contacts = contacts,
                connectionTitle = "Noveo"
            )
        }.onFailure {
            Log.e("AppViewModel", "Failed to load home data", it)
            _uiState.value = _uiState.value.copy(loading = false)
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
                _uiState.value = _uiState.value.copy(loading = true, error = null)
                val session = withContext(Dispatchers.IO) {
                    if (_uiState.value.authModeSignup) api.signup(handle, password) else api.login(handle, password)
                }
                sessionStore.write(session)
                _uiState.value = _uiState.value.copy(
                    startupState = StartupState.Home,
                    session = session,
                    connectionTitle = "Connecting..."
                )
                socket.connect(session)
                loadHomeData(session)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = it.message ?: "Authentication failed"
                )
            }
        }
    }

    fun logout() {
        socket.disconnect()
        sessionStore.clear()
        _uiState.value = AppUiState(startupState = StartupState.Auth)
    }

    fun backToChatList() {
        _uiState.value = _uiState.value.copy(
            selectedChatId = null,
            messages = emptyList()
        )
    }

    fun openChat(chatId: String) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            runCatching {
                _uiState.value = _uiState.value.copy(
                    loading = true,
                    selectedChatId = chatId,
                    connectionTitle = "Updating..."
                )
                val result = withContext(Dispatchers.IO) { api.getMessages(session, chatId) }
                _uiState.value = _uiState.value.copy(
                    usersById = _uiState.value.usersById + result.usersById,
                    messages = result.messages.sortedWith(compareBy<ChatMessage> { it.createdAt }.thenBy { it.id }),
                    loading = false,
                    connectionTitle = "Noveo"
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    connectionTitle = "Noveo"
                )
            }
        }
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
        // If no existing chat, we might need a way to create one.
        // For now, let's just show an error or handle it.
        _uiState.value = state.copy(error = "No existing chat found.")
    }

    fun sendMessage(text: String) {
        val chatId = _uiState.value.selectedChatId ?: return
        if (text.isBlank()) return
        
        // Optimistic update
        val selfUserId = _uiState.value.session?.userId ?: "me"
        val tempMessage = ChatMessage(
            id = "temp-${System.currentTimeMillis()}",
            chatId = chatId,
            senderId = selfUserId,
            senderName = _uiState.value.usersById[selfUserId]?.username ?: "Me",
            content = ir.hienob.noveo.data.MessageContent(text = text),
            createdAt = System.currentTimeMillis()
        )
        
        _uiState.value = _uiState.value.copy(
            messages = (_uiState.value.messages + tempMessage).sortedBy { it.createdAt }
        )

        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                socket.sendMessage(chatId, text)
            }
            if (!success) {
                // If socket fails, try legacy API as fallback
                runCatching {
                    api.sendMessage(_uiState.value.session!!, chatId, text)
                }
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
                        usersById = _uiState.value.usersById + foundUsers.associateBy { it.id }
                    )
                }
            }.onFailure {
                // Silently fail search
            }
        }
    }
    
    fun updateProfile(username: String, bio: String) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            runCatching {
                _uiState.value = _uiState.value.copy(loading = true)
                withContext(Dispatchers.IO) { api.updateProfile(session, username, bio) }
                // Refresh contacts and home data to get updated profile
                loadHomeData(session)
            }.onFailure {
                _uiState.value = _uiState.value.copy(loading = false, error = it.message)
            }
        }
    }
}
