package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.util.SizeF
import android.widget.RemoteViews
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mynotes.R
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Ejecutar en Android 13 y, para el fallback, también en API 24/30. */
@RunWith(AndroidJUnit4::class)
class WidgetResizeTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun preservesExactHostPairsDespiteDifferentApplicationOrientation() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        val options = legacyOptions().apply {
            putParcelableArrayList(
                AppWidgetManager.OPTION_APPWIDGET_SIZES,
                arrayListOf(SizeF(203f, 337f), SizeF(412f, 184f))
            )
        }
        for (orientation in listOf(Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_LANDSCAPE)) {
            val rendered = mutableListOf<WidgetSizeInfo>()
            val views = createSizedWidgetViews(options, orientation) { size ->
                rendered += size
                RemoteViews(context.packageName, if (size.isTall) R.layout.widget_focus_note_tall else R.layout.widget_focus_note)
            }
            assertEquals(listOf(WidgetSizeInfo(203, 337), WidgetSizeInfo(412, 184)), rendered)
            // Comprueba también que las variantes se puedan enviar al proceso del launcher.
            val parcel = Parcel.obtain()
            try {
                views.writeToParcel(parcel, 0)
                parcel.setDataPosition(0)
                assertEquals(views.layoutId, RemoteViews.CREATOR.createFromParcel(parcel).layoutId)
            } finally {
                parcel.recycle()
            }
        }
    }

    @Test
    fun skipsDuplicateAndNonPositiveHostSizes() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        val options = Bundle().apply {
            putParcelableArrayList(
                AppWidgetManager.OPTION_APPWIDGET_SIZES,
                arrayListOf(SizeF(220f, 280f), SizeF(0f, 280f), SizeF(220f, -1f), SizeF(220f, 280f))
            )
        }
        assertEquals(listOf(WidgetSizeInfo(220, 280)), renderSizes(options))
    }

    @Test
    fun fallsBackToLegacyRangesWhenLauncherOmitsSizes() {
        assertEquals(listOf(WidgetSizeInfo(200, 350)), renderSizes(legacyOptions()))
        assertEquals(
            listOf(WidgetSizeInfo(430, 180)),
            renderSizes(legacyOptions(), Configuration.ORIENTATION_LANDSCAPE)
        )
    }

    @Test
    fun toleratesEmptyAndInvalidSizeOptions() {
        assertEquals(listOf(WidgetSizeInfo(190, 180)), renderSizes(Bundle()))
        val options = Bundle().apply {
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, -20)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                putParcelableArrayList(AppWidgetManager.OPTION_APPWIDGET_SIZES, arrayListOf(SizeF(0f, 0f)))
            }
        }
        assertEquals(listOf(WidgetSizeInfo(190, 180)), renderSizes(options))
    }

    @Test
    fun fallsBackInsteadOfExceedingRemoteViewsVariantLimit() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        val options = legacyOptions().apply {
            putParcelableArrayList(
                AppWidgetManager.OPTION_APPWIDGET_SIZES,
                ArrayList((0..16).map { SizeF(200f + it, 350f) })
            )
        }
        assertEquals(listOf(WidgetSizeInfo(200, 350)), renderSizes(options))
    }

    @Test
    fun doesNotSelectTallLayoutBelowExistingHeightThreshold() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        val options = Bundle().apply {
            putParcelableArrayList(
                AppWidgetManager.OPTION_APPWIDGET_SIZES,
                arrayListOf(SizeF(220f, 259.9f), SizeF(220f, 260f))
            )
        }
        assertEquals(listOf(false, true), renderSizes(options).map { it.isTall })
    }

    @Test
    fun allFourProvidersReceiveResizeEventsThroughSharedCallback() {
        val parameters = arrayOf(
            android.content.Context::class.java,
            AppWidgetManager::class.java,
            Int::class.javaPrimitiveType!!,
            Bundle::class.java
        )
        for (provider in listOf(
            QuickNoteWidgetProvider::class.java,
            RecentNotesWidgetProvider::class.java,
            FavoritesWidgetProvider::class.java,
            FocusNoteWidgetProvider::class.java
        )) {
            assertEquals(
                ResponsiveNoteWidgetProvider::class.java,
                provider.getMethod("onAppWidgetOptionsChanged", *parameters).declaringClass
            )
        }
    }

    private fun renderSizes(options: Bundle, orientation: Int = Configuration.ORIENTATION_PORTRAIT): List<WidgetSizeInfo> {
        val result = mutableListOf<WidgetSizeInfo>()
        createSizedWidgetViews(options, orientation) { size ->
            result += size
            RemoteViews(context.packageName, R.layout.widget_focus_note)
        }
        return result
    }

    private fun legacyOptions() = Bundle().apply {
        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 200)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 430)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 180)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 350)
    }
}
