package ir.hienob.noveo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.hienob.noveo.data.ChatMessage
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

internal val CONTEXT_MENU_REACTIONS = listOf(
    "🙏", "👍", "😭", "😍", "🥰", "🙈", "❤️", "🤔", "🤣", "😘", "😱", "💯", "👎", "🔥", "💩", "🤯",
    "💔", "☃️", "😁", "🎉", "🤷", "😇", "🎃", "🗿", "🥴", "😐", "👏", "🤬", "😢", "🤩", "🤮", "👌",
    "🕊️", "🤡", "🐳", "💘", "🌭", "⚡", "🍌", "🏆", "🤨", "🍓", "🍾", "🖕", "😈", "🤔", "😴", "🤓", "👻",
    "👨‍💻", "👀", "🙉", "😨", "🤝", "✍️", "🤗", "🫡", "🎅", "🎄", "💅", "🤪", "🆒", "🦄", "💊", "🙊",
    "😎", "👾"
)
private val CONTEXT_MENU_QUICK_REACTIONS = CONTEXT_MENU_REACTIONS.take(7)

internal data class MessageContextMenuState(
    val message: ChatMessage,
    val ownMessage: Boolean,
    val bubbleBounds: Rect
)

@Composable
internal fun MessageContextMenuOverlay(
    state: MessageContextMenuState,
    expanded: Boolean,
    tgColors: TelegramThemeColors,
    onDismiss: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onReply: () -> Unit,
    onCopyText: () -> Unit,
    onReaction: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit,
    onForward: () -> Unit,
    onDownload: () -> Unit,
    onAddAsSticker: () -> Unit,
    strings: NoveoStrings,
    modifier: Modifier = Modifier
) {
    val overlayColor = Color.Black.copy(alpha = 0.42f)
    
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )
        MessageContextMenu(
            state = state,
            expanded = expanded,
            tgColors = tgColors,
            onDismiss = onDismiss,
            onExpandedChange = onExpandedChange,
            onReply = onReply,
            onCopyText = onCopyText,
            onReaction = onReaction,
            onEdit = onEdit,
            onDelete = onDelete,
            onPin = onPin,
            onForward = onForward,
            onDownload = onDownload,
            onAddAsSticker = onAddAsSticker,
            strings = strings
        )
    }
}

@Composable
private fun MessageContextMenu(
    state: MessageContextMenuState,
    expanded: Boolean,
    tgColors: TelegramThemeColors,
    onDismiss: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onReply: () -> Unit,
    onCopyText: () -> Unit,
    onReaction: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit,
    onForward: () -> Unit,
    onDownload: () -> Unit,
    onAddAsSticker: () -> Unit,
    strings: NoveoStrings
) {
    val menuSurface = tgColors.incomingBubble
    val menuSecondary = if (tgColors.isDark) Color(0xFF2C353F) else Color(0xFFF0F2F5)
    val menuMuted = tgColors.incomingTime
    val menuIcon = tgColors.headerIcon
    val menuText = tgColors.incomingText
    val deleteColor = Color(0xFFE53935)

    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateIn = true }

    val wrapperScale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.85f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "contextMenuScale"
    )
    val wrapperAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(200),
        label = "contextMenuAlpha"
    )

    val reactionsWidth by animateDpAsState(
        targetValue = if (expanded) 320.dp else 320.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "reactionsWidth"
    )
    val reactionsHeight by animateDpAsState(
        targetValue = if (expanded) 320.dp else 52.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "reactionsHeight"
    )
    val reactionsRadius by animateDpAsState(
        targetValue = if (expanded) 18.dp else 26.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "reactionsRadius"
    )

    val density = LocalDensity.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val targetWidthPx = with(density) { (if (expanded) 320.dp else 290.dp).toPx() }
        val targetHeightPx = with(density) { (if (expanded) 280.dp else 240.dp).toPx() }

        val minLeft = with(density) { 8.dp.toPx() }
        val maxLeft = (screenWidthPx - targetWidthPx - with(density) { 12.dp.toPx() }).coerceAtLeast(minLeft)
        val left = state.bubbleBounds.left.coerceIn(minLeft, maxLeft)

        var top = state.bubbleBounds.top - targetHeightPx - with(density) { 10.dp.toPx() }
        if (top < with(density) { 16.dp.toPx() }) {
            top = state.bubbleBounds.bottom + with(density) { 10.dp.toPx() }
        }
        val minTop = with(density) { 8.dp.toPx() }
        val maxTop = (screenHeightPx - targetHeightPx - minTop).coerceAtLeast(minTop)
        top = top.coerceIn(minTop, maxTop)

        Column(
            modifier = Modifier
                .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                .wrapContentWidth()
                .wrapContentHeight()
                .graphicsLayer {
                    alpha = wrapperAlpha
                    scaleX = wrapperScale
                    scaleY = wrapperScale
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                },
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                color = menuSurface,
                shape = RoundedCornerShape(reactionsRadius),
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .width(reactionsWidth)
                        .height(reactionsHeight)
                        .animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing))
                ) {
                    if (expanded) {
                        ExpandedReactions(
                            strings = strings,
                            menuSecondary = menuSecondary,
                            menuMuted = menuMuted,
                            onExpandedChange = onExpandedChange,
                            onReaction = onReaction
                        )
                    } else {
                        CompactReactions(
                            menuSecondary = menuSecondary,
                            menuMuted = menuMuted,
                            onExpandedChange = onExpandedChange,
                            onReaction = onReaction
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !expanded,
                enter = fadeIn() + slideInVertically { -it / 8 },
                exit = fadeOut() + slideOutVertically { -it / 8 }
            ) {
                Surface(
                    color = menuSurface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.width(220.dp).padding(vertical = 4.dp)) {
                        ContextMenuActionItem(
                            label = strings.reply,
                            icon = { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, tint = menuIcon, modifier = Modifier.size(18.dp)) },
                            textColor = menuText,
                            onClick = onReply
                        )
                        if (state.ownMessage && state.message.content.text != null) {
                            ContextMenuActionItem(
                                label = strings.edit,
                                icon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = menuIcon, modifier = Modifier.size(18.dp)) },
                                textColor = menuText,
                                onClick = onEdit
                            )
                        }
                        ContextMenuActionItem(
                            label = if (state.message.isPinned) strings.unpin else strings.pin,
                            icon = { Icon(Icons.Outlined.Bookmark, contentDescription = null, tint = menuIcon, modifier = Modifier.size(18.dp)) },
                            textColor = menuText,
                            onClick = onPin
                        )
                        if (state.message.content.text != null) {
                            ContextMenuActionItem(
                                label = strings.copyText,
                                icon = { Icon(Icons.Outlined.Description, contentDescription = null, tint = menuIcon, modifier = Modifier.size(18.dp)) },
                                textColor = menuText,
                                onClick = onCopyText
                            )
                        }
                        ContextMenuActionItem(
                            label = strings.forward,
                            icon = { Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = menuIcon, modifier = Modifier.size(18.dp)) },
                            textColor = menuText,
                            onClick = onForward
                        )
                        
                        val file = state.message.content.file
                        if (file != null && (file.isImage() || file.isTgsSticker())) {
                            ContextMenuActionItem(
                                label = strings.addAsSticker,
                                icon = { Icon(Icons.Outlined.Star, contentDescription = null, tint = menuIcon, modifier = Modifier.size(18.dp)) },
                                textColor = menuText,
                                onClick = onAddAsSticker
                            )
                        }

                        if (state.message.content.file != null) {
                            ContextMenuActionItem(
                                label = strings.download,
                                icon = { Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = menuIcon, modifier = Modifier.size(18.dp)) },
                                textColor = menuText,
                                onClick = onDownload
                            )
                        }
                        ContextMenuActionItem(
                            label = strings.delete,
                            icon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = deleteColor, modifier = Modifier.size(18.dp)) },
                            textColor = deleteColor,
                            onClick = onDelete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedReactions(
    strings: NoveoStrings,
    menuSecondary: Color,
    menuMuted: Color,
    onExpandedChange: (Boolean) -> Unit,
    onReaction: (String) -> Unit
) {
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(strings.reactions, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = menuMuted)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(menuSecondary)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onExpandedChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Collapse reactions", tint = menuMuted, modifier = Modifier.size(18.dp))
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            gridState.scrollBy(-dragAmount)
                        }
                    }
                }
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = true
            ) {
                items(CONTEXT_MENU_REACTIONS) { emoji ->
                    ReactionButton(
                        emoji = emoji, 
                        expanded = true, 
                        menuSecondary = menuSecondary, 
                        onClick = { onReaction(emoji) }
                    )
                }
                
                item {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CompactReactions(
    menuSecondary: Color,
    menuMuted: Color,
    onExpandedChange: (Boolean) -> Unit,
    onReaction: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CONTEXT_MENU_QUICK_REACTIONS.forEach { emoji ->
            ReactionButton(emoji = emoji, expanded = false, menuSecondary = menuSecondary, onClick = { onReaction(emoji) })
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(menuSecondary)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onExpandedChange(true) },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "More reactions", tint = menuMuted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ReactionButton(
    emoji: String,
    expanded: Boolean,
    menuSecondary: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (expanded) menuSecondary else Color.Transparent)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 20.sp)
    }
}

@Composable
private fun ContextMenuActionItem(
    label: String,
    icon: @Composable () -> Unit,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(14.dp))
        Text(text = label, fontSize = 15.sp, color = textColor)
    }
}

private fun ir.hienob.noveo.data.MessageFileAttachment.isImage(): Boolean {
    val nameValue = name.lowercase(Locale.getDefault())
    return nameValue.endsWith(".png") || nameValue.endsWith(".jpg") || nameValue.endsWith(".jpeg") || nameValue.endsWith(".webp") || nameValue.endsWith(".gif")
}

private fun ir.hienob.noveo.data.MessageFileAttachment.isVideo(): Boolean {
    val nameValue = name.lowercase(Locale.getDefault())
    return nameValue.endsWith(".mp4") || nameValue.endsWith(".mov") || nameValue.endsWith(".webm")
}
