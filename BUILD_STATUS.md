# Patsy1 Android Build Status

Status date: 29 August 2026  
Lineage: `3.3.8-patsy1`  
Project: `patsy1`

## Verified passes

- Final clean `assembleDebug`: **passes** with zero Kotlin, Compose, Gradle, manifest or resource compilation errors after the Rive runtime, secure auth/Owner UI, service-state additions, final `PatsyMotion` -> Rive control wiring, fail-closed feature bindings, and the Patsy AI/search coordinator. All 36 tasks executed and the verified build completed on 29 August 2026 in 4m 03s. The debug APK is at `app/build/outputs/apk/debug/app-debug.apk`.
- A final combined `testDebugUnitTest lintDebug` invocation was attempted again after the successful build, but the sandbox denied Gradle access to the installed Android SDK `package.xml`/Build Tools metadata before either task could start. This is recorded as **not run**, not as a source failure or a pass. The earlier pre-Rive lineage lint run passed; this archive still has no Android unit-test sources.
- The new provider-neutral DM, scheduling and Creation Studio coordinators compile independently with Kotlin 2.0.21 targeting JVM 17.
- Java source/target compatibility and Kotlin `jvmTarget` are all set to JVM 17.
- Gradle wrapper 8.10.2 and all wrapper scripts/config required by Android Studio are included.
- Debug APK installs and launches on the connected OUKITEL C68 as `com.patsy.app.debug`, alongside the existing differently signed `com.patsy.app` without overwriting its data.
- Welcome screen was visually inspected on the device. Official white Patsy logo #4 is used.
- The former square storyboard used as the live Patsy image was replaced with a generated, transparent, full-body Patsy cutout. Real photos remain reference-only assets and are not rendered by the app.
- Five mockups recovered from the **Animated Patsy Character** task are preserved under `docs/animated_patsy_reference/` with their role documented.
- Four additional generated rig-pose references (walk, paw point, airborne jump/ear motion and listening rest) are preserved under `docs/patsy_rig_pose_references/`. They are rigging references, not runtime pose-swap assets.
- Compose contains continuous breathing, bobbing, looking/turning, jumping and contextual movement behaviour plus explicit animation states.
- `PatsyMotion` now maps those states into the Rive ABI for motion, expression, gaze/head tilt, independent left/right ear drive, tail energy, pointing coordinates, jump/wave/point triggers, and deterministic talking-viseme amplitude. The generated transparent fallback remains active until a validated production `.riv` is bundled.
- The official Rive Android Compose runtime is integrated behind a strict validating host. It resolves only `res/raw/patsy_assistant.riv`, validates the stable artboard/state-machine/View Model/property ABI, and keeps the generated Patsy fallback visible whenever the asset is missing, loading, invalid or incompatible.
- Signup now calls the authentication boundary in the locked username/email -> password sequence. Login accepts username or email, password/reset/session restore/sign-out are service-backed, secret character buffers are erased after transport, and confirmation-email success is displayed only for provider-confirmed queued/sent states.
- The former any-credentials login and local Owner boolean were removed. Separate Owner Profile and Owner Tools surfaces appear only for current, server-verified, per-capability grants and fail closed on unavailable/denied/expired decisions.
- DMs, scheduling and Creation Studio now have authenticated, age-policy-aware coordinator states for loading, success, unavailable, denied and failure; they never manufacture provider success.
- AI/search now has the same authenticated, age-policy-aware coordination, input limits, request-id checks and honest provider states. A single provider-composition object defaults AI/search, DMs, scheduling and Creation Studio to explicit `NOT_CONFIGURED` results until production adapters replace them.
- The newer ChatGPT/Codex handoff, Animated Patsy, image/PawMoji, storage/backend and GitHub decisions were reconciled in `docs/CHATGPT_CONTINUITY_AUDIT_2026-08-29.md`. Reported cloud/backend work is preserved as a continuation requirement without being mislabelled as deployed source.

## Not production-complete / service-level blockers

- **Fully rigged Patsy animation:** the Rive cloud project `patsy1_3.3.8` now exists with the stable 1000x1000 `PatsyAssistant` artboard, `PatsyAssistantMachine`, and `PatsyAssistantVM`. Rive Agent reached its account usage limit before producing character layers, so no production `.riv` export exists yet. The transparent generated fallback continues to move but is not the finished Rive-quality rig. Natural walking, sitting, lying down, paw pointing/waving, blinking, independent ear/tail physics, expression morphing and speech-synchronised visemes still require the authored rig. See `docs/RIVE_PROJECT_STATUS_3.3.8.md` and `docs/PATSY_RIVE_RIG_CONTRACT_3.3.8.md`.
- **Authentication and Owner setup:** the UI and fail-closed integration are complete, but no production auth endpoint, database, session issuer or OWNER claim authority is configured. The app correctly refuses to create a pretend local account or grant Owner access from a username/local flag. The user's real Owner grant/profile therefore requires the secure backend authority to be configured.
- **Email:** no email provider/backend is configured or tested. The app must not claim a confirmation email was sent until the service confirms queueing/delivery.
- **AI/web search:** no production provider or secure proxy is configured or tested.
- **DMs, scheduling and Creation Studio providers:** UI/architecture exists, but production messaging, publishing, image-generation and video-generation services are not configured or tested.
- The connected device was simultaneously in use, so automated navigation beyond verified installation/launch was stopped to avoid interfering with the user's live phone session.
- A new device check could not be run after the final Rive wiring because the sandbox denied execution of the installed SDK `adb.exe`. The earlier installation/launch/welcome-screen smoke result above remains the latest device evidence; the final Rive wiring is compile-verified, not newly device-smoke-tested.

## Android Studio

Open the inner `patsy1` folder, select a JDK supported by Android Gradle Plugin 8.7.3 (JDK 17 or 21), sync, and run the `app` configuration. The installed Android Studio JBR on the build host reports Java 25.0.2 and is too new for this Kotlin/Gradle combination; this verification used installed JBR 21 while compiling app bytecode for JVM 17.
