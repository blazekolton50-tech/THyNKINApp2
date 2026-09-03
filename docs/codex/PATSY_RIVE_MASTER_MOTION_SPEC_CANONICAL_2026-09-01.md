# Patsy Master Motion Specification — CANONICAL V1

Artboard: `PatsyAssistant`
State Machine: `PatsyAssistantMachine`
View Model: `PatsyAssistantVM`
Instance: `Default`

## Idle, Breathing & Blink
Patsy should feel continuously alive without looking mechanically looped. Use subtle chest/body breathing, tiny weight shifts, occasional independent ear/tail life and natural blink variation. Timing and amplitude are authoring values to validate visually from the approved 13 videos; they are not fixed ABI constants.

## Gaze & Head
Use `head/look_x`, `head/look_y`, and `head/tilt` (-1..1). Eyes may lead stronger head turns slightly. Internal authored angles may map from these normalized values, but Android-facing values remain normalized.

## Posture & Locomotion
`motion/mode` exact Enum:
- `idle`
- `walk`
- `sit`
- `lie`
- `jump`
- `wave`
- `point`

Durable: idle, walk, sit, lie.
One-shots: jump, wave, point.

## One-Shot Retrigger
`motion/action_sequence` is a monotonically increasing Number. Set the desired one-shot `motion/mode`, then increment the sequence. It is never an action ID.

## Point
Use `motion/point_x/y` 0..1. Gaze leads paw. Do not stretch limbs beyond authored reach.

## Speech & Visemes
`speech/talking` enables the overlay.
Exact `speech/viseme` Enum:
`rest`, `a`, `e`, `i`, `o`, `u`, `mbp`, `fv`, `l`, `sz`.
Use `speech/viseme_intensity` and `speech/energy` for blend/emphasis.

## Ears
Independent left/right chains, independent drives and secondary motion. Preserve Patsy's approved low-hanging silhouette and natural asymmetry.

## Tail
Own curved multi-segment chain with `tail/drive`, `tail/energy`, lag and natural settle.

## Expressions
Exact `face/expression` Enum:
`neutral`, `cheeky`, `excited`, `curious`, `confused`, `concerned`, `proud`, `sleepy`.

## Walk / Reposition / Return
Compose owns the real safe-screen translation. Rive animates gait and body response. `stage/x/y/scale` mirror host state only.

## Shrink / Expand
Compose owns real host size. `stage/scale` (0.45..1.4) mirrors/communicates it to the rig. No independent Rive camera zoom.

## Reduced Motion
`motion/reduced=true`: suppress cross-screen walk/jump, retain calm idle/blink and readable speech mouth motion, with reduced ear/tail response.

## Source Lock
ONLY the 13 videos in `PATSY_RIVE_APPROVED_VIDEO_SOURCES_2026-09-01.md` may define production motion. Any behavior inferred from older/unlisted videos is superseded.
