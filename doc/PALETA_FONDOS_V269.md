# Paleta reducida y fondos de Configuración — v269

> Actualización v270: el fondo se aplica a toda la interfaz. Ver
> [FONDO_GLOBAL_V270.md](FONDO_GLOBAL_V270.md).

## Uso

En Configuración, debajo de la paleta de colores, aparece **Fondo de
Configuración**. Permite elegir:

- **Sólido:** conserva el fondo uniforme y es la opción predeterminada.
- **Degradado:** mezcla tres colores de la familia y tonalidad seleccionadas.
- **Blur:** mezcla manchas de color con bordes difuminados. El texto, las
  tarjetas y los controles permanecen nítidos.

La elección está disponible en modo básico y avanzado, se guarda en DataStore y
se conserva al cerrar y abrir la aplicación. También se exporta e importa en las
copias manuales de MyNotes. Las copias anteriores usan Sólido cuando no incluyen
el nuevo campo. Los fondos se aplican a Configuración; la preferencia de modo
nocturno y el resto de las pantallas conservan su funcionamiento.

## 23 familias visibles

Se reduce de 46 a 23 el número de familias del selector, con cinco tonalidades
por familia. Se mantienen colores neutros, cálidos, fríos y brillantes, incluidos
oliva y mostaza. La geometría de los círculos conserva sus medidas.

El catálogo interno conserva las 46 familias y sus recursos para abrir
preferencias y copias anteriores. Si la familia que estaba seleccionada no
pertenece a las 23 propuestas, ocupa temporalmente la última posición del
selector. La elección anterior sigue visible, con sus tonos originales, y el
selector conserva 23 opciones. Seleccionar otra familia restaura la lista
reducida habitual.

## Dibujo y contraste

Degradado y Blur usan mezclas RGB de los colores de la familia activa. Las
mezclas se acercan al color base cuando es necesario para mantener el contraste
del texto automático. El control de tono de panel sigue actuando sobre las
tarjetas al utilizar estos fondos.

Blur se dibuja con gradientes radiales de opacidad decreciente. Se emplean
primitivas de dibujo compatibles con el mínimo API 24 del proyecto; no requiere
`RenderEffect`, shaders AGSL ni el modificador de desenfoque exclusivo de
versiones recientes de Android. Las brochas se mantienen en una caché de dibujo
para no generarlas durante cada desplazamiento.

## Compose y XML

`SettingsBackground.kt` contiene el dibujo y los tres controles de selección de
Compose. `tools/update_native_layouts_v268.py` usa las mismas posiciones,
opacidades, colores y lista reducida para actualizar los diseños XML.

Se conservan 50 diseños anteriores y se añaden:

- `preview_37_settings_background_solid.xml`
- `preview_38_settings_background_gradient.xml`
- `preview_39_settings_background_blur.xml`

Hay 53 diseños XML principales y una variante para pantallas amplias. Los fondos
XML son vectores con gradientes editables; se mantienen los controles nativos de
las pantallas. Los previews Compose equivalentes permanecen en `src/debug`.

La aplicación continúa ejecutando Compose. Los XML son diseños paralelos de
referencia y sus controles no se conectan automáticamente a los ViewModels.

## Verificación

Se comprueban recursos, referencias, contratos de eventos, índices de las
secciones y conservación de las correcciones previas. Se incluyen regresiones
JUnit para las 23 familias, las selecciones anteriores, el contraste de los
fondos y el valor predeterminado de la nueva preferencia.

Las pruebas JUnit y la compilación Android no se ejecutaron: este entorno carece
de SDK Android, compilador Kotlin y distribución Gradle disponible. La revisión
visual de los colores se hizo con una representación de los XML; el render de
Android Studio y la prueba en un dispositivo quedan pendientes.

Comprobaciones estáticas:

```sh
python3 tools/check_settings_widgets_v259.py
python3 tools/check_layout_compose_sync_v263.py
python3 tools/check_native_xml_v268.py
```
