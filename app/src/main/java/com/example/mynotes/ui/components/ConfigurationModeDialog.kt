package com.example.mynotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.adaptiveUiButtonContainer
import com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors
import com.example.mynotes.ui.theme.automaticUiTextColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.softenUiColorToContrast
import androidx.compose.ui.res.stringResource

/**
 * Selector obligatorio mostrado una sola vez mientras configurationMode == "unset".
 *
 * Tocar Básica o Avanzada solo cambia una selección temporal local. El modo no se
 * persiste hasta pulsar el botón de confirmación (Aceptar/OK). Así se evita cambiar
 * de edición accidentalmente al tocar uno de los recuadros.
 *
 * No existen dos APK ni dos ediciones de MyNotes: el modo únicamente decide cuántos
 * controles se muestran en Configuración. Elegir el modo básico no elimina ni
 * restablece preferencias avanzadas; simplemente oculta sus controles.
 */
@Composable
fun ConfigurationModeDialog(
    fontFamily: FontFamily,
    onBasicSelected: () -> Unit,
    onAdvancedSelected: () -> Unit
) {
    val context = LocalContext.current
    var pendingMode by remember { mutableStateOf<String?>(null) }

    val dialogBackground = MaterialTheme.colorScheme.surfaceContainerHigh
    val primaryColor = MaterialTheme.colorScheme.primary
    val unselectedBaseColor = MaterialTheme.colorScheme.surfaceContainer
    val dialogContentColor = remember(dialogBackground) {
        automaticUiTextColor(dialogBackground)
    }
    val dialogSecondaryContentColor = remember(dialogContentColor, dialogBackground) {
        softenUiColorToContrast(
            foreground = dialogContentColor,
            background = dialogBackground,
            minimumContrast = 4.5f,
            maximumSoftening = 0.45f
        )
    }
    val selectedButtonColors = remember(primaryColor, dialogBackground) {
        resolveAdaptiveUiButtonColors(
            preferred = primaryColor,
            background = dialogBackground,
            textColorMode = "auto",
            minimumContentContrast = 4.5f,
            minimumSurfaceContrast = 1.85f
        )
    }
    val selectedContainerColor = selectedButtonColors.container
    val selectedContentColor = selectedButtonColors.content
    val unselectedButtonColors = remember(unselectedBaseColor, dialogBackground) {
        resolveAdaptiveUiButtonColors(
            preferred = unselectedBaseColor,
            background = dialogBackground,
            textColorMode = "auto",
            minimumContentContrast = 4.5f,
            minimumSurfaceContrast = 1.35f
        )
    }
    val unselectedContainerColor = unselectedButtonColors.container
    val unselectedContentColor = unselectedButtonColors.content
    val unselectedBorderColor = remember(unselectedContentColor, unselectedContainerColor) {
        ensureUiContrast(
            preferred = unselectedContentColor,
            background = unselectedContainerColor,
            minimumContrast = 3f
        )
    }

    /*
     * No usamos androidx.compose.ui.window.Dialog aquí. Un Dialog crea una
     * segunda Window y varios launchers Samsung vuelven a mostrar la barra
     * de navegación durante ese cambio de ventana. Esta capa vive dentro de
     * la misma Window de MainActivity, por lo que conserva el modo inmersivo
     * desde el primer frame y evita el destello de los tres botones Android.
     */
    BackHandler(enabled = true) { /* La primera elección es obligatoria. */ }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.56f))
            .pointerInput(Unit) {
                detectTapGestures { /* Consumir toques fuera del panel. */ }
            }
            .padding(horizontal = 18.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp),
            shape = RoundedCornerShape(26.dp),
            color = dialogBackground,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.configuration_mode_welcome_title),
                    color = dialogContentColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = stringResource(R.string.configuration_mode_welcome_description),
                    modifier = Modifier.padding(top = 6.dp),
                    color = dialogSecondaryContentColor,
                    fontFamily = fontFamily,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(18.dp))

                ConfigurationModeChoiceButton(
                    selected = pendingMode == "basic",
                    title = stringResource(R.string.configuration_mode_basic),
                    description = stringResource(R.string.configuration_mode_basic_description),
                    fontFamily = fontFamily,
                    selectedContentColor = selectedContentColor,
                    unselectedContentColor = unselectedContentColor,
                    selectedContainerColor = selectedContainerColor,
                    unselectedContainerColor = unselectedContainerColor,
                    unselectedBorderColor = unselectedBorderColor,
                    onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                        pendingMode = "basic"
                    }
                )

                Spacer(Modifier.height(10.dp))

                ConfigurationModeChoiceButton(
                    selected = pendingMode == "advanced",
                    title = stringResource(R.string.configuration_mode_advanced),
                    description = stringResource(R.string.configuration_mode_advanced_description),
                    fontFamily = fontFamily,
                    selectedContentColor = selectedContentColor,
                    unselectedContentColor = unselectedContentColor,
                    selectedContainerColor = selectedContainerColor,
                    unselectedContainerColor = unselectedContainerColor,
                    unselectedBorderColor = unselectedBorderColor,
                    onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                        pendingMode = "advanced"
                    }
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Confirm)
                        when (pendingMode) {
                            "basic" -> onBasicSelected()
                            "advanced" -> onAdvancedSelected()
                        }
                    },
                    enabled = pendingMode != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedContainerColor,
                        contentColor = selectedContentColor,
                        disabledContainerColor = unselectedContainerColor,
                        disabledContentColor = unselectedContentColor.copy(alpha = 0.62f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.configuration_mode_confirm),
                        color = if (pendingMode != null) selectedContentColor else unselectedContentColor.copy(alpha = 0.62f),
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = stringResource(R.string.configuration_mode_change_later),
                    modifier = Modifier.padding(top = 14.dp),
                    color = dialogSecondaryContentColor,
                    fontFamily = fontFamily,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun ConfigurationModeChoiceButton(
    selected: Boolean,
    title: String,
    description: String,
    fontFamily: FontFamily,
    selectedContentColor: androidx.compose.ui.graphics.Color,
    unselectedContentColor: androidx.compose.ui.graphics.Color,
    selectedContainerColor: androidx.compose.ui.graphics.Color,
    unselectedContainerColor: androidx.compose.ui.graphics.Color,
    unselectedBorderColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    val contentColor = if (selected) selectedContentColor else unselectedContentColor
    val content: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(
                text = title,
                color = contentColor,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = contentColor,
                fontFamily = fontFamily,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }

    if (selected) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedContainerColor,
                contentColor = contentColor
            )
        ) {
            content()
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(1.dp, unselectedBorderColor),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = unselectedContainerColor,
                contentColor = contentColor
            )
        ) {
            content()
        }
    }
}

