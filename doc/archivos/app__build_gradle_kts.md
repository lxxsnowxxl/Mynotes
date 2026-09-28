# build.gradle.kts — explicación completa

**Ruta:** `app/build.gradle.kts`  
**SHA-256:** `39f5bb80281605646df213c95938df1e8c06a76a912a8599a2ccd2a942c17a8e`  
**Líneas:** 79

## Qué hace este archivo

Configuración Gradle del módulo app: SDK, identificador, Compose/Kotlin, dependencias y opciones de empaquetado/build.

## Configuración Android detectada

- **namespace:** `"com.example.mynotes"` — define el valor de compilación/empaquetado correspondiente.
- **compileSdk:** `37` — define el valor de compilación/empaquetado correspondiente.
- **minSdk:** `24` — define el valor de compilación/empaquetado correspondiente.
- **targetSdk:** `36` — define el valor de compilación/empaquetado correspondiente.
- **versionCode:** `1` — define el valor de compilación/empaquetado correspondiente.
- **versionName:** `"1.3.0"` — define el valor de compilación/empaquetado correspondiente.

## Plugins

- `android.application`
- `kotlin.compose`
- `ksp`

Estos plugins habilitan el módulo Android, Kotlin/Compose y los procesadores declarados por el proyecto.

## Dependencias declaradas

- **implementation:** `"androidx.datastore:datastore-preferences:1.1.7"`
- **implementation:** `"androidx.appcompat:appcompat:1.7.1"`
- **implementation:** `"io.coil-kt.coil3:coil-compose:3.3.0"`
- **implementation:** `"io.coil-kt.coil3:coil-network-okhttp:3.3.0"`
- **implementation:** `"io.coil-kt.coil3:coil-gif:3.3.0"`
- **implementation:** `"androidx.media3:media3-exoplayer:1.11.0"`
- **implementation:** `"androidx.media3:media3-ui:1.11.0"`
- **implementation:** `libs.androidx.lifecycle.viewmodel.compose`
- **implementation:** `libs.androidx.lifecycle.runtime.ktx`
- **implementation:** `"androidx.lifecycle:lifecycle-runtime-compose:2.11.0"`
- **implementation:** `"androidx.room:room-runtime:2.7.2"`
- **implementation:** `"androidx.room:room-ktx:2.7.2"`
- **ksp:** `"androidx.room:room-compiler:2.7.2"`
- **implementation:** `platform(libs.androidx.compose.bom)`
- **implementation:** `libs.androidx.activity.compose`
- **implementation:** `libs.androidx.compose.material3`
- **implementation:** `"androidx.compose.animation:animation"`
- **implementation:** `libs.androidx.compose.ui`
- **implementation:** `libs.androidx.compose.ui.graphics`
- **implementation:** `libs.androidx.compose.ui.tooling.preview`
- **implementation:** `"androidx.compose.material:material-icons-extended"`
- **implementation:** `libs.androidx.core.ktx`
- **testImplementation:** `libs.junit`
- **androidTestImplementation:** `platform(libs.androidx.compose.bom)`
- **androidTestImplementation:** `libs.androidx.compose.ui.test.junit4`
- **androidTestImplementation:** `libs.androidx.espresso.core`
- **androidTestImplementation:** `libs.androidx.junit`
- **debugImplementation:** `libs.androidx.compose.ui.test.manifest`
- **debugImplementation:** `libs.androidx.compose.ui.tooling`

## Cómo afecta al resto del proyecto

Este archivo no contiene lógica de pantalla. Determina qué APIs pueden compilarse, qué librerías están disponibles y qué versión se empaqueta. Un cambio de SDK/plugin/dependencia puede afectar a todos los archivos Kotlin aunque ninguno de ellos cambie.

## Qué revisar antes de modificarlo

- Compatibilidad entre AGP, Kotlin, Compose y Gradle wrapper.
- `minSdk` porque el código contiene ramas específicas para APIs antiguas/nuevas.
- Dependencias de Room/KSP, Coil, Compose y AndroidX utilizadas por las capas de datos/UI.
- `versionName` y `versionCode`, ya que el actualizador de GitHub compara la versión instalada.
