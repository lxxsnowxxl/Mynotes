# AppDatabase.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/data/AppDatabase.kt`  **SHA-256:** `1fa3a2fe21723358f1effe2847ca51c55e0b5a570ba228dd55d016e0e824fa1d`  **Líneas:** 123 · **Bytes:** 4536 · **Imports:** 6 · **Declaraciones detectadas:** 4
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Base Room y punto de acceso a DAO de notas/adjuntos.
## 2. Package e imports

Package declarado: `com.example.mynotes.data`.

### Android / Jetpack / Compose

`android.content.Context`, `androidx.room.Database`, `androidx.room.Room`, `androidx.room.RoomDatabase`, `androidx.room.migration.Migration`, `androidx.sqlite.db.SupportSQLiteDatabase`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 11 | `class` | `AppDatabase` | `abstract class AppDatabase :` |
| 13 | `fun` | `noteDao` | `abstract fun noteDao():` |
| 15 | `fun` | `attachmentDao` | `abstract fun attachmentDao():` |
| 110 | `fun` | `getDatabase` | `fun getDatabase(context: Context): AppDatabase {` |

## 4. Estado, efectos y límites observables

- **Room:** 2 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
