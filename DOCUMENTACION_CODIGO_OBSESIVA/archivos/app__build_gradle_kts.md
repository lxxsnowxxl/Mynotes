# build.gradle.kts — documentación exhaustiva por bloques

**Ruta de código real:** `app/build.gradle.kts`  
**SHA-256 del archivo ejecutable sin tocar:** `327918a9526b4cd3eb9087e4c20cb4b2cc8d846a4647c6f8ec5e9f3670e5acb0`  
**Líneas del código real:** 79

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Configuración Gradle del módulo Android: SDK, build types, compatibilidad Java, Compose y dependencias de Room, Coil, Media3, Lifecycle y pruebas.

**Arquitectura.** No contiene lógica de ejecución de MyNotes, pero determina qué APIs y bibliotecas están disponibles durante compilación y empaquetado.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

No necesita imports explícitos; utiliza tipos del mismo package o del conjunto importado implícitamente por Kotlin.

## 3. Restricciones e invariantes visibles en el archivo

- No se detectaron automáticamente operadores de restricción comunes; las restricciones específicas siguen documentadas dentro de cada declaración.

## 4. Bloques de código, uno por uno

Este archivo no contiene funciones/clases/objetos detectables con cuerpo propio. Por ello se documentan sus declaraciones de propiedades y configuración como un bloque único.

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.mynotes"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.example.mynotes"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Compatibilidad consistente de recursos vectoriales en API 24/25.
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    // Coil 3 separa el cargador de imágenes por Internet del módulo Compose.
    // Sin este artefacto AsyncImage conoce la URL, pero no puede descargarla.
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
    // Decodificador GIF por software para Android 7/8 y animaciones en APIs nuevas.
    implementation("io.coil-kt.coil3:coil-gif:3.3.0")
    implementation("androidx.media3:media3-exoplayer:1.11.0")
    /*
     * PlayerView mantiene la relación de aspecto y la salida de vídeo de
     * forma consistente desde Android 7 hasta versiones actuales.
     */
    implementation("androidx.media3:media3-ui:1.11.0")
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    /*
     * AnimatedVisibility, Crossfade y transiciones ligeras.
     * La versión la administra el Compose BOM.
     */
    implementation("androidx.compose.animation:animation")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
```
## 5. Inventario global de propiedades/variables detectadas

No se detectaron propiedades/variables con inicializador mediante el patrón habitual `val/var = ...`.

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 1–5 | 0 | `plugins` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 7–36 | 0 | `android` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 10–21 | 1 | `defaultConfig` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 18–20 | 2 | `vectorDrawables` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 22–28 | 1 | `buildTypes` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 23–27 | 2 | `release` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 29–32 | 1 | `compileOptions` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 33–35 | 1 | `buildFeatures` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 38–79 | 0 | `dependencies` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
