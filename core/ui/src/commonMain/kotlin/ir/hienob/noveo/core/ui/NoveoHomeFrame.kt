package ir.hienob.noveo.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Shared Android-style home shell used by desktop while the full Android HomeUi is
 * extracted. It renders real state only: no demo chats and no hardcoded messages.
 */
data class NoveoHomeFrameState(
    val currentUserId: String? = null,
    val chats: List<NoveoHomeChat> = emptyList(),
    val selectedChatId: String? = null,
    val messages: List<NoveoHomeMessage> = emptyList(),
    val totalUnreadCount: Int = 0,
    val loading: Boolean = false,
    val error: String? = null,
    val isSendingMessage: Boolean = false,
    val canSendMessage: Boolean = true,
    val activeCallTitle: String? = null
) {
    val selectedChat: NoveoHomeChat?
        get() = chats.firstOrNull { it.id == selectedChatId }
}

data class NoveoHomeChat(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val time: String = "",
    val unreadCount: Int = 0,
    val avatarInitial: String = title.take(1).ifBlank { "N" },
    val isOnline: Boolean = false,
    val isVerified: Boolean = false,
    val canChat: Boolean = true
)

data class NoveoHomeMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val time: String = "",
    val isOutgoing: Boolean = false,
    val pending: Boolean = false,
    val edited: Boolean = false,
    val forwarded: Boolean = false
)

@Composable
fun NoveoHomeFrame(
    state: NoveoHomeFrameState,
    strings: NoveoStrings,
    onOpenChat: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onStartNewChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredChats = remember(state.chats, searchQuery) {
        if (searchQuery.isBlank()) {
            state.chats
        } else {
            state.chats.filter { chat ->
                chat.title.contains(searchQuery, ignoreCase = true) ||
                    chat.subtitle.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val compact = maxWidth < 760.dp
            val selectedChat = state.selectedChat
            if (compact) {
                AnimatedContent(
                    targetState = selectedChat != null,
                    label = "compact_home_chat_switch",
                    transitionSpec = {
                        val transition = if (targetState) {
                            (slideInHorizontally { it } + fadeIn())
                                .togetherWith(slideOutHorizontally { -it / 4 } + fadeOut())
                        } else {
                            (slideInHorizontally { -it / 4 } + fadeIn())
                                .togetherWith(slideOutHorizontally { it } + fadeOut())
                        }
                        transition.using(SizeTransform(clip = false))
                    }
                ) { showingChat ->
                    if (showingChat && selectedChat != null) {
                        ConversationPane(
                            state = state,
                            chat = selectedChat,
                            strings = strings,
                            compact = true,
                            onBackToChats = onBackToChats,
                            onSend = onSend,
                            onTyping = onTyping,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ChatListPane(
                            state = state,
                            strings = strings,
                            chats = filteredChats,
                            searchQuery = searchQuery,
                            onSearchQuery = { searchQuery = it },
                            showMenu = showMenu,
                            onToggleMenu = { showMenu = !showMenu },
                            onOpenChat = onOpenChat,
                            onRefresh = onRefresh,
                            onLogout = onLogout,
                            onOpenSettings = onOpenSettings,
                            onStartNewChat = onStartNewChat,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            } else {
                Row(Modifier.fillMaxSize()) {
                    ChatListPane(
                        state = state,
                        strings = strings,
                        chats = filteredChats,
                        searchQuery = searchQuery,
                        onSearchQuery = { searchQuery = it },
                        showMenu = showMenu,
                        onToggleMenu = { showMenu = !showMenu },
                        onOpenChat = onOpenChat,
                        onRefresh = onRefresh,
                        onLogout = onLogout,
                        onOpenSettings = onOpenSettings,
                        onStartNewChat = onStartNewChat,
                        modifier = Modifier.width(372.dp).fillMaxHeight()
                    )
                    Box(Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)))
                    if (selectedChat != null) {
                        ConversationPane(
                            state = state,
                            chat = selectedChat,
                            strings = strings,
                            compact = false,
                            onBackToChats = onBackToChats,
                            onSend = onSend,
                            onTyping = onTyping,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    } else {
                        EmptyConversation(strings = strings, modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatListPane(
    state: NoveoHomeFrameState,
    strings: NoveoStrings,
    chats: List<NoveoHomeChat>,
    searchQuery: String,
    onSearchQuery: (String) -> Unit,
    showMenu: Boolean,
    onToggleMenu: () -> Unit,
    onOpenChat: (String) -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.background(MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onToggleMenu) { Icon(Icons.Outlined.Menu, contentDescription = strings.menu) }
                Text(strings.brandName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (state.totalUnreadCount > 0) {
                    UnreadBadge(state.totalUnreadCount)
                }
                IconButton(onClick = onRefresh) { Icon(Icons.Outlined.Refresh, contentDescription = strings.refresh) }
                IconButton(onClick = onStartNewChat) { Icon(Icons.Outlined.Add, contentDescription = strings.newChat) }
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQuery,
                placeholder = { Text(strings.searchPlaceholder) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(22.dp),
                singleLine = true
            )
            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            if (state.loading && chats.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
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
        if (showMenu) {
            SideMenu(
                strings = strings,
                onDismiss = onToggleMenu,
                onSettings = onOpenSettings,
                onLogout = onLogout,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
    }
}

@Composable
private fun SideMenu(
    strings: NoveoStrings,
    onDismiss: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(292.dp).fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(strings.brandName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.online, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDismiss) { Icon(Icons.Outlined.Close, contentDescription = strings.close) }
            }
            HorizontalDivider()
            MenuAction(strings.settings, Icons.Outlined.Settings) { onSettings(); onDismiss() }
            MenuAction(strings.allContacts, Icons.Outlined.AccountCircle) { onDismiss() }
            MenuAction(strings.newChat, Icons.Outlined.Add) { onDismiss() }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { onLogout(); onDismiss() }, modifier = Modifier.fillMaxWidth()) { Text(strings.logout) }
        }
    }
}

@Composable
private fun MenuAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ChatRow(chat: NoveoHomeChat, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(background).clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)), contentAlignment = Alignment.Center) {
            Text(chat.avatarInitial.take(1).uppercase(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            if (chat.isOnline) {
                Box(Modifier.align(Alignment.BottomEnd).size(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
            }
        }
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(chat.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                if (chat.time.isNotBlank()) Text(chat.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            Text(chat.subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (chat.unreadCount > 0) UnreadBadge(chat.unreadCount)
    }
}

@Composable
private fun UnreadBadge(count: Int) {
    Box(Modifier.size(22.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
        Text(count.coerceAtMost(99).toString(), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ConversationPane(
    state: NoveoHomeFrameState,
    chat: NoveoHomeChat,
    strings: NoveoStrings,
    compact: Boolean,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (compact) IconButton(onClick = onBackToChats) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null) }
            Box(Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)), contentAlignment = Alignment.Center) {
                Text(chat.avatarInitial.take(1).uppercase(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f)) {
                Text(chat.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (chat.isOnline) strings.online else chat.subtitle.ifBlank { strings.offline }, style = MaterialTheme.typography.labelMedium, color = if (chat.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = {}) { Icon(Icons.Outlined.Call, contentDescription = strings.voiceChat) }
        }
        state.activeCallTitle?.let { callTitle ->
            Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
                Text("${strings.activeCall}: $callTitle", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true,
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.messages.isEmpty()) {
                item { EmptyMessages(strings) }
            }
            items(state.messages.asReversed(), key = { it.id }) { message ->
                MessageBubble(message)
            }
        }
        Composer(
            enabled = chat.canChat && state.canSendMessage,
            sending = state.isSendingMessage,
            strings = strings,
            onTyping = onTyping,
            onSend = onSend
        )
    }
}

@Composable
private fun EmptyConversation(strings: NoveoStrings, modifier: Modifier = Modifier) {
    Box(modifier.background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)) {
            Text(strings.selectChatHint, modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyMessages(strings: NoveoStrings) {
    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)) {
            Text(strings.noMessagesYet, modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MessageBubble(message: NoveoHomeMessage) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.isOutgoing) Arrangement.End else Arrangement.Start) {
        Card(
            modifier = Modifier.width(320.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.isOutgoing) 20.dp else 6.dp,
                bottomEnd = if (message.isOutgoing) 6.dp else 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isOutgoing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                contentColor = if (message.isOutgoing) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (!message.isOutgoing) Text(message.senderName, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                if (message.forwarded) Text("Forwarded", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                Text(message.text, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = buildString {
                        append(message.time)
                        if (message.edited) append(" · edited")
                        if (message.pending) append(" · sending")
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun Composer(
    enabled: Boolean,
    sending: Boolean,
    strings: NoveoStrings,
    onTyping: () -> Unit,
    onSend: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { value -> text = value; onTyping() },
            placeholder = { Text(if (enabled) strings.messagePlaceholder else strings.cannotSendMessage) },
            modifier = Modifier.weight(1f).heightIn(min = 52.dp),
            shape = RoundedCornerShape(26.dp),
            enabled = enabled && !sending,
            maxLines = 5
        )
        IconButton(
            enabled = enabled && !sending && text.isNotBlank(),
            onClick = {
                val value = text.trim()
                text = ""
                onSend(value)
            }
        ) {
            if (sending) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Icon(Icons.Outlined.Send, contentDescription = strings.messagePlaceholder)
        }
    }
}
