# MyNotes v21 - Sound & Motion upgrade

This revision expands UI feedback without modifying attachment rendering or thumbnail/preview code.

## New modern sound packs
- Material
- Expressive
- Prism
- Aurora
- Fluid
- Pulse

Each pack includes six short WAV assets used by MyNotes (edit, delete, priority, slider tick, attachment/action and toggle). The new packs use layered synthesized transients, pitch movement and short envelopes. Selected semantic actions can also add a low-volume accent layer for richer confirmation feedback while retaining SoundPool.

Preview playback still stops the previous preview before starting a new one, including both layers, so quickly changing styles does not stack old previews.

## New motion styles
- Expressive spring
- Container transform
- Soft reveal
- Elastic slide
- Predictive
- Tonal pop

## New easing curves
- Expressive
- Emphasized accelerate
- Emphasized decelerate

The new motion presets are based on Compose transforms (scale, slide and alpha) plus spring specs. Performance mode automatically substitutes the heavier spring presets with cheaper equivalents.

## Protected rendering paths
The attachment and thumbnail paths were intentionally not modified. Verified unchanged against v20:
- AttachmentPreviewCache.kt
- AttachmentPreviewTile.kt
- InlineNoteAttachment.kt
- LinkPreviewCard.kt
- NoteCard.kt
- LinkPreviewRepository.kt
- AttachmentViewerActivity.kt
- Attachment.kt
- AttachmentDao.kt
- PendingAttachment.kt
- WidgetMediaPreview.kt

## Validation
- Resource XML parsed successfully.
- New WAV files validated as mono PCM 16-bit / 44.1 kHz.
- New resource references checked.
- Full Gradle compile could not run in this environment because the wrapper attempts to download Gradle 9.3.0 and network access is unavailable.
