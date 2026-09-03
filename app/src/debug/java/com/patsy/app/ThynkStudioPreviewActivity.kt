package com.patsy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import com.patsy.app.thynk.ThynkStudioScreen
import com.patsy.app.ui.finaldesign.FinalCharcoal
import com.patsy.app.ui.finaldesign.FinalWhite

/**
 * Debug-only direct launcher for visual QA of the real native THyNK Studio.
 *
 * This activity is intentionally absent from release builds. It bypasses app-shell navigation only
 * for local/debug visual inspection; it does not alter production authentication or authorization.
 */
class ThynkStudioPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = FinalCharcoal,
                    surface = FinalCharcoal,
                    primary = FinalWhite,
                ),
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize().background(FinalCharcoal),
                    color = FinalCharcoal,
                ) {
                    ThynkStudioScreen()
                }
            }
        }
    }
}
