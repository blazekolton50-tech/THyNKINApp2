package com.patsy.app.thynk

/** Stable render registry for every visible THyNK Music destination. */
internal enum class ThynkMusicRenderKind {
    HOME,
    CREATE_MUSIC,
    AI_MUSIC,
    RECORDING,
    TRACK_EDITOR,
    MIXER,
    EQUALIZER,
    EFFECTS,
    LYRICS_VOCALS,
    DJ_STUDIO,
    BEATS_SAMPLER,
    PIANO_ROLL,
    AI_TOOLS,
    AUTO_TUNER,
    MASTERING,
    VIDEO_HOME,
    VIDEO_PLAYER,
    VIDEO_EDITOR,
    EXPORT,
}

internal object ThynkMusicPageRenderer {
    fun kind(pageId: String): ThynkMusicRenderKind? = when (pageId) {
        "music-home" -> ThynkMusicRenderKind.HOME
        "create-music" -> ThynkMusicRenderKind.CREATE_MUSIC
        "ai-music-generator" -> ThynkMusicRenderKind.AI_MUSIC
        "recording" -> ThynkMusicRenderKind.RECORDING
        "track-editor" -> ThynkMusicRenderKind.TRACK_EDITOR
        "mixer" -> ThynkMusicRenderKind.MIXER
        "equalizer" -> ThynkMusicRenderKind.EQUALIZER
        "effects" -> ThynkMusicRenderKind.EFFECTS
        "lyrics-vocals" -> ThynkMusicRenderKind.LYRICS_VOCALS
        "dj-studio" -> ThynkMusicRenderKind.DJ_STUDIO
        "beats-sampler" -> ThynkMusicRenderKind.BEATS_SAMPLER
        "piano-roll" -> ThynkMusicRenderKind.PIANO_ROLL
        "ai-tools" -> ThynkMusicRenderKind.AI_TOOLS
        "auto-tuner" -> ThynkMusicRenderKind.AUTO_TUNER
        "mastering" -> ThynkMusicRenderKind.MASTERING
        "video-home" -> ThynkMusicRenderKind.VIDEO_HOME
        "video-player" -> ThynkMusicRenderKind.VIDEO_PLAYER
        "video-editor" -> ThynkMusicRenderKind.VIDEO_EDITOR
        "export" -> ThynkMusicRenderKind.EXPORT
        else -> null
    }
}
