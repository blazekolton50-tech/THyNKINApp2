# patsy1 — latest locked updates (28 Aug 2026)

This file supersedes conflicting older wording in legacy handoff docs.

## Project identity
- Android Studio project name: `patsy1`.
- Current consolidated lineage: 3.3.8.
- Java and Kotlin JVM targets must both be 17. This directly fixes the observed Java 1.8 vs Kotlin 21 mismatch.

## Official app logo — LOCKED
- Use logo choice **#4 only** from `logo_choice_4_master_reference.png`.
- White brush-script `Patsy` with the black paw inside the `a`.
- No pink wordmark. No rainbow wordmark. No substitute recreation unless owner explicitly changes this.

## Patsy visual character — LOCKED
- Real Patsy photos are IDENTITY REFERENCE ONLY inside the project. Do not display a real photograph of Patsy in the app unless explicitly instructed for a promo or other exceptional use.
- The in-app Patsy must be a generated/animated version based closely on the locked photo references.
- Identity anchors: grey/white shaggy coat; darker, straighter ears; white muzzle and full white chest; large dark expressive eyes; black nose; cheeky look; characteristic head tilt; darker fluffy curved tail.
- Ears: smooth the sticky-out tuft, slightly longer, darker/straighter than body fur, softly rounded/feathered tips. Some expressions can raise one ear.
- Tail: dark grey, fluffy/full, slightly more fluffy than early generations, held/curved up naturally.
- Approved generated references are the two strongest selected character treatments in the latest sheets; other poses must converge toward those rather than drifting.
- Speech bubbles: plain neutral/white/charcoal presentation. Never pink text. Emoji-style paw/pamoji is allowed where appropriate.

## Patsy is the AI — LOCKED
Patsy is not a mascot beside an AI feature. Patsy herself is the conversational AI/search/creation assistant.

She must be alive and interactive: move around the screen; walk/jump/travel to relevant controls; point with her paws; change position by context; move mouth while talking; support lip-sync/talking frames; react with eyes/head/face; celebrate; think; warn; rest; react to taps/loading/errors/success; guide the user visually without becoming distracting.

## Account creation / login
User flow: Get Started -> experience/safety mode -> username + email -> immediate password setup -> account creation -> email link/verification/confirmation -> login/workspace. Login supports username OR linked email + password. Include password recovery and secure session handling when backend is connected.

The confirmation email may only be reported as sent when the email provider/backend confirms it.

## Owner profile
- Owner display name: Patsy.
- Owner uses a private chosen username and linked email.
- Owner follows username/email -> immediate password setup -> confirmation flow.
- OWNER is a server-authorised role, separate from normal USER accounts. Username knowledge, profile switching, local flags or hidden routes must never grant owner privileges.

### Owner Profile surface
Profile identity, project/schedule/content summary, owner-access status, quick actions, Patsy AI chat, analytics, content library, inbox, hashtags/trends, drafts and owner sign-out.

### Owner Tools surface
Manage & Configure: Workspace Settings; App Settings & Preferences; Brand Kit & Assets; Integrations/API configuration (secrets server-side only).
Content & Creation: Creation Studio; Templates & Presets; Drafts Manager; Media Library.
Analytics & Growth: Analytics Dashboard; Growth Centre & Ideas; Hashtag Research; competitor/public trend research.
Users & Community: User Management & Roles; Community Settings; DM Management; Reports & Moderation.
Owner & Security: Owner Security Centre; Data & Privacy Controls; Activity Log & Audit; Backup & Restore.
System & Support: System Status & Health; Diagnostics & Logs; Support Centre; Updates & Releases.

## Storage / social retention
- Patsy Social feed supports statuses, images, questions and templates; video can remain disabled initially.
- Media retention target: 3 months unless saved/locked under profile limits.
- Profile saved-media limits: up to 30 videos and 100 pictures.
- DMs auto-delete after 3 days by default unless the user's permitted settings change retention.
- Prefer device/local storage for generated media where practical; do not promise impossible zero-server social storage.

## Production honesty
A screen or contract is not proof that a cloud service is live. Production email, AI/search, real-time DMs, generation, cross-device sync and secure owner authorisation require deployed services and credentials. Never fake success.
