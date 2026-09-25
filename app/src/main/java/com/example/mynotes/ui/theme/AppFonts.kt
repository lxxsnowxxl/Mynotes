package com.example.mynotes.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as GoogleFontsFont
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.mynotes.R
import com.example.mynotes.settings.FontPreferencePolicy

/*
 * Google Sans Flex se solicita al proveedor oficial de Google Fonts. El APK no
 * vuelve a empaquetar los antiguos binarios Google Sans que fueron retirados.
 */
private val googleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val googleSansFlexName = GoogleFont("Google Sans Flex")

private val googleSansFlexFamily = FontFamily(
    GoogleFontsFont(googleFont = googleSansFlexName, fontProvider = googleFontsProvider, weight = FontWeight.Normal),
    GoogleFontsFont(googleFont = googleSansFlexName, fontProvider = googleFontsProvider, weight = FontWeight.Medium),
    GoogleFontsFont(googleFont = googleSansFlexName, fontProvider = googleFontsProvider, weight = FontWeight.SemiBold),
    GoogleFontsFont(googleFont = googleSansFlexName, fontProvider = googleFontsProvider, weight = FontWeight.Bold)
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
        FontPreferencePolicy.DEVELOPER_GOOGLE_SANS_FLEX -> googleSansFlexFamily
        else -> FontFamily.Default
    }
}
