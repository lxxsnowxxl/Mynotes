package com.example.mynotes.ui.theme

import androidx.compose.runtime.Immutable
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.mynotes.R

@Immutable
data class MyNotesPalette(val key: String,
    @StringRes val labelRes: Int, val tones: List<Color>, val accent: Color)
object PaletteCatalog {
    private fun p(key: String, labelRes: Int, c1: Long, c2: Long, c3: Long, c4: Long, accent: Long) = MyNotesPalette(key = key,
        labelRes = labelRes, tones = listOf(Color(c1), Color(c2), Color(c3), Color(c4)), accent = Color(accent))
    val palettes = listOf(p("neutral", R.string.mock_palette_neutral, 0xFFF3EFE9, 0xFFD9D0C5, 0xFF726B65, 0xFF2D2D2D, 0xFF4B4743),
        p("warm", R.string.mock_palette_warm, 0xFFFFE8D2, 0xFFF3C59B, 0xFF995E38, 0xFF9C4F2A, 0xFFD9773C),
        p("sage", R.string.mock_palette_sage, 0xFFE7ECE4, 0xFFB9C9B7, 0xFF597260, 0xFF315C47, 0xFF5E836D),
        p("ocean", R.string.mock_palette_ocean, 0xFFE0F1F9, 0xFFA9D4E8, 0xFF48718A, 0xFF205D8C, 0xFF4E94BF),
        p("lavender", R.string.mock_palette_lavender, 0xFFF1EBFA, 0xFFD6C5EF, 0xFF7B6297, 0xFF6B489B, 0xFF8E6ABA),
        p("rose", R.string.mock_palette_rose, 0xFFFBE9E9, 0xFFF3BABA, 0xFFA05858, 0xFFBB4444, 0xFFD96666),
        p("sunset", R.string.mock_palette_sunset, 0xFFFFE3B3, 0xFFFBB931, 0xFFA05C16, 0xFFB64C0F, 0xFFF2771A),
        p("violet_dusk", R.string.mock_palette_violet_dusk, 0xFFF8F4E9, 0xFFF6DBC0, 0xFF935073, 0xFF502D55, 0xFF7E4769),
        p("marple", R.string.mock_palette_marple, 0xFFFFE3B3, 0xFFFFB173, 0xFFB64949, 0xFFCA2851, 0xFFE94E65),
        p("classic", R.string.mock_palette_classic, 0xFFF9F7F2, 0xFFE4E0D8, 0xFF706C67, 0xFF4A4743, 0xFF77716A),
        p("cream", R.string.mock_palette_cream, 0xFFFFF9EA, 0xFFF7EACB, 0xFF776B54, 0xFF806948, 0xFFC7A96D),
        p("graphite", R.string.mock_palette_graphite, 0xFFECEFF1, 0xFFB0BEC5, 0xFF56707D, 0xFF263238, 0xFF546E7A),
        p("soft_pink", R.string.mock_palette_soft_pink, 0xFFFFF0F4, 0xFFF9CCD8, 0xFF975B6B, 0xFFA33C5A, 0xFFD85F80),
        p("coral", R.string.mock_palette_coral, 0xFFFFE8E1, 0xFFFFB8A5, 0xFFA75546, 0xFFB7493B, 0xFFE96D56),
        p("orange", R.string.mock_palette_orange, 0xFFFFEDD9, 0xFFFFC48B, 0xFF9F5C1A, 0xFF9C4A00, 0xFFE87915),
        p("sun", R.string.mock_palette_sun, 0xFFFFF7CC, 0xFFFFE37D, 0xFF826A25, 0xFF8B6700, 0xFFE7B624),
        p("blue", R.string.mock_palette_blue, 0xFFE8F1FF, 0xFFB7D3FF, 0xFF496DA3, 0xFF28548F, 0xFF4E83D1),
        p("sky", R.string.mock_palette_sky, 0xFFEAF9FF, 0xFFB8E7F7, 0xFF417388, 0xFF267494, 0xFF4EAFD4),
        p("cyan", R.string.mock_palette_cyan, 0xFFE5FAFA, 0xFFA9E7E8, 0xFF307778, 0xFF176C70, 0xFF339EA2),
        p("green", R.string.mock_palette_green, 0xFFE9F6E9, 0xFFB6DDB4, 0xFF4C764E, 0xFF2C6133, 0xFF4D8953),
        p("mint", R.string.mock_palette_mint, 0xFFE9FAF4, 0xFFB9EAD9, 0xFF457664, 0xFF2E7961, 0xFF55A889),
        p("peach", R.string.mock_palette_peach, 0xFFFFEFE5, 0xFFFFD0B6, 0xFF965F48, 0xFFA5573C, 0xFFE3825B),
        // Paletas brillantes. Los tonos 1-2 siguen siendo claros para texto negro
        // y los tonos 3-4 mantienen suficiente contraste para texto blanco.
        p("neon_pink", R.string.mock_palette_neon_pink, 0xFFFFF0FA, 0xFFFF66C4, 0xFFC21875, 0xFF8A0E53, 0xFFFF3EAE),
        p("electric_blue", R.string.mock_palette_electric_blue, 0xFFE8F4FF, 0xFF40A9FF, 0xFF0066CC, 0xFF004799, 0xFF238CFF),
        p("electric_cyan", R.string.mock_palette_electric_cyan, 0xFFE6FFFF, 0xFF20E0FF, 0xFF007C99, 0xFF00566B, 0xFF00CFE8),
        p("neon_lime", R.string.mock_palette_neon_lime, 0xFFF3FFD6, 0xFFB6FF36, 0xFF4E7D00, 0xFF355800, 0xFF9EF01A),
        p("neon_violet", R.string.mock_palette_neon_violet, 0xFFF5EBFF, 0xFFC56BFF, 0xFF7630B5, 0xFF511B82, 0xFFAB47FF),
        p("vivid_orange", R.string.mock_palette_vivid_orange, 0xFFFFF1DE, 0xFFFF9F1C, 0xFFB94D00, 0xFF823500, 0xFFFF7A00),
        p("neon_yellow", R.string.mock_palette_neon_yellow, 0xFFFFFCE0, 0xFFFFE83D, 0xFF8A7200, 0xFF604E00, 0xFFFFD600),
        p("neon_green", R.string.mock_palette_neon_green, 0xFFE8FFE9, 0xFF55F26C, 0xFF1B7D34, 0xFF115824, 0xFF2EEA59),
        // Paletas adicionales. Cada una conserva cuatro tonos seleccionables.
        p("midnight", R.string.mock_palette_midnight, 0xFFE9EDF7, 0xFFAEBBDD, 0xFF5A6B9A, 0xFF202A44, 0xFF4B5F91),
        p("navy", R.string.mock_palette_navy, 0xFFE7EEF8, 0xFFA8BEDA, 0xFF4C6F98, 0xFF173A63, 0xFF356896),
        p("emerald", R.string.mock_palette_emerald, 0xFFE5F5EC, 0xFFAFE0C3, 0xFF3D7857, 0xFF1F6442, 0xFF3F8E63),
        p("forest", R.string.mock_palette_forest, 0xFFE9F0E6, 0xFFBDD0B4, 0xFF5D7252, 0xFF304D2C, 0xFF58784D),
        p("turquoise", R.string.mock_palette_turquoise, 0xFFE5F7F5, 0xFFA6DDD7, 0xFF337773, 0xFF176963, 0xFF32938D),
        p("ice", R.string.mock_palette_ice, 0xFFF0FAFD, 0xFFC9EAF2, 0xFF4B727C, 0xFF367486, 0xFF65AEC0),
        p("indigo", R.string.mock_palette_indigo, 0xFFEEEFFD, 0xFFC6C8F2, 0xFF6367A7, 0xFF3C427F, 0xFF6067AF),
        p("plum", R.string.mock_palette_plum, 0xFFF6EBF5, 0xFFE2BEDD, 0xFF8E5C86, 0xFF65375E, 0xFF914E87),
        p("wine", R.string.mock_palette_wine, 0xFFF6E9ED, 0xFFE3BCC8, 0xFF9E576C, 0xFF642C3E, 0xFF8F4359),
        p("cherry", R.string.mock_palette_cherry, 0xFFFFE9EC, 0xFFF7BCC5, 0xFFAD4E5C, 0xFF9E2D42, 0xFFCD4960),
        p("terracotta", R.string.mock_palette_terracotta, 0xFFF9ECE6, 0xFFE8C1AE, 0xFF975E46, 0xFF75412F, 0xFFA35E45),
        p("coffee", R.string.mock_palette_coffee, 0xFFF3ECE7, 0xFFD8C2B5, 0xFF886553, 0xFF574033, 0xFF81604D),
        p("sand", R.string.mock_palette_sand, 0xFFFBF5E8, 0xFFE8D7B3, 0xFF7D6A47, 0xFF75603C, 0xFFA18855),
        p("olive", R.string.mock_palette_olive, 0xFFF1F2E3, 0xFFD5D7A6, 0xFF6D703E, 0xFF555A29, 0xFF797E3D),
        p("mustard", R.string.mock_palette_mustard, 0xFFFFF5D9, 0xFFF0D788, 0xFF846922, 0xFF7C5D12, 0xFFA77E21),
        p("monochrome", R.string.mock_palette_monochrome, 0xFFF5F5F5, 0xFFD5D5D5, 0xFF6C6C6C, 0xFF303030, 0xFF606060))
    fun find(key: String): MyNotesPalette = palettes.firstOrNull {
            it.key == key
        } ?: palettes.first()
}
