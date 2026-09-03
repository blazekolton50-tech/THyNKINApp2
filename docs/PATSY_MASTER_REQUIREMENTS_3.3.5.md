# PATSY 3.3.5 — COMPLETE MASTER REQUIREMENTS

## 1. Non-negotiable Patsy behaviour
- Patsy IS the AI. She is not a decorative mascot.
- When Patsy is visible she must be an active, animated companion.
- She must move naturally and purposefully around the available UI.
- She can move/paw/point toward the exact control she is explaining.
- She can jump across the screen to draw attention to the next place to go.
- Her position changes with context rather than being permanently fixed.
- Her mouth/talking state must animate while she speaks; production should use proper lip-sync or believable talking states.
- Eyes, head, ears and facial expression should react to conversation and events.
- She reacts to taps, successful actions, errors, waiting/loading, warnings and celebrations.
- Approved states include idle, happy, excited, thinking, talking, pointing, jumping, concerned/warning, encouraging, sleepy/resting and celebration.
- If a full real-time animation engine is not yet available, the architecture must support adding real Patsy animation frames/rig/lip-sync without redesigning screens.
- A placeholder dog/emoji is not the finished Patsy. The approved real Patsy asset must be inserted before release.

## 2. Owner identity and account flow
The owner's profile is separate from ordinary user profiles.

Owner profile:
- Display name: `Patsy`.
- Owner chooses a private username.
- Owner supplies/link a real email address.
- Entering the username and continuing moves immediately to password setup.
- Password setup is a separate secure step.
- After valid password creation, the account is created and the email is linked through the backend.
- A confirmation email is generated/sent by the configured secure email service.
- The app must not claim the email was sent unless the service confirms it was queued/sent.
- Owner authentication and role assignment must be server-side/secure; never rely on a local flag.

## 3. Normal user account flow
- Get Started -> experience mode -> profile -> username/email -> password immediately.
- Username availability must be checked by the backend.
- Email must be verified/linked through the backend.
- Account creation must genuinely create an account.
- Login supports username OR linked email + password.
- Remember Me/session restore may be offered securely.
- Password visibility control.
- Forgot-password/recovery flow.
- Clear duplicate username, invalid credentials, missing field, offline and server-error messages.
- Never log/display plaintext passwords.

## 4. Owner-only tools
Only the authenticated owner role can access:
- administration
- app configuration
- AI/provider configuration
- diagnostics
- moderation/safety controls
- security/audit tools
- app-management functions

Hidden UI is not security. Direct navigation, local storage changes, username knowledge or modified APK values must not grant owner access.

## 5. AI
Patsy is the primary AI/search interface.
- Natural language conversation.
- Web search when enabled.
- Explain results.
- Turn answers into actions.
- Never pretend an action happened when it did not.
- Confirm consequential/irreversible actions.
- Secure backend/proxy for provider calls.
- No API keys/secrets in APK/source.

## 6. DMs
- Private authenticated messaging.
- Conversation list and individual chats.
- Sending and message states.
- Private messages must never be exposed through public feed/search unless explicitly shared.

## 7. Creation Studio
- Real editor/canvas.
- Upload/import.
- AI image generation when provider configured.
- 10-second video generation when provider configured.
- Crop/resize/rotate/position.
- Brightness/contrast/saturation/exposure.
- Filters/effects.
- Text/stickers/shapes/overlays.
- Layers/order.
- Undo/redo/reset.
- Preview/export/save.
- Patsy command box understands natural requests such as "make this brighter", "remove the background", "turn this into a Reel", and "make a 10 second video".

## 8. Visual system
- Deep black/near-black background.
- Dark charcoal cards.
- Rainbow is a core accent system.
- Warm + cool rainbow hues: orange/yellow/green/cyan/blue/purple/pink.
- White/light typography.
- Rainbow gradients for active states, progress, highlights, borders/glows and selected controls.
- Pink/magenta as supporting Patsy/brand accent.
- Rounded controls.
- Paw prints/speech bubbles used functionally.
- Typography must distinguish the PATSY wordmark, handwritten guidance and clean UI text.
- Do not revert to the old plain white prototype.

## 9. Permission/memory rule
- Approved decisions become locked until explicitly changed.
- Patsy must not silently change approved brand assets, fonts, colours, wording, workflows or settings.
- A better alternative is suggested with the benefit explained; user approves before replacement.
- Material public/destructive/costly/account-setting actions require confirmation.
- Locked memory and working memory remain separate.

## 10. Brand/creator intelligence
Patsy should help with ideas, shortcuts, free options, scheduling, repurposing, brand checks, growth ideas, clean-up, encouragement and useful current platform/tool changes.

Voice:
- UK English
- warm, real, friendly, cool, relatable
- occasionally cheeky
- never patronising
- concise unless detail is requested

## 11. Safety/privacy
- 16+ mode.
- Under-16 protected mode.
- Protected mode when age is uncertain.
- Safety behaviour must actually change with mode.
- Protect credentials, sessions, DMs and user content.
- Minimise data collection.
- Secure secrets/tokens.
- HTTPS/TLS.
- Clear local-only vs cloud-synchronised data.

## 12. Release gate
Do not call the app finished until:
- onboarding works end-to-end
- username availability is real
- password setup works
- email linking/confirmation is real
- login works
- returning sessions work safely
- owner authorisation is genuinely protected
- Patsy is the AI
- Patsy visibly moves, points, jumps and talks with an approved real Patsy asset
- chat/AI/search work when configured
- DMs work when configured
- Creation Studio controls are functional
- offline/error states work
- target-device testing passes
- build has zero compilation errors
