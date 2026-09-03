# THyNK Studio ULTRA MEGA — Export Core Design

**Source:** Owner-approved THyNK Studio v3.0 ULTRA MEGA donor specification.

## Goal

Integrate the first production-safe THyNK Studio engine slice into the existing THyNK-IN! Android app without replacing the current Compose shell, Camera, Supabase/auth/security, Patsy/Rive boundary, studio persistence, or newer Android/Media3 configuration.

## Integration boundary

- Existing app package/namespace remains `com.patsy.app`.
- New engine code for this slice lives under `com.patsy.app.studio.export`.
- Existing `com.patsy.app.studio` project/layer/revision/autosave services remain authoritative.
- Room is not introduced in this slice.
- Existing SDK and Media3 versions are not downgraded.
- GL/VFX, real footage decoding, LUTs, beat sync, captions and object tracking are separate later slices.

## Slice 1: export-core correctness

### Strict muxer lifecycle

`MuxerCoordinator` owns track registration and muxer start state.

Requirements:
- VIDEO and AUDIO formats may each be registered exactly once.
- `registerFormat()` fails if called after muxer start.
- Duplicate VIDEO format registration fails.
- Duplicate AUDIO format registration fails.
- Muxer starts exactly once, only after both required tracks are registered.
- `trackIndex(kind)` returns the exact index produced by the muxer track registration.
- A video-only mode is handled explicitly rather than weakening the two-track invariant.

### AAC input clock

Audio encoding must be independent from the video-frame loop.

`sampleCursor` is the single source of truth for audio presentation timestamps:

`ptsUs = sampleCursor * 1_000_000 / sampleRate`

For every AAC encoder input opportunity:
1. Drain available AAC output first.
2. Obtain an encoder input buffer.
3. Determine the input buffer capacity in PCM frames from byte capacity, channel count and PCM16 sample width.
4. Limit a single feed operation to at most 1024 PCM frames.
5. Request/mix/copy only the number of PCM frames that fit.
6. Queue PCM with PTS derived from the starting `sampleCursor`.
7. Advance `sampleCursor` by the number of frames actually queued.

The feeder must never queue more PCM bytes than the codec input buffer can contain.

### Audio EOS

After all real PCM frames have been queued:
- Compute `finalAudioPtsUs` from the final `sampleCursor`.
- Queue one zero-byte AAC input buffer with `BUFFER_FLAG_END_OF_STREAM` at `finalAudioPtsUs`.
- Continue draining encoder output until an output buffer containing `BUFFER_FLAG_END_OF_STREAM` is observed.
- Do not treat input EOS submission as equivalent to encoder completion.

### Testability

Android `MediaCodec` and `MediaMuxer` mechanics stay behind small interfaces/helpers so the sample clock, capacity math, muxer lifecycle and EOS state can be unit-tested on the JVM.

## Deferred slices

Not part of this implementation slice:
- procedural-audio replacement with MediaExtractor PCM,
- MediaExtractor video decode to OES SurfaceTexture,
- FBO ping-pong VFX,
- LUT application,
- Transformer timeline integration,
- Room cache,
- foreground export orchestration,
- captions,
- ML Kit object tracking.

## Acceptance criteria

- RED tests demonstrate missing strict muxer and sample-cursor behavior.
- GREEN implementation passes new tests plus existing unit tests.
- Debug APK builds.
- Release variant builds.
- No changes to Camera, auth/security, Owner tools, Patsy animation contracts, or existing Supabase studio persistence.
