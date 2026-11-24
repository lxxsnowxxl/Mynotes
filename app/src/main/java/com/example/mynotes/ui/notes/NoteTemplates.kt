package com.example.mynotes.ui.notes

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.theme.ensureUiContrast

@Composable
internal fun TemplatePickerDialog(onSelect: (String, String) -> Unit, onDismiss: () -> Unit) {
    val templates = listOf(
        R.string.organize_template_meeting to R.string.organize_template_meeting_body,
        R.string.organize_template_journal to R.string.organize_template_journal_body,
        R.string.organize_template_shopping to R.string.organize_template_shopping_body,
        R.string.organize_template_plan to R.string.organize_template_plan_body
    )
    val dialogShape = RoundedCornerShape(24.dp)
    val dialogColor = MaterialTheme.colorScheme.surface
    val dialogBorder = ensureUiContrast(
        preferred = MaterialTheme.colorScheme.primary.copy(alpha = 0.90f),
        background = dialogColor,
        minimumContrast = 2.2f
    )
    val cardColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val cardBorder = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.60f)

    AppAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .shadow(20.dp, dialogShape)
            .border(1.dp, dialogBorder, dialogShape),
        containerColor = dialogColor,
        title = {
            Text(
                stringResource(R.string.organize_templates),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                templates.forEach { (titleId, bodyId) ->
                    val title = stringResource(titleId)
                    val body = stringResource(bodyId)
                    Surface(
                        onClick = { onSelect(title, body); UiSoundPlayer.playFallbackClick() },
                        shape = RoundedCornerShape(18.dp),
                        color = cardColor,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        border = BorderStroke(1.dp, cardBorder),
                        tonalElevation = 1.dp,
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                NoteMarkup.render(body).text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 6
                            )
                            Text(
                                stringResource(R.string.organize_use_template),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            FilledTonalButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) {
                Text(stringResource(R.string.organize_blank_note))
            }
        }
    )
}
