# Enlaces, actualizador y compartir

## Enlaces
`LinkPreviewRepository` obtiene/cacha metadatos; `LinkPreviewCard` los presenta. El flujo reciente permite que una URL deje de ocupar espacio visible en el texto tras resolverse la preview, manteniendo la referencia necesaria para la tarjeta.

## Compartir hacia MyNotes
`MainActivity` acepta `ACTION_SEND` con `text/plain`, lo que permite iniciar el flujo de nota desde navegadores y otras apps.

## Actualizaciones
`GitHubUpdateManager` consulta GitHub Releases, compara versión, descarga APK a almacenamiento temporal y lo entrega al instalador mediante FileProvider. En Android 8+ la autorización de instalar apps desconocidas sigue siendo decisión explícita del usuario/sistema.
