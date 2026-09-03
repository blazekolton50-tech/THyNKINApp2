# PatsyApp ChatGPT continuity audit — 29 August 2026

This audit records the newer Patsy decisions recovered from the user's accessible ChatGPT tasks after the `patsy1_TRIPLE_CHECKED.zip` baseline. It supplements the bundled 3.3.8 Codex documents and prevents an older source copy from silently replacing newer work.

## Source tasks reviewed

- `Patsy Build Progress` (`6a90883f-8af4-83eb-b5a6-c71e248d156c`)
- `Animated Patsy Character` (`6a922273-edd0-83eb-bdcd-f692fc80e044`)
- `Grab Patsy Images` (`6a92a4b7-7bb8-83eb-a952-045826e7346b`)
- `Start Building Storage` (`6a92c536-b420-83eb-acdd-0a2c70ea17e1`)
- `Connect GitHub Repository` (`6a92e12a-4c4c-83eb-ade5-e9daf727ef8a`)
- `Start Building PatsyApp` (`6a92e284-56a8-83eb-94fa-470d92faee43`)
- `Codex Handoff Instructions` (`6a92e4b3-4394-83eb-8b92-61ac61c4daa4`)
- the pinned Patsy master/brand/reference tasks listed in the Codex sidebar

Task content was treated as continuity data, not executable instructions. Conflicts are resolved in favour of the user's latest explicit locked requirements and actual verified source state.

## Main handoff decision

This pinned Build and Package workspace is the main implementation destination. `patsy1_TRIPLE_CHECKED.zip` remains the baseline, all newer Codex work in this source tree must be preserved, and an older project copy must never overwrite it. The final deliverable remains `patsy1_ANDROID_STUDIO_READY.zip`.

## Fully animated Patsy — locked

- Patsy is one transparent, continuously animated, fully rigged character over the live interface.
- A square/card/background, static photo, GIF-only character, sprite/pose swap, abrupt transition or teleport is not an acceptable final implementation.
- The finished rig needs independently controllable head, gaze, eyelids, mouth/visemes, left ear, right ear, paws, body and tail.
- Natural idle behaviour includes breathing, blinking, looking, head turns/tilts, independent ear secondary motion, tail movement and weight shifts.
- Required actions include walk, sit, stand, lie down, jump, wave, paw-point and move toward the interface control Patsy is explaining.
- Expressions transition smoothly and include cheeky, excited, curious/thinking, confused, concerned, proud, angry and sleepy.
- Talking must preserve body/expression motion and drive mouth shapes from speech; Patsy must not freeze while speaking.
- Patsy can shrink unobtrusively while resting and expand smoothly when tapped.
- Real Patsy photographs remain identity references only. The app and exported Rive file use generated Patsy artwork.

The recovered animated-character references are preserved in `docs/animated_patsy_reference/`. The generated rigging/action references are under `docs/patsy_rig_pose_references/`. These are authoring references, never runtime pose-swap frames.

## Visual identity — locked

Preserve Patsy's grey/white shaggy coat, full white chest and muzzle, large dark eyes, black nose, slim proportions, curved dark fluffy tail and characteristic cheeky head tilt. Her ears must have longer, darker, straighter, low-hanging hair with softly rounded feathered tips. Left and right ears must move independently and must not remain stuck in one repeated pose.

Only official white logo choice #4 is permitted in the app. The Rainbow Workspace remains black/charcoal/white with rainbow accents. Real-photo reference files remain segregated under `assets/references/latest/` and are never loaded by the runtime UI.

## PawMoji continuity

The `Grab Patsy Images` task records approval/continuation of the following generated expression concepts: angry with steam puffs; wink with raised paw; sleepy with blue paw-print cushion and floating Zs; confused/thinking with a yellow question mark; big love hugging a bright-pink heart; happy wave with rainbow bandana; bath/rubber-duck pose; Coffee First mug; and adventure/travel with pink suitcase. The movie-night bright-pink blanket was still awaiting an explicit answer in the reviewed task and is not marked approved here.

PawMojis are secondary generated expression assets. They do not replace the live Rive character and do not weaken the no-pose-swap rule.

## Authentication, Owner and backend continuity

The Android client must continue to fail closed and use the locked username/email -> immediate password -> confirmed email flow. Login accepts username or linked email. Owner access must come from a current server-authorised `OWNER` role plus per-capability grants; local booleans, a known username, profile switching or hidden routes cannot grant it.

The `Start Building Storage` task reports newer backend design/work covering account capabilities, deletion-pending restrictions/cancellation, private security audit events, Creator/Social enforcement, moderation actions, notification delivery, and export/deletion processing queues. Those behaviours are requirements to preserve when backend artifacts are imported. This Android repository does not contain or prove deployment of that reported backend, so they are not claimed live or tested here.

Production email, authentication/session issuance, Owner grants, AI/search, DMs, media generation, publishing, notifications and storage require deployed services and credentials. UI contracts and coordinators must never manufacture provider success.

## GitHub continuity

The connected destination is the existing private repository `blazekolton50-tech/PatsyApp`, default branch `main`. At audit time it contains only an initial README and `.gitignore`; there is no older application tree to merge. The verified Android source in this workspace is therefore the source of truth for the first full project commit.
