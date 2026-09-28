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


# ML Kit Subject Segmentation (módulo opcional de Google Play services).
# El release se minifica; estas clases participan en Binder/carga dinámica y no deben
# renombrarse ni eliminarse aunque R8 no vea todas las referencias de forma estática.
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_subject_segmentation.** { *; }
-keep class com.google.android.gms.common.moduleinstall.** { *; }
