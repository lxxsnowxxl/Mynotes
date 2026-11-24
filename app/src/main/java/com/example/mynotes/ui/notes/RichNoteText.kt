package com.example.mynotes.ui.notes

import com.example.mynotes.ui.workspace.workspaceToolIconButtonColors
import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R

private fun annotated(rendered: NoteMarkup.Rendered, highlight: Color, fontSize: Float): AnnotatedString = buildAnnotatedString {
    append(rendered.text)
    rendered.spans.forEach { span ->
        addStyle(when (span.style) {
            NoteMarkup.Style.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
            NoteMarkup.Style.HEADING -> SpanStyle(fontWeight = FontWeight.Bold, fontSize = (fontSize * 1.25f).sp)
            NoteMarkup.Style.HIGHLIGHT -> SpanStyle(background = highlight.copy(alpha = .32f))
        }, span.start, span.end)
    }
}

internal class NoteMarkupTransformation(private val highlight: Color, private val fontSize: Float) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val rendered = NoteMarkup.render(text.text)
        return TransformedText(annotated(rendered, highlight, fontSize), object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = rendered.originalToDisplay[offset.coerceIn(0, text.length)]
            override fun transformedToOriginal(offset: Int) = rendered.displayToOriginal[offset.coerceIn(0, rendered.text.length)]
        })
    }
}

@Composable
internal fun RichNoteBody(source: String, style: TextStyle, color: Color, onToggleTask: (Int) -> Unit) {
    val blocks = remember(source) { NoteMarkup.blocks(source) }
    val highlight = MaterialTheme.colorScheme.primary
    val size = style.fontSize.value.takeIf { it.isFinite() } ?: 16f
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        blocks.forEach { block ->
            when (block) {
                is NoteMarkup.Block.Text -> com.example.mynotes.studio.StudioRichText(block.source,source,style,color){text->Text(remember(text,highlight,size){annotated(NoteMarkup.render(text),highlight,size)},style=style,color=color)}
                is NoteMarkup.Block.Task -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = block.checked, onCheckedChange = { onToggleTask(block.line) ; UiSoundPlayer.playFallbackClick()})
                    Text(remember(block.source, highlight, size) { annotated(NoteMarkup.render(block.source), highlight, size) },
                        style = style, color = color, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun FormattingBar(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onFormat: (NoteMarkup.Action) -> Unit
) {
    val colors = workspaceToolIconButtonColors() ?: IconButtonDefaults.filledTonalIconButtonColors()
    Row(modifier.horizontalScroll(rememberScrollState()).padding(horizontal = if (compact) 2.dp else 12.dp),
        horizontalArrangement = Arrangement.spacedBy(if (compact) 1.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        val actions = listOf(
            Triple(NoteMarkup.Action.BOLD, Icons.Default.FormatBold, R.string.organize_bold),
            Triple(NoteMarkup.Action.HEADING, Icons.Default.Title, R.string.organize_heading),
            Triple(NoteMarkup.Action.HIGHLIGHT, Icons.Default.FormatColorFill, R.string.organize_highlight),
            Triple(NoteMarkup.Action.BULLET, Icons.Default.FormatListBulleted, R.string.organize_bullets),
            Triple(NoteMarkup.Action.TASK, Icons.Default.CheckBox, R.string.organize_tasks)
        )
        actions.forEach { (action, icon, label) ->
            FilledTonalIconButton(
                onClick = { onFormat(action); UiSoundPlayer.playFallbackClick() },
                modifier = Modifier.size(if (compact) 42.dp else 48.dp),
                colors = colors
            ) {
                Icon(icon, stringResource(label), modifier = Modifier.size(if (compact) 21.dp else 24.dp))
            }
        }
    }
}
