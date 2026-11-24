# Revisión de rendimiento — v276

La revisión se centró en el panel de perfil, el ajuste del avatar, la carga de la foto y los fondos incorporados recientemente. Es una revisión de código con optimizaciones; no una medición del rendimiento de la aplicación completa.

## Cambios aplicados

- La lectura del tamaño se pospone hasta los componentes del avatar y del deslizador. El panel recibe una función para consultar el valor, en lugar de recibir un nuevo número en cada movimiento. Esto permite limitar la recomposición de las partes que no cambian.
- La foto utiliza una solicitud de Coil recordada, con un tamaño de carga equivalente al máximo de 84 dp del avatar. La solicitud cambia cuando cambia la foto, el contexto o la densidad, sin depender de los tamaños intermedios del gesto.
- El fondo de degradado crea su pincel lineal; el difuminado crea sus tres pinceles radiales. Antes se construían ambos conjuntos aunque uno no se utilizara. Se conserva la caché de dibujo, la geometría y los colores.

## Comprobaciones

La zona reservada al avatar sigue siendo de 96 dp y mantiene su centro fijo. Las acciones de foto, las tarjetas de modo y los XML conservan su distribución. La preferencia del tamaño sigue guardándose al terminar de arrastrar.

El editor de recorte ya decodifica y guarda las imágenes en `Dispatchers.IO`, y reduce las imágenes grandes antes de editarlas. No se modificó ese procesamiento.

Pasaron las comprobaciones estáticas de contratos Compose, referencias de recursos, estructura XML, conexión del fondo global y conservación de los controles del panel.

## Límites de la revisión

No se ejecutó compilación Android ni se midieron FPS, CPU, memoria o tiempos de cuadro. No se puede certificar la ausencia de tirones o fugas a partir del análisis estático ni asignar una mejora porcentual.

La validación en dispositivo debe cubrir el ajuste repetido de 36 a 84 dp con y sin foto, el desplazamiento de Configuración con los tres estilos de fondo y varias entradas y salidas del editor de recorte. Conviene realizar la medición con una compilación release en el teléfono de destino.

## Referencias técnicas

- [Fases y rendimiento de Jetpack Compose](https://developer.android.com/develop/ui/compose/performance/phases)
- [Carga de imágenes con Coil en Compose](https://coil-kt.github.io/coil/compose/)
- [ImageRequest.Builder](https://coil-kt.github.io/coil/api/coil-core/coil3.request/-image-request/-builder/index.html)

