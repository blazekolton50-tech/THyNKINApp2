package com.patsy.app.thynk

enum class DriveDonorReadiness {
    NATIVE_PRESENT,
    DONOR_INTERACTIVE,
    DONOR_MOCK_ONLY,
    NOT_CONFIGURED,
}

data class ThynkMusicDriveCapability(
    val id: String,
    val sourceDriveFileId: String,
    val readiness: DriveDonorReadiness,
    val nativeTarget: String,
)

/**
 * Reconciles the 2 September THyNK Music Drive donors with the native Android app.
 *
 * Browser demos are donors only. Mock Next.js endpoints remain explicitly mock and must not
 * be surfaced as completed Android providers until a real native/service implementation is verified.
 */
object ThynkMusicDriveDonorContract {
    const val everyButtonDriveFileId = "1fZn4Covc2SFjLuVNQKlE_K9LaU-f-biu"
    const val fullStackDriveFileId = "1AQ7_vyjf0q2WFx8T5mXoATMe3x6GcBLo"
    const val studioProDriveFileId = "1-68My9MQmLuykAfClenu4r4pOrLPbhK3"

    val capabilities = listOf(
        ThynkMusicDriveCapability(
            id = "arrangement-drag",
            sourceDriveFileId = studioProDriveFileId,
            readiness = DriveDonorReadiness.DONOR_INTERACTIVE,
            nativeTarget = "track-editor",
        ),
        ThynkMusicDriveCapability(
            id = "mixer-faders",
            sourceDriveFileId = everyButtonDriveFileId,
            readiness = DriveDonorReadiness.DONOR_INTERACTIVE,
            nativeTarget = "mixer",
        ),
        ThynkMusicDriveCapability(
            id = "three-band-eq",
            sourceDriveFileId = everyButtonDriveFileId,
            readiness = DriveDonorReadiness.DONOR_INTERACTIVE,
            nativeTarget = "equalizer",
        ),
        ThynkMusicDriveCapability(
            id = "beat-pads",
            sourceDriveFileId = everyButtonDriveFileId,
            readiness = DriveDonorReadiness.DONOR_INTERACTIVE,
            nativeTarget = "beats-sampler",
        ),
        ThynkMusicDriveCapability(
            id = "dj-decks",
            sourceDriveFileId = studioProDriveFileId,
            readiness = DriveDonorReadiness.DONOR_INTERACTIVE,
            nativeTarget = "dj-studio",
        ),
        ThynkMusicDriveCapability(
            id = "autotune-controls",
            sourceDriveFileId = studioProDriveFileId,
            readiness = DriveDonorReadiness.DONOR_INTERACTIVE,
            nativeTarget = "auto-tuner",
        ),
        ThynkMusicDriveCapability(
            id = "microphone-recording",
            sourceDriveFileId = studioProDriveFileId,
            readiness = DriveDonorReadiness.NATIVE_PRESENT,
            nativeTarget = "NativeThynkAudioRecorder / ThynkRecordingWorkspaceScreen",
        ),
        ThynkMusicDriveCapability(
            id = "audio-import-playback",
            sourceDriveFileId = studioProDriveFileId,
            readiness = DriveDonorReadiness.NATIVE_PRESENT,
            nativeTarget = "ThynkAudioImportCard / Android media picker / Media3",
        ),
        ThynkMusicDriveCapability(
            id = "project-persistence",
            sourceDriveFileId = fullStackDriveFileId,
            readiness = DriveDonorReadiness.NATIVE_PRESENT,
            nativeTarget = "StudioProjectService / StudioPersistenceService",
        ),
        ThynkMusicDriveCapability(
            id = "stem-separation",
            sourceDriveFileId = fullStackDriveFileId,
            readiness = DriveDonorReadiness.DONOR_MOCK_ONLY,
            nativeTarget = "NOT_CONFIGURED until a verified separation provider is connected",
        ),
        ThynkMusicDriveCapability(
            id = "mixdown-export",
            sourceDriveFileId = fullStackDriveFileId,
            readiness = DriveDonorReadiness.DONOR_MOCK_ONLY,
            nativeTarget = "NOT_CONFIGURED until a verified renderer/export service is connected",
        ),
    )

    fun capability(id: String): ThynkMusicDriveCapability? = capabilities.firstOrNull { it.id == id }
}
