package com.patsy.app.thynk

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.R
import com.patsy.app.ui.finaldesign.FinalCard
import com.patsy.app.ui.finaldesign.FinalCharcoal
import com.patsy.app.ui.finaldesign.FinalMuted
import com.patsy.app.ui.finaldesign.FinalRainbow
import com.patsy.app.ui.finaldesign.FinalWhite
import com.patsy.app.ui.finaldesign.ThynkDestinationLogo
import com.patsy.app.ui.finaldesign.ThynkPanelDestination
import com.patsy.app.studio.StudioEditorState
import com.patsy.app.studio.StudioVideoPlayer
import com.patsy.app.studio.reduceStudioState

@Composable
fun ThynkStudioScreen(
    entry: ThynkStudioEntry = ThynkStudioEntry.IT,
    onEditingBoardChanged: (Boolean) -> Unit = {},
    onHome: () -> Unit = {},
) {
    var route by remember { mutableStateOf<ThynkWorkspaceRoute>(ThynkWorkspaceNavigation.initialRoute(entry)) }
    val chrome = ThynkWorkspaceNavigation.chrome(route)

    LaunchedEffect(entry) {
        route = ThynkWorkspaceNavigation.initialRoute(entry)
    }
    LaunchedEffect(route) {
        onEditingBoardChanged(ThynkWorkspaceNavigation.isEditingBoard(route))
    }
    DisposableEffect(Unit) {
        onDispose { onEditingBoardChanged(false) }
    }

    Column(Modifier.fillMaxSize().background(FinalCharcoal)) {
        ThynkHeader(
            chrome = chrome,
            destination = ThynkWorkspaceNavigation.topLogoDestination(route),
            onBack = { route = ThynkWorkspaceNavigation.back(route) },
            onHome = onHome,
        )
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (val current = route) {
                ThynkWorkspaceRoute.Hub -> ThynkHub(onOpen = { category ->
                    route = if (category.id == "music") {
                        ThynkWorkspaceRoute.Music("music-home")
                    } else {
                        ThynkWorkspaceRoute.Category(category.id)
                    }
                })
                is ThynkWorkspaceRoute.Category -> {
                    val category = ThynkStudioCatalog.categories.firstOrNull { it.id == current.categoryId }
                    if (category == null) {
                        InfoPanel("CATEGORY UNAVAILABLE", "This THyNK category is not configured.")
                    } else {
                        ThynkCategoryScreen(category) { item ->
                            if (category.id == "music") {
                                route = ThynkWorkspaceRoute.Music(musicPageForItem(item))
                            } else {
                                route = ThynkWorkspaceNavigation.openItItem(category.id, item)
                            }
                        }
                    }
                }
                is ThynkWorkspaceRoute.Tool -> {
                    if (current.categoryId == "fashion") {
                        FashionWorkspaceScreen(initialItem = current.item)
                    } else {
                        ThynkToolScreen(
                            categoryId = current.categoryId,
                            item = current.item,
                        )
                    }
                }
                is ThynkWorkspaceRoute.Tool -> ThynkToolScreen(
                    categoryId = current.categoryId,
                    item = current.item,
                )
                is ThynkWorkspaceRoute.Music -> {
                    if (current.pageId == "recording") {
                        ThynkRecordingWorkspaceScreen()
                    } else {
                        ThynkMusicScreen(
                            pageId = current.pageId,
                            onOpenPage = { route = ThynkWorkspaceRoute.Music(it) },
                        )
                    }
                }
                is ThynkWorkspaceRoute.Editor -> when (editorDestinationForPage(current.pageId)?.kind) {
                    ThynkEditorKind.DESIGN -> ThynkDesignEditorScreen()
                    ThynkEditorKind.VIDEO -> ThynkVideoEditorScreen()
                    null -> InfoPanel("EDITOR UNAVAILABLE", "This editor route is not configured.")
                }
            }
        }
    }
}

@Composable
private fun ThynkHeader(
    chrome: ThynkWorkspaceChrome,
    destination: ThynkPanelDestination,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 16.dp)) {
        if (chrome.showBack) {
            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Text("‹ Back", color = FinalWhite)
            }
        }
        if (!chrome.showOverflowHome) {
            ThynkDestinationLogo(
                destination = destination,
                modifier = Modifier.align(Alignment.Center).width(104.dp).height(48.dp),
            )
        }
        if (chrome.showOverflowHome) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(FinalRainbow, CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(FinalCharcoal)
                        .clickable { menuExpanded = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("•••", color = FinalWhite, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Home") },
                        onClick = {
                            menuExpanded = false
                            onHome()
                        },
                    )
                }
            }
        }
    }
    Box(Modifier.fillMaxWidth().height(2.dp).background(FinalRainbow))
}

@Composable
private fun ThynkHub(onOpen: (ThynkCategory) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(top = 18.dp, bottom = 8.dp)) {
                Text("THyNK", style = TextStyle(brush = FinalRainbow, fontSize = 32.sp, fontWeight = FontWeight.Black))
                Text("What are we making?", color = FinalMuted, fontSize = 15.sp)
            }
        }
        items(ThynkStudioCatalog.categories.chunked(2)) { rowCategories ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowCategories.forEach { category ->
                    ThynkCategoryCard(category, Modifier.weight(1f)) { onOpen(category) }
                }
                if (rowCategories.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        item {
            Column(
                Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 26.dp)
                    .border(1.dp, Color(0xFF34343A), RoundedCornerShape(22.dp))
                    .background(FinalCard, RoundedCornerShape(22.dp)).padding(16.dp),
            ) {
                Text("CONTINUE CREATING", color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Black)
                Text("Your THyNK drafts and projects", color = FinalWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
                Text("Project persistence restores here when the production project repository is connected.", color = FinalMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun ThynkCategoryCard(category: ThynkCategory, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier.height(150.dp).border(1.dp, Color(0xFF34343A), RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp)).background(FinalCard).clickable(onClick = onClick).padding(14.dp),
    ) {
        Text(category.label, color = FinalWhite, fontSize = 12.sp, lineHeight = 15.sp, fontWeight = FontWeight.Black)
        Text(category.description, color = FinalMuted, fontSize = 11.sp, lineHeight = 14.sp, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.weight(1f))
        Text("→", style = TextStyle(brush = FinalRainbow, fontSize = 20.sp, fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun ThynkCategoryScreen(category: ThynkCategory, onItem: (String) -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(category.label, color = FinalWhite, fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text(category.description, color = FinalMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
        }
        items(category.items.chunked(2)) { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { item ->
                    Box(
                        Modifier.weight(1f).height(92.dp).border(1.dp, Color(0xFF34343A), RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp)).background(FinalCard).clickable { onItem(item) }.padding(13.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(item, color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        item { Spacer(Modifier.height(18.dp)) }
    }
}

@Composable
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
    var editorState by remember { mutableStateOf(StudioEditorState.video(durationMs = 0)) }
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("VIDEO EDITOR", color = FinalWhite, fontSize = 25.sp, fontWeight = FontWeight.Black)
        Text(
            "Preview and edit a real selected video clip. No sample media is substituted.",
            color = FinalMuted,
            fontSize = 12.sp,
        )
        StudioVideoPlayer(
            sourceUri = editorState.sourceUri,
            state = editorState,
            onAction = { action -> editorState = reduceStudioState(editorState, action) },
            modifier = Modifier.fillMaxWidth(),
        )
        InfoPanel(
            "MEDIA",
            "No video is loaded yet. Android media picking is the next verified slice; this editor remains truthfully EMPTY until a real URI is selected.",
        )
    }
}

private fun musicPageForItem(item: String): String = when (item) {
    "CREATE MUSIC" -> "create-music"
    "AI MUSIC GENERATOR" -> "ai-music-generator"
    "RECORD" -> "recording"
    "TRACK EDITOR", "IMPORT AUDIO", "LOOPS & SAMPLES", "STEMS", "SOUND EFFECTS", "MY MUSIC" -> "track-editor"
    "MIXER" -> "mixer"
    "LYRICS & VOCALS" -> "lyrics-vocals"
    "MASTERING" -> "mastering"
    else -> "music-home"
}

@Composable
private fun ThynkMusicScreen(pageId: String, onOpenPage: (String) -> Unit) {
    val page = ThynkMusicCatalog.pages.firstOrNull { it.id == pageId } ?: ThynkMusicCatalog.pages.first()
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(top = 16.dp)) {
                Text("THyNK MUSIC", style = TextStyle(brush = FinalRainbow, fontSize = 13.sp, fontWeight = FontWeight.Black))
                Text(page.title, color = FinalWhite, fontSize = 28.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp))
                Text(page.subtitle, color = FinalMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp))
                if (page.providerState == "NOT_CONFIGURED") {
                    ProviderStateBadge()
                }
            }
        }
        if (page.id in ThynkMusicLockedWorkspaces.ids) {
            item { SharedArrangementStrip() }
        }
        when (ThynkMusicPageRenderer.kind(page.id)) {
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
            ThynkMusicRenderKind.BEATS_SAMPLER -> beatsSamplerItems()
            ThynkMusicRenderKind.PIANO_ROLL -> pianoRollItems()
            ThynkMusicRenderKind.AI_TOOLS -> aiToolsItems()
            ThynkMusicRenderKind.AUTO_TUNER -> autoTunerItems()
            ThynkMusicRenderKind.MASTERING -> masteringItems(onOpenPage)
            ThynkMusicRenderKind.VIDEO_HOME -> videoHomeItems(onOpenPage)
            ThynkMusicRenderKind.VIDEO_PLAYER -> videoPlayerItems()
            ThynkMusicRenderKind.VIDEO_EDITOR -> videoEditorItems()
            ThynkMusicRenderKind.EXPORT -> exportItems()
            null -> item { InfoPanel("PAGE UNAVAILABLE", "This THyNK Music route is not configured.") }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.musicHomeItems(onOpenPage: (String) -> Unit) {
    item {
        Text("Create. Record.\nEdit. Mix. Master.", color = FinalWhite, fontSize = 25.sp, fontWeight = FontWeight.Black)
        WaveformPreview(72, Modifier.fillMaxWidth().height(68.dp).padding(top = 12.dp))
    }
    item { SectionLabel("QUICK START") }
    item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MusicActionCard("♫", "CREATE MUSIC", Modifier.weight(1f)) { onOpenPage("create-music") }
            MusicActionCard("✦", "AI MUSIC", Modifier.weight(1f)) { onOpenPage("ai-music-generator") }
        }
    }
    item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MusicActionCard("●", "RECORD", Modifier.weight(1f)) { onOpenPage("recording") }
            MusicActionCard("↓", "IMPORT", Modifier.weight(1f)) { onOpenPage("track-editor") }
        }
    }
    item { SectionLabel("CREATOR") }
    item {
        val pages = listOf(
            "mixer" to "MIXER / MASTER CONSOLE",
            "effects" to "EFFECTS RACK",
            "equalizer" to "EQUALISER",
            "lyrics-vocals" to "VOCAL STUDIO",
            "dj-studio" to "DJ STUDIO",
            "beats-sampler" to "BEATS SAMPLER",
            "piano-roll" to "PIANO ROLL / MIDI",
            "ai-tools" to "AI TOOLS SUITE",
            "mastering" to "MASTERING & EXPORT",
        )
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pages.forEach { (id, label) -> MusicPill(label) { onOpenPage(id) } }
        }
    }
    item {
        InfoPanel("MY MUSIC", "No production music projects are stored yet. Finished and autosaved tracks will appear here after project storage is connected.")
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.createMusicItems(onOpenPage: (String) -> Unit) {
    item {
        var prompt by mutableStateOf("")
        Column {
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Describe the music you want", color = FinalMuted) },
                minLines = 4,
            )
            SectionLabel("STYLE")
            ChipRail(listOf("Lo-fi hip hop", "Afrobeat", "Trap", "R&B", "Gospel", "Amapiano", "Pop", "Rock"))
            SectionLabel("MOOD")
            ChipRail(listOf("Chill", "Happy", "Dark", "Energetic"))
            SectionLabel("DURATION")
            InfoPanel("03:00", "Duration control is prepared; generation remains provider-backed.")
            DisabledProviderButton("GENERATE MUSIC — NOT CONFIGURED")
            TextButton(onClick = { onOpenPage("track-editor") }, modifier = Modifier.fillMaxWidth()) {
                Text("Open track editor instead", color = FinalWhite)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.aiMusicItems() {
    item {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp)) {
            Box(Modifier.size(154.dp).border(3.dp, FinalRainbow, CircleShape), contentAlignment = Alignment.Center) {
                Text("♫", style = TextStyle(brush = FinalRainbow, fontSize = 58.sp, fontWeight = FontWeight.Black))
            }
            Text("PROVIDER NOT CONFIGURED", color = FinalWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp))
            Text("No fake generation percentage or audio is shown. Connect an approved music provider before this can create a real track.", color = FinalMuted, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 8.dp))
            DisabledProviderButton("GENERATE — NOT CONFIGURED")
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.recordingItems() {
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

@Composable
private fun SharedArrangementStrip() {
    val tracks = listOf(
        "Vocals" to Color(0xFFB65CFF),
        "Drums" to Color(0xFFFF8A38),
        "Bass" to Color(0xFF54E884),
        "Synth" to Color(0xFF55C8FF),
    )
    Column(
        Modifier.fillMaxWidth()
            .border(1.dp, Color(0xFF3B3B45), RoundedCornerShape(18.dp))
            .background(FinalCard, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("ARRANGEMENT / TIMELINE", color = FinalWhite, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            Text("120 BPM • 4/4", color = FinalMuted, fontSize = 9.sp)
        }
        Text("Shared MusicProject • empty lanes until real clips are loaded", color = FinalMuted, fontSize = 9.sp)
        tracks.forEach { (name, trackColor) ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).background(trackColor, CircleShape))
                Text(name, color = FinalWhite, fontSize = 9.sp, modifier = Modifier.width(52.dp).padding(start = 5.dp))
                Box(
                    Modifier.weight(1f).height(17.dp)
                        .border(1.dp, Color(0xFF3B3B45), RoundedCornerShape(5.dp))
                        .background(Color(0xFF17171C), RoundedCornerShape(5.dp)),
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.beatsSamplerItems() {
    item { SectionLabel("16-PAD SAMPLER") }
    item {
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            repeat(4) { rowIndex ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    repeat(4) { columnIndex ->
                        val pad = rowIndex * 4 + columnIndex + 1
                        MusicActionCard(
                            icon = pad.toString().padStart(2, '0'),
                            title = if (pad <= 4) listOf("KICK", "SNARE", "HAT", "CLAP")[pad - 1] else "PAD $pad",
                            modifier = Modifier.weight(1f),
                        ) { }
                    }
                }
            }
        }
    }
    item { SectionLabel("PERFORMANCE") }
    item { ChipRail(listOf("1 BAR", "2 BARS", "4 BARS", "QUANTISE", "SWING", "ROLL")) }
    item { SectionLabel("PAD SHAPING") }
    item { ChipRail(listOf("VELOCITY", "FILTER", "PITCH", "ATTACK", "DECAY", "TRIM", "REVERSE")) }
    item {
        InfoPanel(
            "SAMPLER ENGINE",
            "Pads and sequence controls are now part of the native workspace. No sample playback or generated clip is claimed until a real audio asset and sampler engine are connected.",
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.pianoRollItems() {
    item { SectionLabel("PIANO ROLL / MIDI") }
    item {
        var selectedNotes by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
        Column(
            Modifier.fillMaxWidth().background(FinalCard, RoundedCornerShape(18.dp)).padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            listOf("C5", "B4", "A4", "G4", "F4", "E4", "D4", "C4").forEachIndexed { row, note ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(note, color = FinalMuted, fontSize = 8.sp, modifier = Modifier.width(28.dp))
                    repeat(8) { column ->
                        val key = row to column
                        val selected = key in selectedNotes
                        Box(
                            Modifier.weight(1f).height(24.dp).padding(1.dp)
                                .border(1.dp, Color(0xFF35353D), RoundedCornerShape(3.dp))
                                .background(if (selected) Color(0xFF55C8FF) else Color(0xFF15151A), RoundedCornerShape(3.dp))
                                .clickable {
                                    selectedNotes = if (selected) selectedNotes - key else selectedNotes + key
                                },
                        )
                    }
                }
            }
        }
    }
    item { ChipRail(listOf("DRAW", "SELECT", "ERASE", "SNAP 1/16", "QUANTISE", "OCT -", "OCT +")) }
    item { SectionLabel("VELOCITY / INSTRUMENT") }
    item { ChipRail(listOf("VELOCITY", "CUTOFF", "RESONANCE", "ATTACK", "RELEASE", "PAN", "SEND")) }
    item {
        InfoPanel(
            "MIDI PROJECT STATE",
            "Grid edits are local native editor state. Instrument playback and recorded MIDI are only claimed after the verified audio/MIDI engine is connected to the shared MusicProject.",
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.aiToolsItems() {
    item { SectionLabel("AI TOOLS SUITE") }
    item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProviderToolRow("AI BEAT MAKER", "AI STEM SEPARATOR")
            ProviderToolRow("AI MASTERING", "AI LYRICS")
            ProviderToolRow("AI CHORD PROGRESSION", "AI VOCAL GENERATOR")
        }
    }
    item {
        InfoPanel(
            "PROVIDER BOUNDARY",
            "All six production AI tool areas are visible. None can fabricate a beat, stem, master, lyric, chord progression or vocal result while its provider is not configured.",
        )
    }
}

@Composable
private fun ProviderToolRow(left: String, right: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ProviderToolCard(left, Modifier.weight(1f))
        ProviderToolCard(right, Modifier.weight(1f))
    }
}

@Composable
private fun ProviderToolCard(title: String, modifier: Modifier = Modifier) {
    Column(
        modifier.height(104.dp)
            .border(1.dp, Color(0xFF41414A), RoundedCornerShape(18.dp))
            .background(FinalCard, RoundedCornerShape(18.dp))
            .padding(12.dp),
    ) {
        Text("✦", style = TextStyle(brush = FinalRainbow, fontSize = 20.sp, fontWeight = FontWeight.Black))
        Spacer(Modifier.weight(1f))
        Text(title, color = FinalWhite, fontSize = 10.sp, fontWeight = FontWeight.Black)
        Text("NOT_CONFIGURED", color = Color(0xFFFFC46B), fontSize = 8.sp, modifier = Modifier.padding(top = 3.dp))
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
            Slider(value = strength, onValueChange = { strength = it })
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
    item { ThynkAudioImportCard() }
    item {
        InfoPanel("My New Track", "Local editor state • imported audio plays from the real selected URI")
    }
    items(listOf("Vocals", "Beat", "Bass", "Keys", "FX")) { name ->
        TrackRow(name)
    }
    item {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("+ ADD TRACK", "SPLIT", "TRIM", "DELETE", "MORE").forEach { MusicPill(it) {} }
        }
    }
    item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MusicActionCard("≋", "MIXER", Modifier.weight(1f)) { onOpenPage("mixer") }
            MusicActionCard("EQ", "EQUALIZER", Modifier.weight(1f)) { onOpenPage("equalizer") }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.mixerItems(onOpenPage: (String) -> Unit) {
    item {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Vocals", "Beat", "Bass", "Keys", "FX").forEach { name -> MixerChannel(name) }
        }
    }
    item { InfoPanel("MASTER", "Local level controls only. Audio rendering is not claimed complete.") }
    item {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MusicActionCard("EQ", "EQUALIZER", Modifier.weight(1f)) { onOpenPage("equalizer") }
            MusicActionCard("FX", "EFFECTS", Modifier.weight(1f)) { onOpenPage("effects") }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.equalizerItems() {
    item {
        Column(Modifier.fillMaxWidth().background(FinalCard, RoundedCornerShape(22.dp)).padding(16.dp)) {
            Text("8-BAND EQUALIZER", color = FinalWhite, fontWeight = FontWeight.Bold)
            listOf("32", "64", "125", "250", "500", "1k", "4k", "16k").forEachIndexed { index, band ->
                var value by remember(index) { mutableFloatStateOf(if (index % 3 == 0) 0.65f else 0.45f) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(band, color = FinalMuted, fontSize = 11.sp, modifier = Modifier.width(36.dp))
                    Slider(value = value, onValueChange = { value = it }, modifier = Modifier.weight(1f))
                }
            }
        }
    }
    item { ChipRail(listOf("Flat", "Pop", "Rock", "Hip Hop", "Jazz", "EDM")) }
}

private fun androidx.compose.foundation.lazy.LazyListScope.effectsItems() {
    items(listOf("Reverb", "Delay", "Chorus", "Distortion", "Compressor", "Limiter")) { effect ->
        var enabled by remember(effect) { mutableStateOf(effect == "Reverb" || effect == "Delay" || effect == "Compressor" || effect == "Limiter") }
        Row(
            Modifier.fillMaxWidth().background(FinalCard, RoundedCornerShape(18.dp)).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(effect, color = FinalWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
    item { Text("Controls currently represent editor state only; a real audio engine must apply them before processed audio is claimed.", color = FinalMuted, fontSize = 12.sp) }
}

private fun androidx.compose.foundation.lazy.LazyListScope.lyricsItems() {
    item {
        var lyrics by mutableStateOf("Verse 1\nLate nights, city lights,\nChasing dreams, we ignite.\n\nChorus\nWe rise, we fall,\nWe break the wall.")
        OutlinedTextField(value = lyrics, onValueChange = { lyrics = it }, modifier = Modifier.fillMaxWidth(), minLines = 10, label = { Text("LYRICS", color = FinalMuted) })
    }
    item { ChipRail(listOf("AI Write", "Rhymes", "Improve", "Translate")) }
    item { InfoPanel("VOCALS", "Recording and lyric editing can be wired locally; AI writing/voice providers remain NOT_CONFIGURED.") }
}

private fun androidx.compose.foundation.lazy.LazyListScope.masteringItems(onOpenPage: (String) -> Unit) {
    item { SectionLabel("MASTER BUS") }
    item { MasterOption("Compressor", "Master dynamics control", true) }
    item { MasterOption("Limiter", "Prevent clipping", true) }
    item { MasterOption("Stereo Image", "Width and mono compatibility", true) }
    item { MasterOption("Loudness", "LUFS appears only when a genuine measurement path is connected", false) }
    item { DisabledProviderButton("AI MASTERING — NOT CONFIGURED") }
    item { SectionLabel("EXPORT FORMAT") }
    item { ChipRail(listOf("WAV", "MP3", "AAC", "FLAC")) }
    item { SectionLabel("QUALITY") }
    item { ChipRail(listOf("24-bit", "16-bit", "320 kbps", "Lossless")) }
    item { MasterOption("Export Stems", "Separate rendered track outputs", false) }
    item { DisabledProviderButton("EXPORT TRACK — NOT CONFIGURED") }
    item {
        TextButton(onClick = { onOpenPage("export") }, modifier = Modifier.fillMaxWidth()) {
            Text("Compatibility export settings", color = FinalMuted)
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.exportItems() {
    item { SectionLabel("FORMAT") }
    item { ChipRail(listOf("MP3", "WAV", "AAC", "FLAC")) }
    item { SectionLabel("QUALITY") }
    item { ChipRail(listOf("128 kbps", "320 kbps", "Lossless")) }
    item { MasterOption("Normalize", "Prepare consistent level", true) }
    item { MasterOption("Export Stems", "Separate track outputs", false) }
    item { MasterOption("Include Artwork", "Bundle project artwork", true) }
    item { DisabledProviderButton("EXPORT TRACK — NOT CONFIGURED") }
    item { Text("A successful UI choice is not treated as a file export. This button stays unavailable until the Android audio export pipeline produces and verifies a real output file.", color = FinalMuted, fontSize = 12.sp, lineHeight = 17.sp) }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = FinalMuted, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 12.dp, bottom = 7.dp))
}

@Composable
private fun ProviderStateBadge() {
    Box(Modifier.padding(top = 8.dp).border(1.dp, Color(0xFFFFA94D), RoundedCornerShape(12.dp)).padding(horizontal = 9.dp, vertical = 5.dp)) {
        Text("NOT_CONFIGURED", color = Color(0xFFFFC46B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DisabledProviderButton(text: String) {
    Button(
        onClick = {},
        enabled = false,
        modifier = Modifier.fillMaxWidth().padding(top = 14.dp).height(52.dp),
        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF34343A), disabledContentColor = FinalMuted),
        shape = RoundedCornerShape(18.dp),
    ) { Text(text, fontWeight = FontWeight.Black) }
}

@Composable
private fun InfoPanel(title: String, text: String) {
    Column(Modifier.fillMaxWidth().background(FinalCard, RoundedCornerShape(20.dp)).border(1.dp, Color(0xFF34343A), RoundedCornerShape(20.dp)).padding(15.dp)) {
        Text(title, color = FinalWhite, fontSize = 15.sp, fontWeight = FontWeight.Black)
        Text(text, color = FinalMuted, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun MusicActionCard(icon: String, title: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier.height(92.dp).border(1.dp, Color(0xFF3A3A42), RoundedCornerShape(18.dp)).clip(RoundedCornerShape(18.dp))
            .background(FinalCard).clickable(onClick = onClick).padding(12.dp),
    ) {
        Text(icon, style = TextStyle(brush = FinalRainbow, fontSize = 22.sp, fontWeight = FontWeight.Black))
        Spacer(Modifier.weight(1f))
        Text(title, color = FinalWhite, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun MusicPill(text: String, onClick: () -> Unit) {
    Box(Modifier.border(1.dp, Color(0xFF44444C), RoundedCornerShape(50)).clip(RoundedCornerShape(50)).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 10.dp)) {
        Text(text, color = FinalWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ChipRail(chips: List<String>) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        chips.forEach { chip -> MusicPill(chip) {} }
    }
}

@Composable
private fun WaveformPreview(bars: Int, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(bars) { index ->
            val height = (12 + ((index * 17) % 46)).dp
            Box(Modifier.weight(1f).height(height).background(if (index % 4 == 0) Color(0xFFFF4C98) else Color(0xFF8E4DFF), RoundedCornerShape(50)))
        }
    }
}

@Composable
private fun TrackRow(name: String) {
    Row(Modifier.fillMaxWidth().height(64.dp).background(FinalCard, RoundedCornerShape(16.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.width(70.dp)) {
            Text(name, color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("M  S", color = FinalMuted, fontSize = 9.sp)
        }
        WaveformPreview(34, Modifier.weight(1f).height(42.dp))
    }
}

@Composable
private fun MixerChannel(name: String) {
    var level by remember(name) { mutableFloatStateOf(0.68f) }
    Column(Modifier.width(70.dp).height(330.dp).background(FinalCard, RoundedCornerShape(18.dp)).padding(horizontal = 8.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, color = FinalWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text("PAN", color = FinalMuted, fontSize = 8.sp, modifier = Modifier.padding(top = 8.dp))
        Slider(value = level, onValueChange = { level = it }, modifier = Modifier.height(220.dp))
        Text(String.format("%.1f", (level - 1f) * 12f), color = FinalWhite, fontSize = 10.sp)
    }
}

@Composable
private fun MasterOption(title: String, subtitle: String, defaultOn: Boolean) {
    var enabled by remember(title) { mutableStateOf(defaultOn) }
    Row(Modifier.fillMaxWidth().background(FinalCard, RoundedCornerShape(18.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = FinalWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, color = FinalMuted, fontSize = 10.sp)
        }
        Switch(checked = enabled, onCheckedChange = { enabled = it })
    }
}

@Composable
fun LockedCameraHub() {
    LazyColumn(Modifier.fillMaxSize().background(FinalCharcoal).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("CAMERA", style = TextStyle(brush = FinalRainbow, fontSize = 28.sp, fontWeight = FontWeight.Black))
            Text("Capture into THyNK", color = FinalMuted, fontSize = 13.sp)
        }
        items(listOf("PHOTO", "VIDEO", "RECENT CAPTURES", "OPEN THyNK EDITOR")) { item ->
            InfoPanel(item, if (item == "OPEN THyNK EDITOR") "Use THyNK from the bottom navigation for creation and editing." else "Native capture is not configured in this preview build; no fake camera result is produced.")
        }
    }
}