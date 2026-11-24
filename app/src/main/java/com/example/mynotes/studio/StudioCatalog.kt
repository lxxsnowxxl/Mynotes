package com.example.mynotes.studio

internal data class StudioFeature(val id:Int,val label:String,val route:String)
internal object StudioCatalog{
 val all=listOf(
 "Dashboard|home","Document tabs|tabs","Command palette|commands","Long-press preview|preview","Folder breadcrumbs|folders","Unified search|search","One-handed and left-handed controls|preferences","Operations center|jobs",
 "Nested folders|folders","Smart collections|collections","Kanban board|kanban","Project timeline|timeline","Backlinks and mentions|links","Relationship graph|graph","Visual card boards|board",
 "Custom properties|editor","Editable tables|editor","Footnotes and references|editor","Document outline|editor","Merge and split notes|editor","Reusable snippets|editor","Writing goals and statistics|editor","Mathematical formulas|editor","History with attachments|editor",
 "Infinite drawing canvas|drawing","Canvas rotation|drawing","Stroke stabilizer|drawing","Shape recognition|drawing","Symmetry drawing|drawing","Fill closed areas|drawing","Reusable stamps|drawing","SVG export|drawing",
 "Merge PDFs|pdf","Split PDF chapters|pdf","Camera scanner|scan","Permanent PDF redaction|pdf","PDF forms|pdf","Annotation browser|pdf","Crop and rotate PDF pages|pdf","Read PDF aloud|pdf",
 "Daily and weekly agenda|agenda","Subtasks and progress|tasks","Advanced recurrence|reminders","Location reminders|locations","Occurrence limits|reminders","Multiple reminder alerts|reminders","Reminder history|reminder_history","Pomodoro timer|pomodoro",
 "Two notes side by side|split_notes","PDF and note workspace|split_pdf","Floating reference note|reference","External display presentation|presentation","Custom keyboard shortcuts|shortcuts","Mouse context menus|context","Two/three-finger undo and redo|drawing","Saved workspaces|workspaces",
 "Audio synchronized with writing|audio","Local timestamped transcription|transcription","Spaced repetition study cards|study","Password-encrypted notebooks|vault","Complete selective backups|backup","Device synchronization|sync","Shared notebook permissions|sharing","Recoverable trash|trash"
 ).mapIndexed{index,line->StudioFeature(index+1,line.substringBefore('|'),line.substringAfter('|'))}
}
