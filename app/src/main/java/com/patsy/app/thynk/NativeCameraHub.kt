package com.patsy.app.thynk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.patsy.app.studio.StudioAction
import com.patsy.app.studio.StudioEditorState
import com.patsy.app.studio.StudioVideoPlayer
import com.patsy.app.studio.reduceStudioState
import com.patsy.app.ui.finaldesign.FinalCard
import com.patsy.app.ui.finaldesign.FinalCharcoal
import com.patsy.app.ui.finaldesign.FinalMuted
import com.patsy.app.ui.finaldesign.FinalRainbow
import com.patsy.app.ui.finaldesign.FinalWhite
import java.io.File

private enum class CameraHubPage {
    HUB,
    VIDEO_EDITOR,
}

@Composable
fun NativeCameraHub(
    onOpenThynk: () -> Unit = {},
) {
    val context = LocalContext.current
    var page by remember { mutableStateOf(CameraHubPage.HUB) }
    var lastSelection by remember { mutableStateOf(CameraMediaHandoff.peek()) }
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingVideoUri by remember { mutableStateOf<Uri?>(null) }
    var statusText by remember { mutableStateOf("Ready") }

    fun acceptSelection(uri: Uri, kind: CameraMediaKind, status: String) {
        if (CameraMediaHandoff.offer(uri.toString(), kind)) {
            lastSelection = CameraMediaHandoff.peek()
            statusText = status
            if (kind == CameraMediaKind.VIDEO) page = CameraHubPage.VIDEO_EDITOR
        }
    }

    val takePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = pendingPhotoUri
        pendingPhotoUri = null
        if (success && uri != null) {
            acceptSelection(uri, CameraMediaKind.PHOTO, "Photo captured")
        } else {
            statusText = "Photo capture cancelled"
        }
    }

    val recordVideo = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        val uri = pendingVideoUri
        pendingVideoUri = null
        if (success && uri != null) {
            acceptSelection(uri, CameraMediaKind.VIDEO, "Video captured")
        } else {
            statusText = "Video capture cancelled"
        }
    }

    val importPhoto = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) {
            statusText = "Photo import cancelled"
        } else {
            persistReadPermission(context, uri)
            acceptSelection(uri, CameraMediaKind.PHOTO, "Photo imported")
        }
    }

    val importVideo = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) {
            statusText = "Video import cancelled"
        } else {
            persistReadPermission(context, uri)
            acceptSelection(uri, CameraMediaKind.VIDEO, "Video imported")
        }
    }

    when (page) {
        CameraHubPage.HUB -> CameraHubContent(
            lastSelection = lastSelection,
            statusText = statusText,
            onAction = { action ->
                when (action.id) {
                    "take-photo" -> runCatching {
                        createCaptureUri(context, CameraMediaKind.PHOTO)
                    }.onSuccess { uri ->
                        pendingPhotoUri = uri
                        takePhoto.launch(uri)
                    }.onFailure {
                        statusText = "Unable to prepare photo capture"
                    }

                    "record-video" -> runCatching {
                        createCaptureUri(context, CameraMediaKind.VIDEO)
                    }.onSuccess { uri ->
                        pendingVideoUri = uri
                        recordVideo.launch(uri)
                    }.onFailure {
                        statusText = "Unable to prepare video capture"
                    }

                    "import-photo" -> importPhoto.launch(arrayOf("image/*"))
                    "import-video" -> importVideo.launch(arrayOf("video/*"))
                    "open-editor" -> {
                        if (lastSelection?.kind == CameraMediaKind.VIDEO) {
                            page = CameraHubPage.VIDEO_EDITOR
                        } else {
                            onOpenThynk()
                        }
                    }

                    "templates", "projects" -> onOpenThynk()
                    "ai-image", "ai-video" -> Unit
                }
            },
        )

        CameraHubPage.VIDEO_EDITOR -> CameraVideoEditor(
            selection = lastSelection?.takeIf { it.kind == CameraMediaKind.VIDEO },
            onBack = { page = CameraHubPage.HUB },
            onOpenThynk = onOpenThynk,
        )
    }
}

@Composable
private fun CameraHubContent(
    lastSelection: CameraMediaSelection?,
    statusText: String,
    onAction: (CameraHubAction) -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize().background(FinalCharcoal).padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(top = 18.dp)) {
                Text(
                    "CAMERA",
                    style = TextStyle(
                        brush = FinalRainbow,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                    ),
                )
                Text("Capture into THyNK", color = FinalMuted, fontSize = 13.sp)
            }
        }

        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(FinalCard, RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF34343A), RoundedCornerShape(20.dp))
                    .padding(15.dp),
            ) {
                Text("STATUS", color = FinalMuted, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text(statusText, color = FinalWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                lastSelection?.let { selected ->
                    Text(
                        "${selected.kind.name.lowercase().replaceFirstChar { it.uppercase() }} ready for THyNK",
                        color = FinalMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        items(CameraHubContract.actions.chunked(2)) { rowActions ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowActions.forEach { action ->
                    CameraActionCard(
                        action = action,
                        modifier = Modifier.weight(1f),
                        onClick = { onAction(action) },
                    )
                }
                if (rowActions.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun CameraActionCard(
    action: CameraHubAction,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val enabled = action.availability != CameraActionAvailability.NOT_CONFIGURED
    Column(
        modifier
            .height(108.dp)
            .border(1.dp, Color(0xFF34343A), RoundedCornerShape(20.dp))
            .background(FinalCard, RoundedCornerShape(20.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(13.dp),
    ) {
        Text(action.label, color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.weight(1f))
        when (action.availability) {
            CameraActionAvailability.NATIVE -> Text("ANDROID", color = Color(0xFF7CE6A4), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            CameraActionAvailability.INTERNAL -> Text("THyNK", color = FinalMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            CameraActionAvailability.NOT_CONFIGURED -> Text("NOT_CONFIGURED", color = Color(0xFFFFC46B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            CameraActionAvailability.FAKE_COMPLETE -> Text("UNAVAILABLE", color = FinalMuted, fontSize = 9.sp)
        }
    }
}

@Composable
private fun CameraVideoEditor(
    selection: CameraMediaSelection?,
    onBack: () -> Unit,
    onOpenThynk: () -> Unit,
) {
    var editorState by remember(selection?.uri) {
        mutableStateOf(StudioEditorState.video(durationMs = 0, projectName = "Camera video"))
    }
    val sourceUri = selection?.uri

    Column(
        Modifier.fillMaxSize().background(FinalCharcoal).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("‹ Camera", color = FinalWhite) }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onOpenThynk) { Text("Open THyNK", color = FinalWhite) }
        }
        Text("VIDEO EDITOR", color = FinalWhite, fontSize = 25.sp, fontWeight = FontWeight.Black)
        if (sourceUri.isNullOrBlank()) {
            Text("Capture or import a real video first.", color = FinalMuted, fontSize = 13.sp)
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = FinalWhite, contentColor = Color.Black),
            ) {
                Text("Back to Camera")
            }
        } else {
            StudioVideoPlayer(
                sourceUri = sourceUri,
                state = editorState,
                onAction = { action: StudioAction -> editorState = reduceStudioState(editorState, action) },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                "This is the shared THyNK Media3 preview/editor state. No sample clip is substituted.",
                color = FinalMuted,
                fontSize = 11.sp,
            )
        }
    }
}

private fun createCaptureUri(context: Context, kind: CameraMediaKind): Uri {
    val directoryType = if (kind == CameraMediaKind.PHOTO) {
        Environment.DIRECTORY_PICTURES
    } else {
        Environment.DIRECTORY_MOVIES
    }
    val extension = if (kind == CameraMediaKind.PHOTO) ".jpg" else ".mp4"
    val prefix = if (kind == CameraMediaKind.PHOTO) "patsy-photo-" else "patsy-video-"
    val captureRoot = context.getExternalFilesDir(directoryType)
        ?: File(context.filesDir, "captures").apply { mkdirs() }
    if (!captureRoot.exists()) captureRoot.mkdirs()
    val output = File.createTempFile(prefix, extension, captureRoot)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.capture-provider",
        output,
    )
}

private fun persistReadPermission(context: Context, uri: Uri) {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    }
}
