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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.hienob.noveo.data.ChatMessage
import kotlin.math.roundToInt

private val CONTEXT_MENU_REACTIONS = listOf(
    "🙏", "🥰", "👍", "😭", "😍", "🙈", "🤣", "🔥", "❤️", "🤯",
    "🤬", "😢", "🎉", "🤩", "🤮", "💩", "👌", "🕊", "🤡", "🥱",
    "🥴", "🐳", "❤️‍🔥", "🌚", "🌭", "💯", "⚡️", "🏆", "💔", "🤨"
)
private val CONTEXT_MENU_QUICK_REACTIONS = CONTEXT_MENU_REACTIONS.take(6)

internal data class MessageContextMenuState(
    val message: ChatMessage,
    val ownMessage: Boolean,
    val bubbleBounds: Rect
)

@Composable
internal fun MessageContextMenu(
    state: MessageContextMenuState,
    expanded: Boolean,
    tgColors: TelegramThemeColors,
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

        var left = state.bubbleBounds.left
        val minLeft = with(density) { 8.dp.toPx() }
        val maxLeft = (screenWidthPx - targetWidthPx - with(density) { 12.dp.toPx() }).coerceAtLeast(minLeft)
        left = left.coerceIn(minLeft, maxLeft)

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
                color = tgColors.contextMenuReactionSurface,
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
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Reactions",
                                    fontSize = 13.sp,
                                    color = tgColors.contextMenuReactionMuted
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(tgColors.contextMenuReactionSecondary)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onExpandedChange(false) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = "Collapse reactions",
                                        tint = tgColors.contextMenuReactionMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        row.forEach { emoji ->
                                            ReactionButton(
                                                emoji = emoji,
                                                expanded = true,
                                                tgColors = tgColors,
                                                onClick = onDismiss
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CONTEXT_MENU_QUICK_REACTIONS.forEach { emoji ->
                                ReactionButton(
                                    emoji = emoji,
                                    expanded = false,
                                    tgColors = tgColors,
                                    onClick = onDismiss
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(tgColors.contextMenuReactionSecondary)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onExpandedChange(true) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "More reactions",
                                    tint = tgColors.contextMenuReactionMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !expanded,
                enter = fadeIn() + slideInVertically { -it / 8 },
                exit = fadeOut() + slideOutVertically { -it / 8 }
            ) {
                Surface(
                    color = tgColors.contextMenuBackground,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.width(200.dp).padding(vertical = 4.dp)) {
                        ContextMenuActionItem(
                            label = "Reply",
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = null,
                                    tint = tgColors.contextMenuIcon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            tgColors = tgColors,
                            onClick = onReply
                        )
                        ContextMenuActionItem(
                            label = "Pin",
                            icon = {
                                Icon(
                                    Icons.Outlined.Bookmark,
                                    contentDescription = null,
                                    tint = tgColors.contextMenuIcon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            tgColors = tgColors,
                            onClick = onDismiss
                        )
                        ContextMenuActionItem(
                            label = "Copy Text",
                            icon = {
                                Icon(
                                    Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = tgColors.contextMenuIcon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            tgColors = tgColors,
                            onClick = onCopyText
                        )
                        ContextMenuActionItem(
                            label = "Forward",
                            icon = {
                                Icon(
                                    Icons.Outlined.ArrowForward,
                                    contentDescription = null,
                                    tint = tgColors.contextMenuIcon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            tgColors = tgColors,
                            onClick = onDismiss
                        )
                        ContextMenuActionItem(
                            label = "Delete",
                            icon = {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = null,
                                    tint = tgColors.contextMenuIcon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            tgColors = tgColors,
                            onClick = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReactionButton(
    emoji: String,
    expanded: Boolean,
    tgColors: TelegramThemeColors,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (expanded) tgColors.contextMenuReactionSecondary else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 16.sp)
    }
}

@Composable
private fun ContextMenuActionItem(
    label: String,
    icon: @Composable () -> Unit,
    tgColors: TelegramThemeColors,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = tgColors.contextMenuText
        )
    }
}
