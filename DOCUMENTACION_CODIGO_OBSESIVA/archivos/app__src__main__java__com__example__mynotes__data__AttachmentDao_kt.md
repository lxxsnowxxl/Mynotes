# AttachmentDao.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/data/AttachmentDao.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `b08c95b65977d9cd903a61de27f1ae385743249f560299c65cae57b7171e1ae1`  
**Líneas del código real:** 69

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Interfaz DAO de Room para consultar, insertar y eliminar adjuntos y para relacionarlos con sus notas.

**Arquitectura.** Concentra SQL y operaciones de persistencia de adjuntos para que el resto del proyecto no tenga que construir consultas manualmente.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.data`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **6 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Jetpack/Compose:** `androidx.room.Dao`, `androidx.room.Delete`, `androidx.room.Insert`, `androidx.room.OnConflictStrategy`, `androidx.room.Query`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.flow.Flow`.

## 3. Restricciones e invariantes visibles en el archivo

- No se detectaron automáticamente operadores de restricción comunes; las restricciones específicas siguen documentadas dentro de cada declaración.

## 4. Bloques de código, uno por uno

### 4.1 `AttachmentDao` — interface, líneas 11–69

```kotlin
interface AttachmentDao {
    /*
     * NotesScreen observa todos los adjuntos una sola vez.
     * Después los agrupa por noteId en memoria.
     */
    @Query(
        """
        SELECT *
        FROM attachments
        ORDER BY createdAt DESC
        """
    )
    fun getAllAttachments():
        Flow<List<Attachment>>
    @Query(
        """
        SELECT *
        FROM attachments
        ORDER BY id ASC
        """
    )
    suspend fun getAllAttachmentsOnce():
        List<Attachment>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachmentsForRestore(attachments: List<Attachment>)
    @Query("DELETE FROM attachments")
    suspend fun deleteAllAttachments()
    @Query(
        """
        SELECT *
        FROM attachments
        WHERE noteId = :noteId
        ORDER BY createdAt ASC
        """
    )
    fun getAttachments(noteId: Int): Flow<List<Attachment>>
    @Query(
        """
        SELECT *
        FROM attachments
        WHERE noteId = :noteId
        ORDER BY createdAt ASC
        """
    )
    suspend fun getAttachmentsOnce(noteId: Int): List<Attachment>
    @Insert
    suspend fun insertAttachment(attachment: Attachment)
    @Insert
    suspend fun insertAttachments(attachments: List<Attachment>)
    @Delete
    suspend fun deleteAttachment(attachment: Attachment)
    @Query(
        """
        DELETE FROM attachments
        WHERE noteId = :noteId
        """
    )
    suspend fun deleteAttachmentsForNote(noteId: Int)
}
```

#### Qué hace y por qué existe

Interfaz DAO de Room para consultar, insertar y eliminar adjuntos y para relacionarlos con sus notas.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Query`, `Insert`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `getAllAttachments` — fun, líneas 23–31

```kotlin
    fun getAllAttachments():
        Flow<List<Attachment>>
    @Query(
        """
        SELECT *
        FROM attachments
        ORDER BY id ASC
        """
    )
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Flow<List<Attachment>> @Query( """ SELECT * FROM attachments ORDER BY id ASC """ ) suspend fun getAllAttachmentsOnce(): List<Attachment> @Insert(onConflict`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Query`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `getAllAttachmentsOnce` — fun, líneas 32–34

```kotlin
    suspend fun getAllAttachmentsOnce():
        List<Attachment>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `List<Attachment> @Insert(onConflict`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Insert`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `insertAttachmentsForRestore` — fun, líneas 35–36

```kotlin
    suspend fun insertAttachmentsForRestore(attachments: List<Attachment>)
    @Query("DELETE FROM attachments")
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `attachments: List<Attachment>` — `attachments` recibe un valor de tipo `List<Attachment>`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Query`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `deleteAllAttachments` — fun, líneas 37–45

```kotlin
    suspend fun deleteAllAttachments()
    @Query(
        """
        SELECT *
        FROM attachments
        WHERE noteId = :noteId
        ORDER BY createdAt ASC
        """
    )
```

#### Qué hace y por qué existe

Elimina o invalida el estado/recurso indicado, incluyendo las limpiezas auxiliares previstas por el bloque.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Query`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `getAttachments` — fun, líneas 46–54

```kotlin
    fun getAttachments(noteId: Int): Flow<List<Attachment>>
    @Query(
        """
        SELECT *
        FROM attachments
        WHERE noteId = :noteId
        ORDER BY createdAt ASC
        """
    )
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `noteId: Int` — `noteId` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Flow<List<Attachment>> @Query( """ SELECT * FROM attachments WHERE noteId`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Query`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `getAttachmentsOnce` — fun, líneas 55–56

```kotlin
    suspend fun getAttachmentsOnce(noteId: Int): List<Attachment>
    @Insert
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `noteId: Int` — `noteId` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `List<Attachment> @Insert suspend fun insertAttachment(attachment: Attachment) @Insert suspend fun insertAttachments(attachments: List<Attachment>) @Delete suspend fun deleteAttachment(attachment: Attachment) @Query( """ DELETE FROM attachments WHERE noteId`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

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

### 4.8 `insertAttachment` — fun, líneas 57–58

```kotlin
    suspend fun insertAttachment(attachment: Attachment)
    @Insert
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `attachment: Attachment` — `attachment` recibe un valor de tipo `Attachment`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

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

### 4.9 `insertAttachments` — fun, líneas 59–60

```kotlin
    suspend fun insertAttachments(attachments: List<Attachment>)
    @Delete
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `attachments: List<Attachment>` — `attachments` recibe un valor de tipo `List<Attachment>`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

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

### 4.10 `deleteAttachment` — fun, líneas 61–67

```kotlin
    suspend fun deleteAttachment(attachment: Attachment)
    @Query(
        """
        DELETE FROM attachments
        WHERE noteId = :noteId
        """
    )
```

#### Qué hace y por qué existe

Elimina o invalida el estado/recurso indicado, incluyendo las limpiezas auxiliares previstas por el bloque.

#### Contrato de la declaración

**Parámetros:**

- `attachment: Attachment` — `attachment` recibe un valor de tipo `Attachment`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Query`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.11 `deleteAttachmentsForNote` — fun, líneas 68–69

```kotlin
    suspend fun deleteAttachmentsForNote(noteId: Int)
}
```

#### Qué hace y por qué existe

Elimina o invalida el estado/recurso indicado, incluyendo las limpiezas auxiliares previstas por el bloque.

#### Contrato de la declaración

**Parámetros:**

- `noteId: Int` — `noteId` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

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

No se detectaron propiedades/variables con inicializador mediante el patrón habitual `val/var = ...`.

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 11–69 | 0 | `interface AttachmentDao` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
