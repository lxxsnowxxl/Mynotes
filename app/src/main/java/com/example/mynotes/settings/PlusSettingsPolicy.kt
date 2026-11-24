package com.example.mynotes.settings

/** Names match the persisted keys. Resetting a section never clears the entire store. */
internal object PlusSettingsPolicy {
 val sections:Map<String,Set<String>> = mapOf(
  "Appearance" to setOf("design_preset","oled_mode","dark_mode","background_color","background_tone_index","settings_background_style","settings_panel_tone","surface_panel_intensity","text_color","text_outline_enabled","slider_style","font","font_size","accent_color"),
  "Cards" to setOf("card_density","grid_columns","sort_order","note_card_corner_radius","note_card_elevation","note_card_padding","note_card_image_height","note_card_outline_enabled","note_card_outline_width","note_title_max_lines","note_content_max_lines","note_line_spacing","show_note_date","show_category_chip","show_favorite_icon"),
  "Controls" to setOf("profile_image_uri","profile_image_size","icon_style","icon_size","fab_size","option_menu_order","option_menu_hidden_items","option_menu_show_icons","option_menu_text_color","option_menu_opacity","priority_menu_hidden_items","color_menu_hidden_items"),
  "Feedback" to setOf("sound_effects_enabled","sound_effects_volume","sound_effects_theme","reminder_sound_enabled","reminder_sound_volume","reminder_ringtone","haptic_effects_enabled","haptic_effects_intensity","haptic_effects_style"),
  "Editor" to setOf("editor_focus_mode","reader_width","reader_line_spacing"),
  "Motion" to setOf("performance_mode","animations_enabled","animation_style","animation_easing","animation_speed","animation_intensity")
 )
 fun keys(section:String):Set<String> = sections[section]?:error("Unknown settings section")
}
