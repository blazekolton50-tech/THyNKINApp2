# Patsy Rig Pose References

These generated images support creation of Patsy's single continuous 2D Rive/Live2D-quality rig:

- `patsy_walk_side_reference.png` — side-profile walk anatomy and tail carriage.
- `patsy_paw_point_reference.png` — seated UI paw-point/wave range.
- `patsy_jump_ear_motion_reference.png` — airborne body, paw, tail and independent ear follow-through.
- `patsy_rest_listening_reference.png` — resting body and asymmetric listening ears.

They are design/rigging references only. Do not implement them as a pose-swapping slideshow. The rig must interpolate continuously between body, head, mouth, eye, tail and ear controls.

## Ear requirement

The left and right ears are independently deformable chains. Each needs root, mid and tip controls plus spring/secondary-motion behaviour. Ear motion must respond to head turns, vertical acceleration, landing, walking, listening and emotional state. Patsy's darker ear hair is straighter and lower-hanging than her body coat when relaxed; movement must preserve that identity rather than turning the ears into fixed fluffy triangles.

## Identity authority

Exact identity remains governed by the three real-photo files under `app/src/main/assets/references/latest/`. Those photos are reference-only and must never be rendered in-app. Generated pose references should be corrected by the rig artist wherever they diverge from the real ears, eyes, proportions, markings or tail.
