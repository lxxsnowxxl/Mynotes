# Método y garantía documental

La documentación se generó **fuera de `app/`**. El proceso leyó los 72 archivos `.kt/.kts`, calculó SHA-256 y extrajo package, imports, declaraciones, referencias `R.*` y marcadores de estado/side effects.

No se modificó código para hacerlo coincidir con la documentación. Los hashes de `02_SHA256_CODIGO_INTACTO.txt` son la referencia binaria del árbol usado para esta revisión.

La revisión también analizó `AndroidManifest.xml`, recursos `res/`, localizaciones y referencias estáticas a recursos. Las descripciones de comportamiento sólo afirman lo que puede observarse en el fuente; cuando una intención no es demostrable se documenta el contrato observable.
