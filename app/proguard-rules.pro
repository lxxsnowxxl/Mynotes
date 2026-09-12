# MyNotes - reglas ProGuard/R8
#
# Este archivo satisface la configuración release que referencia:
# proguardFiles(
#     getDefaultProguardFile("proguard-android-optimize.txt"),
#     "proguard-rules.pro"
# )
#
# Por ahora no se requieren reglas personalizadas adicionales.
# Room, Compose, DataStore y AndroidX aportan sus propias reglas necesarias.

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions,InnerClasses,EnclosingMethod
