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
        versionName = "1.5.0"
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
