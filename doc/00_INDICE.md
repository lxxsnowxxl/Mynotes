# Índice de documentación — v63 (explicación ampliada)

Esta revisión corrige la carencia de v62: ya no se limita a imports, hashes y métricas. Cada fuente tiene una explicación función por función basada en el código actual.

## Documentos principales

- [`EXPLICACION_COMPLETA_DEL_CODIGO.txt`](EXPLICACION_COMPLETA_DEL_CODIGO.txt) — toda la explicación en un único TXT.
- `explicacion_texto/` — un TXT independiente por cada archivo Kotlin/KTS.
- `archivos/` — la misma explicación ampliada en Markdown, con firmas, efectos, dependencias y recursos.
- `06_MAPA_ARQUITECTURA_ACTUAL.md` — mapa de subsistemas.
- `08_AUDITORIA_CODIGO_Y_COBERTURA.md` — verificación de cobertura.

## Cobertura

- Fuentes `.kt/.kts`: **72**.
- Explicaciones Markdown detalladas: **72**.
- Explicaciones TXT detalladas: **72**.
- Cobertura documental: **100% del árbol Kotlin/KTS incluido en `app/`**.

## Fuentes documentadas

- `app/build.gradle.kts` — 79 líneas de código, 0 funciones detectadas, 69 líneas de explicación.
- `app/src/androidTest/java/com/example/mynotes/ExampleInstrumentedTest.kt` — 24 líneas de código, 1 funciones detectadas, 55 líneas de explicación.
- `app/src/main/java/com/example/mynotes/MainActivity.kt` — 1169 líneas de código, 19 funciones detectadas, 392 líneas de explicación.
- `app/src/main/java/com/example/mynotes/data/AppDataBackupManager.kt` — 499 líneas de código, 13 funciones detectadas, 296 líneas de explicación.
- `app/src/main/java/com/example/mynotes/data/AppDatabase.kt` — 123 líneas de código, 5 funciones detectadas, 113 líneas de explicación.
- `app/src/main/java/com/example/mynotes/data/Attachment.kt` — 24 líneas de código, 0 funciones detectadas, 57 líneas de explicación.
- `app/src/main/java/com/example/mynotes/data/AttachmentDao.kt` — 69 líneas de código, 10 funciones detectadas, 155 líneas de explicación.
- `app/src/main/java/com/example/mynotes/data/Note.kt` — 47 líneas de código, 0 funciones detectadas, 73 líneas de explicación.
- `app/src/main/java/com/example/mynotes/data/NoteDao.kt` — 142 líneas de código, 20 funciones detectadas, 261 líneas de explicación.
- `app/src/main/java/com/example/mynotes/data/PendingAttachment.kt` — 21 líneas de código, 0 funciones detectadas, 55 líneas de explicación.
- `app/src/main/java/com/example/mynotes/links/LinkPreviewRepository.kt` — 606 líneas de código, 21 funciones detectadas, 440 líneas de explicación.
- `app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt` — 819 líneas de código, 31 funciones detectadas, 678 líneas de explicación.
- `app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt` — 129 líneas de código, 7 funciones detectadas, 170 líneas de explicación.
- `app/src/main/java/com/example/mynotes/reminders/Reminder.kt` — 25 líneas de código, 0 funciones detectadas, 64 líneas de explicación.
- `app/src/main/java/com/example/mynotes/reminders/ReminderAlarmScheduler.kt` — 53 líneas de código, 3 funciones detectadas, 109 líneas de explicación.
- `app/src/main/java/com/example/mynotes/reminders/ReminderFeedbackPreferences.kt` — 172 líneas de código, 3 funciones detectadas, 146 líneas de explicación.
- `app/src/main/java/com/example/mynotes/reminders/ReminderReceiver.kt` — 250 líneas de código, 6 funciones detectadas, 212 líneas de explicación.
- `app/src/main/java/com/example/mynotes/reminders/ReminderRepository.kt` — 153 líneas de código, 9 funciones detectadas, 206 líneas de explicación.
- `app/src/main/java/com/example/mynotes/settings/AppSettings.kt` — 158 líneas de código, 0 funciones detectadas, 164 líneas de explicación.
- `app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt` — 640 líneas de código, 71 funciones detectadas, 1238 líneas de explicación.
- `app/src/main/java/com/example/mynotes/settings/SettingsViewModel.kt` — 6 líneas de código, 0 funciones detectadas, 47 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/AttachmentViewerActivity.kt` — 1288 líneas de código, 43 funciones detectadas, 931 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/AppPopupStyles.kt` — 143 líneas de código, 2 funciones detectadas, 105 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/AttachmentPreviewTile.kt` — 329 líneas de código, 9 funciones detectadas, 247 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/BackupRestoreSection.kt` — 163 líneas de código, 1 funciones detectadas, 83 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/ConfigurationModeDialog.kt` — 294 líneas de código, 2 funciones detectadas, 113 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/ExtremeCustomizationSection.kt` — 631 líneas de código, 5 funciones detectadas, 209 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/InlineNoteAttachment.kt` — 745 líneas de código, 15 funciones detectadas, 341 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/LinkPreviewCard.kt` — 1781 líneas de código, 81 funciones detectadas, 1464 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/NoteCard.kt` — 883 líneas de código, 12 funciones detectadas, 342 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/NoteCardStyle.kt` — 16 líneas de código, 1 funciones detectadas, 55 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/OptionsMenuCustomizationSection.kt` — 368 líneas de código, 7 funciones detectadas, 210 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/PaletteSelector.kt` — 222 líneas de código, 3 funciones detectadas, 140 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/ProfileImageEditorDialog.kt` — 492 líneas de código, 10 funciones detectadas, 312 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt` — 272 líneas de código, 5 funciones detectadas, 178 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/SettingsSectionPanel.kt` — 46 líneas de código, 1 funciones detectadas, 69 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/components/StyledSettingsSlider.kt` — 281 líneas de código, 2 funciones detectadas, 122 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt` — 342 líneas de código, 8 funciones detectadas, 205 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/sound/UiHapticPlayer.kt` — 215 líneas de código, 12 funciones detectadas, 296 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt` — 402 líneas de código, 19 funciones detectadas, 435 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/AppFonts.kt` — 51 líneas de código, 1 funciones detectadas, 66 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/Color.kt` — 11 líneas de código, 0 funciones detectadas, 51 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/DevelopmentInfoScreen.kt` — 312 líneas de código, 5 funciones detectadas, 165 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/DrawingScreen.kt` — 843 líneas de código, 9 funciones detectadas, 254 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/NoteColors.kt` — 328 líneas de código, 17 funciones detectadas, 371 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/NoteDetailScreen.kt` — 708 líneas de código, 7 funciones detectadas, 235 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/NoteEditorScreen.kt` — 1068 líneas de código, 11 funciones detectadas, 313 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/NotesScreen.kt` — 854 líneas de código, 4 funciones detectadas, 187 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt` — 71 líneas de código, 2 funciones detectadas, 82 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/ReminderScreen.kt` — 901 líneas de código, 10 funciones detectadas, 296 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/SettingsScreen.kt` — 1816 líneas de código, 8 funciones detectadas, 304 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/SettingsSectionColors.kt` — 17 líneas de código, 1 funciones detectadas, 59 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/SliderColors.kt` — 57 líneas de código, 1 funciones detectadas, 57 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/SourceCodeInfoScreen.kt` — 220 líneas de código, 4 funciones detectadas, 131 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/Theme.kt` — 269 líneas de código, 13 funciones detectadas, 307 líneas de explicación.
- `app/src/main/java/com/example/mynotes/ui/theme/Type.kt` — 28 líneas de código, 0 funciones detectadas, 60 líneas de explicación.
- `app/src/main/java/com/example/mynotes/update/GitHubUpdateManager.kt` — 228 líneas de código, 9 funciones detectadas, 232 líneas de explicación.
- `app/src/main/java/com/example/mynotes/viewmodel/NoteViewModel.kt` — 523 líneas de código, 19 funciones detectadas, 462 líneas de explicación.
- `app/src/main/java/com/example/mynotes/viewmodel/SettingsViewModel.kt` — 303 líneas de código, 54 funciones detectadas, 994 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/FavoritesWidgetProvider.kt` — 103 líneas de código, 2 funciones detectadas, 127 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/FocusNoteWidgetProvider.kt` — 93 líneas de código, 1 funciones detectadas, 92 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/MyNotesWidgetUpdater.kt` — 65 líneas de código, 2 funciones detectadas, 92 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/QuickNoteWidgetProvider.kt` — 45 líneas de código, 1 funciones detectadas, 79 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/RecentNotesWidgetProvider.kt` — 107 líneas de código, 2 funciones detectadas, 127 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/WidgetActionReceiver.kt` — 37 líneas de código, 1 funciones detectadas, 75 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/WidgetActions.kt` — 22 líneas de código, 0 funciones detectadas, 61 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/WidgetIntents.kt` — 78 líneas de código, 7 funciones detectadas, 184 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/WidgetLocale.kt` — 49 líneas de código, 3 funciones detectadas, 97 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/WidgetMediaPreview.kt` — 142 líneas de código, 9 funciones detectadas, 262 líneas de explicación.
- `app/src/main/java/com/example/mynotes/widget/WidgetPresentation.kt` — 264 líneas de código, 15 funciones detectadas, 327 líneas de explicación.
- `app/src/test/java/com/example/mynotes/ExampleUnitTest.kt` — 17 líneas de código, 1 funciones detectadas, 55 líneas de explicación.
- `app/src/test/java/com/example/mynotes/SettingsSectionColorsTest.kt` — 43 líneas de código, 4 funciones detectadas, 91 líneas de explicación.
- `17_PDF_STUDIO_V65.md` - Editor PDF, importación/exportación, imágenes y recorte por IA.
