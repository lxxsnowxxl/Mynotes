package com.example.mynotes.ui.pdf

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.util.UUID
import org.json.JSONObject
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PdfProjectSummaryReaderTest {
    @Test fun nestedPagesAndEscapedNamesKeepTheSameSummary() {
        val points = List(2000) { "{\"x\":$it,\"y\":0.25}" }.joinToString(",")
        assertMatchesPreviousReader(
            """{
                "id":"project-1", "name":"Título \"PDF\"\n\u00f1",
                "modifiedAt":1790899200000,
                "pages":[
                    {"strokes":[{"points":[$points]}],"texts":[{"text":"nota"}]},
                    {"images":[{"file":"images/1.png"}]}
                ],
                "unknown":{"nested":[null,true,{"value":42}]}
            }""".trimIndent()
        )
    }

    @Test fun missingMetadataAndEmptyPagesKeepTheirDefaults() {
        listOf("{}", """{"pages":[]}""", """{"pages":[null,1,"page",{}]}""")
            .forEach(::assertMatchesPreviousReader)
    }

    @Test fun legacyMetadataUsesThePreviousConversions() {
        listOf(
            """{"id":42,"name":true,"modifiedAt":"1234","pages":null}""",
            """{"id":null,"name":null,"modifiedAt":null,"pages":{}}""",
            """{"name":{"title":"PDF"},"modifiedAt":1.9,"pages":3}""",
            """{"modifiedAt":1e3,"pages":[]}""",
            """{"modifiedAt":9223372036854775808,"pages":[]}""",
            """{'name':'legacy',/* saved by an older importer */'pages':[{}]}"""
        ).forEach(::assertMatchesPreviousReader)
    }

    @Test fun duplicatePropertiesAndLargeIntegerDatesMatch() {
        listOf(
            """{"id":"first","id":"last","pages":[{}],"pages":[],"modifiedAt":9007199254740993}""",
            """{"name":null,"name":"last","modifiedAt":-9223372036854775808}""",
            """{"modifiedAt":9223372036854775807,"pages":[{},{}]}"""
        ).forEach(::assertMatchesPreviousReader)
    }

    @Test fun malformedProjectsStillFailAndLeaveTheSourceUntouched() {
        listOf("", "[]", """{"pages":[{"strokes":[1,2}""", """{"name":"unfinished}""")
            .forEach { source ->
                withProject(source) { file ->
                    assertTrue(runCatching { previousSummary(file) }.isFailure)
                    assertTrue(runCatching { readPdfProjectSummary(file) }.isFailure)
                }
            }
    }

    @Test fun rereadingUsesTheCurrentFileWithoutStaleMetadata() {
        withProject("""{"name":"first","pages":[]}""") { file ->
            assertEquals("first", readPdfProjectSummary(file).name)
            val replacement = """{"name":"next!","pages":[]}"""
            val modifiedAt = file.lastModified()
            file.writeText(replacement)
            assertTrue(file.setLastModified(modifiedAt))
            assertEquals("next!", readPdfProjectSummary(file).name)
            file.writeText("""{"name":"first","pages":[]}""")
        }
    }

    private fun assertMatchesPreviousReader(source: String) {
        withProject(source) { file -> assertEquals(previousSummary(file), readPdfProjectSummary(file)) }
    }

    private fun previousSummary(file: File): PdfProjectRepository.Summary {
        val json = JSONObject(file.readText())
        val dir = requireNotNull(file.parentFile)
        return PdfProjectRepository.Summary(
            id = json.optString("id", dir.name),
            name = json.optString("name", "PDF"),
            modifiedAt = json.optLong("modifiedAt", dir.lastModified()),
            pageCount = json.optJSONArray("pages")?.length() ?: 1
        )
    }

    private fun withProject(source: String, block: (File) -> Unit) {
        val cacheDir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir
        val dir = File(cacheDir, "summary-${UUID.randomUUID()}").apply { mkdirs() }
        try {
            val file = File(dir, "project.json").apply { writeText(source) }
            val original = file.readBytes()
            block(file)
            assertArrayEquals(original, file.readBytes())
        } finally {
            dir.deleteRecursively()
        }
    }
}
