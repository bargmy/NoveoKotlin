package ir.hienob.noveo.ui

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.hienob.noveo.data.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * A snapshot of the chat *before* the context menu is shown. The bitmap is already blurred,
 * saturated and contrast-adjusted. [windowOrigin] is the capture view's top-left IN WINDOW;
 * therefore the same image can be sampled by separate, moving glass surfaces without
 * applying blur to the reaction emoji or the action labels.
 *
 * In the chat's long-press handler, call captureLiquidGlassBackdrop(chatView) BEFORE setting
 * the state that makes MessageContextMenuOverlay visible; keep the result in remember state,
 * then pass it to MessageContextMenuOverlay(backdrop = snapshot).
 *
 * The captured view must contain the chat wallpaper/messages but no visible menu. Do not
 * capture sensitive chat contents into files, logs, or persistent caches.
 */
internal data class LiquidGlassBackdrop(
    val image: ImageBitmap,
    val viewportSize: IntSize,
    val windowOrigin: IntOffset
)

/** Call on the main thread from the existing long-press handler, before showing the overlay. */
internal fun captureLiquidGlassBackdrop(
    chatView: View,
    maxImageDimensionPx: Int = 780
): LiquidGlassBackdrop? {
    if (chatView.width <= 0 || chatView.height <= 0) return null
    val w = chatView.width
    val h = chatView.height
    val scale = min(1f, maxImageDimensionPx.coerceAtLeast(64).toFloat() / max(w, h))
    val imageW = max(1, (w * scale).roundToInt())
    val imageH = max(1, (h * scale).roundToInt())
    val source = Bitmap.createBitmap(imageW, imageH, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(source)
    canvas.scale(imageW.toFloat() / w, imageH.toFloat() / h)
    chatView.draw(canvas)

    // Blur the sampled backdrop ONCE, not on every pointer movement or Compose frame.
    // Three separable box passes approximate a broad Gaussian blur without RenderScript,
    // additional libraries, or API-31-only drawing behavior.
    val blurRadius = (30f * chatView.resources.displayMetrics.density * scale / 1.8f)
        .roundToInt().coerceIn(2, 22)
    val pixels = IntArray(imageW * imageH)
    source.getPixels(pixels, 0, imageW, 0, 0, imageW, imageH)
    source.recycle()
    val scratch = IntArray(pixels.size)
    repeat(3) {
        glassBoxBlurHorizontal(pixels, scratch, imageW, imageH, blurRadius)
        glassBoxBlurVertical(scratch, pixels, imageW, imageH, blurRadius)
    }
    val blurred = Bitmap.createBitmap(imageW, imageH, Bitmap.Config.ARGB_8888)
    blurred.setPixels(pixels, 0, imageW, 0, 0, imageW, imageH)

    // CSS reference: saturate(190%) contrast(1.08). The same operation is baked into
    // the one-time capture rather than repeatedly filtering the sharp foreground text.
    val saturation = ColorMatrix().apply { setSaturation(1.90f) }
    val shift = (1f - 1.08f) * 127.5f
    val contrast = ColorMatrix(floatArrayOf(
        1.08f, 0f, 0f, 0f, shift,
        0f, 1.08f, 0f, 0f, shift,
        0f, 0f, 1.08f, 0f, shift,
        0f, 0f, 0f, 1f, 0f
    ))
    saturation.postConcat(contrast)
    val output = Bitmap.createBitmap(imageW, imageH, Bitmap.Config.ARGB_8888)
    AndroidCanvas(output).drawBitmap(
        blurred, 0f, 0f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(saturation)
            isFilterBitmap = true
        }
    )
    blurred.recycle()
    val location = IntArray(2)
    chatView.getLocationInWindow(location)
    return LiquidGlassBackdrop(
        image = output.asImageBitmap(),
        viewportSize = IntSize(w, h),
        windowOrigin = IntOffset(location[0], location[1])
    )
}

private fun glassBoxBlurHorizontal(src: IntArray, dst: IntArray, w: Int, h: Int, r: Int) {
    val divisor = 2 * r + 1
    for (y in 0 until h) {
        val base = y * w
        var a = 0; var red = 0; var green = 0; var blue = 0
        for (k in -r..r) {
            val p = src[base + k.coerceIn(0, w - 1)]
            a += p ushr 24; red += (p ushr 16) and 255
            green += (p ushr 8) and 255; blue += p and 255
        }
        for (x in 0 until w) {
            dst[base + x] = ((a / divisor) shl 24) or ((red / divisor) shl 16) or
                ((green / divisor) shl 8) or (blue / divisor)
            val old = src[base + (x - r).coerceIn(0, w - 1)]
            val fresh = src[base + (x + r + 1).coerceIn(0, w - 1)]
            a += (fresh ushr 24) - (old ushr 24)
            red += ((fresh ushr 16) and 255) - ((old ushr 16) and 255)
            green += ((fresh ushr 8) and 255) - ((old ushr 8) and 255)
            blue += (fresh and 255) - (old and 255)
        }
    }
}

private fun glassBoxBlurVertical(src: IntArray, dst: IntArray, w: Int, h: Int, r: Int) {
    val divisor = 2 * r + 1
    for (x in 0 until w) {
        var a = 0; var red = 0; var green = 0; var blue = 0
        for (k in -r..r) {
            val p = src[k.coerceIn(0, h - 1) * w + x]
            a += p ushr 24; red += (p ushr 16) and 255
            green += (p ushr 8) and 255; blue += p and 255
        }
        for (y in 0 until h) {
            dst[y * w + x] = ((a / divisor) shl 24) or ((red / divisor) shl 16) or
                ((green / divisor) shl 8) or (blue / divisor)
            val old = src[(y - r).coerceIn(0, h - 1) * w + x]
            val fresh = src[(y + r + 1).coerceIn(0, h - 1) * w + x]
            a += (fresh ushr 24) - (old ushr 24)
            red += ((fresh ushr 16) and 255) - ((old ushr 16) and 255)
            green += ((fresh ushr 8) and 255) - ((old ushr 8) and 255)
            blue += (fresh and 255) - (old and 255)
        }
    }
}

private class GlassPalette(isDark: Boolean) {
    val ink = Color(if (isDark) 0xFFF1F7FF else 0xFF17354A)
    val muted = Color(if (isDark) 0xFFB0C2D3 else 0xFF6E8292)
    val accent = Color(if (isDark) 0xFF62B2FF else 0xFF218CFA)
    val tint = if (isDark) Color(0xFF243952).copy(alpha = .55f)
        else Color(0xFFEAF7FF).copy(alpha = .37f)
    val edge = if (isDark) Color(0xFFDEF0FF).copy(alpha = .23f)
        else Color.White.copy(alpha = .71f)
    val shine = Color.White.copy(alpha = if (isDark) .26f else .80f)
    val shadow = if (isDark) Color.Black.copy(alpha = .53f)
        else Color(0xFF193B5F).copy(alpha = .24f)
    val closeShadow = if (isDark) Color.Black.copy(alpha = .28f)
        else Color(0xFF173553).copy(alpha = .12f)
    val rowPress = Color.White.copy(alpha = if (isDark) .14f else .34f)
    val reactionBackground = Color.White.copy(alpha = if (isDark) .095f else .27f)
    // Only shown when no caller-provided snapshot is available; this is NOT real blur.
    val noBackdrop = if (isDark) Color(0xFF1E3149).copy(alpha = .94f)
        else Color(0xFFDFEFFB).copy(alpha = .94f)
}

/** Both the reaction pill and the action sheet use this exact same material. */
@Composable
private fun LiquidGlassSurface(
    palette: GlassPalette,
    backdrop: LiquidGlassBackdrop?,
    radius: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val radiusPx = with(density) { radius.toPx() }
    val edgePx = with(density) { 1.dp.toPx() }
    val insetPx = with(density) { 1.5.dp.toPx() }
    val shape = RoundedCornerShape(radius)
    var positionInWindow by remember { mutableStateOf(Offset.Zero) }
    var touch by remember { mutableStateOf<Offset?>(null) }
    val highlightX by animateFloatAsState(
        targetValue = touch?.x ?: .25f,
        animationSpec = tween(230), label = "glassHighlightX"
    )
    val highlightY by animateFloatAsState(
        targetValue = touch?.y ?: 0f,
        animationSpec = tween(230), label = "glassHighlightY"
    )
    Box(
        modifier = modifier
            .shadow(24.dp, shape, clip = false, ambientColor = palette.shadow, spotColor = palette.shadow)
            .shadow(5.dp, shape, clip = false,
                ambientColor = palette.closeShadow, spotColor = palette.closeShadow)
            .onGloballyPositioned { positionInWindow = it.positionInWindow() }
            .clip(shape)
            // Non-consuming observation: reaction/grid scrolling and clicks still receive input.
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val first = event.changes.firstOrNull { it.pressed }
                        touch = if (first != null && size.width > 0 && size.height > 0) {
                            Offset(
                                (first.position.x / size.width).coerceIn(0f, 1f),
                                (first.position.y / size.height).coerceIn(0f, 1f)
                            )
                        } else null
                    }
                }
            }
            .drawWithContent {
                val w = size.width
                val h = size.height
                // Sample the blurred chat screenshot using WINDOW coordinates. Consequently a
                // menu that moves above/below its message shows the backdrop at its real location.
                if (backdrop != null && backdrop.viewportSize.width > 0 && backdrop.viewportSize.height > 0) {
                    val image = backdrop.image
                    val sx = image.width.toFloat() / backdrop.viewportSize.width
                    val sy = image.height.toFloat() / backdrop.viewportSize.height
                    val requestedLeft = (positionInWindow.x - backdrop.windowOrigin.x) * sx
                    val requestedTop = (positionInWindow.y - backdrop.windowOrigin.y) * sy
                    val requestedW = w * sx
                    val requestedH = h * sy
                    val srcL = floor(requestedLeft).toInt().coerceIn(0, image.width)
                    val srcT = floor(requestedTop).toInt().coerceIn(0, image.height)
                    val srcR = ceil(requestedLeft + requestedW).toInt().coerceIn(0, image.width)
                    val srcB = ceil(requestedTop + requestedH).toInt().coerceIn(0, image.height)
                    if (srcR > srcL && srcB > srcT && requestedW > 0f && requestedH > 0f) {
                        val dstL = ((srcL - requestedLeft) / sx).roundToInt()
                        val dstT = ((srcT - requestedTop) / sy).roundToInt()
                        val dstW = ((srcR - srcL) / sx).roundToInt().coerceAtLeast(1)
                        val dstH = ((srcB - srcT) / sy).roundToInt().coerceAtLeast(1)
                        drawImage(
                            image = image,
                            srcOffset = IntOffset(srcL, srcT),
                            srcSize = IntSize(srcR - srcL, srcB - srcT),
                            dstOffset = IntOffset(dstL, dstT),
                            dstSize = IntSize(dstW, dstH),
                            filterQuality = FilterQuality.Medium
                        )
                    } else {
                        drawRect(palette.noBackdrop)
                    }
                } else {
                    drawRect(palette.noBackdrop)
                }
                // CSS .menu::before: translucent tint above blurred saturated content.
                drawRect(palette.tint)
                // CSS .menu-refract: directional blue/white haze, without fake opaque borders.
                drawRect(brush = Brush.linearGradient(
                    0f to Color.White.copy(alpha = .17f),
                    .18f to Color.Transparent,
                    .47f to Color(0xFF55B5FF).copy(alpha = .045f),
                    .75f to Color.White.copy(alpha = .10f),
                    1f to Color.Transparent,
                    start = Offset(w * .10f, 0f), end = Offset(w * .84f, h)
                ))
                // CSS .menu::after: the broad diagonal and touch-positioned glossy highlight.
                drawRect(brush = Brush.linearGradient(
                    0f to Color.White.copy(alpha = .43f),
                    .32f to Color.White.copy(alpha = .065f),
                    .55f to Color.Transparent,
                    1f to Color.White.copy(alpha = .17f),
                    start = Offset.Zero, end = Offset(w, h)
                ))
                drawRect(brush = Brush.radialGradient(
                    0f to palette.shine,
                    .42f to palette.shine.copy(alpha = palette.shine.alpha * .24f),
                    .75f to Color.Transparent,
                    1f to Color.Transparent,
                    center = Offset(w * highlightX, h * highlightY),
                    radius = max(w, h) * .82f
                ))
                // Foreground content is ALWAYS drawn after the backdrop, and is never blurred.
                drawContent()
                // Outer glass rim + top/left inner highlights + cool bottom depth line.
                val corner = CornerRadius(radiusPx, radiusPx)
                drawRoundRect(
                    color = palette.edge, topLeft = Offset(edgePx / 2, edgePx / 2),
                    size = Size(w - edgePx, h - edgePx),
                    cornerRadius = corner, style = Stroke(width = edgePx)
                )
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        0f to Color.White.copy(alpha = if (palette.ink.red > .5f) .27f else .68f),
                        .4f to Color.White.copy(alpha = .08f),
                        1f to Color(0xFF265681).copy(alpha = .08f)
                    ),
                    topLeft = Offset(insetPx, insetPx),
                    size = Size((w - 2 * insetPx).coerceAtLeast(0f), (h - 2 * insetPx).coerceAtLeast(0f)),
                    cornerRadius = CornerRadius((radiusPx - insetPx).coerceAtLeast(0f)),
                    style = Stroke(width = insetPx)
                )
            },
        content = { content() }
    )
}

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
    val palette = remember(tgColors.isDark) { GlassPalette(tgColors.isDark) }
    var open by remember(state.message) { mutableStateOf(false) }
    var dismissing by remember(state.message) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(state.message) { open = true }
    val scrimAlpha by animateFloatAsState(
        targetValue = if (open) (if (tgColors.isDark) .25f else .15f) else 0f,
        animationSpec = tween(190), label = "glassScrim"
    )
    fun closeWithAnimation() {
        if (dismissing) return
        dismissing = true
        open = false
        scope.launch {
            delay(170)
            onDismiss()
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = scrimAlpha))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { closeWithAnimation() }
                )
        )
        MessageContextMenu(
            state = state,
            expanded = expanded,
            palette = palette,
            backdrop = backdrop,
            open = open,
            onExpandedChange = onExpandedChange,
            onReply = onReply,
            onCopyText = onCopyText,
            onReaction = onReaction,
            onEdit = onEdit,
            onDelete = onDelete,
            onPin = onPin,
            onForward = onForward,
            onDownload = onDownload,
            onSeenBy = onSeenBy,
            onAddAsSticker = onAddAsSticker,
            strings = strings
        )
    }
}

@Composable
private fun MessageContextMenu(
    state: MessageContextMenuState,
    expanded: Boolean,
    palette: GlassPalette,
    backdrop: LiquidGlassBackdrop?,
    open: Boolean,
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
    strings: NoveoStrings
) {
    val wrapperScale by animateFloatAsState(
        targetValue = if (open) 1f else .92f,
        animationSpec = spring(dampingRatio = .75f, stiffness = 350f),
        label = "glassMenuScale"
    )
    val wrapperAlpha by animateFloatAsState(
        targetValue = if (open) 1f else 0f,
        animationSpec = tween(210), label = "glassMenuAlpha"
    )
    val wrapperRise by animateFloatAsState(
        targetValue = if (open) 0f else -10f,
        animationSpec = tween(270, easing = FastOutSlowInEasing),
        label = "glassMenuRise"
    )
    val reactionsHeight by animateDpAsState(
        targetValue = if (expanded) 320.dp else 52.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "glassReactionsHeight"
    )
    val reactionsRadius by animateDpAsState(
        targetValue = if (expanded) 28.dp else 26.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "glassReactionsRadius"
    )
    val density = LocalDensity.current
    var measuredHeightPx by remember(state.message, expanded) { mutableIntStateOf(0) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val safeH = 8.dp
        val safeTop = 8.dp
        val safeBottom = 16.dp
        val gap = 6.dp
        val menuWidth = minOf(320.dp, (maxWidth - safeH * 2).coerceAtLeast(1.dp))
        val actionMaxHeight = (maxHeight - 52.dp - 32.dp).coerceAtLeast(80.dp)
        val actionCount = (if (state.message.chatType != "channel") 1 else 0) +
            (if (state.ownMessage && state.message.content.text != null) 1 else 0) +
            1 + (if (state.message.content.text != null) 1 else 0) + 1 +
            (if (state.message.seenBy.isNotEmpty() && state.ownMessage && state.message.chatType != "channel") 1 else 0) +
            (if (state.message.content.file?.let { it.isImage() || it.isTgsSticker() } == true) 1 else 0) +
            (if (state.message.content.file != null) 1 else 0) +
            (if (state.message.chatType != "channel") 1 else 0)
        val estimatedActions = minOf(actionMaxHeight, (actionCount * 48 + 8).dp)
        val estimatedHeight = if (expanded) 320.dp else 52.dp + gap + estimatedActions
        val menuHeightPx = if (measuredHeightPx > 0) measuredHeightPx.toFloat()
            else with(density) { estimatedHeight.toPx() }
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val targetWidthPx = with(density) { menuWidth.toPx() }
        val safeHPx = with(density) { safeH.toPx() }
        val safeTopPx = with(density) { safeTop.toPx() }
        val safeBottomPx = with(density) { safeBottom.toPx() }
        val bubbleGapPx = with(density) { 10.dp.toPx() }
        val tap = state.tapPosition.takeUnless { it == Offset.Unspecified }
        val anchorX = tap?.x ?: if (state.ownMessage) state.bubbleBounds.right else state.bubbleBounds.left
        val anchorY = tap?.y ?: state.bubbleBounds.top
        val preferredLeft = if (state.ownMessage) anchorX - targetWidthPx + with(density) { 32.dp.toPx() }
            else anchorX - with(density) { 32.dp.toPx() }
        val maxLeft = (screenWidthPx - targetWidthPx - safeHPx).coerceAtLeast(safeHPx)
        val left = preferredLeft.coerceIn(safeHPx, maxLeft)
        val spaceAbove = anchorY - safeTopPx - bubbleGapPx
        val spaceBelow = screenHeightPx - safeBottomPx - anchorY - bubbleGapPx
        val below = spaceAbove < menuHeightPx && spaceBelow > with(density) { 64.dp.toPx() }
        val preferredTop = if (below) anchorY + bubbleGapPx
            else anchorY - menuHeightPx - bubbleGapPx
        val maxTop = (screenHeightPx - menuHeightPx - safeBottomPx).coerceAtLeast(safeTopPx)
        val top = preferredTop.coerceIn(safeTopPx, maxTop)

        Column(
            modifier = Modifier
                .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                .width(menuWidth)
                .onSizeChanged { if (measuredHeightPx != it.height) measuredHeightPx = it.height }
                .graphicsLayer {
                    alpha = wrapperAlpha
                    scaleX = wrapperScale
                    scaleY = wrapperScale
                    translationY = with(density) { wrapperRise.dp.toPx() }
                    transformOrigin = TransformOrigin(.5f, if (below) 0f else 1f)
                },
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalAlignment = if (state.ownMessage) Alignment.End else Alignment.Start
        ) {
            if (below && !expanded) {
                MessageContextMenuActions(
                    state, palette, backdrop, strings, actionMaxHeight,
                    onReply, onEdit, onPin, onCopyText, onForward, onSeenBy,
                    onAddAsSticker, onDownload, onDelete
                )
            }
            LiquidGlassSurface(
                palette = palette,
                backdrop = backdrop,
                radius = reactionsRadius,
                modifier = Modifier.width(menuWidth).height(reactionsHeight)
                    .animateContentSize(animationSpec = tween(220))
            ) {
                if (expanded) {
                    ExpandedReactions(
                        strings = strings, palette = palette,
                        onExpandedChange = onExpandedChange, onReaction = onReaction
                    )
                } else {
                    CompactReactions(
                        palette = palette,
                        onExpandedChange = onExpandedChange, onReaction = onReaction
                    )
                }
            }
            if (!below && !expanded) {
                MessageContextMenuActions(
                    state, palette, backdrop, strings, actionMaxHeight,
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
    palette: GlassPalette,
    backdrop: LiquidGlassBackdrop?,
    strings: NoveoStrings,
    maxHeight: androidx.compose.ui.unit.Dp,
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
    LiquidGlassSurface(
        palette = palette,
        backdrop = backdrop,
        radius = 28.dp,
        modifier = Modifier.width(240.dp).heightIn(max = maxHeight)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(vertical = 4.dp)
        ) {
            if (state.message.chatType != "channel") {
                ContextMenuActionItem(strings.reply, palette, onReply) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            if (state.ownMessage && state.message.content.text != null) {
                ContextMenuActionItem(strings.edit, palette, onEdit) {
                    Icon(Icons.Outlined.Edit, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            ContextMenuActionItem(if (state.message.isPinned) strings.unpin else strings.pin, palette, onPin) {
                Icon(Icons.Outlined.Bookmark, null, tint = palette.accent, modifier = Modifier.size(19.dp))
            }
            if (state.message.content.text != null) {
                ContextMenuActionItem(strings.copyText, palette, onCopyText) {
                    Icon(Icons.Outlined.Description, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            ContextMenuActionItem(strings.forward, palette, onForward) {
                Icon(Icons.Outlined.ArrowForward, null, tint = palette.accent, modifier = Modifier.size(19.dp))
            }
            if (state.message.seenBy.isNotEmpty() && state.ownMessage && state.message.chatType != "channel") {
                ContextMenuActionItem(strings.seenBy, palette, onSeenBy) {
                    Icon(Icons.Outlined.Check, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            val file = state.message.content.file
            if (file != null && (file.isImage() || file.isTgsSticker())) {
                ContextMenuActionItem(strings.addAsSticker, palette, onAddAsSticker) {
                    Icon(Icons.Outlined.Star, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            if (file != null) {
                ContextMenuActionItem(strings.download, palette, onDownload) {
                    Icon(Icons.Outlined.KeyboardArrowDown, null, tint = palette.accent, modifier = Modifier.size(19.dp))
                }
            }
            if (state.message.chatType != "channel") {
                ContextMenuActionItem(strings.delete, palette, onDelete, destructive = true) {
                    Icon(Icons.Outlined.Delete, null, tint = Color(0xFFE53935), modifier = Modifier.size(19.dp))
                }
            }
        }
    }
}

@Composable
private fun ExpandedReactions(
    strings: NoveoStrings,
    palette: GlassPalette,
    onExpandedChange: (Boolean) -> Unit,
    onReaction: (String) -> Unit
) {
    val gridState = rememberLazyGridState()
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 14.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.reactions,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp, color = palette.muted, fontWeight = FontWeight.Medium
                ),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(palette.reactionBackground)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onExpandedChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.KeyboardArrowDown, "Collapse reactions",
                    tint = palette.muted, modifier = Modifier.size(19.dp))
            }
        }
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(CONTEXT_MENU_REACTIONS) { emoji ->
                ReactionButton(emoji, true, palette) { onReaction(emoji) }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun CompactReactions(
    palette: GlassPalette,
    onExpandedChange: (Boolean) -> Unit,
    onReaction: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CONTEXT_MENU_QUICK_REACTIONS.forEach { emoji ->
            ReactionButton(emoji, false, palette) { onReaction(emoji) }
        }
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape)
                .background(palette.reactionBackground)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onExpandedChange(true) },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.KeyboardArrowDown, "More reactions",
                tint = palette.muted, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun ReactionButton(
    emoji: String,
    expanded: Boolean,
    palette: GlassPalette,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(
        targetValue = if (pressed) .38f else if (expanded) palette.reactionBackground.alpha else 0f,
        animationSpec = tween(140), label = "reactionPress"
    )
    Box(
        modifier = Modifier.size(36.dp).clip(CircleShape)
            .background(Color.White.copy(alpha = pressAlpha))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Never force a custom Latin font on emoji: Android's system colour-emoji fallback
        // handles variation selectors, skin tones and ZWJ sequences such as 👨‍💻.
        Text(
            text = emoji,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 20.sp,
                lineHeight = 26.sp,
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
    palette: GlassPalette,
    onClick: () -> Unit,
    destructive: Boolean = false,
    icon: @Composable () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(
        targetValue = if (pressed) palette.rowPress.alpha else 0f,
        animationSpec = tween(140), label = "actionPress"
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = pressAlpha))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(13.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = if (destructive) Color(0xFFE53935) else palette.ink
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun ir.hienob.noveo.data.MessageFileAttachment.isImage(): Boolean {
    val nameValue = name.lowercase(Locale.ROOT)
    return nameValue.endsWith(".png") || nameValue.endsWith(".jpg") ||
        nameValue.endsWith(".jpeg") || nameValue.endsWith(".webp") || nameValue.endsWith(".gif")
}

private fun ir.hienob.noveo.data.MessageFileAttachment.isVideo(): Boolean {
    val nameValue = name.lowercase(Locale.ROOT)
    return nameValue.endsWith(".mp4") || nameValue.endsWith(".mov") || nameValue.endsWith(".webm")
}
