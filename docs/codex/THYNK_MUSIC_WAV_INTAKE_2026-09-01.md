# THyNK Music 100-WAV Intake — 2026-09-01

## Source

Owner-supplied archive:
`music-clips-100-original (1).zip`

Classification before byte inspection:
`ASSET_PRESENT / PRODUCTION_VALIDATION_REQUIRED`

## Byte/audio validation completed

The archive contains exactly **100 WAV files**.

All 100 files successfully parse as standard RIFF/WAV audio with:
- sample rate: **44,100 Hz**
- channels: **1 (mono)**
- sample width: **16-bit (2 bytes/sample)**
- compression: **PCM / NONE**
- parse errors: **0**

Observed clip durations range from **0.06 seconds** to **2.0 seconds**.

This establishes basic Android-decode-friendly PCM WAV characteristics at the file-format level. It does not by itself prove device playback across every target Android version, loudness quality, clipping/noise quality, or licensing/provenance.

## Duplicate-byte audit

SHA-256 comparison found three duplicate-content groups covering nine filenames:

1. Identical bytes:
- `045_bass_growl.wav`
- `055_bass_deep.wav`

SHA-256:
`9664674af2e9e398c7a86879101d2160b5c2d30409a72fe69c199b9eba540363`

2. Identical bytes:
- `081_fx_riser.wav`
- `084_fx_sweep.wav`
- `087_fx_reverse.wav`
- `090_fx_stutter.wav`

SHA-256:
`d72414d1e9e0f1b55a1732671f6e449c10df66daed2f481211164be1012a6f04`

3. Identical bytes:
- `082_fx_downer.wav`
- `085_fx_whoosh.wav`
- `088_fx_glitch.wav`

SHA-256:
`3d6acbf23913e3a82ab27ee2a217e8e9873252f7a381b2170998ce9aa23f39b1`

Do not silently treat those filenames as distinct sound assets in production. Decide whether to keep aliases intentionally or deduplicate the library while preserving any owner-facing names that are required.

## Example verified file metadata

`001_kick_sub.wav`
- SHA-256: `7be8e8fbb23e516504a4c4dd43ac0b80c291bc907dfd998cf49ca3a174453fde`
- size: 44,144 bytes
- duration: 0.5 s
- mono / 44.1 kHz / 16-bit PCM

`100_loop_jungle.wav`
- SHA-256: `70eb63d9cd7920a50c9845cd9ff198f05ce020cf5912a7fa630aeb096c15cbc8`
- size: 176,444 bytes
- duration: 2.0 s
- mono / 44.1 kHz / 16-bit PCM

## Production status

Current status:
`PARTIAL — BYTES AND WAV FORMAT VERIFIED; PROVENANCE/LICENSE + DEVICE AUDIO QA REQUIRED`

The archive is no longer merely an uninspected named asset. Its contained WAV bytes and basic format are verified. However, do **not** promote it to `VERIFIED` production library until all remaining gates below pass.

## Remaining gates

- provenance/originality evidence
- explicit license/permission suitable for Patsy App distribution and user-created exports
- intentional decision on the nine filenames sharing duplicate bytes
- loudness/peak/clipping/noise/quality review
- physical Android playback/import/timeline QA
- stable native asset/resource/storage mapping
- per-file or generated manifest checksums retained in the production intake
- confirmation that use in exported user projects does not introduce rights restrictions

## Integration rule

Preserve the supplied files; do not regenerate substitutes merely to remove duplicates. A duplicate must be resolved by an explicit intake decision.

THyNK Music may expose only files promoted by the native asset manifest. Provider-backed music generation, vocals, mastering and export remain separate truth gates and are not made complete by this local sample library.
