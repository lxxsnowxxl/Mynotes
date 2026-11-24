package com.example.mynotes.preview

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import com.example.mynotes.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/** Preview XML deliberately uses framework widgets, as does Theme.MyNotes (minSdk 24). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[24],qualifiers="w360dp-h800dp-mdpi")
class NativePreviewInflationTest {
    private fun views(view:View):Sequence<View> = sequence {
        yield(view)
        if(view is ViewGroup)for(i in 0 until view.childCount)yieldAll(views(view.getChildAt(i)))
    }
    private fun inflate(id:Int):View {
        val context=ContextThemeWrapper(RuntimeEnvironment.getApplication(),R.style.Theme_MyNotes)
        return LayoutInflater.from(context).inflate(id,FrameLayout(context),false)
    }
    @Test fun allNativePreviewsKeepFrameworkIconsAndWorkingTintsOnMinSdk() {
        val layouts=R.layout::class.java.fields.filter{it.name.startsWith("preview_")}
        assertTrue(layouts.size>=50)
        var icons=0;var tinted=0
        for(layout in layouts){
            val view=inflate(layout.getInt(null))
            for(node in views(view))if(node is ImageView){
                assertTrue("${layout.name}: ${node.javaClass}",node.javaClass==ImageView::class.java||node.javaClass==ImageButton::class.java)
                icons++
                if(node.imageTintList!=null)tinted++
            }
        }
        assertTrue(icons>250);assertTrue(tinted>200)
    }
    @Test @Config(qualifiers="w800dp-h1000dp-mdpi")
    fun tabletColumnVariantInflatesWithNativeIcons() {
        val icons=views(inflate(R.layout.preview_11_notes_three_columns)).filterIsInstance<ImageView>().toList()
        assertTrue(icons.isNotEmpty());assertTrue(icons.any{it.imageTintList!=null})
    }
}
