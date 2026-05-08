package ir.hienob.noveo.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Android-home-compatible state consumed by the shared desktop home surface.
 * Keep this state factual: desktop fills it from the server, not from mock/demo data.
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

private data class TelegramHomeColors(
    val isDark: Boolean,
    val composerBlue: Color,
    val composerPanel: Color,
    val composerField: Color,
    val composerIcon: Color,
    val composerHint: Color,
    val composerDivider: Color,
    val composerText: Color,
    val composerCursor: Color,
    val chatSurface: Color,
    val headerTitle: Color,
    val headerSubtitle: Color,
    val headerIcon: Color,
    val incomingBubble: Color,
    val incomingBubbleSelected: Color,
    val outgoingBubble: Color,
    val outgoingBubbleSelected: Color,
    val incomingText: Color,
    val incomingLink: Color,
    val incomingTime: Color,
    val outgoingText: Color,
    val outgoingTime: Color,
    val replyIncoming: Color,
    val replyOutgoing: Color
)

@Composable
private fun telegramHomeColors(): TelegramHomeColors {
    val scheme = MaterialTheme.colorScheme
    val isDark = scheme.background.red * 0.299f + scheme.background.green * 0.587f + scheme.background.blue * 0.114f < 0.5f
    return if (!isDark) {
        TelegramHomeColors(
            isDark = false,
            composerBlue = scheme.primary,
            composerPanel = scheme.surfaceVariant.copy(alpha = 0.5f),
            composerField = scheme.surface,
            composerIcon = scheme.onSurfaceVariant.copy(alpha = 0.7f),
            composerHint = scheme.onSurfaceVariant.copy(alpha = 0.5f),
            composerDivider = scheme.outlineVariant.copy(alpha = 0.3f),
            composerText = scheme.onSurface,
            composerCursor = scheme.primary,
            chatSurface = scheme.background,
            headerTitle = scheme.onSurface,
            headerSubtitle = scheme.onSurfaceVariant.copy(alpha = 0.7f),
            headerIcon = scheme.onSurfaceVariant,
            incomingBubble = scheme.surface,
            incomingBubbleSelected = scheme.surfaceVariant,
            outgoingBubble = scheme.primaryContainer.copy(alpha = 0.8f),
            outgoingBubbleSelected = scheme.primaryContainer,
            incomingText = scheme.onSurface,
            incomingLink = scheme.primary,
            incomingTime = scheme.onSurfaceVariant.copy(alpha = 0.6f),
            outgoingText = scheme.onPrimaryContainer,
            outgoingTime = scheme.onPrimaryContainer.copy(alpha = 0.7f),
            replyIncoming = scheme.secondaryContainer.copy(alpha = 0.5f),
            replyOutgoing = scheme.onPrimaryContainer.copy(alpha = 0.15f)
        )
    } else {
        TelegramHomeColors(
            isDark = true,
            composerBlue = scheme.primary,
            composerPanel = scheme.surfaceVariant.copy(alpha = 0.3f),
            composerField = scheme.surface,
            composerIcon = scheme.onSurfaceVariant.copy(alpha = 0.7f),
            composerHint = scheme.onSurfaceVariant.copy(alpha = 0.5f),
            composerDivider = scheme.outlineVariant.copy(alpha = 0.3f),
            composerText = scheme.onSurface,
            composerCursor = scheme.primary,
            chatSurface = scheme.background,
            headerTitle = scheme.onSurface,
            headerSubtitle = scheme.onSurfaceVariant.copy(alpha = 0.7f),
            headerIcon = scheme.onSurfaceVariant,
            incomingBubble = scheme.surface,
            incomingBubbleSelected = scheme.surfaceVariant,
            outgoingBubble = scheme.primaryContainer.copy(alpha = 0.7f),
            outgoingBubbleSelected = scheme.primaryContainer,
            incomingText = scheme.onSurface,
            incomingLink = scheme.primary,
            incomingTime = scheme.onSurfaceVariant.copy(alpha = 0.6f),
            outgoingText = scheme.onPrimaryContainer,
            outgoingTime = scheme.onPrimaryContainer.copy(alpha = 0.7f),
            replyIncoming = scheme.secondaryContainer.copy(alpha = 0.4f),
            replyOutgoing = scheme.onPrimaryContainer.copy(alpha = 0.15f)
        )
    }
}

private class TelegramBubbleShape(
    private val isOutgoing: Boolean,
    private val hasTail: Boolean,
    private val cornerRadius: Float = 48f
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            val r = cornerRadius
            val tr = 6f * density.density
            if (isOutgoing) {
                moveTo(r, 0f)
                lineTo(w - r, 0f)
                quadraticTo(w, 0f, w, r)
                if (hasTail) {
                    lineTo(w, h - r)
                    lineTo(w, h - 10f * density.density)
                    cubicTo(w, h, w + tr, h, w + tr, h)
                    lineTo(w - r, h)
                } else {
                    lineTo(w, h - r)
                    quadraticTo(w, h, w - r, h)
                }
                lineTo(r, h)
                quadraticTo(0f, h, 0f, h - r)
                lineTo(0f, r)
                quadraticTo(0f, 0f, r, 0f)
            } else {
                moveTo(r, 0f)
                lineTo(w - r, 0f)
                quadraticTo(w, 0f, w, r)
                lineTo(w, h - r)
                quadraticTo(w, h, w - r, h)
                lineTo(r, h)
                if (hasTail) {
                    lineTo(10f * density.density, h)
                    cubicTo(0f, h, -tr, h, -tr, h)
                    lineTo(0f, h - 10f * density.density)
                } else {
                    quadraticTo(0f, h, 0f, h - r)
                }
                lineTo(0f, r)
                quadraticTo(0f, 0f, r, 0f)
            }
            close()
        }
        return Outline.Generic(path)
    }
}

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
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredChats = remember(state.chats, searchQuery) {
        if (searchQuery.isBlank()) state.chats
        else state.chats.filter { it.title.contains(searchQuery, true) || it.subtitle.contains(searchQuery, true) }
    }
    val tgColors = telegramHomeColors()

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val compact = maxWidth < 760.dp
            val selectedChat = state.selectedChat
            if (compact) {
                AnimatedContent(
                    targetState = selectedChat != null,
                    label = "android_compact_chat_switch",
                    transitionSpec = {
                        val transition = if (targetState) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it / 4 } + fadeOut())
                        } else {
                            (slideInHorizontally { -it / 4 } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                        }
                        transition.using(SizeTransform(clip = false))
                    }
                ) { showingChat ->
                    if (showingChat && selectedChat != null) {
                        AndroidStyleConversationPane(
                            state = state,
                            chat = selectedChat,
                            strings = strings,
                            compact = true,
                            tgColors = tgColors,
                            onBackToChats = onBackToChats,
                            onSend = onSend,
                            onTyping = onTyping,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AndroidStyleSidebarPane(
                            state = state,
                            strings = strings,
                            chats = filteredChats,
                            showSearch = showSearch,
                            searchQuery = searchQuery,
                            showMenu = showMenu,
                            onSearchQuery = { searchQuery = it },
                            onMenuClick = { showMenu = !showMenu },
                            onSearchToggle = { showSearch = !showSearch; if (showSearch) searchQuery = "" },
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
                    AndroidStyleSidebarPane(
                        state = state,
                        strings = strings,
                        chats = filteredChats,
                        showSearch = showSearch,
                        searchQuery = searchQuery,
                        showMenu = showMenu,
                        onSearchQuery = { searchQuery = it },
                        onMenuClick = { showMenu = !showMenu },
                        onSearchToggle = { showSearch = !showSearch; if (showSearch) searchQuery = "" },
                        onOpenChat = onOpenChat,
                        onRefresh = onRefresh,
                        onLogout = onLogout,
                        onOpenSettings = onOpenSettings,
                        onStartNewChat = onStartNewChat,
                        modifier = Modifier.width(372.dp).fillMaxHeight()
                    )
                    Box(Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)))
                    if (selectedChat != null) {
                        AndroidStyleConversationPane(
                            state = state,
                            chat = selectedChat,
                            strings = strings,
                            compact = false,
                            tgColors = tgColors,
                            onBackToChats = onBackToChats,
                            onSend = onSend,
                            onTyping = onTyping,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    } else {
                        AndroidWelcomePane(strings = strings, modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                }
            }
        }
    }
}

@Composable
private fun AndroidStyleSidebarPane(
    state: NoveoHomeFrameState,
    strings: NoveoStrings,
    chats: List<NoveoHomeChat>,
    showSearch: Boolean,
    searchQuery: String,
    showMenu: Boolean,
    onSearchQuery: (String) -> Unit,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onOpenChat: (String) -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                AndroidStyleSidebarHeader(
                    strings = strings,
                    showSearch = showSearch,
                    searchQuery = searchQuery,
                    connectionTitle = strings.brandName,
                    onMenuClick = onMenuClick,
                    onSearchToggle = onSearchToggle,
                    onSearchQueryChange = onSearchQuery
                )
                state.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                if (showSearch) {
                    AndroidStyleSearchResults(strings, chats, onOpenChat)
                } else {
                    AndroidStyleChatListContent(state, strings, chats, onOpenChat)
                }
            }
            AnimatedVisibility(
                visible = showMenu,
                enter = fadeIn(tween(160)) + slideInHorizontally(tween(220, easing = FastOutSlowInEasing)) { -it },
                exit = fadeOut(tween(160)) + slideOutHorizontally(tween(200, easing = FastOutSlowInEasing)) { -it }
            ) {
                Box(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.30f)).clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onMenuClick
                        )
                    )
                    AndroidStyleSideMenu(
                        strings = strings,
                        onDismiss = onMenuClick,
                        onSettings = onOpenSettings,
                        onStartNewChat = onStartNewChat,
                        onRefresh = onRefresh,
                        onLogout = onLogout,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
            }
        }
    }
}

@Composable
private fun AndroidStyleSidebarHeader(
    strings: NoveoStrings,
    showSearch: Boolean,
    searchQuery: String,
    connectionTitle: String,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderIconButton(icon = Icons.Outlined.Menu, onClick = onMenuClick)
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = showSearch,
                label = "sidebar_header_swap",
                contentAlignment = Alignment.Center,
                transitionSpec = {
                    (slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn()).togetherWith(slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut())
                }
            ) { searching ->
                if (searching) {
                    SearchField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = strings.searchPlaceholder,
                        modifier = Modifier.fillMaxWidth(0.88f).height(46.dp)
                    )
                } else {
                    AnimatedContent(
                        targetState = connectionTitle,
                        label = "title_animation",
                        transitionSpec = {
                            (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
                        }
                    ) { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.fillMaxWidth().height(28.dp).alpha(1f),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        HeaderIconButton(icon = if (showSearch) Icons.Outlined.Close else Icons.Outlined.Search, onClick = onSearchToggle)
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(23.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)) {
        Row(Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (value.isBlank()) Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.68f), fontSize = 14.sp)
                    inner()
                }
            )
        }
    }
}

@Composable
private fun AndroidStyleChatListContent(state: NoveoHomeFrameState, strings: NoveoStrings, chats: List<NoveoHomeChat>, onOpenChat: (String) -> Unit) {
    if (state.loading && chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
        }
    } else if (chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(strings.noMessagesYet, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = state.error ?: strings.selectChatHint,
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
                AndroidStyleChatRow(chat = chat, strings = strings, selected = chat.id == state.selectedChatId, onClick = { onOpenChat(chat.id) })
            }
        }
    }
}

@Composable
private fun AndroidStyleSearchResults(strings: NoveoStrings, chats: List<NoveoHomeChat>, onOpenChat: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (chats.isNotEmpty()) {
            item { Text(strings.newChat, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary) }
            items(chats, key = { it.id }) { chat ->
                AndroidStyleChatRow(chat = chat, strings = strings, selected = false, onClick = { onOpenChat(chat.id) })
            }
        }
    }
}

@Composable
private fun AndroidStyleChatRow(chat: NoveoHomeChat, strings: NoveoStrings, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProfileCircle(name = chat.title, isSavedMessages = chat.title == strings.savedMessages || chat.title == "Saved Messages")
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(chat.title.ifBlank { strings.messages }, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (chat.isVerified) {
                        Spacer(Modifier.width(4.dp))
                        VerifiedIcon()
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    chat.subtitle.ifBlank { strings.noMessagesYet },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (chat.time.isNotBlank()) {
                Spacer(Modifier.width(8.dp))
                Text(chat.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (chat.unreadCount > 0) {
                Spacer(Modifier.width(8.dp))
                UnreadBadge(chat.unreadCount)
            }
        }
    }
}

@Composable
private fun AndroidStyleSideMenu(
    strings: NoveoStrings,
    onDismiss: () -> Unit,
    onSettings: () -> Unit,
    onStartNewChat: () -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.width(296.dp).fillMaxHeight(), color = MaterialTheme.colorScheme.surface, shadowElevation = 12.dp) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileCircle(name = strings.brandName, size = 56.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(strings.brandName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.online, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                HeaderIconButton(icon = Icons.Outlined.Close, onClick = onDismiss)
            }
            HorizontalDivider()
            DrawerAction(strings.settings, Icons.Outlined.Settings) { onSettings(); onDismiss() }
            DrawerAction(strings.allContacts, Icons.Outlined.AccountCircle) { onDismiss() }
            DrawerAction(strings.newChat, Icons.Outlined.Add) { onStartNewChat(); onDismiss() }
            DrawerAction(strings.refresh, Icons.Outlined.Refresh) { onRefresh(); onDismiss() }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { onLogout(); onDismiss() }, modifier = Modifier.fillMaxWidth()) { Text(strings.logout) }
        }
    }
}

@Composable
private fun DrawerAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
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
private fun AndroidStyleConversationPane(
    state: NoveoHomeFrameState,
    chat: NoveoHomeChat,
    strings: NoveoStrings,
    compact: Boolean,
    tgColors: TelegramHomeColors,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    modifier: Modifier = Modifier
) {
    var draft by rememberSaveable(chat.id) { mutableStateOf("") }
    val listState = rememberLazyListState()
    var contextMessage by remember { mutableStateOf<NoveoHomeMessage?>(null) }
    var showMoreMenu by remember { mutableStateOf(false) }

    LaunchedEffect(chat.id, state.messages.size) {
        if (state.messages.isNotEmpty()) listState.scrollToItem(state.messages.lastIndex)
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(tgColors.chatSurface)) {
        val maxBubbleWidth = maxWidth * 0.78f
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 8.dp, top = 64.dp, end = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (state.messages.isEmpty()) item { EmptyMessagesSurface(strings, tgColors) }
            itemsIndexed(state.messages, key = { _, message -> message.id }) { index, message ->
                val prev = state.messages.getOrNull(index - 1)
                val next = state.messages.getOrNull(index + 1)
                val showSenderInfo = !message.isOutgoing && (prev == null || prev.senderId != message.senderId)
                val hasTail = next == null || next.senderId != message.senderId
                AndroidStyleMessageRow(
                    message = message,
                    strings = strings,
                    showSenderInfo = showSenderInfo,
                    hasTail = hasTail,
                    maxBubbleWidth = maxBubbleWidth,
                    tgColors = tgColors,
                    onOpenMenu = { contextMessage = message }
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp).align(Alignment.BottomCenter).offset(y = 42.dp).background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, tgColors.chatSurface),
                    startY = 0f,
                    endY = 110f
                )
            )
        )

        Surface(
            modifier = Modifier.fillMaxWidth().height(56.dp).align(Alignment.TopCenter),
            color = tgColors.incomingBubble,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp
        ) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                HeaderIconButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    onClick = onBackToChats,
                    tint = tgColors.headerIcon,
                    modifier = Modifier.padding(start = 4.dp).alpha(if (compact) 1f else 0f)
                )
                Row(modifier = Modifier.weight(1f).padding(vertical = 4.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    ProfileCircle(name = chat.title, isSavedMessages = chat.title == strings.savedMessages || chat.title == "Saved Messages", size = 40.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                chat.title,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = tgColors.headerTitle,
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (chat.isVerified) {
                                Spacer(Modifier.width(4.dp))
                                VerifiedIcon(modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            if (chat.isOnline) strings.online else chat.subtitle.ifBlank { strings.offline },
                            color = tgColors.headerSubtitle,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                HeaderIconButton(icon = Icons.Outlined.Call, onClick = {}, tint = tgColors.headerIcon)
                Box {
                    HeaderIconButton(icon = Icons.Outlined.Search, onClick = { showMoreMenu = true }, tint = tgColors.headerIcon, modifier = Modifier.padding(end = 4.dp))
                    DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                        DropdownMenuItem(text = { Text(strings.refresh) }, onClick = { showMoreMenu = false })
                    }
                }
            }
        }

        state.activeCallTitle?.let { callTitle ->
            Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).offset(y = 56.dp)) {
                Text("${strings.activeCall}: $callTitle", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 6.dp).padding(bottom = 4.dp)) {
            AndroidStyleComposer(
                draft = draft,
                onDraftChange = { draft = it; onTyping() },
                placeholder = if (chat.canChat && state.canSendMessage) strings.messagePlaceholder else strings.cannotSendMessage,
                enabled = chat.canChat && state.canSendMessage,
                sending = state.isSendingMessage,
                tgColors = tgColors,
                onSend = {
                    val text = draft.trim()
                    if (text.isNotBlank()) {
                        draft = ""
                        onSend(text)
                    }
                }
            )
        }

        contextMessage?.let { message ->
            MessageContextMenuOverlay(
                message = message,
                strings = strings,
                tgColors = tgColors,
                onDismiss = { contextMessage = null }
            )
        }
    }
}

@Composable
private fun AndroidWelcomePane(strings: NoveoStrings, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(strings.brandName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(strings.selectChatHint, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun EmptyMessagesSurface(strings: NoveoStrings, tgColors: TelegramHomeColors) {
    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(20.dp), color = tgColors.incomingBubble.copy(alpha = 0.88f)) {
            Text(strings.noMessagesYet, modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp), color = tgColors.headerSubtitle)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AndroidStyleMessageRow(
    message: NoveoHomeMessage,
    strings: NoveoStrings,
    showSenderInfo: Boolean,
    hasTail: Boolean,
    maxBubbleWidth: androidx.compose.ui.unit.Dp,
    tgColors: TelegramHomeColors,
    onOpenMenu: () -> Unit
) {
    val ownMessage = message.isOutgoing
    Row(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = {}, onLongClick = onOpenMenu),
        horizontalArrangement = if (ownMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!ownMessage && hasTail) {
            ProfileCircle(name = message.senderName, size = 28.dp)
            Spacer(Modifier.width(4.dp))
        } else if (!ownMessage) {
            Spacer(Modifier.width(32.dp))
        }
        Surface(
            modifier = Modifier.widthIn(max = maxBubbleWidth),
            shape = TelegramBubbleShape(isOutgoing = ownMessage, hasTail = hasTail),
            color = if (ownMessage) tgColors.outgoingBubble else tgColors.incomingBubble,
            shadowElevation = 0.dp
        ) {
            Column(Modifier.padding(start = if (ownMessage) 12.dp else 14.dp, end = 12.dp, top = 7.dp, bottom = 6.dp)) {
                if (showSenderInfo) {
                    Text(message.senderName, color = tgColors.incomingLink, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 16.sp)
                    Spacer(Modifier.height(2.dp))
                }
                if (message.forwarded) {
                    Text(strings.forwarded, color = if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(2.dp))
                }
                Text(message.text, color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText, fontSize = 15.sp, lineHeight = 20.sp)
                Row(modifier = Modifier.align(Alignment.End).padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = buildString {
                            append(message.time)
                            if (message.edited) append(" · ${strings.edit.lowercase()}")
                            if (message.pending) append(" · ${strings.sending}")
                        },
                        fontSize = 11.sp,
                        color = if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime
                    )
                }
            }
        }
    }
}

@Composable
private fun AndroidStyleComposer(
    draft: String,
    onDraftChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    sending: Boolean,
    tgColors: TelegramHomeColors,
    onSend: () -> Unit
) {
    val showSendButton = draft.isNotBlank() || sending
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            color = tgColors.composerField,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderIconButton(icon = Icons.Outlined.Add, onClick = {}, tint = tgColors.composerIcon, modifier = Modifier.size(40.dp))
                BasicTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    enabled = enabled && !sending,
                    cursorBrush = SolidColor(tgColors.composerCursor),
                    textStyle = TextStyle(color = tgColors.composerText, fontSize = 16.sp, lineHeight = 20.sp),
                    modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                    maxLines = 5,
                    decorationBox = { inner ->
                        if (draft.isBlank()) Text(placeholder, color = tgColors.composerHint, fontSize = 16.sp)
                        inner()
                    }
                )
                HeaderIconButton(icon = Icons.Outlined.KeyboardArrowDown, onClick = {}, tint = tgColors.composerIcon, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.width(8.dp))
        val targetScale by animateFloatAsState(targetValue = if (showSendButton) 1f else 0.95f, animationSpec = tween(150), label = "desktopSendScale")
        Surface(
            modifier = Modifier.size(48.dp).graphicsLayer { scaleX = targetScale; scaleY = targetScale },
            shape = CircleShape,
            color = if (showSendButton) tgColors.composerBlue else tgColors.composerField,
            shadowElevation = 1.dp
        ) {
            Box(Modifier.fillMaxSize().clickable(enabled = enabled && !sending && draft.isNotBlank(), onClick = onSend), contentAlignment = Alignment.Center) {
                if (sending) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                else Icon(Icons.Outlined.Send, contentDescription = null, tint = if (showSendButton) Color.White else tgColors.composerIcon, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun MessageContextMenuOverlay(message: NoveoHomeMessage, strings: NoveoStrings, tgColors: TelegramHomeColors, onDismiss: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
        )
        Surface(
            modifier = Modifier.align(if (message.isOutgoing) Alignment.CenterEnd else Alignment.CenterStart).padding(18.dp),
            shape = RoundedCornerShape(22.dp),
            color = tgColors.incomingBubble,
            shadowElevation = 8.dp
        ) {
            Column(Modifier.width(220.dp).padding(vertical = 6.dp)) {
                MenuItem(strings.reply, Icons.Outlined.Reply, tgColors.headerIcon, tgColors.incomingText, onDismiss)
                MenuItem(strings.copyText, Icons.Outlined.ContentCopy, tgColors.headerIcon, tgColors.incomingText, onDismiss)
                if (message.isOutgoing) MenuItem(strings.edit, Icons.Outlined.Edit, tgColors.headerIcon, tgColors.incomingText, onDismiss)
                MenuItem(strings.delete, Icons.Outlined.Delete, Color(0xFFE53935), Color(0xFFE53935), onDismiss)
            }
        }
    }
}

@Composable
private fun MenuItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, textColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 15.sp, color = textColor)
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier.size(44.dp)) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun ProfileCircle(name: String, isSavedMessages: Boolean = false, size: androidx.compose.ui.unit.Dp = 48.dp, modifier: Modifier = Modifier) {
    val colors = if (isSavedMessages) listOf(Color(0xFF60A5FA), Color(0xFF2563EB)) else listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.92f), MaterialTheme.colorScheme.secondary.copy(alpha = 0.82f))
    Box(
        modifier = modifier.size(size).clip(CircleShape).background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        if (isSavedMessages) {
            Icon(Icons.Outlined.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.58f))
        } else {
            Text(name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.38f).sp)
        }
    }
}

@Composable
private fun VerifiedIcon(modifier: Modifier = Modifier.size(14.dp)) {
    Box(modifier = modifier.background(Color(0xFF2EA6FF), CircleShape), contentAlignment = Alignment.Center) {
        Icon(Icons.Outlined.Close, contentDescription = null, tint = Color.White, modifier = Modifier.fillMaxSize().padding(3.dp).graphicsLayer { rotationZ = -45f })
    }
}

@Composable
private fun UnreadBadge(count: Int) {
    Box(modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary).padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
        Text(count.coerceAtMost(99).toString(), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
    }
}
