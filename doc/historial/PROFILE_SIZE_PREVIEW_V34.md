# MyNotes v34 - Vista previa del tamaño de perfil

- El avatar de la sección Profile ahora usa `localProfileSize` mientras se arrastra la barra `Avatar size`.
- La imagen se agranda o reduce inmediatamente dentro de Configuración.
- La imagen conserva recorte circular y `ContentScale.Crop`.
- El icono de perfil sin foto también escala proporcionalmente.
- El valor persistente continúa guardándose al terminar de arrastrar el slider, evitando escrituras continuas en DataStore.
- La pantalla principal sigue usando el valor persistido de `profileImageSize`.
