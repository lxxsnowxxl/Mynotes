# v279 — Degradado y blur en los cinco tonos

Se revisaron las 46 paletas con sus cinco tonos: 230 pares de paleta y tono. La selección guarda la clave de paleta y el índice de tono juntos. El fondo global y los tres recuadros de ejemplo de Ajustes incluyen ambos valores entre las dependencias que recalculan sus colores.

## Fallos encontrados y corrección

En el cuarto tono de Amarillo neón (`neon_yellow`, índice 3), el cálculo de contraste devolvía el mismo color base para ambos tintes. El degradado y el blur terminaban dibujando un fondo uniforme. Ahora, cuando ambos tintes coinciden con la base, se generan dos variaciones de luminosidad que conservan el contraste disponible. Para este tono, la base es `#8A7200` y los tintes pasan a ser `#715D00` y `#7F6900`.

La elección manual de texto blanco también podía dejar planos muchos fondos claros: la función exigía alcanzar 4.5:1 aunque el color de texto elegido ya tuviera menos contraste sobre la base. Se conserva esa elección manual y se admiten mezclas que mantienen o mejoran el contraste de partida. Un margen pequeño protege las mezclas frente al redondeo de los canales sRGB. El texto automático mantiene el objetivo de 4.5:1.

No se modifican los colores originales de las paletas ni sus índices. Se mantienen el fondo compartido entre pantallas, el dibujo con `drawWithCache`, el degradado de tres paradas y las tres manchas radiales del estilo Blur.

## Comprobación realizada

`python3 tools/check_palette_backgrounds_v279.py` analiza los valores del catálogo y los parámetros del efecto. Evalúa una referencia matemática sRGB para las 230 selecciones, los temas claro/oscuro y las opciones de texto automático/negro/blanco: 1,380 contextos y 2,760 efectos.

| Comprobación | Resultado |
| --- | --- |
| Paletas / tonos por paleta | 46 / 5 |
| Muestras de degradado por contexto | 101 |
| Muestras de blur por contexto | 825 |
| Efectos muestreados sin variación | 0 |
| Contraste por debajo del mínimo disponible en las muestras | 0 |
| Contraste mínimo muestreado con texto automático | 4.50078:1 |
| Dependencias de selección y caché de vistas de ejemplo | Correctas |

Las pruebas JUnit existentes se amplían para comprobar las 230 selecciones en los tres modos de configuración y los tres estilos, así como los colores de fondo en ambos temas y con las tres opciones de texto. Se añade la regresión concreta de Amarillo neón.

Los XML de referencia conservan sus escenas existentes. Sus fondos de ejemplo usan los tonos claros de Neutro, Océano y Lavanda, cuyos colores no cambian con esta corrección. No se añaden centenares de recursos XML para un efecto que la aplicación calcula a partir de la selección.

## Alcance de la validación

La referencia matemática y las comprobaciones estáticas no ejecutan Compose ni miden píxeles de un dispositivo. En este entorno no se ha compilado Android, ejecutado JUnit ni generado un APK. Falta revisar los cambios en Android, incluidos los ejemplos de Ajustes y el fondo de las demás pantallas.

Referencias de API: [Color y representación sRGB](https://developer.android.com/reference/kotlin/androidx/compose/ui/graphics/Color), [Brush](https://developer.android.com/reference/kotlin/androidx/compose/ui/graphics/Brush).
