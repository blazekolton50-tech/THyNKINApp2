package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DebugLauncherContractTest {
    @Test
    fun `debug preview activity must not override the real app launcher`() {
        val manifest = sequenceOf(
            File("src/debug/AndroidManifest.xml"),
            File("app/src/debug/AndroidManifest.xml"),
        ).firstOrNull(File::isFile)
            ?: error("Could not locate app/src/debug/AndroidManifest.xml")

        val xml = manifest.readText()

        assertTrue(
            xml.contains(".ThynkStudioPreviewActivity"),
            "The debug-only preview activity may remain available for explicit visual QA.",
        )
        assertFalse(
            xml.contains("android.intent.action.MAIN") ||
                xml.contains("android.intent.category.LAUNCHER"),
            "Debug manifest must not add its own launcher; FinalMainActivity is the single app launcher.",
        )
    }
}
