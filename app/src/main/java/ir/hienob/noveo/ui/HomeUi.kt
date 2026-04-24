package ir.hienob.noveo.ui


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import ir.hienob.noveo.app.AppUiState
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSummary
import ir.hienob.noveo.data.MessageFileAttachment
import ir.hienob.noveo.data.Session
import ir.hienob.noveo.data.UserSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val NOVEO_BASE_URL = "https://noveo.ir:8443"
private const val CLIENT_VERSION = "v0.1 mobile"

private enum class SettingsSection {
    MENU, SUBSCRIPTION, PROFILE, ACCOUNT, PREFERENCES, CHANGELOG, THEME
}

private data class ThemeSection(
    val title: String,
    val subtitle: String,
    val presets: List<ThemePreset>
)

private enum class DebugConsoleTab(val label: String) {
    EVENTS("Events"),
    WEBSOCKET("WebSocket")
}

@Composable
internal fun HomeScreen(
    state: AppUiState,
    onOpenChat: (String) -> Unit,
    onStartDirectChat: (String) -> Unit,
    onSearchPublic: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    onLogout: () -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onClearDebugLogs: () -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showContactsModal by rememberSaveable { mutableStateOf(false) }
    var showCreateModal by rememberSaveable { mutableStateOf(false) }
    var showSettingsModal by rememberSaveable { mutableStateOf(false) }
    var showDebugConsole by rememberSaveable { mutableStateOf(false) }
    var settingsSection by rememberSaveable { mutableStateOf(SettingsSection.MENU) }
    var profileUserId by rememberSaveable { mutableStateOf<String?>(null) }
    var showGroupInfo by rememberSaveable { mutableStateOf(false) }
    var selectedMediaUrl by rememberSaveable { mutableStateOf<String?>(null) }

    val filteredChats = remember(state.chats, searchQuery) {
        state.chats.filter {
            searchQuery.isBlank() ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.lastMessagePreview.contains(searchQuery, ignoreCase = true) ||
                (it.handle?.contains(searchQuery, ignoreCase = true) == true)
        }
    }
    val filteredUsers = remember(state.usersById, searchQuery, state.session?.userId) {
        state.usersById.values
            .filter { it.id != state.session?.userId }
            .sortedBy { it.username.lowercase() }
            .filter {
                searchQuery.isBlank() ||
                    it.username.contains(searchQuery, ignoreCase = true) ||
                    (it.handle?.contains(searchQuery, ignoreCase = true) == true) ||
                    it.bio.contains(searchQuery, ignoreCase = true)
            }
    }

    val selectedChat = state.chats.firstOrNull { it.id == state.selectedChatId }
    val selectedProfile = remember(profileUserId, state.usersById) { profileUserId?.let(state.usersById::get) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val compact = maxWidth < 760.dp

        if (compact) {
            AnimatedContent(
                targetState = state.selectedChatId == null,
                label = "compact_shell_transition",
                transitionSpec = {
                    if (targetState) {
                        slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { it / 3 }) + fadeOut()
                    } else {
                        slideInHorizontally(initialOffsetX = { it / 3 }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut()
                    }.using(SizeTransform(clip = false))
                }
            ) { showList ->
                if (showList) {
                    SidebarPane(
                        state = state,
                        chats = filteredChats,
                        users = filteredUsers,
                        showSearch = showSearch,
                        searchQuery = searchQuery,
                        onMenuClick = { showMenu = true },
                        onSearchToggle = {
                            showSearch = !showSearch
                            if (!showSearch) searchQuery = ""
                        },
                        onSearchQueryChange = {
                            searchQuery = it
                            onSearchPublic(it)
                        },
                        onOpenDebugConsole = { showDebugConsole = true },
                        onOpenChat = onOpenChat,
                        onOpenContacts = { showContactsModal = true },
                        onOpenCreate = { showCreateModal = true },
                        onOpenSettings = {
                            settingsSection = SettingsSection.MENU
                            showSettingsModal = true
                        },
                        onOpenStars = {
                            settingsSection = SettingsSection.SUBSCRIPTION
                            showSettingsModal = true
                        },
                        onOpenProfile = { profileUserId = it },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ChatPane(
                        state = state,
                        compact = true,
                        selectedChat = selectedChat,
                        onBackToChats = onBackToChats,
                        onSend = onSend,
                        onTyping = onTyping,
                        onOpenDebugConsole = { showDebugConsole = true },
                        onMediaClick = { selectedMediaUrl = it },
                        onOpenProfile = { userId -> profileUserId = userId },
                        onOpenGroupInfo = { showGroupInfo = true }
                    )
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                SidebarPane(
                    state = state,
                    chats = filteredChats,
                    users = filteredUsers,
                    showSearch = showSearch,
                    searchQuery = searchQuery,
                    onMenuClick = { showMenu = true },
                    onSearchToggle = {
                        showSearch = !showSearch
                        if (!showSearch) searchQuery = ""
                    },
                    onSearchQueryChange = {
                        searchQuery = it
                        onSearchPublic(it)
                    },
                    onOpenDebugConsole = { showDebugConsole = true },
                    onOpenChat = onOpenChat,
                    onOpenContacts = { showContactsModal = true },
                    onOpenCreate = { showCreateModal = true },
                    onOpenSettings = {
                        settingsSection = SettingsSection.MENU
                        showSettingsModal = true
                    },
                    onOpenStars = {
                        settingsSection = SettingsSection.SUBSCRIPTION
                        showSettingsModal = true
                    },
                    onOpenProfile = { profileUserId = it },
                    modifier = Modifier.width(360.dp).fillMaxHeight()
                )
                AnimatedContent(
                    targetState = state.selectedChatId,
                    label = "wide_shell_transition",
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it / 5 }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { -it / 5 }) + fadeOut()
                    }
                ) { selectedId ->
                    if (selectedId == null) {
                        WelcomePane(modifier = Modifier.weight(1f))
                    } else {
                        ChatPane(
                            state = state,
                            compact = false,
                            selectedChat = selectedChat,
                            onBackToChats = onBackToChats,
                            onSend = onSend,
                            onTyping = onTyping,
                            onOpenDebugConsole = { showDebugConsole = true },
                            onMediaClick = { selectedMediaUrl = it },
                            onOpenProfile = { userId -> profileUserId = userId },
                            onOpenGroupInfo = { showGroupInfo = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showMenu,
            enter = fadeIn(animationSpec = tween(180)),
            exit = fadeOut(animationSpec = tween(180))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showMenu = false }
                    )
            )
        }

        AnimatedVisibility(
            visible = showMenu,
            enter = slideInHorizontally(initialOffsetX = { -it / 2 }) + fadeIn(animationSpec = tween(180)),
            exit = slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut(animationSpec = tween(180))
        ) {
            MenuSheet(
                state = state,
                onOpenContacts = {
                    showMenu = false
                    showContactsModal = true
                },
                onOpenCreate = {
                    showMenu = false
                    showCreateModal = true
                },
                onOpenStars = {
                    showMenu = false
                    settingsSection = SettingsSection.SUBSCRIPTION
                    showSettingsModal = true
                },
                onOpenSettings = {
                    showMenu = false
                    settingsSection = SettingsSection.MENU
                    showSettingsModal = true
                }
            )
        }

        ModalHost(visible = showContactsModal, onDismiss = { showContactsModal = false }) {
            ContactsModal(
                users = state.contacts,
                chats = state.chats,
                selfUserId = state.session?.userId,
                onClose = { showContactsModal = false },
                onMessage = { userId ->
                    showContactsModal = false
                    onStartDirectChat(userId)
                },
                onOpenProfile = { userId -> profileUserId = userId }
            )
        }
ModalHost(visible = showCreateModal, onDismiss = { showCreateModal = false }) {
    CreateChannelModal(onClose = { showCreateModal = false })
}

        if (selectedMediaUrl != null) {
            FullscreenMediaModal(
                url = selectedMediaUrl!!,
                onDismiss = { selectedMediaUrl = null }
            )
        }

        ModalHost(visible = showSettingsModal, onDismiss = { showSettingsModal = false }) {
            SettingsModal(
                state = state,
                section = settingsSection,
                onSectionChange = { settingsSection = it },
                onClose = { showSettingsModal = false },
                onLogout = onLogout,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange,
                onUpdateProfile = onUpdateProfile
            )
        }

        ModalHost(visible = showDebugConsole, onDismiss = { showDebugConsole = false }) {
            DebugConsoleModal(
                logs = state.debugLogs,
                websocketFrames = state.websocketFrames,
                onClose = { showDebugConsole = false },
                onClear = onClearDebugLogs
            )
        }

        ModalHost(visible = showGroupInfo && selectedChat != null, onDismiss = { showGroupInfo = false }) {
            selectedChat?.let { chat ->
                GroupInfoModal(
                    chat = chat,
                    usersById = state.usersById,
                    onClose = { showGroupInfo = false }
                )
            }
        }

        ModalHost(visible = selectedProfile != null, onDismiss = { profileUserId = null }) {
            selectedProfile?.let { user ->
                ProfileModal(
                    user = user,
                    chats = state.chats,
                    selfUserId = state.session?.userId,
                    onClose = { profileUserId = null },
                    onMessage = {
                        profileUserId = null
                        onStartDirectChat(user.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun SidebarPane(
    state: AppUiState,
    chats: List<ChatSummary>,
    users: List<UserSummary>,
    showSearch: Boolean,
    searchQuery: String,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenDebugConsole: () -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenContacts: () -> Unit,
    onOpenCreate: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStars: () -> Unit,
    onOpenProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(modifier = Modifier.fillMaxSize()) {
            SidebarHeader(
                showSearch = showSearch,
                searchQuery = searchQuery,
                connectionTitle = state.connectionTitle,
                onMenuClick = onMenuClick,
                onSearchToggle = onSearchToggle,
                onSearchQueryChange = onSearchQueryChange,
                onDebugClick = onOpenDebugConsole
            )
            state.error?.takeIf { it.isNotBlank() }?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp)
                ) {
                    Text(it, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (showSearch) {
                SearchResultsList(
                    chats = chats,
                    users = users,
                    onOpenChat = onOpenChat,
                    onOpenContacts = onOpenContacts,
                    onOpenProfile = onOpenProfile
                )
            } else {
                ChatListContent(state = state, chats = chats, onOpenChat = onOpenChat)
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    chats: List<ChatSummary>,
    users: List<UserSummary>,
    onOpenChat: (String) -> Unit,
    onOpenContacts: () -> Unit,
    onOpenProfile: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (chats.isNotEmpty()) {
            item { Text("Chats", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary) }
            items(chats) { chat ->
                ChatRow(chat = chat, selected = false, onClick = { onOpenChat(chat.id) })
            }
        }
        if (users.isNotEmpty()) {
            item { Text("Contacts", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary) }
            items(users) { user ->
                ContactRow(
                    user = user,
                    existingChat = null,
                    onMessage = { onOpenProfile(user.id) },
                    onOpenProfile = { onOpenProfile(user.id) }
                )
            }
        }
    }
}

@Composable
private fun ChatListContent(
    state: AppUiState,
    chats: List<ChatSummary>,
    onOpenChat: (String) -> Unit
) {
    if (state.loading && chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (state.error != null) "Failed to load chats" else "No chats found",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = state.error ?: "Your conversations will appear here.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chats, key = { it.id }) { chat ->
                ChatRow(
                    chat = chat,
                    selected = chat.id == state.selectedChatId,
                    onClick = { onOpenChat(chat.id) }
                )
            }
        }
    }
}

@Composable
private fun SidebarHeader(
    showSearch: Boolean,
    searchQuery: String,
    connectionTitle: String,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onDebugClick: () -> Unit
) {
    val statusTransition = rememberInfiniteTransition(label = "connection_status_fade")
    val statusAlpha by statusTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "connection_status_alpha"
    )
    val titleAlpha = if (connectionTitle == "Noveo") 1f else statusAlpha

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderIconButton(icon = Icons.Outlined.Menu, onClick = onMenuClick)
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = showSearch,
                label = "sidebar_header_swap",
                transitionSpec = {
                    (slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn())
                        .togetherWith(slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut())
                }
            ) { searching ->
                if (searching) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(0.88f).height(46.dp),
                        placeholder = { Text("Search", style = MaterialTheme.typography.bodyMedium) },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        shape = RoundedCornerShape(23.dp)
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AnimatedContent(targetState = connectionTitle, label = "connection_title_swap") { title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.alpha(titleAlpha)
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        HeaderIconButton(icon = Icons.Outlined.Info, onClick = onDebugClick)
        Spacer(Modifier.width(8.dp))
        HeaderIconButton(
            icon = if (showSearch) Icons.Outlined.Close else Icons.Outlined.Search,
            onClick = onSearchToggle
        )
    }
}

@Composable
fun VerifiedIcon(modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier = modifier.size(18.dp)) {
        val path1 = androidx.compose.ui.graphics.vector.PathParser().parsePathString("M12.3 2.9c.1.1.2.1.3.2.7.6 1.3 1.1 2 1.7.3.2.6.4.9.4.9.1 1.7.2 2.6.2.5 0 .6.1.7.7.1.9.1 1.8.2 2.6 0 .4.2.7.4 1 .6.7 1.1 1.3 1.7 2 .3.4.3.5 0 .8-.5.6-1.1 1.3-1.6 1.9-.3.3-.5.7-.5 1.2-.1.8-.2 1.7-.2 2.5 0 .4-.2.5-.6.6-.8 0-1.6.1-2.5.2-.5 0-1 .2-1.4.5-.6.5-1.3 1.1-1.9 1.6-.3.3-.5.3-.8 0-.7-.6-1.4-1.2-2-1.8-.3-.2-.6-.4-.9-.4-.9-.1-1.8-.2-2.7-.2-.4 0-.5-.2-.6-.5 0-.9-.1-1.7-.2-2.6 0-.4-.2-.8-.4-1.1-.6-.6-1.1-1.3-1.6-2-.4-.4-.3-.5 0-1 .6-.6 1.1-1.3 1.7-1.9.3-.3.4-.6.4-1 0-.8.1-1.6.2-2.5 0-.5.1-.6.6-.6.9-.1 1.7-.1 2.6-.2.4 0 .7-.2 1-.4.7-.6 1.4-1.2 2.1-1.7.1-.2.3-.3.5-.2z").toPath()
        val path2 = androidx.compose.ui.graphics.vector.PathParser().parsePathString("M16.4 10.1l-.2.2-5.4 5.4c-.1.1-.2.2-.4 0l-2.6-2.6c-.2-.2-.1-.3 0-.4.2-.2.5-.6.7-.6.3 0 .5.4.7.6l1.1 1.1c.2.2.3.2.5 0l4.3-4.3c.2-.2.4-.3.6 0 .1.2.3.3.4.5.2 0 .3.1.3.1z").toPath()
        
        drawPath(path = path1, color = tint)
        drawPath(path = path2, color = Color.White)
    }
}

@Composable
private fun ChatPane(
    state: AppUiState,
    compact: Boolean,
    selectedChat: ChatSummary?,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    onOpenDebugConsole: () -> Unit,
    onMediaClick: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenGroupInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var draft by rememberSaveable(state.selectedChatId) { mutableStateOf("") }
    var sendPulse by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val sendScale by animateFloatAsState(
        targetValue = if (sendPulse) 1.18f else 1f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "send_button_scale"
    )
    val scope = rememberCoroutineScope()
    val selectedTitle = selectedChat?.title?.ifBlank { "Chat" } ?: "Chat"
    
    val profileUserId = remember(selectedChat, state.session?.userId) {
        resolveProfileUserId(selectedChat, state.session?.userId)
    }
    val profileUser = remember(profileUserId, state.usersById) { state.usersById[profileUserId] }
    val isOnline = remember(profileUserId, state.onlineUserIds) { state.onlineUserIds.contains(profileUserId) }
    
    val onlineCount = remember(selectedChat, state.onlineUserIds) {
        selectedChat?.memberIds?.count { state.onlineUserIds.contains(it) } ?: 0
    }
    
    val typingUsers = state.typingUsers[selectedChat?.id].orEmpty()
    val typingText = remember(typingUsers, state.usersById) {
        if (typingUsers.isEmpty()) null
        else {
            val names = typingUsers.mapNotNull { state.usersById[it]?.username?.split(" ")?.firstOrNull() }
            when {
                names.isEmpty() -> "someone is typing..."
                names.size == 1 -> "${names[0]} is typing..."
                names.size == 2 -> "${names[0]} and ${names[1]} are typing..."
                else -> "${names.size} people are typing..."
            }
        }
    }

    val subtitle = remember(selectedChat, profileUser, isOnline, onlineCount, typingText) {
        if (selectedChat == null) return@remember "conversation"
        if (typingText != null) return@remember typingText
        if (selectedChat.chatType == "private") {
            if (isOnline) "online" else "last seen recently"
        } else {
            val total = selectedChat.memberIds.size
            if (onlineCount > 0) "$total members, $onlineCount online" else "$total members"
        }
    }

    LaunchedEffect(state.selectedChatId, state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.scrollToItem(state.messages.lastIndex)
        }
    }
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            delay(50)
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (compact) {
                HeaderIconButton(icon = Icons.AutoMirrored.Outlined.ArrowBack, onClick = onBackToChats)
                Spacer(Modifier.width(8.dp))
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        if (profileUserId != null) onOpenProfile(profileUserId) else if (selectedChat != null) onOpenGroupInfo()
                    }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileCircle(name = selectedTitle, imageUrl = selectedChat?.avatarUrl, size = 42.dp)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(selectedTitle, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall)
                }
            }
            HeaderIconButton(icon = Icons.Outlined.Info, onClick = onOpenDebugConsole)
            Spacer(Modifier.width(6.dp))
            HeaderIconButton(icon = Icons.Outlined.Call, onClick = {})
        }

        state.error?.takeIf { it.isNotBlank() }?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 12.dp))
            Spacer(Modifier.height(6.dp))
        }

        if (state.loading && state.messages.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp) // Grouping handles spacing
            ) {
                items(state.messages.size, key = { state.messages[it].id }) { index ->
                    val message = state.messages[index]
                    val prevMessage = if (index > 0) state.messages[index - 1] else null
                    val nextMessage = if (index < state.messages.size - 1) state.messages[index + 1] else null
                    
                    val showSenderInfo = remember(message, prevMessage) {
                        prevMessage == null || 
                        prevMessage.senderId != message.senderId || 
                        (message.timestamp - prevMessage.timestamp) > 300 ||
                        prevMessage.senderId == "system"
                    }

                    MessageRow(
                        message = message,
                        ownMessage = message.senderId == state.session?.userId,
                        senderAvatarUrl = state.usersById[message.senderId]?.avatarUrl,
                        showSenderInfo = showSenderInfo,
                        onMediaClick = onMediaClick
                    )
                }
            }
        }

        ComposerBar(
            draft = draft,
            onDraftChange = { 
                draft = it
                onTyping()
            },
            sendScale = sendScale,
            onSendClick = {
                val text = draft.trim()
                if (text.isBlank()) return@ComposerBar
                onSend(text)
                draft = ""
                sendPulse = true
                scope.launch {
                    delay(220)
                    sendPulse = false
                }
            }
        )
    }
}

@Composable
private fun ComposerBar(
    draft: String,
    onDraftChange: (String) -> Unit,
    sendScale: Float,
    onSendClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HeaderIconButton(icon = Icons.Outlined.Menu, onClick = {})
            Spacer(Modifier.width(6.dp))
            HeaderIconButton(icon = Icons.Outlined.Star, onClick = {})
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message...") },
                shape = RoundedCornerShape(22.dp),
                maxLines = 4
            )
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.scale(sendScale)) {
                SendIconButton(enabled = draft.isNotBlank(), onClick = onSendClick)
            }
        }
    }
}

@Composable
private fun DebugConsoleModal(
    logs: List<String>,
    websocketFrames: List<String>,
    onClose: () -> Unit,
    onClear: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()
    var selectedTab by rememberSaveable { mutableStateOf(DebugConsoleTab.EVENTS) }
    val renderedLogs = remember(logs, websocketFrames, selectedTab) {
        val activeLogs = when (selectedTab) {
            DebugConsoleTab.EVENTS -> logs
            DebugConsoleTab.WEBSOCKET -> websocketFrames
        }
        if (activeLogs.isEmpty()) {
            when (selectedTab) {
                DebugConsoleTab.EVENTS -> "No debug logs yet."
                DebugConsoleTab.WEBSOCKET -> "No websocket frames yet."
            }
        } else {
            activeLogs.joinToString(separator = "\n")
        }
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth(0.96f)
            .fillMaxHeight(0.82f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(title = "Debug Console", onClose = onClose)
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                DebugConsoleTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.label) }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { clipboardManager.setText(AnnotatedString(renderedLogs)) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy")
                }
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
            }
            HorizontalDivider()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .verticalScroll(scrollState)
                    .padding(12.dp)
            ) {
                Text(
                    text = renderedLogs,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MessageRow(
    message: ChatMessage,
    ownMessage: Boolean,
    senderAvatarUrl: String?,
    showSenderInfo: Boolean,
    onMediaClick: (String) -> Unit
) {
    val isSystem = message.senderId == "system"
    if (isSystem) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = CircleShape
            ) {
                Text(
                    message.content.text ?: "",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val timeStr = remember(message.timestamp) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(message.timestamp * 1000))
    }

    Column(
        horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth().padding(vertical = if (showSenderInfo) 4.dp else 1.dp)
    ) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            if (!ownMessage) {
                if (showSenderInfo) {
                    ProfileCircle(name = message.senderName, imageUrl = senderAvatarUrl, size = 34.dp)
                } else {
                    Spacer(Modifier.width(34.dp))
                }
                Spacer(Modifier.width(8.dp))
            } else {
                Spacer(Modifier.weight(1f))
            }
            Column(
                horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
                modifier = if (ownMessage) Modifier else Modifier.weight(1f, false)
            ) {
                if (!ownMessage && showSenderInfo) {
                    Text(
                        message.senderName,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Card(
                    shape = RoundedCornerShape(22.dp).copy(
                        bottomEnd = if (ownMessage) CornerSize(4.dp) else CornerSize(22.dp),
                        bottomStart = if (!ownMessage) CornerSize(4.dp) else CornerSize(22.dp)
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (ownMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    val hasVisualMedia = message.content.file?.let { it.isImage() || it.isVideo() } == true
                    Column(modifier = Modifier.padding(if (hasVisualMedia) 6.dp else 12.dp)) {
                        AttachmentPreview(file = message.content.file, onMediaClick = onMediaClick)
                        val caption = message.content.text

                        if (!caption.isNullOrBlank()) {
                            if (message.content.file != null) Spacer(Modifier.height(8.dp))
                            Box(modifier = Modifier.padding(horizontal = if (hasVisualMedia) 6.dp else 0.dp)) {
                                MarkdownText(
                                    text = caption,
                                    color = if (ownMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        Row(
                            modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                timeStr,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (ownMessage) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (ownMessage) {
                                Spacer(Modifier.width(4.dp))
                                if (message.pending) {
                                    Text("...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                                } else {
                                    val seen = message.seenBy.isNotEmpty()
                                    Icon(
                                        imageVector = if (seen) Icons.AutoMirrored.Outlined.ArrowBack else Icons.Outlined.Info, // Placeholder ticks
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (seen) Color(0xFF90EE90) else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (!ownMessage) {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MarkdownText(text: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    val annotated = remember(text) {
        buildAnnotatedString {
            var index = 0
            var bold = false
            while (index < text.length) {
                val markerIndex = text.indexOf("**", startIndex = index)
                if (markerIndex == -1) {
                    withStyle(if (bold) SpanStyle(fontWeight = FontWeight.Bold) else SpanStyle()) {
                        append(text.substring(index))
                    }
                    break
                }
                if (markerIndex > index) {
                    withStyle(if (bold) SpanStyle(fontWeight = FontWeight.Bold) else SpanStyle()) {
                        append(text.substring(index, markerIndex))
                    }
                }
                bold = !bold
                index = markerIndex + 2
            }
        }
    }
    Text(annotated, color = color)
}

@Composable
private fun PendingMessageBubble(text: String) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(220)) + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut(animationSpec = tween(220)) + slideOutVertically(targetOffsetY = { it / 2 })
    ) {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text)
                    Spacer(Modifier.height(4.dp))
                    Text("Sending…", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun AttachmentPreview(file: MessageFileAttachment?, onMediaClick: (String) -> Unit) {
    val uriHandler = LocalUriHandler.current
    val normalizedUrl = remember(file?.url) { file?.url.normalizeNoveoUrl() }
    if (file == null) return

    if (normalizedUrl != null && file.isImage()) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(128.dp).clickable { onMediaClick(normalizedUrl) },
            colors = CardDefaults.cardColors(containerColor = Color(0x1F94A3B8))
        ) {
            AsyncImage(
                model = normalizedUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        return
    }

    if (normalizedUrl != null && file.isVideo()) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(320.dp).aspectRatio(16f / 9f).clickable { onMediaClick(normalizedUrl) },
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // In a real app we'd use a VideoPlayer, for now we show a play icon placeholder exactly like a non-playing preview
                Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                AsyncImage(
                    model = normalizedUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().alpha(0.5f),
                    contentScale = ContentScale.Fit
                )
            }
        }
        return
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !normalizedUrl.isNullOrBlank()) { normalizedUrl?.let(uriHandler::openUri) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info, // Standard file icon
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name.ifBlank { "File" }, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("Click to download", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ContactsModal(
    users: List<UserSummary>,
    chats: List<ChatSummary>,
    selfUserId: String?,
    onClose: () -> Unit,
    onMessage: (String) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth().height(560.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(title = "New Chat", onClose = onClose)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users, key = { it.id }) { user ->
                    ContactRow(
                        user = user,
                        existingChat = findDirectChatForUser(chats, selfUserId, user.id),
                        onMessage = { onMessage(user.id) },
                        onOpenProfile = { onOpenProfile(user.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    user: UserSummary,
    existingChat: ChatSummary?,
    onMessage: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f).clickable(onClick = onOpenProfile),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileCircle(name = user.username, imageUrl = user.avatarUrl, size = 46.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.username, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        user.handle ?: user.bio.ifBlank { if (user.isOnline) "online" else "offline" },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onMessage) {
                Text(if (existingChat != null) "Message" else "Open")
            }
        }
    }
}

@Composable
private fun CreateChannelModal(onClose: () -> Unit) {
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ModalHeader(title = "Create", onClose = onClose)
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Create Channel") }
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Create Group") }
            }
        }
    }
}

@Composable
private fun SettingsModal(
    state: AppUiState,
    section: SettingsSection,
    onSectionChange: (SettingsSection) -> Unit,
    onClose: () -> Unit,
    onLogout: () -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onUpdateProfile: (String, String) -> Unit
) {
    val me = state.session?.userId?.let { state.usersById[it] }
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth().height(620.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(
                title = when (section) {
                    SettingsSection.MENU -> "Settings"
                    SettingsSection.SUBSCRIPTION -> "Subscription"
                    SettingsSection.PROFILE -> "Profile"
                    SettingsSection.ACCOUNT -> "Account"
                    SettingsSection.PREFERENCES -> "Preferences"
                    SettingsSection.CHANGELOG -> "Changelog"
                    SettingsSection.THEME -> "Themes"
                },
                onClose = onClose,
                onBack = if (section != SettingsSection.MENU) ({ onSectionChange(SettingsSection.MENU) }) else null
            )
            Crossfade(targetState = section, label = "settings_section") { current ->
                when (current) {
                    SettingsSection.MENU -> SettingsMenu(onSectionChange)
                    SettingsSection.SUBSCRIPTION -> SettingsSubscriptionSection()
                    SettingsSection.PROFILE -> SettingsProfileSection(me, onUpdateProfile)
                    SettingsSection.ACCOUNT -> SettingsAccountSection(state, onLogout)
                    SettingsSection.PREFERENCES -> SettingsPreferencesSection(currentTheme, onThemeChange)
                    SettingsSection.CHANGELOG -> SettingsChangelogSection()
                    SettingsSection.THEME -> SettingsThemeSection(currentTheme, onThemeChange)
                }
            }
        }
    }
}

@Composable
private fun SettingsMenu(onSectionChange: (SettingsSection) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettingsRow("Subscription", Icons.Outlined.Star) { onSectionChange(SettingsSection.SUBSCRIPTION) }
        SettingsRow("Profile", Icons.Outlined.Info) { onSectionChange(SettingsSection.PROFILE) }
        SettingsRow("Account", Icons.Outlined.Menu) { onSectionChange(SettingsSection.ACCOUNT) }
        SettingsRow("Themes", Icons.Outlined.Search) { onSectionChange(SettingsSection.THEME) }
        SettingsRow("Preferences", Icons.Outlined.Settings) { onSectionChange(SettingsSection.PREFERENCES) }
        SettingsRow("Changelog", Icons.Outlined.Search) { onSectionChange(SettingsSection.CHANGELOG) }
    }
}

@Composable
private fun SettingsSubscriptionSection() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailCard(title = "Premium", body = "Web already includes premium, stars, wallet, gifts, profile skins, and premium themes. Android needs those full flows, not placeholders.")
        DetailCard(title = "Stars", body = "Stars belongs in the same settings/wallet orbit as web. This surface is reachable now, but wallet parity is still incomplete.")
    }
}

@Composable
private fun SettingsProfileSection(me: UserSummary?, onUpdateProfile: (String, String) -> Unit) {
    var username by remember(me) { mutableStateOf(me?.username ?: "") }
    var bio by remember(me) { mutableStateOf(me?.bio ?: "") }

    Column(
        modifier = Modifier.fillMaxSize().padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileCircle(name = me?.username ?: "Me", imageUrl = me?.avatarUrl, size = 90.dp)
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onUpdateProfile(username, bio) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Changes")
        }
        Spacer(Modifier.height(16.dp))
        DetailCard(title = "Profile Skin", body = "Web exposes profile skin and premium visuals here. Android still needs the real editor flow.")
    }
}

@Composable
private fun SettingsAccountSection(state: AppUiState, onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DetailRow("User ID", state.session?.userId ?: "Unknown")
        DetailRow("Session ID", state.session?.sessionId?.ifBlank { "Connected" } ?: "Unavailable")
        DetailRow("Expiry", formatExpiry(state.session))
        DetailCard(title = "Account status", body = "This build now shows your real active session fields from the live account. Logout is still wired and working.")
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
    }
}

@Composable
private fun SettingsPreferencesSection(currentTheme: ThemePreset, onThemeChange: (ThemePreset) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DetailCard(title = "Privacy", body = "Block group invites and related privacy controls belong here like web.")
        DetailCard(title = "Language", body = "English, فارسی, Русский, 中文")
        DetailCard(title = "Emoji Style", body = "Default or iOS")
        DetailCard(title = "Auto-Night Mode", body = "Schedule or system-follow mode selection.")
    }
}

@Composable
private fun SettingsThemeSection(currentTheme: ThemePreset, onThemeChange: (ThemePreset) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val themeSections = listOf(
            ThemeSection(
                title = "Light",
                subtitle = "Bright themes for daytime usage.",
                presets = listOf(ThemePreset.LIGHT, ThemePreset.SKY_LIGHT, ThemePreset.SUNSET_LIGHT, ThemePreset.SNOWY_DAYDREAM)
            ),
            ThemeSection(
                title = "Dark",
                subtitle = "Low-light themes for nighttime usage.",
                presets = listOf(ThemePreset.DARK, ThemePreset.OCEAN_DARK, ThemePreset.PLUM_DARK, ThemePreset.OLED_DARK)
            ),
            ThemeSection(
                title = "Premium",
                subtitle = "Exclusive themes with rich color palettes.",
                presets = listOf(ThemePreset.SUNSET_SHIMMER, ThemePreset.CHERRY_RED, ThemePreset.RAINBOW_RAGEBAIT, ThemePreset.SANOKI_MEOA)
            )
        )

        themeSections.forEach { section ->
            ThemeSectionBlock(
                section = section,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}

@Composable
private fun ThemeSectionBlock(
    section: ThemeSection,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(section.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(section.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        section.presets.forEach { preset ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onThemeChange(preset) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (preset == currentTheme) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(preset.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    if (preset == currentTheme) {
                        Text("Selected", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsChangelogSection() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DetailCard(title = "Client Version", body = CLIENT_VERSION)
        DetailCard(title = "Status", body = "Android now has the web menu structure, larger shell buttons, profile surfaces, attachment previews, and a first sending animation pass.")
    }
}

@Composable
private fun ProfileModal(
    user: UserSummary,
    chats: List<ChatSummary>,
    selfUserId: String?,
    onClose: () -> Unit,
    onMessage: () -> Unit
) {
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                HeaderIconButton(icon = Icons.Outlined.Close, onClick = onClose)
            }
            ProfileCircle(name = user.username, imageUrl = user.avatarUrl, size = 96.dp)
            Spacer(Modifier.height(12.dp))
            Text(user.username, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(user.handle ?: "No handle", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Text(user.bio.ifBlank { "No bio yet." }, textAlign = TextAlign.Center)
            Spacer(Modifier.height(18.dp))
            DetailCard(
                title = "Mutual chat state",
                body = if (findDirectChatForUser(chats, selfUserId, user.id) != null) "This profile already has a private chat you can open." else "No private chat has been found yet in the current Android snapshot."
            )
            Spacer(Modifier.height(14.dp))
            Button(onClick = onMessage, modifier = Modifier.fillMaxWidth()) { Text("Message") }
        }
    }
}

@Composable
private fun GroupInfoModal(chat: ChatSummary, usersById: Map<String, UserSummary>, onClose: () -> Unit) {
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth().height(620.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(title = chat.title.ifBlank { "Chat info" }, onClose = onClose)
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                ProfileCircle(name = chat.title, imageUrl = chat.avatarUrl, size = 96.dp)
                Spacer(Modifier.height(12.dp))
                Text(chat.title.ifBlank { "Chat" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(chat.chatType.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(18.dp))
                DetailRow("Members", chat.memberIds.size.toString())
                Spacer(Modifier.height(10.dp))
                if (chat.memberIds.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(top = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chat.memberIds, key = { it }) { memberId ->
                            val user = usersById[memberId]
                            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    ProfileCircle(name = user?.username ?: memberId, imageUrl = user?.avatarUrl, size = 40.dp)
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(user?.username ?: memberId, fontWeight = FontWeight.SemiBold)
                                        Text(user?.handle ?: user?.bio?.ifBlank { memberId } ?: memberId, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuSheet(
    state: AppUiState,
    onOpenContacts: () -> Unit,
    onOpenCreate: () -> Unit,
    onOpenStars: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val me = state.session?.userId?.let { state.usersById[it] }
    Column(
        modifier = Modifier
            .width(296.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text("Menu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        MenuRow("All Contacts", Icons.Outlined.Info, onOpenContacts)
        MenuRow("New Chat", Icons.Outlined.Menu, onOpenCreate)
        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenStars),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFFFD700))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Stars", fontWeight = FontWeight.SemiBold)
                    Text("${state.wallet?.balanceLabel ?: "0.00"} Stars", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        MenuRow("Settings", Icons.Outlined.Settings, onOpenSettings)
        Spacer(Modifier.weight(1f))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Noveo Messenger", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(CLIENT_VERSION, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MenuRow(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(text, fontWeight = FontWeight.SemiBold)
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SettingsRow(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(text, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ChatRow(chat: ChatSummary, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProfileCircle(name = chat.title, imageUrl = chat.avatarUrl)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(chat.title.ifBlank { "Chat" }, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (chat.isVerified) {
                        Spacer(Modifier.width(4.dp))
                        Text("✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(chat.lastMessagePreview.ifBlank { "No messages yet" }, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
            }
            if (chat.unreadCount > 0) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary).padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(chat.unreadCount.toString(), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun WelcomePane(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text("Noveo Messenger", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Select a chat to keep messaging from the same live account.", textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ModalHost(visible: Boolean, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(180)),
        exit = fadeOut(animationSpec = tween(180))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(18.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ModalHeader(title: String, onClose: () -> Unit, onBack: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            HeaderIconButton(icon = Icons.AutoMirrored.Outlined.ArrowBack, onClick = onBack)
            Spacer(Modifier.width(8.dp))
        }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        HeaderIconButton(icon = Icons.Outlined.Close, onClick = onClose)
    }
    HorizontalDivider()
}

@Composable
private fun DetailCard(title: String, body: String) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(body)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(16.dp))
            Text(value, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End)
        }
    }
}

@Composable
private fun ProfileCircle(name: String, imageUrl: String?, size: Dp = 42.dp) {
    val resolvedImageUrl = remember(imageUrl) { imageUrl.normalizeNoveoUrl() }
    val isDefaultAvatar = remember(resolvedImageUrl) { resolvedImageUrl?.endsWith("default.png") == true }
    
    val fallback = @Composable {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(name.firstOrNull()?.uppercase() ?: "N", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }

    if (!resolvedImageUrl.isNullOrBlank() && !isDefaultAvatar) {
        SubcomposeAsyncImage(
            model = resolvedImageUrl,
            contentDescription = name,
            modifier = Modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
            contentScale = ContentScale.Crop,
            loading = { fallback() },
            error = { fallback() }
        )
    } else {
        fallback()
    }
}

@Composable
private fun HeaderIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier.size(46.dp).clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null)
        }
    }
}

@Composable
private fun SendIconButton(enabled: Boolean, onClick: () -> Unit) {
    val background = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val tint = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        shape = CircleShape,
        color = background,
        modifier = Modifier.size(52.dp).clickable(enabled = enabled, onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(42.dp)) {
                val path = androidx.compose.ui.graphics.vector.PathParser().parsePathString("M11.5003 12H5.41872M5.24634 12.7972L4.24158 15.7986C3.69128 17.4424 3.41613 18.2643 3.61359 18.7704C3.78506 19.21 4.15335 19.5432 4.6078 19.6701C5.13111 19.8161 5.92151 19.4604 7.50231 18.7491L17.6367 14.1886C19.1797 13.4942 19.9512 13.1471 20.1896 12.6648C20.3968 12.2458 20.3968 11.7541 20.1896 11.3351C19.9512 10.8529 19.1797 10.5057 17.6367 9.81135L7.48483 5.24303C5.90879 4.53382 5.12078 4.17921 4.59799 4.32468C4.14397 4.45101 3.77572 4.78336 3.60365 5.22209C3.40551 5.72728 3.67772 6.54741 4.22215 8.18767L5.24829 11.2793C5.34179 11.561 5.38855 11.7019 5.407 11.8459C5.42338 11.9738 5.42321 12.1032 5.40651 12.231C5.38768 12.375 5.34057 12.5157 5.24634 12.7972Z").toPath()
                drawPath(path = path, color = tint, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
            }
        }
    }
}

private fun resolveProfileUserId(chat: ChatSummary?, selfUserId: String?): String? {
    if (chat == null || selfUserId.isNullOrBlank()) return null
    if (chat.chatType != "private") return null
    return chat.memberIds.firstOrNull { it != selfUserId }
}

private fun findDirectChatForUser(chats: List<ChatSummary>, selfUserId: String?, userId: String): ChatSummary? {
    if (selfUserId.isNullOrBlank()) return null
    return chats.firstOrNull { chat ->
        chat.chatType == "private" && chat.memberIds.contains(selfUserId) && chat.memberIds.contains(userId)
    }
}

private fun formatExpiry(session: Session?): String {
    val value = session?.expiresAt ?: 0L
    if (value <= 0L) return "Unknown"
    return runCatching {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(value))
    }.getOrElse { "Unknown" }
}

private fun MessageFileAttachment.isImage(): Boolean {
    val typeValue = type.lowercase(Locale.getDefault())
    val nameValue = name.lowercase(Locale.getDefault())
    val urlValue = url.lowercase(Locale.getDefault())
    return typeValue.startsWith("image/") ||
        nameValue.endsWith(".png") ||
        nameValue.endsWith(".jpg") ||
        nameValue.endsWith(".jpeg") ||
        nameValue.endsWith(".webp") ||
        nameValue.endsWith(".gif") ||
        urlValue.endsWith(".png") ||
        urlValue.endsWith(".jpg") ||
        urlValue.endsWith(".jpeg") ||
        urlValue.endsWith(".webp") ||
        urlValue.endsWith(".gif")
}

private fun MessageFileAttachment.isVideo(): Boolean {
    val typeValue = type.lowercase(Locale.getDefault())
    val nameValue = name.lowercase(Locale.getDefault())
    val urlValue = url.lowercase(Locale.getDefault())
    return typeValue.startsWith("video/") ||
        nameValue.endsWith(".mp4") ||
        nameValue.endsWith(".mov") ||
        nameValue.endsWith(".webm") ||
        urlValue.endsWith(".mp4") ||
        urlValue.endsWith(".mov") ||
        urlValue.endsWith(".webm")
}

private fun String?.normalizeNoveoUrl(): String? {
    val value = this?.trim().orEmpty().replace("\\", "/")
    if (value.isBlank()) return null
    if (value.startsWith("data:")) return value
    if (value.startsWith("//")) return "https:$value"
    if (value.startsWith("http://") || value.startsWith("https://")) return value
    if (value.startsWith("ws://")) return value.replaceFirst("ws://", "http://")
    if (value.startsWith("wss://")) return value.replaceFirst("wss://", "https://")
    
    val normalized = if (value.startsWith("/")) value else "/$value"
    return "$NOVEO_BASE_URL$normalized"
}

@Composable
private fun FullscreenMediaModal(url: String, onDismiss: () -> Unit) {
    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            
            HeaderIconButton(
                icon = Icons.Outlined.Close,
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(16.dp)
            )
        }
    }
}
