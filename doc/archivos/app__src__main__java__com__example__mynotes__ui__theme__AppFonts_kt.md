# AppFonts.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/AppFonts.kt`  
**SHA-256:** `3b9a9b91cd033607fd9ba69a16e099cdc244233374c1471e1d9bed151359c724`  
**Líneas:** 52  
**Package:** `com.example.mynotes.ui.theme`

## 1. Para qué existe este archivo

Mapea claves de fuente guardadas en ajustes a FontFamily utilizadas por Compose.

## 2. Tipos/clases declarados

- No declara una clase/objeto propio; contiene funciones/valores de soporte o es un archivo marcador.

## 3. Estado, constantes y valores importantes

- **`GoogleSansFontFamily`** (línea 13) inicia con `FontFamily(Font(resId = R.font.google_sans_regular`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`GoogleSansRegularFontFamily`** (línea 23) inicia con `FontFamily(Font(R.font.google_sans_regular`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`GoogleSansMediumFontFamily`** (línea 24) inicia con `FontFamily(Font(R.font.google_sans_medium`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`GoogleSansBoldFontFamily`** (línea 26) inicia con `FontFamily(Font(R.font.google_sans_bold`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`GoogleSansItalicFontFamily`** (línea 28) inicia con `FontFamily(Font(R.font.google_sans_italic`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`GoogleSansMediumItalicFontFamily`** (línea 30) inicia con `FontFamily(Font(R.font.google_sans_medium_italic`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`GoogleSansBoldItalicFontFamily`** (línea 32) inicia con `FontFamily(Font(R.font.google_sans_bold_italic`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`GoogleSansFlexFontFamily`** (línea 34) inicia con `FontFamily(Font(R.font.google_sans_flex`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `appFontFamily` — líneas 37–51

**Firma:** `fun appFontFamily(key: String): FontFamily`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `key: String`

**Salida:** FontFamily.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.R`.

## 6. Recursos Android que utiliza

- `R.font`: `google_sans_bold`, `google_sans_bold_italic`, `google_sans_flex`, `google_sans_italic`, `google_sans_medium`, `google_sans_medium_italic`, `google_sans_regular`

## 7. Tecnologías y efectos relevantes

- Principalmente lógica Kotlin/Compose sin I/O especial detectado por estas reglas.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `appFontFamily` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

## 9. Qué no debe romperse al modificarlo

- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.

## 10. Resumen en lenguaje sencillo

En términos simples: Mapea claves de fuente guardadas en ajustes a FontFamily utilizadas por Compose. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
