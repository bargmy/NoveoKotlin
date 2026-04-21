package ir.hienob.noveo.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.hienob.noveo.app.AppUiState
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSummary

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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val compact = maxWidth < 720.dp
        val filteredChats = state.chats.filter {
            if (searchQuery.isBlank()) true
            else it.title.contains(searchQuery, ignoreCase = true) || it.lastMessagePreview.contains(searchQuery, ignoreCase = true)
        }

        if (compact) {
            if (state.selectedChatId == null) {
                SidebarPane(
                    state = state,
                    chats = filteredChats,
                    searchMode = searchMode,
                    searchQuery = searchQuery,
                    onToggleMenu = { showMenu = !showMenu },
                    onToggleSearch = { searchMode = !searchMode; if (!searchMode) searchQuery = "" },
                    onSearchQueryChange = { searchQuery = it },
                    onOpenChat = onOpenChat,
                )
            } else {
                ChatPane(
                    state = state,
                    compact = true,
                    onBackToChats = onBackToChats,
                    onSend = onSend,
                )
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                SidebarPane(
                    state = state,
                    chats = filteredChats,
                    searchMode = searchMode,
                    searchQuery = searchQuery,
                    onToggleMenu = { showMenu = !showMenu },
                    onToggleSearch = { searchMode = !searchMode; if (!searchMode) searchQuery = "" },
                    onSearchQueryChange = { searchQuery = it },
                    onOpenChat = onOpenChat,
                    modifier = Modifier.width(320.dp).fillMaxHeight()
                )
                if (state.selectedChatId == null) {
                    WelcomePane(modifier = Modifier.weight(1f))
                } else {
                    ChatPane(
                        state = state,
                        compact = false,
                        onBackToChats = onBackToChats,
                        onSend = onSend,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (showMenu) {
            MenuOverlay(
                onDismiss = { showMenu = false },
                onLogout = { showMenu = false; onLogout() }
            )
        }
    }
}

@Composable
private fun SidebarPane(
    state: AppUiState,
    chats: List<ChatSummary>,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
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
            if (state.loading && state.chats.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (chats.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No chats yet.", textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(chats) { chat ->
                        ChatRow(
                            chat = chat,
                            selected = chat.id == state.selectedChatId,
                            onClick = { onOpenChat(chat.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatRow(chat: ChatSummary, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarBubble(label = chat.title)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(chat.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, false))
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
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (compact) {
                HeaderButton("←", onBackToChats)
                Spacer(Modifier.width(8.dp))
            }
            AvatarBubble(label = selectedChat?.title ?: "Chat")
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(selectedChat?.title ?: "Chat", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(selectedChat?.handle ?: selectedChat?.chatType ?: "conversation", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(4.dp))
        if (state.error != null) {
            Text(state.error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 12.dp))
            Spacer(Modifier.height(6.dp))
        }
        if (state.loading && state.messages.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.messages.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("No messages here yet.", textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.messages) { message ->
                    MessageRow(message = message, ownMessage = message.senderId == state.session?.userId)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderButton("+") {}
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                label = { Text("Message") },
                maxLines = 4
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { if (draft.isNotBlank()) { onSend(draft); draft = "" } }) {
                Text("Send")
            }
        }
    }
}

@Composable
private fun MessageRow(message: ChatMessage, ownMessage: Boolean) {
    Column(horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        if (!ownMessage) {
            Text(message.senderName, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 4.dp))
        }
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(if (ownMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), MaterialTheme.shapes.large)
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
private fun MenuOverlay(onDismiss: () -> Unit, onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.35f)).clickable(onClick = onDismiss)) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text("Menu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            MenuRow("All Contacts")
            MenuRow("New Chat")
            MenuRow("Settings")
            MenuRow("Profile")
            MenuRow("Account")
            Spacer(Modifier.weight(1f))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
        }
    }
}

@Composable
private fun MenuRow(text: String) {
    Text(text, modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).clickable { }.padding(vertical = 14.dp, horizontal = 8.dp), fontWeight = FontWeight.Medium)
}

@Composable
private fun AvatarBubble(label: String) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Text(label.firstOrNull()?.uppercase() ?: "N", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HeaderButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}
