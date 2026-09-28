# WidgetLocale.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/widget/WidgetLocale.kt`  
**SHA-256:** `b1ad100c06b36528e9ef782ebe2130783ff012838223e0b1e43a66a675fc09ba`  
**Líneas:** 50  
**Package:** `com.example.mynotes.widget`

## 1. Para qué existe este archivo

Crea Context localizado con el idioma elegido dentro de MyNotes para que RemoteViews no dependa del idioma del launcher.

## 2. Tipos/clases declarados

- Línea **17** — `object WidgetLocale`.

## 3. Estado, constantes y valores importantes

- **`LOCALE_PREFS`** (línea 18) inicia con `"locale_prefs"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`LANGUAGE_KEY`** (línea 19) inicia con `"language"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`SYSTEM_LANGUAGE`** (línea 20) inicia con `"system"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`language`** (línea 29) inicia con `selectedLanguage(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`locale`** (línea 31) inicia con `Locale.forLanguageTag(language`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`configuration`** (línea 33) inicia con `Configuration(context.resources.configuration`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`localized`** (línea 41) inicia con `localizedContext(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `selectedLanguage` — líneas 22–22

**Firma:** `fun selectedLanguage(context: Context): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`

**Salida:** String.

### `localizedContext` — líneas 28–38

**Firma:** `fun localizedContext(context: Context): Context`

Crea ConfigurationContext con Locale de MyNotes para resolver strings del widget en el idioma de la app aunque launcher/sistema use otro.

**Entradas:**
- `context: Context`

**Salida:** Context.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `selectedLanguage`, `forLanguageTag`, `Configuration`, `setLocale`, `setLayoutDirection`, `createConfigurationContext`.

### `locale` — líneas 40–48

**Firma:** `fun locale(context: Context): Locale`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`

**Salida:** Locale.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `localizedContext`, `Suppress`.

## 5. Cómo se conecta con el resto de MyNotes

- No importa directamente otro componente `com.example.mynotes`; funciona como modelo/utilidad base o mediante APIs Android/Jetpack.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Lee o escribe SharedPreferences.
- Opera con RemoteViews/AppWidget fuera de Compose.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `selectedLanguage` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
2. `localizedContext` — Crea ConfigurationContext con Locale de MyNotes para resolver strings del widget en el idioma de la app aunque launcher/sistema use otro.
3. `locale` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

## 9. Qué no debe romperse al modificarlo

- RemoteViews tiene restricciones, especialmente en Samsung/API 28; probar el widget en launcher real.

## 10. Resumen en lenguaje sencillo

En términos simples: Crea Context localizado con el idioma elegido dentro de MyNotes para que RemoteViews no dependa del idioma del launcher. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
