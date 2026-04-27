package ir.hienob.noveo.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import ir.hienob.noveo.R
import ir.hienob.noveo.data.ChatMessage
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.slideIn
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun ChatInput(
    draft: String,
    onDraftChange: (String) -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    sendScale: Float = 1f,
    replyingTo: ChatMessage? = null,
    onCancelReply: () -> Unit = {},
    placeholder: String = "Message",
    onAttachClick: () -> Unit = {},
    onLongAttachClick: () -> Unit = {},
    onPasteUri: (android.net.Uri) -> Unit = {},
    hasAttachment: Boolean = false,
    tgColors: TelegramThemeColors = telegramColors()
) {
    val buttonInteraction = remember { MutableInteractionSource() }
    val inputFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = androidx.compose.ui.platform.LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    var isRecording by remember { androidx.compose.runtime.mutableStateOf(false) }
    var recordingLocked by remember { androidx.compose.runtime.mutableStateOf(false) }
    var recordTimeMillis by remember { androidx.compose.runtime.mutableStateOf(0L) }
    var dragOffset by remember { androidx.compose.runtime.mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            audioRecorder.start()
            isRecording = true
            recordTimeMillis = 0L
        }
    }

    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                kotlinx.coroutines.delay(100)
                recordTimeMillis += 100
            }
        }
    }

    LaunchedEffect(replyingTo?.id) {
        if (replyingTo != null && !isRecording) {
            inputFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    fun finishRecording(send: Boolean) {
        if (!isRecording) return
        isRecording = false
        recordingLocked = false
        dragOffset = androidx.compose.ui.geometry.Offset.Zero
        if (send) {
            audioRecorder.stop()
            audioRecorder.outputFile?.let { file ->
                val uri = android.net.Uri.fromFile(file)
                onPasteUri(uri)
            }
        } else {
            audioRecorder.cancel()
        }
    }

    val recordingAlpha by animateFloatAsState(
        targetValue = if (isRecording) 1f else 0f,
        animationSpec = tween(150),
        label = "recordingAlpha"
    )

    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 6.dp).padding(bottom = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            // Main Input Bubble
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = tgColors.composerField,
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    // Regular Input UI
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { alpha = 1f - recordingAlpha }
                    ) {
                        // Reply Preview
                        AnimatedVisibility(
                            visible = replyingTo != null && !isRecording,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ) {
                            replyingTo?.let { reply ->
                                Row(modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.width(2.dp).height(30.dp).background(tgColors.composerBlue, RoundedCornerShape(1.dp)))
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(reply.senderName, style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp), color = tgColors.composerBlue, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text(reply.content.previewText(), style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp), color = tgColors.composerHint, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    IconButton(onClick = onCancelReply, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(17.dp), tint = tgColors.composerHint)
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassIconButton(
                                resId = R.drawable.tg_input_smile,
                                contentDescription = "Emoji",
                                tint = tgColors.composerIcon,
                                selectorTint = Color.Transparent,
                                onClick = {},
                                modifier = Modifier.padding(start = 4.dp)
                            )

                            Box(
                                modifier = Modifier.weight(1f).padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (draft.isBlank()) {
                                    Text(placeholder, color = tgColors.composerHint, fontSize = 17.sp)
                                }
                                BasicTextField(
                                    value = draft,
                                    onValueChange = onDraftChange,
                                    modifier = Modifier.fillMaxWidth().focusRequester(inputFocusRequester),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp, color = tgColors.composerText),
                                    cursorBrush = SolidColor(tgColors.composerCursor),
                                    minLines = 1,
                                    maxLines = 6
                                )
                                
                                // Content Receiver for pasting files
                                androidx.compose.ui.viewinterop.AndroidView(
                                    factory = { context ->
                                        val view = android.view.View(context)
                                        androidx.core.view.ViewCompat.setOnReceiveContentListener(
                                            view,
                                            arrayOf("image/*", "video/*", "application/*", "text/*"),
                                            object : androidx.core.view.OnReceiveContentListener {
                                                override fun onReceiveContent(view: android.view.View, payload: androidx.core.view.ContentInfoCompat): androidx.core.view.ContentInfoCompat? {
                                                    val split = payload.partition { it.uri != null }
                                                    val uriPart = split.first
                                                    val remaining = split.second
                                                    
                                                    if (uriPart != null) {
                                                        val clip = uriPart.clip
                                                        for (i in 0 until clip.itemCount) {
                                                            clip.getItemAt(i).uri?.let { uri -> onPasteUri(uri) }
                                                        }
                                                    }
                                                    return remaining
                                                }
                                            }
                                        )
                                        view
                                    },
                                    modifier = Modifier.matchParentSize()
                                )
                            }

                            GlassIconButton(
                                resId = R.drawable.tg_msg_input_attach2,
                                contentDescription = "Attach",
                                tint = tgColors.composerIcon,
                                selectorTint = Color.Transparent,
                                onClick = onAttachClick,
                                onLongClick = onLongAttachClick,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                    // Recording UI
                    if (recordingAlpha > 0f) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .padding(horizontal = 16.dp)
                                .graphicsLayer { alpha = recordingAlpha },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (recordingLocked) {
                                IconButton(onClick = { finishRecording(send = false) }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error)
                                }
                            } else {
                                // Blinking red dot
                                val dotAlpha by androidx.compose.animation.core.rememberInfiniteTransition(label = "dot").animateFloat(
                                    initialValue = 1f,
                                    targetValue = 0f,
                                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                        animation = tween(500),
                                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                                    ),
                                    label = "dotAlpha"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red.copy(alpha = dotAlpha))
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            
                            val seconds = (recordTimeMillis / 1000) % 60
                            val minutes = (recordTimeMillis / 1000) / 60
                            Text(
                                String.format("%02d:%02d", minutes, seconds),
                                fontSize = 17.sp,
                                color = tgColors.composerText
                            )

                            Spacer(Modifier.weight(1f))

                            if (!recordingLocked) {
                                // Slide to cancel text + chevron
                                val slideOffsetAbs = Math.abs(dragOffset.x)
                                val slideAlpha = (1f - (slideOffsetAbs / 300f)).coerceIn(0f, 1f)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.graphicsLayer { alpha = slideAlpha }
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Outlined.KeyboardArrowLeft,
                                        contentDescription = null,
                                        tint = tgColors.composerHint,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "Slide to cancel",
                                        fontSize = 15.sp,
                                        color = tgColors.composerHint
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // Lock Icon when recording
            Box(
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .width(48.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = isRecording && !recordingLocked,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Outlined.Lock,
                                contentDescription = "Lock",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Separate Circular Send/Mic Button
            val isBlank = draft.isBlank() && !hasAttachment
            val showSendButton = !isBlank || recordingLocked
            val buttonColor = if (!showSendButton) tgColors.composerField else tgColors.composerBlue
            val iconColor = if (!showSendButton) tgColors.composerIcon else Color.White
            
            val micScale by animateFloatAsState(
                targetValue = if (isRecording && !recordingLocked) 1.5f else sendScale,
                animationSpec = tween(150),
                label = "micScale"
            )

            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .scale(micScale)
                    .offset(x = dragOffset.x.dp / 2f, y = dragOffset.y.dp / 2f),
                shape = CircleShape,
                color = buttonColor,
                shadowElevation = if (isRecording && !recordingLocked) 4.dp else 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .then(
                            if (!showSendButton) {
                                Modifier.pointerInput(Unit) {
                                    androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                                audioRecorder.start()
                                                isRecording = true
                                                recordingLocked = false
                                                recordTimeMillis = 0L
                                            } else {
                                                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                            }
                                        },
                                        onDragEnd = {
                                            if (isRecording) {
                                                finishRecording(send = !recordingLocked)
                                            }
                                        },
                                        onDragCancel = {
                                            if (isRecording) finishRecording(send = false)
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            if (isRecording && !recordingLocked) {
                                                dragOffset += dragAmount
                                                if (dragOffset.x < -200f) {
                                                    // Cancel recording
                                                    finishRecording(send = false)
                                                } else if (dragOffset.y < -200f) {
                                                    // Lock recording
                                                    recordingLocked = true
                                                    dragOffset = androidx.compose.ui.geometry.Offset.Zero
                                                }
                                            }
                                        }
                                    )
                                }.clickable(onClick = { /* Mic tap: do nothing or show toast */ })
                            } else {
                                Modifier.clickable(
                                    interactionSource = buttonInteraction, 
                                    indication = null, 
                                    onClick = {
                                        if (recordingLocked) {
                                            finishRecording(send = true)
                                        } else {
                                            onActionClick()
                                        }
                                    }
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val density = androidx.compose.ui.platform.LocalDensity.current
                    AnimatedContent(
                        targetState = !showSendButton,
                        transitionSpec = { 
                            if (!targetState) { // Mic -> Send
                                (fadeIn(tween(200)) + 
                                 slideIn(tween(250, easing = LinearOutSlowInEasing)) { 
                                     IntOffset(with(density) { -50.dp.roundToPx() }, with(density) { 50.dp.roundToPx() }) 
                                 }
                                ).togetherWith(fadeOut(tween(150)))
                            } else { // Send -> Mic
                                fadeIn(tween(150)).togetherWith(fadeOut(tween(150)))
                            }
                        },
                        label = "send_icon_animation"
                    ) { targetIsMic ->
                        val animRotation by transition.animateFloat(
                            label = "rotation",
                            transitionSpec = { tween(250) }
                        ) { enterExitState ->
                            if (!targetIsMic && enterExitState == androidx.compose.animation.EnterExitState.PreEnter) -25f else 0f
                        }

                        Image(
                            painter = painterResource(if (targetIsMic) R.drawable.tg_input_mic else R.drawable.tg_send_plane_24),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp).graphicsLayer {
                                rotationZ = if (targetIsMic) 0f else animRotation
                            },
                            colorFilter = ColorFilter.tint(iconColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun AttachmentPreview(
    attachment: ir.hienob.noveo.app.PendingAttachment,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (attachment.mimeType.startsWith("image/") || attachment.mimeType.startsWith("video/")) {
                    SubcomposeAsyncImage(
                        model = attachment.uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        error = {
                            Icon(Icons.Outlined.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Close, // Using Close as a generic file icon placeholder
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                if (attachment.isUploading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        progress = { attachment.progress },
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                    )
                }
            }
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.fileName,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (attachment.isUploading) 
                        "Uploading ${ (attachment.progress * 100).toInt() }%" 
                        else "Ready to send",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            IconButton(onClick = onRemove, enabled = !attachment.isUploading) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GlassIconButton(
    resId: Int,
    contentDescription: String,
    tint: Color,
    selectorTint: Color,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(selectorTint, CircleShape)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(resId),
            contentDescription = contentDescription,
            modifier = Modifier.size(22.dp),
            colorFilter = ColorFilter.tint(tint)
        )
    }
}
