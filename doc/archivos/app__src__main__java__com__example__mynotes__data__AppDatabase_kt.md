# AppDatabase.kt

**Ruta:** `app/src/main/java/com/example/mynotes/data/AppDatabase.kt`  
**Paquete:** `com.example.mynotes.data`  
**Líneas:** 178 → 123 (30.9% menos)

## Responsabilidad

Define y construye la base de datos Room de la aplicación, registra entidades/DAO y contiene la estrategia de creación/migración necesaria para conservar datos entre versiones.

## Papel dentro de la arquitectura

Es la raíz de persistencia SQLite/Room. Los ViewModel y repositorios llegan a notas/adjuntos a través de los DAO expuestos por esta base de datos.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Android/Jetpack:** `android.content.Context`, `androidx.room.Database`, `androidx.room.Room`, `androidx.room.RoomDatabase`, `androidx.room.migration.Migration`, `androidx.sqlite.db.SupportSQLiteDatabase`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 11 | class | `AppDatabase` | `abstract class AppDatabase :` | Clase que encapsula estado y comportamiento de esta parte del sistema. |
| 13 | fun | `noteDao` | `abstract fun noteDao():` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 15 | fun | `attachmentDao` | `abstract fun attachmentDao():` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 33 | fun | `migrate` | `override fun migrate(database:` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 67 | fun | `migrate` | `override fun migrate(database:` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 110 | fun | `getDatabase` | `fun getDatabase(context: Context): AppDatabase {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
