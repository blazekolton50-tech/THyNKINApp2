from pathlib import Path

HOST = Path("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")

ALREADY_APPLIED_MARKERS = {
    "THyNK-IT tool routing": "ThynkWorkspaceNavigation.openItItem(category.id, item)",
    "THyNK-IT tool page host": "is ThynkWorkspaceRoute.Tool ->",
    "THyNK-IT tool screen": "private fun ThynkToolScreen(categoryId: String, item: String)",
    "recording route": '"RECORD" -> "recording"',
    "THyNK Music render registry": "when (ThynkMusicPageRenderer.kind(page.id))",
    "record quick-start page": 'onOpenPage("recording")',
    "music creator page rail": '"beats-sampler" to "BEATS SAMPLER"',
    "specialist THyNK Music page bodies": "private fun androidx.compose.foundation.lazy.LazyListScope.recordingItems()",
}


def replace_once(source: str, old: str, new: str, label: str) -> str:
    if new in source:
        return source
    marker = ALREADY_APPLIED_MARKERS.get(label)
    if marker is not None and marker in source:
        return source
    if old not in source:
        raise RuntimeError(f"Could not apply {label}: expected source block was not found")
    return source.replace(old, new, 1)


def main() -> None:
    source = HOST.read_text(encoding="utf-8")

    source = replace_once(
        source,
        '''                            } else {
                                editorPageForThynkItem(item)?.let { editorPage ->
                                    route = ThynkWorkspaceRoute.Editor(
                                        pageId = editorPage,
                                        categoryId = category.id,
                                    )
                                }
                            }
''',
        '''                            } else {
                                route = ThynkWorkspaceNavigation.openItItem(category.id, item)
                            }
''',
        "THyNK-IT tool routing",
    )

    source = replace_once(
        source,
        '''                is ThynkWorkspaceRoute.Music -> ThynkMusicScreen(
''',
        '''                is ThynkWorkspaceRoute.Tool -> ThynkToolScreen(
                    categoryId = current.categoryId,
                    item = current.item,
                )
                is ThynkWorkspaceRoute.Music -> ThynkMusicScreen(
''',
        "THyNK-IT tool page host",
    )

    source = replace_once(
        source,
        '''@Composable
private fun ThynkVideoEditorScreen() {
''',
        '''@Composable
private fun ThynkToolScreen(categoryId: String, item: String) {
    val category = ThynkStudioCatalog.categories.firstOrNull { it.id == categoryId }
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(item, color = FinalWhite, fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text(
                category?.label ?: "THyNK-IT",
                color = FinalMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        item {
            InfoPanel(
                "THyNK-IT WORKSPACE",
                "This destination is now routed and visible. Specialist native controls will be added here without replacing the existing Kotlin/Compose editor foundation.",
            )
        }
        item {
            InfoPanel(
                "PROJECT STATE",
                "No fake project, provider result or saved output is shown. Existing autosave/project services remain the source of truth when this tool gains specialist controls.",
            )
        }
    }
}

@Composable
private fun ThynkVideoEditorScreen() {
''',
        "THyNK-IT tool screen",
    )

    source = replace_once(
        source,
        '''    "TRACK EDITOR", "RECORD", "IMPORT AUDIO", "LOOPS & SAMPLES", "STEMS", "SOUND EFFECTS", "MY MUSIC" -> "track-editor"
''',
        '''    "RECORD" -> "recording"
    "TRACK EDITOR", "IMPORT AUDIO", "LOOPS & SAMPLES", "STEMS", "SOUND EFFECTS", "MY MUSIC" -> "track-editor"
''',
        "recording route",
    )

    source = replace_once(
        source,
        '''        when (page.id) {
            "music-home" -> musicHomeItems(onOpenPage)
            "create-music" -> createMusicItems(onOpenPage)
            "ai-music-generator" -> aiMusicItems()
            "track-editor" -> trackEditorItems(onOpenPage)
            "mixer" -> mixerItems(onOpenPage)
            "equalizer" -> equalizerItems()
            "effects" -> effectsItems()
            "lyrics-vocals" -> lyricsItems()
            "mastering" -> masteringItems(onOpenPage)
            "export" -> exportItems()
        }
''',
        '''        when (ThynkMusicPageRenderer.kind(page.id)) {
            ThynkMusicRenderKind.HOME -> musicHomeItems(onOpenPage)
            ThynkMusicRenderKind.CREATE_MUSIC -> createMusicItems(onOpenPage)
            ThynkMusicRenderKind.AI_MUSIC -> aiMusicItems()
            ThynkMusicRenderKind.RECORDING -> recordingItems()
            ThynkMusicRenderKind.TRACK_EDITOR -> trackEditorItems(onOpenPage)
            ThynkMusicRenderKind.MIXER -> mixerItems(onOpenPage)
            ThynkMusicRenderKind.EQUALIZER -> equalizerItems()
            ThynkMusicRenderKind.EFFECTS -> effectsItems()
            ThynkMusicRenderKind.LYRICS_VOCALS -> lyricsItems()
            ThynkMusicRenderKind.DJ_STUDIO -> djStudioItems()
            ThynkMusicRenderKind.AUTO_TUNER -> autoTunerItems()
            ThynkMusicRenderKind.MASTERING -> masteringItems(onOpenPage)
            ThynkMusicRenderKind.VIDEO_HOME -> videoHomeItems(onOpenPage)
            ThynkMusicRenderKind.VIDEO_PLAYER -> videoPlayerItems()
            ThynkMusicRenderKind.VIDEO_EDITOR -> videoEditorItems()
            ThynkMusicRenderKind.EXPORT -> exportItems()
            null -> item { InfoPanel("PAGE UNAVAILABLE", "This THyNK Music route is not configured.") }
        }
''',
        "THyNK Music render registry",
    )

    source = replace_once(
        source,
        '''            MusicActionCard("●", "RECORD", Modifier.weight(1f)) { onOpenPage("track-editor") }
''',
        '''            MusicActionCard("●", "RECORD", Modifier.weight(1f)) { onOpenPage("recording") }
''',
        "record quick-start page",
    )

    source = replace_once(
        source,
        '''        val pages = listOf("track-editor" to "TRACK EDITOR", "mixer" to "MIXER", "equalizer" to "EQUALIZER", "effects" to "EFFECTS", "lyrics-vocals" to "LYRICS & VOCALS", "mastering" to "MASTERING", "export" to "EXPORT")
''',
        '''        val pages = listOf(
            "track-editor" to "TRACK EDITOR",
            "mixer" to "MIXER",
            "equalizer" to "EQUALIZER",
            "effects" to "EFFECTS",
            "lyrics-vocals" to "LYRICS & VOCALS",
            "dj-studio" to "DJ STUDIO",
            "auto-tuner" to "AUTO-TUNER",
            "mastering" to "MASTERING",
            "video-home" to "VIDEO",
            "export" to "EXPORT",
        )
''',
        "music creator page rail",
    )

    source = replace_once(
        source,
        '''private fun androidx.compose.foundation.lazy.LazyListScope.trackEditorItems(onOpenPage: (String) -> Unit) {
''',
        '''private fun androidx.compose.foundation.lazy.LazyListScope.recordingItems() {
    item {
        WaveformPreview(72, Modifier.fillMaxWidth().height(82.dp))
    }
    item {
        InfoPanel(
            "MIC / INPUT",
            "The recording workspace is present, but native microphone capture is not claimed active until the verified Android audio capture pipeline is connected.",
        )
    }
    item { ChipRail(listOf("Lead Vocal", "Backing Vocal", "Voice Note", "Instrument")) }
    item { DisabledProviderButton("START RECORDING — NOT CONFIGURED") }
}

private fun androidx.compose.foundation.lazy.LazyListScope.djStudioItems() {
    item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MusicActionCard("A", "DECK A", Modifier.weight(1f)) {}
            MusicActionCard("B", "DECK B", Modifier.weight(1f)) {}
        }
    }
    item {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MixerChannel("Deck A")
            MixerChannel("Mic")
            MixerChannel("Deck B")
            MixerChannel("Master")
        }
    }
    item {
        InfoPanel(
            "DJ PERFORMANCE",
            "Deck layout, cue and mixer controls are visible. Real dual-deck playback, scratching and recording remain NOT_CONFIGURED until the native audio engine is verified.",
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.autoTunerItems() {
    item { SectionLabel("KEY / SCALE") }
    item { ChipRail(listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")) }
    item { ChipRail(listOf("Major", "Minor", "Chromatic")) }
    item {
        var strength by remember { mutableFloatStateOf(0.65f) }
        Column(Modifier.fillMaxWidth().background(FinalCard, RoundedCornerShape(20.dp)).padding(15.dp)) {
            Text("CORRECTION STRENGTH", color = FinalWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Slider(value = strength, onValueChange = { strength = it }, modifier = Modifier.weight(1f))
            Text("${(strength * 100).toInt()}%", color = FinalMuted, fontSize = 11.sp)
        }
    }
    item {
        InfoPanel(
            "AUTO-TUNER",
            "Controls are available for the workspace. Pitch correction is not claimed applied until a verified processing engine produces real processed audio.",
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.videoHomeItems(onOpenPage: (String) -> Unit) {
    item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MusicActionCard("▶", "VIDEO PLAYER", Modifier.weight(1f)) { onOpenPage("video-player") }
            MusicActionCard("✂", "VIDEO EDITOR", Modifier.weight(1f)) { onOpenPage("video-editor") }
        }
    }
    item {
        InfoPanel(
            "THyNK Music VIDEO",
            "Video stays inside the THyNK Music media family. Real selected media is handed to the existing native Media3 foundation; no sample clip is substituted.",
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.videoPlayerItems() {
    item {
        var playerState by remember { mutableStateOf(StudioEditorState.video(durationMs = 0)) }
        StudioVideoPlayer(
            sourceUri = playerState.sourceUri,
            state = playerState,
            onAction = { action -> playerState = reduceStudioState(playerState, action) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
    item {
        InfoPanel(
            "VIDEO PLAYER",
            "No video is selected yet. The player stays empty rather than inventing playback state.",
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.videoEditorItems() {
    item {
        var editorState by remember { mutableStateOf(StudioEditorState.video(durationMs = 0)) }
        StudioVideoPlayer(
            sourceUri = editorState.sourceUri,
            state = editorState,
            onAction = { action -> editorState = reduceStudioState(editorState, action) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
    item {
        InfoPanel(
            "VIDEO EDITOR",
            "The native Media3 editor surface is present. A real selected URI is required before timeline edits or playback can be truthfully shown.",
        )
    }
    item { ChipRail(listOf("Trim", "Split", "Transitions", "Subtitles", "Overlays", "Filters", "Audio")) }
}

private fun androidx.compose.foundation.lazy.LazyListScope.trackEditorItems(onOpenPage: (String) -> Unit) {
''',
        "specialist THyNK Music page bodies",
    )

    HOST.write_text(source, encoding="utf-8")
    print("THyNK page completeness applied: IT tool routes + all Music/Video page bodies")


if __name__ == "__main__":
    main()
