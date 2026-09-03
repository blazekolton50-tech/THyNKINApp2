# Patsy Rive Rig Acceptance Gate Tests — CANONICAL V1

1. **Exact Resolvability**: `PatsyAssistant`, `PatsyAssistantMachine`, `PatsyAssistantVM`, and `Default` resolve.
2. **Type & Enum Compliance**: Every V1 property path matches the exact locked type, range, and enum spelling/case.
3. **Idle Life**: Idle/sit/lie breathing and micro-movement look natural and non-mechanical when visually validated against approved references. Do not lock an arbitrary breathing frequency as ABI.
4. **Natural Blinking**: `face/blink_sequence` retriggers natural blinks. Blink timing may be tuned during authoring; no fixed millisecond timing is part of the V1 contract.
5. **Independent Ears**: Left/right ear chains operate independently with distinct stiffness, damping, and follow-through.
6. **Tail Follow-Through**: Multi-segment tail chain tracks `tail/drive` and `tail/energy` with inertial lag and natural settling.
7. **Point Reach & Gaze Lead**: `motion/mode=point` plus `motion/point_x/y`; gaze reaches the target before paw extension.
8. **Walk Cadence**: `motion/mode=walk`; grounded quadruped gait coordinates with Compose-owned host travel without foot sliding.
9. **Jump Mechanics**: `motion/mode=jump`; anticipation, lift, apex, landing compression and settle are clearly readable.
10. **Wave Mechanics**: `motion/mode=wave`; one paw waves with believable weight shift and clean return.
11. **One-Shot Retriggering**: Increment `motion/action_sequence` to replay jump/wave/point at least three times consecutively without stale state.
12. **Viseme Overlays**: `speech/talking=true`; cycle locked viseme enums and confirm mouth motion layers over expressions/actions without breaking placement.
13. **Seamless Transitions**: No mesh popping, visual seams, identity distortion or foot sliding.
14. **Reduced Motion**: `motion/reduced=true`; cross-screen travel/jump is suppressed while calm breathing, blink and readable mouth motion remain.
15. **Single Actor & Transparency**: Exactly one Patsy normally renders, with a transparent artboard/background.
16. **Identity Stability**: Approved realistic grey shaggy Patsy proportions, face, muzzle, ears and coat identity remain stable in every pose.
17. **Compose/Rive Transform Ownership**: `stage/x/y/scale` mirror host movement/scale and never create a second internal translation/zoom.
18. **Fallback Safety**: Missing/invalid `patsy_assistant.riv` leaves the safe fallback active and never reports a false Ready state.
19. **Accessibility**: TalkBack exposes Patsy as one meaningful semantic element, not internal rig parts.
20. **Physical Device Gate**: Do not remove the fallback or claim production completion until physical Android-device validation passes.
