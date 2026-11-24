# MyNotes - reglas ProGuard/R8
#
# Este archivo satisface la configuración release que referencia:
# proguardFiles(
#     getDefaultProguardFile("proguard-android-optimize.txt"),
#     "proguard-rules.pro"
# )
#
# Las bibliotecas AndroidX aportan sus reglas. Las siguientes protegen además
# los módulos cargados dinámicamente y las interfaces nativas de esta app.

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions,InnerClasses,EnclosingMethod


# ML Kit Subject Segmentation (módulo opcional de Google Play services).
# El release se minifica; estas clases participan en Binder/carga dinámica y no deben
# renombrarse ni eliminarse aunque R8 no vea todas las referencias de forma estática.
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_subject_segmentation.** { *; }
-keep class com.google.android.gms.common.moduleinstall.** { *; }

# PDFBox text search does not install the optional JPEG 2000 decoder.
# The port checks for this decoder at runtime; keep this warning narrow.
-dontwarn com.gemalto.jp2.**

