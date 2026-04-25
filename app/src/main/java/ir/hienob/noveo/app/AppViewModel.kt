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
import ir.hienob.noveo.ui.getStrings
import java.io.File
import java.io.FileOutputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
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
    val typingUsers: Map<String, Set<String>> = emptyMap(), // chatId -> set of userIds
    val replyingToMessage: ChatMessage? = null,
    val languageCode: String = java.util.Locale.getDefault().language,
    val updateInfo: UpdateInfo? = null,
    val isCheckingUpdate: Boolean = false
)

data class UpdateInfo(
    val version: String,
    val url: String,
    val isAvailable: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val isDownloaded: Boolean = false,
    val localPath: String? = null,
    val isDismissed: Boolean = false
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionStore = SessionStore(application)
    private val api = NoveoApi()
    private val socket = ChatSocket()
    private val messageCacheByChat = mutableMapOf<String, List<ChatMessage>>()

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var socketJob: Job? = null
    private var socketResyncJob: Job? = null
    private var selectedChatRefreshJob: Job? = null

    init {
        restoreSession()
        checkForUpdate()
    }

    fun checkForUpdate(manual: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (manual) _uiState.value = _uiState.value.copy(isCheckingUpdate = true)
            val updateJson = api.checkForUpdate()
            if (manual) delay(500) // Small delay for UX

            if (updateJson == null) {
                if (manual) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(isCheckingUpdate = false, error = getStrings(_uiState.value.languageCode).noUpdateAvailable)
                        delay(2000)
                        _uiState.value = _uiState.value.copy(error = null)
                    }
                }
                return@launch
            }

            val version = updateJson.optString("version")
            val url = updateJson.optString("url")
            val currentVersion = ir.hienob.noveo.BuildConfig.VERSION_NAME

            if (version > currentVersion) {
                val apkFile = File(getApplication<Application>().getExternalFilesDir(null), "update-$version.apk")
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isCheckingUpdate = false,
                        updateInfo = UpdateInfo(
                            version = version,
                            url = url,
                            isAvailable = true,
                            isDownloaded = apkFile.exists(),
                            localPath = if (apkFile.exists()) apkFile.absolutePath else null,
                            isDismissed = false // Re-show bubble if manual check
                        )
                    )
                }
            } else if (manual) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(isCheckingUpdate = false, error = getStrings(_uiState.value.languageCode).noUpdateAvailable)
                    delay(2000)
                    _uiState.value = _uiState.value.copy(error = null)
                }
            }
        }
    }

    fun downloadUpdate() {
        val info = _uiState.value.updateInfo ?: return
        if (info.isDownloading || info.isDownloaded) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(
                updateInfo = info.copy(isDownloading = true, downloadProgress = 0f)
            )

            val client = OkHttpClient()
            val request = Request.Builder().url(info.url).build()
            runCatching {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw Exception("Download failed")
                    val body = response.body ?: throw Exception("Empty body")
                    val total = body.contentLength()
                    val apkFile = File(getApplication<Application>().getExternalFilesDir(null), "update-${info.version}.apk")

                    body.byteStream().use { input ->
                        FileOutputStream(apkFile).use { output ->
                            val buffer = ByteArray(8192)
                            var read: Int
                            var current = 0L
                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                                current += read
                                withContext(Dispatchers.Main) {
                                    _uiState.value = _uiState.value.copy(
                                        updateInfo = _uiState.value.updateInfo?.copy(
                                            downloadProgress = if (total > 0) current.toFloat() / total else 0f
                                        )
                                    )
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            updateInfo = _uiState.value.updateInfo?.copy(
                                isDownloading = false,
                                isDownloaded = true,
                                localPath = apkFile.absolutePath
                            )
                        )
                    }
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        updateInfo = _uiState.value.updateInfo?.copy(isDownloading = false),
                        error = "Download failed: ${it.message}"
                    )
                }
            }
        }
    }

    fun dismissUpdate() {
        _uiState.value = _uiState.value.copy(
            updateInfo = _uiState.value.updateInfo?.copy(isDismissed = true)
        )
    }

    fun installUpdate() {
        val info = _uiState.value.updateInfo ?: return
        val path = info.localPath ?: return
        val apkFile = File(path)
        if (!apkFile.exists()) return

        val context = getApplication<Application>()
        
        runCatching {
            // Android 8.0+ check for unknown apps permission
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    return@runCatching
                }
            }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }.onFailure {
            _uiState.value = _uiState.value.copy(error = "Installation failed: ${it.message}")
        }
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
                connectionTitle = "Noveo",
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
                _uiState.value = _uiState.value.copy(startupState = StartupState.Home, loading = true, connectionTitle = "Noveo")
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
        socketResyncJob?.cancel()
        selectedChatRefreshJob?.cancel()
        messageCacheByChat.clear()
        sessionStore.clear()
        _uiState.value = AppUiState(startupState = StartupState.Auth)
    }

    fun backToChatList() {
        selectedChatRefreshJob?.cancel()
        _uiState.value = _uiState.value.copy(selectedChatId = null, messages = emptyList(), replyingToMessage = null)
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
        ensureSocketObserved(session)
        startSelectedChatRefresh(session, chatId)
        viewModelScope.launch {
            val cachedMessages = messageCacheByChat[chatId].orEmpty().sortedBy { it.timestamp }
            val updatedChats = _uiState.value.chats.map {
                if (it.id == chatId) it.copy(unreadCount = 0) else it
            }
            _uiState.value = _uiState.value.copy(
                selectedChatId = chatId,
                chats = updatedChats,
                messages = cachedMessages,
                loading = cachedMessages.isEmpty(),
                replyingToMessage = null
            )
            refreshHomeSilently()
        }
    }

    fun setReplyingTo(message: ChatMessage?) {
        _uiState.value = _uiState.value.copy(replyingToMessage = message)
    }

    fun sendMessage(text: String) {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        val replyingTo = _uiState.value.replyingToMessage
        if (text.isBlank()) return

        val tempId = "temp-${System.currentTimeMillis()}"
        val pendingMsg = ChatMessage(
            id = tempId,
            chatId = chatId,
            senderId = session.userId,
            senderName = _uiState.value.usersById[session.userId]?.username ?: "Me",
            content = MessageContent(text = text, replyToId = replyingTo?.id),
            timestamp = System.currentTimeMillis() / 1000,
            pending = true,
            clientTempId = tempId,
            replyToId = replyingTo?.id
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + pendingMsg,
            replyingToMessage = null
        )
        messageCacheByChat[chatId] = mergeMessages(messageCacheByChat[chatId].orEmpty(), listOf(pendingMsg))

        val contentObj = org.json.JSONObject().put("text", text)
        if (replyingTo != null) {
            contentObj.put("replyToId", replyingTo.id)
        }

        val payload = org.json.JSONObject()
            .put("type", "message")
            .put("chatId", chatId)
            .put("content", contentObj.toString())
            .put("clientTempId", tempId)
            .put("replyToId", replyingTo?.id)
        
        val sent = socket.send(payload)
    }

    fun sendTyping() {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        val payload = org.json.JSONObject()
            .put("type", "typing")
            .put("chatId", chatId)
        socket.send(payload)
    }

    fun markAsSeen(messageId: String) {
        val session = _uiState.value.session ?: return
        val chatId = _uiState.value.selectedChatId ?: return
        val payload = org.json.JSONObject()
            .put("type", "message_seen")
            .put("chatId", chatId)
            .put("messageId", messageId)
        socket.send(payload)
    }

    fun loadOlderMessages() {
        val state = _uiState.value
        val chatId = state.selectedChatId ?: return
        val oldestMsg = state.messages.minByOrNull { it.timestamp } ?: return
        
        val payload = org.json.JSONObject()
            .put("type", "load_older_messages")
            .put("chatId", chatId)
            .put("beforeTimestamp", oldestMsg.timestamp)
            .put("beforeMessageId", oldestMsg.id)
        
        socket.send(payload)
    }

    fun refreshHomeSilently() {
        val session = _uiState.value.session ?: return
        val payload = org.json.JSONObject().put("type", "resync_state")
        socket.send(payload)
    }

    private suspend fun loadHome(session: Session) {
        // STRICTLY observe socket, no API load for chats
        observeSocket(session)
        
        // Only load non-socket features via HTTP
        runCatching {
            val wallet = withContext(Dispatchers.IO) { runCatching { api.getStarsOverview(session) }.getOrNull() }
            val contacts = withContext(Dispatchers.IO) { runCatching { api.getContacts(session) }.getOrDefault(emptyList()) }
            
            _uiState.value = _uiState.value.copy(
                startupState = StartupState.Home,
                loading = false,
                session = session,
                wallet = wallet,
                contacts = contacts,
                connectionTitle = "Noveo",
            )
        }
    }

    private fun observeSocket(session: Session) {
        if (socketJob?.isActive == true) return
        startSocketResyncLoop()
        socketJob = viewModelScope.launch {
            while (true) {
                runCatching {
                    socket.connect(
                        session = session,
                        getKnownUsers = { _uiState.value.usersById }
                    ).collect { event ->
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
                            is SocketEvent.ChatUpdated -> refreshHomeSilently()
                            is SocketEvent.HistoryUpdate -> {
                                event.messagesByChat.forEach { (chatId, incomingMessages) ->
                                    messageCacheByChat[chatId] = mergeMessages(
                                        messageCacheByChat[chatId].orEmpty(),
                                        incomingMessages
                                    )
                                }
                                val selectedChatMessages = _uiState.value.selectedChatId
                                    ?.let { messageCacheByChat[it].orEmpty() }
                                    ?: _uiState.value.messages

                                val self = event.users[session.userId]
                                
                                _uiState.value = _uiState.value.copy(
                                    chats = event.chats,
                                    usersById = _uiState.value.usersById + event.users,
                                    messages = selectedChatMessages,
                                    loading = false,
                                    connectionDetail = null,
                                    connectionTitle = "Noveo",
                                    languageCode = self?.languageCode ?: _uiState.value.languageCode
                                )
                            }
                            is SocketEvent.OlderMessages -> {
                                val currentMessages = messageCacheByChat[event.chatId].orEmpty()
                                val updatedMessages = mergeMessages(currentMessages, event.messages)
                                messageCacheByChat[event.chatId] = updatedMessages
                                
                                val updatedChats = _uiState.value.chats.map {
                                    if (it.id == event.chatId) it.copy(hasMoreHistory = event.hasMoreHistory) else it
                                }

                                if (event.chatId == _uiState.value.selectedChatId) {
                                    _uiState.value = _uiState.value.copy(
                                        messages = updatedMessages,
                                        chats = updatedChats
                                    )
                                } else {
                                    _uiState.value = _uiState.value.copy(chats = updatedChats)
                                }
                            }
                        }
                    }
                }.onFailure {
                    _uiState.value = _uiState.value.copy(
                        connectionTitle = "Noveo",
                        connectionDetail = null
                    )
                    delay(1500)
                }
            }
        }
    }

    private fun ensureSocketObserved(session: Session) {
        observeSocket(session)
    }

    private fun startSelectedChatRefresh(session: Session, chatId: String) {
        selectedChatRefreshJob?.cancel()
        selectedChatRefreshJob = viewModelScope.launch {
            refreshHomeSilently()
            while (_uiState.value.session?.userId == session.userId && _uiState.value.selectedChatId == chatId) {
                delay(5000)
                refreshHomeSilently()
            }
        }
    }


    private suspend fun refreshSelectedChat(session: Session, chatId: String, reason: String) {
        // We now rely on WebSocket for real-time updates.
        // If a specific refresh is needed, we could send a targeted message sync request via socket.
    }

    private fun startSocketResyncLoop() {
        if (socketResyncJob?.isActive == true) return
        socketResyncJob = viewModelScope.launch {
            while (true) {
                delay(15000)
                if (_uiState.value.session != null) {
                    refreshHomeSilently()
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
        val payload = org.json.JSONObject()
            .put("type", "update_profile")
            .put("username", username)
            .put("bio", bio)
        socket.send(payload)
        // Optimistic update or wait for sync? The server should broadcast a user update.
        // For now, refresh home silently after a short delay
        viewModelScope.launch {
            delay(500)
            refreshHomeSilently()
        }
    }

    fun changePassword(old: String, new: String) {
        val payload = org.json.JSONObject()
            .put("type", "change_password")
            .put("oldPassword", old)
            .put("newPassword", new)
        socket.send(payload)
    }

    fun deleteAccount(password: String) {
        val payload = org.json.JSONObject()
            .put("type", "delete_account")
            .put("password", password)
        socket.send(payload)
    }

    fun setLanguage(code: String) {
        val payload = org.json.JSONObject()
            .put("type", "update_profile")
            .put("languageCode", code)
        socket.send(payload)
        _uiState.value = _uiState.value.copy(languageCode = code)
        viewModelScope.launch {
            delay(500)
            refreshHomeSilently()
        }
    }
}

private fun mergeMessages(existing: List<ChatMessage>, incoming: List<ChatMessage>): List<ChatMessage> {
    if (incoming.isEmpty()) return existing.sortedBy { it.timestamp }

    val merged = existing.toMutableList()
    for (message in incoming) {
        val existingIndex = if (message.clientTempId != null) {
            merged.indexOfFirst { it.clientTempId == message.clientTempId }
        } else if (message.id.isNotBlank()) {
            merged.indexOfFirst { it.id == message.id }
        } else {
            -1
        }

        if (existingIndex >= 0) {
            merged[existingIndex] = message
            continue
        }

        val pendingIndex = if (message.pending) {
            -1
        } else {
            findPendingReplacementIndex(merged, message)
        }

        if (pendingIndex >= 0) {
            merged[pendingIndex] = message
        } else {
            merged.add(message)
        }
    }
    return dedupeMergedMessages(merged).sortedBy { it.timestamp }
}

private fun findPendingReplacementIndex(messages: List<ChatMessage>, incoming: ChatMessage): Int {
    val pendingIndexes = messages.indices.filter { messages[it].pending }
    if (pendingIndexes.isEmpty()) return -1

    val exactSignatureIndex = pendingIndexes.firstOrNull { index ->
        messageMatchSignature(messages[index]) == messageMatchSignature(incoming)
    }
    if (exactSignatureIndex != null) return exactSignatureIndex

    val sameSenderCandidates = pendingIndexes.filter { index ->
        val current = messages[index]
        current.chatId == incoming.chatId &&
            current.senderId == incoming.senderId &&
            current.replyToId == incoming.replyToId &&
            isTimestampClose(current.timestamp, incoming.timestamp)
    }
    if (sameSenderCandidates.size == 1) return sameSenderCandidates.first()

    return -1
}

private fun dedupeMergedMessages(messages: List<ChatMessage>): List<ChatMessage> {
    val kept = mutableListOf<ChatMessage>()
    val seenIds = mutableMapOf<String, ChatMessage>()
    for (message in messages) {
        val id = message.id.takeIf { it.isNotBlank() }
        val existingById = id?.let(seenIds::get)
        if (existingById != null) {
            if (messageQualityScore(message) > messageQualityScore(existingById)) {
                val replaceIndex = kept.indexOf(existingById)
                if (replaceIndex >= 0) kept[replaceIndex] = message
                seenIds[id] = message
            }
            continue
        }

        val duplicatePendingIndex = kept.indexOfFirst { current ->
            current.pending != message.pending &&
                current.chatId == message.chatId &&
                current.senderId == message.senderId &&
                current.replyToId == message.replyToId &&
                messageMatchSignature(current) == messageMatchSignature(message) &&
                isTimestampClose(current.timestamp, message.timestamp)
        }
        if (duplicatePendingIndex >= 0) {
            if (messageQualityScore(message) >= messageQualityScore(kept[duplicatePendingIndex])) {
                kept[duplicatePendingIndex] = message
            }
            if (id != null) seenIds[id] = message
            continue
        }

        kept += message
        if (id != null) seenIds[id] = message
    }
    return kept
}

private fun messageMatchSignature(message: ChatMessage): String = buildString {
    append(message.chatId)
    append('|')
    append(message.senderId)
    append('|')
    append(message.replyToId.orEmpty())
    append('|')
    append(message.content.text.orEmpty().trim())
    append('|')
    append(message.content.file?.url.orEmpty())
    append('|')
    append(message.content.file?.name.orEmpty())
    append('|')
    append(message.content.file?.type.orEmpty())
    append('|')
    append(message.content.poll.orEmpty())
    append('|')
    append(message.content.theme.orEmpty())
    append('|')
    append(message.content.callLog.orEmpty())
}

private fun messageQualityScore(message: ChatMessage): Int {
    var score = 0
    if (!message.pending) score += 10
    if (!message.id.startsWith("temp-")) score += 5
    if (!message.clientTempId.isNullOrBlank()) score += 1
    return score
}

private fun isTimestampClose(left: Long, right: Long, maxDeltaSeconds: Long = 120L): Boolean {
    if (left <= 0L || right <= 0L) return false
    return kotlin.math.abs(left - right) <= maxDeltaSeconds
}
