# Patsy Creation Studio UX Contract — 3.3.9

Date: 29 August 2026
Status: Owner-approved direction; implementation contract for Codex

## Product direction

Creation Studio starts as a high-quality "Canva-lite" editor integrated with the single Patsy Brain/Memory/Awareness architecture. It must be useful without Patsy, but Patsy is visibly present as an optional interactive guide/assistant.

## First-entry onboarding

On the user's first Creation Studio visit, animated Patsy appears on screen in character with wording along the lines of:

> "Are you ready for this?! I'll show you the basics. And I'm over here if you need anything else."

Do not make this a static mascot/tutorial slide. Patsy should move/point around the actual Studio UI and introduce the basics in place.

Onboarding must cover:

- uploads
- opening/importing files
- a clearly accessible explainer for image/video locking and storage/retention behaviour
- canvas sizing
- main editor controls
- templates
- notes
- stickers
- fonts/text
- AI Generate
- Homework or Work entry according to the user's age tier

After onboarding, Patsy remains available but does not obstruct the editor. Users must be able to skip/replay the tour.

## Canvas and responsive editor layout

The central design area/canvas initially presents as a square design surface.

The first creation decision is SIZE.

Provide:

- custom dimensions
- common portrait/vertical social sizes
- common square social sizes
- common landscape/horizontal social sizes
- other standard useful presets as the product grows

Do not hard-code platform dimensions throughout UI; use a typed preset catalogue so dimensions can be maintained centrally.

Editor action/tool controls adapt to orientation/screen space:

- when layout permits, tools may sit beside the canvas
- when the chosen canvas/device orientation makes that poor UX, move them below/bottom-sheet style
- preserve the canvas as the primary focus

## Main tool families

Initial Studio navigation must include at least:

- Templates
- Notes
- Upload
- Stickers
- Fonts / Text
- AI Generate
- Homework (younger age tier) OR Work (appropriate older/adult tier)

The tool system should be extensible rather than one giant screen.

## Canva-lite editing baseline

Build a strong general editing foundation. Initial/near-term tool contract includes:

- select/move/resize
- crop
- rotate/flip
- undo/redo
- layers/order
- duplicate/delete
- opacity
- brightness/exposure
- contrast
- saturation
- warmth/temperature where supported
- highlights/shadows where supported
- blur
- sharpen/clarity where supported
- filters/presets
- text creation/editing
- font family, size, weight/style, alignment, spacing and colour
- shapes/basic graphic elements
- stickers
- PawMojis
- image/file uploads
- background controls
- AI Generate entry

Advanced AI editing (remove/replace objects, background removal/replacement, expand, etc.) should be provider-neutral capabilities and may remain NOT_CONFIGURED until an approved production provider exists. Never fake successful AI editing.

## Patsy in the editor

Patsy can proactively help when confidence is high and permissions allow. Examples:

- point out where a tool is during onboarding
- explain a control in simple language
- suggest a more suitable social-media canvas size
- notice likely text clipping/safe-area problems
- suggest an available tool that makes the user's intended task easier
- help write an AI generation prompt
- help younger users with age-appropriate Homework workflows

All actions remain user-controlled. Patsy does not silently alter the design.

## Templates and community sharing

Users can create and save their own designs/templates and may choose whether a creation is personal/private or shareable.

Every save/share flow must clearly support an ownership/visibility choice such as:

- Personal / private creation
- Share with others / community
- Publish as reusable template (where eligible)

Do not assume that saving a design grants Patsy/the service permission to publish or reuse it.

When a user explicitly chooses an eligible share/template option, the design can enter the community/template contribution pipeline. Shared template content must retain attribution/ownership metadata as required and pass applicable safety/moderation/licensing checks before being made broadly reusable.

The product may use approved user-shared designs to grow the template library, but private/personal creations must never be silently harvested into public templates.

Provide an owner/admin moderation path for community templates before broad production rollout.

## Age-aware Work / Homework

The same Studio architecture adapts by age policy:

- younger/restricted users see Homework-oriented assistance and child protections
- appropriate older/adult users see Work-oriented creation/productivity assistance

Age policy executes before personalization, AI tools, community sharing or external integrations. Do not allow personality/memory to weaken age restrictions.

## Image/video locking explainer

Onboarding must point to a dedicated accessible file/help surface explaining image/video locking, retention and what is stored locally vs by the service. This explanation must match the actual implemented storage behaviour and must not claim protections/retention that are not technically active.

## Save model

Keep separate concepts for:

- editable project
- exported media
- locked/retained media
- personal template
- community-shared template

Do not collapse these into one boolean `saved` state.

## Codex implementation guidance

Build this in phases:

1. Typed Studio project/canvas/preset/tool models.
2. Responsive square-first canvas shell and size picker.
3. Main tool navigation and local editor state.
4. Core non-AI editing operations with undo/redo.
5. Patsy first-run guided tour hooks using typed targets/events.
6. Upload/file flows and truthful storage/locking explainer.
7. Personal/private save model.
8. Community/template visibility and contribution model behind moderation/safety gates.
9. Age-aware Homework/Work surfaces.
10. Provider-neutral AI Generate/image/video editing adapters; keep unavailable capabilities explicitly NOT_CONFIGURED until backend providers exist.

## Still requiring owner decisions

Do not guess these yet:

- exact initial social-media size preset list
- exact first-run screen visual arrangement
- exact template categories shipped at launch
- exact Notes behaviour (canvas sticky notes vs project notes vs both)
- exact Homework feature set
- exact Work feature set
- whether autosave is always on and project/version retention rules
- collaboration/multi-user live editing
- template creator attribution/profile presentation
- whether community templates can be remixed and under what license/terms
- export formats/quality controls
- video timeline/audio/captions behaviour
- AI generation controls and fair-use/rate limits

These should be resolved with the owner before Codex treats them as final product behaviour.
