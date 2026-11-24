plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Optional local APK for a single device, for example -PtargetAbi=arm64-v8a.
// Omitting this property preserves support for every architecture supplied by dependencies.
val targetAbi = providers.gradleProperty("targetAbi").orNull
require(targetAbi == null || targetAbi in setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")) {
    "targetAbi must be armeabi-v7a, arm64-v8a, x86 or x86_64"
}

android {
    namespace = "com.example.mynotes"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.example.mynotes"
        minSdk = 24
        targetSdk = 36
        versionCode = 30604
        versionName = "7.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        targetAbi?.let { selectedAbi -> ndk { abiFilters += selectedAbi } }
        // Compatibilidad consistente de recursos vectoriales en API 24/25.
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            // Se conserva R8/resource shrinking para mantener el APK ligero y rápido.
            // Las clases de Subject Segmentation que el módulo opcional resuelve en
            // runtime se protegen específicamente en proguard-rules.pro.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        create("optimized") {
            // Release optimizations with the local debug certificate, for on-device checks.
            // This does not include src/debug, UI tooling or the Compose test activity.
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
            matchingFallbacks += listOf("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("androidx.work:work-runtime-ktx:2.10.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-mlkit-document-scanner:16.0.0")
    // Vosk y JNA se retiraron: ningún archivo Kotlin de la app utiliza sus APIs.
    // Sus bibliotecas nativas para múltiples arquitecturas incrementaban notablemente el APK.
    // La grabación de notas de voz usa android.media.MediaRecorder y sigue habilitada.
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    // Coil 3 separa el cargador de imágenes por Internet del módulo Compose.
    // Sin este artefacto AsyncImage conoce la URL, pero no puede descargarla.
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
    // Decodificador GIF por software para Android 7/8 y animaciones en APIs nuevas.
    implementation("io.coil-kt.coil3:coil-gif:3.3.0")
    // Recorte por IA de imágenes dentro del editor PDF. El modelo de
    // Subject Segmentation se entrega mediante Google Play Services.
    implementation("com.google.android.gms:play-services-mlkit-subject-segmentation:16.0.0-beta1")
    implementation("com.google.android.gms:play-services-base:18.11.0")
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
    // Fuente descargable oficial de Google Fonts; no se empaqueta el antiguo binario cerrado.
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation(libs.androidx.compose.ui.graphics)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    // Execute JSON codec and drawing/transcription metadata tests on the JVM.
    testImplementation("org.json:json:20240303")
    testImplementation("org.robolectric:robolectric:4.16.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
}
