package com.example.mynotes.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as GoogleFontsFont
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.mynotes.R
import com.example.mynotes.settings.FontPreferencePolicy

/*
 * Google Sans Bold se solicita al proveedor oficial de Google Fonts.
 * Conservamos la clave de preferencia histórica para no romper ajustes/backups.
 */
private val googleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val googleSansBoldName = GoogleFont("Google Sans")

private val googleSansBoldFamily = FontFamily(
    GoogleFontsFont(googleFont = googleSansBoldName, fontProvider = googleFontsProvider, weight = FontWeight.Bold)
)

/**
 * Convierte una clave ya validada por SettingsRepository en la familia Compose.
 *
 * SYSTEM_DEFAULT deja a Android/OEM resolver la tipografía predeterminada real
 * del dispositivo. Las preferencias de tipografía retiradas se migran a
 * SYSTEM_DEFAULT antes de llegar aquí.
 */
fun appFontFamily(key: String): FontFamily {
    return when (FontPreferencePolicy.normalize(key, googleSansFlexUnlocked = true)) {
        FontPreferencePolicy.SYSTEM_DEFAULT -> FontFamily.Default
        FontPreferencePolicy.SERIF -> FontFamily.Serif
        FontPreferencePolicy.MONOSPACE -> FontFamily.Monospace
        FontPreferencePolicy.DEVELOPER_GOOGLE_SANS_FLEX -> googleSansBoldFamily
        else -> FontFamily.Default
    }
}
