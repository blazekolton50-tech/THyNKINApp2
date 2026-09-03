# Patsy Rive rig and Android contract — lineage 3.3.8

Status: implementation contract and authoring brief. **No production `.riv` file is present or claimed by this document.** The current generated transparent cutout remains a temporary UI fallback until the authored asset passes the acceptance checks below.

This contract follows Rive's current recommendation for new integrations: the editor state machine is controlled through data-bound View Model properties, not a growing collection of legacy state-machine inputs. On Android Compose, the eventual adapter will create/remember the file, artboard, state machine and View Model instance, pass the instance into the `Rive` composable, and write values through typed property paths.

## Locked visual source

The character artist must use the bundled real Patsy images for identity reference only. They must never be embedded or rendered in the app. Patsy has a grey/white shaggy coat, full white chest and muzzle, dark eyes, black nose, dark fluffy curved tail, and longer dark straight/low-hanging ear hair with soft feathered ends. The sticky-out ear tuft seen in weaker generations must be smoothed. Approved generated sheets control illustration treatment; the real photographs control identity.

The production asset must be a transparent skeletal/mesh character, not a square image, GIF, photo, or visible still-pose swap.

## Stable Rive names

These strings form the designer/developer ABI and must match exactly:

| Item | Name |
| --- | --- |
| Artboard | `PatsyAssistant` |
| Default state machine | `PatsyAssistantMachine` |
| View Model | `PatsyAssistantVM` |
| Default View Model instance | `Default` |

The View Model should use nested View Models named `motion`, `stage`, `head`, `ears`, `tail`, `face`, and `speech`. Android addresses their properties by forward-slash-delimited paths.

## Property ABI

| Path | Rive type | Range / enum | Direction | Purpose |
| --- | --- | --- | --- | --- |
| `motion/mode` | Enum | `idle`, `walk`, `sit`, `lie`, `jump`, `wave`, `point` | App → rig | Primary state |
| `motion/speed` | Number | 0..1 | App → rig | Walk cadence / transition energy |
| `motion/facing` | Number | -1 or 1 | App → rig | Face left/right without changing identity details |
| `motion/action_sequence` | Number | increasing integer | App → rig | Replays jump/wave/point even if enum is unchanged |
| `motion/point_x`, `motion/point_y` | Number | 0..1 artboard space | App → rig | Paw/eye target for contextual pointing |
| `motion/reduced` | Boolean | — | App → rig | Accessibility-safe motion branch |
| `stage/x`, `stage/y` | Number | 0..1 safe-screen space | App → rig | Mirrors host placement for anticipation and landing reactions |
| `stage/scale` | Number | 0.45..1.4 | App → rig | Resting, normal and focus scale |
| `head/look_x`, `head/look_y` | Number | -1..1 | App → rig | Eye/head gaze |
| `head/tilt` | Number | -1..1 | App → rig | Characteristic Patsy head tilt |
| `ears/left_drive` | Number | -1..1 | App → rig | Independent left-ear intention/pose |
| `ears/right_drive` | Number | -1..1 | App → rig | Independent right-ear intention/pose |
| `ears/physics_enabled` | Boolean | — | App → rig | Enables secondary ear follow-through |
| `tail/drive` | Number | -1..1 | App → rig | Tail angle/direction |
| `tail/energy` | Number | 0..1 | App → rig | Tail wag cadence/amplitude |
| `face/expression` | Enum | `neutral`, `cheeky`, `excited`, `curious`, `confused`, `concerned`, `proud`, `sleepy` | App → rig | Expression family |
| `face/expression_intensity` | Number | 0..1 | App → rig | Smooth blend amount |
| `face/blink_sequence` | Number | increasing integer | App → rig | Natural/reaction blink retrigger |
| `speech/talking` | Boolean | — | App → rig | Talking/rest transition |
| `speech/viseme` | Enum | `rest`, `a`, `e`, `i`, `o`, `u`, `mbp`, `fv`, `l`, `sz` | App → rig | Current mouth shape |
| `speech/viseme_intensity` | Number | 0..1 | App → rig | Mouth-shape blend |
| `speech/energy` | Number | 0..1 | App → rig | Subtle jaw/head/body speech emphasis |

Rive Enum spelling/case and property types are release ABI. Renaming or changing a type requires a contract-version change and matching Android update.

## Artboard and rig construction

- Use one 1000 × 1000 transparent artboard with generous overflow-safe space for ear tips, tail, jumps and paw pointing. Keep root/body origin around lower centre.
- Separate deformable meshes and bone chains for body, head, muzzle/jaw, each brow/eyelid, each eye/pupil, front paws, rear legs, tail and **each ear**.
- Left and right ears must not share a single transform or baked pose. Give each an independent 3–4 bone chain, independent weighting and its own target/constraint. Dark straight hair should bend as a soft sheet, with delayed tip follow-through rather than rotating as a rigid triangle.
- Add spring/secondary-motion behaviour independently to both ear chains. The drive property supplies deliberate expression pose; head acceleration and body landings supply follow-through. Offset stiffness/damping slightly between ears so they never look mechanically mirrored. Clamp motion to preserve Patsy's low-hanging ear silhouette.
- Tail gets its own curved bone chain and follow-through. Do not attach its tip rigidly to the torso or mirror ear timing.
- Mouth uses deformable muzzle/jaw shapes for the listed visemes. Preserve the white muzzle, black nose and face proportions through every shape. Visemes blend with expressions; they do not replace the whole head.
- Eyes, lids and brows combine gaze, blink and expression additively. Clamp pupils inside the eye shapes.
- Paw-pointing uses a constrained IK target derived from `motion/point_x/y`; supply authored left/right reach variants selected by target side. Never stretch a limb beyond its designed reach—turn the body and use gaze when the target is farther away.

## State-machine layers

Use independent layers so behaviours can combine cleanly:

1. **Base locomotion:** continuously looping idle ↔ walk ↔ sit ↔ lie transitions. Idle must include breathing, tiny weight shift and occasional non-synchronised ear/tail movement.
2. **One-shot body action:** jump, wave and point with entrance, hold where appropriate, and return to the correct base state. `motion/action_sequence` retriggers the action.
3. **Head and gaze:** look/tilt blend driven continuously; it must remain usable while walking or talking.
4. **Ear secondary motion:** two independent controls plus physics. Ears react to jump take-off/landing, turns, excitement, concern, curiosity and taps. Never leave both in one baked position across states.
5. **Tail:** energy/drive blend plus motion follow-through.
6. **Face:** expression blends, natural blinks and reaction blinks.
7. **Speech:** viseme, intensity and speech energy layered over the current expression/action.

Jump/wave/point are one-shots. Walk, idle, sit and lie are durable states. Transitions should use short blends without foot sliding or visible mesh popping. A walk command continues until a new durable state arrives; a one-shot returns to the previously selected durable state.

## Screen travel and Compose ownership

Android Compose owns Patsy's actual safe-screen position and size so she can travel between interface controls without being clipped by the artboard. `stage/x`, `stage/y`, and `stage/scale` mirror the host transform into the rig, allowing the state machine to anticipate direction, face the route and react on landing. They must not cause a second positional transform inside the Rive artboard.

Movement planning must respect system bars, display cut-outs, touch-target visibility and the current control's bounding box. Pointing targets use the same normalised safe-screen coordinates. Patsy must not intercept unrelated taps outside her own semantic hit area.

## Behaviour profiles

- **Idle:** breathing, tiny weight shift, blink variation, tail settle and independent micro ear responses. Avoid perpetual high-energy motion.
- **Walk/travel:** face route, real foot cadence, mild body bob and delayed ear/tail follow-through. Host transform duration should match walk distance and cadence.
- **Sit/lie:** authored body silhouettes and lowered stage scale; continue breathing/blink/ear life.
- **Jump:** anticipation, lift, apex, landing squash and settle. Both ears lag differently, then settle naturally.
- **Wave:** one paw waves; head/eyes acknowledge the user; free ear and tail react subtly.
- **Point:** choose reachable paw and body turn from target location; gaze reaches target before the paw, then holds without jitter.
- **Talking:** mouth follows timestamped visemes; speech energy adds subtle emphasis. On missing phoneme timing, use a conservative amplitude-driven open/close fallback, never random mouth flapping.
- **Expressions:** use brows, lids, eyes, head, ears and tail together at restrained intensities. Ear expression cues must remain asymmetric where natural.

## Lip-sync pipeline

The AI/audio layer supplies timestamped `PatsyRigViseme` cues and 0..1 intensity. Android advances cues on the audio playback clock, not frame-count assumptions. Coarticulate by briefly blending adjacent cues and return to `rest` at silence/end/cancel. `speech/talking=false` is mandatory on stop, failure or interruption. No network voice or AI service may be described as live until configured and tested.

## Reduced motion and lifecycle

- Read Android's accessibility/animation-scale preference and also honour the in-app reduced-motion setting. Either one enables `motion/reduced`.
- In reduced motion, do not walk or jump across the screen. Snap/cross-fade the host position, retain quiet breathing/blinking, limit ear/tail response to 20%, and keep speech mouth motion functional for comprehension.
- Pause the state machine when the app is not visible and no audio is playing. Release runtime objects with the owning lifecycle. Retain only the latest desired pose while the asset loads.
- If the `.riv` fails contract validation, show the existing transparent generated fallback and log missing contract names in diagnostics. Do not crash and do not silently pretend the full rig is active.

## Android adapter acceptance gate

Before the Rive asset replaces the fallback, the adapter and asset together must pass:

- Artboard/state machine/View Model/default instance all resolve by the exact names above.
- Every required property path exists with the correct Rive type and all enum values resolve.
- Idle, walk, sit and lie can run for 30 seconds each without mesh tears, frozen ears, visible seams or drift.
- Jump, wave and point retrigger three times consecutively and return cleanly to the prior durable state.
- Left/right ear drives produce visibly independent motion; landing and direction changes produce damped follow-through; ears settle instead of oscillating forever.
- Every expression works while idle, walking and talking without deforming identity.
- Every viseme is distinguishable at 0.5 and 1.0 intensity; stop/cancel returns to `rest`.
- Patsy travels/points to controls on compact and large screens, portrait and landscape, without covering the active control or leaving safe bounds.
- Reduced motion suppresses travel/jump and keeps calm idle, blink and readable lip motion.
- TalkBack exposes Patsy as one meaningful control/status element; internal rig shapes are not separate focus targets.
- Automated Android smoke tests retain the generated fallback for missing/invalid assets.

## Planned adapter sequence (not implemented here)

1. Add the current official Rive Android Compose dependency and initialise its runtime as required by that release.
2. Place the commissioned asset at `app/src/main/res/raw/patsy_assistant.riv`.
3. Load and remember the Rive file; select `PatsyAssistant`, `PatsyAssistantMachine`, and a `PatsyAssistantVM` instance.
4. Validate descriptors and property types before setting the ready state.
5. Implement `PatsyRigRuntimePort` with typed View Model property writes and bind that instance to the `Rive` composable/state machine.
6. Connect `PatsyRigCoordinator` to UI context, safe-screen travel, audio clock/visemes, lifecycle and accessibility preferences.
7. Run the acceptance gate on physical low/mid/high devices before removing the fallback.

Official concepts referenced: Rive Android data binding, Android state-machine playback, editor data-binding overview, and the editor's migration guidance favouring View Model properties for new work.
