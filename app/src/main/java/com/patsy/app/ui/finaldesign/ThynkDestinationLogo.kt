package com.patsy.app.ui.finaldesign

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.patsy.app.R
import kotlin.math.roundToInt

/**
 * Renders one exact logo crop from the owner-supplied five-logo THyNK panel raster.
 * No logo text is recreated with fonts or vector approximations.
 */
@Composable
fun ThynkDestinationLogo(
    destination: ThynkPanelDestination,
    modifier: Modifier = Modifier,
) {
    val image = ImageBitmap.imageResource(R.drawable.thynk_panel_official)
    val density = LocalDensity.current
    Canvas(
        modifier = modifier.semantics {
            contentDescription = destination.visibleLabel
        },
    ) {
        val count = ThynkPanelDestination.entries.size
        val startX = image.width * destination.ordinal / count
        val endX = image.width * (destination.ordinal + 1) / count
        val srcOffset = IntOffset(startX, 0)
        val srcSize = IntSize(endX - startX, image.height)
        val dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())

        drawImage(
            image = image,
            srcOffset = srcOffset,
            srcSize = srcSize,
            dstSize = dstSize,
            filterQuality = FilterQuality.High,
        )
    }
}
