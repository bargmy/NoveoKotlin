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
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.math.roundToInt

private val CONTEXT_MENU_REACTIONS = listOf(
    "\uD83D\uDE4F", "\uD83E\uDD70", "\uD83D\uDC4D", "\uD83D\uDE2D", "\uD83D\uDE0D", "\uD83D\uDE48",
    "\uD83E\uDD23", "\uD83D\uDD25", "\u2764\uFE0F", "\uD83E\uDD2F", "\uD83E\uDD2C", "\uD83D\uDE22",
    "\uD83C\uDF89", "\uD83E\uDD29", "\uD83E\uDD2E", "\uD83D\uDCA9", "\uD83D\uDC4C", "\uD83D\uDD4A\uFE0F",
    "\uD83E\uDD21", "\uD83E\uDD71", "\uD83E\uDD74", "\uD83D\uDC33", "\u2764\uFE0F\u200D\uD83D\uDD25", "\uD83C\uDF1A",
    "\uD83C\uDF2D", "\uD83D\uDCAF", "\u26A1\uFE0F", "\uD83C\uDFC6", "\uD83D\uDC94", "\uD83E\uDD28"
)
private val CONTEXT_MENU_QUICK_REACTIONS = CONTEXT_MENU_REACTIONS.take(6)

private val MenuOverlay = Color.Black.copy(alpha = 0.42f)
private val MenuSurface = Color(0xFF242428)
private val MenuSecondary = Color(0xFF3A3A40)
private val MenuMuted = Color(0xFFB8B8C0)
private val MenuIcon = Color(0xFFEDEDF2)
private val MenuText = Color.White

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
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MenuOverlay)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )
        MessageContextMenu(
            state = state,
            expanded = expanded,
            onDismiss = onDismiss,
            onExpandedChange = onExpandedChange,
            onReply = onReply,
            onCopyText = onCopyText
        )
    }
}

@Composable
private fun MessageContextMenu(
    state: MessageContextMenuState,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onReply: () -> Unit,
    onCopyText: () -> Unit
) {
    val wrapperScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "contextMenuScale"
    )
    val reactionsWidth by animateDpAsState(
        targetValue = if (expanded) 250.dp else 220.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "reactionsWidth"
    )
    val reactionsHeight by animateDpAsState(
        targetValue = if (expanded) 240.dp else 36.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "reactionsHeight"
    )
    val reactionsRadius by animateDpAsState(
        targetValue = if (expanded) 16.dp else 20.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "reactionsRadius"
    )

    val density = LocalDensity.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val targetWidthPx = with(density) { (if (expanded) 250.dp else 220.dp).toPx() }
        val targetHeightPx = with(density) { (if (expanded) 240.dp else 222.dp).toPx() }

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
                    alpha = 1f
                    scaleX = wrapperScale
                    scaleY = wrapperScale
                    transformOrigin = TransformOrigin(0f, 1f)
                },
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                color = MenuSurface,
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
                        ExpandedReactions(onExpandedChange = onExpandedChange, onDismiss = onDismiss)
                    } else {
                        CompactReactions(onExpandedChange = onExpandedChange, onDismiss = onDismiss)
                    }
                }
            }

            AnimatedVisibility(
                visible = !expanded,
                enter = fadeIn() + slideInVertically { -it / 8 },
                exit = fadeOut() + slideOutVertically { -it / 8 }
            ) {
                Surface(
                    color = MenuSurface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.width(200.dp).padding(vertical = 4.dp)) {
                        ContextMenuActionItem(
                            label = "Reply",
                            icon = { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, tint = MenuIcon, modifier = Modifier.size(18.dp)) },
                            onClick = onReply
                        )
                        ContextMenuActionItem(
                            label = "Pin",
                            icon = { Icon(Icons.Outlined.Bookmark, contentDescription = null, tint = MenuIcon, modifier = Modifier.size(18.dp)) },
                            onClick = onDismiss
                        )
                        ContextMenuActionItem(
                            label = "Copy Text",
                            icon = { Icon(Icons.Outlined.Description, contentDescription = null, tint = MenuIcon, modifier = Modifier.size(18.dp)) },
                            onClick = onCopyText
                        )
                        ContextMenuActionItem(
                            label = "Forward",
                            icon = { Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = MenuIcon, modifier = Modifier.size(18.dp)) },
                            onClick = onDismiss
                        )
                        ContextMenuActionItem(
                            label = "Delete",
                            icon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = MenuIcon, modifier = Modifier.size(18.dp)) },
                            onClick = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedReactions(
    onExpandedChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reactions", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MenuMuted)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MenuSecondary)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onExpandedChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Collapse reactions", tint = MenuMuted, modifier = Modifier.size(16.dp))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CONTEXT_MENU_REACTIONS.chunked(6).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { emoji ->
                        ReactionButton(emoji = emoji, expanded = true, onClick = onDismiss)
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactReactions(
    onExpandedChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CONTEXT_MENU_QUICK_REACTIONS.forEach { emoji ->
            ReactionButton(emoji = emoji, expanded = false, onClick = onDismiss)
        }
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(MenuSecondary)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onExpandedChange(true) },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "More reactions", tint = MenuMuted, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun ReactionButton(
    emoji: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (expanded) MenuSecondary else Color.Transparent)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 16.sp)
    }
}

@Composable
private fun ContextMenuActionItem(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(12.dp))
        Text(text = label, fontSize = 14.sp, color = MenuText)
    }
}
