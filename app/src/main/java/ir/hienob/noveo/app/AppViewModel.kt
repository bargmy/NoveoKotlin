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
    private val messageCacheByChat = mutableMapOf<String, List<ChatMessage>>()

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
        messageCacheByChat.clear()
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
            val cachedMessages = messageCacheByChat[chatId].orEmpty().sortedBy { it.timestamp }
            val updatedChats = _uiState.value.chats.map {
                if (it.id == chatId) it.copy(unreadCount = 0) else it
            }
            _uiState.value = _uiState.value.copy(
                selectedChatId = chatId,
                chats = updatedChats,
                messages = cachedMessages,
                loading = true
            )
            runCatching {
                val result = withContext(Dispatchers.IO) { api.getMessages(session, chatId) }
                val mergedMessages = mergeMessages(messageCacheByChat[chatId].orEmpty(), result.messages)
                messageCacheByChat[chatId] = mergedMessages
                val currentState = _uiState.value
                _uiState.value = currentState.copy(
                    usersById = currentState.usersById + result.usersById,
                    messages = if (currentState.selectedChatId == chatId) mergedMessages else currentState.messages,
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
        messageCacheByChat[chatId] = mergeMessages(messageCacheByChat[chatId].orEmpty(), listOf(pendingMsg))

        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { api.sendMessage(session, chatId, text, clientTempId = tempId) }
            }.onFailure {
                messageCacheByChat[chatId] = messageCacheByChat[chatId].orEmpty().filter { it.id != tempId }
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

    fun refreshHomeSilently() {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            runCatching {
                val home = withContext(Dispatchers.IO) { api.getHomeData(session) }
                _uiState.value = _uiState.value.copy(
                    usersById = _uiState.value.usersById + home.usersById,
                    chats = home.chats,
                    onlineUserIds = home.onlineUserIds
                )
            }
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
            while (true) {
                runCatching {
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
                                refreshHomeSilently()
                            }
                            is SocketEvent.HistoryUpdate -> {
                                _uiState.value = _uiState.value.copy(
                                    chats = event.chats,
                                    usersById = _uiState.value.usersById + event.users,
                                    connectionDetail = null
                                )
                            }
                        }
                    }
                }.onFailure {
                    _uiState.value = _uiState.value.copy(connectionDetail = "Connection lost, retrying...")
                    delay(3000)
                }
            }
        }
    }

    private fun handleIncomingMessage(msg: ChatMessage) {
        val latestState = _uiState.value
        val session = latestState.session ?: return
        val baseMessages = if (msg.chatId == latestState.selectedChatId) {
            mergeMessages(messageCacheByChat[msg.chatId].orEmpty(), latestState.messages)
        } else {
            messageCacheByChat[msg.chatId].orEmpty()
        }
        val cachedMessages = mergeMessages(baseMessages, listOf(msg))
        messageCacheByChat[msg.chatId] = cachedMessages

        val currentChats = latestState.chats.toMutableList()
        val chatIndex = currentChats.indexOfFirst { it.id == msg.chatId }

        if (chatIndex != -1) {
            val chat = currentChats.removeAt(chatIndex)
            val updatedChat = chat.copy(
                lastMessagePreview = msg.content.previewText(),
                unreadCount = when {
                    msg.chatId == latestState.selectedChatId -> 0
                    msg.senderId == session.userId -> chat.unreadCount
                    else -> chat.unreadCount + 1
                }
            )
            currentChats.add(0, updatedChat)
        } else {
            refreshHomeSilently()
        }

        val isSelectedChat = msg.chatId == latestState.selectedChatId
        _uiState.value = latestState.copy(
            messages = if (isSelectedChat) cachedMessages else latestState.messages,
            chats = currentChats
        )

        if (isSelectedChat && msg.senderId != session.userId) {
            markAsSeen(msg.id)
        }
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
        messageCacheByChat[chatId] = messageCacheByChat[chatId].orEmpty().map {
            if (it.id == messageId) {
                it.copy(seenBy = (it.seenBy + userId).distinct())
            } else it
        }
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

private fun mergeMessages(existing: List<ChatMessage>, incoming: List<ChatMessage>): List<ChatMessage> {
    if (incoming.isEmpty()) return existing.sortedBy { it.timestamp }

    val merged = existing.toMutableList()
    for (message in incoming) {
        val existingIndex = if (message.clientTempId != null) {
            merged.indexOfFirst { it.clientTempId == message.clientTempId }
        } else {
            merged.indexOfFirst { it.id == message.id }
        }

        if (existingIndex >= 0) {
            merged[existingIndex] = message
        } else {
            merged.add(message)
        }
    }
    return merged.sortedBy { it.timestamp }
}
