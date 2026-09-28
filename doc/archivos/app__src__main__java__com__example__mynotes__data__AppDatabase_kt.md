# AppDatabase.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/data/AppDatabase.kt`  
**SHA-256:** `1fa3a2fe21723358f1effe2847ca51c55e0b5a570ba228dd55d016e0e824fa1d`  
**Líneas:** 124  
**Package:** `com.example.mynotes.data`

## 1. Para qué existe este archivo

Define la base Room, sus DAOs, versión y migraciones. Centraliza la instancia singleton de la base de notas/adjuntos.

## 2. Tipos/clases declarados

- Línea **11** — `abstract  class AppDatabase`.

## 3. Estado, constantes y valores importantes

- **`INSTANCE`** (línea 19) inicia con `null`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`MIGRATION_3_4`** (línea 31) inicia con `object :`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`MIGRATION_4_5`** (línea 65) inicia con `object :`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`instance`** (línea 112) inicia con `Room.databaseBuilder(context.applicationContext`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `noteDao` — líneas 13–13

**Firma:** `abstract fun noteDao():`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Salida:** Unit o inferido por Kotlin.

### `attachmentDao` — líneas 15–15

**Firma:** `abstract fun attachmentDao():`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Salida:** Unit o inferido por Kotlin.

### `migrate` — líneas 33–49

**Firma:** `override fun migrate(database: SupportSQLiteDatabase)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `database: SupportSQLiteDatabase`

**Salida:** Unit o inferido por Kotlin.

**Operaciones/funciones que coordina:** `execSQL`, `trimIndent`.

### `migrate` — líneas 67–108

**Firma:** `override fun migrate(database: SupportSQLiteDatabase)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `database: SupportSQLiteDatabase`

**Salida:** Unit o inferido por Kotlin.

**Operaciones/funciones que coordina:** `execSQL`, `trimIndent`.

### `getDatabase` — líneas 110–121

**Firma:** `fun getDatabase(context: Context): AppDatabase`

Obtiene el dato solicitado desde la fuente o estructura que maneja este archivo, sin cambiar el contrato público del resto del módulo.

**Entradas:**
- `context: Context`

**Salida:** AppDatabase.

**Efectos/APIs observados en el cuerpo:**
- Accede a la base Room/DAO.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.

**Operaciones/funciones que coordina:** `synchronized`, `databaseBuilder`, `addMigrations`, `build`.

## 5. Cómo se conecta con el resto de MyNotes

- No importa directamente otro componente `com.example.mynotes`; funciona como modelo/utilidad base o mediante APIs Android/Jetpack.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Accede a la base Room/DAO.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `noteDao` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
2. `attachmentDao` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
3. `migrate` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
4. `migrate` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
5. `getDatabase` — Obtiene el dato solicitado desde la fuente o estructura que maneja este archivo, sin cambiar el contrato público del resto del módulo.

## 9. Qué no debe romperse al modificarlo

- Los cambios de esquema Room requieren revisar versión/migraciones y compatibilidad con datos existentes.

## 10. Resumen en lenguaje sencillo

En términos simples: Define la base Room, sus DAOs, versión y migraciones. Centraliza la instancia singleton de la base de notas/adjuntos. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
