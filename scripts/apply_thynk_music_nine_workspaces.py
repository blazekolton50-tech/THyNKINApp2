#!/usr/bin/env python3
from pathlib import Path

path = Path("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")
text = path.read_text()

# All nine workspaces share one immediately-accessible arrangement layer.
shared_anchor = """        when (ThynkMusicPageRenderer.kind(page.id)) {\n"""
if "page.id in ThynkMusicLockedWorkspaces.ids" not in text:
    if shared_anchor not in text:
        raise SystemExit("THyNK Music renderer anchor not found")
    text = text.replace(
        shared_anchor,
        """        if (page.id in ThynkMusicLockedWorkspaces.ids) {\n            item { SharedArrangementStrip() }\n        }\n        when (ThynkMusicPageRenderer.kind(page.id)) {\n""",
        1,
    )

# Add the three missing first-class native renderer bodies.
old_switch = """            ThynkMusicRenderKind.LYRICS_VOCALS -> lyricsItems()\n            ThynkMusicRenderKind.DJ_STUDIO -> djStudioItems()\n            ThynkMusicRenderKind.AUTO_TUNER -> autoTunerItems()\n"""
new_switch = """            ThynkMusicRenderKind.LYRICS_VOCALS -> lyricsItems()\n            ThynkMusicRenderKind.DJ_STUDIO -> djStudioItems()\n            ThynkMusicRenderKind.BEATS_SAMPLER -> beatsSamplerItems()\n            ThynkMusicRenderKind.PIANO_ROLL -> pianoRollItems()\n            ThynkMusicRenderKind.AI_TOOLS -> aiToolsItems()\n            ThynkMusicRenderKind.AUTO_TUNER -> autoTunerItems()\n"""
if "ThynkMusicRenderKind.BEATS_SAMPLER -> beatsSamplerItems()" not in text:
    if old_switch not in text:
        raise SystemExit("THyNK Music switch anchor not found")
    text = text.replace(old_switch, new_switch, 1)

# Replace the legacy creator rail with the exact nine owner-locked production workspaces.
old_pages = """        val pages = listOf(\n            \"track-editor\" to \"TRACK EDITOR\",\n            \"mixer\" to \"MIXER\",\n            \"equalizer\" to \"EQUALIZER\",\n            \"effects\" to \"EFFECTS\",\n            \"lyrics-vocals\" to \"LYRICS & VOCALS\",\n            \"dj-studio\" to \"DJ STUDIO\",\n            \"auto-tuner\" to \"AUTO-TUNER\",\n            \"mastering\" to \"MASTERING\",\n            \"video-home\" to \"VIDEO\",\n            \"export\" to \"EXPORT\",\n        )\n"""
new_pages = """        val pages = listOf(\n            \"mixer\" to \"MIXER / MASTER CONSOLE\",\n            \"effects\" to \"EFFECTS RACK\",\n            \"equalizer\" to \"EQUALISER\",\n            \"lyrics-vocals\" to \"VOCAL STUDIO\",\n            \"dj-studio\" to \"DJ STUDIO\",\n            \"beats-sampler\" to \"BEATS SAMPLER\",\n            \"piano-roll\" to \"PIANO ROLL / MIDI\",\n            \"ai-tools\" to \"AI TOOLS SUITE\",\n            \"mastering\" to \"MASTERING & EXPORT\",\n        )\n"""
if "\"beats-sampler\" to \"BEATS SAMPLER\"" not in text:
    if old_pages not in text:
        raise SystemExit("Legacy THyNK Music page rail not found")
    text = text.replace(old_pages, new_pages, 1)

# Mastering & Export must actually expose export choices on the same production workspace.
old_mastering = """private fun androidx.compose.foundation.lazy.LazyListScope.masteringItems(onOpenPage: (String) -> Unit) {\n    item { MasterOption(\"Auto Master\", \"Optimize your track\", false) }\n    item { MasterOption(\"Equalizer\", \"Tone & balance\", true) }\n    item { MasterOption(\"Stereo Widen\", \"Enhance stereo image\", true) }\n    item { MasterOption(\"Loudness\", \"Optimize volume\", true) }\n    item { MasterOption(\"Limiter\", \"Prevent clipping\", true) }\n    item { DisabledProviderButton(\"MASTER TRACK — NOT CONFIGURED\") }\n    item {\n        TextButton(onClick = { onOpenPage(\"export\") }, modifier = Modifier.fillMaxWidth()) { Text(\"Open export settings\", color = FinalWhite) }\n    }\n}\n"""
new_mastering = """private fun androidx.compose.foundation.lazy.LazyListScope.masteringItems(onOpenPage: (String) -> Unit) {\n    item { SectionLabel(\"MASTER BUS\") }\n    item { MasterOption(\"Compressor\", \"Master dynamics control\", true) }\n    item { MasterOption(\"Limiter\", \"Prevent clipping\", true) }\n    item { MasterOption(\"Stereo Image\", \"Width and mono compatibility\", true) }\n    item { MasterOption(\"Loudness\", \"LUFS appears only when a genuine measurement path is connected\", false) }\n    item { DisabledProviderButton(\"AI MASTERING — NOT CONFIGURED\") }\n    item { SectionLabel(\"EXPORT FORMAT\") }\n    item { ChipRail(listOf(\"WAV\", \"MP3\", \"AAC\", \"FLAC\")) }\n    item { SectionLabel(\"QUALITY\") }\n    item { ChipRail(listOf(\"24-bit\", \"16-bit\", \"320 kbps\", \"Lossless\")) }\n    item { MasterOption(\"Export Stems\", \"Separate rendered track outputs\", false) }\n    item { DisabledProviderButton(\"EXPORT TRACK — NOT CONFIGURED\") }\n    item {\n        TextButton(onClick = { onOpenPage(\"export\") }, modifier = Modifier.fillMaxWidth()) {\n            Text(\"Compatibility export settings\", color = FinalMuted)\n        }\n    }\n}\n"""
if "item { SectionLabel(\"MASTER BUS\") }" not in text:
    if old_mastering not in text:
        raise SystemExit("Mastering workspace anchor not found")
    text = text.replace(old_mastering, new_mastering, 1)

# Add the shared arrangement and missing production workspace bodies before the legacy auto-tuner.
insert_anchor = """private fun androidx.compose.foundation.lazy.LazyListScope.autoTunerItems() {\n"""
if "private fun androidx.compose.foundation.lazy.LazyListScope.beatsSamplerItems()" not in text:
    if insert_anchor not in text:
        raise SystemExit("Auto-tuner insertion anchor not found")
    additions = r'''@Composable
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

'''
    text = text.replace(insert_anchor, additions + insert_anchor, 1)

path.write_text(text)
print("Locked nine THyNK Music native workspace UI applied")
