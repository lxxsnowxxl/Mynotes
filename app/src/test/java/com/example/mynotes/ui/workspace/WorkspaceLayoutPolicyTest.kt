package com.example.mynotes.ui.workspace

import org.junit.Assert.*
import org.junit.Test

class WorkspaceLayoutPolicyTest {
    @Test fun tabletPanelsKeepMinimumWidthAfterSubtractingDivider() {
        for(width in listOf(840f,960f,1200f,1600f)) for(font in listOf(1f,1.3f,2f)) {
            if(!WorkspaceLayoutPolicy.useTwoPanes(width,800f,font))continue
            for(saved in listOf(.01f,.25f,.64f,.95f,Float.NaN)) {
                val fraction=WorkspaceLayoutPolicy.panelFraction(saved,width,font)
                val net=width-WorkspaceLayoutPolicy.dividerWidth
                assertTrue(net*fraction+.01f>=WorkspaceLayoutPolicy.minimumPanelWidth(font))
                assertTrue(net*(1-fraction)+.01f>=WorkspaceLayoutPolicy.minimumPanelWidth(font))
            }
        }
    }
    @Test fun largeTextUsesOnePaneUntilBothPanelsFit() {
        assertTrue(WorkspaceLayoutPolicy.useTwoPanes(840f,800f,1f))
        assertFalse(WorkspaceLayoutPolicy.useTwoPanes(840f,800f,2f))
        assertTrue(WorkspaceLayoutPolicy.useTwoPanes(1200f,800f,2f))
    }
    @Test fun narrowAndShortWindowsStaySinglePane() {
        assertFalse(WorkspaceLayoutPolicy.useTwoPanes(600f,800f,1f))
        assertFalse(WorkspaceLayoutPolicy.useTwoPanes(1200f,300f,1f))
        assertFalse(WorkspaceLayoutPolicy.useTwoPanes(Float.NaN,800f,1f))
    }
    @Test fun saveUsesAnAccessibleIconWhenLabelsDoNotFit() {
        assertTrue(WorkspaceLayoutPolicy.compactPrimaryAction(320f,1f))
        assertTrue(WorkspaceLayoutPolicy.compactPrimaryAction(600f,2f))
        assertFalse(WorkspaceLayoutPolicy.compactPrimaryAction(411f,1f))
    }
    @Test fun invalidRestoredFractionCannotBreakLayout() {
        assertEquals(.5f,WorkspaceLayoutPolicy.panelFraction(Float.NaN,240f,2f),.001f)
        assertEquals(.5f,WorkspaceLayoutPolicy.panelFraction(20f,0f,1f),.001f)
    }
}
