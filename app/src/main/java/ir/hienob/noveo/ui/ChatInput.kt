package ir.hienob.noveo.ui

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.hienob.noveo.R

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

@Composable
internal fun ChatInput(
    draft: String,
    onDraftChange: (String) -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    sendScale: Float = 1f,
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
    val fieldShape = RoundedCornerShape(26.dp)
    val buttonInteraction = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.tg_compose_panel_shadow),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(12.dp)
                .offset(y = (-2).dp),
            contentScale = ContentScale.FillBounds,
            alpha = 0.5f
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .clip(fieldShape)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(colors.fieldTop, colors.fieldBottom)
                        )
                    )
                    .border(1.dp, colors.fieldBorder, fieldShape)
            ) {
                GlassIconButton(
                    resId = R.drawable.tg_input_smile,
                    contentDescription = "Emoji",
                    tint = colors.iconTint,
                    selectorTint = colors.selectorTint,
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 2.dp)
                )

                GlassIconButton(
                    resId = R.drawable.tg_msg_input_attach2,
                    contentDescription = "Attach",
                    tint = colors.iconTint,
                    selectorTint = colors.selectorTint,
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 2.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp, end = 48.dp, bottom = 2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = draft,
                        onValueChange = onDraftChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 11.dp, bottom = 12.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
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
                                        fontSize = 18.sp,
                                        lineHeight = 22.sp
                                    ),
                                    color = colors.hint
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(sendScale),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(colors.actionTop, colors.actionBottom)
                            )
                        )
                )

                Image(
                    painter = painterResource(if (draft.isBlank()) R.drawable.tg_input_mic else R.drawable.tg_send_plane_24),
                    contentDescription = null,
                    modifier = Modifier.size(if (draft.isBlank()) 28.dp else 22.dp),
                    colorFilter = ColorFilter.tint(colors.actionIcon)
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
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
            .size(44.dp)
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
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(tint)
        )
    }
}
