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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.HeadsetOff
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicOff
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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontFamily
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
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
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
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.lerp as lerpDp
import androidx.compose.ui.text.lerp as lerpTextStyle
import androidx.compose.ui.util.lerp as lerpFloat
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import ir.hienob.noveo.R
import ir.hienob.noveo.app.AppUiState
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSummary
import ir.hienob.noveo.data.E2EESessionStatus
import ir.hienob.noveo.data.MessageFileAttachment
import ir.hienob.noveo.data.NotificationSettings
import ir.hienob.noveo.data.SavedSticker
import ir.hienob.noveo.data.Session
import ir.hienob.noveo.data.SocketEvent
import ir.hienob.noveo.data.UserSummary
import ir.hienob.noveo.data.ProfileSkin
import ir.hienob.noveo.data.PremiumStarIcon
import ir.hienob.noveo.data.UserGift
import androidx.compose.ui.draw.drawBehind
import androidx.compose.material3.LocalContentColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt
import androidx.compose.ui.res.pluralStringResource

private const val NOVEO_BASE_URL = "https://noveo.ir:8443"
private val CLIENT_VERSION: String
    get() = "v${ir.hienob.noveo.BuildConfig.VERSION_NAME} Kotlin"

private data class SelectedMediaAttachment(
    val attachment: MessageFileAttachment,
    val localPath: String
)

private fun ChatSummary.isSavedMessagesChat(currentUserId: String?): Boolean =
    id.startsWith("saved_") ||
        avatarUrl == "saved_messages" ||
        title == "Saved Messages" ||
        (chatType == "private" && currentUserId != null && memberIds.size == 1 && memberIds.firstOrNull() == currentUserId)

private fun getNicknameFontFamily(fontId: String?): FontFamily? {
    val normalized = fontId?.trim()?.lowercase(Locale.ROOT) ?: return null
    if (normalized.isBlank()) return null
    return when (normalized) {
        "bradhitc" -> FontFamily(androidx.compose.ui.text.font.Font(R.font.bradhitc))
        "freescpt" -> FontFamily(androidx.compose.ui.text.font.Font(R.font.freescpt))
        "f_majik" -> FontFamily(androidx.compose.ui.text.font.Font(R.font.f_majik))
        "impact" -> FontFamily(androidx.compose.ui.text.font.Font(R.font.impact))
        "npidivani" -> FontFamily(androidx.compose.ui.text.font.Font(R.font.npidivani))
        else -> null
    }
}

private fun localAttachmentCacheFile(root: File, file: MessageFileAttachment): File {
    val extension = file.name.substringAfterLast('.', "").ifBlank {
        when {
            file.isVideo() -> "mp4"
            file.type.equals("image/gif", true) -> "gif"
            file.type.equals("image/webp", true) -> "webp"
            file.type.equals("image/jpeg", true) -> "jpg"
            file.isTgsSticker() -> "tgs"
            else -> "bin"
        }
    }
    val safeName = file.name
        .substringBeforeLast('.', file.name)
        .replace(Regex("[^A-Za-z0-9._-]"), "_")
        .take(40)
        .ifBlank { "attachment" }
    return File(root, "attachments/$safeName-${file.downloadKey()}.$extension")
}

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

private fun messageDayKey(timestampSeconds: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestampSeconds * 1000L))
}

private fun formatMessageDateSeparator(timestampSeconds: Long, strings: NoveoStrings): String {
    if (strings.languageCode == "fa") {
        return formatPersianMessageDateSeparator(timestampSeconds)
    }

    val date = Date(timestampSeconds * 1000L)
    val messageCalendar = Calendar.getInstance().apply { time = date }
    val now = Calendar.getInstance()
    val pattern = if (messageCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) "MMMM d" else "MMMM d, yyyy"
    val locale = Locale.forLanguageTag(strings.languageCode.ifBlank { "en" })
    return localizeDigits(SimpleDateFormat(pattern, locale).format(date), strings.languageCode)
}

private data class JalaliDate(val year: Int, val month: Int, val day: Int)

private val jalaliMonthNames = listOf(
    "فروردین",
    "اردیبهشت",
    "خرداد",
    "تیر",
    "مرداد",
    "شهریور",
    "مهر",
    "آبان",
    "آذر",
    "دی",
    "بهمن",
    "اسفند"
)

private fun formatPersianMessageDateSeparator(timestampSeconds: Long): String {
    val messageCalendar = Calendar.getInstance().apply { timeInMillis = timestampSeconds * 1000L }
    val nowCalendar = Calendar.getInstance()
    val messageDate = gregorianToJalali(
        messageCalendar.get(Calendar.YEAR),
        messageCalendar.get(Calendar.MONTH) + 1,
        messageCalendar.get(Calendar.DAY_OF_MONTH)
    )
    val nowDate = gregorianToJalali(
        nowCalendar.get(Calendar.YEAR),
        nowCalendar.get(Calendar.MONTH) + 1,
        nowCalendar.get(Calendar.DAY_OF_MONTH)
    )
    val monthName = jalaliMonthNames.getOrElse(messageDate.month - 1) { "" }
    val label = if (messageDate.year == nowDate.year) {
        "${messageDate.day} $monthName"
    } else {
        "${messageDate.day} $monthName ${messageDate.year}"
    }
    return localizeDigits(label, "fa")
}

private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JalaliDate {
    val gregorianMonthDays = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    val adjustedYear = if (gm > 2) gy + 1 else gy
    var days = 355666 + (365 * gy) + ((adjustedYear + 3) / 4) -
        ((adjustedYear + 99) / 100) + ((adjustedYear + 399) / 400) +
        gd + gregorianMonthDays[gm - 1]

    var jy = -1595 + 33 * (days / 12053)
    days %= 12053
    jy += 4 * (days / 1461)
    days %= 1461

    if (days > 365) {
        jy += (days - 1) / 365
        days = (days - 1) % 365
    }

    val jm: Int
    val jd: Int
    if (days < 186) {
        jm = 1 + (days / 31)
        jd = 1 + (days % 31)
    } else {
        jm = 7 + ((days - 186) / 30)
        jd = 1 + ((days - 186) % 30)
    }

    return JalaliDate(jy, jm, jd)
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
    MENU, SUBSCRIPTION, PROFILE, ACCOUNT, PREFERENCES, THEME, NOTIFICATIONS
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
    onAddContact: (String, String) -> Unit = { _, _ -> },
    onUpdateProfile: (String, String, String?, String?, ProfileSkin?, PremiumStarIcon?) -> Unit,
    onSubmitPaymentRequest: (String, ByteArray, String) -> Unit,
    onCancelSubscription: () -> Unit,
    onUpdateChatProfile: (String, String, String) -> Unit,
    onUpdateChatHandle: (String, String?) -> Unit,
    onContactAdmin: () -> Unit,
    onFetchUserProfile: (String) -> Unit,
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
    onSetBetaUpdatesEnabled: (Boolean) -> Unit,
    onSetDoubleTapReaction: (String) -> Unit,
    onUpdateNotificationSettings: (NotificationSettings) -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onPlayAudio: (ChatMessage) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onDownloadFile: (ChatMessage) -> Unit,
    onCancelDownload: (ChatMessage) -> Unit,
    onCall: (String) -> Unit,
    onAcceptCall: (String, String) -> Unit,
    onDeclineCall: () -> Unit,
    onLeaveCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleDeafen: () -> Unit,
    onToggleMinimize: () -> Unit,
    onCancelUpload: (String) -> Unit,
    onSendSticker: (SavedSticker) -> Unit,
    onAddSavedSticker: (ChatMessage) -> Unit,
    onHandleClick: (String) -> Unit,
    onJoinChat: (String) -> Unit,
    onLeaveChat: (String) -> Unit,
    onClearNavigationSignal: () -> Unit,
    onBotCallback: (String, String, String) -> Unit,
    onConnectE2EE: () -> Unit,
    onEndE2EE: () -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onForwardConfirm: (ChatMessage, String) -> Unit = { _, _ -> },
    onDismissError: () -> Unit = {}
) {
    val strings = getStrings(state.languageCode)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        initializeTgsSupport(context)
    }

    var showMenu by rememberSaveable { mutableStateOf(false) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showContactsModal by rememberSaveable { mutableStateOf(false) }
    var showCreateModal by rememberSaveable { mutableStateOf(false) }
    var showSettingsModal by rememberSaveable { mutableStateOf(false) }
    var settingsSection by rememberSaveable { mutableStateOf(SettingsSection.MENU) }
    var profileUserId by rememberSaveable { mutableStateOf<String?>(null) }
    var infoChatId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedMediaAttachment by remember { mutableStateOf<SelectedMediaAttachment?>(null) }
    var animateModalEntrance by remember { mutableStateOf(false) }

    val onOpenProfile = { userId: String ->
        profileUserId = userId
        animateModalEntrance = true
    }

    LaunchedEffect(profileUserId) {
        profileUserId?.let { onFetchUserProfile(it) }
    }

    val keyboardHeight = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    var lastKeyboardHeight by remember { mutableStateOf(300.dp) }
    LaunchedEffect(keyboardHeight) {
        if (keyboardHeight > 0.dp) {
            lastKeyboardHeight = keyboardHeight
        }
    }

    val onMediaClick = { message: ChatMessage, attachment: MessageFileAttachment ->
        val localPath = state.attachmentDownloads[attachment.downloadKey()]?.localPath
            ?: localAttachmentCacheFile(context.filesDir, attachment).takeIf { it.exists() }?.absolutePath
        if (!localPath.isNullOrBlank() && (attachment.isImage() || attachment.isVideo())) {
            selectedMediaAttachment = SelectedMediaAttachment(attachment = attachment, localPath = localPath)
        } else if (attachment.isImage() || attachment.isVideo()) {
            onDownloadFile(message)
        } else {
            val url = attachment.url.normalizeNoveoUrl()
            if (url != null) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    context.startActivity(intent)
                } catch (_: Exception) {
                }
            }
        }
    }

    var showForwardPicker by remember { mutableStateOf(false) }
    
    LaunchedEffect(state.forwardingMessage) {
        showForwardPicker = state.forwardingMessage != null
    }

    LaunchedEffect(state.pendingProfileId) {
        state.pendingProfileId?.let {
            profileUserId = it
            animateModalEntrance = true
            onClearNavigationSignal()
        }
    }

    LaunchedEffect(state.pendingGroupInfoId) {
        state.pendingGroupInfoId?.let {
            val chat = state.chats.firstOrNull { c -> c.id == it }
            if (chat?.chatType == "private") {
                val otherUserId = chat.memberIds.firstOrNull { memberId -> memberId != state.session?.userId }
                infoChatId = null
                otherUserId?.let { userId ->
                    profileUserId = userId
                    animateModalEntrance = true
                }
            } else {
                infoChatId = it
                animateModalEntrance = true
            }
            onClearNavigationSignal()
        }
    }

    val isAnyModalVisible = showContactsModal || showCreateModal || showSettingsModal ||
                          infoChatId != null ||
                          profileUserId != null || selectedMediaAttachment != null || showSearch || showForwardPicker

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
    val rootView = LocalView.current
    var chatSnapshot by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var chatSnapshotBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    var chatSnapshotCapturedForGesture by remember { mutableStateOf(false) }
    var lockedSlidingChatId by remember { mutableStateOf<String?>(null) }
    var lockedSlidingChat by remember { mutableStateOf<ChatSummary?>(null) }
    var lockedSlidingMessages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var isBackSwipeDragging by remember { mutableStateOf(false) }
    var isCompletingBackSwipe by remember { mutableStateOf(false) }
    var lastCompactSelectedChatId by remember { mutableStateOf<String?>(null) }
    var openingChatId by remember { mutableStateOf<String?>(null) }
    var directChatDragOffset by remember { mutableStateOf<Float?>(null) }

    // Sync menu state with animation
    LaunchedEffect(showMenu) {
        if (showMenu) {
            sidebarOffset.animateTo(0f, tween(250, easing = FastOutSlowInEasing))
        } else {
            sidebarOffset.animateTo(-menuWidthPx, tween(250, easing = FastOutSlowInEasing))
        }
    }

    LaunchedEffect(state.selectedChatId) {
        if (isBackSwipeDragging || isCompletingBackSwipe || openingChatId != null) return@LaunchedEffect
        chatSnapshot = null
        chatSnapshotCapturedForGesture = false
        if (state.selectedChatId == null) {
            chatBackOffset.snapTo(0f)
            directChatDragOffset = null
            lockedSlidingChatId = null
            lockedSlidingChat = null
            lockedSlidingMessages = emptyList()
        }
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

    val effectiveSelectedChatId = lockedSlidingChatId ?: state.selectedChatId
    val selectedChat = state.chats.firstOrNull { it.id == effectiveSelectedChatId }
    val effectiveSelectedChat = lockedSlidingChat ?: selectedChat
    val effectiveChatState = if (lockedSlidingChat != null) state.copy(selectedChatId = lockedSlidingChatId, messages = lockedSlidingMessages) else state
    val latestState by rememberUpdatedState(state)
    val latestSelectedChat by rememberUpdatedState(selectedChat)
    val latestOnBackToChats by rememberUpdatedState(onBackToChats)
    val selectedProfile = remember(profileUserId, state.usersById) { profileUserId?.let(state.usersById::get) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        val compact = maxWidth < 760.dp
        val screenWidthPx = with(density) { maxWidth.toPx() }

        fun clearBackSwipeVisualState() {
            chatSnapshotCapturedForGesture = false
            chatSnapshot = null
            lockedSlidingChatId = null
            lockedSlidingChat = null
            lockedSlidingMessages = emptyList()
            isBackSwipeDragging = false
            isCompletingBackSwipe = false
            directChatDragOffset = null
        }

        fun lockCurrentChatForBack(currentState: AppUiState = latestState): Boolean {
            val currentSelectedId = currentState.selectedChatId ?: return false
            val currentSelectedChat = latestSelectedChat?.takeIf { it.id == currentSelectedId }
                ?: currentState.chats.firstOrNull { it.id == currentSelectedId }
                ?: return false
            lockedSlidingChatId = currentSelectedId
            lockedSlidingChat = currentSelectedChat
            lockedSlidingMessages = currentState.messages.toList()
            return true
        }

        suspend fun finishCompactBackNavigation(targetWidth: Float) {
            if (isCompletingBackSwipe) return
            isBackSwipeDragging = false
            isCompletingBackSwipe = true
            openingChatId = null
            sidebarOffset.stop()
            chatBackOffset.stop()
            val startingOffset = directChatDragOffset ?: chatBackOffset.value
            directChatDragOffset = null
            chatBackOffset.snapTo(startingOffset.coerceAtLeast(0f))
            chatBackOffset.animateTo(
                targetWidth.coerceAtLeast(chatBackOffset.value),
                tween(260, easing = FastOutSlowInEasing)
            )
            latestOnBackToChats()
            lastCompactSelectedChatId = null
            chatBackOffset.snapTo(0f)
            clearBackSwipeVisualState()
        }

        fun startCompactBackNavigation() {
            if (compact) {
                if (lockedSlidingChat != null || lockCurrentChatForBack(latestState)) {
                    scope.launch { finishCompactBackNavigation(screenWidthPx) }
                } else {
                    latestOnBackToChats()
                }
            } else {
                latestOnBackToChats()
            }
        }

        LaunchedEffect(compact, state.selectedChatId, screenWidthPx) {
            val currentChatId = state.selectedChatId
            if (!compact) {
                openingChatId = null
                directChatDragOffset = null
                lastCompactSelectedChatId = currentChatId
                return@LaunchedEffect
            }
            if (isBackSwipeDragging || isCompletingBackSwipe || lockedSlidingChat != null) {
                return@LaunchedEffect
            }
            val previousChatId = lastCompactSelectedChatId
            if (currentChatId != null && currentChatId != previousChatId) {
                openingChatId = currentChatId
                directChatDragOffset = null
                chatBackOffset.stop()
                chatBackOffset.snapTo(screenWidthPx)
                try {
                    chatBackOffset.animateTo(0f, tween(260, easing = FastOutSlowInEasing))
                } finally {
                    if (state.selectedChatId == currentChatId) {
                        lastCompactSelectedChatId = currentChatId
                    }
                    openingChatId = null
                }
            } else if (currentChatId == null) {
                openingChatId = null
                directChatDragOffset = null
                chatBackOffset.snapTo(0f)
                lastCompactSelectedChatId = null
            } else {
                lastCompactSelectedChatId = currentChatId
            }
        }

        androidx.activity.compose.BackHandler(enabled = isAnyModalVisible || showMenu || state.selectedChatId != null) {
            when {
                selectedMediaAttachment != null -> selectedMediaAttachment = null
                profileUserId != null -> {
                    profileUserId = null
                    animateModalEntrance = false
                }
                infoChatId != null -> {
                    infoChatId = null
                    animateModalEntrance = false
                }
                showSettingsModal -> showSettingsModal = false
                showCreateModal -> showCreateModal = false
                showContactsModal -> showContactsModal = false
                showForwardPicker -> onForwardMessage(null)
                showSearch -> { showSearch = false; searchQuery = "" }
                showMenu -> showMenu = false
                state.selectedChatId != null -> startCompactBackNavigation()
            }
        }
        
        // Allow sidebar swiping even when a chat is open in landscape, but keep it constrained
        val allowSidebarSwipe = !compact || state.selectedChatId == null

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(showMenu, isAnyModalVisible, compact) {
                    if (isAnyModalVisible) return@pointerInput
                    var allowChatBackDrag = false
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            val currentState = latestState
                            val currentSelectedId = currentState.selectedChatId
                            val currentSelectedChat = currentSelectedId?.let { id ->
                                latestSelectedChat?.takeIf { it.id == id }
                                    ?: currentState.chats.firstOrNull { it.id == id }
                            }
                            allowChatBackDrag = compact && currentSelectedId != null && currentSelectedChat != null && offset.x <= backSwipeEdgePx
                            if (allowChatBackDrag) {
                                lockedSlidingChatId = currentSelectedId
                                lockedSlidingChat = currentSelectedChat
                                lockedSlidingMessages = currentState.messages.toList()
                                openingChatId = null
                                directChatDragOffset = chatBackOffset.value.coerceAtLeast(0f)
                                isBackSwipeDragging = true
                            }
                            scope.launch {
                                sidebarOffset.stop()
                                chatBackOffset.stop()
                            }
                            if (allowChatBackDrag) {
                                chatSnapshotCapturedForGesture = false
                                chatSnapshot = null
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (compact && (lockedSlidingChat != null || latestState.selectedChatId != null)) {
                                if (!allowChatBackDrag) return@detectHorizontalDragGestures
                                // Chat back: only positive drag
                                val currentOffset = directChatDragOffset ?: chatBackOffset.value
                                val target = currentOffset + dragAmount
                                if (target >= 0) {
                                    directChatDragOffset = target
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
                                if (compact && (lockedSlidingChat != null || latestState.selectedChatId != null)) {
                                    val total = directChatDragOffset ?: chatBackOffset.value
                                    if (total > 150) {
                                        if (lockedSlidingChat == null) {
                                            lockCurrentChatForBack(latestState)
                                        }
                                        finishCompactBackNavigation(size.width.toFloat().coerceAtLeast(total))
                                    } else {
                                        chatBackOffset.snapTo(total.coerceAtLeast(0f))
                                        directChatDragOffset = null
                                        chatBackOffset.animateTo(0f, tween(220, easing = FastOutSlowInEasing))
                                        clearBackSwipeVisualState()
                                    }
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
                        },
                        onDragCancel = {
                            scope.launch {
                                if (compact && (lockedSlidingChat != null || latestState.selectedChatId != null)) {
                                    val cancelOffset = directChatDragOffset ?: chatBackOffset.value
                                    chatBackOffset.snapTo(cancelOffset.coerceAtLeast(0f))
                                    directChatDragOffset = null
                                    chatBackOffset.animateTo(0f, tween(220, easing = FastOutSlowInEasing))
                                    clearBackSwipeVisualState()
                                }
                            }
                        }
                    )
                }
        ) {
            if (compact) {
                val compactOpeningPrime = compact &&
                    state.selectedChatId != null &&
                    state.selectedChatId != lastCompactSelectedChatId &&
                    openingChatId == null &&
                    lockedSlidingChat == null &&
                    !isBackSwipeDragging &&
                    !isCompletingBackSwipe
                val showingChatSurface = lockedSlidingChat != null || state.selectedChatId != null || openingChatId != null || compactOpeningPrime
                fun chatSurfaceOffset(): IntOffset {
                    val offset = when {
                        compactOpeningPrime -> screenWidthPx
                        directChatDragOffset != null -> directChatDragOffset ?: 0f
                        showingChatSurface -> chatBackOffset.value
                        else -> 0f
                    }
                    return IntOffset(offset.roundToInt(), 0)
                }

                Box(modifier = Modifier.fillMaxSize()) {
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
                        onOpenProfile = onOpenProfile,
                        onOpenGroupInfo = { chatId ->
                            infoChatId = chatId
                            animateModalEntrance = true
                        },
                        onDismissUpdate = onDismissUpdate,
                        onDownloadUpdate = onDownloadUpdate,
                        onInstallUpdate = onInstallUpdate,
                        onPauseAudio = onPauseAudio,
                        onResumeAudio = onResumeAudio,
                        onStopAudio = onStopAudio,
                        onSeekAudio = onSeekAudio,
                        onToggleMute = onToggleMute,
                        onToggleMinimize = onToggleMinimize,
                        onLeaveCall = onLeaveCall,
                        modifier = Modifier.fillMaxSize()
                    )

                    val visibleChat = effectiveSelectedChat
                    if (showingChatSurface && visibleChat != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned { chatSnapshotBounds = it.boundsInRoot() }
                                .offset { chatSurfaceOffset() }
                        ) {
                            ChatPane(
                                state = effectiveChatState,
                                compact = true,
                                strings = strings,
                                selectedChat = visibleChat,
                                currentUserId = state.session?.userId,
                                onBackToChats = { startCompactBackNavigation() },
                                onSend = onSend,
                                onTyping = onTyping,
                                onLoadOlder = onLoadOlder,
                                onMediaClick = onMediaClick,
                                onAttachFile = onAttachFile,
                                onRemoveAttachment = onRemoveAttachment,
                                onOpenProfile = onOpenProfile,
                                onOpenGroupInfo = {
                                    infoChatId = visibleChat.id
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
                                onCancelDownload = onCancelDownload,
                                onCall = { onCall(visibleChat.id) },
                                onCancelUpload = { onCancelUpload(visibleChat.id) },
                                onSendSticker = onSendSticker,
                                onAddSavedSticker = onAddSavedSticker,
                                onHandleClick = onHandleClick,
                                onJoinChat = onJoinChat,
                                onLeaveChat = onLeaveChat,
                                onBotCallback = onBotCallback,
                                onConnectE2EE = onConnectE2EE,
                                onEndE2EE = onEndE2EE,
                                onToggleMute = onToggleMute,
                                onToggleMinimize = onToggleMinimize,
                                onLeaveCall = onLeaveCall,
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
                        onOpenProfile = onOpenProfile,
                        onOpenGroupInfo = { chatId ->
                            infoChatId = chatId
                            animateModalEntrance = true
                        },
                        onDismissUpdate = onDismissUpdate,
                        onDownloadUpdate = onDownloadUpdate,
                        onInstallUpdate = onInstallUpdate,
                        onPauseAudio = onPauseAudio,
                        onResumeAudio = onResumeAudio,
                        onStopAudio = onStopAudio,
                        onSeekAudio = onSeekAudio,
                        onToggleMute = onToggleMute,
                        onToggleMinimize = onToggleMinimize,
                        onLeaveCall = onLeaveCall,
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
                                onOpenProfile = onOpenProfile,
                                onOpenGroupInfo = { 
                                    selectedChat?.id?.let {
                                        infoChatId = it
                                        animateModalEntrance = true
                                    }
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
                                onCancelDownload = onCancelDownload,
                                onCall = { selectedChat?.id?.let { onCall(it) } },
                                onCancelUpload = { selectedChat?.id?.let { onCancelUpload(it) } },
                                onSendSticker = onSendSticker,
                                onAddSavedSticker = onAddSavedSticker,
                                onHandleClick = onHandleClick,
                                onJoinChat = onJoinChat,
                                onLeaveChat = onLeaveChat,
                                onBotCallback = onBotCallback,
                                onConnectE2EE = onConnectE2EE,
                                onEndE2EE = onEndE2EE,
                                onToggleMute = onToggleMute,
                                onToggleMinimize = onToggleMinimize,
                                onLeaveCall = onLeaveCall,
                                lastKeyboardHeight = lastKeyboardHeight,
                                modifier = Modifier.weight(1f)
                                )                        }
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
                        },
                        onOpenProfile = { userId ->
                            showMenu = false
                            onOpenProfile(userId)
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

        ModalHost(visible = showSettingsModal, onDismiss = { showSettingsModal = false; onDismissError() }, fullscreen = true) {
            SettingsModal(
                state = state,
                strings = strings,
                onAddContact = onAddContact,
                section = settingsSection,
                onSectionChange = { settingsSection = it },
                onClose = { showSettingsModal = false; onDismissError() },
                onLogout = onLogout,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange,
                onUpdateProfile = onUpdateProfile,
                onSubmitPaymentRequest = onSubmitPaymentRequest,
                onCancelSubscription = onCancelSubscription,
                onUpdateChatProfile = onUpdateChatProfile,
                onUpdateChatHandle = onUpdateChatHandle,
                onContactAdmin = onContactAdmin,
                onChangePassword = onChangePassword,
                onDeleteAccount = onDeleteAccount,
                onSetLanguage = onSetLanguage,
                onCheckUpdate = onCheckUpdate,
                onSetBetaUpdatesEnabled = onSetBetaUpdatesEnabled,
                onSetDoubleTapReaction = onSetDoubleTapReaction,
                onUpdateNotificationSettings = onUpdateNotificationSettings,
                onRequestBatteryOptimization = onRequestBatteryOptimization,
                onRequestPermission = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                onDismissError = onDismissError,
                onFetchUserProfile = onFetchUserProfile
            )
        }

        ModalHost(visible = infoChatId != null, onDismiss = { infoChatId = null; animateModalEntrance = false }, fullscreen = true) {
            val infoChat = remember(infoChatId, state.chats) { state.chats.firstOrNull { it.id == infoChatId } }
            infoChat?.let { chat ->
                GroupInfoModal(
                    chat = chat,
                    strings = strings,
                    state = state,
                    onOpenProfile = onOpenProfile,
                    onClose = { 
                        infoChatId = null
                        animateModalEntrance = false
                    },
                    onJoinChat = onJoinChat,
                    onLeaveChat = { chatId ->
                        onLeaveChat(chatId)
                        if (infoChatId == chatId) {
                            infoChatId = null
                            animateModalEntrance = false
                        }
                    },
                    onOpenChat = { chatId ->
                        onOpenChat(chatId)
                        infoChatId = null
                        animateModalEntrance = false
                    },
                    animateEntrance = animateModalEntrance,
                    onAvatarClick = { avatarUrl ->
                        selectedMediaAttachment = SelectedMediaAttachment(
                            attachment = MessageFileAttachment(
                                url = avatarUrl,
                                name = "Avatar",
                                type = "image/png",
                                size = 0L
                            ),
                            localPath = ""
                        )
                    }
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
                    contacts = state.contacts,
                    onAddContact = onAddContact,
                    onClose = { 
                        profileUserId = null
                        animateModalEntrance = false
                    },
                    onMessage = {
                        profileUserId = null
                        infoChatId = null
                        animateModalEntrance = false
                        onStartDirectChat(user.id)
                    },
                    onLeaveChat = onLeaveChat,
                    animateEntrance = animateModalEntrance,
                    onAvatarClick = { avatarUrl ->
                        selectedMediaAttachment = SelectedMediaAttachment(
                            attachment = MessageFileAttachment(
                                url = avatarUrl,
                                name = "Avatar",
                                type = "image/png",
                                size = 0L
                            ),
                            localPath = ""
                        )
                    }
                )
            }
        }

        ModalHost(
            visible = showForwardPicker && state.forwardingMessage != null,
            onDismiss = { onForwardMessage(null) }
        ) {
            val msg = state.forwardingMessage
            if (msg != null) {
                ForwardChatPicker(
                    strings = strings,
                    chats = state.chats,
                    usersById = state.usersById,
                    currentUserId = state.session?.userId,
                    onClose = { onForwardMessage(null) },
                    onForward = { targetChatId ->
                        onForwardConfirm(msg, targetChatId)
                    }
                )
            }
        }

        // Voice Call Overlay
        if (state.voiceChatState.connectionState != ir.hienob.noveo.data.VoiceConnectionState.IDLE && !state.voiceChatState.isMinimized) {
            VoiceCallOverlay(
                state = state.voiceChatState,
                strings = strings,
                usersById = state.usersById,
                onLeave = onLeaveCall,
                onToggleMute = onToggleMute,
                onToggleDeafen = onToggleDeafen,
                onMinimize = onToggleMinimize
            )
        }

        if (selectedMediaAttachment != null) {
            FullscreenMediaModal(
                attachment = selectedMediaAttachment!!.attachment,
                localPath = selectedMediaAttachment!!.localPath,
                onDismiss = { selectedMediaAttachment = null }
            )
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
    onOpenGroupInfo: (String) -> Unit,
    onDismissUpdate: () -> Unit,
    onDownloadUpdate: () -> Unit,
    onInstallUpdate: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleMinimize: () -> Unit,
    onLeaveCall: () -> Unit,
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
                connectionTitle = localizeConnectionTitle(state.connectionTitle, strings),
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

            if (state.voiceChatState.connectionState != ir.hienob.noveo.data.VoiceConnectionState.IDLE && state.voiceChatState.isMinimized) {
                VoiceChatTray(
                    state = state.voiceChatState,
                    strings = strings,
                    onExpand = onToggleMinimize,
                    onLeave = onLeaveCall,
                    onToggleMute = onToggleMute,
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
                    state = state,
                    strings = strings,
                    chats = chats,
                    users = users,
                    onOpenChat = onOpenChat,
                    onOpenContacts = onOpenContacts,
                    onOpenProfile = onOpenProfile,
                    onOpenGroupInfo = onOpenGroupInfo
                )
            } else {
                ChatListContent(state = state, strings = strings, chats = chats, onOpenChat = onOpenChat)
            }
        }
    }
}

private fun localizeConnectionTitle(rawTitle: String, strings: NoveoStrings): String {
    val title = rawTitle.trim()
    return when {
        title.isBlank() -> strings.brandName
        title in knownBrandTitles -> strings.brandName
        title in knownConnectingTitles -> strings.connecting
        title in knownUpdatingTitles -> strings.updating
        else -> rawTitle
    }
}

private val knownBrandTitles = setOf("Noveo", "Новео", "诺欧", "نوئو", "نوفيو")
private val knownConnectingTitles = setOf(
    "Connecting...",
    "Verbinden...",
    "Подключение...",
    "连接中...",
    "در حال اتصال...",
    "Conectando...",
    "Connexion...",
    "جاري الاتصال...",
    "Bağlanıyor..."
)
private val knownUpdatingTitles = setOf(
    "Updating...",
    "Обновление...",
    "更新中...",
    "در حال بروزرسانی..."
)

@Composable
private fun SearchResultsList(
    state: AppUiState,
    strings: NoveoStrings,
    chats: List<ChatSummary>,
    users: List<UserSummary>,
    onOpenChat: (String) -> Unit,
    onOpenContacts: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenGroupInfo: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (chats.isNotEmpty()) {
            items(chats) { chat ->
                ChatRow(
                    chat = chat, 
                    strings = strings, 
                    usersById = state.usersById,
                    currentUserId = state.session?.userId,
                    selected = false, 
                    onClick = { 
                        if (chat.chatType == "private") {
                            onOpenChat(chat.id)
                        } else {
                            onOpenGroupInfo(chat.id)
                        }
                    }
                )
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
            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
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
            items(
                chats, 
                key = { it.id },
                contentType = { "chat" }
            ) { chat ->
                ChatRow(
                    chat = chat,
                    strings = strings,
                    usersById = state.usersById,
                    currentUserId = state.session?.userId,
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
                    Surface(
                        modifier = Modifier.fillMaxWidth(0.88f).height(38.dp),
                        shape = RoundedCornerShape(19.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    ) {
                        Row(
                            Modifier.fillMaxSize().padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                singleLine = true,
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier.weight(1f),
                                decorationBox = { inner ->
                                    if (searchQuery.isBlank()) Text(strings.searchPlaceholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                                    inner()
                                }
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AnimatedContent(
                            targetState = connectionTitle,
                            label = "title_animation",
                            transitionSpec = {
                                (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                                    slideOutVertically { height -> height } + fadeOut())
                            }
                        ) { title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                lineHeight = 22.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .wrapContentHeight(Alignment.CenterVertically)
                                    .alpha(titleAlpha),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
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

private fun formatDuration(seconds: Int, strings: NoveoStrings): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins > 0) {
        "${localizeDigits(mins.toString(), strings.languageCode)}m ${localizeDigits(secs.toString(), strings.languageCode)}s"
    } else {
        "${localizeDigits(secs.toString(), strings.languageCode)}s"
    }
}

@Composable
private fun CallLogView(
    callLogJson: String,
    strings: NoveoStrings,
    ownMessage: Boolean,
    tgColors: TelegramThemeColors
) {
    val log = remember(callLogJson) { runCatching { org.json.JSONObject(callLogJson) }.getOrNull() } ?: return
    val type = log.optString("type").ifBlank { log.optString("status") }
    val duration = if (log.has("duration")) log.optInt("duration", 0) else log.optInt("durationSeconds", 0)
    
    val icon = when (type) {
        "outgoing" -> Icons.Outlined.Call
        "incoming" -> Icons.Outlined.Call
        "missed" -> Icons.Outlined.ErrorOutline
        "cancelled", "canceled" -> Icons.Outlined.Close
        "declined", "rejected" -> Icons.Outlined.Close
        else -> Icons.Outlined.Call
    }
    
    val label = when (type) {
        "outgoing" -> strings.outgoingCall
        "incoming" -> strings.incomingCall
        "missed" -> strings.missedCall
        "cancelled", "canceled" -> strings.cancelledCall
        "declined", "rejected" -> strings.declinedCall
        else -> strings.incomingCall
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink, 
            modifier = Modifier.size(20.dp).graphicsLayer {
                if (type == "outgoing") {
                    rotationZ = 45f
                } else if (type == "incoming") {
                    rotationZ = 225f
                }
            }
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                label, 
                fontWeight = FontWeight.Bold, 
                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText,
                fontSize = 15.sp
            )
            if (duration > 0) {
                Text(
                    formatDuration(duration, strings), 
                    style = MaterialTheme.typography.labelSmall, 
                    color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingText).copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun VerifiedIcon(modifier: Modifier = Modifier.size(14.dp)) {
    Box(
        modifier = modifier.background(Color(0xFF2EA6FF), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = "Verified",
            tint = Color.White,
            modifier = Modifier.matchParentSize().padding(2.dp)
        )
    }
}

@Composable
fun UserBadges(
    isVerified: Boolean,
    premiumStarIcon: PremiumStarIcon?,
    modifier: Modifier = Modifier,
    verifiedSize: Dp = 14.dp,
    starSize: Dp = 14.dp,
    spacing: Dp = 4.dp
) {
    if (!isVerified && (premiumStarIcon == null || premiumStarIcon.url.isNullOrBlank())) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        if (isVerified) {
            VerifiedIcon(modifier = Modifier.size(verifiedSize))
        }
        if (premiumStarIcon != null && !premiumStarIcon.url.isNullOrBlank()) {
            val normalizedUrl = remember(premiumStarIcon.url) { premiumStarIcon.url.normalizeNoveoUrl() }
            if (premiumStarIcon.type == "tgs") {
                TgsSticker(
                    url = normalizedUrl,
                    modifier = Modifier.size(starSize)
                )
            } else {
                AsyncImage(
                    model = normalizedUrl,
                    contentDescription = "Premium Star",
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}

@Composable
private fun MessageDateSeparator(
    label: String,
    tgColors: TelegramThemeColors
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = tgColors.incomingBubble.copy(alpha = if (tgColors.isDark) 0.56f else 0.82f),
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 0.5.dp
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                color = tgColors.headerSubtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
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
    onMediaClick: (ChatMessage, MessageFileAttachment) -> Unit,
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
    onCancelDownload: (ChatMessage) -> Unit,
    onCall: () -> Unit,
    onCancelUpload: () -> Unit,
    onSendSticker: (SavedSticker) -> Unit,
    onAddSavedSticker: (ChatMessage) -> Unit,
    onHandleClick: (String) -> Unit,
    onJoinChat: (String) -> Unit,
    onLeaveChat: (String) -> Unit,
    onBotCallback: (String, String, String) -> Unit,
    onConnectE2EE: () -> Unit,
    onEndE2EE: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleMinimize: () -> Unit,
    onLeaveCall: () -> Unit,
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

    val selectedTitle = remember(selectedChat, strings, state.session?.userId) {
        if (selectedChat?.isSavedMessagesChat(state.session?.userId) == true) strings.savedMessages
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
    
    val messages = state.messages
    val usersById = state.usersById
    val sessionUserId = state.session?.userId
    val attachmentDownloads = state.attachmentDownloads
    val currentAudioMessageId = state.currentAudioMessage?.id
    val currentAudioPlaying = state.isAudioPlaying
    val currentAudioProgress = state.audioProgress
    val doubleTapEmoji = state.doubleTapReaction.ifBlank { "❤" }

    val e2eeSession = selectedChat?.id?.let { state.e2eeSessions[it] }
    val e2eeActive = e2eeSession?.status == E2EESessionStatus.ACTIVE
    val e2eePending = e2eeSession?.status == E2EESessionStatus.PENDING
    val typingUsers = state.typingUsers[selectedChat?.id].orEmpty()
    
    val showScrollToBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            totalItems > 0 && lastVisibleItem >= 0 && (totalItems - 1 - lastVisibleItem) >= 10
        }
    }

    val typingText = remember(selectedChat?.chatType, typingUsers, state.usersById, strings) {
        if (typingUsers.isEmpty()) null
        else if (selectedChat?.chatType == "private") {
            strings.typingPrivate
        } else {
            val names = typingUsers.mapNotNull { state.usersById[it]?.username?.split(" ")?.firstOrNull() }
            when {
                names.isEmpty() -> strings.typingSomeone
                names.size == 1 -> "${names.first()} ${strings.typingSingle}"
                names.size == 2 -> "${names[0]} ${strings.typingDouble} ${names[1]}"
                else -> "${localizeDigits(names.size.toString(), strings.languageCode)} ${strings.typingMulti}"
            }
        }
    }

    val subtitle = remember(selectedChat, profileUser, isOnline, onlineCount, typingText, strings, messages.size, sessionUserId, e2eeActive, e2eePending) {
        if (selectedChat == null) return@remember ""
        if (e2eeActive) return@remember strings.e2eeActive
        if (e2eePending) return@remember strings.e2eeConnecting
        if (typingText != null) return@remember typingText
        val isSavedMessages = selectedChat.isSavedMessagesChat(sessionUserId)
        if (isSavedMessages) {
            formatMessagesCount(messages.size, strings)
        } else if (selectedChat.chatType == "private") {
            if (isOnline) strings.membersOnline
            else formatLastSeen(profileUser?.lastSeen, strings)
        } else {
            val onlineStr = localizeDigits(onlineCount.toString(), strings.languageCode)
            val totalMembersText = formatMembersCount(selectedChat.memberIds.size, strings)
            val rawSubtitle = if (onlineCount > 0) "$totalMembersText${strings.comma} $onlineStr ${strings.membersOnline}" else totalMembersText
            if (strings.languageCode == "fa" || strings.languageCode == "ar") "\u200F$rawSubtitle" else rawSubtitle
        }
    }

    var highlightedMessageId by remember { mutableStateOf<String?>(null) }
    var contextMenuState by remember { mutableStateOf<MessageContextMenuState?>(null) }
    var contextMenuExpanded by remember { mutableStateOf(false) }
    var showSeenByMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var showAttachPopup by remember { mutableStateOf(false) }
    var showStickers by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val imeVisible = WindowInsets.isImeVisible


    val canLoadOlder = selectedChat?.hasMoreHistory == true && !state.loading
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    
    LaunchedEffect(firstVisibleItemIndex) {
        if (firstVisibleItemIndex <= 2 && canLoadOlder && messages.isNotEmpty()) {
            onLoadOlder()
        }
    }

    LaunchedEffect(imeVisible) {
        if (imeVisible && showStickers) {
            showStickers = false
        }
    }

    // Force scroll to bottom when a chat is first opened
    LaunchedEffect(state.selectedChatId) {
        if (state.selectedChatId != null && messages.isNotEmpty()) {
            listState.scrollToItem(messages.lastIndex)
        }
    }

    // Handle history loading vs new messages
    val lastMessageId = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(messages.size) {
        if (messages.isEmpty()) return@LaunchedEffect
        val newLastId = messages.last().id
        if (lastMessageId.value != null && newLastId != lastMessageId.value) {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: messages.lastIndex
            val itemsFromBottom = messages.lastIndex - lastVisibleItem
            if (itemsFromBottom <= 2) {
                listState.scrollToItem(messages.lastIndex)
            } else if (itemsFromBottom < 10) {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }
        lastMessageId.value = newLastId
    }

    val tgColors = telegramColors()
    val onScrollToMessage = { messageId: String ->
        val index = messages.indexOfFirst { it.id == messageId }
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

    val density = LocalDensity.current

    val hasAudio = state.currentAudioMessage != null
    val hasVoice = state.voiceChatState.connectionState != ir.hienob.noveo.data.VoiceConnectionState.IDLE && state.voiceChatState.isMinimized
    val hasPinned = selectedChat?.pinnedMessage != null
    
    val topPadding = 56.dp + (if (hasAudio) 48.dp else 0.dp) + (if (hasVoice) 48.dp else 0.dp) + (if (hasPinned) 48.dp else 0.dp) + 8.dp

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(tgColors.chatSurface)) {
        val maxBubbleWidth = maxWidth * 0.78f

        // 1. Messages Layer
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 8.dp, 
                top = topPadding, 
                end = 8.dp, 
                bottom = 90.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(
                items = messages,
                key = { _, message -> message.id },
                contentType = { _, msg ->
                    when {
                        msg.senderId == "system" -> "system"
                        msg.content.file?.isSticker() == true -> "sticker"
                        msg.content.file?.isAudio() == true -> "audio"
                        msg.content.file != null -> "attachment"
                        msg.content.inlineKeyboard.isNotEmpty() -> "bot_keyboard"
                        else -> "message"
                    }
                }
            ) { index, message ->
                val prevMessage = messages.getOrNull(index - 1)
                val nextMessage = messages.getOrNull(index + 1)
                if (prevMessage == null || messageDayKey(prevMessage.timestamp) != messageDayKey(message.timestamp)) {
                    MessageDateSeparator(
                        label = formatMessageDateSeparator(message.timestamp, strings),
                        tgColors = tgColors
                    )
                }
                val ownMessage = message.senderId == sessionUserId
                val showSenderInfo = prevMessage == null ||
                    prevMessage.senderId != message.senderId ||
                    (message.timestamp - prevMessage.timestamp) > 300 ||
                    prevMessage.senderId == "system"
                val hasTail = nextMessage == null ||
                    nextMessage.senderId != message.senderId ||
                    (nextMessage.timestamp - message.timestamp) > 300 ||
                    nextMessage.senderId == "system"
                val senderAvatarUrl = state.usersById[message.senderId]?.avatarUrl
                val repliedMessage = message.replyToId?.let { replyId ->
                    state.messages.firstOrNull { it.id == replyId }
                }
                val isSenderVerified = state.usersById[message.senderId]?.isVerified == true

                MessageRow(
                    strings = strings,
                    message = message,
                    ownMessage = ownMessage,
                    senderAvatarUrl = senderAvatarUrl,
                    showSenderInfo = showSenderInfo,
                    hasTail = hasTail,
                    isGroupChat = selectedChat?.chatType != "private",
                    currentUserId = currentUserId,
                    usersById = usersById,
                    onMediaClick = onMediaClick,
                    onOpenProfile = onOpenProfile,
                    repliedMessage = repliedMessage,
                    isSenderVerified = isSenderVerified,
                    maxBubbleWidth = maxBubbleWidth,
                    onReply = { onReply(message) },
                    onToggleReaction = onToggleReaction,
                    onOpenContextMenu = { bubbleBounds ->
                        if (message.e2ee) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast.makeText(context, strings.e2eeSecretAlert, Toast.LENGTH_SHORT).show()
                        } else {
                            contextMenuState = MessageContextMenuState(
                                message = message,
                                ownMessage = ownMessage,
                                bubbleBounds = bubbleBounds
                            )
                            contextMenuExpanded = false
                        }
                    },
                    onScrollToMessage = onScrollToMessage,
                    onPlayAudio = onPlayAudio,
                    onPauseAudio = onPauseAudio,
                    onResumeAudio = onResumeAudio,
                    onStopAudio = onStopAudio,
                    onSeekAudio = onSeekAudio,
                    doubleTapReaction = doubleTapEmoji,
                    onDownloadFile = onDownloadFile,
                    onCancelDownload = onCancelDownload,
                    onHandleClick = onHandleClick,
                    onSendCommand = onSend,
                    onBotCallback = onBotCallback,
                    currentAudioMessageId = currentAudioMessageId,
                    isAudioPlaying = currentAudioMessageId == message.id && currentAudioPlaying,
                    audioProgress = if (currentAudioMessageId == message.id) currentAudioProgress else 0f,
                    attachmentDownloadState = message.content.file?.let { attachmentDownloads[it.downloadKey()] },
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
                            if (profileUserId != null) onOpenProfile(profileUserId)
                            else if (selectedChat?.chatType == "private") profileUserId?.let(onOpenProfile)
                            else if (selectedChat != null) onOpenGroupInfo()
                        }
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileCircle(
                        name = selectedTitle,
                        imageUrl = selectedChat?.avatarUrl,
                        isSavedMessages = selectedChat?.id?.startsWith("saved_") == true,
                        size = 40.dp,
                        modifier = Modifier.clickable {
                            profileUserId?.let { onOpenProfile(it) }
                                ?: if (selectedChat?.chatType != "private") onOpenGroupInfo() else Unit
                        }
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        val nicknameFont = profileUserId?.let { state.usersById[it]?.nicknameFont }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                selectedTitle,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = tgColors.headerTitle,
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                fontFamily = getNicknameFontFamily(nicknameFont),
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            UserBadges(
                                isVerified = selectedChat?.isVerified == true || profileUser?.isVerified == true,
                                premiumStarIcon = profileUser?.premiumStarIcon,
                                modifier = Modifier.padding(start = 4.dp),
                                verifiedSize = 14.dp,
                                starSize = 14.dp
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            subtitle,
                            color = tgColors.headerSubtitle,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                if (selectedChat?.chatType == "private" && selectedChat?.isSavedMessagesChat(sessionUserId) != true) {
                    HeaderIconButton(
                        icon = Icons.Outlined.Lock,
                        onClick = {
                            when {
                                e2eeActive || e2eePending -> onEndE2EE()
                                !isOnline -> {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    Toast.makeText(context, strings.e2eeOfflineAlert, Toast.LENGTH_SHORT).show()
                                }
                                else -> onConnectE2EE()
                            }
                        },
                        tint = if (e2eeActive) Color(0xFF10B981) else tgColors.headerIcon
                    )
                }
                HeaderIconButton(icon = Icons.Outlined.Call, onClick = onCall, tint = tgColors.headerIcon)
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

        if (hasVoice) {
            val voiceOffset = 56.dp + (if (hasAudio) 48.dp else 0.dp)
            Box(modifier = Modifier.padding(top = voiceOffset)) {
                VoiceChatTray(
                    state = state.voiceChatState,
                    strings = strings,
                    onExpand = onToggleMinimize,
                    onLeave = onLeaveCall,
                    onToggleMute = onToggleMute,
                    tgColors = tgColors
                )
            }
        }

        // 2.1 Pinned Message Bar
        selectedChat?.pinnedMessage?.let { pinned ->
            val pinnedOffset = 56.dp + (if (hasAudio) 48.dp else 0.dp) + (if (hasVoice) 48.dp else 0.dp)
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
                            if (messages.isNotEmpty()) {
                                val targetIndex = messages.lastIndex
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
        val isMember = selectedChat?.memberIds?.contains(currentUserId) == true
        if (selectedChat?.canChat != false && (selectedChat?.chatType == "private" || isMember)) {
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
                        isSendingMessage = state.isSendingMessage,
                        onCancelSend = onCancelUpload,
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
        } else if (selectedChat != null && selectedChat.chatType != "private" && !isMember) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                    .padding(bottom = 4.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable { onJoinChat(selectedChat!!.id) },
                        shape = RoundedCornerShape(24.dp),
                        color = tgColors.composerField,
                        shadowElevation = 1.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                strings.join,
                                color = tgColors.composerBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                    }
                    
                    Spacer(Modifier.width(8.dp))
                    
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onJoinChat(selectedChat!!.id) },
                        shape = CircleShape,
                        color = tgColors.composerField,
                        shadowElevation = 1.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = tgColors.composerBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
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
                    onSeenBy = {
                        contextMenuState = null
                        contextMenuExpanded = false
                        showSeenByMessage = menuState.message
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

        ModalHost(visible = showSeenByMessage != null, onDismiss = { showSeenByMessage = null }) {
            showSeenByMessage?.let { msg ->
                SeenByModal(
                    strings = strings,
                    message = msg,
                    usersById = state.usersById,
                    onClose = { showSeenByMessage = null },
                    onOpenProfile = onOpenProfile
                )
            }
        }

    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
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
    usersById: Map<String, UserSummary>,
    onMediaClick: (ChatMessage, MessageFileAttachment) -> Unit,
    onOpenProfile: (String) -> Unit,
    repliedMessage: ChatMessage? = null,
    isSenderVerified: Boolean = false,
    maxBubbleWidth: Dp,
    onReply: () -> Unit,
    onToggleReaction: (String, String) -> Unit,
    onOpenContextMenu: (Rect) -> Unit = {},
    onScrollToMessage: (String) -> Unit,
    onPlayAudio: (ChatMessage) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    doubleTapReaction: String,
    onDownloadFile: (ChatMessage) -> Unit,
    onCancelDownload: (ChatMessage) -> Unit,
    onHandleClick: (String) -> Unit,
    onSendCommand: ((String) -> Unit)? = null,
    onBotCallback: (String, String, String) -> Unit,
    currentAudioMessageId: String?,
    isAudioPlaying: Boolean,
    audioProgress: Float,
    attachmentDownloadState: ir.hienob.noveo.app.AttachmentDownloadState?,
    isHighlighted: Boolean = false,
    tgColors: TelegramThemeColors = telegramColors()
) {
    val haptic = LocalHapticFeedback.current
    val isSystem = message.senderId == "system"
    val isAnonymous = message.senderId == "anonymous" || message.chatType == "channel"
    val isCallLog = !message.content.callLog.isNullOrBlank()
    if (isSystem && !isCallLog) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Surface(
                color = tgColors.chatServiceBackground,
                shape = CircleShape
            ) {
                Text(
                    message.content.text ?: strings.noMessagesYet,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = tgColors.chatServiceText,
                    textAlign = TextAlign.Center,
                    softWrap = false
                )
            }
        }
        return
    }

    val timeStr = remember(message.timestamp, strings.languageCode) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        localizeDigits(sdf.format(Date(message.timestamp * 1000)), strings.languageCode)
    }
    val bubbleBoundsRef = remember(message.id) { arrayOfNulls<Rect>(1) }

    val animatePendingIntro = ownMessage && message.pending
    val pendingIntroFraction = if (animatePendingIntro) {
        var pendingIntroStarted by remember(message.id) { mutableStateOf(false) }
        LaunchedEffect(message.id) { pendingIntroStarted = true }
        val pendingIntroProgress by animateFloatAsState(
            targetValue = if (pendingIntroStarted) 1f else 0f,
            animationSpec = tween(140, easing = FastOutSlowInEasing),
            label = "message_pending_intro"
        )
        pendingIntroProgress
    } else {
        1f
    }

    // Swipe state
    var swipeOffset by remember(message.id) { mutableStateOf(0f) }
    val rowTransformModifier = if (animatePendingIntro || swipeOffset != 0f) {
        Modifier.graphicsLayer {
            translationY = (1f - pendingIntroFraction) * 18f
            translationX = ((1f - pendingIntroFraction) * 26f) + swipeOffset
            alpha = 0.35f + (0.65f * pendingIntroFraction)
            scaleX = 0.92f + (0.08f * pendingIntroFraction)
            scaleY = 0.92f + (0.08f * pendingIntroFraction)
            transformOrigin = TransformOrigin(if (ownMessage) 1f else 0f, 1f)
        }
    } else {
        Modifier
    }

    Column(
        horizontalAlignment = if (ownMessage) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (showSenderInfo) 10.dp else 0.dp)
            .padding(bottom = if (hasTail) 6.dp else 0.dp)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { bubbleBoundsRef[0]?.let(onOpenContextMenu) },
                onDoubleClick = { onToggleReaction(message.id, doubleTapReaction) },
                onLongClick = { bubbleBoundsRef[0]?.let(onOpenContextMenu) }
            )
            .then(rowTransformModifier)
    ) {
        val bubbleModifier = Modifier
            .onGloballyPositioned { bubbleBoundsRef[0] = it.boundsInRoot() }
            .pointerInput(message.id) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        if (dragAmount < 0) { // Only swipe left
                            val current = swipeOffset
                            val target = (current + dragAmount).coerceIn(-100f, 0f)
                            if (current > -60f && target <= -60f) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            swipeOffset = target
                        }
                    },
                    onDragEnd = {
                        if (swipeOffset < -60f) {
                            onReply()
                        }
                        swipeOffset = 0f
                    },
                    onDragCancel = {
                        swipeOffset = 0f
                    }
                )
            }

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            if (!ownMessage) {
                // Telegram only shows avatar in group chats, and only for the last message in a group
                if (isGroupChat && !isAnonymous) {
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
                val emojiTgsUrl = remember(message.content.text) {
                    message.content.text?.let { EmojiTgsManager.getTgsUrlForEmoji(it) }
                }
                val isSticker = (message.content.file?.isSticker() == true) || (emojiTgsUrl != null)
                
                if (isSticker) {
                    val file = message.content.file
                    val emojiTgsUrlState = remember(message.content.text) {
                        message.content.text?.let { EmojiTgsManager.getTgsUrlForEmoji(it) }
                    }
                    val normalizedUrl = remember(file?.url, emojiTgsUrlState) { 
                        emojiTgsUrlState ?: file?.url.normalizeNoveoUrl() ?: ""
                    }
                    Box(
                        modifier = bubbleModifier.padding(vertical = 4.dp)
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
                                            val replySenderFont = repliedMessage?.let { usersById[it.senderId]?.nicknameFont }
                                            Text(
                                                text = repliedMessage.senderName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                                fontWeight = FontWeight.Bold,
                                                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                                fontFamily = getNicknameFontFamily(replySenderFont),
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

                            val isTgs = emojiTgsUrlState != null || file?.isTgsSticker() == true
                            if (isTgs) {
                                TgsSticker(
                                    url = normalizedUrl,
                                    modifier = Modifier.size(if (emojiTgsUrlState != null) 80.dp else 160.dp),
                                    tint = Color.White,
                                    iterations = if (emojiTgsUrlState != null) 1 else com.airbnb.lottie.compose.LottieConstants.IterateForever
                                )
                            } else {
                                AsyncImage(
                                    model = normalizedUrl,
                                    contentDescription = "sticker",
                                    modifier = Modifier.size(160.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

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
                        modifier = bubbleModifier
                            .widthIn(max = maxBubbleWidth),
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
                            if (!ownMessage && isGroupChat && showSenderInfo && !isAnonymous) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)) {
                                    val senderFont = usersById[message.senderId]?.nicknameFont
                                    Text(
                                        message.senderName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                        color = tgColors.incomingLink,
                                        fontFamily = getNicknameFontFamily(senderFont),
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    UserBadges(
                                        isVerified = isSenderVerified,
                                        premiumStarIcon = usersById[message.senderId]?.premiumStarIcon,
                                        modifier = Modifier.padding(start = 4.dp),
                                        verifiedSize = 13.dp,
                                        starSize = 13.dp
                                    )
                                }
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
                                            val replySenderFont = repliedMessage?.let { usersById[it.senderId]?.nicknameFont }
                                            Text(
                                                text = repliedMessage.senderName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                                fontWeight = FontWeight.Bold,
                                                color = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink,
                                                fontFamily = getNicknameFontFamily(replySenderFont),
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
                                        isCurrent = currentAudioMessageId == message.id,
                                        isPlaying = isAudioPlaying,
                                        progress = audioProgress,
                                        onPlayToggle = { onPlayAudio(message) },
                                        onSeek = onSeekAudio,
                                        tgColors = tgColors
                                    )
                                } else {
                                    MessageAttachment(
                                        file = file,
                                        downloadState = attachmentDownloadState,
                                        ownMessage = ownMessage,
                                        onClick = { onMediaClick(message, file) },
                                        onDownloadClick = { onDownloadFile(message) },
                                        onCancelClick = { onCancelDownload(message) },
                                        tgColors = tgColors
                                    )
                                }
                            }
                            
                            val callLog = message.content.callLog
                            if (!callLog.isNullOrBlank()) {
                                CallLogView(callLog, strings, ownMessage, tgColors)
                            }

                            val caption = message.content.text

                            if (!caption.isNullOrBlank() && !(callLog != null && caption.equals("Call", ignoreCase = true))) {
                                if (message.content.file != null) Spacer(Modifier.height(4.dp))
                                Box(modifier = Modifier.padding(horizontal = if (hasVisualMedia) 6.dp else 4.dp)) {
                                    MarkdownText(
                                        text = caption,
                                        color = if (ownMessage) tgColors.outgoingText else tgColors.incomingText,
                                        onHandleClick = onHandleClick,
                                        onSendCommand = onSendCommand,
                                        strings = strings
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
                                        strings.edited,
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

                if (message.content.inlineKeyboard.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .widthIn(max = maxBubbleWidth),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        message.content.inlineKeyboard.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                row.forEach { button ->
                                    Button(
                                        onClick = { 
                                            button.callbackData?.let { data ->
                                                onBotCallback(message.chatId, message.id, data)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (ownMessage) tgColors.outgoingBubble.copy(alpha = 0.8f) else tgColors.incomingBubble.copy(alpha = 0.8f),
                                            contentColor = if (ownMessage) tgColors.outgoingText else tgColors.incomingText
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                                    ) {
                                        Text(
                                            text = button.text,
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
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

private const val LONG_MESSAGE_COLLAPSE_CHAR_LIMIT = 900
private const val LONG_MESSAGE_COLLAPSE_MAX_LINES = 28

private fun showMoreText(strings: NoveoStrings): String = when (strings.languageCode) {
    "fa" -> "نمایش بیشتر"
    "fr" -> "Afficher plus"
    "de" -> "Mehr anzeigen"
    "ru" -> "Показать больше"
    "zh" -> "显示更多"
    else -> "Show more"
}

private fun showLessText(strings: NoveoStrings): String = when (strings.languageCode) {
    "fa" -> "نمایش کمتر"
    "fr" -> "Afficher moins"
    "de" -> "Weniger anzeigen"
    "ru" -> "Скрыть"
    "zh" -> "收起"
    else -> "Show less"
}

sealed class MessageChunk {
    data class Text(val content: String) : MessageChunk()
    data class CodeBlock(val language: String, val code: String) : MessageChunk()
}

private fun parseMessageChunks(text: String): List<MessageChunk> {
    val chunks = mutableListOf<MessageChunk>()
    var index = 0
    while (index < text.length) {
        val startCode = text.indexOf("```", index)
        if (startCode == -1) {
            chunks.add(MessageChunk.Text(text.substring(index)))
            break
        }
        
        if (startCode > index) {
            chunks.add(MessageChunk.Text(text.substring(index, startCode)))
        }
        
        val endCode = text.indexOf("```", startCode + 3)
        if (endCode != -1) {
            val block = text.substring(startCode + 3, endCode)
            val firstNewLine = block.indexOf('\n')
            val (lang, code) = if (firstNewLine != -1) {
                val potentialLang = block.substring(0, firstNewLine).trim()
                if (potentialLang.all { it.isLetterOrDigit() }) {
                    potentialLang to block.substring(firstNewLine + 1)
                } else {
                    "" to block
                }
            } else {
                "" to block
            }
            chunks.add(MessageChunk.CodeBlock(lang, code.trim('\n', '\r')))
            index = endCode + 3
        } else {
            chunks.add(MessageChunk.Text(text.substring(startCode)))
            break
        }
    }
    return chunks
}

private fun getUrlWarningTitle(lang: String): String = when (lang) {
    "fa" -> "لینک خارجی"
    "fr" -> "Lien externe"
    "de" -> "Externer Link"
    "ru" -> "Внешняя ссылка"
    "zh" -> "外部链接"
    "es" -> "Enlace externo"
    "ar" -> "رابط خارجي"
    "tr" -> "Dış Bağlantı"
    else -> "External Link"
}

private fun getUrlWarningText(lang: String, url: String): String = when (lang) {
    "fa" -> "این لینک به آدرس زیر می‌رود:\n$url\nآیا مطمئن هستید که می‌خواهید آن را باز کنید؟"
    "fr" -> "Cette URL mène à :\n$url\nÊtes-vous sûr de vouloir l'ouvrir ?"
    "de" -> "Diese URL führt zu:\n$url\nSind Sie sicher, dass Sie sie öffnen möchten?"
    "ru" -> "Этот URL ведёт на:\n$url\nВы уверены, что хотите открыть его?"
    "zh" -> "此 URL 指向：\n$url\n您确定要打开它吗？"
    "es" -> "Este enlace lleva a:\n$url\n¿Está seguro de que desea abrirlo?"
    "ar" -> "هذا الرابط يؤدي إلى:\n$url\nهل أنت متأكد أنك تريد فتحه؟"
    "tr" -> "Bu bağlantı şuraya gidiyor:\n$url\nAçmak istediğinizden emin misiniz?"
    else -> "This URL goes to:\n$url\nAre you sure you want to open it?"
}

private fun getYesText(lang: String): String = when (lang) {
    "fa" -> "بله"
    "fr" -> "Oui"
    "de" -> "Ja"
    "ru" -> "Да"
    "zh" -> "确定"
    "es" -> "Sí"
    "ar" -> "نعم"
    "tr" -> "Evet"
    else -> "Yes"
}

@Composable
private fun CodeBlockView(
    code: String,
    language: String,
    strings: NoveoStrings?
) {
    val context = LocalContext.current
    val isLong = remember(code) { code.lines().size > 6 || code.length > 250 }
    var maximized by remember { mutableStateOf(!isLong) }
    
    val visibleCode = remember(code, maximized, isLong) {
        if (isLong && !maximized) {
            code.lines().take(6).joinToString("\n") + "\n..."
        } else {
            code
        }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        border = BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D2D2D))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.ifBlank { "code" }.uppercase(),
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Copied Code", code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color(0xFF888888),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Copy",
                        color = Color(0xFF888888),
                        fontSize = 11.sp
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = visibleCode,
                    color = Color(0xFFD4D4D4),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                )
            }
            
            if (isLong) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF252526))
                        .clickable { maximized = !maximized }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (maximized) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF9CDCFE),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (maximized) {
                                when (strings?.languageCode) {
                                    "fa" -> "کوچک کردن"
                                    "fr" -> "Réduire"
                                    "de" -> "Minimieren"
                                    "ru" -> "Свернуть"
                                    "zh" -> "收起"
                                    else -> "Minimize"
                                }
                            } else {
                                when (strings?.languageCode) {
                                    "fa" -> "بزرگ کردن"
                                    "fr" -> "Agrandir"
                                    "de" -> "Maximieren"
                                    "ru" -> "Развернуть"
                                    "zh" -> "展开"
                                    else -> "Maximize"
                                }
                            },
                            color = Color(0xFF9CDCFE),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkdownTextChunk(
    visibleText: String,
    color: Color,
    onHandleClick: ((String) -> Unit)?,
    onSendCommand: ((String) -> Unit)?,
    strings: NoveoStrings?,
    maxLines: Int,
    urlToConfirm: (String) -> Unit
) {
    val handleColor = if (color == MaterialTheme.colorScheme.onSurface) MaterialTheme.colorScheme.primary else color.copy(alpha = 0.95f)
    val urlRegex = remember { Regex("""\b(?:https?://|www\.)[^\s()<>]+(?:\([\w\d]+\)|[^\s`!()\[\]{};:'".,<>?«»“”‘’])""", RegexOption.IGNORE_CASE) }
    
    val hasHandle = onHandleClick != null && visibleText.indexOf('@') >= 0
    val hasCommand = onSendCommand != null && visibleText.indexOf('/') >= 0
    val hasRegularUrl = visibleText.indexOf("http://") >= 0 || visibleText.indexOf("https://") >= 0 || visibleText.indexOf("www.") >= 0
    val hasLink = (visibleText.indexOf('[') >= 0 && visibleText.indexOf(']') >= 0 && visibleText.indexOf('(') >= 0 && visibleText.indexOf(')') >= 0) || hasRegularUrl
    val hasClickableElement = hasHandle || hasCommand || hasLink
    val hasFormattingMarkup = visibleText.indexOf('*') >= 0

    if (!hasClickableElement && !hasFormattingMarkup) {
        Text(
            text = visibleText,
            style = TextStyle(color = color, fontSize = 16.sp, lineHeight = 20.sp),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    } else {
        val annotated = remember(visibleText, handleColor, hasHandle, hasCommand, hasLink) {
            buildAnnotatedString {
                var index = 0
                while (index < visibleText.length) {
                    val nextMarker = visibleText.indexOf("**", index)
                    
                    var pos = index
                    var nextItalic = -1
                    while (pos < visibleText.length) {
                        val star = visibleText.indexOf('*', pos)
                        if (star == -1) break
                        val isDoublePrev = star > 0 && visibleText[star - 1] == '*'
                        val isDoubleNext = star + 1 < visibleText.length && visibleText[star + 1] == '*'
                        if (!isDoublePrev && !isDoubleNext) {
                            nextItalic = star
                            break
                        }
                        pos = star + 1
                    }

                    val nextHandle = if (hasHandle) visibleText.indexOf("@", index) else -1
                    val nextSlash = if (hasCommand) visibleText.indexOf("/", index) else -1
                    val nextLink = if (hasLink) visibleText.indexOf("[", index) else -1
                    
                    var nextUrlIndex = -1
                    var urlText = ""
                    val urlMatch = urlRegex.find(visibleText, index)
                    if (urlMatch != null) {
                        nextUrlIndex = urlMatch.range.first
                        urlText = urlMatch.value
                    }

                    val markers = mutableListOf<Pair<Int, String>>()
                    if (nextMarker != -1) markers.add(nextMarker to "**")
                    if (nextItalic != -1) markers.add(nextItalic to "*")
                    if (nextHandle != -1) markers.add(nextHandle to "@")
                    if (nextSlash != -1) markers.add(nextSlash to "/")
                    if (nextLink != -1) markers.add(nextLink to "[")
                    if (nextUrlIndex != -1) markers.add(nextUrlIndex to "URL")

                    val nearest = markers.minByOrNull { it.first }

                    if (nearest == null) {
                        append(visibleText.substring(index))
                        break
                    }

                    if (nearest.first > index) {
                        append(visibleText.substring(index, nearest.first))
                    }

                    when (nearest.second) {
                        "**" -> {
                            val endBold = visibleText.indexOf("**", nearest.first + 2)
                            if (endBold != -1) {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(visibleText.substring(nearest.first + 2, endBold))
                                }
                                index = endBold + 2
                            } else {
                                append("**")
                                index = nearest.first + 2
                            }
                        }
                        "*" -> {
                            var pos2 = nearest.first + 1
                            var endItalic = -1
                            while (pos2 < visibleText.length) {
                                val star = visibleText.indexOf('*', pos2)
                                if (star == -1) break
                                val isDoublePrev = star > 0 && visibleText[star - 1] == '*'
                                val isDoubleNext = star + 1 < visibleText.length && visibleText[star + 1] == '*'
                                if (!isDoublePrev && !isDoubleNext) {
                                    endItalic = star
                                    break
                                }
                                pos2 = star + 1
                            }
                            if (endItalic != -1) {
                                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                    append(visibleText.substring(nearest.first + 1, endItalic))
                                }
                                index = endItalic + 1
                            } else {
                                append("*")
                                index = nearest.first + 1
                            }
                        }
                        "@" -> {
                            var endHandle = nearest.first + 1
                            while (endHandle < visibleText.length && (visibleText[endHandle].isLetterOrDigit() || visibleText[endHandle] == '_')) {
                                endHandle++
                            }
                            if (endHandle > nearest.first + 1) {
                                val handle = visibleText.substring(nearest.first, endHandle)
                                pushStringAnnotation("handle", handle)
                                withStyle(SpanStyle(color = handleColor, fontWeight = FontWeight.SemiBold)) {
                                    append(handle)
                                }
                                pop()
                                index = endHandle
                            } else {
                                append("@")
                                index = nearest.first + 1
                            }
                        }
                        "/" -> {
                            val isPrecededByWhitespace = nearest.first == 0 || visibleText[nearest.first - 1].isWhitespace()
                            var endCommand = nearest.first + 1
                            while (endCommand < visibleText.length && (visibleText[endCommand].isLetterOrDigit() || visibleText[endCommand] == '_')) {
                                endCommand++
                            }
                            if (isPrecededByWhitespace && endCommand > nearest.first + 1) {
                                val command = visibleText.substring(nearest.first, endCommand)
                                pushStringAnnotation("command", command)
                                withStyle(SpanStyle(color = handleColor, fontWeight = FontWeight.SemiBold)) {
                                    append(command)
                                }
                                pop()
                                index = endCommand
                            } else {
                                append("/")
                                index = nearest.first + 1
                            }
                        }
                        "[" -> {
                            val endTitle = visibleText.indexOf("](", nearest.first + 1)
                            if (endTitle != -1) {
                                val endUrl = visibleText.indexOf(")", endTitle + 2)
                                if (endUrl != -1) {
                                    val title = visibleText.substring(nearest.first + 1, endTitle)
                                    val url = visibleText.substring(endTitle + 2, endUrl)
                                    
                                    pushStringAnnotation("link", url)
                                    withStyle(SpanStyle(color = handleColor, fontWeight = FontWeight.SemiBold, textDecoration = TextDecoration.Underline)) {
                                        append(title)
                                    }
                                    pop()
                                    index = endUrl + 1
                                } else {
                                    append("[")
                                    index = nearest.first + 1
                                }
                            } else {
                                append("[")
                                index = nearest.first + 1
                            }
                        }
                        "URL" -> {
                            pushStringAnnotation("link", urlText)
                            withStyle(SpanStyle(color = handleColor, fontWeight = FontWeight.SemiBold, textDecoration = TextDecoration.Underline)) {
                                append(urlText)
                            }
                            pop()
                            index = nearest.first + urlText.length
                        }
                    }
                }
            }
        }

        if (hasClickableElement) {
            val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
            Text(
                text = annotated,
                style = TextStyle(color = color, fontSize = 16.sp, lineHeight = 20.sp),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { layoutResult.value = it },
                modifier = Modifier.pointerInput(annotated, onHandleClick, onSendCommand, urlToConfirm) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: continue
                            if (event.type == PointerEventType.Press) {
                                val layout = layoutResult.value ?: continue
                                val position = layout.getOffsetForPosition(change.position)
                                val handleAnnotation = annotated.getStringAnnotations("handle", position, position).firstOrNull()
                                val commandAnnotation = annotated.getStringAnnotations("command", position, position).firstOrNull()
                                val linkAnnotation = annotated.getStringAnnotations("link", position, position).firstOrNull()

                                if (handleAnnotation != null) {
                                    change.consume()
                                    val up = waitForUpOrCancellation()
                                    if (up != null) {
                                        up.consume()
                                        onHandleClick?.invoke(handleAnnotation.item)
                                    }
                                } else if (commandAnnotation != null) {
                                    change.consume()
                                    val up = waitForUpOrCancellation()
                                    if (up != null) {
                                        up.consume()
                                        onSendCommand?.invoke(commandAnnotation.item)
                                    }
                                } else if (linkAnnotation != null) {
                                    change.consume()
                                    val up = waitForUpOrCancellation()
                                    if (up != null) {
                                        up.consume()
                                        urlToConfirm(linkAnnotation.item)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        } else {
            Text(
                text = annotated,
                style = TextStyle(color = color, fontSize = 16.sp, lineHeight = 20.sp),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MarkdownText(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onHandleClick: ((String) -> Unit)? = null,
    onSendCommand: ((String) -> Unit)? = null,
    strings: NoveoStrings? = null,
    collapseLongText: Boolean = true
) {
    val context = LocalContext.current
    val handleColor = if (color == MaterialTheme.colorScheme.onSurface) MaterialTheme.colorScheme.primary else color.copy(alpha = 0.95f)
    val shouldCollapse = collapseLongText && text.length > LONG_MESSAGE_COLLAPSE_CHAR_LIMIT
    var expanded by remember(text) { mutableStateOf(false) }
    val visibleText = remember(text, shouldCollapse, expanded) {
        if (shouldCollapse && !expanded) {
            text.take(LONG_MESSAGE_COLLAPSE_CHAR_LIMIT).trimEnd() + "…"
        } else {
            text
        }
    }
    val maxLines = if (shouldCollapse && !expanded) LONG_MESSAGE_COLLAPSE_MAX_LINES else Int.MAX_VALUE
    
    val chunks = remember(visibleText) { parseMessageChunks(visibleText) }
    var urlToConfirm by remember { mutableStateOf<String?>(null) }

    Column {
        chunks.forEach { chunk ->
            when (chunk) {
                is MessageChunk.Text -> {
                    MarkdownTextChunk(
                        visibleText = chunk.content,
                        color = color,
                        onHandleClick = onHandleClick,
                        onSendCommand = onSendCommand,
                        strings = strings,
                        maxLines = maxLines,
                        urlToConfirm = { urlToConfirm = it }
                    )
                }
                is MessageChunk.CodeBlock -> {
                    CodeBlockView(
                        code = chunk.code,
                        language = chunk.language,
                        strings = strings
                    )
                }
            }
        }

        if (shouldCollapse && strings != null) {
            Text(
                text = if (expanded) showLessText(strings) else showMoreText(strings),
                style = TextStyle(
                    color = handleColor,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        }
    }

    if (urlToConfirm != null) {
        val url = urlToConfirm!!
        val lang = strings?.languageCode ?: "en"
        AlertDialog(
            onDismissRequest = { urlToConfirm = null },
            title = { Text(getUrlWarningTitle(lang)) },
            text = { Text(getUrlWarningText(lang, url)) },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val targetUrl = if (!url.startsWith("http://", ignoreCase = true) && !url.startsWith("https://", ignoreCase = true)) {
                                "https://$url"
                            } else {
                                url
                            }
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(targetUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open URL", Toast.LENGTH_SHORT).show()
                        }
                        urlToConfirm = null
                    }
                ) {
                    Text(getYesText(lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { urlToConfirm = null }) {
                    Text(strings?.cancel ?: "Cancel")
                }
            }
        )
    }
}

@Composable
private fun MessageAttachment(
    file: MessageFileAttachment,
    downloadState: ir.hienob.noveo.app.AttachmentDownloadState?,
    ownMessage: Boolean,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onCancelClick: () -> Unit,
    tgColors: TelegramThemeColors = telegramColors()
) {
    val context = LocalContext.current
    val localPath = downloadState?.localPath
    val cacheFile = remember(file.url, file.name, file.type) { localAttachmentCacheFile(context.filesDir, file) }
    val localFile = remember(localPath, cacheFile.absolutePath) {
        localPath?.let(::File)?.takeIf { it.exists() } ?: cacheFile.takeIf { it.exists() }
    }
    val isDownloaded = localFile != null
    val isDownloading = downloadState?.isDownloading == true
    val progress = downloadState?.progress ?: 0f
    val overlayTint = if (ownMessage) tgColors.outgoingText else tgColors.incomingLink

    if (file.isImage()) {
        var imageLoaded by remember(localFile?.absolutePath) { mutableStateOf(false) }
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(bottom = 2.dp)
                .fillMaxWidth()
                .heightIn(max = 340.dp)
                .clickable(enabled = isDownloaded) { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp)
                    .background((if (ownMessage) tgColors.outgoingBubble else tgColors.incomingBubble).copy(alpha = 0.68f))
            ) {
                if (localFile != null) {
                    AsyncImage(
                        model = localFile,
                        contentDescription = file.name,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        onSuccess = { imageLoaded = true }
                    )
                }

                if (!imageLoaded || isDownloading || localFile == null) {
                    AttachmentDownloadOverlay(
                        isVideo = false,
                        isDownloaded = isDownloaded,
                        isDownloading = isDownloading,
                        progress = progress,
                        tint = overlayTint,
                        onDownloadClick = onDownloadClick,
                        onCancelClick = onCancelClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    } else if (file.isVideo()) {
        Surface(
            modifier = Modifier
                .padding(bottom = 2.dp)
                .fillMaxWidth()
                .heightIn(min = 180.dp)
                .clickable(enabled = isDownloaded) { onClick() },
            color = (if (ownMessage) tgColors.outgoingText else tgColors.incomingLink).copy(alpha = 0.08f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                tgColors.incomingBubble.copy(alpha = 0.85f),
                                tgColors.chatSurface.copy(alpha = 0.95f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        tint = overlayTint.copy(alpha = 0.8f),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(Modifier.height(8.dp))
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
                AttachmentDownloadOverlay(
                    isVideo = true,
                    isDownloaded = isDownloaded,
                    isDownloading = isDownloading,
                    progress = progress,
                    tint = overlayTint,
                    onDownloadClick = onDownloadClick,
                    onCancelClick = onCancelClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    } else {
        val normalizedUrl = remember(file.url) { file.url.normalizeNoveoUrl() }
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
                        imageVector = Icons.Outlined.Description,
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
private fun AttachmentDownloadOverlay(
    isVideo: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    progress: Float,
    tint: Color,
    onDownloadClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isDownloaded && !isVideo) return

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.42f)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clickable(enabled = !isDownloading && !isDownloaded) { onDownloadClick() },
                contentAlignment = Alignment.Center
            ) {
                when {
                    isDownloading -> {
                        Box(contentAlignment = Alignment.Center) {
                            DownloadProgressGlyph(progress = progress, tint = tint)
                            IconButton(onClick = onCancelClick) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Cancel",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    isDownloaded && isVideo -> Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                    else -> Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        if (isDownloading) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${(progress.coerceIn(0f, 1f) * 100).roundToInt()}%",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun DownloadProgressGlyph(progress: Float, tint: Color) {
    val clamped = progress.coerceIn(0f, 1f)
    Canvas(modifier = Modifier.size(34.dp)) {
        val stroke = 5.dp.toPx()
        val gap = (1f - clamped) * 260f
        drawArc(
            color = tint,
            startAngle = -90f + (gap / 2f),
            sweepAngle = 360f - gap,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
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
            if (users.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.noContacts,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
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
                        Text(
                            user.username,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = getNicknameFontFamily(user.nicknameFont)
                        )
                        UserBadges(
                            isVerified = user.isVerified,
                            premiumStarIcon = user.premiumStarIcon,
                            modifier = Modifier.padding(start = 4.dp),
                            verifiedSize = 14.dp,
                            starSize = 14.dp
                        )
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

    val isFa = strings.languageCode == "fa"

    val nameLabel = if (isFa) {
        if (type == "group") "نام گروه" else "نام کانال"
    } else {
        if (type == "group") "Group Name" else "Channel Name"
    }

    val handleLabel = if (isFa) {
        if (type == "channel") "نام کاربری (الزامی)" else "نام کاربری (اختیاری)"
    } else {
        if (type == "channel") "Username / Handle (Required)" else "Username / Handle (Optional)"
    }

    val handleHint = if (isFa) {
        "نام کاربری یکتایی برای لینک عمومی (مثلا my_chat)"
    } else {
        "A unique handle for your public link (e.g., my_chat)"
    }

    val handleRequiredError = if (isFa) {
        "نام کاربری برای ساخت کانال الزامی است"
    } else {
        "Handle is required for channels"
    }

    val handleFormatError = if (isFa) {
        "باید ۳ تا ۳۲ کاراکتر انگلیسی، اعداد یا خط تیره (_) باشد"
    } else {
        "Must be 3-32 characters: English letters, numbers, or underscores"
    }

    val bioLabel = if (isFa) "توضیحات (اختیاری)" else strings.bioOptional

    // Validation
    val isHandleFormatValid = handle.isEmpty() || handle.matches(Regex("^[a-zA-Z0-9_]{3,32}$"))
    val isNameValid = name.isNotBlank() && name.length <= 80
    val isHandleProvided = type == "group" || handle.isNotBlank()
    val isFormValid = isNameValid && isHandleProvided && isHandleFormatValid

    Surface(
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    )
                )
        ) {
            // Modal Header
            ModalHeader(
                title = if (isFa) {
                    if (type == "group") "ایجاد گروه جدید" else "ایجاد کانال جدید"
                } else {
                    if (type == "group") "Create New Group" else "Create New Channel"
                },
                onClose = onClose
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Type Switcher (Pill Layout)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("group", "channel").forEach { t ->
                        val isSelected = type == t
                        val typeText = if (t == "group") strings.group else strings.channel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .clickable { type = t }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = typeText,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Description subtitle depending on selected type
                val switcherDescription = if (isFa) {
                    if (type == "group") "گروه‌ها برای گفتگوی گروهی با دوستان و اعضا عالی هستند." else "کانال‌ها برای انتشار یک‌طرفه مطالب به مخاطبان نامحدود می‌باشند."
                } else {
                    if (type == "group") "Groups are ideal for multi-party chat and collaboration." else "Channels are built for broadcasting messages to unlimited audiences."
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = switcherDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Group / Channel Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 100) name = it },
                    label = { Text(nameLabel) },
                    singleLine = true,
                    supportingText = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                text = "${name.length}/80",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (name.length > 80) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    },
                    isError = name.length > 80,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Handle (Public Link) Field
                val hasHandleError = !isHandleFormatValid || (type == "channel" && handle.isBlank())
                val handleErrText = when {
                    !isHandleFormatValid -> handleFormatError
                    type == "channel" && handle.isBlank() -> handleRequiredError
                    else -> null
                }

                OutlinedTextField(
                    value = handle,
                    onValueChange = { input ->
                        val clean = input.trim().removePrefix("@")
                        if (clean.length <= 40) handle = clean
                    },
                    label = { Text(handleLabel) },
                    singleLine = true,
                    leadingIcon = {
                        Text(
                            text = "@",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp, end = 2.dp)
                        )
                    },
                    supportingText = {
                        Text(
                            text = handleErrText ?: handleHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (hasHandleError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    isError = hasHandleError,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Bio Description Field
                OutlinedTextField(
                    value = bio,
                    onValueChange = { if (it.length <= 250) bio = it },
                    label = { Text(bioLabel) },
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Create Action Button
                Button(
                    onClick = {
                        if (isFormValid) {
                            val cleanHandle = handle.trim().takeIf { it.isNotBlank() }?.let {
                                if (it.startsWith("@")) it else "@$it"
                            }
                            onCreate(name.trim(), type, cleanHandle, bio.trim().takeIf { it.isNotBlank() })
                            onClose()
                        }
                    },
                    enabled = isFormValid,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (isFa) "ایجاد" else strings.create,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsModal(
    state: AppUiState,
    strings: NoveoStrings,
    onAddContact: (String, String) -> Unit,
    section: SettingsSection,
    onSectionChange: (SettingsSection) -> Unit,
    onClose: () -> Unit,
    onLogout: () -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onUpdateProfile: (String, String, String?, String?, ProfileSkin?, PremiumStarIcon?) -> Unit,
    onSubmitPaymentRequest: (String, ByteArray, String) -> Unit,
    onCancelSubscription: () -> Unit,
    onUpdateChatProfile: (String, String, String) -> Unit,
    onUpdateChatHandle: (String, String?) -> Unit,
    onContactAdmin: () -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onCheckUpdate: () -> Unit,
    onSetBetaUpdatesEnabled: (Boolean) -> Unit,
    onSetDoubleTapReaction: (String) -> Unit,
    onUpdateNotificationSettings: (NotificationSettings) -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onRequestPermission: () -> Unit,
    onDismissError: () -> Unit = {},
    onFetchUserProfile: (String) -> Unit
) {
    val me = state.session?.userId?.let { state.usersById[it] }
    var isProfileEditing by remember { mutableStateOf(false) }

    LaunchedEffect(section) {
        if (section != SettingsSection.PROFILE) {
            isProfileEditing = false
        }
    }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            if (section != SettingsSection.PROFILE || isProfileEditing) {
                ModalHeader(
                    title = when (section) {
                        SettingsSection.MENU -> strings.settings
                        SettingsSection.SUBSCRIPTION -> strings.subscription
                        SettingsSection.PROFILE -> strings.profile
                        SettingsSection.ACCOUNT -> strings.account
                        SettingsSection.PREFERENCES -> strings.preferences
                        SettingsSection.THEME -> strings.themes
                        SettingsSection.NOTIFICATIONS -> strings.notificationSettings
                    },
                    onClose = onClose,
                    onBack = when (section) {
                        SettingsSection.MENU -> null
                        SettingsSection.PROFILE -> ({ isProfileEditing = false })
                        SettingsSection.THEME -> ({ onSectionChange(SettingsSection.PREFERENCES) })
                        SettingsSection.NOTIFICATIONS -> ({ onSectionChange(SettingsSection.PREFERENCES) })
                        else -> ({ onSectionChange(SettingsSection.MENU) })
                    },
                    showCloseOnLeft = (section == SettingsSection.MENU)
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                Crossfade(targetState = section, label = "settings_section") { current ->
                    when (current) {
                        SettingsSection.MENU -> SettingsMenu(strings, onSectionChange)
                        SettingsSection.SUBSCRIPTION -> SettingsSubscriptionSection(strings, me, state.receiptUploadProgress, state.receiptUploadStatusLog, onSubmitPaymentRequest, onCancelSubscription, onContactAdmin)
                        SettingsSection.PROFILE -> SettingsProfileSection(
                            strings = strings,
                            me = me,
                            savedStickers = state.savedStickers,
                            chats = state.chats,
                            sessionUserId = state.session?.userId,
                            onUpdateProfile = onUpdateProfile,
                            onUpdateChatProfile = onUpdateChatProfile,
                            onUpdateChatHandle = onUpdateChatHandle,
                            onSectionChange = onSectionChange,
                            contacts = state.contacts,
                            onAddContact = onAddContact,
                            isEditing = isProfileEditing,
                            onIsEditingChange = { isProfileEditing = it },
                            onFetchUserProfile = onFetchUserProfile
                        )
                        SettingsSection.ACCOUNT -> SettingsAccountSection(strings, state, onLogout, onChangePassword, onDeleteAccount)
                        SettingsSection.PREFERENCES -> SettingsPreferencesSection(state, strings, onSectionChange, onSetLanguage, onCheckUpdate, onSetBetaUpdatesEnabled, onSetDoubleTapReaction, currentTheme, onThemeChange, onRequestBatteryOptimization)
                        SettingsSection.THEME -> SettingsThemeSection(strings, currentTheme, onThemeChange)
                        SettingsSection.NOTIFICATIONS -> SettingsNotificationSection(state, strings, onUpdateNotificationSettings, onRequestPermission)
                    }
                }
            }
            // WebSocket error banner — shows at the bottom of the settings modal
            AnimatedVisibility(
                visible = state.error != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = state.error ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismissError, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSubscriptionSection(
    strings: NoveoStrings,
    me: UserSummary?,
    receiptUploadProgress: Float?,
    receiptUploadStatusLog: String?,
    onSubmitPaymentRequest: (String, ByteArray, String) -> Unit,
    onCancelSubscription: () -> Unit,
    onContactAdmin: () -> Unit
) {
    val context = LocalContext.current
    var selectedTier by remember { mutableStateOf("premium") }
    var uploadError by remember { mutableStateOf<String?>(null) }
    
    val fileLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            runCatching {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                if (bytes != null) {
                    val fileName = "receipt_${System.currentTimeMillis()}.jpg"
                    onSubmitPaymentRequest(selectedTier, bytes, fileName)
                    uploadError = null
                } else {
                    uploadError = "Unable to read selected receipt image file."
                }
            }.onFailure {
                uploadError = it.message ?: "Failed to read file."
            }
        }
    }
    
    val currentTier = me?.membershipTier?.lowercase() ?: ""
    val isPremium = currentTier == "premium"
    val isSilver = currentTier == "silver"
    val isFa = strings.languageCode == "fa"
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isFa) "وضعیت فعلی" else "Current Status",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = when (currentTier) {
                            "premium" -> "Noveo Premium Active 🌟"
                            "silver" -> "Noveo Silver Active 🥈"
                            else -> "Noveo Free 🥉"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Cancel subscription removed per product decision
                }
            }
        }
        
        if (!isPremium) {
            item {
                Text(
                    text = if (isFa) "انتخاب طرح" else "Select a Plan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            item {
                val isSelected = selectedTier == "silver"
                Card(
                    onClick = { if (!isSilver) selectedTier = "silver" },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Noveo Silver 🥈", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("35,000 Tomans / mo", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "• Access gorgeous premium fonts for your display name\n• Customize chat bubbles and messages",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            item {
                val isSelected = selectedTier == "premium" || selectedTier == "premium_upgrade"
                val actualTier = if (isSilver) "premium_upgrade" else "premium"
                Card(
                    onClick = { selectedTier = actualTier },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Noveo Premium 🌟", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (isSilver) "15,000 Tomans (Upgrade)" else "50,000 Tomans / mo",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "• Full profile skin gradient customization\n• Translucent glowing presets (Liquid Ass & more)\n• Exquisite name fonts and premium custom badges",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            item {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { fileLauncher.launch("image/*") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(if (isFa) "آپلود تصویر رسید پرداخت" else "Upload Receipt Photo to Pay", fontWeight = FontWeight.Bold)
                    }
                    
                    OutlinedButton(
                        onClick = onContactAdmin,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Outlined.AccountCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isFa) "تماس با @pcpapc172 جهت پرداخت کارت به کارت" else "Contact @pcpapc172 for Credit Card Payment",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    uploadError?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            
            if (receiptUploadProgress != null || receiptUploadStatusLog != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            receiptUploadStatusLog?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            receiptUploadProgress?.let {
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = it,
                                    modifier = Modifier.fillMaxWidth().height(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
     @Composable
private fun SettingsProfileSection(
    strings: NoveoStrings,
    me: UserSummary?,
    savedStickers: List<SavedSticker>,
    chats: List<ChatSummary>,
    sessionUserId: String?,
    onUpdateProfile: (String, String, String?, String?, ProfileSkin?, PremiumStarIcon?) -> Unit,
    onUpdateChatProfile: (String, String, String) -> Unit,
    onUpdateChatHandle: (String, String?) -> Unit,
    onSectionChange: (SettingsSection) -> Unit,
    contacts: List<UserSummary>,
    onAddContact: (String, String) -> Unit,
    isEditing: Boolean,
    onIsEditingChange: (Boolean) -> Unit,
    onFetchUserProfile: (String) -> Unit
) {
    var activeEditChat by remember { mutableStateOf<ChatSummary?>(null) }
    val isFa = strings.languageCode == "fa"

    if (activeEditChat != null) {
        val chat = activeEditChat!!
        var chatName by remember(chat) { mutableStateOf(chat.title) }
        var chatBio by remember(chat) { mutableStateOf("") } // Bio is loaded on socket or dynamically
        var chatHandle by remember(chat) { mutableStateOf(chat.handle?.replace(Regex("^@"), "") ?: "") }

        // Fetch bio if available from existing chats or fallback
        LaunchedEffect(chat) {
            // Find existing bio in state if present
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = if (isFa) "ویرایش پروفایل گفتگو" else "Edit Chat Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                OutlinedTextField(
                    value = chatName,
                    onValueChange = { chatName = it },
                    label = { Text(if (isFa) "نام گفتگو" else "Chat Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = chatBio,
                    onValueChange = { chatBio = it },
                    label = { Text(if (isFa) "درباره (Bio)" else "Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = chatHandle,
                    onValueChange = { chatHandle = it.trim().removePrefix("@") },
                    label = { Text(if (isFa) "شناسه عمومی" else "Public Handle") },
                    leadingIcon = {
                        Text(
                            text = "@",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    OutlinedButton(
                        onClick = { activeEditChat = null },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(if (isFa) "بازگشت" else "Back")
                    }

                    Button(
                        onClick = {
                            val cleanHandle = chatHandle.trim().removePrefix("@")
                            onUpdateChatProfile(chat.id, chatName.trim(), chatBio.trim())
                            onUpdateChatHandle(chat.id, cleanHandle.takeIf { it.isNotBlank() })
                            activeEditChat = null
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.5f).height(48.dp)
                    ) {
                        Text(if (isFa) "ذخیره تغییرات" else "Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        LaunchedEffect(sessionUserId) {
            sessionUserId?.let { onFetchUserProfile(it) }
        }
        
        var username by remember(me) { mutableStateOf(me?.username ?: "") }
        var bio by remember(me) { mutableStateOf(me?.bio ?: "") }
        var handle by remember(me) { mutableStateOf(me?.handle?.replace(Regex("^@"), "") ?: "") }
        
        val tier = me?.membershipTier?.lowercase() ?: ""
        val hasFontAccess = tier == "premium" || tier == "silver"
        val hasSkinAccess = tier == "premium"
        
        var selectedFont by remember(me) { mutableStateOf(me?.nicknameFont ?: "") }
        
        // Custom 1:1 Web Client Skin States
        val initialSkin = me?.profileSkin
        var skinMode by remember(me) { mutableStateOf(initialSkin?.mode?.lowercase() ?: "") }
        var skinPrimary by remember(me) { mutableStateOf(initialSkin?.primaryColor?.ifBlank { "#00FFF0" } ?: "#00FFF0") }
        var skinSecondary by remember(me) { mutableStateOf(initialSkin?.secondaryColor?.ifBlank { "#00838F" } ?: "#00838F") }
        var skinTertiary by remember(me) { mutableStateOf(initialSkin?.tertiaryColor?.ifBlank { "#00E5FF" } ?: "#00E5FF") }
        var skinStops by remember(me) { mutableStateOf(initialSkin?.gradientStops ?: 2) }
        // Star badge: track selected template OR custom TGS url
        var selectedBadgeTemplateId by remember(me) { mutableStateOf(me?.premiumStarIcon?.templateId ?: "none") }
        var selectedBadgeTgsUrl by remember(me) { mutableStateOf(if (me?.premiumStarIcon?.type == "tgs") me.premiumStarIcon.url else "") }
        // Star picker tab: "templates" | "tgs" | "stickers"
        var starPickerTab by remember { mutableStateOf("templates") }
        var showStarPicker by remember { mutableStateOf(false) }

        // Default TGS star stickers from server (0001..0020)
        val defaultTgsStars = remember {
            (1..20).map { i -> "https://noveo.ir/emoji_tgs/%04d.tgs".format(i) }
        }

        val isFaLocal = strings.languageCode == "fa"

        if (!isEditing) {
            // ── VIEW MODE (REUSED PROFILE SCREEN) ──
            me?.let { meUser ->
                ProfileModal(
                    strings = strings,
                    user = meUser,
                    chats = chats,
                    selfUserId = sessionUserId,
                    contacts = contacts,
                    onAddContact = onAddContact,
                    onClose = { onSectionChange(SettingsSection.MENU) },
                    onMessage = { onIsEditingChange(true) },
                    onAvatarClick = {}
                )
            }
        } else {
            // ── EDIT MODE ──

        // Star Picker Dialog
        if (showStarPicker) {
            AlertDialog(
                onDismissRequest = { showStarPicker = false },
                title = {
                    Text(if (isFaLocal) "نشان ستاره پریمیوم" else "Premium Star Badge", fontWeight = FontWeight.ExtraBold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // None option
                        val noneSelected = selectedBadgeTemplateId == "none"
                        Card(
                            onClick = { selectedBadgeTemplateId = "none"; selectedBadgeTgsUrl = "" },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(if (noneSelected) 2.dp else 1.dp, if (noneSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = if (noneSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isFaLocal) "بدون نشان" else "No badge", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                        }
                        listOf("gold" to "⭐ Gold Star", "cyan" to "🔵 Cyan Star", "purple" to "💜 Purple Star").forEach { (tId, label) ->
                            val isSel = selectedBadgeTemplateId == tId
                            Card(
                                onClick = { selectedBadgeTemplateId = tId; selectedBadgeTgsUrl = "" },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(if (isSel) 2.dp else 1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(label, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showStarPicker = false }) {
                        Text(if (isFaLocal) "تایید" else "Done")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        selectedBadgeTemplateId = "none"; selectedBadgeTgsUrl = ""; showStarPicker = false
                    }) {
                        Text(if (isFaLocal) "پاک کردن" else "Clear")
                    }
                }
            )
        }

        val presetColors = listOf("#FF5252", "#FFA726", "#FFD54F", "#66BB6A", "#26C6DA", "#29B6F6", "#AB47BC", "#EC407A")

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isFaLocal) "ویرایش پروفایل" else "Edit Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedButton(
                        onClick = { onIsEditingChange(false) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(if (isFaLocal) "بازگشت" else "Cancel", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            item {
                ProfileCircle(name = username.ifBlank { "Me" }, imageUrl = me?.avatarUrl, size = 90.dp)
            }
            item {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(strings.displayName) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text(strings.bio) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it.trim().removePrefix("@") },
                    label = { Text("Public Handle") },
                    leadingIcon = {
                        Text(
                            text = "@",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            if (hasFontAccess) {
                item {
                    Text(
                        text = if (isFaLocal) "قلم نام مستعار" else "Nickname Font",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
                item {
                    val fontsMap = listOf(
                        "" to "Default",
                        "8514oem" to "OEM",
                        "bradhitc" to "Bradley",
                        "coure" to "Courier",
                        "freescpt" to "Script",
                        "f_majik" to "Magic",
                        "impact" to "Impact",
                        "npidivani" to "Divani"
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        fontsMap.chunked(4).forEach { chunk ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                chunk.forEach { (fontKey, fontName) ->
                                    val isSelected = selectedFont == fontKey
                                    Card(
                                        onClick = { selectedFont = fontKey },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(modifier = Modifier.padding(6.dp), contentAlignment = Alignment.Center) {
                                            Text(
                                                text = fontName,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (hasSkinAccess) {
                item {
                    Text(
                        text = if (isFaLocal) "پوسته پروفایل پریمیوم" else "Premium Profile Skin",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
                item {
                    val modes = listOf("" to "Default", "solid" to "Static Color", "gradient" to "Gradient")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        modes.forEach { (modeKey, modeTitle) ->
                            val isSelected = skinMode == modeKey
                            Card(
                                onClick = { skinMode = modeKey },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = modeTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
                if (skinMode == "solid") {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = runCatching { Color(android.graphics.Color.parseColor(skinPrimary)) }.getOrDefault(Color.Gray),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                )
                                OutlinedTextField(
                                    value = skinPrimary,
                                    onValueChange = { skinPrimary = it },
                                    label = { Text(if (isFaLocal) "رنگ اصلی (Hex)" else "Primary Color (Hex)") },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                presetColors.forEach { hex ->
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(android.graphics.Color.parseColor(hex)), RoundedCornerShape(12.dp))
                                            .clickable { skinPrimary = hex }
                                            .border(
                                                width = if (skinPrimary.lowercase() == hex.lowercase()) 2.dp else 0.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
                if (skinMode == "gradient") {
                    item {
                        val stopsOptions = listOf(2 to "2 Colors", 3 to "3 Colors")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            stopsOptions.forEach { (stopsVal, stopsTitle) ->
                                val isSelected = skinStops == stopsVal
                                Card(
                                    onClick = { skinStops = stopsVal },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(modifier = Modifier.padding(6.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = stopsTitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = runCatching { Color(android.graphics.Color.parseColor(skinPrimary)) }.getOrDefault(Color.Gray),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                )
                                OutlinedTextField(
                                    value = skinPrimary,
                                    onValueChange = { skinPrimary = it },
                                    label = { Text("Color 1 (Hex)") },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                presetColors.forEach { hex ->
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(Color(android.graphics.Color.parseColor(hex)), RoundedCornerShape(10.dp))
                                            .clickable { skinPrimary = hex }
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = runCatching { Color(android.graphics.Color.parseColor(skinSecondary)) }.getOrDefault(Color.Gray),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                )
                                OutlinedTextField(
                                    value = skinSecondary,
                                    onValueChange = { skinSecondary = it },
                                    label = { Text("Color 2 (Hex)") },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                presetColors.forEach { hex ->
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(Color(android.graphics.Color.parseColor(hex)), RoundedCornerShape(10.dp))
                                            .clickable { skinSecondary = hex }
                                    )
                                }
                            }
                            if (skinStops == 3) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                color = runCatching { Color(android.graphics.Color.parseColor(skinTertiary)) }.getOrDefault(Color.Gray),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                    )
                                    OutlinedTextField(
                                        value = skinTertiary,
                                        onValueChange = { skinTertiary = it },
                                        label = { Text("Color 3 (Hex)") },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    presetColors.forEach { hex ->
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color(android.graphics.Color.parseColor(hex)), RoundedCornerShape(10.dp))
                                                .clickable { skinTertiary = hex }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
            if (!hasFontAccess && !hasSkinAccess) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Unlock Premium Customizations 👑",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Get access to gorgeous name fonts, glowing profile skin gradient presets, and custom star badge icons.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = { onSectionChange(SettingsSection.SUBSCRIPTION) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text("Upgrade Plan", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        val cleanHandle = handle.trim().removePrefix("@")
                        val skinObj = if (hasSkinAccess && skinMode.isNotBlank()) {
                            if (skinMode == "solid") {
                                ProfileSkin(mode = "solid", color = skinPrimary)
                            } else {
                                val colors = mutableListOf(skinPrimary, skinSecondary)
                                if (skinStops == 3) colors.add(skinTertiary)
                                ProfileSkin(mode = "gradient", primaryColor = skinPrimary, secondaryColor = skinSecondary, tertiaryColor = if (skinStops == 3) skinTertiary else "", colors = colors, gradientStops = skinStops)
                            }
                        } else null
                        val badgeObj = if (hasSkinAccess) {
                            when {
                                selectedBadgeTemplateId != "none" && selectedBadgeTemplateId.isNotBlank() -> {
                                    val colorUrl = when (selectedBadgeTemplateId) {
                                        "gold" -> "https://noveo.ir/badges/star_gold.png"
                                        "cyan" -> "https://noveo.ir/badges/star_cyan.png"
                                        "purple" -> "https://noveo.ir/badges/star_purple.png"
                                        else -> ""
                                    }
                                    PremiumStarIcon(url = colorUrl, type = "image", source = "template", templateId = selectedBadgeTemplateId)
                                }
                                else -> null
                            }
                        } else null
                        onUpdateProfile(
                            username.trim(),
                            bio.trim(),
                            cleanHandle.takeIf { it.isNotBlank() },
                            selectedFont.takeIf { it.isNotBlank() },
                            skinObj,
                            badgeObj
                        )
                        onIsEditingChange(false)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.saveChanges, fontWeight = FontWeight.Bold)
                }
            }
            // Owned groups & channels
            val ownedChatsEdit = chats.filter { (it.chatType == "group" || it.chatType == "channel") && it.ownerId == sessionUserId }
            if (ownedChatsEdit.isNotEmpty()) {
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        text = if (isFaLocal) "مدیریت گروه‌ها و کانال‌ها" else "Manage Groups & Channels",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                ownedChatsEdit.forEach { chat ->
                    item {
                        Card(
                            onClick = { activeEditChat = chat },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    ProfileCircle(name = chat.title, imageUrl = chat.avatarUrl, size = 36.dp)
                                    Column {
                                        Text(chat.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text(
                                            chat.handle ?: (if (chat.chatType == "group") "Group" else "Channel"),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Icon(imageVector = Icons.Outlined.ArrowForward, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        } // end else (isEditing)
    } // end outer else (no activeEditChat)
}

@Composable
private fun SettingsMenu(strings: NoveoStrings, onSectionChange: (SettingsSection) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettingsRow(strings.subscription, Icons.Outlined.Star) { onSectionChange(SettingsSection.SUBSCRIPTION) }
        SettingsRow(strings.profile, Icons.Outlined.Person) { onSectionChange(SettingsSection.PROFILE) }
        SettingsRow(strings.account, Icons.Outlined.AccountCircle) { onSectionChange(SettingsSection.ACCOUNT) }
        SettingsRow(strings.preferences, Icons.Outlined.Settings) { onSectionChange(SettingsSection.PREFERENCES) }
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
    onSetBetaUpdatesEnabled: (Boolean) -> Unit,
    onSetDoubleTapReaction: (String) -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onRequestBatteryOptimization: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showReactionDialog by rememberSaveable { mutableStateOf(false) }
    val reactionOptions = CONTEXT_MENU_REACTIONS
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
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSetBetaUpdatesEnabled(!state.betaUpdatesEnabled) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(strings.betaUpdates, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(strings.betaUpdatesBody, style = MaterialTheme.typography.bodySmall)
                }
                androidx.compose.material3.Switch(
                    checked = state.betaUpdatesEnabled,
                    onCheckedChange = onSetBetaUpdatesEnabled
                )
            }
        }
        SettingsRow("${strings.doubleTapReaction}: ${state.doubleTapReaction.ifBlank { "❤" }}", Icons.Outlined.Star) {
            showReactionDialog = true
        }

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

    if (showReactionDialog) {
        AlertDialog(
            onDismissRequest = { showReactionDialog = false },
            title = { Text(strings.doubleTapReaction) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.doubleTapReactionBody)
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(48.dp),
                        modifier = Modifier.heightIn(max = 320.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(reactionOptions) { reaction ->
                            Surface(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        onSetDoubleTapReaction(reaction)
                                        showReactionDialog = false
                                    },
                                color = if (reaction == state.doubleTapReaction.ifBlank { "❤" }) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = reaction, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showReactionDialog = false }) { Text(strings.cancel) } }
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
                title = if (strings.languageCode == "fa") "تم‌های دیگر" else "Other Themes",
                subtitle = if (strings.languageCode == "fa") "تم‌های رنگارنگ" else "Colorful themes",
                presets = listOf(ThemePreset.SUNSET_SHIMMER, ThemePreset.CHERRY_RED)
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
private fun ProfileModal(
    strings: NoveoStrings,
    user: UserSummary,
    chats: List<ChatSummary>,
    selfUserId: String?,
    contacts: List<UserSummary>,
    onAddContact: (String, String) -> Unit,
    onClose: () -> Unit,
    onMessage: () -> Unit,
    onLeaveChat: ((String) -> Unit)? = null,
    animateEntrance: Boolean = false,
    onAvatarClick: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(strings.information, strings.gifts)

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
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Tab Row
                        TabRow(
                            selectedTabIndex = selectedTab,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(title, style = MaterialTheme.typography.labelLarge) }
                                )
                            }
                        }

                        if (selectedTab == 0) {
                            // ── Information Tab ──────────────────────────────────────
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
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

                                val isSelf = selfUserId == user.id
                                Button(
                                    onClick = onMessage,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isSelf) strings.editProfile else strings.sendMessage)
                                }

                                if (!isSelf) {
                                    val isAlreadyContact = remember(user.id, contacts) { contacts.any { it.id == user.id } }
                                    if (!isAlreadyContact) {
                                        var showAddContactDialog by remember { mutableStateOf(false) }
                                        var contactCustomName by remember(user) { mutableStateOf(user.username) }

                                        if (showAddContactDialog) {
                                            val isFa = strings.languageCode == "fa"
                                            AlertDialog(
                                                onDismissRequest = { showAddContactDialog = false },
                                                title = {
                                                    Text(
                                                        text = if (isFa) "افزودن به مخاطبین" else "Add to Contacts",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                },
                                                text = {
                                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        Text(
                                                            text = if (isFa) "لطفاً نام مخاطب را وارد کنید:" else "Please enter a name for this contact:",
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        OutlinedTextField(
                                                            value = contactCustomName,
                                                            onValueChange = { contactCustomName = it },
                                                            label = { Text(if (isFa) "نام مخاطب" else "Contact Name") },
                                                            singleLine = true,
                                                            modifier = Modifier.fillMaxWidth(),
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                    }
                                                },
                                                confirmButton = {
                                                    Button(
                                                        onClick = {
                                                            val finalName = contactCustomName.trim()
                                                            onAddContact(user.id, finalName.ifBlank { user.username })
                                                            showAddContactDialog = false
                                                        },
                                                        shape = RoundedCornerShape(10.dp)
                                                    ) {
                                                        Text(if (isFa) "ذخیره" else "Save")
                                                    }
                                                },
                                                dismissButton = {
                                                    TextButton(
                                                        onClick = { showAddContactDialog = false }
                                                    ) {
                                                        Text(strings.cancel)
                                                    }
                                                }
                                            )
                                        }

                                        Spacer(Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { showAddContactDialog = true },
                                            modifier = Modifier.fillMaxWidth().height(48.dp),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(strings.addToContacts)
                                        }
                                    } else {
                                        Spacer(Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = {},
                                            enabled = false,
                                            modifier = Modifier.fillMaxWidth().height(48.dp),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(strings.alreadyInContacts)
                                        }
                                    }
                                }

                                Spacer(Modifier.height(300.dp))
                            }
                        } else {
                            // ── Gifts Tab ────────────────────────────────────────────
                            val giftsList = user.gifts.orEmpty()
                            if (giftsList.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Icon(
                                            imageVector = Icons.Outlined.Star,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        )
                                        Text(
                                            strings.noGifts,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            } else {
                                // Grid: 3 columns
                                val columns = 3
                                val rows = (giftsList.size + columns - 1) / columns
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (row in 0 until rows) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            for (col in 0 until columns) {
                                                val index = row * columns + col
                                                if (index < giftsList.size) {
                                                    val gift = giftsList[index]
                                                    GiftCard(gift = gift, modifier = Modifier.weight(1f))
                                                } else {
                                                    Spacer(modifier = Modifier.weight(1f))
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

            // Collapsing Header
            val currentHeaderHeight = lerpDp(expandedHeight, collapsedHeight, fraction)
            val hasPremiumSkin = user.membershipTier.lowercase(Locale.ROOT) == "premium" && user.profileSkin != null
            val fallbackHeaderBg = MaterialTheme.colorScheme.surface
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentHeaderHeight)
                    .profileGradientBackground(
                        profileSkin = user.profileSkin,
                        membershipTier = user.membershipTier,
                        fallbackColor = fallbackHeaderBg
                    ),
                color = Color.Transparent,
                shadowElevation = 0.dp,
                contentColor = if (hasPremiumSkin) Color.White else MaterialTheme.colorScheme.onSurface
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Back Button
                    HeaderIconButton(
                        icon = Icons.AutoMirrored.Outlined.ArrowBack,
                        onClick = onClose,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                        tint = LocalContentColor.current
                    )

                    val avatarSize = lerpDp(120.dp, 38.dp, fraction)

                    // Avatar position calculation
                    val expandedAvatarX = (screenWidth / 2) - (avatarSize / 2)
                    val collapsedAvatarX = 52.dp // Next to back button
                    val avatarX = lerpDp(expandedAvatarX, collapsedAvatarX, fraction)

                    val expandedAvatarY = (expandedHeight / 2) - (avatarSize / 2) - 20.dp
                    val collapsedAvatarY = (collapsedHeight / 2) - (avatarSize / 2)
                    val avatarY = lerpDp(expandedAvatarY, collapsedAvatarY, fraction)

                    val hasAvatar = !user.avatarUrl.isNullOrBlank() && !user.avatarUrl.endsWith("default.png")
                    Box(
                        modifier = Modifier
                            .offset(x = avatarX, y = avatarY)
                            .then(
                                if (hasAvatar) Modifier.clip(CircleShape).clickable { onAvatarClick(user.avatarUrl) }
                                else Modifier
                            )
                    ) {
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
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = getNicknameFontFamily(user.nicknameFont)
                                )
                                UserBadges(
                                    isVerified = user.isVerified,
                                    premiumStarIcon = user.premiumStarIcon,
                                    modifier = Modifier.padding(start = 6.dp),
                                    verifiedSize = 18.dp,
                                    starSize = 18.dp
                                )
                            }
                            val lastSeenText = remember(user, strings) {
                                if (user.isOnline) strings.online
                                else formatLastSeen(user.lastSeen, strings)
                            }
                            Text(
                                lastSeenText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (user.isOnline) {
                                    if (hasPremiumSkin) Color(0xFF81D4FA) else MaterialTheme.colorScheme.primary
                                } else {
                                    LocalContentColor.current.copy(alpha = 0.7f)
                                }
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
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = getNicknameFontFamily(user.nicknameFont)
                                )
                                UserBadges(
                                    isVerified = user.isVerified,
                                    premiumStarIcon = user.premiumStarIcon,
                                    modifier = Modifier.padding(start = 4.dp),
                                    verifiedSize = 14.dp,
                                    starSize = 14.dp
                                )
                            }
                            val lastSeenText = remember(user, strings) {
                                if (user.isOnline) strings.online
                                else formatLastSeen(user.lastSeen, strings)
                            }
                            Text(
                                lastSeenText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (user.isOnline) {
                                    if (hasPremiumSkin) Color(0xFF81D4FA) else MaterialTheme.colorScheme.primary
                                } else {
                                    LocalContentColor.current.copy(alpha = 0.7f)
                                },
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
private fun WebmPlayer(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember(url) {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
            setMediaItem(androidx.media3.common.MediaItem.fromUri(Uri.parse(url)))
            repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
        }
    }
    
    androidx.compose.runtime.DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    androidx.compose.ui.viewinterop.AndroidView(
        factory = { ctx ->
            androidx.media3.ui.PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun GiftCard(gift: UserGift, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Gift thumbnail
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (gift.imageUrl.isNotBlank()) {
                        val normalizedUrl = remember(gift.imageUrl) { gift.imageUrl.normalizeNoveoUrl() }
                        val isTgs = remember(gift.imageUrl) {
                            gift.imageUrl.lowercase(Locale.ROOT).contains(".tgs")
                        }
                        val isWebm = remember(gift.imageUrl) {
                            gift.imageUrl.lowercase(Locale.ROOT).contains(".webm")
                        }
                        if (isTgs) {
                            TgsSticker(
                                url = normalizedUrl,
                                modifier = Modifier.fillMaxSize().padding(6.dp)
                            )
                        } else if (isWebm && normalizedUrl != null) {
                            WebmPlayer(
                                url = normalizedUrl,
                                modifier = Modifier.fillMaxSize().padding(6.dp)
                            )
                        } else {
                            val context = LocalContext.current
                            val request = remember<ImageRequest>(normalizedUrl) {
                                ImageRequest.Builder(context)
                                    .data(normalizedUrl)
                                    .crossfade(true)
                                    .build()
                            }
                            AsyncImage(
                                model = request,
                                contentDescription = gift.name,
                                modifier = Modifier.fillMaxSize().padding(6.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = gift.name,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                }
                // Gift name
                Text(
                    text = gift.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            // Quantity badge
            if (gift.quantity > 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "×${gift.quantity}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
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

private fun Modifier.profileGradientBackground(
    profileSkin: ProfileSkin?,
    membershipTier: String,
    fallbackColor: Color
): Modifier = this.drawBehind {
    val skin = profileSkin
    val isPremium = membershipTier.lowercase(Locale.ROOT) == "premium"
    if (isPremium && skin != null) {
        val mode = skin.mode.lowercase(Locale.ROOT).trim()
        if (mode == "solid") {
            val solidHex = skin.color.ifBlank { skin.primaryColor }
            if (solidHex.isNotBlank()) {
                val parsedColor = runCatching {
                    val formatted = if (solidHex.startsWith("#")) solidHex else "#$solidHex"
                    Color(android.graphics.Color.parseColor(formatted))
                }.getOrDefault(fallbackColor)
                drawRect(color = parsedColor)
            } else {
                drawRect(color = fallbackColor)
            }
        } else if (mode == "gradient") {
            val colorsList = skin.colors.map { hex ->
                runCatching {
                    val formatted = if (hex.startsWith("#")) hex else "#$hex"
                    Color(android.graphics.Color.parseColor(formatted))
                }.getOrDefault(fallbackColor)
            }.filter { it != Color.Unspecified }

            if (colorsList.size >= 2) {
                val w = size.width
                val h = size.height
                val angleRad = Math.toRadians(skin.angle.toDouble())
                val dx = Math.sin(angleRad)
                val dy = -Math.cos(angleRad)
                val len = Math.abs(w * dx) + Math.abs(h * dy)
                
                val startX = (w / 2f) - (dx * len / 2f).toFloat()
                val startY = (h / 2f) - (dy * len / 2f).toFloat()
                val endX = (w / 2f) + (dx * len / 2f).toFloat()
                val endY = (h / 2f) + (dy * len / 2f).toFloat()
                
                val brush = Brush.linearGradient(
                    colors = colorsList,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY)
                )
                drawRect(brush = brush)
            } else if (colorsList.size == 1) {
                drawRect(color = colorsList[0])
            } else {
                val legacyColors = listOfNotNull(
                    skin.primaryColor.takeIf { it.isNotBlank() },
                    skin.secondaryColor.takeIf { it.isNotBlank() },
                    skin.tertiaryColor.takeIf { it.isNotBlank() }
                ).map { hex ->
                    runCatching {
                        val formatted = if (hex.startsWith("#")) hex else "#$hex"
                        Color(android.graphics.Color.parseColor(formatted))
                    }.getOrDefault(fallbackColor)
                }
                if (legacyColors.size >= 2) {
                    val w = size.width
                    val h = size.height
                    val angleRad = Math.toRadians(skin.angle.toDouble())
                    val dx = Math.sin(angleRad)
                    val dy = -Math.cos(angleRad)
                    val len = Math.abs(w * dx) + Math.abs(h * dy)
                    
                    val startX = (w / 2f) - (dx * len / 2f).toFloat()
                    val startY = (h / 2f) - (dy * len / 2f).toFloat()
                    val endX = (w / 2f) + (dx * len / 2f).toFloat()
                    val endY = (h / 2f) + (dy * len / 2f).toFloat()
                    
                    val brush = Brush.linearGradient(
                        colors = legacyColors,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY)
                    )
                    drawRect(brush = brush)
                } else if (legacyColors.size == 1) {
                    drawRect(color = legacyColors[0])
                } else {
                    drawRect(color = fallbackColor)
                }
            }
        } else {
            drawRect(color = fallbackColor)
        }
    } else {
        drawRect(color = fallbackColor)
    }
}

@Composable
private fun GroupInfoModal(
    chat: ChatSummary, 
    strings: NoveoStrings, 
    state: AppUiState,
    onOpenProfile: (String) -> Unit,
    onClose: () -> Unit,
    onJoinChat: (String) -> Unit,
    onLeaveChat: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    animateEntrance: Boolean = false,
    onAvatarClick: (String) -> Unit
) {
    val usersById = state.usersById
    val sessionUserId = state.session?.userId
    val isSavedMessages = remember(chat, sessionUserId) { chat.isSavedMessagesChat(sessionUserId) }
    val savedMessagesCount = state.messagesByChat[chat.id]?.size ?: state.messages.size
    val chatTitle = remember(chat.title, strings) {
        if (isSavedMessages) strings.savedMessages
        else chat.title.ifBlank { strings.chatInfo }
    }
    val profileUserId = remember(chat, sessionUserId) { resolveProfileUserId(chat, sessionUserId) }
    val isVerified = chat.isVerified || (profileUserId?.let { usersById[it]?.isVerified } == true)
    
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
                                    val normalizedHandle = chat.handle.removePrefix("@")
                                    InfoItem(label = strings.link, value = "@$normalizedHandle", onClick = {
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString("@$normalizedHandle"))
                                    })
                                }
                                if (!isSavedMessages) {
                                    InfoItem(label = strings.type, value = formatChatType(chat.chatType, strings))
                                }
                                if (isSavedMessages) {
                                    InfoItem(
                                        label = strings.messages,
                                        value = formatMessagesCount(savedMessagesCount, strings)
                                    )
                                }
                                
                                val isMember = chat.memberIds.contains(state.session?.userId)
                                if (chat.chatType != "private") {
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onOpenChat(chat.id) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(strings.open)
                                        }
                                        
                                        if (!isMember) {
                                            Button(
                                                onClick = { onJoinChat(chat.id) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            ) {
                                                Text(strings.join)
                                            }
                                        } else if (!isSavedMessages) {
                                            Button(
                                                onClick = { onLeaveChat(chat.id) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            ) {
                                                Text(strings.leave)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            if (isSavedMessages) strings.messages else strings.members,
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                items(if (isSavedMessages) emptyList() else chat.memberIds, key = { it }) { memberId ->
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
                                        Text(
                                            user?.username ?: memberId,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = getNicknameFontFamily(user?.nicknameFont)
                                        )
                                        UserBadges(
                                            isVerified = user?.isVerified == true,
                                            premiumStarIcon = user?.premiumStarIcon,
                                            modifier = Modifier.padding(start = 4.dp),
                                            verifiedSize = 14.dp,
                                            starSize = 14.dp
                                        )
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
                    
                    val hasAvatar = !chat.avatarUrl.isNullOrBlank() && !chat.avatarUrl.endsWith("default.png")
                    Box(
                        modifier = Modifier
                            .offset(x = avatarX, y = avatarY)
                            .then(
                                if (hasAvatar) Modifier.clip(CircleShape).clickable { onAvatarClick(chat.avatarUrl) }
                                else Modifier
                            )
                    ) {
                        ProfileCircle(name = chatTitle, imageUrl = chat.avatarUrl, size = avatarSize, isSavedMessages = isSavedMessages)
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
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                UserBadges(
                                    isVerified = isVerified,
                                    premiumStarIcon = profileUserId?.let { usersById[it]?.premiumStarIcon },
                                    modifier = Modifier.padding(start = 6.dp),
                                    verifiedSize = 18.dp,
                                    starSize = 18.dp
                                )
                            }
                            Text(
                                if (isSavedMessages) formatMessagesCount(savedMessagesCount, strings) else formatMembersCount(chat.memberIds.size, strings),
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
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                UserBadges(
                                    isVerified = isVerified,
                                    premiumStarIcon = profileUserId?.let { usersById[it]?.premiumStarIcon },
                                    modifier = Modifier.padding(start = 4.dp),
                                    verifiedSize = 14.dp,
                                    starSize = 14.dp
                                )
                            }
                            Text(
                                if (isSavedMessages) formatMessagesCount(savedMessagesCount, strings) else formatMembersCount(chat.memberIds.size, strings),
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
    onOpenSettings: () -> Unit,
    onOpenProfile: (String) -> Unit
) {
    val me = state.session?.userId?.let { state.usersById[it] }
    val tgColors = telegramColors()

    Column(
        modifier = Modifier
            .width(296.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Telegram-Style Header Box
        val themePrimary = MaterialTheme.colorScheme.primary
        val themePrimaryContainer = MaterialTheme.colorScheme.primaryContainer
        val themeSecondary = MaterialTheme.colorScheme.secondary
        val themeBackground = MaterialTheme.colorScheme.background

        // Determine harmonious header colors dynamically from active theme
        val headerBrush = remember(themePrimary, themePrimaryContainer, themeSecondary, tgColors.isDark) {
            if (tgColors.isDark) {
                // In dark mode: rich deep primary-to-background gradient matching active theme tint
                Brush.linearGradient(
                    colors = listOf(
                        themePrimary.copy(alpha = 0.85f),
                        Color(
                            red = (themePrimary.red * 0.4f + themeBackground.red * 0.6f),
                            green = (themePrimary.green * 0.4f + themeBackground.green * 0.6f),
                            blue = (themePrimary.blue * 0.4f + themeBackground.blue * 0.6f)
                        )
                    )
                )
            } else {
                // In light mode: vibrant primary-to-secondary gradient matching active theme tint
                Brush.linearGradient(
                    colors = listOf(
                        themePrimary,
                        Color(
                            red = (themePrimary.red * 0.7f + themeSecondary.red * 0.3f),
                            green = (themePrimary.green * 0.7f + themeSecondary.green * 0.3f),
                            blue = (themePrimary.blue * 0.7f + themeSecondary.blue * 0.3f)
                        )
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBrush)
                .profileGradientBackground(
                    profileSkin = me?.profileSkin,
                    membershipTier = me?.membershipTier ?: "",
                    fallbackColor = Color.Transparent
                )
                .clickable {
                    val userId = state.session?.userId
                    if (!userId.isNullOrBlank()) {
                        onOpenProfile(userId)
                    } else {
                        onOpenSettings()
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                ProfileCircle(
                    name = me?.username?.ifBlank { "Me" } ?: "Me",
                    imageUrl = me?.avatarUrl,
                    size = 64.dp
                )

                Spacer(Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = me?.username?.ifBlank { strings.settings } ?: strings.settings,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    UserBadges(
                        isVerified = me?.isVerified == true,
                        premiumStarIcon = me?.premiumStarIcon,
                        modifier = Modifier.padding(start = 4.dp),
                        verifiedSize = 16.dp
                    )
                }

                val subtitleText = when {
                    !me?.handle.isNullOrBlank() -> "@${me.handle.removePrefix("@")}"
                    else -> strings.brandName
                }
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Drawer Menu Items
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            MenuRow(strings.allContacts, Icons.Outlined.Info, onOpenContacts)
            MenuRow(strings.newChat, Icons.Outlined.Menu, onOpenCreate)
            Card(
                modifier = Modifier.fillMaxWidth(),
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
    usersById: Map<String, UserSummary>,
    currentUserId: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val chatTitle = remember(chat, strings, currentUserId) {
        if (chat.isSavedMessagesChat(currentUserId)) strings.savedMessages
        else chat.title.ifBlank { strings.chatInfo }
    }
    val profileUserId = remember(chat, currentUserId) { resolveProfileUserId(chat, currentUserId) }
    val isVerified = chat.isVerified || (profileUserId?.let { usersById[it]?.isVerified } == true)
    val colors = MaterialTheme.colorScheme
    val containerColor = remember(selected, colors) {
        if (selected) colors.secondaryContainer else colors.surface
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { clip = true } // Hardware caching
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProfileCircle(name = chatTitle, imageUrl = chat.avatarUrl, isSavedMessages = chat.isSavedMessagesChat(currentUserId))
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                val nicknameFont = profileUserId?.let { usersById[it]?.nicknameFont }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        chatTitle,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = getNicknameFontFamily(nicknameFont),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    UserBadges(
                        isVerified = isVerified,
                        premiumStarIcon = profileUserId?.let { usersById[it]?.premiumStarIcon },
                        modifier = Modifier.padding(start = 4.dp),
                        verifiedSize = 14.dp,
                        starSize = 14.dp
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(localizeMessagePreview(chat.lastMessagePreview, strings).ifBlank { strings.noMessagesYet }, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
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
                modifier = (if (fullscreen) Modifier.fillMaxSize() else Modifier.padding(18.dp))
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
private fun ModalHeader(
    title: String,
    onClose: () -> Unit,
    onBack: (() -> Unit)? = null,
    showCloseOnLeft: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showCloseOnLeft) {
            HeaderIconButton(icon = Icons.Outlined.Close, onClick = onClose)
            Spacer(Modifier.width(8.dp))
        } else if (onBack != null) {
            HeaderIconButton(icon = Icons.AutoMirrored.Outlined.ArrowBack, onClick = onBack)
            Spacer(Modifier.width(8.dp))
        }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (!showCloseOnLeft) {
            HeaderIconButton(icon = Icons.Outlined.Close, onClick = onClose)
        }
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
private fun ProfileCircle(name: String, imageUrl: String?, size: Dp = 40.dp, modifier: Modifier = Modifier, isSavedMessages: Boolean = false, shape: androidx.compose.ui.graphics.Shape = CircleShape) {
    if (isSavedMessages) {
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
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
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "N",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = with(LocalDensity.current) { (size * 0.45f).toSp() }
            )
        }
    }

    if (!resolvedImageUrl.isNullOrBlank() && !isDefaultAvatar) {
        AsyncImage(
            model = resolvedImageUrl,
            contentDescription = name,
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface),
            contentScale = ContentScale.Crop
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

@Composable
private fun SeenByModal(
    strings: NoveoStrings,
    message: ChatMessage,
    usersById: Map<String, UserSummary>,
    onClose: () -> Unit,
    onOpenProfile: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.seenBy,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Outlined.Close, contentDescription = null)
                }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            
            val seenUsers = remember(message.seenBy, usersById) {
                message.seenBy.mapNotNull { usersById[it] }
            }
            
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(seenUsers) { user ->
                    ContactRow(
                        user = user,
                        strings = strings,
                        existingChat = null,
                        onMessage = {
                            onClose()
                            onOpenProfile(user.id)
                        },
                        onOpenProfile = {
                            onClose()
                            onOpenProfile(user.id)
                        }
                    )
                }
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

private fun formatExpiry(session: Session?, strings: NoveoStrings): String {
    val value = session?.expiresAt ?: 0L
    if (value <= 0L) return strings.unknown
    return runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        localizeDigits(sdf.format(Date(value)), strings.languageCode)
    }.getOrElse { strings.unknown }
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
private fun FullscreenMediaModal(attachment: MessageFileAttachment, localPath: String, onDismiss: () -> Unit) {
    val normalizedUrl = remember(attachment.url) { attachment.url.normalizeNoveoUrl() }
    val context = LocalContext.current
    val isVideo = remember(attachment) { attachment.isVideo() }
    val mediaUri = remember(localPath, normalizedUrl) {
        Uri.fromFile(File(localPath)).takeIf { localPath.isNotBlank() } ?: normalizedUrl?.let(Uri::parse)
    }

    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isVideo && mediaUri != null) {
                val exoPlayer = remember {
                    androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
                        setMediaItem(androidx.media3.common.MediaItem.fromUri(mediaUri))
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
                    model = mediaUri ?: File(localPath),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            
            HeaderIconButton(
                icon = Icons.Outlined.Close,
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape),
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
            .height(displayHeight)
            .navigationBarsPadding(),
        color = tgColors.incomingBubble,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.width(44.dp).height(4.dp),
                    color = tgColors.headerSubtitle.copy(alpha = 0.35f),
                    shape = CircleShape
                ) {}
            }
            Text(
                text = strings.stickers,
                color = tgColors.headerTitle,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(stickers.size, key = { index -> stickers[index].url }) { index ->
                    val sticker = stickers[index]
                    val normalizedUrl = remember(sticker.url) { sticker.url.normalizeNoveoUrl() }
                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onStickerSelected(sticker) },
                        color = tgColors.chatSurface.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (sticker.type == "tgs") {
                                TgsSticker(
                                    url = normalizedUrl,
                                    modifier = Modifier.fillMaxSize(),
                                    tint = tgColors.headerIcon
                                )
                            } else {
                                AsyncImage(
                                    model = normalizedUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
                if (stickers.isEmpty()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(4) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
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
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val saveUpdateLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/vnd.android.package-archive")
    ) { uri ->
        val source = updateInfo.localPath?.let(::File)
        if (uri != null && source?.exists() == true) {
            runCatching {
                source.inputStream().use { input ->
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        input.copyTo(output)
                    } ?: error("Unable to open destination")
                }
            }.onSuccess {
                Toast.makeText(context, strings.saveLocally, Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                    Spacer(Modifier.width(4.dp))
                    TextButton(
                        onClick = { saveUpdateLauncher.launch("noveo-${updateInfo.version}.apk") },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(strings.saveLocally, color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelLarge)
                    }
                } else if (!updateInfo.isDownloading) {
                    TextButton(onClick = { uriHandler.openUri(updateInfo.url) }, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(strings.manualUpdate, color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.width(4.dp))
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
    usersById: Map<String, UserSummary>,
    currentUserId: String?,
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
                        usersById = usersById,
                        currentUserId = currentUserId,
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


@Composable
fun VoiceCallOverlay(
    state: ir.hienob.noveo.data.VoiceChatState,
    strings: NoveoStrings,
    usersById: Map<String, UserSummary>,
    onLeave: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleDeafen: () -> Unit,
    onMinimize: () -> Unit
) {
    var sheetDragOffsetY by remember { mutableStateOf(0f) }
    val hideThresholdPx = with(LocalDensity.current) { 96.dp.toPx() }
    val joinedCount = remember(state.participantIds) { state.participantIds.distinct().size }
    val joinedCountLabel = remember(joinedCount, strings) { formatVoiceJoinedCount(joinedCount, strings) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, sheetDragOffsetY.roundToInt()) }
                .pointerInput(onMinimize) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, dragAmount ->
                            sheetDragOffsetY = (sheetDragOffsetY + dragAmount).coerceAtLeast(0f)
                        },
                        onDragEnd = {
                            if (sheetDragOffsetY > hideThresholdPx) {
                                onMinimize()
                            }
                            sheetDragOffsetY = 0f
                        },
                        onDragCancel = {
                            sheetDragOffsetY = 0f
                        }
                    )
                },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(38.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.28f))
                )

                Spacer(Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    IconButton(
                        onClick = onMinimize,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.Outlined.KeyboardArrowDown,
                            contentDescription = strings.minimize,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = strings.voiceChat,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Text(
                    text = when (state.connectionState) {
                        ir.hienob.noveo.data.VoiceConnectionState.CONNECTING -> strings.connecting
                        ir.hienob.noveo.data.VoiceConnectionState.RECONNECTING -> strings.connecting
                        ir.hienob.noveo.data.VoiceConnectionState.CONNECTED -> joinedCountLabel
                        else -> joinedCountLabel
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (state.participantIds.isEmpty()) {
                        VoiceParticipantListItem(
                            user = null,
                            isSpeaking = false,
                            statusLabel = strings.connecting
                        )
                    } else {
                        state.participantIds.forEach { id ->
                            val user = usersById[id]
                            val isSpeaking = state.activeSpeakers.contains(id)
                            VoiceParticipantListItem(
                                user = user,
                                isSpeaking = isSpeaking,
                                statusLabel = if (isSpeaking) "Speaking" else strings.online
                            )
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (state.isMuted) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (state.isMuted) Icons.Outlined.MicOff else Icons.Outlined.Mic,
                                contentDescription = if (state.isMuted) strings.micOn else strings.muted,
                                tint = if (state.isMuted) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = if (state.isMuted) strings.muted else strings.micOn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Button(
                        onClick = onLeave,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = strings.leave, tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onToggleDeafen,
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (state.isDeafened) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (state.isDeafened) Icons.Outlined.HeadsetOff else Icons.Outlined.Headset,
                                contentDescription = if (state.isDeafened) strings.audioOn else strings.deafened,
                                tint = if (state.isDeafened) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = if (state.isDeafened) strings.deafened else strings.audioOn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


private fun formatVoiceJoinedCount(count: Int, strings: NoveoStrings): String {
    val localizedCount = localizeDigits(count.toString(), strings.languageCode)
    val rawText = when (strings.languageCode) {
        "fa" -> "$localizedCount نفر پیوسته‌اند"
        "ar" -> "$localizedCount مشارك"
        "tr" -> "$localizedCount katildi"
        "de" -> if (count == 1) "$localizedCount Person beigetreten" else "$localizedCount Personen beigetreten"
        "ru" -> "$localizedCount участников"
        "zh" -> "$localizedCount 人已加入"
        "es" -> if (count == 1) "$localizedCount usuario unido" else "$localizedCount usuarios unidos"
        "fr" -> if (count == 1) "$localizedCount utilisateur a rejoint" else "$localizedCount utilisateurs ont rejoint"
        else -> if (count == 1) "$localizedCount user joined" else "$localizedCount users joined"
    }
    return if (strings.languageCode == "fa" || strings.languageCode == "ar") "\u200F$rawText" else rawText
}

@Composable
private fun VoiceParticipantListItem(
    user: UserSummary?,
    isSpeaking: Boolean,
    statusLabel: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSpeaking) {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                    ) {}
                }

                ProfileCircle(
                    name = user?.username ?: "User",
                    imageUrl = user?.avatarUrl,
                    size = 44.dp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user?.username?.split(" ")?.firstOrNull() ?: "User",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSpeaking) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "LIVE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun IncomingCallOverlay(
    call: SocketEvent.IncomingCall,
    strings: NoveoStrings,
    caller: UserSummary?,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Blurred-like background (using a dark semi-transparent overlay over the avatar)
            if (caller?.avatarUrl != null) {
                AsyncImage(
                    model = caller.avatarUrl.normalizeNoveoUrl(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().alpha(0.3f),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(vertical = 64.dp, horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ProfileCircle(name = caller?.username ?: "User", imageUrl = caller?.avatarUrl, size = 120.dp)
                    Spacer(Modifier.height(32.dp))
                    Text(
                        text = caller?.username ?: "Unknown Caller",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = strings.incomingCall,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onDecline,
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFFF44336), CircleShape)
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = strings.decline, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(text = strings.decline, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onAccept,
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        ) {
                            Icon(Icons.Outlined.Call, contentDescription = strings.accept, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(text = strings.accept, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceChatTray(
    state: ir.hienob.noveo.data.VoiceChatState,
    strings: NoveoStrings,
    onExpand: () -> Unit,
    onLeave: () -> Unit,
    onToggleMute: () -> Unit,
    tgColors: TelegramThemeColors = telegramColors()
) {
    Surface(
        color = tgColors.chatSurface,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onExpand() },
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleMute,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (state.isMuted) Icons.Outlined.MicOff else Icons.Outlined.Mic,
                    contentDescription = null,
                    tint = if (state.isMuted) MaterialTheme.colorScheme.error else tgColors.headerIcon
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = strings.activeCall,
                    style = MaterialTheme.typography.labelMedium,
                    color = tgColors.headerTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (state.isMuted) strings.muted else strings.micOn,
                    style = MaterialTheme.typography.labelSmall,
                    color = tgColors.headerSubtitle,
                    maxLines = 1
                )
            }

            IconButton(onClick = onLeave, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = null,
                    tint = tgColors.headerSubtitle
                )
            }
        }
    }
}
