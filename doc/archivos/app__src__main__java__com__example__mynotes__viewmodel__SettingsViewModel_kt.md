# SettingsViewModel.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/viewmodel/SettingsViewModel.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `b43e0de71ed0b759a2e74c6a6a04e5a73461ed831376da6f708c125895cfab70`  
**Líneas del código real:** 280

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

ViewModel de configuración que expone AppSettings como StateFlow y ofrece métodos de escritura de alto nivel para cada opción visible en Configuración.

**Arquitectura.** Es la capa de adaptación entre Compose y SettingsRepository: los composables invocan setters simples y el ViewModel ejecuta las escrituras dentro de viewModelScope.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.viewmodel`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **9 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.app.Application`.

**Jetpack/Compose:** `androidx.lifecycle.AndroidViewModel`, `androidx.lifecycle.viewModelScope`.

**Proyecto MyNotes:** `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.settings.SettingsRepository`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.flow.SharingStarted`, `kotlinx.coroutines.flow.StateFlow`, `kotlinx.coroutines.flow.stateIn`, `kotlinx.coroutines.launch`.

## 3. Restricciones e invariantes visibles en el archivo

- No se detectaron automáticamente operadores de restricción comunes; las restricciones específicas siguen documentadas dentro de cada declaración.

## 4. Bloques de código, uno por uno

### 4.1 `SettingsViewModel` — class, líneas 13–280

```kotlin
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `application: Application` — `application` recibe un valor de tipo `Application`. El contrato no marca este parámetro como anulable.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 14 | `val repository` | `inferido` | `SettingsRepository(application)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Referencia una capa de repositorio, separando la coordinación de UI/estado del acceso y persistencia de datos. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `AndroidViewModel`, `SettingsRepository`, `repository.settings.stateIn`, `SharingStarted.WhileSubscribed`, `AppSettings`, `repository.setDarkMode`, `repository.setBackgroundColor`, `repository.setBackgroundToneIndex`, `repository.setBackgroundIntensity`, `repository.setSettingsPanelTone`, `repository.setSurfacePanelIntensity`, `repository.setHeaderIntensity`, `repository.setTextColor`, `repository.setTextOutlineEnabled`, `repository.setNoteUiTextColor`, `repository.setSliderStyle`, `repository.setFont`, `repository.setFontSize`, `repository.setSoundEffectsEnabled`, `repository.setSoundEffectsVolume`, `repository.setSoundEffectsTheme`, `repository.setHapticEffectsEnabled`, `repository.setHapticEffectsIntensity`, `repository.setHapticEffectsStyle`, `repository.setLanguage`, `repository.setGridColumns`, `repository.setSortOrder`, `repository.setProfileImageUri`, `repository.setProfileImageSize`, `repository.setIconStyle`, `repository.setIconSize`, `repository.setAccentColor`, `repository.setNoteCardCornerRadius`, `repository.setNoteCardElevation`, `repository.setNoteCardPadding`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `setDarkMode` — fun, líneas 20–24

```kotlin
    fun setDarkMode(value: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setDarkMode`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `setBackgroundColor` — fun, líneas 25–29

```kotlin
    fun setBackgroundColor(value: String) {
        viewModelScope.launch {
            repository.setBackgroundColor(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setBackgroundColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `setBackgroundToneIndex` — fun, líneas 30–34

```kotlin
    fun setBackgroundToneIndex(value: Int) {
        viewModelScope.launch {
            repository.setBackgroundToneIndex(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Int` — `value` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setBackgroundToneIndex`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `setBackgroundIntensity` — fun, líneas 35–39

```kotlin
    fun setBackgroundIntensity(value: Float) {
        viewModelScope.launch {
            repository.setBackgroundIntensity(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setBackgroundIntensity`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `setSettingsPanelTone` — fun, líneas 40–44

```kotlin
    fun setSettingsPanelTone(value: Float) {
        viewModelScope.launch {
            repository.setSettingsPanelTone(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setSettingsPanelTone`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `setSurfacePanelIntensity` — fun, líneas 45–49

```kotlin
    fun setSurfacePanelIntensity(value: Float) {
        viewModelScope.launch {
            repository.setSurfacePanelIntensity(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setSurfacePanelIntensity`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `setHeaderIntensity` — fun, líneas 50–54

```kotlin
    fun setHeaderIntensity(value: Float) {
        viewModelScope.launch {
            repository.setHeaderIntensity(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setHeaderIntensity`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.9 `setTextColor` — fun, líneas 55–59

```kotlin
    fun setTextColor(value: String) {
        viewModelScope.launch {
            repository.setTextColor(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setTextColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.10 `setTextOutlineEnabled` — fun, líneas 60–64

```kotlin
    fun setTextOutlineEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setTextOutlineEnabled(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setTextOutlineEnabled`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.11 `setNoteUiTextColor` — fun, líneas 65–69

```kotlin
    fun setNoteUiTextColor(value: String) {
        viewModelScope.launch {
            repository.setNoteUiTextColor(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setNoteUiTextColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.12 `setSliderStyle` — fun, líneas 70–74

```kotlin
    fun setSliderStyle(value: String) {
        viewModelScope.launch {
            repository.setSliderStyle(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setSliderStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.13 `setFont` — fun, líneas 75–79

```kotlin
    fun setFont(value: String) {
        viewModelScope.launch {
            repository.setFont(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setFont`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.14 `setFontSize` — fun, líneas 80–84

```kotlin
    fun setFontSize(value: Float) {
        viewModelScope.launch {
            repository.setFontSize(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setFontSize`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.15 `setSoundEffectsEnabled` — fun, líneas 85–89

```kotlin
    fun setSoundEffectsEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setSoundEffectsEnabled(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setSoundEffectsEnabled`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.16 `setSoundEffectsVolume` — fun, líneas 90–94

```kotlin
    fun setSoundEffectsVolume(value: Float) {
        viewModelScope.launch {
            repository.setSoundEffectsVolume(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setSoundEffectsVolume`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.17 `setSoundEffectsTheme` — fun, líneas 95–99

```kotlin
    fun setSoundEffectsTheme(value: String) {
        viewModelScope.launch {
            repository.setSoundEffectsTheme(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setSoundEffectsTheme`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.18 `setHapticEffectsEnabled` — fun, líneas 100–104

```kotlin
    fun setHapticEffectsEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setHapticEffectsEnabled(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setHapticEffectsEnabled`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.19 `setHapticEffectsIntensity` — fun, líneas 105–109

```kotlin
    fun setHapticEffectsIntensity(value: Float) {
        viewModelScope.launch {
            repository.setHapticEffectsIntensity(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setHapticEffectsIntensity`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.20 `setHapticEffectsStyle` — fun, líneas 110–114

```kotlin
    fun setHapticEffectsStyle(value: String) {
        viewModelScope.launch {
            repository.setHapticEffectsStyle(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setHapticEffectsStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.21 `setLanguage` — fun, líneas 115–119

```kotlin
    fun setLanguage(value: String) {
        viewModelScope.launch {
            repository.setLanguage(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setLanguage`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.22 `setGridColumns` — fun, líneas 120–124

```kotlin
    fun setGridColumns(value: Int) {
        viewModelScope.launch {
            repository.setGridColumns(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Int` — `value` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setGridColumns`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.23 `setSortOrder` — fun, líneas 125–129

```kotlin
    fun setSortOrder(value: String) {
        viewModelScope.launch {
            repository.setSortOrder(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setSortOrder`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.24 `setProfileImageUri` — fun, líneas 130–134

```kotlin
    fun setProfileImageUri(value: String) {
        viewModelScope.launch {
            repository.setProfileImageUri(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setProfileImageUri`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.25 `setProfileImageSize` — fun, líneas 135–139

```kotlin
    fun setProfileImageSize(value: Float) {
        viewModelScope.launch {
            repository.setProfileImageSize(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setProfileImageSize`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.26 `setIconStyle` — fun, líneas 140–144

```kotlin
    fun setIconStyle(value: String) {
        viewModelScope.launch {
            repository.setIconStyle(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setIconStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.27 `setIconSize` — fun, líneas 145–149

```kotlin
    fun setIconSize(value: Float) {
        viewModelScope.launch {
            repository.setIconSize(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setIconSize`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.28 `setAccentColor` — fun, líneas 150–154

```kotlin
    fun setAccentColor(value: String) {
        viewModelScope.launch {
            repository.setAccentColor(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setAccentColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.29 `setNoteCardCornerRadius` — fun, líneas 155–159

```kotlin
    fun setNoteCardCornerRadius(value: Float) {
        viewModelScope.launch {
            repository.setNoteCardCornerRadius(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setNoteCardCornerRadius`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.30 `setNoteCardElevation` — fun, líneas 160–164

```kotlin
    fun setNoteCardElevation(value: Float) {
        viewModelScope.launch {
            repository.setNoteCardElevation(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setNoteCardElevation`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.31 `setNoteCardPadding` — fun, líneas 165–169

```kotlin
    fun setNoteCardPadding(value: Float) {
        viewModelScope.launch {
            repository.setNoteCardPadding(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setNoteCardPadding`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.32 `setNoteCardImageHeight` — fun, líneas 170–174

```kotlin
    fun setNoteCardImageHeight(value: Float) {
        viewModelScope.launch {
            repository.setNoteCardImageHeight(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setNoteCardImageHeight`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.33 `setNoteTitleMaxLines` — fun, líneas 175–179

```kotlin
    fun setNoteTitleMaxLines(value: Int) {
        viewModelScope.launch {
            repository.setNoteTitleMaxLines(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Int` — `value` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setNoteTitleMaxLines`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.34 `setNoteContentMaxLines` — fun, líneas 180–184

```kotlin
    fun setNoteContentMaxLines(value: Int) {
        viewModelScope.launch {
            repository.setNoteContentMaxLines(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Int` — `value` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setNoteContentMaxLines`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.35 `setNoteLineSpacing` — fun, líneas 185–189

```kotlin
    fun setNoteLineSpacing(value: Float) {
        viewModelScope.launch {
            repository.setNoteLineSpacing(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setNoteLineSpacing`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.36 `setShowNoteDate` — fun, líneas 190–194

```kotlin
    fun setShowNoteDate(value: Boolean) {
        viewModelScope.launch {
            repository.setShowNoteDate(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setShowNoteDate`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.37 `setShowCategoryChip` — fun, líneas 195–199

```kotlin
    fun setShowCategoryChip(value: Boolean) {
        viewModelScope.launch {
            repository.setShowCategoryChip(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setShowCategoryChip`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.38 `setShowFavoriteIcon` — fun, líneas 200–204

```kotlin
    fun setShowFavoriteIcon(value: Boolean) {
        viewModelScope.launch {
            repository.setShowFavoriteIcon(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setShowFavoriteIcon`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.39 `setFabSize` — fun, líneas 205–209

```kotlin
    fun setFabSize(value: Float) {
        viewModelScope.launch {
            repository.setFabSize(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setFabSize`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.40 `setOptionMenuOrder` — fun, líneas 210–214

```kotlin
    fun setOptionMenuOrder(value: String) {
        viewModelScope.launch {
            repository.setOptionMenuOrder(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setOptionMenuOrder`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.41 `setOptionMenuHiddenItems` — fun, líneas 215–219

```kotlin
    fun setOptionMenuHiddenItems(value: String) {
        viewModelScope.launch {
            repository.setOptionMenuHiddenItems(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setOptionMenuHiddenItems`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.42 `setOptionMenuShowIcons` — fun, líneas 220–224

```kotlin
    fun setOptionMenuShowIcons(value: Boolean) {
        viewModelScope.launch {
            repository.setOptionMenuShowIcons(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setOptionMenuShowIcons`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.43 `setOptionMenuTextColor` — fun, líneas 225–229

```kotlin
    fun setOptionMenuTextColor(value: String) {
        viewModelScope.launch {
            repository.setOptionMenuTextColor(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setOptionMenuTextColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.44 `setOptionMenuOpacity` — fun, líneas 230–234

```kotlin
    fun setOptionMenuOpacity(value: Float) {
        viewModelScope.launch {
            repository.setOptionMenuOpacity(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setOptionMenuOpacity`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.45 `setPriorityMenuHiddenItems` — fun, líneas 235–239

```kotlin
    fun setPriorityMenuHiddenItems(value: String) {
        viewModelScope.launch {
            repository.setPriorityMenuHiddenItems(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setPriorityMenuHiddenItems`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.46 `setColorMenuHiddenItems` — fun, líneas 240–244

```kotlin
    fun setColorMenuHiddenItems(value: String) {
        viewModelScope.launch {
            repository.setColorMenuHiddenItems(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setColorMenuHiddenItems`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.47 `resetOptionMenuSettings` — fun, líneas 245–249

```kotlin
    fun resetOptionMenuSettings() {
        viewModelScope.launch {
            repository.resetOptionMenuSettings()
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.resetOptionMenuSettings`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.48 `setPerformanceMode` — fun, líneas 250–254

```kotlin
    fun setPerformanceMode(value: String) {
        viewModelScope.launch {
            repository.setPerformanceMode(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setPerformanceMode`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.49 `setAnimationsEnabled` — fun, líneas 255–259

```kotlin
    fun setAnimationsEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setAnimationsEnabled(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setAnimationsEnabled`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.50 `setAnimationSpeed` — fun, líneas 260–264

```kotlin
    fun setAnimationSpeed(value: Float) {
        viewModelScope.launch {
            repository.setAnimationSpeed(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setAnimationSpeed`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.51 `setAnimationStyle` — fun, líneas 265–269

```kotlin
    fun setAnimationStyle(value: String) {
        viewModelScope.launch {
            repository.setAnimationStyle(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setAnimationStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.52 `setAnimationEasing` — fun, líneas 270–274

```kotlin
    fun setAnimationEasing(value: String) {
        viewModelScope.launch {
            repository.setAnimationEasing(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setAnimationEasing`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.53 `setAnimationIntensity` — fun, líneas 275–279

```kotlin
    fun setAnimationIntensity(value: Float) {
        viewModelScope.launch {
            repository.setAnimationIntensity(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repository.setAnimationIntensity`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 14 | `repository` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Referencia una capa de repositorio, separando la coordinación de UI/estado del acceso y persistencia de datos. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 13–280 | 0 | `class SettingsViewModel(application: Application) : AndroidViewModel(application)` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 20–24 | 1 | `fun setDarkMode(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 21–23 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 25–29 | 1 | `fun setBackgroundColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 26–28 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 30–34 | 1 | `fun setBackgroundToneIndex(value: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 31–33 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 35–39 | 1 | `fun setBackgroundIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 36–38 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 40–44 | 1 | `fun setSettingsPanelTone(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 41–43 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 45–49 | 1 | `fun setSurfacePanelIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 46–48 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 50–54 | 1 | `fun setHeaderIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 51–53 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 55–59 | 1 | `fun setTextColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 56–58 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 60–64 | 1 | `fun setTextOutlineEnabled(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 61–63 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 65–69 | 1 | `fun setNoteUiTextColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 66–68 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 70–74 | 1 | `fun setSliderStyle(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 71–73 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 75–79 | 1 | `fun setFont(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 76–78 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 80–84 | 1 | `fun setFontSize(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 81–83 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 85–89 | 1 | `fun setSoundEffectsEnabled(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 86–88 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 90–94 | 1 | `fun setSoundEffectsVolume(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 91–93 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 95–99 | 1 | `fun setSoundEffectsTheme(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 96–98 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 100–104 | 1 | `fun setHapticEffectsEnabled(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 101–103 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 105–109 | 1 | `fun setHapticEffectsIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 106–108 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 110–114 | 1 | `fun setHapticEffectsStyle(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 111–113 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 115–119 | 1 | `fun setLanguage(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 116–118 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 120–124 | 1 | `fun setGridColumns(value: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 121–123 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 125–129 | 1 | `fun setSortOrder(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 126–128 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 130–134 | 1 | `fun setProfileImageUri(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 131–133 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 135–139 | 1 | `fun setProfileImageSize(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 136–138 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 140–144 | 1 | `fun setIconStyle(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 141–143 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 145–149 | 1 | `fun setIconSize(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 146–148 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 150–154 | 1 | `fun setAccentColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 151–153 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 155–159 | 1 | `fun setNoteCardCornerRadius(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 156–158 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 160–164 | 1 | `fun setNoteCardElevation(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 161–163 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 165–169 | 1 | `fun setNoteCardPadding(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 166–168 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 170–174 | 1 | `fun setNoteCardImageHeight(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 171–173 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 175–179 | 1 | `fun setNoteTitleMaxLines(value: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 176–178 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 180–184 | 1 | `fun setNoteContentMaxLines(value: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 181–183 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 185–189 | 1 | `fun setNoteLineSpacing(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 186–188 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 190–194 | 1 | `fun setShowNoteDate(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 191–193 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 195–199 | 1 | `fun setShowCategoryChip(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 196–198 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 200–204 | 1 | `fun setShowFavoriteIcon(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 201–203 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 205–209 | 1 | `fun setFabSize(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 206–208 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 210–214 | 1 | `fun setOptionMenuOrder(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 211–213 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 215–219 | 1 | `fun setOptionMenuHiddenItems(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 216–218 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 220–224 | 1 | `fun setOptionMenuShowIcons(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 221–223 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 225–229 | 1 | `fun setOptionMenuTextColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 226–228 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 230–234 | 1 | `fun setOptionMenuOpacity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 231–233 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 235–239 | 1 | `fun setPriorityMenuHiddenItems(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 236–238 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 240–244 | 1 | `fun setColorMenuHiddenItems(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 241–243 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 245–249 | 1 | `fun resetOptionMenuSettings()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 246–248 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 250–254 | 1 | `fun setPerformanceMode(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 251–253 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 255–259 | 1 | `fun setAnimationsEnabled(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 256–258 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 260–264 | 1 | `fun setAnimationSpeed(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 261–263 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 265–269 | 1 | `fun setAnimationStyle(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 266–268 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 270–274 | 1 | `fun setAnimationEasing(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 271–273 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 275–279 | 1 | `fun setAnimationIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 276–278 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
