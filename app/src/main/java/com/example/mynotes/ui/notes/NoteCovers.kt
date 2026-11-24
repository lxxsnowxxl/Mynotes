package com.example.mynotes.ui.notes

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.mynotes.ui.preview.LocalLayoutPreview
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.R
import com.example.mynotes.data.CollectionPolicy
import com.example.mynotes.data.Note
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.components.rememberCardAttachmentPreview
import com.example.mynotes.ui.components.StyledSettingsSlider
import kotlin.math.roundToInt

internal fun collectionIcon(key: String?): ImageVector = when (key) {
    "work" -> Icons.Default.Work
    "idea" -> Icons.Default.Lightbulb
    "study" -> Icons.Default.School
    "shopping" -> Icons.Default.ShoppingCart
    "book" -> Icons.Default.MenuBook
    "heart" -> Icons.Default.FavoriteBorder
    "code" -> Icons.Default.Code
    else -> Icons.Default.Folder
}

internal fun collectionIconLabel(key: String): Int = when (key) {
    "work" -> R.string.organize_icon_work
    "idea" -> R.string.organize_icon_idea
    "study" -> R.string.organize_icon_study
    "shopping" -> R.string.organize_icon_shopping
    "book" -> R.string.organize_icon_book
    "heart" -> R.string.organize_icon_heart
    "code" -> R.string.organize_icon_code
    else -> R.string.organize_icon_folder
}

@Composable
internal fun NoteCover(
    note: Note, modifier: Modifier = Modifier, performanceMode: String = "balanced",
    scrolling: State<Boolean>? = null
) {
    val preview = LocalLayoutPreview.current
    val alignment = BiasAlignment(CollectionPolicy.crop(note.coverX) * 2 - 1, CollectionPolicy.crop(note.coverY) * 2 - 1)
    if (note.coverUri != null) {
        val context = LocalContext.current.applicationContext
        val uri = remember(note.coverUri) { Uri.parse(note.coverUri) }
        val bitmap = rememberCardAttachmentPreview(note.coverUri, performanceMode, scrolling,
            initial = { if (note.coverUri.startsWith("preview:")) preview?.mediaBitmap
                else AttachmentPreviewCache.peekImagePreview(uri, performanceMode) },
            cachedLoader = { AttachmentPreviewCache.loadCachedPreview(context, uri, "image", performanceMode)?.bitmap },
            isTargetPreview = { AttachmentPreviewCache.isExactBitmapPreview("image", uri, performanceMode, it) },
            loader = { mode -> AttachmentPreviewCache.loadImagePreview(context, uri, mode) })
        val image = bitmap
        if (image != null) Image(remember(image) { image.asImageBitmap() }, stringResource(R.string.organize_cover), modifier.clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop, alignment = alignment)
        else Box(modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceContainerLow), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Image, stringResource(R.string.organize_cover))
        }
    } else if (note.coverIcon != null) Box(modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center) {
        Icon(collectionIcon(note.coverIcon), stringResource(R.string.organize_cover), Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
internal fun CoverPickerDialog(uri: String?, icon: String?, x: Float, y: Float,
    onPickImage: () -> Unit, onIcon: (String?) -> Unit, onCrop: (Float, Float) -> Unit, onDismiss: () -> Unit) {
    AppAlertDialog(onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.organize_cover)) },
        text = {
            Column(Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NoteCover(Note(title = "", content = "", coverUri = uri, coverIcon = icon, coverX = x, coverY = y), Modifier.fillMaxWidth().height(160.dp))
                OutlinedButton(onClick = { onPickImage(); UiSoundPlayer.playFallbackClick() }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Icon(Icons.Default.Image, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.organize_choose_cover))
                }
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    CollectionPolicy.icons.forEach { key ->
                        IconToggleButton(checked = uri == null && icon == key, onCheckedChange = { onIcon(key) ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.size(48.dp)) {
                            Icon(collectionIcon(key), stringResource(collectionIconLabel(key)))
                        }
                    }
                }
                if (uri != null) {
                    Text(stringResource(R.string.organize_crop_horizontal))
                    StyledSettingsSlider(value = x, onValueChange = { onCrop(it, y) }, valueLabel = "${(x * 100f).roundToInt()}%")
                    Text(stringResource(R.string.organize_crop_vertical))
                    StyledSettingsSlider(value = y, onValueChange = { onCrop(x, it) }, valueLabel = "${(y * 100f).roundToInt()}%")
                }
                TextButton(onClick = { onIcon(null) ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Delete)}) { Text(stringResource(R.string.organize_remove_cover)) }
            }
        }, confirmButton = { TextButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.organize_done)) } })
}
