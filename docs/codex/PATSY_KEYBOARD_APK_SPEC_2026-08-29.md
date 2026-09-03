# Patsy Keyboard APK — Codex Build Specification

Build a separate installable Android keyboard component for Patsy/PawMojis as part of the Patsy project. The goal is to let a user install/enable the Patsy keyboard on their phone and use PawMojis plus normal emoji/text input from the system keyboard selector.

## Core requirement
- Produce an Android IME (InputMethodService) implementation suitable for packaging as an APK for testing.
- Keep this as a distinct keyboard module/app target so it can be installed/enabled independently while still sharing approved Patsy assets where practical.
- Do not claim PawMojis are native Unicode emoji. Treat them as sticker/image assets unless a platform-supported richer mechanism is available.

## Approved visual direction
- Keyboard background: black / deep charcoal.
- Letter keys: rainbow-colour visual treatment while preserving legibility.
- Normal keyboard functions must remain usable: letters, numbers/symbols, space, enter, backspace, shift/caps, punctuation, language/system keyboard switching where supported.
- Include access to normal emoji alongside PawMojis.
- PawMoji picker should use the approved cartoon Patsy assets only.
- Main realistic Patsy companion must NOT be used as keyboard emoji artwork.

## PawMoji behaviour
- Provide categories for approved PawMoji emotions/actions and make the system extensible for future packs.
- Selecting a PawMoji should insert/share it using the most compatible Android mechanism available for the target app.
- If the receiving app cannot accept image/sticker content, show a clear fallback rather than silently failing.
- Preserve transparency and image quality of PawMoji assets.
- Do not substitute generated placeholder art when an approved asset is missing; mark it `ASSET_REQUIRED`.

## Android requirements
- Implement the keyboard using Android IME APIs (`InputMethodService` or the appropriate current Android equivalent).
- Add the required service declaration, input-method XML metadata, manifest entries and permissions.
- Follow Android security/privacy expectations for keyboards: do not log, transmit or store typed text, passwords or sensitive input.
- Respect password/secure input fields and disable inappropriate suggestions/logging.
- No network permission unless a clearly documented feature genuinely requires it; keyboard input itself must work offline.
- Provide a first-run setup screen that explains how to enable the keyboard and switch to it in Android settings.
- Provide a button/deep link to the Android input-method settings where supported.
- Provide a keyboard-switch key where Android supports it.

## APK / build output
- Configure a debug APK build for device testing.
- Document the exact Gradle command to build the keyboard APK.
- Output/document the expected APK path.
- Keep release signing out of source control. Never commit signing passwords or private keystores.
- A production release APK/AAB should only be generated after explicit release/signing setup.

## Relationship to main Patsy app
- The main Patsy app may include a page that detects whether the Patsy keyboard is installed/enabled and guides the user through setup.
- PawMoji assets/categories should have one canonical manifest/data source where practical so the main app picker and keyboard remain in sync.
- Installing the main Patsy app must not silently enable the keyboard; Android requires the user to explicitly enable/select third-party keyboards.

## Codex workflow
1. Inspect the existing repository for any keyboard/IME/PawMoji work before creating files.
2. Report IMPLEMENTED / PARTIAL / MISSING / CONFLICTING.
3. Reuse existing approved work where possible.
4. Create the keyboard module only after the audit.
5. Build and test the debug APK.
6. Verify ordinary typing works before PawMoji insertion.
7. Test PawMoji sharing in several common text/image-capable app contexts and document limitations.

This keyboard is a real phone keyboard extension, not merely an in-app PawMoji picker.