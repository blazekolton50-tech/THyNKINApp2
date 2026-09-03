package com.patsy.app.ui.finaldesign

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ThynkPanelArtworkContractTest {
    @Test
    fun `official five-logo THyNK panel artwork is packaged as a real raster asset`() {
        val candidates = sequenceOf(
            File("src/main/res/drawable-nodpi/thynk_panel_official.webp"),
            File("app/src/main/res/drawable-nodpi/thynk_panel_official.webp"),
            File("src/main/res/drawable-nodpi/thynk_panel_official.jpg"),
            File("app/src/main/res/drawable-nodpi/thynk_panel_official.jpg"),
        )
        val asset = candidates.firstOrNull(File::isFile)
            ?: error("Missing official five-logo THyNK panel artwork")

        val bytes = asset.readBytes()
        assertTrue(bytes.size > 5_000, "Panel artwork must contain the supplied raster bytes, not a placeholder")

        val isJpeg = bytes.size >= 4 &&
            (bytes[0].toInt() and 0xFF) == 0xFF &&
            (bytes[1].toInt() and 0xFF) == 0xD8 &&
            (bytes[bytes.lastIndex - 1].toInt() and 0xFF) == 0xFF &&
            (bytes.last().toInt() and 0xFF) == 0xD9
        val isWebp = bytes.size >= 12 &&
            String(bytes, 0, 4, Charsets.US_ASCII) == "RIFF" &&
            String(bytes, 8, 4, Charsets.US_ASCII) == "WEBP"

        assertTrue(isJpeg || isWebp, "Panel artwork must be a real JPEG or WebP raster")
    }
}
