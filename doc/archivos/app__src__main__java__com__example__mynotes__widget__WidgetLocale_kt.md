# WidgetLocale.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/widget/WidgetLocale.kt`  **SHA-256:** `b1ad100c06b36528e9ef782ebe2130783ff012838223e0b1e43a66a675fc09ba`  **Líneas:** 49 · **Bytes:** 1848 · **Imports:** 3 · **Declaraciones detectadas:** 4
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Contexto localizado para que widgets sigan el idioma configurado en MyNotes.
## 2. Package e imports

Package declarado: `com.example.mynotes.widget`.

### Android / Jetpack / Compose

`android.content.Context`, `android.content.res.Configuration`

### Kotlin / Coroutines / Java

`java.util.Locale`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 17 | `object` | `WidgetLocale` | `object WidgetLocale {` |
| 21 | `fun` | `selectedLanguage` | `` |
| 27 | `fun` | `localizedContext` | `` |
| 39 | `fun` | `locale` | `` |

## 4. Estado, efectos y límites observables

- **RemoteViews/widgets:** 2 aparición/apariciones.
- **safe calls:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Probar en launcher real/API 28: RemoteViews tiene restricciones distintas a Compose y no admite todos los tintes/Views.
