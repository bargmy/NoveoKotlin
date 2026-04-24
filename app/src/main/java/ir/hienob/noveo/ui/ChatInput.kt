package ir.hienob.noveo.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.hienob.noveo.R
import ir.hienob.noveo.data.ChatMessage

internal data class ChatInputColors(
    val fieldTop: Color,
    val fieldBottom: Color,
    val fieldBorder: Color,
    val iconTint: Color,
    val selectorTint: Color,
    val text: Color,
    val hint: Color,
    val cursor: Color,
    val actionTop: Color,
    val actionBottom: Color,
    val actionIcon: Color
)

@OptIn(ExperimentalAnimationApi::class)
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
    colors: ChatInputColors = ChatInputColors(
        fieldTop = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        fieldBottom = MaterialTheme.colorScheme.surfaceVariant,
        fieldBorder = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
        iconTint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        selectorTint = Color.Transparent,
        text = MaterialTheme.colorScheme.onSurfaceVariant,
        hint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        cursor = MaterialTheme.colorScheme.primary,
        actionTop = MaterialTheme.colorScheme.primary,
        actionBottom = MaterialTheme.colorScheme.primary,
        actionIcon = MaterialTheme.colorScheme.onPrimary
    )
) {
    val fieldShape = RoundedCornerShape(22.dp)
    val buttonInteraction = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 9.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(fieldShape)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(colors.fieldTop, colors.fieldBottom)
                        )
                    )
                    .border(1.dp, colors.fieldBorder, fieldShape)
            ) {
                // Reply Preview inside the input box
                AnimatedContent(
                    targetState = replyingTo,
                    label = "input_reply_preview"
                ) { reply ->
                    if (reply != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(28.dp)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp))
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reply.senderName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = reply.content.previewText(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.text.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = onCancelReply,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Cancel",
                                    modifier = Modifier.size(16.dp),
                                    tint = colors.iconTint
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp)
                ) {
                    GlassIconButton(
                        resId = R.drawable.tg_input_smile,
                        contentDescription = "Emoji",
                        tint = colors.iconTint,
                        selectorTint = colors.selectorTint,
                        onClick = {},
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 1.dp, bottom = 1.dp)
                    )

                    GlassIconButton(
                        resId = R.drawable.tg_msg_input_attach2,
                        contentDescription = "Attach",
                        tint = colors.iconTint,
                        selectorTint = colors.selectorTint,
                        onClick = {},
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 1.dp, bottom = 1.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 42.dp, end = 42.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = draft,
                            onValueChange = onDraftChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 9.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 17.sp,
                                lineHeight = 20.sp,
                                color = colors.text
                            ),
                            cursorBrush = SolidColor(colors.cursor),
                            minLines = 1,
                            maxLines = 6,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Default
                            ),
                            decorationBox = { innerTextField ->
                                if (draft.isBlank()) {
                                    Text(
                                        text = placeholder,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontSize = 17.sp,
                                            lineHeight = 20.sp
                                        ),
                                        color = colors.hint
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .scale(sendScale),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(colors.actionTop, colors.actionBottom)
                            )
                        )
                )

                AnimatedContent(
                    targetState = draft.isBlank(),
                    transitionSpec = {
                        (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                    },
                    label = "send_icon_animation"
                ) { isBlank ->
                    Image(
                        painter = painterResource(if (isBlank) R.drawable.tg_input_mic else R.drawable.tg_send_plane_24),
                        contentDescription = null,
                        modifier = Modifier.size(if (isBlank) 24.dp else 24.dp),
                        colorFilter = ColorFilter.tint(colors.actionIcon)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = buttonInteraction,
                            indication = null,
                            onClick = onActionClick
                        )
                )
            }
        }
    }
}

@Composable
private fun GlassIconButton(
    resId: Int,
    contentDescription: String,
    tint: Color,
    selectorTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(selectorTint, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(resId),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(tint)
        )
    }
}
