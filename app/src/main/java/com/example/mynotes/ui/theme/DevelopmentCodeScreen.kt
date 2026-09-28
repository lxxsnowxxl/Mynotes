package com.example.mynotes.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiTextColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val CODE_BLOCK_STEP = 160

/**
 * Documentación técnica integrada de MyNotes.
 *
 * V206 documenta todos los archivos Kotlin de la aplicación más los archivos
 * esenciales de build/manifiesto. La fuente se empaqueta como assets de texto
 * y se carga únicamente al expandir una tarjeta, en bloques de 160 líneas,
 * para no convertir esta pantalla en un coste permanente de memoria/composición.
 */
@Composable
fun DevelopmentCodeScreen(settings: AppSettings, onBack: () -> Unit) {
    val listState = rememberLazyListState()
    val background = MaterialTheme.colorScheme.background
    val primary = resolveUiTextColor(settings.textColor, background)
        val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    var query by rememberSaveable { mutableStateOf("") }
    var expandedPath by rememberSaveable { mutableStateOf<String?>(null) }
    val filtered = remember(query) {
        val needle = query.trim()
        if (needle.isEmpty()) DevelopmentCodeCatalog.entries else DevelopmentCodeCatalog.entries.filter {
            it.path.contains(needle, true) || it.title.contains(needle, true) || it.category.contains(needle, true) ||
                it.purpose.contains(needle, true) || it.whereInApp.contains(needle, true) || it.keySymbols.any { symbol -> symbol.contains(needle, true) }
        }
    }
    val grouped = remember(filtered) { filtered.groupBy { it.category } }

    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled, animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = background, topBar = { DevelopmentTopBar("Código de desarrollo", fontFamily, primary, onBack) }) { paddingValues ->
            Box(Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(paddingValues).widthIn(max = 980.dp).padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        DevelopmentCodeIntro(settings, DevelopmentCodeCatalog.entries.size, DevelopmentCodeCatalog.kotlinFileCount,
                            DevelopmentCodeCatalog.resourceFileCount)
                    }
                    item {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            label = { Text("Buscar archivo, función o área", fontFamily = fontFamily) }
                        )
                    }
                    grouped.forEach { (category, entries) ->
                        item(key = "header:$category") {
                            Text(
                                text = "$category · ${entries.size} archivo${if (entries.size == 1) "" else "s"}",
                                color = primary,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                            )
                        }
                        items(entries, key = { it.path }) { entry ->
                            DevelopmentFileCard(
                                entry = entry,
                                settings = settings,
                                expanded = expandedPath == entry.path,
                                onToggle = { expandedPath = if (expandedPath == entry.path) null else entry.path }
                            )
                        }
                    }
                    item { Spacer(Modifier.height(28.dp)) }
                }
                ScrollPositionCapsule(
                    state = listState,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(paddingValues),
                    backgroundColor = background,
                    preferredColor = primary
                )
            }
        }
    }
}

@Composable
private fun DevelopmentCodeIntro(settings: AppSettings, total: Int, kotlinCount: Int, resourceCount: Int) {
    DevelopmentSection(title = "Catálogo completo del proyecto", settings = settings) {
        DevelopmentParagraph(
            text = "Esta versión ya no muestra doce ejemplos aislados. El catálogo contiene $kotlinCount archivos Kotlin reales y los archivos " +
                "esenciales de construcción/configuración, para un total de $total entradas documentadas. Además enumera $resourceCount archivos de recursos " +
                "Android por familias. Cada archivo Kotlin se acompaña de responsabilidad, ubicación visible, flujo interno, símbolos clave, dependencias internas " +
                "y su fuente real. El visor comienza con 160 líneas y permite cargar otras 160 tantas veces como sea necesario hasta llegar al archivo completo; " +
                "por eso ya no se limita a unas cuantas líneas de ejemplo.",
            settings = settings
        )
        Spacer(Modifier.height(8.dp))
        DevelopmentParagraph(
            text = "Los snapshots se cargan sólo cuando expandes una tarjeta. Mantener una sola tarjeta expandida a la vez y paginar el código evita que archivos " +
                "grandes como PdfEditorActivity.kt o LinkPreviewCard.kt creen miles de líneas de Text durante la apertura normal de la pantalla.",
            settings = settings
        )
    }
}

@Composable
private fun DevelopmentFileCard(entry: DevelopmentFileDoc, settings: AppSettings, expanded: Boolean, onToggle: () -> Unit) {
    val container = MaterialTheme.colorScheme.surfaceContainerLow
    val primary = resolveUiTextColor(settings.textColor, container)
    val secondary = resolveSecondaryUiTextColor(settings.textColor, container)
    val border = ensureUiContrast(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f), container, 2.2f)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = container,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, border),
        tonalElevation = 0.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle)
            ) {
                Icon(Icons.Default.Code, contentDescription = null, tint = primary, modifier = Modifier.size(20.dp))
                Column(Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(entry.title, color = primary, fontFamily = appFontFamily(settings.font), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        entry.path,
                        color = secondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = secondary)
            }
            Text(
                text = entry.purpose,
                color = secondary,
                fontFamily = appFontFamily(settings.font),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "${entry.lineCount} líneas · ${entry.keySymbols.take(3).joinToString(" · ").ifBlank { "archivo de configuración/recursos" }}",
                color = secondary,
                fontFamily = appFontFamily(settings.font),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 7.dp)
            )
            AnimatedVisibility(visible = expanded) {
                DevelopmentFileDetails(entry = entry, settings = settings)
            }
        }
    }
}

@Composable
private fun DevelopmentFileDetails(entry: DevelopmentFileDoc, settings: AppSettings) {
    val context = LocalContext.current
    var code by remember(entry.assetName) { mutableStateOf<String?>(null) }
    var visibleLines by remember(entry.path) { mutableIntStateOf(CODE_BLOCK_STEP) }
    LaunchedEffect(entry.assetName) {
        code = withContext(Dispatchers.IO) { context.readDevelopmentAsset(entry.assetName) }
        visibleLines = CODE_BLOCK_STEP
    }
    val fullCode = code
    val shownCode = remember(fullCode, visibleLines) {
        fullCode?.lineSequence()?.take(visibleLines)?.joinToString("\n").orEmpty()
    }
    val totalLines = remember(fullCode, entry.lineCount) { fullCode?.lineSequence()?.count() ?: entry.lineCount }
    val hasMore = fullCode != null && visibleLines < totalLines

    Column(Modifier.fillMaxWidth().padding(top = 14.dp)) {
        DevelopmentDetailLabel("Dónde se encuentra dentro de la app", settings)
        DevelopmentParagraph(entry.whereInApp, settings)
        Spacer(Modifier.height(10.dp))
        DevelopmentDetailLabel("Funcionamiento interno", settings)
        DevelopmentParagraph(entry.details, settings)
        Spacer(Modifier.height(10.dp))
        DevelopmentDetailLabel("Símbolos principales", settings)
        DevelopmentParagraph(entry.keySymbols.joinToString(" · ").ifBlank { "Este archivo no declara símbolos Kotlin; contiene configuración declarativa." }, settings)
        if (entry.internalDependencies.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            DevelopmentDetailLabel("Conexiones con otros archivos de MyNotes", settings)
            DevelopmentParagraph(entry.internalDependencies.joinToString("\n"), settings)
        }
        Spacer(Modifier.height(12.dp))
        DevelopmentDetailLabel("Código fuente real", settings)
        Text(
            text = if (fullCode == null) "Cargando snapshot…" else "Mostrando ${minOf(visibleLines, totalLines)} de $totalLines líneas. El texto es seleccionable.",
            color = resolveSecondaryUiTextColor(settings.textColor, MaterialTheme.colorScheme.surfaceContainerLow),
            fontFamily = appFontFamily(settings.font),
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        if (shownCode.isNotEmpty()) DevelopmentCodeBlock(shownCode, settings)
        if (hasMore) {
            TextButton(
                onClick = { visibleLines = (visibleLines + CODE_BLOCK_STEP).coerceAtMost(totalLines) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = resolveUiTextColor(settings.textColor, MaterialTheme.colorScheme.surfaceContainerLow))
            ) {
                Text("Cargar ${minOf(CODE_BLOCK_STEP, totalLines - visibleLines)} líneas más", fontFamily = appFontFamily(settings.font), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun Context.readDevelopmentAsset(assetName: String): String = runCatching {
    assets.open("development_code/$assetName").bufferedReader(Charsets.UTF_8).use { it.readText() }
}.getOrElse { "No se pudo abrir el snapshot empaquetado: ${it.message.orEmpty()}" }

@Composable
private fun DevelopmentDetailLabel(text: String, settings: AppSettings) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    Text(
        text = text,
        color = resolveUiTextColor(settings.textColor, background),
        fontFamily = appFontFamily(settings.font),
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun DevelopmentCodeBlock(code: String, settings: AppSettings) {
    val container = MaterialTheme.colorScheme.surfaceContainerHighest
    val codeColor = resolveUiTextColor(settings.textColor, container)
    Surface(modifier = Modifier.fillMaxWidth(), color = container, shape = RoundedCornerShape(12.dp), tonalElevation = 0.dp) {
        SelectionContainer {
            Column(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = code,
                    color = codeColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    softWrap = false
                )
            }
        }
    }
}
