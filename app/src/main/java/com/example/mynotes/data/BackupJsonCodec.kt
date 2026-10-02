package com.example.mynotes.data

import com.example.mynotes.settings.AppSettings
import org.json.JSONArray
import org.json.JSONObject

internal fun noteToJson(note: Note) = JSONObject().apply {
            put("id", note.id)
            put("title", note.title)
            put("content", note.content)
            put("createdAt", note.createdAt)
            put("color", note.color)
            put("priority", note.priority)
            put("category", note.category)
            put("isFavorite", note.isFavorite)
            put("isPinned", note.isPinned)
        }
internal fun attachmentToJson(attachment: Attachment, fileEntry: String) = JSONObject().apply {
            put("id", attachment.id)
            put("noteId", attachment.noteId)
            put("type", attachment.type)
            put("name", attachment.name ?: JSONObject.NULL)
            put("createdAt", attachment.createdAt)
            put("fileEntry", fileEntry)
        }
internal fun jsonToNotes(array: JSONArray): List<Note> = buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(Note(id = item.getInt("id"), title = item.optString("title", ""), content = item.optString("content", ""),
                        createdAt = item.optLong("createdAt", System.currentTimeMillis()), color = item.optString("color", "default"),
                        priority = item.optInt("priority", 0), category = item.optString("category", "personal"),
                        isFavorite = item.optBoolean("isFavorite", false), isPinned = item.optBoolean("isPinned", false)))
            }
        }
internal data class AttachmentRecord(val id: Int, val noteId: Int, val type: String, val name: String?, val createdAt: Long,
        val fileEntry: String)
internal fun jsonToAttachmentRecords(array: JSONArray): List<AttachmentRecord> = buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(AttachmentRecord(id = item.getInt("id"), noteId = item.getInt("noteId"), type = item.optString("type", "file"), name =
                            if (item.isNull("name")) null
                            else item.optString("name").takeIf { it.isNotBlank() },
                        createdAt = item.optLong("createdAt", System.currentTimeMillis()), fileEntry = item.getString("fileEntry")))
            }
        }
internal fun settingsToJson(settings: AppSettings, profileEntry: String?) = JSONObject().apply {
        put("configurationMode", settings.configurationMode)
        put("darkMode", settings.darkMode)
        put("backgroundColor", settings.backgroundColor)
        put("backgroundToneIndex", settings.backgroundToneIndex)
        put("backgroundIntensity", settings.backgroundIntensity.toDouble())
        put("settingsPanelTone", settings.settingsPanelTone.toDouble())
        put("surfacePanelIntensity", settings.surfacePanelIntensity.toDouble())
        put("headerIntensity", settings.headerIntensity.toDouble())
        put("textColor", settings.textColor)
        put("textOutlineEnabled", settings.textOutlineEnabled)
        put("sliderStyle", settings.sliderStyle)
        put("font", settings.font)
        put("fontSize", settings.fontSize.toDouble())
        put("soundEffectsEnabled", settings.soundEffectsEnabled)
        put("soundEffectsVolume", settings.soundEffectsVolume.toDouble())
        put("soundEffectsTheme", settings.soundEffectsTheme)
        put("reminderSoundEnabled", settings.reminderSoundEnabled)
        put("reminderSoundVolume", settings.reminderSoundVolume.toDouble())
        put("reminderRingtone", settings.reminderRingtone)
        put("hapticEffectsEnabled", settings.hapticEffectsEnabled)
        put("hapticEffectsIntensity", settings.hapticEffectsIntensity.toDouble())
        put("hapticEffectsStyle", settings.hapticEffectsStyle)
        put("language", settings.language)
        put("gridColumns", settings.gridColumns)
        put("sortOrder", settings.sortOrder)
        put("profileImageSize", settings.profileImageSize.toDouble())
        put("profileEntry", profileEntry ?: "")
        put("iconStyle", settings.iconStyle)
        put("iconSize", settings.iconSize.toDouble())
        put("accentColor", settings.accentColor)
        put("noteCardCornerRadius", settings.noteCardCornerRadius.toDouble())
        put("noteCardElevation", settings.noteCardElevation.toDouble())
        put("noteCardPadding", settings.noteCardPadding.toDouble())
        put("noteCardImageHeight", settings.noteCardImageHeight.toDouble())
        put("noteCardOutlineEnabled", settings.noteCardOutlineWidth > 0.01f)
        put("noteCardOutlineWidth", settings.noteCardOutlineWidth.coerceIn(0f, 6f).toDouble())
        put("noteTitleMaxLines", settings.noteTitleMaxLines)
        put("noteContentMaxLines", settings.noteContentMaxLines)
        put("noteLineSpacing", settings.noteLineSpacing.toDouble())
        put("showNoteDate", settings.showNoteDate)
        put("showCategoryChip", settings.showCategoryChip)
        put("showFavoriteIcon", settings.showFavoriteIcon)
        put("fabSize", settings.fabSize.toDouble())
        put("optionMenuOrder", settings.optionMenuOrder)
        put("optionMenuHiddenItems", settings.optionMenuHiddenItems)
        put("optionMenuShowIcons", settings.optionMenuShowIcons)
        put("optionMenuTextColor", settings.optionMenuTextColor)
        put("optionMenuOpacity", settings.optionMenuOpacity.toDouble())
        put("priorityMenuHiddenItems", settings.priorityMenuHiddenItems)
        put("colorMenuHiddenItems", settings.colorMenuHiddenItems)
        put("performanceMode", settings.performanceMode)
        put("animationsEnabled", settings.animationsEnabled)
        put("animationStyle", settings.animationStyle)
        put("animationEasing", settings.animationEasing)
        put("animationSpeed", settings.animationSpeed.toDouble())
        put("animationIntensity", settings.animationIntensity.toDouble())
    }
internal fun jsonToSettings(json: JSONObject): AppSettings {
        val defaults = AppSettings()
        return AppSettings(configurationMode = json.optString("configurationMode", "advanced"),
            darkMode = json.optBoolean("darkMode", defaults.darkMode),
            backgroundColor = json.optString("backgroundColor", defaults.backgroundColor),
            backgroundToneIndex = json.optInt("backgroundToneIndex", defaults.backgroundToneIndex),
            backgroundIntensity = json.optDouble("backgroundIntensity", defaults.backgroundIntensity.toDouble()).toFloat(),
            settingsPanelTone = json.optDouble("settingsPanelTone", defaults.settingsPanelTone.toDouble()).toFloat(),
            surfacePanelIntensity = json.optDouble("surfacePanelIntensity", defaults.surfacePanelIntensity.toDouble()).toFloat(),
            headerIntensity = json.optDouble("headerIntensity", defaults.headerIntensity.toDouble()).toFloat(),
            textColor = json.optString("textColor", defaults.textColor),
            textOutlineEnabled = json.optBoolean("textOutlineEnabled", defaults.textOutlineEnabled),
            sliderStyle = json.optString("sliderStyle", defaults.sliderStyle), font = json.optString("font", defaults.font),
            fontSize = json.optDouble("fontSize", defaults.fontSize.toDouble()).toFloat(),
            soundEffectsEnabled = json.optBoolean("soundEffectsEnabled", defaults.soundEffectsEnabled),
            soundEffectsVolume = json.optDouble("soundEffectsVolume", defaults.soundEffectsVolume.toDouble()).toFloat(),
            soundEffectsTheme = json.optString("soundEffectsTheme", defaults.soundEffectsTheme),
            reminderSoundEnabled = json.optBoolean("reminderSoundEnabled", defaults.reminderSoundEnabled),
            reminderSoundVolume = json.optDouble("reminderSoundVolume", defaults.reminderSoundVolume.toDouble()).toFloat(),
            reminderRingtone = json.optString("reminderRingtone", defaults.reminderRingtone),
            hapticEffectsEnabled = json.optBoolean("hapticEffectsEnabled", defaults.hapticEffectsEnabled),
            hapticEffectsIntensity = json.optDouble("hapticEffectsIntensity", defaults.hapticEffectsIntensity.toDouble()).toFloat(),
            hapticEffectsStyle = json.optString("hapticEffectsStyle", defaults.hapticEffectsStyle),
            language = json.optString("language", defaults.language), gridColumns = json.optInt("gridColumns", defaults.gridColumns),
            sortOrder = json.optString("sortOrder", defaults.sortOrder), profileImageUri = "",
            profileImageSize = json.optDouble("profileImageSize", defaults.profileImageSize.toDouble()).toFloat(),
            iconStyle = json.optString("iconStyle", defaults.iconStyle),
            iconSize = json.optDouble("iconSize", defaults.iconSize.toDouble()).toFloat(),
            accentColor = json.optString("accentColor", defaults.accentColor),
            noteCardCornerRadius = json.optDouble("noteCardCornerRadius", defaults.noteCardCornerRadius.toDouble()).toFloat(),
            noteCardElevation = json.optDouble("noteCardElevation", defaults.noteCardElevation.toDouble()).toFloat(),
            noteCardPadding = json.optDouble("noteCardPadding", defaults.noteCardPadding.toDouble()).toFloat(),
            noteCardImageHeight = json.optDouble("noteCardImageHeight", defaults.noteCardImageHeight.toDouble()).toFloat(),
            noteCardOutlineEnabled = json.optBoolean("noteCardOutlineEnabled", defaults.noteCardOutlineEnabled),
            noteCardOutlineWidth = if (json.optBoolean("noteCardOutlineEnabled", defaults.noteCardOutlineEnabled)) {
                    json.optDouble("noteCardOutlineWidth", 1.0).toFloat().coerceIn(0f, 6f)
                } else {
                    0f
                },
            noteTitleMaxLines = json.optInt("noteTitleMaxLines", defaults.noteTitleMaxLines),
            noteContentMaxLines = json.optInt("noteContentMaxLines", defaults.noteContentMaxLines),
            noteLineSpacing = json.optDouble("noteLineSpacing", defaults.noteLineSpacing.toDouble()).toFloat(),
            showNoteDate = json.optBoolean("showNoteDate", defaults.showNoteDate),
            showCategoryChip = json.optBoolean("showCategoryChip", defaults.showCategoryChip),
            showFavoriteIcon = json.optBoolean("showFavoriteIcon", defaults.showFavoriteIcon),
            fabSize = json.optDouble("fabSize", defaults.fabSize.toDouble()).toFloat(),
            optionMenuOrder = json.optString("optionMenuOrder", defaults.optionMenuOrder),
            optionMenuHiddenItems = json.optString("optionMenuHiddenItems", defaults.optionMenuHiddenItems),
            optionMenuShowIcons = json.optBoolean("optionMenuShowIcons", defaults.optionMenuShowIcons),
            optionMenuTextColor = json.optString("optionMenuTextColor", defaults.optionMenuTextColor),
            optionMenuOpacity = json.optDouble("optionMenuOpacity", defaults.optionMenuOpacity.toDouble()).toFloat(),
            priorityMenuHiddenItems = json.optString("priorityMenuHiddenItems", defaults.priorityMenuHiddenItems),
            colorMenuHiddenItems = json.optString("colorMenuHiddenItems", defaults.colorMenuHiddenItems),
            performanceMode = json.optString("performanceMode", defaults.performanceMode),
            animationsEnabled = json.optBoolean("animationsEnabled", defaults.animationsEnabled),
            animationStyle = json.optString("animationStyle", defaults.animationStyle),
            animationEasing = json.optString("animationEasing", defaults.animationEasing),
            animationSpeed = json.optDouble("animationSpeed", defaults.animationSpeed.toDouble()).toFloat(),
            animationIntensity = json.optDouble("animationIntensity", defaults.animationIntensity.toDouble()).toFloat())
    }
