# AppDatabase.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/data/AppDatabase.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `1fa3a2fe21723358f1effe2847ca51c55e0b5a570ba228dd55d016e0e824fa1d`  
**Líneas del código real:** 123

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Define y construye la base de datos Room de la aplicación, registra entidades/DAO y contiene la estrategia de creación/migración necesaria para conservar datos entre versiones.

**Arquitectura.** Es la raíz de persistencia SQLite/Room. Los ViewModel y repositorios llegan a notas/adjuntos a través de los DAO expuestos por esta base de datos.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.data`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **6 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`.

**Jetpack/Compose:** `androidx.room.Database`, `androidx.room.Room`, `androidx.room.RoomDatabase`, `androidx.room.migration.Migration`, `androidx.sqlite.db.SupportSQLiteDatabase`.

## 3. Restricciones e invariantes visibles en el archivo

- **Fallback nulo (1 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.

## 4. Bloques de código, uno por uno

### 4.1 `AppDatabase` — class, líneas 11–123

```kotlin
abstract class AppDatabase :
    RoomDatabase() {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Define y construye la base de datos Room de la aplicación, registra entidades/DAO y contiene la estrategia de creación/migración necesaria para conservar datos entre versiones.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 31 | `val MIGRATION_3_4` | `inferido` | `object :` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 65 | `val MIGRATION_4_5` | `inferido` | `object :` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 112 | `val instance` | `inferido` | `Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "notes_database")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 111 | `return INSTANCE?: synchronized(this) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `RoomDatabase`, `Migration`, `database.execSQL`, `trimIndent`, `synchronized`, `Room.databaseBuilder`, `addMigrations`, `build`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `noteDao` — fun, líneas 13–122

```kotlin
    abstract fun noteDao():
        NoteDao
    abstract fun attachmentDao():
        AttachmentDao
    companion object {
        @Volatile
        private var INSTANCE:
                AppDatabase? = null
        /*
         * -----------------------------------------------------
         * 3 -> 4
         * -----------------------------------------------------
         *
         * Esta es la migración de la fase de rendimiento anterior.
         * Se incluye también aquí para que una instalación que aún
         * esté en versión 3 pueda llegar hasta versión 5 sin borrar
         * ninguna nota.
         */
        private val MIGRATION_3_4 = object :
                Migration(3, 4) {
                override fun migrate(database:
                    SupportSQLiteDatabase) {
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_notes_priority_createdAt`
                        ON `notes` (`priority`, `createdAt`)
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_attachments_noteId_createdAt`
                        ON `attachments` (`noteId`, `createdAt`)
                        """.trimIndent()
                    )
                }
            }
        /*
         * -----------------------------------------------------
         * 4 -> 5
         * -----------------------------------------------------
         *
         * Añade exclusivamente las propiedades necesarias para
         * el diseño nuevo:
         *
         * - category
         * - isFavorite
         * - isPinned
         *
         * No se elimina ni recrea ninguna tabla.
         */
        private val MIGRATION_4_5 = object :
                Migration(4, 5) {
                override fun migrate(database:
                    SupportSQLiteDatabase) {
                    database.execSQL(
                        """
                        ALTER TABLE `notes`
                        ADD COLUMN `category`
                        TEXT NOT NULL
                        DEFAULT 'personal'
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        ALTER TABLE `notes`
                        ADD COLUMN `isFavorite`
                        INTEGER NOT NULL
                        DEFAULT 0
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        ALTER TABLE `notes`
                        ADD COLUMN `isPinned`
                        INTEGER NOT NULL
                        DEFAULT 0
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_notes_isPinned_priority_createdAt`
                        ON `notes`
                        (`isPinned`, `priority`, `createdAt`)
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_notes_category`
                        ON `notes` (`category`)
                        """.trimIndent()
                    )
                }
            }
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                    val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "notes_database")
                            /*
                             * Importante:
                             * no usamos fallback destructivo.
                             */
                            .addMigrations(MIGRATION_3_4, MIGRATION_4_5).build()
                    INSTANCE = instance
                    instance
                }
        }
    }
```

#### Qué hace y por qué existe

Define y construye la base de datos Room de la aplicación, registra entidades/DAO y contiene la estrategia de creación/migración necesaria para conservar datos entre versiones.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `NoteDao abstract fun attachmentDao(): AttachmentDao companion object`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 31 | `val MIGRATION_3_4` | `inferido` | `object :` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 65 | `val MIGRATION_4_5` | `inferido` | `object :` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 112 | `val instance` | `inferido` | `Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "notes_database")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 111 | `return INSTANCE?: synchronized(this) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Migration`, `database.execSQL`, `trimIndent`, `synchronized`, `Room.databaseBuilder`, `addMigrations`, `build`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `attachmentDao` — fun, líneas 15–122

```kotlin
    abstract fun attachmentDao():
        AttachmentDao
    companion object {
        @Volatile
        private var INSTANCE:
                AppDatabase? = null
        /*
         * -----------------------------------------------------
         * 3 -> 4
         * -----------------------------------------------------
         *
         * Esta es la migración de la fase de rendimiento anterior.
         * Se incluye también aquí para que una instalación que aún
         * esté en versión 3 pueda llegar hasta versión 5 sin borrar
         * ninguna nota.
         */
        private val MIGRATION_3_4 = object :
                Migration(3, 4) {
                override fun migrate(database:
                    SupportSQLiteDatabase) {
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_notes_priority_createdAt`
                        ON `notes` (`priority`, `createdAt`)
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_attachments_noteId_createdAt`
                        ON `attachments` (`noteId`, `createdAt`)
                        """.trimIndent()
                    )
                }
            }
        /*
         * -----------------------------------------------------
         * 4 -> 5
         * -----------------------------------------------------
         *
         * Añade exclusivamente las propiedades necesarias para
         * el diseño nuevo:
         *
         * - category
         * - isFavorite
         * - isPinned
         *
         * No se elimina ni recrea ninguna tabla.
         */
        private val MIGRATION_4_5 = object :
                Migration(4, 5) {
                override fun migrate(database:
                    SupportSQLiteDatabase) {
                    database.execSQL(
                        """
                        ALTER TABLE `notes`
                        ADD COLUMN `category`
                        TEXT NOT NULL
                        DEFAULT 'personal'
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        ALTER TABLE `notes`
                        ADD COLUMN `isFavorite`
                        INTEGER NOT NULL
                        DEFAULT 0
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        ALTER TABLE `notes`
                        ADD COLUMN `isPinned`
                        INTEGER NOT NULL
                        DEFAULT 0
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_notes_isPinned_priority_createdAt`
                        ON `notes`
                        (`isPinned`, `priority`, `createdAt`)
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_notes_category`
                        ON `notes` (`category`)
                        """.trimIndent()
                    )
                }
            }
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                    val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "notes_database")
                            /*
                             * Importante:
                             * no usamos fallback destructivo.
                             */
                            .addMigrations(MIGRATION_3_4, MIGRATION_4_5).build()
                    INSTANCE = instance
                    instance
                }
        }
    }
```

#### Qué hace y por qué existe

Define y construye la base de datos Room de la aplicación, registra entidades/DAO y contiene la estrategia de creación/migración necesaria para conservar datos entre versiones.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `AttachmentDao companion object`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 31 | `val MIGRATION_3_4` | `inferido` | `object :` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 65 | `val MIGRATION_4_5` | `inferido` | `object :` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 112 | `val instance` | `inferido` | `Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "notes_database")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 111 | `return INSTANCE?: synchronized(this) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Migration`, `database.execSQL`, `trimIndent`, `synchronized`, `Room.databaseBuilder`, `addMigrations`, `build`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `getDatabase` — fun, líneas 110–121

```kotlin
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                    val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "notes_database")
                            /*
                             * Importante:
                             * no usamos fallback destructivo.
                             */
                            .addMigrations(MIGRATION_3_4, MIGRATION_4_5).build()
                    INSTANCE = instance
                    instance
                }
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.

**Retorno:** `AppDatabase`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 112 | `val instance` | `inferido` | `Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "notes_database")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 111 | `return INSTANCE?: synchronized(this) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `synchronized`, `Room.databaseBuilder`, `addMigrations`, `build`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 31 | `MIGRATION_3_4` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 65 | `MIGRATION_4_5` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 112 | `instance` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 12–123 | 0 | `RoomDatabase()` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 17–122 | 1 | `companion object` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 32–50 | 2 | `Migration(3, 4)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 34–49 | 3 | `SupportSQLiteDatabase)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 66–109 | 2 | `Migration(4, 5)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 68–108 | 3 | `SupportSQLiteDatabase)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 110–121 | 2 | `fun getDatabase(context: Context): AppDatabase` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 111–120 | 3 | `return INSTANCE?: synchronized(this)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
