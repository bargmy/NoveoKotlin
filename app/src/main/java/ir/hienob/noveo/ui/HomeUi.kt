package ir.hienob.noveo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ir.hienob.noveo.app.AppUiState
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSummary
import ir.hienob.noveo.data.UserSummary

private enum class SidebarPanel {
    CHATS, CONTACTS, NEW_CHAT, SETTINGS, PROFILE, ACCOUNT
}

private const val NOVEO_BASE_URL = "https://noveo.ir"

@Composable
internal fun HomeScreen(
    state: AppUiState,
    onOpenChat: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onLogout: () -> Unit
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    var searchMode by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var panel by rememberSaveable { mutableStateOf(SidebarPanel.CHATS) }

    val filteredChats = remember(state.chats, searchQuery) {
        state.chats.filter {
            if (searchQuery.isBlank()) true
            else it.title.contains(searchQuery, true) || it.lastMessagePreview.contains(searchQuery, true)
        }
    }
    val sortedUsers = remember(state.usersById) {
        state.usersById.values.sortedBy { it.username.lowercase() }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val compact = maxWidth < 720.dp

        if (compact) {
            if (state.selectedChatId == null) {
                SidebarPane(
                    state = state,
                    chats = filteredChats,
                    users = sortedUsers,
                    panel = panel,
                    searchMode = searchMode,
                    searchQuery = searchQuery,
                    onToggleMenu = { showMenu = !showMenu },
                    onToggleSearch = {
                        searchMode = !searchMode
                        if (!searchMode) searchQuery = ""
                    },
                    onSearchQueryChange = { searchQuery = it },
                    onOpenChat = onOpenChat,
                )
            } else {
                ChatPane(state = state, compact = true, onBackToChats = onBackToChats, onSend = onSend)
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                SidebarPane(
                    state = state,
                    chats = filteredChats,
                    users = sortedUsers,
                    panel = panel,
                    searchMode = searchMode,
                    searchQuery = searchQuery,
                    onToggleMenu = { showMenu = !showMenu },
                    onToggleSearch = {
                        searchMode = !searchMode
                        if (!searchMode) searchQuery = ""
                    },
                    onSearchQueryChange = { searchQuery = it },
                    onOpenChat = onOpenChat,
                    modifier = Modifier.width(340.dp).fillMaxHeight()
                )
                if (state.selectedChatId == null) {
                    when (panel) {
                        SidebarPanel.CHATS -> WelcomePane(modifier = Modifier.weight(1f))
                        SidebarPanel.CONTACTS -> PeoplePanel(title = "Contacts", users = sortedUsers, modifier = Modifier.weight(1f))
                        SidebarPanel.NEW_CHAT -> PeoplePanel(title = "Start a chat", users = sortedUsers, modifier = Modifier.weight(1f))
                        SidebarPanel.SETTINGS -> InfoPanel(title = "Settings", body = "Notifications, appearance, privacy, devices, and language belong here.", modifier = Modifier.weight(1f))
                        SidebarPanel.PROFILE -> ProfilePanel(state = state, modifier = Modifier.weight(1f))
                        SidebarPanel.ACCOUNT -> InfoPanel(title = "Account", body = "Security, sessions, password, and account management belong here.", modifier = Modifier.weight(1f))
                    }
                } else {
                    ChatPane(state = state, compact = false, onBackToChats = onBackToChats, onSend = onSend, modifier = Modifier.weight(1f))
                }
            }
        }

        if (showMenu) {
            MenuOverlay(
                onDismiss = { showMenu = false },
                onSelect = {
                    panel = it
                    showMenu = false
                },
                onLogout = {
                    showMenu = false
                    onLogout()
                }
            )
        }
    }
}

@Composable
private fun SidebarPane(
    state: AppUiState,
    chats: List<ChatSummary>,
    users: List<UserSummary>,
    panel: SidebarPanel,
    searchMode: Boolean,
    searchQuery: String,
    onToggleMenu: () -> Unit,
    onToggleSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderButton("≡", onToggleMenu)
                Spacer(Modifier.weight(1f))
                Text("Noveo", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                HeaderButton(if (searchMode) "✕" else "⌕", onToggleSearch)
            }
            if (searchMode) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    label = { Text("Search") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
            }
            when (panel) {
                SidebarPanel.CHATS -> ChatListContent(state = state, chats = chats, onOpenChat = onOpenChat)
                SidebarPanel.CONTACTS -> SidebarPeopleList(title = "Contacts", users = users)
                SidebarPanel.NEW_CHAT -> SidebarPeopleList(title = "Start a chat", users = users)
                SidebarPanel.SETTINGS -> SidebarInfo(title = "Settings")
                SidebarPanel.PROFILE -> SidebarInfo(title = "Profile")
                SidebarPanel.ACCOUNT -> SidebarInfo(title = "Account")
            }
        }
    }
}

@Composable
private fun ChatListContent(state: AppUiState, chats: List<ChatSummary>, onOpenChat: (String) -> Unit) {
    if (state.loading && state.chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else if (chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("No chats yet.", textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(chats, key = { it.id }) { chat ->
                ChatRow(chat = chat, selected = chat.id == state.selectedChatId, onClick = { onOpenChat(chat.id) })
            }
        }
    }
}

@Composable
private fun SidebarPeopleList(title: String, users: List<UserSummary>) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (title.isNotBlank()) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }
        if (users.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No users found.") }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(users, key = { it.id }) { user ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).background(MaterialTheme.colorScheme.surface).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileCircle(name = user.username, imageUrl = user.avatarUrl)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.username.ifBlank { "Unknown" }, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(user.handle ?: user.bio.ifBlank { if (user.isOnline) "online" else "offline" }, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarInfo(title: String) {
    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.TopStart) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ChatRow(chat: ChatSummary, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).background(if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface).clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileCircle(name = chat.title, imageUrl = chat.avatarUrl)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(chat.title.ifBlank { "Chat" }, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, false))
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
            Box(modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary).padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
                Text(chat.unreadCount.toString(), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun ChatPane(
    state: AppUiState,
    compact: Boolean,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var draft by rememberSaveable(state.selectedChatId) { mutableStateOf("") }
    val selectedChat = state.chats.firstOrNull { it.id == state.selectedChatId }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            if (compact) {
                HeaderButton("←", onBackToChats)
                Spacer(Modifier.width(8.dp))
            }
            ProfileCircle(name = selectedChat?.title ?: "Chat", imageUrl = selectedChat?.avatarUrl)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(selectedChat?.title?.ifBlank { "Chat" } ?: "Chat", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(selectedChat?.handle ?: selectedChat?.chatType ?: "conversation", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(4.dp))
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 12.dp))
            Spacer(Modifier.height(6.dp))
        }
        if (state.loading && state.messages.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (state.messages.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { Text("No messages here yet.", textAlign = TextAlign.Center) }
        } else {
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.messages, key = { it.id }) { message ->
                    MessageRow(message = message, ownMessage = message.senderId == state.session?.userId)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            HeaderButton("+") {}
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = draft, onValueChange = { draft = it }, modifier = Modifier.weight(1f), label = { Text("Message") }, maxLines = 4)
            Spacer(Modifier.width(8.dp))
            SendIconButton(enabled = draft.isNotBlank()) {
                onSend(draft)
                draft = ""
            }
        }
    }
}

@Composable
private fun MessageRow(message: ChatMessage, ownMessage: Boolean) {
    Column(horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            if (!ownMessage) {
                ProfileCircle(name = message.senderName, imageUrl = null, size = 34.dp)
                Spacer(Modifier.width(8.dp))
            } else {
                Spacer(Modifier.weight(1f))
            }
            Column(horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start, modifier = if (ownMessage) Modifier else Modifier.weight(1f, false)) {
                if (!ownMessage) {
                    Text(message.senderName.ifBlank { "Unknown" }, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 4.dp))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (ownMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(message.content.previewText().ifBlank { "Unsupported message" })
                        if (message.content.file != null && message.content.file.url.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(message.content.file.name.ifBlank { message.content.file.url }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
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
            Text("Select a chat to start a conversation.", textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun PeoplePanel(title: String, users: List<UserSummary>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(20.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        SidebarPeopleList(title = "", users = users)
    }
}

@Composable
private fun ProfilePanel(state: AppUiState, modifier: Modifier = Modifier) {
    val me = state.session?.userId?.let { state.usersById[it] }
    Column(modifier = modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileCircle(name = me?.username ?: "Me", imageUrl = me?.avatarUrl, size = 84.dp)
        Spacer(Modifier.height(12.dp))
        Text(me?.username ?: "Me", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(me?.handle ?: "@noveo", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(10.dp))
        Text(me?.bio?.ifBlank { "No bio yet." } ?: "No bio yet.", textAlign = TextAlign.Center)
    }
}

@Composable
private fun InfoPanel(title: String, body: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(20.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text(body)
    }
}

@Composable
private fun MenuOverlay(onDismiss: () -> Unit, onSelect: (SidebarPanel) -> Unit, onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.35f)).clickable(onClick = onDismiss)) {
        Column(modifier = Modifier.width(280.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
            Text("Menu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            MenuRow("Chats") { onSelect(SidebarPanel.CHATS) }
            MenuRow("All Contacts") { onSelect(SidebarPanel.CONTACTS) }
            MenuRow("New Chat") { onSelect(SidebarPanel.NEW_CHAT) }
            MenuRow("Settings") { onSelect(SidebarPanel.SETTINGS) }
            MenuRow("Profile") { onSelect(SidebarPanel.PROFILE) }
            MenuRow("Account") { onSelect(SidebarPanel.ACCOUNT) }
            Spacer(Modifier.weight(1f))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
        }
    }
}

@Composable
private fun MenuRow(text: String, onClick: () -> Unit) {
    Text(text, modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).clickable(onClick = onClick).padding(vertical = 14.dp, horizontal = 8.dp), fontWeight = FontWeight.Medium)
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
        Box(modifier = Modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
            Text(name.firstOrNull()?.uppercase() ?: "N", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HeaderButton(text: String, onClick: () -> Unit) {
    Box(modifier = Modifier.clip(CircleShape).clickable(onClick = onClick).padding(8.dp), contentAlignment = Alignment.Center) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SendIconButton(enabled: Boolean, onClick: () -> Unit) {
    val background = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val tint = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            val path = Path().apply {
                moveTo(size.width * 0.1f, size.height * 0.52f)
                lineTo(size.width * 0.9f, size.height * 0.1f)
                lineTo(size.width * 0.62f, size.height * 0.9f)
                lineTo(size.width * 0.48f, size.height * 0.58f)
                close()
            }
            drawPath(path = path, color = tint)
            drawLine(color = tint, start = Offset(size.width * 0.18f, size.height * 0.5f), end = Offset(size.width * 0.5f, size.height * 0.58f), strokeWidth = 1.8.dp.toPx())
        }
    }
}

private fun String?.normalizeNoveoUrl(): String? {
    val value = this?.trim().orEmpty()
    if (value.isBlank()) return null
    if (value.startsWith("http://") || value.startsWith("https://")) return value
    return if (value.startsWith("/")) "$NOVEO_BASE_URL$value" else "$NOVEO_BASE_URL/$value"
}
