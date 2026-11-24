# Fondo global — v270

La elección de Sólido, Degradado o Blur se aplica a toda la interfaz. El control
se llama **Fondo de la aplicación** y sigue dentro de Configuración.

## Corrección

El fondo de v269 se dibujaba únicamente dentro de `SettingsScreen`. Las otras
pantallas tenían contenedores sólidos que lo cubrían. Ahora `MyNotesTheme`
proporciona una apariencia compartida mediante `AppBackgroundProvider`.

Las pantallas dibujan su fondo con `appScreenBackground`, y las barras permiten
verlo cuando se elige Degradado o Blur. Se proporciona explícitamente el color
del contenido al hacer transparentes los contenedores. Durante las transiciones
queda visible el fondo del tema, sin capas sólidas intermedias.

Se revisaron:

- Lista de notas, editor y detalle.
- Configuración, Desarrollo y mapa de código.
- Lista y editor de recordatorios.
- Dibujo.
- Biblioteca y editor PDF.
- Visores de adjuntos: imagen, vídeo, audio, PDF, texto, documentos y archivos.

Las actividades de PDF y adjuntos reciben la misma preferencia a través de su
tema y de la lectura habitual de DataStore. La preferencia ya guardada y su
campo de copia manual `settingsBackgroundStyle` conservan sus nombres para
mantener la selección de v269. Sólido permanece como valor predeterminado.

Los colores particulares de las notas sirven como base para el efecto de sus
pantallas. Las páginas PDF, imágenes, vídeos y lienzos mantienen sus colores de
contenido; el estilo se dibuja detrás de la interfaz.

## XML

Los diseños normales usan el recurso común
`res/drawable/layout_xml_app_background.xml`. Este recurso apunta al ejemplo
neutro sólido predeterminado. Para revisar otro fondo en el editor de layouts,
su referencia puede cambiarse a uno de los vectores `layout_xml_settings_*` de
v269. Los tres diseños de demostración de paleta/estilo conservan sus ejemplos
explícitos.

Los 53 XML principales y la variante para pantallas amplias siguen siendo
jerarquías editables de vistas Android. La aplicación ejecuta Compose y las
vistas XML son representaciones de diseño que se mantienen por separado.

## Verificación

La auditoría global comprueba 10 archivos de pantallas, 11 puntos de dibujo del
fondo, ocho contenedores Scaffold transparentes con color de contenido explícito,
tres anfitriones del tema y las dos actividades PDF que comparten su anfitrión.
También comprueba 51 raíces XML con fondo común y tres muestras explícitas.

```sh
python3 tools/check_global_background_v270.py
python3 tools/check_layout_compose_sync_v263.py
python3 tools/check_settings_widgets_v259.py
python3 tools/check_native_xml_v268.py
```

Se incluyen regresiones JUnit sobre el uso de la preferencia en los modos básico,
avanzado y sin seleccionar, y sobre valores inválidos. No se ejecutaron las
pruebas JUnit, la compilación o el render Android: este entorno carece de SDK
Android, compilador Kotlin y distribución Gradle disponible.

En un dispositivo, elegir Degradado y Blur y recorrer las pantallas anteriores,
incluyendo abrir y cerrar las actividades PDF y de adjuntos. Comprobar después
Sólido y reiniciar la aplicación para confirmar la persistencia.
