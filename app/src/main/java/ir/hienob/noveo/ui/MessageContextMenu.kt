package ir.hienob.noveo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.hienob.noveo.data.ChatMessage
import java.util.Locale
import kotlin.math.roundToInt

internal val CONTEXT_MENU_REACTIONS = listOf(
    "🙏", "🥰", "👍", "😭", "😍", "🙈", "🤣", "🔥", "❤️", "🤯", 
    "🤬", "😢", "🎉", "🤩", "🤮", "💩", "👌", "🕊️", "🤡", "🥱", 
    "🥴", "🐳", "❤️‍🔥", "🌚", "🌭", "💯", "⚡", "🏆", "💔", "🤨"
)
private val CONTEXT_MENU_QUICK_REACTIONS = CONTEXT_MENU_REACTIONS.take(6)

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
    val overlayColor = Color.Black.copy(alpha = 0.4f)
    
    Box(modifier = modifier.fillMaxSize()) {
        // Blur Background Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
                .blur(2.dp) // Exact blur from HTML logic
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
    // Exact colors from HTML/Themes
    val menuBg = if (tgColors.isDark) Color(0xFF21212B) else Color(0xFFFFFFFF)
    val menuText = if (tgColors.isDark) Color.White else Color.Black
    val menuIcon = if (tgColors.isDark) Color.White else Color.Gray
    val hoverBg = Color.White.copy(alpha = 0.08f)
    
    val springSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    val sizeSpringSpec = spring<androidx.compose.ui.unit.IntSize>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateIn = true }

    val scale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.9f,
        animationSpec = springSpec,
        label = "menuScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(200),
        label = "menuAlpha"
    )

    val density = LocalDensity.current
    
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        
        // Exact sizes from HTML
        val menuWidth = 200.dp
        val reactionsWidth = if (expanded) 250.dp else 220.dp
        val reactionsHeight = if (expanded) 240.dp else 36.dp
        val gap = 6.dp
        
        val totalHeightPx = with(density) { (reactionsHeight + gap + (if (expanded) 0.dp else 180.dp)).toPx() }
        val targetWidthPx = with(density) { maxOf(menuWidth, reactionsWidth).toPx() }

        val minLeft = with(density) { 8.dp.toPx() }
        val maxLeft = (screenWidthPx - targetWidthPx - with(density) { 12.dp.toPx() }).coerceAtLeast(minLeft)
        val left = state.bubbleBounds.left.coerceIn(minLeft, maxLeft)

        var top = state.bubbleBounds.top - totalHeightPx - with(density) { 10.dp.toPx() }
        val originY = if (top < with(density) { 16.dp.toPx() }) {
            top = state.bubbleBounds.bottom + with(density) { 10.dp.toPx() }
            0f // Top origin
        } else {
            1f // Bottom origin
        }
        
        Column(
            modifier = Modifier
                .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                    transformOrigin = TransformOrigin(0f, originY)
                }
                .wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            // Reactions Box
            Surface(
                color = menuBg,
                shape = RoundedCornerShape(if (expanded) 16.dp else 20.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .width(reactionsWidth)
                    .animateContentSize(animationSpec = sizeSpringSpec)
            ) {
                Box(modifier = Modifier.height(reactionsHeight)) {
                    if (expanded) {
                        ExpandedReactionsGrid(
                            onReaction = onReaction,
                            onMinimize = { onExpandedChange(false) },
                            strings = strings
                        )
                    } else {
                        CompactReactionsRow(
                            onReaction = onReaction,
                            onExpand = { onExpandedChange(true) }
                        )
                    }
                }
            }

            // Action Menu
            AnimatedVisibility(
                visible = !expanded,
                enter = fadeIn() + scaleIn(initialScale = 0.95f, transformOrigin = TransformOrigin(0.5f, 0f)),
                exit = fadeOut() + scaleOut(targetScale = 0.95f, transformOrigin = TransformOrigin(0.5f, 0f))
            ) {
                Surface(
                    color = menuBg,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier.width(menuWidth)
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        MenuItem(
                            label = strings.reply,
                            icon = Icons.AutoMirrored.Outlined.ArrowBack,
                            color = menuText,
                            onClick = onReply
                        )
                        if (state.ownMessage && state.message.content.text != null) {
                            MenuItem(
                                label = strings.edit,
                                icon = Icons.Outlined.Edit,
                                color = menuText,
                                onClick = onEdit
                            )
                        }
                        MenuItem(
                            label = if (state.message.isPinned) strings.unpin else strings.pin,
                            icon = Icons.Outlined.PushPin,
                            color = menuText,
                            onClick = onPin
                        )
                        if (state.message.content.text != null) {
                            MenuItem(
                                label = strings.copyText,
                                icon = Icons.Outlined.ContentCopy,
                                color = menuText,
                                onClick = onCopyText
                            )
                        }
                        MenuItem(
                            label = strings.forward,
                            icon = Icons.Outlined.ArrowForward,
                            color = menuText,
                            onClick = onForward
                        )
                        
                        val file = state.message.content.file
                        if (file != null && (file.isImage() || file.isTgsSticker())) {
                            MenuItem(
                                label = strings.addAsSticker,
                                icon = Icons.Outlined.Star,
                                color = menuText,
                                onClick = onAddAsSticker
                            )
                        }

                        if (state.message.content.file != null) {
                            MenuItem(
                                label = strings.download,
                                icon = Icons.Outlined.FileDownload,
                                color = menuText,
                                onClick = onDownload
                            )
                        }
                        MenuItem(
                            label = strings.delete,
                            icon = Icons.Outlined.Delete,
                            color = Color(0xFFE53935),
                            onClick = onDelete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactReactionsRow(
    onReaction: (String) -> Unit,
    onExpand: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CONTEXT_MENU_QUICK_REACTIONS.forEach { emoji ->
            Text(
                text = emoji,
                fontSize = 18.sp,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onReaction(emoji) }
                    )
                    .wrapContentSize(Alignment.Center)
            )
        }
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "More",
            tint = Color.Gray,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .clickable(onClick = onExpand)
                .padding(2.dp)
        )
    }
}

@Composable
private fun ExpandedReactionsGrid(
    onReaction: (String) -> Unit,
    onMinimize: () -> Unit,
    strings: NoveoStrings
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(strings.reactions, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            IconButton(onClick = onMinimize, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            }
        }
        Divider(color = Color.White.copy(alpha = 0.05f))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxSize().padding(4.dp),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(CONTEXT_MENU_REACTIONS) { emoji ->
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.03f))
                        .clickable { onReaction(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, color = color, fontSize = 14.sp)
    }
}

