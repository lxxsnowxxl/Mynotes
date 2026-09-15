# Recursos, Manifest, XML y configuración alrededor del código Kotlin

La carpeta obsesiva se centra en `.kt/.kts`, pero los cambios recientes también dependen de recursos Android. Este documento evita que esas relaciones queden fuera del mapa técnico.

## AndroidManifest.xml

**Permisos declarados:** `android.permission.INTERNET`, `android.permission.RECORD_AUDIO`, `android.permission.VIBRATE`, `android.permission.MODIFY_AUDIO_SETTINGS`.

**Activities declaradas:** `.ui.AttachmentViewerActivity`, `.MainActivity`.

El permiso de modificación de ajustes de audio debe entenderse junto con la lógica de `MainActivity`: la app no intenta apagar sus propios efectos, sino restaurar el stream de sistema después del ciclo de visibilidad del teclado.

## XML de `res/layout`

Los layouts `preview_*` son una representación visual para Android Studio Design/Split. La UI ejecutada por la app sigue implementada principalmente con Jetpack Compose. Por eso un XML puede quedar desactualizado visualmente sin que afecte la ejecución, pero debe sincronizarse documentalmente cuando cambia la pantalla real.

## Recursos de strings

Los textos de desarrollo, créditos, copyright, repositorio y pantallas secundarias viven en recursos localizados. `stringResource(...)` hace que el texto visible dependa del locale activo en vez de quedar hardcodeado en los composables.

## README / DEVELOPMENT

`README.md` describe el proyecto hacia GitHub/usuarios; `DEVELOPMENT.md` explica detalles técnicos de compilación y arquitectura. No participan en runtime Android, pero forman parte de la documentación del repositorio.
