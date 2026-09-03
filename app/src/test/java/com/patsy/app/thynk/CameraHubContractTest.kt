package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Intentional RED contract for the native Camera foundation.
class CameraHubContractTest {
    @Test
    fun `camera hub exposes real native capture and import actions without fake AI completion`() {
        assertEquals(
            listOf(
                "take-photo",
                "record-video",
                "import-photo",
                "import-video",
                "open-editor",
                "templates",
                "ai-image",
                "ai-video",
                "projects",
            ),
            CameraHubContract.actions.map { it.id },
        )

        val nativeIds = CameraHubContract.actions
            .filter { it.availability == CameraActionAvailability.NATIVE }
            .map { it.id }
        assertEquals(
            listOf("take-photo", "record-video", "import-photo", "import-video"),
            nativeIds,
        )

        val aiActions = CameraHubContract.actions.filter { it.id.startsWith("ai-") }
        assertTrue(aiActions.isNotEmpty())
        assertTrue(aiActions.all { it.availability == CameraActionAvailability.NOT_CONFIGURED })
        assertFalse(CameraHubContract.actions.any { it.availability == CameraActionAvailability.FAKE_COMPLETE })
    }

    @Test
    fun `video capture handoff is accepted only for a real non blank uri`() {
        CameraMediaHandoff.clear()
        assertFalse(CameraMediaHandoff.offer("   ", CameraMediaKind.VIDEO))
        assertNull(CameraMediaHandoff.peek())

        assertTrue(CameraMediaHandoff.offer("content://patsy/capture/clip-1", CameraMediaKind.VIDEO))
        assertEquals(
            CameraMediaSelection("content://patsy/capture/clip-1", CameraMediaKind.VIDEO),
            CameraMediaHandoff.peek(),
        )

        CameraMediaHandoff.clear()
        assertNull(CameraMediaHandoff.peek())
    }

    @Test
    fun `camera video actions route to the shared THyNK video editor`() {
        assertEquals("video-editor", CameraHubContract.editorRouteFor("record-video"))
        assertEquals("video-editor", CameraHubContract.editorRouteFor("import-video"))
        assertNull(CameraHubContract.editorRouteFor("ai-video"))
    }
}
