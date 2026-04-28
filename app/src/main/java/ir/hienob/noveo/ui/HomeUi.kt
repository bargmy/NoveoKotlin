package ir.hienob.noveo.ui


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material.icons.outlined.Collections
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.asPaddingValues
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.lerp as lerpDp
import androidx.compose.ui.text.lerp as lerpTextStyle
import androidx.compose.ui.util.lerp as lerpFloat
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import ir.hienob.noveo.R
import ir.hienob.noveo.app.AppUiState
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSummary
import ir.hienob.noveo.data.MessageFileAttachment
import ir.hienob.noveo.data.NotificationSettings
import ir.hienob.noveo.data.SavedSticker
import ir.hienob.noveo.data.Session
import ir.hienob.noveo.data.UserSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope

private const val NOVEO_BASE_URL = "https://noveo.ir:8443"
private const val CLIENT_VERSION = "v0.4.5 Kotlin"

private fun formatLastSeen(lastSeen: Long?, strings: NoveoStrings): String {
    if (lastSeen == null || lastSeen <= 0) return strings.lastSeenRecently
    
    val now = Calendar.getInstance()
    val date = Calendar.getInstance().apply { timeInMillis = lastSeen * 1000L }
    
    val diffMillis = now.timeInMillis - date.timeInMillis
    val diffSeconds = diffMillis / 1000
    val diffMinutes = diffSeconds / 60
    val diffHours = diffMinutes / 60
    val diffDays = diffHours / 24
    
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeStr = timeFormat.format(date.time)
    
    return when {
        diffSeconds < 60 -> strings.justNow
        diffMinutes < 60 -> strings.minutesAgo.format(diffMinutes)
        now.get(Calendar.YEAR) == date.get(Calendar.YEAR) && now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) -> "${strings.lastSeenAt} $timeStr"
        else -> {
            val yesterday = Calendar.getInstance().apply { 
                timeInMillis = now.timeInMillis
                add(Calendar.DAY_OF_YEAR, -1)
            }
            if (yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR) && yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)) {
                "${strings.lastSeenYesterday} $timeStr"
            } else if (diffDays < 7) {
                strings.lastSeenDaysAgo.format(diffDays)
            } else if (diffDays < 30) {
                val weeks = diffDays / 7
                if (weeks <= 1) strings.lastSeenWeekAgo else strings.lastSeenWeeksAgo.format(weeks)
            } else if (diffDays < 365) {
                val months = diffDays / 30
                if (months <= 1) strings.lastSeenMonthAgo else strings.lastSeenMonthsAgo.format(months)
            } else {
                strings.lastSeenLongTimeAgo
            }
        }
    }
}

@Immutable
class TelegramBubbleShape(
    val isOutgoing: Boolean,
    val hasTail: Boolean,
    val cornerRadius: Float = 48f
) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val path = androidx.compose.ui.graphics.Path().apply {
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
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

private enum class SettingsSection {
    MENU, SUBSCRIPTION, PROFILE, ACCOUNT, PREFERENCES, CHANGELOG, THEME, NOTIFICATIONS
}

private data class ThemeSection(
    val title: String,
    val subtitle: String,
    val presets: List<ThemePreset>
)

@Composable
internal fun HomeScreen(
    state: AppUiState,
    onOpenChat: (String) -> Unit,
    onStartDirectChat: (String) -> Unit,
    onStartCreateChat: (String, String, String?, String?) -> Unit,
    onSearchPublic: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    onLogout: () -> Unit,
    onAttachFile: (android.net.Uri) -> Unit,
    onRemoveAttachment: () -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onLoadOlder: () -> Unit,
    onReply: (ChatMessage?) -> Unit,
    onEditMessage: (ChatMessage?) -> Unit,
    onForwardMessage: (ChatMessage?) -> Unit,
    onToggleReaction: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onPinMessage: (String, Boolean) -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onDismissUpdate: () -> Unit,
    onDownloadUpdate: () -> Unit,
    onInstallUpdate: () -> Unit,
    onCheckUpdate: () -> Unit,
    onUpdateNotificationSettings: (NotificationSettings) -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onPlayAudio: (ChatMessage) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onDownloadFile: (ChatMessage) -> Unit,
    onSendSticker: (SavedSticker) -> Unit,
    onAddSavedSticker: (ChatMessage) -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onForwardConfirm: (ChatMessage, String) -> Unit = { _, _ -> }
) {
    val strings = getStrings(state.languageCode)
    val context = LocalContext.current

    var showMenu by rememberSaveable { mutableStateOf(false) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showContactsModal by rememberSaveable { mutableStateOf(false) }
    var showCreateModal by rememberSaveable { mutableStateOf(false) }
    var showSettingsModal by rememberSaveable { mutableStateOf(false) }
    var settingsSection by rememberSaveable { mutableStateOf(SettingsSection.MENU) }
    var profileUserId by rememberSaveable { mutableStateOf<String?>(null) }
    var showGroupInfo by rememberSaveable { mutableStateOf(false) }
    var selectedMediaAttachment by remember { mutableStateOf<MessageFileAttachment?>(null) }
    var animateModalEntrance by remember { mutableStateOf(false) }

    val keyboardHeight = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    var lastKeyboardHeight by remember { mutableStateOf(300.dp) }
    LaunchedEffect(keyboardHeight) {
        if (keyboardHeight > 0.dp) {
            lastKeyboardHeight = keyboardHeight
        }
    }

    val onMediaClick = { attachment: MessageFileAttachment ->
        if (attachment.isImage() || attachment.isVideo()) {
            selectedMediaAttachment = attachment
        } else {
            val url = attachment.url.normalizeNoveoUrl()
            if (url != null) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback or ignore
                }
            }
        }
    }

    var showForwardPicker by remember { mutableStateOf(false) }
    
    LaunchedEffect(state.forwardingMessage) {
        showForwardPicker = state.forwardingMessage != null
    }

    val isAnyModalVisible = showContactsModal || showCreateModal || showSettingsModal ||
                          (showGroupInfo && state.selectedChatId != null) ||
                          profileUserId != null || selectedMediaAttachment != null || showSearch || showForwardPicker

    androidx.activity.compose.BackHandler(enabled = isAnyModalVisible || showMenu || state.selectedChatId != null) {
        when {
            selectedMediaAttachment != null -> selectedMediaAttachment = null
            profileUserId != null -> {
                profileUserId = null
                animateModalEntrance = false
            }
            showGroupInfo -> {
                showGroupInfo = false
                animateModalEntrance = false
            }
            showSettingsModal -> showSettingsModal = false
            showCreateModal -> showCreateModal = false
            showContactsModal -> showContactsModal = false
            showForwardPicker -> onForwardMessage(null)
            showSearch -> { showSearch = false; searchQuery = "" }
            showMenu -> showMenu = false
            state.selectedChatId != null -> onBackToChats()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onUpdateNotificationSettings(state.notificationSettings.copy(enabled = true))
        }
    }

    val density = LocalDensity.current
    val menuWidth = 296.dp
    val menuWidthPx = with(density) { menuWidth.toPx() }
    val backSwipeEdgePx = with(density) { 32.dp.toPx() }
    
    // Core offsets
    val sidebarOffset = remember { androidx.compose.animation.core.Animatable(-menuWidthPx) }
    val chatBackOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    
    val scope = rememberCoroutineScope()

    // Sync menu state with animation
    LaunchedEffect(showMenu) {
        if (showMenu) {
            sidebarOffset.animateTo(0f, tween(250, easing = FastOutSlowInEasing))
        } else {
            sidebarOffset.animateTo(-menuWidthPx, tween(250, easing = FastOutSlowInEasing))
        }
    }

    LaunchedEffect(state.selectedChatId) {
        chatBackOffset.snapTo(0f)
    }

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
            .imePadding()
    ) {
        val compact = maxWidth < 760.dp
        
        // Allow sidebar swiping even when a chat is open in landscape, but keep it constrained
        val allowSidebarSwipe = !compact || state.selectedChatId == null

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(state.selectedChatId, showMenu, isAnyModalVisible, compact) {
                    if (isAnyModalVisible) return@pointerInput
                    var allowChatBackDrag = false
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            allowChatBackDrag = compact &&
                                state.selectedChatId != null &&
                                offset.x <= backSwipeEdgePx
                            scope.launch {
                                sidebarOffset.stop()
                                chatBackOffset.stop()
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (compact && state.selectedChatId != null) {
                                if (!allowChatBackDrag) return@detectHorizontalDragGestures
                                // Chat back: only positive drag
                                val target = chatBackOffset.value + dragAmount
                                if (target >= 0) {
                                    scope.launch { chatBackOffset.snapTo(target) }
                                }
                            } else if (allowSidebarSwipe) {
                                // Sidebar: constrain to valid range
                                val target = sidebarOffset.value + dragAmount
                                if (target <= 0f && target >= -menuWidthPx) {
                                    scope.launch { sidebarOffset.snapTo(target) }
                                }
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                if (compact && state.selectedChatId != null) {
                                    val total = chatBackOffset.value
                                    if (total > 150) {
                                        onBackToChats()
                                    }
                                    chatBackOffset.animateTo(0f)
                                } else if (allowSidebarSwipe) {
                                    if (sidebarOffset.value > -menuWidthPx * 0.6f) {
                                        showMenu = true
                                        sidebarOffset.animateTo(0f)
                                    } else {
                                        showMenu = false
                                        sidebarOffset.animateTo(-menuWidthPx)
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            if (compact) {
                val chatTranslation = if (state.selectedChatId != null) chatBackOffset.value else 0f
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { translationX = chatTranslation }
                ) {
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
                                strings = strings,
                                chats = filteredChats,                                users = filteredUsers,
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
                                onDismissUpdate = onDismissUpdate,
                                onDownloadUpdate = onDownloadUpdate,
                                onInstallUpdate = onInstallUpdate,
                                onPauseAudio = onPauseAudio,
                                onResumeAudio = onResumeAudio,
                                onStopAudio = onStopAudio,
                                onSeekAudio = onSeekAudio,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            ChatPane(
                                state = state,
                                compact = true,
                                strings = strings,
                                selectedChat = selectedChat,
                                currentUserId = state.session?.userId,
                                onBackToChats = onBackToChats,
                                onSend = onSend,
                                onTyping = onTyping,
                                onLoadOlder = onLoadOlder,
                                onMediaClick = onMediaClick,
                                onAttachFile = onAttachFile,
                                onRemoveAttachment = onRemoveAttachment,
                                onOpenProfile = { userId -> 
                                    profileUserId = userId
                                    animateModalEntrance = true
                                },
                                onOpenGroupInfo = { 
                                    showGroupInfo = true 
                                    animateModalEntrance = true
                                },
                                onReply = { onReply(it) },
                                onEditMessage = onEditMessage,
                                onForwardMessage = onForwardMessage,
                                onToggleReaction = onToggleReaction,
                                onDeleteMessage = onDeleteMessage,
                                onPinMessage = onPinMessage,
                                onCancelEdit = { onEditMessage(null) },
                                onPlayAudio = onPlayAudio,
                                onPauseAudio = onPauseAudio,
                                onResumeAudio = onResumeAudio,
                                onStopAudio = onStopAudio,
                                onSeekAudio = onSeekAudio,
                                onDownloadFile = onDownloadFile,
                                onSendSticker = onSendSticker,
                                onAddSavedSticker = onAddSavedSticker,
                                lastKeyboardHeight = lastKeyboardHeight
                            )
                        }
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    SidebarPane(
                        state = state,
                        strings = strings,
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
                        onDismissUpdate = onDismissUpdate,
                        onDownloadUpdate = onDownloadUpdate,
                        onInstallUpdate = onInstallUpdate,
                        onPauseAudio = onPauseAudio,
                        onResumeAudio = onResumeAudio,
                        onStopAudio = onStopAudio,
                        onSeekAudio = onSeekAudio,
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
                            WelcomePane(strings = strings, modifier = Modifier.weight(1f))
                        } else {
                            ChatPane(
                                state = state,
                                compact = false,
                                strings = strings,
                                selectedChat = selectedChat,
                                currentUserId = state.session?.userId,
                                onBackToChats = onBackToChats,
                                onSend = onSend,
                                onTyping = onTyping,
                                onLoadOlder = onLoadOlder,
                                onMediaClick = onMediaClick,
                                onAttachFile = onAttachFile,
                                onRemoveAttachment = onRemoveAttachment,
                                onOpenProfile = { userId -> 
                                    profileUserId = userId
                                    animateModalEntrance = true
                                },
                                onOpenGroupInfo = { 
                                    showGroupInfo = true 
                                    animateModalEntrance = true
                                },
                                onReply = { onReply(it) },
                                onEditMessage = onEditMessage,
                                onForwardMessage = onForwardMessage,
                                onToggleReaction = onToggleReaction,
                                onDeleteMessage = onDeleteMessage,
                                onPinMessage = onPinMessage,
                                onCancelEdit = { onEditMessage(null) },
                                onPlayAudio = onPlayAudio,
                                onPauseAudio = onPauseAudio,
                                onResumeAudio = onResumeAudio,
                                onStopAudio = onStopAudio,
                                onSeekAudio = onSeekAudio,
                                onDownloadFile = onDownloadFile,
                                onSendSticker = onSendSticker,
                                onAddSavedSticker = onAddSavedSticker,
                                lastKeyboardHeight = lastKeyboardHeight,
                                modifier = Modifier.weight(1f)
                                )
                        }
                    }
                }
            }
        }

        // Sidebar Menu Overlay & Sheet
        if (!compact || state.selectedChatId == null) {
            val currentMenuOffset = sidebarOffset.value
            val progress = (currentMenuOffset + menuWidthPx) / menuWidthPx
            if (progress > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = (0.5f * progress).coerceIn(0f, 0.5f)))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { showMenu = false }
                        )
                )

                Box(
                    modifier = Modifier
                        .width(menuWidth)
                        .fillMaxHeight()
                        .graphicsLayer { translationX = currentMenuOffset.coerceIn(-menuWidthPx, 0f) }
                ) {
                    MenuSheet(
                        state = state,
                        strings = strings,
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
            }
        }

        ModalHost(visible = showContactsModal, onDismiss = { showContactsModal = false }) {
            ContactsModal(
                strings = strings,
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
    CreateChannelModal(
        strings = strings,
        onCreate = onStartCreateChat,
        onClose = { showCreateModal = false }
    )
}

        if (selectedMediaAttachment != null) {
            FullscreenMediaModal(
                attachment = selectedMediaAttachment!!,
                onDismiss = { selectedMediaAttachment = null }
            )
        }

        ModalHost(visible = showSettingsModal, onDismiss = { showSettingsModal = false }) {
            SettingsModal(
                state = state,
                strings = strings,
                section = settingsSection,
                onSectionChange = { settingsSection = it },
                onClose = { showSettingsModal = false },
                onLogout = onLogout,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange,
                onUpdateProfile = onUpdateProfile,
                onChangePassword = onChangePassword,
                onDeleteAccount = onDeleteAccount,
                onSetLanguage = onSetLanguage,
                onCheckUpdate = onCheckUpdate,
                onUpdateNotificationSettings = onUpdateNotificationSettings,
                onRequestBatteryOptimization = onRequestBatteryOptimization,
                onRequestPermission = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
            )
        }

        ModalHost(visible = showGroupInfo && selectedChat != null, onDismiss = { showGroupInfo = false; animateModalEntrance = false }, fullscreen = true) {
            selectedChat?.let { chat ->
                GroupInfoModal(
                    chat = chat,
                    strings = strings,
                    usersById = state.usersById,
                    onOpenProfile = { userId -> 
                        profileUserId = userId
                        showGroupInfo = false
                        animateModalEntrance = false // standard open from list
                    },
                    onClose = { 
                        showGroupInfo = false
                        animateModalEntrance = false
                    },
                    animateEntrance = animateModalEntrance
                )
            }
        }

        ModalHost(visible = selectedProfile != null, onDismiss = { profileUserId = null; animateModalEntrance = false }, fullscreen = true) {
            selectedProfile?.let { user ->
                ProfileModal(
                    strings = strings,
                    user = user,
                    chats = state.chats,
                    selfUserId = state.session?.userId,
                    onClose = { 
                        profileUserId = null
                        animateModalEntrance = false
                    },
                    onMessage = {
                        profileUserId = null
                        animateModalEntrance = false
                        onStartDirectChat(user.id)
                    },
                    animateEntrance = animateModalEntrance
                )
            }
        }

        ModalHost(
            visible = showForwardPicker && state.forwardingMessage != null,
            onDismiss = { onForwardMessage(null) }
        ) {
            state.forwardingMessage?.let { msg ->
                ForwardChatPicker(
                    strings = strings,
                    chats = state.chats,
                    onClose = { onForwardMessage(null) },
                    onForward = { targetChatId ->
                        onForwardConfirm(msg, targetChatId)
                    }
                )
            }
        }
    }
}

@Composable
private fun SidebarPane(
    state: AppUiState,
    strings: NoveoStrings,
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
    onDismissUpdate: () -> Unit,
    onDownloadUpdate: () -> Unit,
    onInstallUpdate: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val tgColors = telegramColors()
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxSize()) {
            SidebarHeader(
                state = state,
                strings = strings,
                showSearch = showSearch,
                searchQuery = searchQuery,
                connectionTitle = state.connectionTitle,
                onMenuClick = onMenuClick,
                onSearchToggle = onSearchToggle,
                onSearchQueryChange = onSearchQueryChange
            )
            
            if (state.currentAudioMessage != null) {
                GlobalAudioMiniPlayer(
                    state = state,
                    strings = strings,
                    onPause = onPauseAudio,
                    onResume = onResumeAudio,
                    onStop = onStopAudio,
                    onSeek = onSeekAudio,
                    tgColors = tgColors
                )
            }
            
            state.updateInfo?.let { info ->
                UpdateBubble(
                    strings = strings,
                    updateInfo = info,
                    onDismiss = onDismissUpdate,
                    onUpdate = onDownloadUpdate,
                    onInstall = onInstallUpdate
                )
            }

            if (showSearch) {
                SearchResultsList(
                    strings = strings,
                    chats = chats,
                    users = users,
                    onOpenChat = onOpenChat,
                    onOpenContacts = onOpenContacts,
                    onOpenProfile = onOpenProfile
                )
            } else {
                ChatListContent(state = state, strings = strings, chats = chats, onOpenChat = onOpenChat)
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    strings: NoveoStrings,
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
            item { Text(strings.newChat, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary) }
            items(chats) { chat ->
                ChatRow(chat = chat, strings = strings, selected = false, onClick = { onOpenChat(chat.id) })
            }
        }
        if (users.isNotEmpty()) {
            item { Text(strings.allContacts, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary) }
            items(users) { user ->
                ContactRow(
                    user = user,
                    strings = strings,
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
    strings: NoveoStrings,
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
                    text = strings.noMessagesYet,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
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
                ChatRow(
                    chat = chat,
                    strings = strings,
                    selected = chat.id == state.selectedChatId,
                    onClick = { onOpenChat(chat.id) }
                )
            }
        }
    }
}

@Composable
private fun SidebarHeader(
    state: AppUiState,
    strings: NoveoStrings,
    showSearch: Boolean,
    searchQuery: String,
    connectionTitle: String,
    onMenuClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    val titleAlpha = 1f

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
                contentAlignment = Alignment.Center,
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
                        placeholder = { Text(strings.searchPlaceholder, style = MaterialTheme.typography.bodyMedium) },
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
        HeaderIconButton(
            icon = if (showSearch) Icons.Outlined.Close else Icons.Outlined.Search,
            onClick = onSearchToggle
        )
    }
}

@Composable
fun VerifiedIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(14.dp)
            .background(Color(0xFF2EA6FF), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = "Verified",
            tint = Color.White,
            modifier = Modifier.size(10.dp)
        )
    }
}

@Composable
private fun PinnedMessageBanner(
    pinnedMessage: ChatMessage,
    tgColors: TelegramThemeColors,
    strings: NoveoStrings,
    onClick: () -> Unit,
    onUnpin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() },
        color = tgColors.incomingBubble,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Bookmark,
                contentDescription = null,
                tint = tgColors.headerIcon,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    strings.pinnedMessage,
                    color = tgColors.headerIcon,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    pinnedMessage.content.previewText(),
                    color = tgColors.incomingText,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onUnpin) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Unpin",
                    tint = tgColors.headerSubtitle,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatPane(
    state: AppUiState,
    compact: Boolean,
    strings: NoveoStrings,
    selectedChat: ChatSummary?,
    currentUserId: String?,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    onLoadOlder: () -> Unit,
    onMediaClick: (MessageFileAttachment) -> Unit,
    onAttachFile: (android.net.Uri) -> Unit,
    onRemoveAttachment: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenGroupInfo: () -> Unit,
    onReply: (ChatMessage?) -> Unit,
    onEditMessage: (ChatMessage?) -> Unit,
    onForwardMessage: (ChatMessage?) -> Unit,
    onToggleReaction: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onPinMessage: (String, Boolean) -> Unit,
    onCancelEdit: () -> Unit,
    onPlayAudio: (ChatMessage) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onDownloadFile: (ChatMessage) -> Unit,
    onSendSticker: (SavedSticker) -> Unit,
    onAddSavedSticker: (ChatMessage) -> Unit,
    lastKeyboardHeight: Dp = 300.dp,
    modifier: Modifier = Modifier
) {
    var draft by rememberSaveable(state.selectedChatId) { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.editingMessage) {
        state.editingMessage?.content?.text?.let {
            draft = it
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAttachFile(it) }
    }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onAttachFile(it) }
    }

    val selectedTitle = remember(selectedChat, strings) {
        if (selectedChat?.title == "Saved Messages") strings.savedMessages
        else selectedChat?.title?.ifBlank { strings.chatInfo } ?: strings.chatInfo
    }
    
    val profileUserId = remember(selectedChat, state.session?.userId) {
        resolveProfileUserId(selectedChat, state.session?.userId)
    }
    val profileUser = remember(profileUserId, state.usersById) { state.usersById[profileUserId] }
    val isOnline = remember(profileUserId, state.onlineUserIds) { state.onlineUserIds.contains(profileUserId) }
    
    val onlineCount = remember(selectedChat, state.onlineUserIds) {
        selectedChat?.memberIds?.count { state.onlineUserIds.contains(it) } ?: 0
    }
    
    val typingUsers = state.typingUsers[selectedChat?.id].orEmpty()
    
    val showScrollToBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems - lastVisibleItem > 5
        }
    }

    val typingText = remember(typingUsers, state.usersById, strings) {
        if (typingUsers.isEmpty()) null
        else {
            val names = typingUsers.mapNotNull { state.usersById[it]?.username?.split(" ")?.firstOrNull() }
            when {
                names.isEmpty() -> strings.typingSomeone
                names.size == 1 -> "${names} ${strings.typingSingle}"
                names.size == 2 -> "${names} ${strings.typingDouble} ${names}"
                else -> "${localizeDigits(names.size.toString(), strings.languageCode)} ${strings.typingMulti}"
            }
        }
    }

    val subtitle = remember(selectedChat, profileUser, isOnline, onlineCount, typingText, strings) {
        if (selectedChat == null) return@remember ""
        if (typingText != null) return@remember typingText
        if (selectedChat.chatType == "private") {
            if (isOnline) strings.membersOnline
            else formatLastSeen(profileUser?.lastSeen, strings)
        } else {            val total = selectedChat.memberIds.size
            val totalStr = localizeDigits(total.toString(), strings.languageCode)
            val onlineStr = localizeDigits(onlineCount.toString(), strings.languageCode)
            val rawSubtitle = if (onlineCount > 0) "$totalStr ${strings.membersCount}${strings.comma} $onlineStr ${strings.membersOnline}" else "$totalStr ${strings.membersCount}"
            if (strings.languageCode == "fa" || strings.languageCode == "ar") "\u200F$rawSubtitle" else rawSubtitle
        }
    }

    var highlightedMessageId by remember { mutableStateOf<String?>(null) }
    var contextMenuState by remember { mutableStateOf<MessageContextMenuState?>(null) }
    var contextMenuExpanded by remember { mutableStateOf(false) }
    var showAttachPopup by remember { mutableStateOf(false) }
    var showStickers by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current


    val canLoadOlder = selectedChat?.hasMoreHistory == true && !state.loading
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    
    LaunchedEffect(firstVisibleItemIndex) {
        if (firstVisibleItemIndex <= 2 && canLoadOlder && state.messages.isNotEmpty()) {
            onLoadOlder()
        }
    }

    // Force scroll to bottom when a chat is first opened
    LaunchedEffect(state.selectedChatId) {
        if (state.selectedChatId != null && state.messages.isNotEmpty()) {
            listState.scrollToItem(state.messages.lastIndex)
        }
    }

    // Handle history loading vs new messages
    val lastMessageId = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(state.messages.size) {
        if (state.messages.isEmpty()) return@LaunchedEffect
        val newLastId = state.messages.last().id
        if (lastMessageId.value != null && newLastId != lastMessageId.value) {
            // New message arrived
            listState.animateScrollToItem(state.messages.lastIndex)
        }
        lastMessageId.value = newLastId
    }

    val tgColors = telegramColors()
    val onScrollToMessage = { messageId: String ->
        val index = state.messages.indexOfFirst { it.id == messageId }
        if (index >= 0) {
            scope.launch {
                highlightedMessageId = messageId
                listState.animateScrollToItem(index)
                delay(2000)
                if (highlightedMessageId == messageId) {
                    highlightedMessageId = null
                }
            }
        }
    }

    // Optimization: build a map of messages for fast reply lookup
    val messagesMap = remember(state.messages) { state.messages.associateBy { it.id } }
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxSize().background(tgColors.chatSurface)) {
        // 1. Messages Layer
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 8.dp, 
                top = if (selectedChat?.pinnedMessage != null) 114.dp else 64.dp, 
                end = 8.dp, 
                bottom = 90.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(
                items = state.messages,
                key = { _, msg -> msg.id },
                contentType = { _, _ -> "message" }
            ) { index, message ->
                val prevMessage = if (index > 0) state.messages[index - 1] else null
                val nextMessage = if (index < state.messages.lastIndex) state.messages[index + 1] else null
                
                val isFirstInGroup = prevMessage == null || 
                                     prevMessage.senderId != message.senderId || 
                                     (message.timestamp - prevMessage.timestamp) > 300 ||
                                     prevMessage.senderId == "system"
                
                val isLastInGroup = nextMessage == null || 
                                    nextMessage.senderId != message.senderId || 
                                    (nextMessage.timestamp - message.timestamp) > 300 ||
                                    nextMessage.senderId == "system"

                val repliedMessage = remember(message.replyToId, messagesMap) {
                    message.replyToId?.let { rid -> messagesMap[rid] }
                }

                val ownMessage = message.senderId == state.session?.userId

                MessageRow(
                    strings = strings,
                    message = message,
                    ownMessage = ownMessage,
                    senderAvatarUrl = state.usersById[message.senderId]?.avatarUrl,
                    showSenderInfo = isFirstInGroup,
                    hasTail = isLastInGroup,
                    isGroupChat = selectedChat?.chatType != "private",
                    currentUserId = currentUserId,
                    onMediaClick = onMediaClick,
                    onOpenProfile = onOpenProfile,
                    repliedMessage = repliedMessage,
                    onReply = { onReply(message) },
                    onToggleReaction = onToggleReaction,
                    onOpenContextMenu = { bubbleBounds ->
                        contextMenuState = MessageContextMenuState(
                            message = message,
                            ownMessage = ownMessage,
                            bubbleBounds = bubbleBounds
                        )
                        contextMenuExpanded = false
                    },
                    onScrollToMessage = onScrollToMessage,
                    onPlayAudio = onPlayAudio,
                    onPauseAudio = onPauseAudio,
                    onResumeAudio = onResumeAudio,
                    onStopAudio = onStopAudio,
                    onSeekAudio = onSeekAudio,
                    onDownloadFile = onDownloadFile,
                    appUiState = state,
                    isHighlighted = highlightedMessageId == message.id,
                    tgColors = tgColors
                )
            }
        }

        // 1.5 Chat Input Gradient Layer
        if (selectedChat?.canChat != false) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 42.dp) // Centered on typical input height + padding
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, tgColors.chatSurface),
                            startY = 0f,
                            endY = with(density) { 110.dp.toPx() }
                        )
                    )
            )
        }

        // 2. Headbar Layer (ActionBar)
        Surface(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            color = tgColors.incomingBubble,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderIconButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    onClick = onBackToChats,
                    tint = tgColors.headerIcon,
                    modifier = Modifier.padding(start = 4.dp)
                )
                
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (profileUserId != null) onOpenProfile(profileUserId) else if (selectedChat != null) onOpenGroupInfo()
                        }
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileCircle(
                        name = selectedTitle,
                        imageUrl = selectedChat?.avatarUrl,
                        size = 40.dp,
                        modifier = Modifier.clickable {
                            profileUserId?.let { onOpenProfile(it) } ?: onOpenGroupInfo()
                        }
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                selectedTitle,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = tgColors.headerTitle,
                                fontSize = 16.sp
                            )
                            if (selectedChat?.isVerified == true || profileUser?.isVerified == true) {
                                Spacer(Modifier.width(4.dp))
                                VerifiedIcon(modifier = Modifier.size(14.dp))
                            }
                        }
                        Text(
                            subtitle,
                            color = tgColors.headerSubtitle,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                HeaderIconButton(icon = Icons.Outlined.Call, onClick = {}, tint = tgColors.headerIcon)
                HeaderIconButton(icon = Icons.Outlined.Search, onClick = {}, tint = tgColors.headerIcon, modifier = Modifier.padding(end = 4.dp))
            }
        }

        if (state.currentAudioMessage != null) {
            Box(modifier = Modifier.padding(top = 56.dp)) {
                GlobalAudioMiniPlayer(
                    state = state,
                    strings = strings,
                    onPause = onPauseAudio,
                    onResume = onResumeAudio,
                    onStop = onStopAudio,
                    onSeek = onSeekAudio,
                    tgColors = tgColors
                )
            }
        }

        // 2.1 Pinned Message Bar
        selectedChat?.pinnedMessage?.let { pinned ->
            val pinnedOffset = if (state.currentAudioMessage != null) 104.dp else 56.dp
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = pinnedOffset)
                    .height(48.dp)
                    .clickable { onScrollToMessage(pinned.id) },
                color = tgColors.incomingBubble.copy(alpha = 0.98f),
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Bookmark,
                        contentDescription = null,
                        tint = tgColors.headerIcon,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            strings.pinnedMessage,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = tgColors.headerIcon,
                            fontSize = 12.sp
                        )
                        Text(
                            pinned.content.previewText(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp,
                            color = tgColors.incomingTime
                        )
                    }
                    IconButton(onClick = { onPinMessage(pinned.id, false) }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Unpin",
                            tint = tgColors.headerSubtitle,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2.5 Scroll to Bottom Button
        AnimatedVisibility(
            visible = showScrollToBottom,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 76.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable { 
                        scope.launch { 
                            if (state.messages.isNotEmpty()) {
                                val targetIndex = state.messages.lastIndex
                                if (listState.firstVisibleItemIndex < targetIndex - 20) {
                                    listState.scrollToItem(targetIndex - 10)
                                }
                                listState.animateScrollToItem(targetIndex)
                            }
                        }
                    },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Scroll to bottom",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // 3. Floating Input Layer
        if (selectedChat?.canChat != false) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (state.pendingAttachment != null) {                        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                            AttachmentPreview(
                                attachment = state.pendingAttachment,
                                onRemove = onRemoveAttachment
                            )
                        }
                    }
                    ChatInput(
                        draft = draft,
                        onDraftChange = {
                            draft = it
                            onTyping()
                        },
                        sendScale = 1f,
                        replyingTo = state.replyingToMessage,
                        editingMessage = state.editingMessage,
                        onCancelReply = { onReply(null) },
                        onCancelEdit = {
                            onCancelEdit()
                            draft = ""
                        },
                        placeholder = strings.messagePlaceholder,
                        strings = strings,
                        onAttachClick = { 
                            showAttachPopup = true
                        },
                        onLongAttachClick = {
                            filePicker.launch(arrayOf("*/*"))
                        },
                        onEmojiClick = {
                            showStickers = !showStickers
                            if (showStickers) {
                                keyboardController?.hide()
                            } else {
                                keyboardController?.show()
                            }
                        },
                        onTextFieldFocused = {
                            showStickers = false
                        },
                        showStickers = showStickers,
                        onPasteUri = { onAttachFile(it) },
                        hasAttachment = state.pendingAttachment != null,
                        tgColors = tgColors,
                        onActionClick = {
                            val text = draft.trim()
                            if (text.isNotBlank() || state.pendingAttachment != null) {
                                onSend(text)
                                draft = ""
                                showStickers = false
                            }
                        }
                    )
                    
                    if (showStickers) {
                        StickerPicker(
                            strings = strings,
                            stickers = state.savedStickers,
                            onStickerSelected = { sticker ->
                                onSendSticker(sticker)
                                showStickers = false
                                if (state.replyingToMessage != null) {
                                    onReply(null)
                                }
                            },
                            tgColors = tgColors
                        )
                    }
                }
            }
        }
        
        if (showAttachPopup) {
            AttachmentPicker(
                strings = strings,
                onGalleryClick = {
                    showAttachPopup = false
                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                },
                onFilesClick = {
                    showAttachPopup = false
                    filePicker.launch(arrayOf("*/*"))
                },
                onDismiss = { showAttachPopup = false },
                tgColors = tgColors
            )
        }

        // Context Menu Layer
        val currentContextMenuState = contextMenuState
        val displayedContextMenuState = remember(currentContextMenuState) {
            if (currentContextMenuState != null) currentContextMenuState else null
        }
        // Use a derived state or a separate remember to hold the state during exit animation
        var lastNonNullContextMenuState by remember { mutableStateOf<MessageContextMenuState?>(null) }
        LaunchedEffect(currentContextMenuState) {
            if (currentContextMenuState != null) {
                lastNonNullContextMenuState = currentContextMenuState
            }
        }

        AnimatedVisibility(
            visible = currentContextMenuState != null,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            lastNonNullContextMenuState?.let { menuState ->
                MessageContextMenuOverlay(
                    state = menuState,
                    expanded = contextMenuExpanded,
                    tgColors = tgColors,
                    onDismiss = {
                        contextMenuState = null
                        contextMenuExpanded = false
                    },
                    onExpandedChange = { contextMenuExpanded = it },
                    onReply = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onReply(menuState.message)
                    },
                    onCopyText = {
                        menuState.message.content.text?.let { clipboard.setText(AnnotatedString(it)) }
                        contextMenuState = null
                        contextMenuExpanded = false
                    },
                    onReaction = { emoji ->
                        contextMenuState = null
                        contextMenuExpanded = false
                        onToggleReaction(menuState.message.id, emoji)
                    },
                    onEdit = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onEditMessage(menuState.message)
                    },
                    onDelete = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onDeleteMessage(menuState.message.id)
                    },
                    onPin = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onPinMessage(menuState.message.id, !menuState.message.isPinned)
                    },
                    onForward = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onForwardMessage(menuState.message)
                    },
                    onDownload = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onDownloadFile(menuState.message)
                    },
                    onAddAsSticker = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        onAddSavedSticker(menuState.message)
                    },
                    strings = strings,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MessageRow(
    strings: NoveoStrings,
    message: ChatMessage,
    ownMessage: Boolean,
    senderAvatarUrl: String?,
    showSenderInfo: Boolean,
    hasTail: Boolean,
    isGroupChat: Boolean,
    currentUserId: String?,
    onMediaClick: (MessageFileAttachment) -> Unit,
    onOpenProfile: (String) -> Unit,
    repliedMessage: ChatMessage? = null,
    onReply: () -> Unit,
    onToggleReaction: (String, String) -> Unit,
    onOpenContextMenu: (Rect) -> Unit = {},
    onScrollToMessage: (String) -> Unit,
    onPlayAudio: (ChatMessage) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onDownloadFile: (ChatMessage) -> Unit,
    appUiState: AppUiState,
    isHighlighted: Boolean = false,
    tgColors: TelegramThemeColors = telegramColors()
) {
    val haptic = LocalHapticFeedback.current
    val isSystem = message.senderId == "system"
    if (isSystem) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Surface(
                color = tgColors.chatSurface.copy(alpha = 0.45f),
                shape = CircleShape
            ) {
                Text(
                    message.content.text ?: strings.noMessagesYet,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
        return
    }

    val timeStr = remember(message.timestamp, strings.languageCode) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        localizeDigits(sdf.format(Date(message.timestamp * 1000)), strings.languageCode)
    }
    var bubbleBounds by remember(message.id) { mutableStateOf<Rect?>(null) }
    val bubbleClickSource = remember { MutableInteractionSource() }

    val animOffsetY = remember(message.id) { androidx.compose.animation.core.Animatable(if (ownMessage && message.pending) 18f else 0f) }
    val animOffsetX = remember(message.id) { androidx.compose.animation.core.Animatable(if (ownMessage && message.pending) 26f else 0f) }
    val animAlpha = remember(message.id) { androidx.compose.animation.core.Animatable(if (ownMessage && message.pending) 0.35f else 1f) }
    val animScale = remember(message.id) { androidx.compose.animation.core.Animatable(if (ownMessage && message.pending) 0.92f else 1f) }

    // Swipe state
    val swipeOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(message.id) {
        if (ownMessage && message.pending) {
            launch { animAlpha.animateTo(1f, tween(200)) }
            launch { animOffsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)) }
            launch { animOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)) }
            launch { animScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)) }
        }
    }

    Column(
        horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (showSenderInfo) 10.dp else 0.dp)
            .padding(bottom = if (hasTail) 6.dp else 0.dp)
            .pointerInput(message.id) {
                coroutineScope {
                    launch {
                        detectTapGestures(
                            onTap = { bubbleBounds?.let(onOpenContextMenu) },
                            onDoubleTap = { onToggleReaction(message.id, "❤️") },
                            onLongPress = { bubbleBounds?.let(onOpenContextMenu) }
                        )
                    }
                    launch {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                if (dragAmount < 0) { // Only swipe left
                                    val current = swipeOffset.value
                                    val target = (current + dragAmount).coerceIn(-100f, 0f)
                                    if (current > -60f && target <= -60f) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                    scope.launch {
                                        swipeOffset.snapTo(target)
                                    }
                                }
                            },
                            onDragEnd = {
                                if (swipeOffset.value < -60f) {
                                    onReply()
                                }
                                scope.launch {
                                    swipeOffset.animateTo(0f, tween(200))
                                }
                            },
                            onDragCancel = {
                                scope.launch {
                                    swipeOffset.animateTo(0f, tween(200))
                                }
                            }
                        )
                    }
                }
            }
            .graphicsLayer {
                translationY = animOffsetY.value
                translationX = animOffsetX.value + swipeOffset.value
                alpha = animAlpha.value
                scaleX = animScale.value
                scaleY = animScale.value
                transformOrigin = TransformOrigin(if (ownMessage) 1f else 0f, 1f)
            }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
            if (!ownMessage) {
                // Telegram only shows avatar in group chats, and only for the last message in a group
                if (isGroupChat) {
                    if (hasTail) {
                        ProfileCircle(
                            name = message.senderName,
                            imageUrl = senderAvatarUrl,
                            size = 36.dp,
                            modifier = Modifier.clickable { onOpenProfile(message.senderId) }
                        )
                    } else {
                        Spacer(Modifier.width(36.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                }
            } else {
                Spacer(Modifier.weight(1f))
            }
            Column(
                horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
                modifier = if (ownMessage) Modifier else Modifier.weight(1f, false)
            ) {
                val isSticker = message.content.file?.isSticker() == true
                
                if (isSticker) {
                    val file = message.content.file!!
                    val normalizedUrl = remember(file.url) { file.url.normalizeNoveoUrl() }
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .onGloballyPositioned { bubbleBounds = it.boundsInRoot() }
                            .clickable(
                                interactionSource = bubbleClickSource,
                                indication = null
                            ) { bubbleBounds?.let(onOpenContextMenu) }
                    ) {
                        Column(horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start) {
                            if (repliedMessage != null) {
                                Surface(
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                        .clickable { onScrollToMessage(repliedMessage.id) },
                                    color = tgColors.chatSurface.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(28.dp)
                                                .background(if (ownMessage) tgColors.outgoingText.copy(alpha = 0.6f) else tgColors.incomingLink, RoundedCornerShape(1.dp))
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = repliedMessage.senderName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                                fontWeight = FontWeight.Bold,
                                                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = repliedMessage.content.previewText(),
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }

                            SubcomposeAsyncImage(
                                model = normalizedUrl,
                                contentDescription = "sticker",
                                modifier = Modifier.size(160.dp),
                                contentScale = ContentScale.Fit,
                                loading = {
                                    Box(Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                                        androidx.compose.material3.CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                                    }
                                }
                            )

                            if (message.reactions.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                FlowRow(
                                    modifier = Modifier.padding(horizontal = 4.dp).wrapContentWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    message.reactions.forEach { (emoji, userIds) ->
                                        if (userIds.isNotEmpty()) {
                                            Surface(
                                                modifier = Modifier.clickable { onToggleReaction(message.id, emoji) },
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color.Black.copy(alpha = 0.25f),
                                                border = if (userIds.contains(currentUserId)) BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)) else null
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(emoji, fontSize = 12.sp)
                                                    Spacer(Modifier.width(2.dp))
                                                    Text(
                                                        localizeDigits(userIds.size.toString(), strings.languageCode),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Surface(
                                modifier = Modifier.padding(top = 4.dp),
                                color = Color.Black.copy(alpha = 0.35f),
                                shape = CircleShape
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        timeStr,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = Color.White
                                    )
                                    if (ownMessage) {
                                        Spacer(Modifier.width(4.dp))
                                        val seen = message.seenBy.isNotEmpty()
                                        Icon(
                                            imageVector = if (seen) Icons.Outlined.DoneAll else Icons.Outlined.Check,
                                            contentDescription = if (seen) "Seen" else "Sent",
                                            modifier = Modifier.size(13.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .widthIn(max = this@BoxWithConstraints.maxWidth * 0.78f)
                            .onGloballyPositioned { bubbleBounds = it.boundsInRoot() }
                            .clickable(
                                interactionSource = bubbleClickSource,
                                indication = null
                            ) { bubbleBounds?.let(onOpenContextMenu) },
                        shape = TelegramBubbleShape(
                            isOutgoing = ownMessage,
                            hasTail = hasTail,
                            cornerRadius = with(LocalDensity.current) { 16.dp.toPx() }
                        ),
                        color = when {
                            ownMessage && isHighlighted -> tgColors.outgoingBubbleSelected
                            ownMessage -> tgColors.outgoingBubble
                            isHighlighted -> tgColors.incomingBubbleSelected
                            else -> tgColors.incomingBubble
                        },
                        shadowElevation = 0.5.dp
                    ) {
                        val hasVisualMedia = message.content.file?.let { it.isImage() || it.isVideo() } == true
                        Column(modifier = Modifier.padding(if (hasVisualMedia) 3.dp else 6.dp).padding(horizontal = 4.dp)) {
                            if (!ownMessage && isGroupChat && showSenderInfo) {
                                Text(
                                    message.senderName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                    color = tgColors.incomingLink,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                )
                            }
                            if (message.content.forwardedInfo != null) {
                                Row(
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                        modifier = Modifier.size(14.dp).scale(-1f, 1f) // Mirror for "from"
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Column {
                                        Text(
                                            text = strings.forwardedFrom,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = message.content.forwardedInfo.from,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                            color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink
                                        )
                                    }
                                }
                            }
                            if (repliedMessage != null) {
                                Surface(
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                        .clickable { onScrollToMessage(repliedMessage.id) },
                                    color = if (ownMessage) tgColors.replyOutgoing else tgColors.replyIncoming,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(28.dp)
                                                .background(if (ownMessage) tgColors.outgoingText.copy(alpha = 0.6f) else tgColors.incomingLink, RoundedCornerShape(1.dp))
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = repliedMessage.senderName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                                fontWeight = FontWeight.Bold,
                                                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = repliedMessage.content.previewText(),
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime
                                            )
                                        }
                                    }
                                }
                            }

                            val file = message.content.file
                            if (file != null) {
                                if (file.isAudio()) {
                                    AudioPlayer(
                                        message = message,
                                        isCurrent = appUiState.currentAudioMessage?.id == message.id,
                                        isPlaying = appUiState.isAudioPlaying,
                                        progress = appUiState.audioProgress,
                                        onPlayToggle = { onPlayAudio(message) },
                                        onSeek = onSeekAudio,
                                        tgColors = tgColors
                                    )
                                } else {
                                    MessageAttachment(
                                        file = file,
                                        ownMessage = ownMessage,
                                        onClick = { onMediaClick(file) },
                                        tgColors = tgColors
                                    )
                                }
                            }
                            val caption = message.content.text

                            if (!caption.isNullOrBlank()) {
                                if (message.content.file != null) Spacer(Modifier.height(4.dp))
                                Box(modifier = Modifier.padding(horizontal = if (hasVisualMedia) 6.dp else 4.dp)) {
                                    MarkdownText(
                                        text = caption,
                                        color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText
                                    )
                                }
                            }

                            if (message.reactions.isNotEmpty() || (isSticker && message.reactions.isNotEmpty())) {
                                Spacer(Modifier.height(4.dp))
                                FlowRow(
                                    modifier = Modifier.padding(horizontal = 4.dp).wrapContentWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    message.reactions.forEach { (emoji, userIds) ->
                                        if (userIds.isNotEmpty()) {
                                            Surface(
                                                modifier = Modifier.clickable { onToggleReaction(message.id, emoji) },
                                                shape = RoundedCornerShape(10.dp),
                                                color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.1f),
                                                border = if (userIds.contains(currentUserId)) BorderStroke(1.dp, if (ownMessage) tgColors.outgoingText.copy(alpha = 0.3f) else tgColors.incomingLink.copy(alpha = 0.3f)) else null
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(emoji, fontSize = 12.sp)
                                                    Spacer(Modifier.width(2.dp))
                                                    Text(
                                                        localizeDigits(userIds.size.toString(), strings.languageCode),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Row(
                                modifier = Modifier.align(Alignment.End).padding(top = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (message.editedAt != null) {
                                    Text(
                                        "edited",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = (if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime).copy(alpha = 0.7f),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                                Text(
                                    timeStr,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = if (ownMessage) tgColors.outgoingTime else tgColors.incomingTime
                                )
                                if (ownMessage) {
                                    Spacer(Modifier.width(4.dp))
                                    if (message.pending) {
                                        Icon(
                                            imageVector = Icons.Outlined.Schedule,
                                            contentDescription = strings.sending,
                                            modifier = Modifier.size(13.dp),
                                            tint = tgColors.outgoingTime
                                        )
                                    } else {
                                        val seen = message.seenBy.isNotEmpty()
                                        Icon(
                                            imageVector = if (seen) Icons.Outlined.DoneAll else Icons.Outlined.Check,
                                            contentDescription = if (seen) "Seen" else "Sent",
                                            modifier = Modifier.size(15.dp),
                                            tint = tgColors.outgoingTime
                                        )
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
}

@Composable
private fun ReplyPreview(message: ChatMessage, onCancel: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp).scale(-1f, 1f)
            )
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(32.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp))
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message.content.previewText(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onCancel, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Outlined.Close, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
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
    Text(
        annotated,
        color = color,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
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
private fun MessageAttachment(
    file: MessageFileAttachment,
    ownMessage: Boolean,
    onClick: () -> Unit,
    tgColors: TelegramThemeColors = telegramColors()
) {
    val normalizedUrl = remember(file.url) { file.url.normalizeNoveoUrl() }
    
    if (file.isImage()) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(bottom = 2.dp)
                .fillMaxWidth()
                .heightIn(max = 340.dp)
                .clickable { normalizedUrl?.let { onClick() } },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            SubcomposeAsyncImage(
                model = normalizedUrl,
                contentDescription = file.name,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                loading = {
                    Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp), color = if (ownMessage) tgColors.outgoingTime else tgColors.incomingLink)
                    }
                }
            )
        }
    } else {
        Surface(
            modifier = Modifier
                .padding(bottom = 2.dp)
                .fillMaxWidth()
                .clickable { normalizedUrl?.let { onClick() } },
            color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.08f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background((if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (file.isVideo()) Icons.Outlined.PlayArrow else Icons.Outlined.Description,
                        contentDescription = null,
                        tint = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText
                    )
                    Text(
                        text = file.type.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingText).copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactsModal(
    strings: NoveoStrings,
    users: List<UserSummary>,
    chats: List<ChatSummary>,
    selfUserId: String?,
    onClose: () -> Unit,
    onMessage: (String) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth().height(560.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(title = strings.allContacts, onClose = onClose)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users, key = { it.id }) { user ->
                    ContactRow(
                        user = user,
                        strings = strings,
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
    strings: NoveoStrings,
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.username, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (user.isVerified) {
                            Spacer(Modifier.width(4.dp))
                            VerifiedIcon(modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        user.handle ?: user.bio.ifBlank { if (user.isOnline) strings.online else strings.offline },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onMessage) {
                Text(if (existingChat != null) strings.messageButton else strings.open)
            }
        }
    }
}

@Composable
private fun CreateChannelModal(
    strings: NoveoStrings,
    onCreate: (String, String, String?, String?) -> Unit,
    onClose: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("group") }

    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ModalHeader(title = strings.newChat, onClose = onClose)
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(strings.newChat) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = handle, onValueChange = { handle = it }, label = { Text(strings.handleOptional) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text(strings.bioOptional) }, modifier = Modifier.fillMaxWidth())
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { type = "group" },
                        modifier = Modifier.weight(1f),
                        border = if (type == "group") BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                    ) { Text(strings.group) }
                    OutlinedButton(
                        onClick = { type = "channel" },
                        modifier = Modifier.weight(1f),
                        border = if (type == "channel") BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonBorder
                    ) { Text(strings.channel) }
                }
                
                Button(
                    onClick = { 
                        if (name.isNotBlank()) {
                            onCreate(name, type, handle.takeIf { it.isNotBlank() }, bio.takeIf { it.isNotBlank() })
                            onClose()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank()
                ) { Text(strings.create) }
            }
        }
    }
}

@Composable
private fun SettingsModal(
    state: AppUiState,
    strings: NoveoStrings,
    section: SettingsSection,
    onSectionChange: (SettingsSection) -> Unit,
    onClose: () -> Unit,
    onLogout: () -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onCheckUpdate: () -> Unit,
    onUpdateNotificationSettings: (NotificationSettings) -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onRequestPermission: () -> Unit
) {
    val me = state.session?.userId?.let { state.usersById[it] }
    Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth().height(620.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(
                title = when (section) {
                    SettingsSection.MENU -> strings.settings
                    SettingsSection.SUBSCRIPTION -> strings.subscription
                    SettingsSection.PROFILE -> strings.profile
                    SettingsSection.ACCOUNT -> strings.account
                    SettingsSection.PREFERENCES -> strings.preferences
                    SettingsSection.CHANGELOG -> strings.changelog
                    SettingsSection.THEME -> strings.themes
                    SettingsSection.NOTIFICATIONS -> strings.notificationSettings
                },
                onClose = onClose,
                onBack = when (section) {
                    SettingsSection.MENU -> null
                    SettingsSection.THEME -> ({ onSectionChange(SettingsSection.PREFERENCES) })
                    SettingsSection.NOTIFICATIONS -> ({ onSectionChange(SettingsSection.PREFERENCES) })
                    else -> ({ onSectionChange(SettingsSection.MENU) })
                }
            )
            Crossfade(targetState = section, label = "settings_section") { current ->
                when (current) {
                    SettingsSection.MENU -> SettingsMenu(strings, onSectionChange)
                    SettingsSection.SUBSCRIPTION -> SettingsSubscriptionSection(strings)
                    SettingsSection.PROFILE -> SettingsProfileSection(strings, me, onUpdateProfile)
                    SettingsSection.ACCOUNT -> SettingsAccountSection(strings, state, onLogout, onChangePassword, onDeleteAccount)
                    SettingsSection.PREFERENCES -> SettingsPreferencesSection(state, strings, onSectionChange, onSetLanguage, onCheckUpdate, currentTheme, onThemeChange, onRequestBatteryOptimization)
                    SettingsSection.CHANGELOG -> SettingsChangelogSection(strings)
                    SettingsSection.THEME -> SettingsThemeSection(strings, currentTheme, onThemeChange)
                    SettingsSection.NOTIFICATIONS -> SettingsNotificationSection(state, strings, onUpdateNotificationSettings, onRequestPermission)
                }
            } // Build fix pass 2
        }
    }
}

@Composable
private fun SettingsMenu(strings: NoveoStrings, onSectionChange: (SettingsSection) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettingsRow(strings.subscription, Icons.Outlined.Star) { onSectionChange(SettingsSection.SUBSCRIPTION) }
        SettingsRow(strings.profile, Icons.Outlined.Person) { onSectionChange(SettingsSection.PROFILE) }
        SettingsRow(strings.account, Icons.Outlined.AccountCircle) { onSectionChange(SettingsSection.ACCOUNT) }
        SettingsRow(strings.preferences, Icons.Outlined.Settings) { onSectionChange(SettingsSection.PREFERENCES) }
        SettingsRow(strings.changelog, Icons.Outlined.History) { onSectionChange(SettingsSection.CHANGELOG) }
    }
}

@Composable
private fun SettingsSubscriptionSection(strings: NoveoStrings) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailCard(title = strings.premiumTitle, body = strings.premiumBody)
        DetailCard(title = strings.walletTitle, body = strings.walletBody)
    }
}

@Composable
private fun SettingsProfileSection(strings: NoveoStrings, me: UserSummary?, onUpdateProfile: (String, String) -> Unit) {
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
            label = { Text(strings.displayName) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text(strings.bio) },
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
            Text(strings.saveChanges)
        }
    }
}

@Composable
private fun SettingsAccountSection(strings: NoveoStrings, state: AppUiState, onLogout: () -> Unit, onChangePassword: (String, String) -> Unit, onDeleteAccount: (String) -> Unit) {
    var showChangePassword by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccount by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DetailRow(strings.userId, state.session?.userId ?: strings.unknown)
        DetailRow(strings.sessionId, state.session?.sessionId?.ifBlank { "Connected" } ?: "Unavailable")
        DetailRow(strings.expiry, formatExpiry(state.session, strings))
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

        SettingsRow(strings.changePassword, Icons.Outlined.Lock) { showChangePassword = true }
        SettingsRow(strings.deleteAccount, Icons.Outlined.Delete) { showDeleteAccount = true }
        
        Spacer(Modifier.weight(1f))
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(strings.logout)
        }
    }

    if (showChangePassword) {
        var oldPw by remember { mutableStateOf("") }
        var newPw by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showChangePassword = false },
            title = { Text(strings.changePassword) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = oldPw, onValueChange = { oldPw = it }, label = { Text(strings.oldPassword) }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                    OutlinedTextField(value = newPw, onValueChange = { newPw = it }, label = { Text(strings.newPassword) }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (oldPw.isNotBlank() && newPw.isNotBlank()) {
                        onChangePassword(oldPw, newPw)
                        showChangePassword = false
                    }
                }) { Text(strings.update) }
            },
            dismissButton = { OutlinedButton(onClick = { showChangePassword = false }) { Text(strings.cancel) } }
        )
    }

    if (showDeleteAccount) {
        var pw by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDeleteAccount = false },
            title = { Text(strings.deleteAccount) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.deleteConfirmText)
                    OutlinedTextField(value = pw, onValueChange = { pw = it }, label = { Text(strings.passwordPlaceholder) }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pw.isNotBlank()) {
                            onDeleteAccount(pw)
                            showDeleteAccount = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(strings.delete) }
            },
            dismissButton = { OutlinedButton(onClick = { showDeleteAccount = false }) { Text(strings.cancel) } }
        )
    }
}

@Composable
private fun SettingsPreferencesSection(
    state: AppUiState,
    strings: NoveoStrings,
    onSectionChange: (SettingsSection) -> Unit,
    onSetLanguage: (String) -> Unit,
    onCheckUpdate: () -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onRequestBatteryOptimization: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    val languages = listOf(
        "English" to "en",
        "Persian (فارسی)" to "fa",
        "Russian (Русский)" to "ru",
        "Chinese (中文)" to "zh",
        "German (Deutsch)" to "de",
        "French (Français)" to "fr",
        "Spanish (Español)" to "es",
        "Arabic (العربية)" to "ar",
        "Turkish (Türkçe)" to "tr"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SettingsRow(strings.themes, Icons.Outlined.Palette) { onSectionChange(SettingsSection.THEME) }
        SettingsRow(strings.language, Icons.Outlined.Language) { showLanguageDialog = true }
        SettingsRow(strings.notificationSettings, Icons.Outlined.Notifications) { onSectionChange(SettingsSection.NOTIFICATIONS) }

        val updateText = when {
            state.isCheckingUpdate -> strings.checkingForUpdates
            state.updateInfo != null && state.updateInfo.isAvailable && !state.updateInfo.isDismissed -> strings.updateAvailable.format(state.updateInfo.version)
            state.updateInfo != null && !state.updateInfo.isAvailable -> strings.youAreUpdated
            else -> strings.checkForUpdates
        }
        SettingsRow(updateText, Icons.Outlined.History) { onCheckUpdate() }

        if (state.isBatteryOptimized) {
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(strings.batteryOptimization, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(strings.batteryOptimizationBody, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onRequestBatteryOptimization) {
                        Text(strings.requestPermission)
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

        DetailCard(title = strings.privacy, body = strings.privacyBody)
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(strings.selectLanguage) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(languages) { (name, code) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                onSetLanguage(code)
                                showLanguageDialog = false
                            },
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Text(name, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showLanguageDialog = false }) { Text(strings.cancel) } }
        )
    }
}

@Composable
private fun SettingsNotificationSection(
    state: AppUiState,
    strings: NoveoStrings,
    onUpdateNotificationSettings: (NotificationSettings) -> Unit,
    onRequestPermission: () -> Unit
) {
    val settings = state.notificationSettings
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NotificationToggle(strings.enableNotifications, settings.enabled) { enabled ->
            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                onRequestPermission()
            } else {
                onUpdateNotificationSettings(settings.copy(enabled = enabled))
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        NotificationToggle(strings.notifyDms, settings.dms) {
            onUpdateNotificationSettings(settings.copy(dms = it))
        }
        NotificationToggle(strings.notifyGroups, settings.groups) {
            onUpdateNotificationSettings(settings.copy(groups = it))
        }
        NotificationToggle(strings.notifyChannels, settings.channels) {
            onUpdateNotificationSettings(settings.copy(channels = it))
        }
    }
}

@Composable
private fun NotificationToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        androidx.compose.material3.Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
@Composable
private fun SettingsThemeSection(strings: NoveoStrings, currentTheme: ThemePreset, onThemeChange: (ThemePreset) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val themeSections = listOf(
            ThemeSection(
                title = strings.themeLight,
                subtitle = strings.themeLightDesc,
                presets = listOf(ThemePreset.LIGHT, ThemePreset.SKY_LIGHT, ThemePreset.SUNSET_LIGHT, ThemePreset.SNOWY_DAYDREAM)
            ),
            ThemeSection(
                title = strings.themeDark,
                subtitle = strings.themeDarkDesc,
                presets = listOf(ThemePreset.DARK, ThemePreset.OCEAN_DARK, ThemePreset.PLUM_DARK, ThemePreset.OLED_DARK)
            ),
            ThemeSection(
                title = strings.themePremium,
                subtitle = strings.themePremiumDesc,
                presets = listOf(ThemePreset.SUNSET_SHIMMER, ThemePreset.CHERRY_RED, ThemePreset.RAINBOW_RAGEBAIT, ThemePreset.SANOKI_MEOA)
            )
        )

        themeSections.forEach { section ->
            ThemeSectionBlock(
                strings = strings,
                section = section,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}

@Composable
private fun ThemeSectionBlock(
    strings: NoveoStrings,
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
                        Text(strings.themeSelected, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsChangelogSection(strings: NoveoStrings) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DetailCard(title = strings.version, body = localizeDigits(CLIENT_VERSION, strings.languageCode))
        DetailCard(title = strings.whatNew, body = strings.changelogBody)
    }
}

@Composable
private fun ProfileModal(
    strings: NoveoStrings,
    user: UserSummary,
    chats: List<ChatSummary>,
    selfUserId: String?,
    onClose: () -> Unit,
    onMessage: () -> Unit,
    animateEntrance: Boolean = false
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    
    val expandedHeight = 320.dp
    val collapsedHeight = 56.dp
    val expandedHeightPx = with(density) { expandedHeight.toPx() }
    val collapsedHeightPx = with(density) { collapsedHeight.toPx() }
    
    val fraction = remember { derivedStateOf { 
        if (listState.firstVisibleItemIndex > 0) 1f 
        else (listState.firstVisibleItemScrollOffset.toFloat() / (expandedHeightPx - collapsedHeightPx)).coerceIn(0f, 1f)
    } }.value
    
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = expandedHeight, bottom = 100.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Info Section (Telegram Style)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoItem(label = strings.displayName, value = user.username)
                                if (!user.handle.isNullOrBlank()) {
                                    InfoItem(label = strings.handle, value = user.handle, onClick = {
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(user.handle))
                                    })
                                }
                                if (user.bio.isNotBlank()) {
                                    InfoItem(label = strings.about, value = user.bio)
                                }
                                val joinedDateText = remember(user.joinedAt) {
                                    val joinedAt = user.joinedAt
                                    if (joinedAt != null && joinedAt > 0) {
                                        val date = Date(joinedAt * 1000L)
                                        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date)
                                    } else {
                                        "April 2026"
                                    }
                                }
                                InfoItem(label = strings.joinDate, value = joinedDateText)
                            }
                        }

                        Button(
                            onClick = onMessage, 
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) { 
                            Text(strings.sendMessage) 
                        }
                        
                        Spacer(Modifier.height(300.dp))
                    }
                }
            }
            
            // Collapsing Header
            val currentHeaderHeight = lerpDp(expandedHeight, collapsedHeight, fraction)
            Surface(
                modifier = Modifier.fillMaxWidth().height(currentHeaderHeight),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = lerpDp(0.dp, 4.dp, fraction)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Back Button
                    HeaderIconButton(
                        icon = Icons.AutoMirrored.Outlined.ArrowBack,
                        onClick = onClose,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                    )
                    
                    val avatarSize = lerpDp(120.dp, 38.dp, fraction)
                    
                    // Avatar position calculation
                    val expandedAvatarX = (screenWidth / 2) - (avatarSize / 2)
                    val collapsedAvatarX = 52.dp // Next to back button
                    val avatarX = lerpDp(expandedAvatarX, collapsedAvatarX, fraction)
                    
                    val expandedAvatarY = (expandedHeight / 2) - (avatarSize / 2) - 20.dp
                    val collapsedAvatarY = (collapsedHeight / 2) - (avatarSize / 2)
                    val avatarY = lerpDp(expandedAvatarY, collapsedAvatarY, fraction)
                    
                    Box(modifier = Modifier.offset(x = avatarX, y = avatarY)) {
                        ProfileCircle(name = user.username, imageUrl = user.avatarUrl, size = avatarSize)
                    }
                    
                    // Expanded Name/Status
                    if (fraction < 0.5f) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = expandedAvatarY + avatarSize + 16.dp)
                                .alpha((1f - fraction * 2f).coerceIn(0f, 1f)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    user.username, 
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (user.isVerified) {
                                    Spacer(Modifier.width(6.dp))
                                    VerifiedIcon(modifier = Modifier.size(18.dp))
                                }
                            }
                            val lastSeenText = remember(user, strings) {
                                if (user.isOnline) strings.online
                                else formatLastSeen(user.lastSeen, strings)
                            }
                            Text(
                                lastSeenText, 
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (user.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Collapsed Name/Status
                    if (fraction > 0.5f) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 100.dp) // Offset by back button + avatar
                                .alpha(((fraction - 0.5f) * 2f).coerceIn(0f, 1f))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    user.username,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (user.isVerified) {
                                    Spacer(Modifier.width(4.dp))
                                    VerifiedIcon(modifier = Modifier.size(14.dp))
                                }
                            }
                            val lastSeenText = remember(user, strings) {
                                if (user.isOnline) strings.online
                                else formatLastSeen(user.lastSeen, strings)
                            }
                            Text(
                                lastSeenText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (user.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String, onClick: (() -> Unit)? = null) {
    Column(
        modifier = if (onClick != null) Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp) else Modifier
    ) {
        Text(value, style = MaterialTheme.typography.bodyLarge)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun GroupInfoModal(
    chat: ChatSummary, 
    strings: NoveoStrings, 
    usersById: Map<String, UserSummary>, 
    onOpenProfile: (String) -> Unit,
    onClose: () -> Unit,
    animateEntrance: Boolean = false
) {
    val chatTitle = remember(chat.title, strings) {
        if (chat.title == "Saved Messages") strings.savedMessages
        else chat.title.ifBlank { strings.chatInfo }
    }
    
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    
    val expandedHeight = 320.dp
    val collapsedHeight = 56.dp
    val expandedHeightPx = with(density) { expandedHeight.toPx() }
    val collapsedHeightPx = with(density) { collapsedHeight.toPx() }
    
    val fraction = remember { derivedStateOf { 
        if (listState.firstVisibleItemIndex > 0) 1f 
        else (listState.firstVisibleItemScrollOffset.toFloat() / (expandedHeightPx - collapsedHeightPx)).coerceIn(0f, 1f)
    } }.value
    
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = expandedHeight, bottom = 100.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Info Section
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoItem(label = strings.title, value = chatTitle)
                                if (!chat.handle.isNullOrBlank()) {
                                    InfoItem(label = strings.link, value = "@${chat.handle}", onClick = {
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString("@${chat.handle}"))
                                    })
                                }
                                InfoItem(label = strings.type, value = chat.chatType.replaceFirstChar { it.uppercase() })
                            }
                        }

                        Text(
                            strings.members, 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                items(chat.memberIds, key = { it }) { memberId ->
                    val user = usersById[memberId]
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Card(
                            shape = RoundedCornerShape(12.dp), 
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            onClick = { onOpenProfile(memberId) }
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                ProfileCircle(name = user?.username ?: memberId, imageUrl = user?.avatarUrl, size = 40.dp)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(user?.username ?: memberId, fontWeight = FontWeight.SemiBold)
                                        if (user?.isVerified == true) {
                                            Spacer(Modifier.width(4.dp))
                                            VerifiedIcon(modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    val lastSeenText = remember(user, strings) {
                                        if (user?.isOnline == true) strings.online
                                        else formatLastSeen(user?.lastSeen, strings)
                                    }
                                    Text(
                                        lastSeenText, 
                                        style = MaterialTheme.typography.bodySmall, 
                                        color = if (user?.isOnline == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                item {
                    Spacer(Modifier.height(300.dp))
                }
            }
            
            // Collapsing Header
            val currentHeaderHeight = lerpDp(expandedHeight, collapsedHeight, fraction)
            Surface(
                modifier = Modifier.fillMaxWidth().height(currentHeaderHeight),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = lerpDp(0.dp, 4.dp, fraction)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Back Button
                    HeaderIconButton(
                        icon = Icons.AutoMirrored.Outlined.ArrowBack,
                        onClick = onClose,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                    )
                    
                    val avatarSize = lerpDp(120.dp, 38.dp, fraction)
                    
                    // Avatar position calculation
                    val expandedAvatarX = (screenWidth / 2) - (avatarSize / 2)
                    val collapsedAvatarX = 52.dp 
                    val avatarX = lerpDp(expandedAvatarX, collapsedAvatarX, fraction)
                    
                    val expandedAvatarY = (expandedHeight / 2) - (avatarSize / 2) - 20.dp
                    val collapsedAvatarY = (collapsedHeight / 2) - (avatarSize / 2)
                    val avatarY = lerpDp(expandedAvatarY, collapsedAvatarY, fraction)
                    
                    Box(modifier = Modifier.offset(x = avatarX, y = avatarY)) {
                        ProfileCircle(name = chatTitle, imageUrl = chat.avatarUrl, size = avatarSize)
                    }
                    
                    // Expanded Title/Subtitle
                    if (fraction < 0.5f) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = expandedAvatarY + avatarSize + 16.dp)
                                .alpha((1f - fraction * 2f).coerceIn(0f, 1f)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    chatTitle, 
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (chat.isVerified) {
                                    Spacer(Modifier.width(6.dp))
                                    VerifiedIcon(modifier = Modifier.size(18.dp))
                                }
                            }
                            Text(
                                "${chat.memberIds.size} members", 
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Collapsed Title/Subtitle
                    if (fraction > 0.5f) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 100.dp) 
                                .alpha(((fraction - 0.5f) * 2f).coerceIn(0f, 1f))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    chatTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (chat.isVerified) {
                                    Spacer(Modifier.width(4.dp))
                                    VerifiedIcon(modifier = Modifier.size(14.dp))
                                }
                            }
                            Text(
                                "${chat.memberIds.size} members",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
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
    strings: NoveoStrings,
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
        Text(strings.menu, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        MenuRow(strings.allContacts, Icons.Outlined.Info, onOpenContacts)
        MenuRow(strings.newChat, Icons.Outlined.Menu, onOpenCreate)
        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenStars),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFFFD700))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(strings.stars, fontWeight = FontWeight.SemiBold)
                    Text("${localizeDigits(state.wallet?.balanceLabel ?: "0.00", strings.languageCode)} ${strings.stars}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        MenuRow(strings.settings, Icons.Outlined.Settings, onOpenSettings)
        Spacer(Modifier.weight(1f))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(strings.brandName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(localizeDigits(CLIENT_VERSION, strings.languageCode), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun ChatRow(
    chat: ChatSummary,
    strings: NoveoStrings,
    selected: Boolean,
    onClick: () -> Unit
) {
    val chatTitle = remember(chat.title, strings) {
        if (chat.title == "Saved Messages") strings.savedMessages
        else chat.title.ifBlank { strings.chatInfo }
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProfileCircle(name = chatTitle, imageUrl = chat.avatarUrl)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(chatTitle, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (chat.isVerified) {
                        Spacer(Modifier.width(4.dp))
                        VerifiedIcon()
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(chat.lastMessagePreview.ifBlank { strings.noMessagesYet }, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
            }
            if (chat.unreadCount > 0) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary).padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(localizeDigits(chat.unreadCount.toString(), strings.languageCode), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun WelcomePane(strings: NoveoStrings, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(strings.brandName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(strings.selectChatHint, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ModalHost(visible: Boolean, onDismiss: () -> Unit, fullscreen: Boolean = false, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(250)),
        exit = fadeOut(animationSpec = tween(250))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (fullscreen) MaterialTheme.colorScheme.background else Color.Black.copy(alpha = 0.5f))
                .then(
                    if (fullscreen) Modifier // No close-on-click for fullscreen
                    else Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = if (fullscreen) Modifier.fillMaxSize() else Modifier
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
private fun ProfileCircle(name: String, imageUrl: String?, size: Dp = 40.dp, modifier: Modifier = Modifier) {
    if (name == "Saved Messages") {
        Box(
            modifier = modifier
                .size(size)

                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Bookmark, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.6f))
        }
        return
    }

    val resolvedImageUrl = remember(imageUrl) { imageUrl.normalizeNoveoUrl() }
    val isDefaultAvatar = remember(resolvedImageUrl) { resolvedImageUrl?.endsWith("default.png") == true }
    
    val fallback = @Composable {
        Box(
            modifier = modifier
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
            modifier = modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
            contentScale = ContentScale.Crop,
            loading = { fallback() },
            error = { fallback() }
        )
    } else {
        fallback()
    }
}

@Composable
private fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint)
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

private fun formatExpiry(session: Session?, strings: NoveoStrings): String {
    val value = session?.expiresAt ?: 0L
    if (value <= 0L) return strings.unknown
    return runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        localizeDigits(sdf.format(Date(value)), strings.languageCode)
    }.getOrElse { strings.unknown }
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
        nameValue.endsWith(".ogg") ||
        urlValue.endsWith(".mp4") ||
        urlValue.endsWith(".mov") ||
        urlValue.endsWith(".webm") ||
        urlValue.endsWith(".ogg")
}

private fun String?.normalizeNoveoUrl(): String? {
    val value = this?.trim().orEmpty().replace("\\", "/")
    if (value.isBlank()) return null
    if (value.startsWith("data:")) return value
    val noCaptchaMatch = Regex(
        pattern = "^(?:(?:https?|wss?)://)?server_no_captcha(?::\\d+)?(?:(/.*)?)$",
        option = RegexOption.IGNORE_CASE
    ).matchEntire(value)
    if (noCaptchaMatch != null) {
        val path = noCaptchaMatch.groupValues.getOrNull(1).orEmpty()
        return if (path.isBlank()) NOVEO_BASE_URL else "$NOVEO_BASE_URL$path"
    }
    if (value.startsWith("//")) return "https:$value"
    if (value.startsWith("http://") || value.startsWith("https://")) return value
    if (value.startsWith("ws://")) return value.replaceFirst("ws://", "http://")
    if (value.startsWith("wss://")) return value.replaceFirst("wss://", "https://")
    
    val normalized = if (value.startsWith("/")) value else "/$value"
    return "$NOVEO_BASE_URL$normalized"
}

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun FullscreenMediaModal(attachment: MessageFileAttachment, onDismiss: () -> Unit) {
    val normalizedUrl = remember(attachment.url) { attachment.url.normalizeNoveoUrl() }
    val context = LocalContext.current
    val isVideo = remember(attachment) { attachment.isVideo() }

    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isVideo && normalizedUrl != null) {
                val exoPlayer = remember {
                    androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
                        setMediaItem(androidx.media3.common.MediaItem.fromUri(android.net.Uri.parse(normalizedUrl)))
                        prepare()
                        playWhenReady = true
                    }
                }
                androidx.compose.runtime.DisposableEffect(Unit) {
                    onDispose { exoPlayer.release() }
                }
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { ctx ->
                        androidx.media3.ui.PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = normalizedUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            
            HeaderIconButton(
                icon = Icons.Outlined.Close,
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(16.dp),
                tint = Color.White
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentPicker(
    strings: NoveoStrings,
    onGalleryClick: () -> Unit,
    onFilesClick: () -> Unit,
    onDismiss: () -> Unit,
    tgColors: TelegramThemeColors
) {
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = tgColors.incomingBubble,
        dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle(color = tgColors.headerSubtitle.copy(alpha = 0.4f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = strings.selectSource, 
                style = MaterialTheme.typography.titleMedium,
                color = tgColors.headerTitle,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AttachmentOption(
                    label = strings.gallery,
                    icon = Icons.Outlined.Collections, // Use Collections icon
                    color = Color(0xFF2EA6FF),
                    onClick = onGalleryClick,
                    modifier = Modifier.weight(1f)
                )
                AttachmentOption(
                    label = strings.files,
                    icon = Icons.Outlined.Description,
                    color = Color(0xFF34C759),
                    onClick = onFilesClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AttachmentOption(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun StickerPicker(
    strings: NoveoStrings,
    stickers: List<SavedSticker>,
    onStickerSelected: (SavedSticker) -> Unit,
    tgColors: TelegramThemeColors,
    displayHeight: Dp = 300.dp
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(displayHeight),
        color = tgColors.chatSurface
    ) {
        Column {
            TabRow(
                selectedTabIndex = 0,
                containerColor = tgColors.incomingBubble,
                contentColor = tgColors.headerIcon,
                divider = {}
            ) {
                Tab(selected = true, onClick = {}, text = { Text(strings.stickers) })
            }
            
            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stickers.size, key = { index -> stickers[index].url }) { index ->
                    val sticker = stickers[index]
                    val normalizedUrl = remember(sticker.url) { sticker.url.normalizeNoveoUrl() }
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onStickerSelected(sticker) }
                            .background(tgColors.incomingBubble.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (sticker.type == "tgs") {
                            Text(
                                text = "TGS",
                                color = tgColors.headerIcon,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            AsyncImage(
                                model = normalizedUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
                if (stickers.isEmpty()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(4) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.noSavedStickers,
                                color = tgColors.headerSubtitle,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateBubble(
    strings: NoveoStrings,
    updateInfo: ir.hienob.noveo.app.UpdateInfo,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    onInstall: () -> Unit
) {
    if (updateInfo.isDismissed || !updateInfo.isAvailable) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE8F5E9),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.updateAvailable.format(updateInfo.version),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                
                if (updateInfo.isDownloaded) {
                    TextButton(onClick = onInstall, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(strings.install, color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelLarge)
                    }
                } else if (!updateInfo.isDownloading) {
                    TextButton(onClick = onDismiss, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(strings.dismiss, color = Color(0xFF757575), style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = onUpdate, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(strings.update, color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            
            if (updateInfo.isDownloading) {
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = updateInfo.downloadProgress,
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF2E7D32),
                    trackColor = Color(0xFFC8E6C9)
                )
            }
        }
    }
}

@Composable
private fun ForwardChatPicker(
    strings: NoveoStrings,
    chats: List<ChatSummary>,
    onClose: () -> Unit,
    onForward: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth().height(480.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ModalHeader(title = strings.forwarded, onClose = onClose)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chats) { chat ->
                    ChatRow(
                        chat = chat,
                        strings = strings,
                        selected = false,
                        onClick = { onForward(chat.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioPlayer(
    message: ChatMessage,
    isCurrent: Boolean,
    isPlaying: Boolean,
    progress: Float,
    onPlayToggle: () -> Unit,
    onSeek: (Float) -> Unit,
    tgColors: TelegramThemeColors
) {
    val durationText = remember(message.content.file?.size) {
        "Audio"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPlayToggle,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isCurrent && isPlaying) Icons.Filled.Pause else Icons.Outlined.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = if (isCurrent) tgColors.headerIcon else tgColors.incomingText
            )
        }
        
        Spacer(Modifier.width(8.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            androidx.compose.material3.Slider(
                value = if (isCurrent) progress else 0f,
                onValueChange = onSeek,
                modifier = Modifier.height(24.dp),
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = if (isCurrent) tgColors.headerIcon else tgColors.incomingText,
                    activeTrackColor = if (isCurrent) tgColors.headerIcon else tgColors.incomingText
                )
            )
            Text(
                text = durationText,
                style = MaterialTheme.typography.labelSmall,
                color = if (isCurrent) tgColors.headerSubtitle else tgColors.incomingTime
            )
        }
    }
}

@Composable
private fun GlobalAudioMiniPlayer(
    state: AppUiState,
    strings: NoveoStrings,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSeek: (Float) -> Unit,
    tgColors: TelegramThemeColors
) {
    val audio = state.currentAudioMessage ?: return
    
    Surface(
        color = tgColors.chatSurface,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (state.isAudioPlaying) onPause() else onResume() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (state.isAudioPlaying) Icons.Filled.Pause else Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = tgColors.headerIcon
                )
            }
            
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = audio.content.file?.name ?: strings.brandName,
                    style = MaterialTheme.typography.labelMedium,
                    color = tgColors.headerTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { state.audioProgress },
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                    color = tgColors.headerIcon,
                    trackColor = tgColors.headerIcon.copy(alpha = 0.2f)
                )
            }
            
            IconButton(onClick = onStop, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Close, contentDescription = "Close", tint = tgColors.headerIcon, modifier = Modifier.size(18.dp))
            }
        }
    }
}

