# MyNotes v42 — compact widget resources

## Why the project grew so much

The widget palette implementation generated ten XML drawables for every palette/tone combination:

46 palettes × 4 tones × 10 surface types = 1,840 files.

## v42 strategy

Only the widget ROOT keeps a palette/tone-specific rounded drawable:

46 palettes × 4 tones = 184 root drawables.

Internal widget surfaces now reuse shared translucent rounded drawables for:

- card
- note
- detail
- metric
- action
- primary action
- thumbnail
- chip
- count badge

Because these surfaces are translucent, the selected palette root remains visible underneath. This keeps the widget visually tied to the chosen palette without requiring one XML per internal surface and palette.

## Compatibility

This avoids dynamic `setBackgroundTintList` on RemoteViews, preserving the API 28/Samsung compatibility fix used after the earlier partial widget rendering issue.

The rounded root is still a normal XML shape resource, so the widget outer corners are preserved.
