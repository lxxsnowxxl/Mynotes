package com.example.mynotes.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as GoogleFontsFont
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.mynotes.R

/*
 * Google Sans Flex is requested from the official Google Fonts provider only
 * after the hidden developer option has been selected. The APK does not bundle
 * the legacy Google Sans binaries that identified themselves as non-open-source.
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
 * Font mapping for MyNotes.
 *
 * `system_default` follows the device/OEM default Compose font family, while
 * `system_sans` keeps Android's generic sans-serif family.
 *
 * Older builds stored several `google_sans*` keys in DataStore. Those legacy
 * keys remain mapped to Android sans-serif so an update never tries to revive
 * the old unlicensed font files. The new developer-only key uses Google Sans
 * Flex from the official Google Fonts provider.
 */
fun appFontFamily(key: String): FontFamily {
    return when (key) {
        "system_default" -> FontFamily.Default
        "serif" -> FontFamily.Serif
        "monospace" -> FontFamily.Monospace
        "developer_google_sans_flex" -> googleSansFlexFamily
        "system_sans",
        "default",
        "google_sans",
        "google_sans_regular",
        "google_sans_medium",
        "google_sans_bold",
        "google_sans_italic",
        "google_sans_medium_italic",
        "google_sans_bold_italic",
        "google_sans_flex" -> FontFamily.SansSerif
        else -> FontFamily.SansSerif
    }
}
