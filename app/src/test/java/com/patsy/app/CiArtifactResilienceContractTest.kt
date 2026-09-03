package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class CiArtifactResilienceContractTest {
    @Test
    fun `artifact quota cannot turn a successful Android build red`() {
        val workflow = workflow()
        val apkUpload = workflow.substringAfter("- name: Upload debug APK").substringBefore("- name: Publish direct APK preview")
        val zipUpload = workflow.substringAfter("- name: Upload Android Studio project ZIP")

        assertTrue(apkUpload.contains("continue-on-error: true"))
        assertTrue(zipUpload.contains("continue-on-error: true"))
    }

    @Test
    fun `active THyNK IN branch publishes a direct prerelease APK`() {
        val workflow = workflow()
        val publish = workflow.substringAfter("- name: Publish direct APK preview").substringBefore("- name: Create Android Studio project ZIP")

        assertTrue(publish.contains("chatgpt/device-launcher-thynk-panel-2026-09-02"))
        assertTrue(publish.contains("THyNK-IN-debug.apk"))
        assertTrue(publish.contains("thynk-in-debug-"))
        assertTrue(publish.contains("gh release create"))
    }

    private fun workflow(): String {
        val path = ".github/workflows/consolidation-ci.yml"
        val candidates = sequenceOf(File(path), File("../$path"))
        return candidates.firstOrNull(File::isFile)?.readText()
            ?: error("Could not locate $path")
    }
}
