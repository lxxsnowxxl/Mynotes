# AppSettings.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/settings/AppSettings.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `dc653f621bf3bda12c8371d76bc80cb52eb434c19b45425de23a7a3a53eb7078`  
**Líneas del código real:** 148

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Modelo inmutable que reúne los ajustes configurables de la aplicación y sus valores predeterminados.

**Arquitectura.** Es el estado de configuración que observa la UI. SettingsRepository lo produce desde DataStore y SettingsViewModel lo expone a Compose.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.settings`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **1 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Jetpack/Compose:** `androidx.compose.runtime.Immutable`.

## 3. Restricciones e invariantes visibles en el archivo

- No se detectaron automáticamente operadores de restricción comunes; las restricciones específicas siguen documentadas dentro de cada declaración.

## 4. Bloques de código, uno por uno

### 4.1 `AppSettings` — class, líneas 6–6

```kotlin
data class AppSettings(val darkMode: Boolean = false,
```

#### Qué hace y por qué existe

Modelo inmutable que reúne los ajustes configurables de la aplicación y sus valores predeterminados.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 11 | `backgroundColor` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 16 | `backgroundToneIndex` | `val` | `Int` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int`. No declara nulabilidad explícita. |
| 17 | `backgroundIntensity` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 24 | `settingsPanelTone` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 32 | `surfacePanelIntensity` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 33 | `headerIntensity` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 42 | `textColor` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 47 | `textOutlineEnabled` | `val` | `Boolean` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Boolean`. No declara nulabilidad explícita. |
| 54 | `noteUiTextColor` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 58 | `sliderStyle` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 59 | `font` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 60 | `fontSize` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 66 | `soundEffectsEnabled` | `val` | `Boolean` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Boolean`. No declara nulabilidad explícita. |
| 67 | `soundEffectsVolume` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 72 | `soundEffectsTheme` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 77 | `hapticEffectsEnabled` | `val` | `Boolean` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Boolean`. No declara nulabilidad explícita. |
| 78 | `hapticEffectsIntensity` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 79 | `hapticEffectsStyle` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 80 | `language` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 81 | `gridColumns` | `val` | `Int` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int`. No declara nulabilidad explícita. |
| 82 | `sortOrder` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 84 | `profileImageUri` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 85 | `iconStyle` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 86 | `accentColor` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 87 | `noteCardCornerRadius` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 88 | `noteCardImageHeight` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 90 | `noteTitleMaxLines` | `val` | `Int` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int`. No declara nulabilidad explícita. |
| 91 | `showNoteDate` | `val` | `Boolean` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Boolean`. No declara nulabilidad explícita. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 92 | `fabSize` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 100 | `optionMenuOrder` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 101 | `optionMenuHiddenItems` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 102 | `optionMenuShowIcons` | `val` | `Boolean` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Boolean`. No declara nulabilidad explícita. |
| 108 | `optionMenuTextColor` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 112 | `optionMenuOpacity` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 116 | `priorityMenuHiddenItems` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 117 | `colorMenuHiddenItems` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 124 | `performanceMode` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 128 | `animationsEnabled` | `val` | `Boolean` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Boolean`. No declara nulabilidad explícita. |
| 132 | `animationStyle` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 136 | `animationEasing` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 142 | `animationSpeed` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 148 | `animationIntensity` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

No hay bloques con llaves estructurales en este archivo.

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
