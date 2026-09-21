# Documentación completa de MyNotes — revisión v62

Esta carpeta fue reconstruida comparando el `doc.rar` recibido con el código de **v61**. La carpeta original cubría 48 archivos Kotlin/KTS; el árbol actual contiene **72**, por lo que faltaban **24** documentos y los existentes necesitaban sincronización de hashes/métricas.

## Cobertura

- Kotlin/Kotlin DSL documentados: **72/72 (100%)**.
- Recursos `src/main/res` inventariados: **632**.
- Widgets registrados actualmente: **4** (`QuickNote`, `RecentNotes`, `Favorites`, `FocusNote`).
- Archivos del subsistema Recordatorios: **5**.
- Localizaciones de recursos detectadas: `values, values-en, values-es, values-fr, values-v26, values-v31, values-zh-rCN`.
- Hashes: `02_SHA256_CODIGO_INTACTO.txt`.
- Métricas: `03_METRICAS_DOCUMENTACION.csv`.
- Auditoría: `08_AUDITORIA_CODIGO_Y_COBERTURA.md`.

## Documentos de arquitectura/funciones

- `04_CAMBIOS_RECIENTES_INTEGRADOS.md`
- `05_RECURSOS_XML_Y_CONFIGURACION.md`
- `06_MAPA_ARQUITECTURA_ACTUAL.md`
- `07_IMAGENES_DEL_PROYECTO.md`
- `08_AUDITORIA_CODIGO_Y_COBERTURA.md`
- `09_RECORDATORIOS_Y_NOTIFICACIONES.md`
- `10_WIDGETS_ACTUALES.md`
- `11_DIBUJO_Y_EDITOR.md`
- `12_ENLACES_ACTUALIZADOR_Y_COMPARTIR.md`
- `13_BUILD_DEPENDENCIAS_Y_COMPATIBILIDAD.md`

## Índice de fuentes

| Archivo real | Documento | Líneas | SHA-256 |
|---|---|---:|---|
| `app/build.gradle.kts` | [`app__build_gradle_kts.md`](archivos/app__build_gradle_kts.md) | 79 | `39f5bb802816…` |
| `app/src/androidTest/java/com/example/mynotes/ExampleInstrumentedTest.kt` | [`app__src__androidTest__java__com__example__mynotes__ExampleInstrumentedTest_kt.md`](archivos/app__src__androidTest__java__com__example__mynotes__ExampleInstrumentedTest_kt.md) | 24 | `5211c2be48ea…` |
| `app/src/main/java/com/example/mynotes/MainActivity.kt` | [`app__src__main__java__com__example__mynotes__MainActivity_kt.md`](archivos/app__src__main__java__com__example__mynotes__MainActivity_kt.md) | 1169 | `521e82c74bbb…` |
| `app/src/main/java/com/example/mynotes/data/AppDataBackupManager.kt` | [`app__src__main__java__com__example__mynotes__data__AppDataBackupManager_kt.md`](archivos/app__src__main__java__com__example__mynotes__data__AppDataBackupManager_kt.md) | 499 | `5d9cc192f4cf…` |
| `app/src/main/java/com/example/mynotes/data/AppDatabase.kt` | [`app__src__main__java__com__example__mynotes__data__AppDatabase_kt.md`](archivos/app__src__main__java__com__example__mynotes__data__AppDatabase_kt.md) | 123 | `1fa3a2fe2172…` |
| `app/src/main/java/com/example/mynotes/data/Attachment.kt` | [`app__src__main__java__com__example__mynotes__data__Attachment_kt.md`](archivos/app__src__main__java__com__example__mynotes__data__Attachment_kt.md) | 24 | `d84232af3101…` |
| `app/src/main/java/com/example/mynotes/data/AttachmentDao.kt` | [`app__src__main__java__com__example__mynotes__data__AttachmentDao_kt.md`](archivos/app__src__main__java__com__example__mynotes__data__AttachmentDao_kt.md) | 69 | `b08c95b65977…` |
| `app/src/main/java/com/example/mynotes/data/Note.kt` | [`app__src__main__java__com__example__mynotes__data__Note_kt.md`](archivos/app__src__main__java__com__example__mynotes__data__Note_kt.md) | 47 | `9214c204b4c1…` |
| `app/src/main/java/com/example/mynotes/data/NoteDao.kt` | [`app__src__main__java__com__example__mynotes__data__NoteDao_kt.md`](archivos/app__src__main__java__com__example__mynotes__data__NoteDao_kt.md) | 142 | `7f974ecce790…` |
| `app/src/main/java/com/example/mynotes/data/PendingAttachment.kt` | [`app__src__main__java__com__example__mynotes__data__PendingAttachment_kt.md`](archivos/app__src__main__java__com__example__mynotes__data__PendingAttachment_kt.md) | 21 | `93815c8c9aad…` |
| `app/src/main/java/com/example/mynotes/links/LinkPreviewRepository.kt` | [`app__src__main__java__com__example__mynotes__links__LinkPreviewRepository_kt.md`](archivos/app__src__main__java__com__example__mynotes__links__LinkPreviewRepository_kt.md) | 606 | `d4b2960786de…` |
| `app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt` | [`app__src__main__java__com__example__mynotes__performance__AttachmentPreviewCache_kt.md`](archivos/app__src__main__java__com__example__mynotes__performance__AttachmentPreviewCache_kt.md) | 819 | `b5e0d23958e7…` |
| `app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt` | [`app__src__main__java__com__example__mynotes__performance__DisplayPerformanceController_kt.md`](archivos/app__src__main__java__com__example__mynotes__performance__DisplayPerformanceController_kt.md) | 129 | `ce8d178a143a…` |
| `app/src/main/java/com/example/mynotes/reminders/Reminder.kt` | [`app__src__main__java__com__example__mynotes__reminders__Reminder_kt.md`](archivos/app__src__main__java__com__example__mynotes__reminders__Reminder_kt.md) | 25 | `7375bc5e54ae…` |
| `app/src/main/java/com/example/mynotes/reminders/ReminderAlarmScheduler.kt` | [`app__src__main__java__com__example__mynotes__reminders__ReminderAlarmScheduler_kt.md`](archivos/app__src__main__java__com__example__mynotes__reminders__ReminderAlarmScheduler_kt.md) | 53 | `2d50b0fc0d0b…` |
| `app/src/main/java/com/example/mynotes/reminders/ReminderFeedbackPreferences.kt` | [`app__src__main__java__com__example__mynotes__reminders__ReminderFeedbackPreferences_kt.md`](archivos/app__src__main__java__com__example__mynotes__reminders__ReminderFeedbackPreferences_kt.md) | 172 | `a39bb2849c64…` |
| `app/src/main/java/com/example/mynotes/reminders/ReminderReceiver.kt` | [`app__src__main__java__com__example__mynotes__reminders__ReminderReceiver_kt.md`](archivos/app__src__main__java__com__example__mynotes__reminders__ReminderReceiver_kt.md) | 250 | `dfa8498ebf65…` |
| `app/src/main/java/com/example/mynotes/reminders/ReminderRepository.kt` | [`app__src__main__java__com__example__mynotes__reminders__ReminderRepository_kt.md`](archivos/app__src__main__java__com__example__mynotes__reminders__ReminderRepository_kt.md) | 153 | `825bc3d535cc…` |
| `app/src/main/java/com/example/mynotes/settings/AppSettings.kt` | [`app__src__main__java__com__example__mynotes__settings__AppSettings_kt.md`](archivos/app__src__main__java__com__example__mynotes__settings__AppSettings_kt.md) | 158 | `c0f0b3a5c38f…` |
| `app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt` | [`app__src__main__java__com__example__mynotes__settings__SettingsRepository_kt.md`](archivos/app__src__main__java__com__example__mynotes__settings__SettingsRepository_kt.md) | 640 | `a1b5c28d763a…` |
| `app/src/main/java/com/example/mynotes/settings/SettingsViewModel.kt` | [`app__src__main__java__com__example__mynotes__settings__SettingsViewModel_kt.md`](archivos/app__src__main__java__com__example__mynotes__settings__SettingsViewModel_kt.md) | 6 | `addc44b9b2a1…` |
| `app/src/main/java/com/example/mynotes/ui/AttachmentViewerActivity.kt` | [`app__src__main__java__com__example__mynotes__ui__AttachmentViewerActivity_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__AttachmentViewerActivity_kt.md) | 1288 | `101848b08850…` |
| `app/src/main/java/com/example/mynotes/ui/components/AppPopupStyles.kt` | [`app__src__main__java__com__example__mynotes__ui__components__AppPopupStyles_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__AppPopupStyles_kt.md) | 143 | `6630e22ea68d…` |
| `app/src/main/java/com/example/mynotes/ui/components/AttachmentPreviewTile.kt` | [`app__src__main__java__com__example__mynotes__ui__components__AttachmentPreviewTile_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__AttachmentPreviewTile_kt.md) | 329 | `02a4cd129400…` |
| `app/src/main/java/com/example/mynotes/ui/components/BackupRestoreSection.kt` | [`app__src__main__java__com__example__mynotes__ui__components__BackupRestoreSection_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__BackupRestoreSection_kt.md) | 163 | `89e8386c8cf3…` |
| `app/src/main/java/com/example/mynotes/ui/components/ConfigurationModeDialog.kt` | [`app__src__main__java__com__example__mynotes__ui__components__ConfigurationModeDialog_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__ConfigurationModeDialog_kt.md) | 294 | `b9f0581f2c1e…` |
| `app/src/main/java/com/example/mynotes/ui/components/ExtremeCustomizationSection.kt` | [`app__src__main__java__com__example__mynotes__ui__components__ExtremeCustomizationSection_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__ExtremeCustomizationSection_kt.md) | 631 | `d0469cb4099d…` |
| `app/src/main/java/com/example/mynotes/ui/components/InlineNoteAttachment.kt` | [`app__src__main__java__com__example__mynotes__ui__components__InlineNoteAttachment_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__InlineNoteAttachment_kt.md) | 745 | `b378709c6468…` |
| `app/src/main/java/com/example/mynotes/ui/components/LinkPreviewCard.kt` | [`app__src__main__java__com__example__mynotes__ui__components__LinkPreviewCard_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__LinkPreviewCard_kt.md) | 1781 | `64f3f5dee465…` |
| `app/src/main/java/com/example/mynotes/ui/components/NoteCard.kt` | [`app__src__main__java__com__example__mynotes__ui__components__NoteCard_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__NoteCard_kt.md) | 883 | `6388b10cdc88…` |
| `app/src/main/java/com/example/mynotes/ui/components/NoteCardStyle.kt` | [`app__src__main__java__com__example__mynotes__ui__components__NoteCardStyle_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__NoteCardStyle_kt.md) | 16 | `f43d417b6317…` |
| `app/src/main/java/com/example/mynotes/ui/components/OptionsMenuCustomizationSection.kt` | [`app__src__main__java__com__example__mynotes__ui__components__OptionsMenuCustomizationSection_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__OptionsMenuCustomizationSection_kt.md) | 368 | `e90945c9af4c…` |
| `app/src/main/java/com/example/mynotes/ui/components/PaletteSelector.kt` | [`app__src__main__java__com__example__mynotes__ui__components__PaletteSelector_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__PaletteSelector_kt.md) | 222 | `97497f7e2f3f…` |
| `app/src/main/java/com/example/mynotes/ui/components/ProfileImageEditorDialog.kt` | [`app__src__main__java__com__example__mynotes__ui__components__ProfileImageEditorDialog_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__ProfileImageEditorDialog_kt.md) | 492 | `599379aad0d5…` |
| `app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt` | [`app__src__main__java__com__example__mynotes__ui__components__ScrollPositionCapsule_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__ScrollPositionCapsule_kt.md) | 272 | `8e18edfb325b…` |
| `app/src/main/java/com/example/mynotes/ui/components/SettingsSectionPanel.kt` | [`app__src__main__java__com__example__mynotes__ui__components__SettingsSectionPanel_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__SettingsSectionPanel_kt.md) | 46 | `8f7337521be3…` |
| `app/src/main/java/com/example/mynotes/ui/components/StyledSettingsSlider.kt` | [`app__src__main__java__com__example__mynotes__ui__components__StyledSettingsSlider_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__components__StyledSettingsSlider_kt.md) | 281 | `5f3228215262…` |
| `app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt` | [`app__src__main__java__com__example__mynotes__ui__motion__AppMotion_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__motion__AppMotion_kt.md) | 342 | `d00156b94c88…` |
| `app/src/main/java/com/example/mynotes/ui/sound/UiHapticPlayer.kt` | [`app__src__main__java__com__example__mynotes__ui__sound__UiHapticPlayer_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__sound__UiHapticPlayer_kt.md) | 215 | `66e3744c2bf2…` |
| `app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt` | [`app__src__main__java__com__example__mynotes__ui__sound__UiSoundPlayer_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__sound__UiSoundPlayer_kt.md) | 402 | `d1d18c785aaf…` |
| `app/src/main/java/com/example/mynotes/ui/theme/AppFonts.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__AppFonts_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__AppFonts_kt.md) | 51 | `3b9a9b91cd03…` |
| `app/src/main/java/com/example/mynotes/ui/theme/Color.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__Color_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__Color_kt.md) | 11 | `985d4a3a3467…` |
| `app/src/main/java/com/example/mynotes/ui/theme/DevelopmentInfoScreen.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__DevelopmentInfoScreen_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__DevelopmentInfoScreen_kt.md) | 312 | `7527be1880ad…` |
| `app/src/main/java/com/example/mynotes/ui/theme/DrawingScreen.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__DrawingScreen_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__DrawingScreen_kt.md) | 843 | `313cea20b675…` |
| `app/src/main/java/com/example/mynotes/ui/theme/NoteColors.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__NoteColors_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__NoteColors_kt.md) | 328 | `cf9df405cc74…` |
| `app/src/main/java/com/example/mynotes/ui/theme/NoteDetailScreen.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__NoteDetailScreen_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__NoteDetailScreen_kt.md) | 708 | `1e5c9c6df031…` |
| `app/src/main/java/com/example/mynotes/ui/theme/NoteEditorScreen.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__NoteEditorScreen_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__NoteEditorScreen_kt.md) | 1068 | `13f492397673…` |
| `app/src/main/java/com/example/mynotes/ui/theme/NotesScreen.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__NotesScreen_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__NotesScreen_kt.md) | 854 | `e25669614fa7…` |
| `app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__PaletteCatalog_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__PaletteCatalog_kt.md) | 71 | `3eea86771742…` |
| `app/src/main/java/com/example/mynotes/ui/theme/ReminderScreen.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__ReminderScreen_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__ReminderScreen_kt.md) | 901 | `8bac9372df3b…` |
| `app/src/main/java/com/example/mynotes/ui/theme/SettingsScreen.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__SettingsScreen_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__SettingsScreen_kt.md) | 1816 | `f50e0fe49514…` |
| `app/src/main/java/com/example/mynotes/ui/theme/SettingsSectionColors.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__SettingsSectionColors_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__SettingsSectionColors_kt.md) | 17 | `5ecc52a51572…` |
| `app/src/main/java/com/example/mynotes/ui/theme/SliderColors.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__SliderColors_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__SliderColors_kt.md) | 57 | `c0a82363d076…` |
| `app/src/main/java/com/example/mynotes/ui/theme/SourceCodeInfoScreen.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__SourceCodeInfoScreen_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__SourceCodeInfoScreen_kt.md) | 220 | `ac7747acb83b…` |
| `app/src/main/java/com/example/mynotes/ui/theme/Theme.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__Theme_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__Theme_kt.md) | 269 | `130e512bbdbf…` |
| `app/src/main/java/com/example/mynotes/ui/theme/Type.kt` | [`app__src__main__java__com__example__mynotes__ui__theme__Type_kt.md`](archivos/app__src__main__java__com__example__mynotes__ui__theme__Type_kt.md) | 28 | `3cbc87c84108…` |
| `app/src/main/java/com/example/mynotes/update/GitHubUpdateManager.kt` | [`app__src__main__java__com__example__mynotes__update__GitHubUpdateManager_kt.md`](archivos/app__src__main__java__com__example__mynotes__update__GitHubUpdateManager_kt.md) | 228 | `28439ad2edaa…` |
| `app/src/main/java/com/example/mynotes/viewmodel/NoteViewModel.kt` | [`app__src__main__java__com__example__mynotes__viewmodel__NoteViewModel_kt.md`](archivos/app__src__main__java__com__example__mynotes__viewmodel__NoteViewModel_kt.md) | 523 | `5550157f9fe0…` |
| `app/src/main/java/com/example/mynotes/viewmodel/SettingsViewModel.kt` | [`app__src__main__java__com__example__mynotes__viewmodel__SettingsViewModel_kt.md`](archivos/app__src__main__java__com__example__mynotes__viewmodel__SettingsViewModel_kt.md) | 303 | `6890b7930685…` |
| `app/src/main/java/com/example/mynotes/widget/FavoritesWidgetProvider.kt` | [`app__src__main__java__com__example__mynotes__widget__FavoritesWidgetProvider_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__FavoritesWidgetProvider_kt.md) | 103 | `06a28b27157d…` |
| `app/src/main/java/com/example/mynotes/widget/FocusNoteWidgetProvider.kt` | [`app__src__main__java__com__example__mynotes__widget__FocusNoteWidgetProvider_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__FocusNoteWidgetProvider_kt.md) | 93 | `c245da786b39…` |
| `app/src/main/java/com/example/mynotes/widget/MyNotesWidgetUpdater.kt` | [`app__src__main__java__com__example__mynotes__widget__MyNotesWidgetUpdater_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__MyNotesWidgetUpdater_kt.md) | 65 | `c9002312cabc…` |
| `app/src/main/java/com/example/mynotes/widget/QuickNoteWidgetProvider.kt` | [`app__src__main__java__com__example__mynotes__widget__QuickNoteWidgetProvider_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__QuickNoteWidgetProvider_kt.md) | 45 | `0a537061d639…` |
| `app/src/main/java/com/example/mynotes/widget/RecentNotesWidgetProvider.kt` | [`app__src__main__java__com__example__mynotes__widget__RecentNotesWidgetProvider_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__RecentNotesWidgetProvider_kt.md) | 107 | `2d4cda8ead18…` |
| `app/src/main/java/com/example/mynotes/widget/WidgetActionReceiver.kt` | [`app__src__main__java__com__example__mynotes__widget__WidgetActionReceiver_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__WidgetActionReceiver_kt.md) | 37 | `f4119fe7bdf5…` |
| `app/src/main/java/com/example/mynotes/widget/WidgetActions.kt` | [`app__src__main__java__com__example__mynotes__widget__WidgetActions_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__WidgetActions_kt.md) | 22 | `e96b5d6ae6fd…` |
| `app/src/main/java/com/example/mynotes/widget/WidgetIntents.kt` | [`app__src__main__java__com__example__mynotes__widget__WidgetIntents_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__WidgetIntents_kt.md) | 78 | `6699c32c4694…` |
| `app/src/main/java/com/example/mynotes/widget/WidgetLocale.kt` | [`app__src__main__java__com__example__mynotes__widget__WidgetLocale_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__WidgetLocale_kt.md) | 49 | `b1ad100c06b3…` |
| `app/src/main/java/com/example/mynotes/widget/WidgetMediaPreview.kt` | [`app__src__main__java__com__example__mynotes__widget__WidgetMediaPreview_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__WidgetMediaPreview_kt.md) | 142 | `ce79e30686a9…` |
| `app/src/main/java/com/example/mynotes/widget/WidgetPresentation.kt` | [`app__src__main__java__com__example__mynotes__widget__WidgetPresentation_kt.md`](archivos/app__src__main__java__com__example__mynotes__widget__WidgetPresentation_kt.md) | 264 | `ad2a216a39e3…` |
| `app/src/test/java/com/example/mynotes/ExampleUnitTest.kt` | [`app__src__test__java__com__example__mynotes__ExampleUnitTest_kt.md`](archivos/app__src__test__java__com__example__mynotes__ExampleUnitTest_kt.md) | 17 | `4be77c1c9460…` |
| `app/src/test/java/com/example/mynotes/SettingsSectionColorsTest.kt` | [`app__src__test__java__com__example__mynotes__SettingsSectionColorsTest_kt.md`](archivos/app__src__test__java__com__example__mynotes__SettingsSectionColorsTest_kt.md) | 43 | `e2dccc0e0e4d…` |
