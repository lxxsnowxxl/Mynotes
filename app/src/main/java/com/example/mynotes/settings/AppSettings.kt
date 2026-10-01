package com.example.mynotes.settings

import androidx.compose.runtime.Immutable

@Immutable
data class AppSettings(
    /*
     * Modo de configuración de la interfaz:
     * unset    = todavía no se ha elegido en el primer inicio
     * basic    = muestra únicamente los controles esenciales
     * advanced = conserva todos los apartados de Configuración
     */
    val configurationMode: String = "unset",
    val darkMode: Boolean = false,
    /*
     * Paleta global. PaletteCatalog contiene actualmente 46 paletas,
     * cada una con exactamente cuatro tonalidades seleccionables.
     */
    val backgroundColor: String = "neutral",
    /*
     * 0 = tono más claro
     * 3 = tono más oscuro
     */
    val backgroundToneIndex: Int = 0,
    val backgroundIntensity: Float = 0f,
    /*
     * Tonalidad del gran panel de Configuración.
     *
     * 0%   = surfaceContainerLow original
     * 100% = tono seleccionado de la paleta
     */
    val settingsPanelTone: Float = 0f,
    /*
     * Intensidad de los recuadros/paneles translúcidos de la interfaz.
     *
     * 0%   = se funden con el fondo
     * 72%  = apariencia anterior de MyNotes
     * 100% = máximo contraste del panel
     */
    val surfacePanelIntensity: Float = 72f,
    val headerIntensity: Float = 18f,
    /*
     * Color global de interfaz.
     */
    /*
     * auto  = calcula negro/blanco según contraste con el fondo
     * black = fuerza texto negro
     * white = fuerza texto blanco
     */
    val textColor: String = "auto",
    /*
     * Añade un halo/contorno negro ligero alrededor del texto.
     * Está desactivado por defecto para no añadir trabajo gráfico.
     */
    val textOutlineEnabled: Boolean = false,
    /*
     * Diseños conservados para las barras.
     */
    val sliderStyle: String = "capsule",
    val font: String = FontPreferencePolicy.SYSTEM_DEFAULT,
    val fontSize: Float = 16f,
    /*
     * Efectos cortos de interfaz: editar, borrar, prioridad, sliders
     * y adjuntos. El volumen se guarda como porcentaje para que sea
     * sencillo exponerlo en Configuración.
     */
    val soundEffectsEnabled: Boolean = true,
    val soundEffectsVolume: Float = 65f,
    /*
     * Paquete de sonidos para los efectos de interfaz.
     * classic, soft, digital, glass, retro o pop.
     */
    val soundEffectsTheme: String = "classic",
    /*
     * Sonido dedicado para los recordatorios/notificaciones. Se mantiene
     * separado de los efectos cortos de la interfaz para que el usuario pueda
     * elegir un tono y volumen propios sin alterar taps, menús o adjuntos.
     */
    val reminderSoundEnabled: Boolean = true,
    val reminderSoundVolume: Float = 75f,
    val reminderRingtone: String = "classic",
    /*
     * Respuesta háptica independiente del sonido. El estilo cambia el patrón
     * de vibración y la intensidad se guarda como porcentaje.
     */
    val hapticEffectsEnabled: Boolean = true,
    val hapticEffectsIntensity: Float = 55f,
    val hapticEffectsStyle: String = "soft",
    val language: String = "system",
    val gridColumns: Int = 2,
    val sortOrder: String = "newest",
    // Personalización extrema
    val profileImageUri: String = "", val profileImageSize: Float = 46f,
    val iconStyle: String = "rounded", val iconSize: Float = 22f,
    val accentColor: String = "palette",
    val noteCardCornerRadius: Float = 18f, val noteCardElevation: Float = 1.5f, val noteCardPadding: Float = 12f,
    val noteCardImageHeight: Float = 112f,
    val noteCardOutlineEnabled: Boolean = false,
    val noteCardOutlineWidth: Float = 0f,
    // 4 líneas por defecto para evitar cortar títulos como antes.
    val noteTitleMaxLines: Int = 4, val noteContentMaxLines: Int = 6, val noteLineSpacing: Float = 1.20f,
    val showNoteDate: Boolean = true, val showCategoryChip: Boolean = true, val showFavoriteIcon: Boolean = true,
    val fabSize: Float = 58f,
    /*
     * Personalización del menú ⋮ de cada nota.
     *
     * optionMenuOrder usa claves separadas por comas y permite
     * cambiar el orden sin migraciones complejas de DataStore.
     * optionMenuHiddenItems contiene las acciones ocultas.
     */
    val optionMenuOrder: String = "edit,favorite,pin,priority,color,move,delete",
    val optionMenuHiddenItems: String = "",
    val optionMenuShowIcons: Boolean = true,
    /*
     * note  = usa el color configurado para notas
     * black = fuerza negro
     * white = fuerza blanco
     */
    val optionMenuTextColor: String = "note",
    /*
     * Opacidad del fondo del popup, entre 35% y 100%.
     */
    val optionMenuOpacity: Float = 100f,
    /*
     * Elementos ocultos en los submenús.
     */
    val priorityMenuHiddenItems: String = "",
    val colorMenuHiddenItems: String = "",
    /*
     * Perfil global de rendimiento:
     * performance = prioriza fluidez y menor consumo de RAM/CPU
     * balanced    = equilibrio entre calidad y rendimiento
     * quality     = máxima fidelidad visual
     */
    val performanceMode: String = "balanced",
    /*
     * Animaciones ligeras sincronizadas al frame clock de Compose.
     */
    val animationsEnabled: Boolean = true,
    /*
     * Preset de transición entre pantallas.
     */
    val animationStyle: String = "zoom",
    /*
     * Curva de aceleración de las transiciones.
     */
    val animationEasing: String = "standard",
    /*
     * 0.5x = más lenta
     * 1.0x = normal
     * 2.0x = más rápida
     */
    val animationSpeed: Float = 1f,
    /*
     * 0.5x = movimiento discreto
     * 1.0x = normal
     * 1.5x = movimiento más marcado
     */
    val animationIntensity: Float = 1f)
