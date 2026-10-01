package com.example.mynotes.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "notes", indices = [
        /*
         * Se conserva el índice anterior para que Room valide
         * correctamente instalaciones que ya pasaron por la versión 4.
         */
        Index(value = ["priority", "createdAt"]),
        /*
         * El orden principal de la pantalla:
         * fijadas -> prioridad -> fecha.
         */
        Index(value = ["isPinned", "priority", "createdAt"]),
        /*
         * Acelera filtros Work / Personal.
         */
        Index(value = ["category"])])
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val color: String = "default",
    val priority: Int = 0,
    /*
     * Categorías usadas por los chips del diseño.
     *
     * Valores actuales:
     * "work"
     * "personal"
     */
    val category: String = "personal",
    /*
     * Favorita: aparece en el filtro Favorites.
     */
    val isFavorite: Boolean = false,
    /*
     * Fijada: permanece antes que el resto.
     */
    val isPinned: Boolean = false)
