# Diseños XML editables — v268

> Actualización v293: el selector recupera las proporciones de los tres botones
> originales. Las cuatro opciones permanecen en una fila horizontal deslizable,
> con texto de una línea y sin aumentar la altura para reservar dos líneas.
> Los ocho XML que muestran el selector incluyen su HorizontalScrollView.

> Actualización v292: se amplió el recorrido del Blur animado y se actualizaron
> sus tres fondos XML con los centros del nuevo fotograma inicial. Los diseños
> XML continúan siendo referencias estáticas; la animación se ejecuta en la app.

> Actualización v291: hay 56 diseños principales y una variante de ancho. El
> selector incluye Sólido, Degradado, Blur y Blur animado. El nuevo
> `preview_40_settings_background_motion.xml` muestra el fotograma inicial del
> efecto; el movimiento se ejecuta en Compose. En pantallas estrechas o con
> texto ampliado, el selector Compose se adapta a dos filas.

> Actualización v284: Configuración avanzada se organiza en seis categorías con
> accesos fijos bajo la barra superior. Los 16 XML avanzados siguen el orden y
> el punto inicial de su preview Compose; el diseño básico conserva sus bytes.

> Actualización v283: los cuatro paneles plegables guardan su estado en los ajustes
> de la aplicación. Los previews conservan sus estados de ejemplo abiertos/cerrados.

> Actualización v282: el título, la descripción y los controles de Options menu
> comparten un único recuadro redondeado.

> Actualización v270: el fondo se aplica a toda la interfaz. Ver
> [FONDO_GLOBAL_V270.md](FONDO_GLOBAL_V270.md).

> Actualización v281: hay 55 diseños principales, 46 familias de colores visibles
> y tres estilos de fondo. Tarjetas de notas y los tres paneles de acciones/submenús
> disponen de estados abiertos y contraídos.

La carpeta `app/src/main/res/layout/` contiene **56 diseños XML de pantallas,
menús y diálogos**, además de los nueve XML funcionales de widgets y
notificaciones. Los diseños se componen de vistas Android: `TextView`,
`EditText`, `Button`, `ImageButton`, `Switch`, `SeekBar`, `Spinner`,
`RadioButton`, `LinearLayout`, `FrameLayout`, `GridLayout` y contenedores de
desplazamiento. Cada control tiene un identificador editable.

Se sustituyeron los contenedores `ComposeView` y `tools:composableName` de
v263. Abrir un archivo XML ahora muestra su jerarquía de vistas en **Code**,
**Design** o **Split**. Los iconos, formas, fondos y muestras de medios se
definen en recursos XML de `res/drawable`; el diseño completo no está incrustado
en una imagen.

## Pantallas incluidas

- Lista de notas: normal, vacía, búsqueda, una columna, tres columnas, adjuntos,
  menús de acciones, prioridad, color y categoría, y menú de creación.
- Editor de notas: creación, edición, adjuntos y grabación.
- Detalle: normal, enlaces, contenido multimedia, archivos y confirmación de borrado.
- Ajustes: perfil, modo básico/avanzado, personalización, menús, paletas de cinco
  tonos, sonido, alertas, vibración, tipografía, tarjetas, rendimiento y copias.
- Visores de PDF, imagen, vídeo, audio, texto, documentos y archivos genéricos.
- Desarrollo, mapa de código, dibujo, recordatorios, biblioteca y editor PDF.
- Diálogos de elección de modo, edición de foto de perfil y recorte de imagen.

`preview_20_settings_card_layout.xml` muestra Tarjetas de notas abierto y
`preview_20b_settings_card_layout_collapsed.xml` lo muestra contraído. Ambos
estados tienen su correspondiente entrada Compose en `LayoutPreviews.kt`.

`preview_04c_settings_options_menu.xml` muestra los paneles de acciones y submenús
abiertos. `preview_04e_settings_options_collapsed.xml` muestra los tres contraídos,
también con su entrada Compose. Cada panel se abre o se cierra por separado.

En v288, los cinco XML de detalle muestran las cuatro acciones inferiores dentro
de botones redondeados completos, con icono y etiqueta. Siguen Compose: radio de
10 dp, altura mínima de 48 dp, icono de 18 dp y etiqueta de 9 sp. Cada columna
incorpora 5 dp a cada lado del recuadro, además de los 8 dp entre columnas.
Eliminar usa un fondo rojo. Los colores XML representan el ejemplo neutro con
texto automático; en ejecución Compose adapta el contraste al fondo de la nota
y al modo de texto. El botón completo recibe la pulsación, incluida su etiqueta.

`res/layout-w463dp/preview_11_notes_three_columns.xml` añade la variante de tres
columnas cuando el ancho disponible permite las tres tarjetas mínimas de 145 dp
y los márgenes de 14 dp de la pantalla Compose. El diseño principal usa dos
columnas en pantallas más estrechas.

## Relación con Compose

**La aplicación sigue utilizando Compose en ejecución.** Estos XML son diseños
paralelos para el editor de vistas; sus controles no están conectados a los
ViewModels ni sustituyen automáticamente las pantallas de la aplicación.
Cambiar un XML requiere aplicar también el cambio correspondiente en Compose
para mantener ambos diseños alineados.

El mapa `doc/layout_compose_previews.json` identifica el archivo y la función
Compose de referencia de cada XML. Conserva además las entradas `@Preview` de
Kotlin para comparar las dos representaciones en Android Studio.

Los ajustes de ejemplo siguen los previews actuales: efectos de sonido al 35 %,
alertas al 80 % y vibración al 55 %. Los valores de producción siguen definidos
en `AppSettings` y `SettingsRepository`; no se modifican por abrir estos diseños.

En v287, `preview_07_settings_transition_dropdown.xml` y el arreglo de su
selector ofrecen siete transiciones nuevas y Aleatoria, en el mismo orden de
Compose. Los 25 tipos anteriores se retiraron del selector y de las etiquetas
de los cinco recursos de idioma. Las preferencias antiguas se convierten al
catálogo actual al leerlas o restaurarlas.
La paleta neutra usa los colores y la mezcla de paneles de `Theme.kt`; el ejemplo
de tonos de paleta muestra la selección lavanda. Las cápsulas de Ajustes y notas
tienen 54 dp. Los campos y las tarjetas conservan las medidas usadas como
referencia en los composables.

## Abrir y comparar

1. Integrar el contenido en el proyecto Android Studio completo y sincronizarlo.
2. Abrir cualquier `preview_*.xml` de `res/layout` y elegir **Split** o **Design**.
3. Para comparar con los previews Compose, usar una pantalla de 411 × 891 dp,
   escala de fuente 1 y configuración regional española. Usar un ancho mínimo
   de 463 dp para revisar la variante de tres columnas.
4. Revisar el mismo estado en `src/debug/.../preview/LayoutPreviews.kt` y ajustar
   las diferencias observadas en el editor Android.

## Verificación y límites

Se comprobaron las jerarquías XML, los identificadores, las referencias a
recursos, los contratos de datos de los previews Kotlin y la conservación de
las pantallas Compose y los nueve layouts funcionales. No se compiló Android
ni se ejecutó Layoutlib o un emulador: el entorno no dispone del SDK Android,
compilador Kotlin o distribución Gradle disponible.

**La equivalencia visual píxel por píxel queda pendiente de comprobar en Android
Studio.** Los controles nativos y los controles Material 3 de Compose tienen
motores de medida y dibujo distintos; los XML toman como referencia la
estructura, las medidas, los textos y los colores de Compose.

Comprobaciones reproducibles:

```sh
python3 tools/check_native_xml_v268.py
python3 tools/check_layout_compose_sync_v263.py
python3 tools/check_settings_widgets_v259.py
```

El script `tools/update_native_layouts_v268.py` actualiza las plantillas y los
recursos compartidos de diseño. Revisar sus plantillas al cambiar las pantallas
Compose; regenerar no convierte arbitrariamente código Kotlin en XML.
