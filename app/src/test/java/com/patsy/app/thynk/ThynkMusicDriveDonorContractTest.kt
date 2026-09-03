package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ThynkMusicDriveDonorContractTest {
    @Test
    fun `2 September Drive donors keep exact source ids`() {
        assertEquals("1fZn4Covc2SFjLuVNQKlE_K9LaU-f-biu", ThynkMusicDriveDonorContract.everyButtonDriveFileId)
        assertEquals("1AQ7_vyjf0q2WFx8T5mXoATMe3x6GcBLo", ThynkMusicDriveDonorContract.fullStackDriveFileId)
        assertEquals("1-68My9MQmLuykAfClenu4r4pOrLPbhK3", ThynkMusicDriveDonorContract.studioProDriveFileId)
    }

    @Test
    fun `native persistence is preserved instead of replacing it with donor backend`() {
        assertEquals(
            DriveDonorReadiness.NATIVE_PRESENT,
            ThynkMusicDriveDonorContract.capability("project-persistence")!!.readiness,
        )
    }

    @Test
    fun `microphone recording is now a real native capability`() {
        val capability = ThynkMusicDriveDonorContract.capability("microphone-recording")!!
        assertEquals(DriveDonorReadiness.NATIVE_PRESENT, capability.readiness)
        assertTrue(capability.nativeTarget.contains("NativeThynkAudioRecorder"))

        val recordingPage = ThynkMusicCatalog.pages.first { it.id == "recording" }
        assertEquals("LOCAL_NATIVE", recordingPage.providerState)
    }

    @Test
    fun `audio import and playback are now real native capabilities`() {
        val capability = ThynkMusicDriveDonorContract.capability("audio-import-playback")!!
        assertEquals(DriveDonorReadiness.NATIVE_PRESENT, capability.readiness)
        assertTrue(capability.nativeTarget.contains("ThynkAudioImportCard"))
        assertTrue(capability.nativeTarget.contains("Media3"))
    }

    @Test
    fun `mock stem and export endpoints are never promoted as native complete`() {
        val providerDependentIds = listOf("stem-separation", "mixdown-export")
        assertTrue(
            providerDependentIds.all { id ->
                ThynkMusicDriveDonorContract.capability(id)?.readiness == DriveDonorReadiness.DONOR_MOCK_ONLY
            },
        )
        assertTrue(
            ThynkMusicDriveDonorContract.capabilities.none {
                it.readiness == DriveDonorReadiness.DONOR_MOCK_ONLY && it.nativeTarget.isBlank()
            },
        )
    }
}
