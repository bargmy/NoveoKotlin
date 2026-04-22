package ir.hienob.noveo.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val NOVEO_BASE_URL = "https://noveo.ir"
private const val CLIENT_VERSION = "v0.1 mobile"

private enum class SettingsSection {
    MENU, THEMES, SUBSCRIPTION, PROFILE, ACCOUNT, PREFERENCES, CHANGELOG
}

private data class ThemeSection(
    val title: String,
    val subtitle: String,
    val presets: List<ThemePreset>
)

data class PendingBubble(
    val id: String,
    val text: String
)

@Composable
internal fun HomeScreen(
    state: AppUiState,
    onOpenChat: (String) -> Unit,
    onStartDirectChat: (String) -> Unit,
    onSearchPublic: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onLogout: () -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showContactsModal by rememberSaveable { mutableStateOf(false) }
    var showCreateModal by rememberSaveable { mutableStateOf(false) }
    var showSettingsModal by rememberSaveable { mutableStateOf(false) }
    var settingsSection by rememberSaveable { mutableStateOf(SettingsSection.MENU) }
    var profileUserId by rememberSaveable { mutableStateOf<String?>(null) }
    var showGroupInfo by rememberSaveable { mutableStateOf(false) }

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
                users = filteredUsers,
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
            CreateOptionsModal(onClose = { showCreateModal = false })
        }

        ModalHost(visible = showSettingsModal, onDismiss = { showSettingsModal = false }) {
            SettingsModal(
                state = state,
                section = settingsSection,
                onSectionChange = { settingsSection = it },
                onClose = { showSettingsModal = false },
                onLogout = onLogout,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange
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
                onSearchQueryChange = onSearchQueryChange
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
private fun SidebarHeader(
    showSearch: Boolean,
    searchQuery: String,
    connectionTitle: String,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    val statusAlpha = remember { Animatable(1f) }
    LaunchedEffect(connectionTitle) {
        if (connectionTitle == "Noveo") {
            statusAlpha.snapTo(1f)
            return@LaunchedEffect
        }
        while (isActive) {
            statusAlpha.animateTo(0.5f, animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing))
            statusAlpha.animateTo(1f, animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing))
        }
    }
    val titleAlpha = statusAlpha.value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderIconButton(icon = Icons.Outlined.Menu, onClick = onMenuClick)
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            AnimatedContent(targetState = showSearch, label = "sidebar_header_swap") { searching ->
                if (searching) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(0.92f),
                        placeholder = { Text("Search", maxLines = 1) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp)
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
        Spacer(Modifier.width(10.dp))
        HeaderIconButton(
            icon = if (showSearch) Icons.Outlined.Close else Icons.Outlined.Search,
            onClick = onSearchToggle
        )
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
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (chats.isNotEmpty()) {
            item {
                Text(
                    "Chats",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
            items(chats, key = { it.id }) { chat ->
                ChatRow(chat = chat, selected = false, onClick = { onOpenChat(chat.id) })
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Public handles", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text(
                    "All Contacts",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenContacts() }
                )
            }
        }
        if (users.isEmpty()) {
            item {
                Text("No people found.", modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(users, key = { it.id }) { user ->
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
private fun ChatListContent(state: AppUiState, chats: List<ChatSummary>, onOpenChat: (String) -> Unit) {
    if (chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("No chats yet.", textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chats, key = { it.id }) { chat ->
                ChatRow(chat = chat, selected = chat.id == state.selectedChatId, onClick = { onOpenChat(chat.id) })
            }
        }
    }
}

@Composable
private fun ChatPane(
    state: AppUiState,
    compact: Boolean,
    selectedChat: ChatSummary?,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenGroupInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var draft by rememberSaveable(state.selectedChatId) { mutableStateOf("") }
    var sendPulse by rememberSaveable { mutableStateOf(false) }
    val pendingBubbles = remember(state.selectedChatId) { mutableStateListOf<PendingBubble>() }
    val listState = rememberLazyListState()
    val sendScale by animateFloatAsState(
        targetValue = if (sendPulse) 1.18f else 1f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "send_button_scale"
    )
    val scope = rememberCoroutineScope()
    val selectedTitle = selectedChat?.title?.ifBlank { "Chat" } ?: "Chat"
    val subtitle = selectedChat?.memberIds?.size?.let { "$it members" } ?: "conversation"
    val profileUserId = remember(selectedChat, state.session?.userId) {
        resolveProfileUserId(selectedChat, state.session?.userId)
    }

    LaunchedEffect(state.selectedChatId, state.messages.size) {
        val lastIndex = state.messages.lastIndex
        if (lastIndex >= 0) listState.scrollToItem(lastIndex)
    }
    LaunchedEffect(state.messages.size, pendingBubbles.size) {
        val lastIndex = state.messages.size + pendingBubbles.size - 1
        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (compact) {
                HeaderIconButton(icon = Icons.Outlined.ArrowBack, onClick = onBackToChats)
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.messages, key = { it.id }) { message ->
                    MessageRow(
                        message = message,
                        ownMessage = message.senderId == state.session?.userId,
                        senderAvatarUrl = state.usersById[message.senderId]?.avatarUrl
                    )
                }
                items(pendingBubbles, key = { it.id }) { pending ->
                    PendingMessageBubble(text = pending.text)
                }
            }
        }

        ComposerBar(
            draft = draft,
            onDraftChange = { draft = it },
            sendScale = sendScale,
            onSendClick = {
                val text = draft.trim()
                if (text.isBlank()) return@ComposerBar
                val pending = PendingBubble(id = "pending-${System.currentTimeMillis()}", text = text)
                pendingBubbles += pending
                onSend(text)
                draft = ""
                sendPulse = true
                scope.launch {
                    delay(220)
                    sendPulse = false
                    delay(1200)
                    pendingBubbles.remove(pending)
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
private fun MessageRow(message: ChatMessage, ownMessage: Boolean, senderAvatarUrl: String?) {
    Column(
        horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            if (!ownMessage) {
                ProfileCircle(name = message.senderName, imageUrl = senderAvatarUrl, size = 34.dp)
                Spacer(Modifier.width(8.dp))
            } else {
                Spacer(Modifier.weight(1f))
            }
            Column(
                horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
                modifier = if (ownMessage) Modifier else Modifier.weight(1f, false)
            ) {
                if (!ownMessage) {
                    Text(
                        message.senderName.ifBlank { "Unknown" },
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (ownMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        AttachmentPreview(file = message.content.file)
                        val preview = message.content.previewText().removePrefix("[File] ")
                        if (preview.isNotBlank()) {
                            if (message.content.file != null) Spacer(Modifier.height(8.dp))
                            MarkdownText(preview)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkdownText(text: String) {
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
    Text(annotated)
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
private fun AttachmentPreview(file: MessageFileAttachment?) {
    val uriHandler = LocalUriHandler.current
    val normalizedUrl = remember(file?.url) { file?.url.normalizeNoveoUrl() }
    if (file == null) return
    if (normalizedUrl != null && file.isImage()) {
        AsyncImage(
            model = normalizedUrl,
            contentDescription = file.name.ifBlank { "Image attachment" },
            modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop
        )
        return
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !normalizedUrl.isNullOrBlank()) { normalizedUrl?.let(uriHandler::openUri) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (file.isVideo()) Icons.Outlined.PlayArrow else Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name.ifBlank { "Attachment" }, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                val subtitle = file.type.ifBlank { normalizedUrl ?: "File" }
                Text(subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
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
private fun CreateOptionsModal(onClose: () -> Unit) {
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
    onThemeChange: (ThemePreset) -> Unit
) {
    val me = state.session?.userId?.let { state.usersById[it] }
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth().height(620.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(
                title = when (section) {
                    SettingsSection.MENU -> "Settings"
                    SettingsSection.THEMES -> "Themes"
                    SettingsSection.SUBSCRIPTION -> "Subscription"
                    SettingsSection.PROFILE -> "Profile"
                    SettingsSection.ACCOUNT -> "Account"
                    SettingsSection.PREFERENCES -> "Preferences"
                    SettingsSection.CHANGELOG -> "Changelog"
                },
                onClose = onClose,
                onBack = if (section != SettingsSection.MENU) ({ onSectionChange(SettingsSection.MENU) }) else null
            )
            Crossfade(targetState = section, label = "settings_section") { current ->
                when (current) {
                    SettingsSection.MENU -> SettingsMenu(onSectionChange)
                    SettingsSection.THEMES -> SettingsThemesSection(currentTheme, onThemeChange)
                    SettingsSection.SUBSCRIPTION -> SettingsSubscriptionSection()
                    SettingsSection.PROFILE -> SettingsProfileSection(me)
                    SettingsSection.ACCOUNT -> SettingsAccountSection(state, onLogout)
                    SettingsSection.PREFERENCES -> SettingsPreferencesSection()
                    SettingsSection.CHANGELOG -> SettingsChangelogSection()
                }
            }
        }
    }
}

@Composable
private fun SettingsMenu(onSectionChange: (SettingsSection) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettingsRow("Themes", Icons.Outlined.Star) { onSectionChange(SettingsSection.THEMES) }
        SettingsRow("Subscription", Icons.Outlined.Star) { onSectionChange(SettingsSection.SUBSCRIPTION) }
        SettingsRow("Profile", Icons.Outlined.Info) { onSectionChange(SettingsSection.PROFILE) }
        SettingsRow("Account", Icons.Outlined.Menu) { onSectionChange(SettingsSection.ACCOUNT) }
        SettingsRow("Preferences", Icons.Outlined.Settings) { onSectionChange(SettingsSection.PREFERENCES) }
        SettingsRow("Changelog", Icons.Outlined.Search) { onSectionChange(SettingsSection.CHANGELOG) }
    }
}

@Composable
private fun SettingsSubscriptionSection() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailCard(title = "Premium", body = "Web already includes premium, stars, wallet, gifts, profile skins, and premium themes. Android needs those full flows, not placeholders.")
        DetailCard(title = "Stars", body = "Stars belongs in the same settings/wallet orbit as web. This surface is reachable now, but wallet parity is still incomplete.")
    }
}

@Composable
private fun SettingsProfileSection(me: UserSummary?) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileCircle(name = me?.username ?: "Me", imageUrl = me?.avatarUrl, size = 90.dp)
        Spacer(Modifier.height(12.dp))
        Text(me?.username ?: "Me", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(me?.handle ?: "No handle yet", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(14.dp))
        DetailCard(title = "Bio", body = me?.bio?.ifBlank { "No bio yet." } ?: "No bio yet.")
        Spacer(Modifier.height(10.dp))
        DetailCard(title = "Profile Skin", body = "Web exposes profile skin and premium visuals here. Android still needs the real editor flow.")
    }
}

@Composable
private fun SettingsAccountSection(state: AppUiState, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DetailRow("User ID", state.session?.userId ?: "Unknown")
        DetailRow("Session ID", state.session?.sessionId?.ifBlank { "Connected" } ?: "Unavailable")
        DetailRow("Expiry", formatExpiry(state.session))
        DetailCard(title = "Account status", body = "This build now shows your real active session fields from the live account. Logout is still wired and working.")
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
    }
}

@Composable
private fun SettingsPreferencesSection() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DetailCard(title = "Privacy", body = "Block group invites and related privacy controls belong here like web.")
        DetailCard(title = "Language", body = "English, فارسی, Русский, 中文")
        DetailCard(title = "Emoji Style", body = "Default or iOS")
        DetailCard(title = "Theme", body = "Open Settings → Themes for the full theme catalog and presets.")
    }
}

@Composable
private fun SettingsThemesSection(currentTheme: ThemePreset, onThemeChange: (ThemePreset) -> Unit) {
    val themeSections = listOf(
        ThemeSection(
            title = "System",
            subtitle = "Use Light or Dark manually until automatic system-follow mode is added.",
            presets = emptyList()
        ),
        ThemeSection(
            title = "Light",
            subtitle = "Bright themes for daytime usage.",
            presets = listOf(ThemePreset.LIGHT, ThemePreset.SKY_LIGHT)
        ),
        ThemeSection(
            title = "Dark",
            subtitle = "Low-light themes for nighttime usage.",
            presets = listOf(ThemePreset.DARK, ThemePreset.OCEAN_DARK)
        )
    )
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        themeSections.forEach { section ->
            ThemeSectionBlock(section = section, currentTheme = currentTheme, onThemeChange = onThemeChange)
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
    onOpenContacts: () -> Unit,
    onOpenCreate: () -> Unit,
    onOpenStars: () -> Unit,
    onOpenSettings: () -> Unit
) {
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
        MenuRow("Stars", Icons.Outlined.Star, onOpenStars)
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
            HeaderIconButton(icon = Icons.Outlined.ArrowBack, onClick = onBack)
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
    if (!resolvedImageUrl.isNullOrBlank()) {
        AsyncImage(
            model = resolvedImageUrl,
            contentDescription = name,
            modifier = Modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
            contentScale = ContentScale.Crop
        )
    } else {
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
}

@Composable
private fun HeaderIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.size(46.dp).clickable(onClick = onClick)
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
            Canvas(modifier = Modifier.size(22.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.1f, size.height * 0.52f)
                    lineTo(size.width * 0.9f, size.height * 0.1f)
                    lineTo(size.width * 0.62f, size.height * 0.9f)
                    lineTo(size.width * 0.48f, size.height * 0.58f)
                    close()
                }
                drawPath(path = path, color = tint)
                drawLine(
                    color = tint,
                    start = Offset(size.width * 0.18f, size.height * 0.5f),
                    end = Offset(size.width * 0.5f, size.height * 0.58f),
                    strokeWidth = 1.8.dp.toPx()
                )
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
