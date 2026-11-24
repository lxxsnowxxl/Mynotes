package com.example.mynotes.settings

/** Catálogo actual y compatibilidad con preferencias y copias de versiones anteriores. */
internal object MotionStylePolicy {
    const val DEFAULT_STYLE = "cross_dissolve"

    val supportedStyles: Set<String> = linkedSetOf(
        "cross_dissolve", "lift_card", "lateral_stack", "depth_pull",
        "diagonal_flow", "corner_unfold", "focus_swap", "random"
    )
    val concreteStyles: List<String> = supportedStyles.filterNot { it == "random" }

    private val legacyStyles = mapOf(
        "zoom" to "lift_card", "zoom_fade" to "lift_card", "fade" to "cross_dissolve",
        "slide_left" to "lateral_stack", "slide_right" to "lateral_stack",
        "slide_up" to "lift_card", "slide_down" to "lift_card",
        "slide_zoom_left" to "lateral_stack", "slide_zoom_up" to "lift_card",
        "axis_x" to "lateral_stack", "axis_y" to "lift_card", "axis_z" to "depth_pull",
        "expand" to "corner_unfold", "expand_horizontal" to "corner_unfold",
        "expand_vertical" to "corner_unfold", "bounce" to "lift_card", "elastic" to "lift_card",
        "pop" to "depth_pull", "subtle" to "cross_dissolve", "expressive_spring" to "lift_card",
        "container_transform" to "corner_unfold", "soft_reveal" to "focus_swap",
        "elastic_slide" to "diagonal_flow", "predictive" to "lateral_stack", "tonal_pop" to "depth_pull"
    )

    fun normalize(value: String): String =
        if (value in supportedStyles) value else legacyStyles[value] ?: DEFAULT_STYLE
}
