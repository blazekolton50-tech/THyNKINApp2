# Patsy PawMoji Build Spec

## Purpose
Implement the PawMoji system as part of the Patsy app without changing the main Patsy companion design.

## Critical visual separation
- Main Patsy = realistic, high-grade moving AI companion.
- PawMojis = cartoon/sticker-style Patsy assets only.
- Never use PawMoji/cartoon Patsy as the main assistant character.

## Approved PawMoji starter set
### 20 emotions
1. Happy
2. Love
3. Excited
4. Sad
5. Shocked
6. Angry
7. Confused
8. Proud
9. Cheeky
10. Surprised
11. Cool
12. Trendy
13. Hello
14. Thankful
15. Laughing
16. Silly
17. Sleepy
18. Lovin' It
19. Party Time
20. Determined

### 10 actions
1. Working Out
2. Riding Bike
3. Coffee Time
4. Reading
5. Painting
6. Music Vibes
7. Big Love
8. Chill Time
9. Ignore Mode
10. Adventure

## Product behaviour
- PawMojis are usable in Patsy DMs, comments, reactions and other supported text/media entry points.
- Build a PawMoji picker with categories and search where appropriate.
- Build support for a Patsy custom keyboard/add-on on Android.
- The keyboard direction is black/dark with rainbow-letter styling and access to normal emojis as well as PawMojis.
- PawMojis should be sent as app assets/stickers/images where required. Do not pretend they are native Unicode emojis.
- Preserve transparent backgrounds for individual PawMoji assets where possible.
- Keep assets individually addressable by stable IDs/slugs.
- Allow future PawMoji packs to be added without changing the picker architecture.

## Asset pipeline
Create an asset manifest with at least:
- id
- slug
- display_name
- category (emotion/action/future)
- asset_path
- thumbnail_path
- accessibility_label
- version
- active flag

If final artwork is not present in the repository, do not generate fake placeholder artwork and call it final. Mark the asset state `NOT_CONFIGURED` or `ASSET_REQUIRED` and wire the UI so approved assets can be dropped in later.

## Keyboard requirements
- Android-first implementation.
- Normal typing must remain usable; PawMoji features must not break the standard keyboard path.
- Normal emoji access must remain available.
- PawMoji insertion should degrade gracefully in apps that cannot accept rich image/sticker content.
- Do not claim iOS keyboard support is complete unless it is genuinely implemented and tested.

## Remember Me distinction
The neon paw used for Remember Me / locked-in content is a separate UI symbol. Do not confuse it with PawMoji assets.

## First Codex task
1. Inspect the current repo for keyboard, sticker, emoji, reaction or PawMoji code/assets.
2. Report IMPLEMENTED / PARTIAL / MISSING / CONFLICTING.
3. Preserve any approved PawMoji assets already present.
4. Add the manifest/data model and picker architecture before wiring all entry points.
5. Test that normal keyboard/text input still works after PawMoji integration.
