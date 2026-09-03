# Patsy / Rive approved motion sources — LOCKED 2026-09-01

Status: **SAVE LOCK IN / SAVE MAIN APP**

This document supersedes every earlier Patsy/Rive motion-video list, donor-video list, Gemini motion-source map, or animation-source assumption.

## Non-negotiable rule

For production Patsy Rive motion study and authoring, **ONLY the 13 MP4 files listed below are approved motion-reference videos**.

Do not use any older Patsy clips, donor dog clips, stock clips, generated clips, prior Gemini source lists, or filenames not present here to define Patsy's production movement.

The videos are motion reference only. They do not replace the approved Patsy visual identity, do not become runtime video loops, and do not change the locked native Kotlin/Compose + Rive architecture or V1 ABI.

## Approved motion-reference videos

| # | Exact filename | Duration | SHA-256 |
|---|---|---:|---|
| 1 | `fluffy_dog_leap (1).mp4` | 10.237417 s | `d5b36e472e0b5153179583be41066684a984508f0458906ae2a77b9535ce995c` |
| 2 | `pleading_puppy_closeup.mp4` | 10.237417 s | `f9ec032b9e9aed55cfb9153a7583021635b010ec6eb1801d72539c7f36e28a3f` |
| 3 | `sassy_dog_exit.mp4` | 10.237417 s | `524d302151805c68418aefe3692cf636e05ec60648bb8c250683ce8f47e2c6b7` |
| 4 | `laughing_maltipoo_studio.mp4` | 10.237417 s | `df02d65d9ffb44f08ad7dc0e257a01fa176714d18aa8a00295a4fdb14526060d` |
| 5 | `talking_maltipoo_studio (1).mp4` | 10.237417 s | `6a77bd0b3719dab5f8ea952e4baeb30e7cbefd17bd014d2a4a0f1268925f0cb0` |
| 6 | `shaggy_dog_blinking.mp4` | 10.000000 s | `b95a2e5d24f1bb41280b580fac5894f20dc12f68e1cb90f30af0d27aa4c7af82` |
| 7 | `fluffy_dog_waving.mp4` | 10.000000 s | `a4f9706e898d3439f3a8f0543e7b42bb7c5908b1a59c3a0626c7d5b466a0267f` |
| 8 | `shaggy_dog_portrait_1.mp4` | 10.000000 s | `625099f5659acb2d73c9ce8fd36830de8665b0bc95c01b4dd439a822cbf3bdd6` |
| 9 | `happy_dog_smile (1).mp4` | 10.237417 s | `e38b97fdc09e1b11c97f56e5759c0effccf0c6d719ccc6d1cad489fa92173815` |
| 10 | `shy_dog_peekaboo.mp4` | 10.237417 s | `0fa134f96797619b04639cd4633a2e9beb8a8b3b3e6fb92d2f7baa7abba069e4` |
| 11 | `dog_pointing_studio.mp4` | 10.237417 s | `2af0d2156a431dca19183dd790c9823fa61684d052fe37c80a3df9b0202a1966` |
| 12 | `magical_dog_shrink.mp4` | 10.237417 s | `d96bdd1bc52c9011e0292e160bf14046c7f40a6760d1640d9d32e4610cffe657` |
| 13 | `shaggy_dog_portrait.mp4` | 10.237417 s | `45ea9928d4e703ffb029813df5a4593cde1dd3745dadd74802d1f8d05ec89d8c` |

## Supporting visual reference sheets supplied with this lock

These are images, **not motion sources**:

- `maltipoo_contact_sheet_1.jpg` — SHA-256 `a93bb8f840852f06584ab4419933f2d5a8e18c4de9d29e17ef012cbd884b7aad`
- `maltipoo_contact_sheet.jpg` — SHA-256 `879326af58b8e30d43856a5cb455db02258428665fc8b441da61d26719c22f35`

They may be used only as supporting pose/expression/appearance reference in conjunction with the existing approved Patsy identity rules.

## Superseded / forbidden motion inputs

Any earlier Gemini or Codex material that cites motion from a video not in the 13-file list above is **not authoritative and must be discarded or re-run**. In particular, previous generated motion maps must not silently retain unapproved donor clips such as spinning/head-tilt source videos that are absent from this manifest.

If a useful behaviour was inferred from an unapproved old clip, re-derive that behaviour from one of the approved videos or from the locked written Rive contract rather than carrying the old clip forward.

## Production constraints remain locked

- Approved realistic grey shaggy Patsy only.
- One Patsy normally visible.
- Transparent background and free movement.
- No GIF, sprite, static pose swapping, baked video loop, fake bobbing, HTML/JS/React/WebView animation substitute, or generic dog replacement.
- Native Kotlin + Jetpack Compose remains the app architecture.
- Rive uses the existing V1 ABI and exact names in `PATSY_RIVE_RIG_CONTRACT_3.3.8.md` and `PatsyRigContractV1.kt`.
- A real authored and validated `app/src/main/res/raw/patsy_assistant.riv` is still required before production Rive is claimed complete.
