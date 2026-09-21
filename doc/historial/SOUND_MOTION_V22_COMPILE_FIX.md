# MyNotes v22 - AppMotion compile fix

Se corrigieron tres especificaciones spring usadas por `slideInVertically` y `slideInHorizontally`.

Compose espera `FiniteAnimationSpec<IntOffset>` para animaciones de desplazamiento. La v21 había creado `spring<Int>` en los presets:

- `expressive_spring`
- `container_transform`
- `elastic_slide`

Ahora los tres usan `spring<IntOffset>` y `AppMotion.kt` importa `androidx.compose.ui.unit.IntOffset`.

No se modificó la lógica de adjuntos, miniaturas, previews ni sonidos.
