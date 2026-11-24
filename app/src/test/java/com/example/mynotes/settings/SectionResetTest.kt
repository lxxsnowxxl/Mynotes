package com.example.mynotes.settings
import androidx.datastore.preferences.core.*
import org.junit.Assert.*
import org.junit.Test
class SectionResetTest {
 @Test fun resettingAppearanceKeepsIndependentFeedbackAndLanguage(){
  val preferences=mutablePreferencesOf(booleanPreferencesKey("dark_mode") to true,floatPreferencesKey("font_size") to 25f,floatPreferencesKey("sound_effects_volume") to 12f,floatPreferencesKey("reminder_sound_volume") to 87f,stringPreferencesKey("language") to "en")
  SettingsRepository.writeResetSection(preferences,"Appearance")
  assertNull(preferences[booleanPreferencesKey("dark_mode")]);assertNull(preferences[floatPreferencesKey("font_size")])
  assertEquals(12f,preferences[floatPreferencesKey("sound_effects_volume")]!!,0f);assertEquals(87f,preferences[floatPreferencesKey("reminder_sound_volume")]!!,0f);assertEquals("en",preferences[stringPreferencesKey("language")])
 }
 @Test fun resetSectionsHaveNoOverlappingKeys(){val sections=PlusSettingsPolicy.sections.values;assertEquals(sections.sumOf{it.size},sections.flatten().distinct().size)}
 @Test fun unknownSectionCannotClearPreferences(){val preferences=mutablePreferencesOf(stringPreferencesKey("language") to "en");assertThrows(IllegalStateException::class.java){SettingsRepository.writeResetSection(preferences,"unknown")};assertEquals("en",preferences[stringPreferencesKey("language")])}
}
