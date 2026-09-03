package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ThynkBrandLabelContractTest {
    @Test
    fun `launcher app label uses exact THyNK-IN punctuation`() {
        val strings = source("app/src/main/res/values/strings.xml")
        assertTrue(strings.contains("<string name=\"app_name\">THyNK-IN!</string>"))
    }

    private fun source(path: String): String = sequenceOf(File(path), File("../$path"))
        .firstOrNull(File::isFile)
        ?.readText()
        ?: error("Could not locate $path")
}
