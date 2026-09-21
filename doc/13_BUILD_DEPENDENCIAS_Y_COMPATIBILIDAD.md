# Build, dependencias y compatibilidad

- namespace/applicationId: `com.example.mynotes`
- minSdk: **24**
- targetSdk: **36**
- compileSdk: **37**
- versionCode: **1**
- versionName: **1.3.0**
- Java source/target: **11**
- Compose habilitado.
- Release: minify + shrinkResources.
- Gradle wrapper: **9.3.0**.

## Dependencias declaradas (lectura de build.gradle.kts)

- `"androidx.datastore:datastore-preferences:1.1.7"`
- `"androidx.appcompat:appcompat:1.7.1"`
- `"io.coil-kt.coil3:coil-compose:3.3.0"`
- `"io.coil-kt.coil3:coil-network-okhttp:3.3.0"`
- `"io.coil-kt.coil3:coil-gif:3.3.0"`
- `"androidx.media3:media3-exoplayer:1.11.0"`
- `"androidx.media3:media3-ui:1.11.0"`
- `libs.androidx.lifecycle.viewmodel.compose`
- `libs.androidx.lifecycle.runtime.ktx`
- `"androidx.lifecycle:lifecycle-runtime-compose:2.11.0"`
- `"androidx.room:room-runtime:2.7.2"`
- `"androidx.room:room-ktx:2.7.2"`
- `"androidx.room:room-compiler:2.7.2"`
- `platform(libs.androidx.compose.bom)`
- `libs.androidx.activity.compose`
- `libs.androidx.compose.material3`
- `"androidx.compose.animation:animation"`
- `libs.androidx.compose.ui`
- `libs.androidx.compose.ui.graphics`
- `libs.androidx.compose.ui.tooling.preview`
- `"androidx.compose.material:material-icons-extended"`
- `libs.androidx.core.ktx`
- `libs.junit`
- `platform(libs.androidx.compose.bom)`
- `libs.androidx.compose.ui.test.junit4`
- `libs.androidx.espresso.core`
- `libs.androidx.junit`

## Portabilidad
`local.properties` apunta a un SDK de Windows específico de la máquina de desarrollo; debe regenerarse en otras máquinas y no es parte portable de la arquitectura.
