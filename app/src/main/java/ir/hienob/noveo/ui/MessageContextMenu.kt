package ir.hienob.noveo.ui

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.hienob.noveo.data.ChatMessage
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Optional, immutable, pre-blurred capture supplied by an existing chat screen. */
internal data class LiquidGlassBackdrop(
    val image: ImageBitmap,
    val viewportSize: IntSize,
    val windowOrigin: IntOffset
)

/**
 * Compatibility helper for callers that used the earlier drop-in version.
 * Capture ONLY before the popup is displayed; never call this from a composable,
 * animation, draw callback, or during grid scrolling. A smaller single-pass blur
 * replaces the old 780px / six-pass / per-pixel colour-processing implementation.
 * No capture is required by the menu; if absent, the native theme surface is used.
 */
internal fun captureLiquidGlassBackdrop(
    chatView: View,
    maxImageDimensionPx: Int = 360
): LiquidGlassBackdrop? {
    val viewWidth = chatView.width
    val viewHeight = chatView.height
    if (viewWidth <= 0 || viewHeight <= 0) return null
    val scale = min(1f, maxImageDimensionPx.coerceIn(64, 480).toFloat() / max(viewWidth, viewHeight))
    val width = max(1, (viewWidth * scale).roundToInt())
    val height = max(1, (viewHeight * scale).roundToInt())
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    canvas.scale(width.toFloat() / viewWidth, height.toFloat() / viewHeight)
    chatView.draw(canvas)
    // The deliberate low resolution itself provides part of the blur. One O(width*height)
    // separable box pass is sufficient here and avoids stalling the main thread for 6 passes.
    val src = IntArray(width * height)
    bitmap.getPixels(src, 0, width, 0, 0, width, height)
    val temp = IntArray(src.size)
    val radius = (width / 80).coerceIn(2, 6)
    for (y in 0 until height) {
        val row = y * width
        for (x in 0 until width) {
            var a = 0; var r = 0; var g = 0; var b = 0
            val lo = max(0, x - radius)
            val hi = min(width - 1, x + radius)
            for (xx in lo..hi) {
                val pixel = src[row + xx]
                a += pixel ushr 24
                r += pixel ushr 16 and 255
                g += pixel ushr 8 and 255
                b += pixel and 255
            }
            val n = hi - lo + 1
            temp[row + x] = ((a / n) shl 24) or ((r / n) shl 16) or
                ((g / n) shl 8) or (b / n)
        }
    }
    for (y in 0 until height) {
        for (x in 0 until width) {
            var a = 0; var r = 0; var g = 0; var b = 0
            val lo = max(0, y - radius)
            val hi = min(height - 1, y + radius)
            for (yy in lo..hi) {
                val pixel = temp[yy * width + x]
                a += pixel ushr 24
                r += pixel ushr 16 and 255
                g += pixel ushr 8 and 255
                b += pixel and 255
            }
            val n = hi - lo + 1
            src[y * width + x] = ((a / n) shl 24) or ((r / n) shl 16) or
                ((g / n) shl 8) or (b / n)
        }
    }
    bitmap.setPixels(src, 0, width, 0, 0, width, height)
    val windowPosition = IntArray(2)
    chatView.getLocationInWindow(windowPosition)
    return LiquidGlassBackdrop(
        bitmap.asImageBitmap(), IntSize(viewWidth, viewHeight),
        IntOffset(windowPosition[0], windowPosition[1])
    )
}

private data class MenuGlassPalette(
    val ink: Color,
    val muted: Color,
    val accent: Color,
    val delete: Color,
    val base: Color,
    val tint: Color,
    val edge: Color,
    val sheen: Color,
    val shadow: Color,
    val pressed: Color
)

@Composable
private fun menuGlassPalette(tgColors: TelegramThemeColors): MenuGlassPalette {
    // The selected app theme can be dynamic/custom, not merely a light/dark toggle.
    val scheme = MaterialTheme.colorScheme
    val dark = tgColors.isDark
    return remember(tgColors, scheme) {
        MenuGlassPalette(
            ink = scheme.onSurface,
            muted = scheme.onSurfaceVariant,
            accent = scheme.primary,
            delete = scheme.error,
            base = tgColors.incomingBubble.copy(alpha = if (dark) 0.86f else 0.82f),
            tint = scheme.surface.copy(alpha = if (dark) 0.68f else 0.56f),
            edge = scheme.onSurface.copy(alpha = if (dark) 0.21f else 0.12f),
            sheen = Color.White.copy(alpha = if (dark) 0.14f else 0.34f),
            shadow = Color.Black.copy(alpha = if (dark) 0.36f else 0.17f),
            pressed = scheme.primary.copy(alpha = if (dark) 0.19f else 0.10f)
        )
    }
}

/**
 * A single cached set of glass brushes. No touch-tracking state, per-frame shader
 * reconstruction, animated sizing or dynamic measure/position feedback.
 */
@Composable
private fun MenuGlassSurface(
    palette: MenuGlassPalette,
    modifier: Modifier = Modifier,
    radius: Dp = 25.dp,
    backdrop: LiquidGlassBackdrop? = null,
    content: @Composable () -> Unit
) {
    val shape = remember(radius) { RoundedCornerShape(radius) }
    val density = LocalDensity.current
    val radiusPx = with(density) { radius.toPx() }
    val edgePx = with(density) { 1.dp.toPx() }
    var windowOffset by remember(backdrop) { mutableStateOf(Offset.Unspecified) }
    Box(
        modifier = modifier
            .shadow(12.dp, shape, clip = false, ambientColor = palette.shadow, spotColor = palette.shadow)
            .onGloballyPositioned {
                if (backdrop != null) {
                    val next = it.positionInWindow()
                    if (next != windowOffset) windowOffset = next
                }
            }
            .clip(shape)
            .drawWithCache {
                val width = size.width
                val height = size.height
                val diagonal = Brush.linearGradient(
                    0f to palette.sheen,
                    0.29f to palette.sheen.copy(alpha = palette.sheen.alpha * .26f),
                    0.62f to Color.Transparent,
                    1f to palette.sheen.copy(alpha = palette.sheen.alpha * .14f),
                    start = Offset.Zero,
                    end = Offset(width, height)
                )
                val highlight = Brush.radialGradient(
                    0f to palette.sheen,
                    0.44f to palette.sheen.copy(alpha = palette.sheen.alpha * .20f),
                    1f to Color.Transparent,
                    center = Offset(width * .18f, 0f),
                    radius = max(width, height) * .95f
                )
                val edgeLight = Brush.verticalGradient(
                    0f to palette.sheen.copy(alpha = palette.sheen.alpha * .90f),
                    .6f to Color.Transparent,
                    1f to palette.edge,
                    startY = 0f,
                    endY = height
                )
                onDrawWithContent {
                    val snapshot = backdrop
                    val origin = windowOffset
                    if (snapshot != null && origin != Offset.Unspecified &&
                        snapshot.viewportSize.width > 0 && snapshot.viewportSize.height > 0) {
                        val bitmap = snapshot.image
                        val sx = bitmap.width.toFloat() / snapshot.viewportSize.width
                        val sy = bitmap.height.toFloat() / snapshot.viewportSize.height
                        val left = ((origin.x - snapshot.windowOrigin.x) * sx).roundToInt()
                        val top = ((origin.y - snapshot.windowOrigin.y) * sy).roundToInt()
                        val right = (left + size.width * sx).roundToInt()
                        val bottom = (top + size.height * sy).roundToInt()
                        val x0 = left.coerceIn(0, bitmap.width)
                        val y0 = top.coerceIn(0, bitmap.height)
                        val x1 = right.coerceIn(0, bitmap.width)
                        val y1 = bottom.coerceIn(0, bitmap.height)
                        if (x1 > x0 && y1 > y0) {
                            drawRect(palette.base)
                            drawImage(
                                image = bitmap,
                                srcOffset = IntOffset(x0, y0),
                                srcSize = IntSize(x1 - x0, y1 - y0),
                                dstOffset = IntOffset(
                                    ((x0 - left) / sx).roundToInt(),
                                    ((y0 - top) / sy).roundToInt()
                                ),
                                dstSize = IntSize(
                                    max(1, ((x1 - x0) / sx).roundToInt()),
                                    max(1, ((y1 - y0) / sy).roundToInt())
                                ),
                                filterQuality = FilterQuality.Low
                            )
                            drawRect(palette.tint)
                        } else {
                            drawRect(palette.base)
                        }
                    } else {
                        drawRect(palette.base)
                    }
                    drawRect(diagonal)
                    drawRect(highlight)
                    drawContent()
                    if (width > edgePx * 2 && height > edgePx * 2) {
                        drawRoundRect(
                            color = palette.edge,
                            topLeft = Offset(edgePx / 2, edgePx / 2),
                            size = Size(width - edgePx, height - edgePx),
                            cornerRadius = CornerRadius(radiusPx),
                            style = Stroke(edgePx)
                        )
                        drawRoundRect(
                            brush = edgeLight,
                            topLeft = Offset(edgePx * 1.5f, edgePx * 1.5f),
                            size = Size(width - edgePx * 3, height - edgePx * 3),
                            cornerRadius = CornerRadius(max(0f, radiusPx - edgePx)),
                            style = Stroke(edgePx)
                        )
                    }
                }
            },
        contentAlignment = Alignment.TopStart
    ) { content() }
}

internal val CONTEXT_MENU_REACTIONS = listOf(
    "🙏", "👍", "😭", "😍", "🥰", "🙈", "❤️", "🤔", "🤣", "😘", "😱", "💯", "👎", "🔥", "💩", "🤯",
    "💔", "☃️", "😁", "🎉", "🤷", "😇", "🎃", "🗿", "🥴", "😐", "👏", "🤬", "😢", "🤩", "🤮", "👌",
    "🕊️", "🤡", "🐳", "💘", "🌭", "⚡", "🍌", "🏆", "🤨", "🍓", "🍾", "🖕", "😈", "🤔", "😴", "🤓", "👻",
    "👨‍💻", "👀", "🙉", "😨", "🤝", "✍️", "🤗", "🫡", "🎅", "🎄", "💅", "🤪", "🆒", "🦄", "💊", "🙊",
    "😎", "👾"
)
private val QUICK_REACTIONS = CONTEXT_MENU_REACTIONS.take(7)

internal data class MessageContextMenuState(
    val message: ChatMessage,
    val ownMessage: Boolean,
    val bubbleBounds: Rect,
    val tapPosition: Offset = Offset.Unspecified
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
    onSeenBy: () -> Unit,
    onAddAsSticker: () -> Unit,
    strings: NoveoStrings,
    modifier: Modifier = Modifier,
    backdrop: LiquidGlassBackdrop? = null
) {
    val palette = menuGlassPalette(tgColors)
    BackHandler { onDismiss() }
    Box(modifier.fillMaxSize()) {
        Box(
            Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = if (tgColors.isDark) .26f else .13f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )
        MessageContextMenu(
            state, expanded, palette, backdrop, strings,
            onExpandedChange, onReply, onCopyText, onReaction, onEdit,
            onDelete, onPin, onForward, onDownload, onSeenBy, onAddAsSticker
        )
    }
}

@Composable
private fun MessageContextMenu(
    state: MessageContextMenuState,
    expanded: Boolean,
    palette: MenuGlassPalette,
    backdrop: LiquidGlassBackdrop?,
    strings: NoveoStrings,
    onExpandedChange: (Boolean) -> Unit,
    onReply: () -> Unit,
    onCopyText: () -> Unit,
    onReaction: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit,
    onForward: () -> Unit,
    onDownload: () -> Unit,
    onSeenBy: () -> Unit,
    onAddAsSticker: () -> Unit
) {
    val density = LocalDensity.current
    var show by remember(state.message.id) { mutableStateOf(false) }
    LaunchedEffect(state.message.id) { show = true }
    val alpha by animateFloatAsState(if (show) 1f else 0f, tween(140), label = "menuFade")
    // Only the initial appearance animates. Expanding the grid never animates its
    // containing height or scales the emoji grid: that was causing repeated relayouts.
    val scale by animateFloatAsState(if (show) 1f else .97f, tween(140), label = "menuScale")

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val horizontalInset = 8.dp
        val topInset = 8.dp
        val bottomInset = 16.dp
        val gap = 6.dp
        val menuWidth = minOf(320.dp, (maxWidth - horizontalInset * 2).coerceAtLeast(1.dp))
        val actionsWidth = minOf(244.dp, menuWidth)
        val reactionHeight = 52.dp
        val rowHeight = if (density.fontScale > 1.35f) 60.dp else 48.dp
        val actionCount = (if (state.message.chatType != "channel") 1 else 0) +
            (if (state.ownMessage && state.message.content.text != null) 1 else 0) +
            1 + (if (state.message.content.text != null) 1 else 0) + 1 +
            (if (state.message.seenBy.isNotEmpty() && state.ownMessage && state.message.chatType != "channel") 1 else 0) +
            (if (state.message.content.file?.let { it.isImage() || it.isTgsSticker() } == true) 1 else 0) +
            (if (state.message.content.file != null) 1 else 0) +
            (if (state.message.chatType != "channel") 1 else 0)
        val availableHeight = (maxHeight - topInset - bottomInset).coerceAtLeast(1.dp)
        val actionMaxHeight = (availableHeight - reactionHeight - gap).coerceAtLeast(1.dp)
        val actionsHeight = minOf(actionMaxHeight, rowHeight * actionCount + 8.dp)
        val expandedHeight = minOf(324.dp, availableHeight)
        val totalHeight = if (expanded) expandedHeight else reactionHeight + gap + actionsHeight
        val contentWidthPx = with(density) { menuWidth.toPx() }
        val totalHeightPx = with(density) { totalHeight.toPx() }
        val xMarginPx = with(density) { horizontalInset.toPx() }
        val topMarginPx = with(density) { topInset.toPx() }
        val bottomMarginPx = with(density) { bottomInset.toPx() }
        val bubbleGapPx = with(density) { 10.dp.toPx() }
        val viewWidthPx = with(density) { maxWidth.toPx() }
        val viewHeightPx = with(density) { maxHeight.toPx() }
        val anchor = state.tapPosition.takeIf { it != Offset.Unspecified }
        val anchorX = anchor?.x ?: if (state.ownMessage) state.bubbleBounds.right else state.bubbleBounds.left
        val anchorY = anchor?.y ?: state.bubbleBounds.top
        val touchInset = with(density) { 32.dp.toPx() }
        val preferredX = if (state.ownMessage) anchorX - contentWidthPx + touchInset else anchorX - touchInset
        val x = preferredX.coerceIn(xMarginPx, max(xMarginPx, viewWidthPx - contentWidthPx - xMarginPx))
        val spaceAbove = anchorY - topMarginPx - bubbleGapPx
        val spaceBelow = viewHeightPx - anchorY - bottomMarginPx - bubbleGapPx
        val below = if (spaceAbove >= totalHeightPx) false else
            spaceBelow >= totalHeightPx || spaceBelow > spaceAbove
        val preferredY = if (below) anchorY + bubbleGapPx else anchorY - totalHeightPx - bubbleGapPx
        val y = preferredY.coerceIn(topMarginPx, max(topMarginPx, viewHeightPx - bottomMarginPx - totalHeightPx))

        Column(
            modifier = Modifier
                .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                .width(menuWidth)
                .graphicsLayer {
                    this.alpha = alpha
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(.5f, if (below) 0f else 1f)
                },
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalAlignment = if (state.ownMessage) Alignment.End else Alignment.Start
        ) {
            if (below && !expanded) {
                MessageContextMenuActions(
                    state, palette, backdrop, strings, actionsWidth, actionsHeight, rowHeight,
                    onReply, onEdit, onPin, onCopyText, onForward, onSeenBy,
                    onAddAsSticker, onDownload, onDelete
                )
            }
            MenuGlassSurface(
                palette = palette,
                backdrop = backdrop,
                radius = if (expanded) 27.dp else 26.dp,
                modifier = Modifier.width(menuWidth).height(if (expanded) expandedHeight else reactionHeight)
            ) {
                if (expanded) {
                    ExpandedReactions(state.message.id, strings, palette, onExpandedChange, onReaction)
                } else {
                    CompactReactions(palette, onExpandedChange, onReaction)
                }
            }
            if (!below && !expanded) {
                MessageContextMenuActions(
                    state, palette, backdrop, strings, actionsWidth, actionsHeight, rowHeight,
                    onReply, onEdit, onPin, onCopyText, onForward, onSeenBy,
                    onAddAsSticker, onDownload, onDelete
                )
            }
        }
    }
}

@Composable
private fun MessageContextMenuActions(
    state: MessageContextMenuState,
    palette: MenuGlassPalette,
    backdrop: LiquidGlassBackdrop?,
    strings: NoveoStrings,
    width: Dp,
    height: Dp,
    rowHeight: Dp,
    onReply: () -> Unit,
    onEdit: () -> Unit,
    onPin: () -> Unit,
    onCopyText: () -> Unit,
    onForward: () -> Unit,
    onSeenBy: () -> Unit,
    onAddAsSticker: () -> Unit,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    MenuGlassSurface(palette, Modifier.width(width).height(height), radius = 23.dp, backdrop = backdrop) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 4.dp)) {
            if (state.message.chatType != "channel") {
                ContextMenuActionItem(strings.reply, palette, rowHeight, onReply) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            if (state.ownMessage && state.message.content.text != null) {
                ContextMenuActionItem(strings.edit, palette, rowHeight, onEdit) {
                    Icon(Icons.Outlined.Edit, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            ContextMenuActionItem(if (state.message.isPinned) strings.unpin else strings.pin, palette, rowHeight, onPin) {
                Icon(Icons.Outlined.Bookmark, null, tint = palette.accent, modifier = Modifier.size(19.dp))
            }
            if (state.message.content.text != null) {
                ContextMenuActionItem(strings.copyText, palette, rowHeight, onCopyText) {
                    Icon(Icons.Outlined.Description, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            ContextMenuActionItem(strings.forward, palette, rowHeight, onForward) {
                Icon(Icons.Outlined.ArrowForward, null, tint = palette.accent, modifier = Modifier.size(19.dp))
            }
            if (state.message.seenBy.isNotEmpty() && state.ownMessage && state.message.chatType != "channel") {
                ContextMenuActionItem(strings.seenBy, palette, rowHeight, onSeenBy) {
                    Icon(Icons.Outlined.Check, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            val file = state.message.content.file
            if (file != null && (file.isImage() || file.isTgsSticker())) {
                ContextMenuActionItem(strings.addAsSticker, palette, rowHeight, onAddAsSticker) {
                    Icon(Icons.Outlined.Star, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            if (file != null) {
                ContextMenuActionItem(strings.download, palette, rowHeight, onDownload) {
                    Icon(Icons.Outlined.KeyboardArrowDown, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            if (state.message.chatType != "channel") {
                ContextMenuActionItem(strings.delete, palette, rowHeight, onDelete, destructive = true) {
                    Icon(Icons.Outlined.Delete, null, tint = palette.delete, modifier = Modifier.size(19.dp))
                }
            }
        }
    }
}

@Composable
private fun ExpandedReactions(
    messageId: String,
    strings: NoveoStrings,
    palette: MenuGlassPalette,
    onExpandedChange: (Boolean) -> Unit,
    onReaction: (String) -> Unit
) {
    val gridState = androidx.compose.runtime.key(messageId) { rememberLazyGridState() }
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().height(47.dp)
                .padding(start = 14.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.reactions,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = palette.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape)
                    .background(palette.pressed)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onExpandedChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.KeyboardArrowDown, "Collapse reactions", tint = palette.muted,
                    modifier = Modifier.size(20.dp))
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            state = gridState,
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(start = 6.dp, end = 6.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Index keys are intentional: the existing emoji list contains duplicates.
            items(CONTEXT_MENU_REACTIONS.size, key = { it }) { index ->
                ReactionButton(CONTEXT_MENU_REACTIONS[index], palette, true) {
                    onReaction(CONTEXT_MENU_REACTIONS[index])
                }
            }
        }
    }
}

@Composable
private fun CompactReactions(
    palette: MenuGlassPalette,
    onExpandedChange: (Boolean) -> Unit,
    onReaction: (String) -> Unit
) {
    Row(
        Modifier.fillMaxSize().padding(start = 8.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Only the emoji strip scrolls on narrow phones; the expand button stays visible.
        Row(Modifier.weight(1f).horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically) {
            QUICK_REACTIONS.forEach { emoji ->
                ReactionButton(emoji, palette, false) { onReaction(emoji) }
            }
        }
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(palette.pressed)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    onExpandedChange(true)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.KeyboardArrowDown, "More reactions", tint = palette.muted,
                modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun ReactionButton(
    emoji: String,
    palette: MenuGlassPalette,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val density = LocalDensity.current
    Box(
        modifier = Modifier.size(36.dp).clip(CircleShape)
            .background(if (expanded) palette.pressed.copy(alpha = palette.pressed.alpha * .55f) else Color.Transparent)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            emoji,
            style = TextStyle(
                fontFamily = FontFamily.Default, // Use Android's colour emoji fallback, not app's text font.
                fontSize = (22f / density.fontScale.coerceAtLeast(1f)).sp,
                lineHeight = (27f / density.fontScale.coerceAtLeast(1f)).sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun ContextMenuActionItem(
    label: String,
    palette: MenuGlassPalette,
    rowHeight: Dp,
    onClick: () -> Unit,
    destructive: Boolean = false,
    icon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(rowHeight)
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(13.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(13.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium, // Respects active app font and font scale.
            fontWeight = FontWeight.Medium,
            color = if (destructive) palette.delete else palette.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun ir.hienob.noveo.data.MessageFileAttachment.isImage(): Boolean {
    val nameValue = name.lowercase(Locale.ROOT)
    return nameValue.endsWith(".png") || nameValue.endsWith(".jpg") ||
        nameValue.endsWith(".jpeg") || nameValue.endsWith(".webp") || nameValue.endsWith(".gif")
}
