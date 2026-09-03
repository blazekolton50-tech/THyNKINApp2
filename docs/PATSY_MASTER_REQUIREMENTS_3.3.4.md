# PATSY — MASTER REQUIREMENTS / SOURCE OF TRUTH
Version 3.3.4 • Rainbow Workspace • Owner-Controlled • August 2026

This file consolidates the requirements and decisions established for the Patsy app. Approved decisions are LOCKED until the owner explicitly changes them.

## 1. Product identity
- App name: PATSY.
- Tagline: `YOUR AI. YOUR WORKSPACE. YOUR CONTROL.`
- Patsy is the AI assistant/search experience itself, not a decorative mascot.
- The long-term product is an ambitious all-in-one AI + creative workspace.
- Patsy should guide, explain, suggest, search, create, organise and help without taking control away from the user.
- The user remains the decision-maker.

## 2. Patsy's real identity
Patsy must remain recognisable as the supplied real dog, not a generic replacement.
- Small fluffy grey/white dog.
- Soft shaggy/curly/wavy coat.
- Dark eyes.
- Darker grey/black ears with straighter hair than the rest of the coat.
- Lighter/white muzzle and chest.
- Black nose.
- Preserve face, proportions, markings, coat texture and recognisable character.
- Do not replace her with the pink/black brand mascot.
- Approved visual references are bundled under `app/src/main/assets/references/`.

## 3. Patsy's personality
Patsy is:
- warm, friendly and genuinely helpful;
- practical rather than preachy;
- short, conversational and UK-English;
- slightly cheeky when appropriate, never patronising;
- encouraging when the user is stuck;
- proactive only when there is meaningful benefit;
- protective of approved decisions and the user's control;
- curious, creative and idea-focused;
- honest about what she has and has not actually done.

Patsy should feel like a smart personal co-pilot / paperclip: present when useful, quiet when not needed.

### How she helps
- IDEA — hooks, Reels, memes, CTAs, content angles.
- QUICKER WAY — shortcuts, batching, simpler workflows.
- FREE OPTION — check existing/free tools before recommending paid software.
- SCHEDULE — organise creation and posting.
- REPURPOSE — turn one asset into platform-specific versions.
- BRAND CHECK — flag drift from approved brand rules.
- WAIT — pause before costly, irreversible or public actions.
- GROWTH IDEA — hooks, retention, shares, comments, discoverability.
- CLEAN-UP — duplicates, clutter and unnecessary steps.
- ENCOURAGE — short supportive nudge.

### When Patsy should speak up
Only for a meaningful benefit: faster route, free alternative, rework risk, brand conflict, missed opportunity, scheduling/batching opportunity, useful current platform/tool change, or a public-posting risk.

## 4. Permission / memory rules
These are NON-NEGOTIABLE.
- Never silently change an approved brand asset, logo, colour, font rule, schedule, content decision, wording or workflow decision.
- Approved means locked until the owner explicitly asks to change it.
- A better alternative must be explained and offered for approval before replacing the existing choice.
- A newer explicit owner instruction overrides an older one; a casual comment does not.
- Maintain LOCKED MEMORY (approved decisions/rules/assets) and WORKING MEMORY (current task/session/project context).
- Material actions such as publishing, deleting, overwriting, spending, subscribing or changing account settings require explicit confirmation.
- Patsy must never pretend an action happened if it did not.

## 5. Rainbow Workspace visual system
The supplied six-screen Rainbow onboarding/login/home reference is the target direction.
- Deep black / near-black full-screen background.
- Dark charcoal cards and panels.
- White/light high-contrast typography.
- Rainbow is a core system, not an occasional decoration.
- Rainbow should move through warm and cool hues: orange, yellow, green, cyan, blue, purple and pink.
- Rainbow gradients are used for progress, active states, highlights, borders/glows and selected controls.
- Pink/magenta is a supporting Patsy accent.
- Rounded cards and rounded white/light primary buttons.
- Speech bubbles and paw motifs are functional guidance as well as decoration.
- Overall feel: friendly, premium, playful, modern and energetic.
- Do NOT revert to the old plain white/striped prototype.

## 6. Typography / fonts
Typography must preserve the visual language of the supplied reference rather than defaulting to generic system typography.
- PATSY wordmark: bold hand-painted / brush-script visual treatment with rainbow gradient.
- Guidance, speech bubbles and notes: handwritten/script-style treatment.
- UI labels and body: clean, highly legible sans-serif.
- Buttons/headings: strong readable weight, with selective brush/handwritten accents.
- Font styling must be consistent with the reference while remaining readable and accessible.
- Never silently change an approved typography treatment.
- If custom font files are added later, store them as app assets and treat them as locked design assets.

## 7. Patsy animation
Patsy must be an active on-screen companion whenever she is displayed.
- Natural purposeful movement, not a static sticker.
- Move/paw/point toward controls she is explaining.
- Change position according to screen/context.
- Approved states: happy, excited, thinking, pointing, concerned/warning, encouraging, sleepy/resting, celebratory.
- Mouth/talking state should animate; future lip-sync can plug into the same state architecture.
- Eyes/head/face should react to conversation and actions.
- React visibly to buttons, success, errors and loading/waiting.
- Avoid distracting excessive motion.
- Architecture must allow additional approved frames/animations without redesigning screens.

## 8. Onboarding and authentication
- Welcome / Get Started.
- Existing account / Login.
- Experience modes: 16+ Patsy, Under-16 Patsy, Protected Mode.
- Selected mode is stored with profile and actually affects safety defaults.
- Profile collects display name and username.
- Real username availability validation.
- Password rules and clear feedback.
- Continue/Create Account actually progresses when valid.
- Login supports username or email + password.
- Remember Me where appropriate.
- Password visibility control.
- Forgotten-password/recovery path when backend exists.
- Safe session persistence.
- Useful invalid credential, duplicate username, missing field, offline and server-error states.
- Never display/log plaintext passwords.

## 9. Owner profile — completely separate
The owner profile is a special secured role and must NEVER be treated as an ordinary user profile.

### Owner profile
- Separate role: `OWNER`.
- Separate owner workspace/profile surface.
- Owner-only navigation/tools are only rendered after authenticated authorisation says the account is OWNER.
- Ordinary profile switching can NEVER grant owner privileges.
- Knowing an owner username must never be sufficient.
- Reaching a hidden screen or changing a local flag must never grant owner privileges.

### Owner tools
The owner workspace may contain:
- administration;
- user/profile administration;
- app configuration;
- AI/provider configuration;
- web-search/provider configuration;
- diagnostics;
- moderation and safety controls;
- feature flags/configuration;
- app-management tools;
- security/audit information;
- service health / connectivity information.

### Owner security
- Owner authorisation must be enforced by the authentication/authorisation layer, not merely hidden UI.
- Production role checks must be server-side/trusted wherever a backend exists.
- Secure session/token handling is required.
- Prevent privilege escalation.
- Audit security-sensitive owner actions where appropriate.
- NEVER hard-code an owner password, API key or privileged secret in the APK/source.
- If a secure owner capability cannot yet be implemented, keep it disabled rather than faking security.

## 10. AI + web search
Patsy is the primary AI entry point.
- Natural-language requests.
- Answer questions.
- Search the internet when enabled.
- Explain results.
- Turn answers into actions.
- Clear thinking/loading, success, offline, provider-failure and unavailable states.
- Secure backend/proxy for AI and web search.
- No plaintext provider credentials in the Android client.
- HTTPS/TLS.
- Timeouts, offline mode, rate limits and outages handled gracefully.
- UI must not freeze while waiting for network responses.
- Separate UI from service/provider implementations so providers can be changed later.
- Patsy must not claim she searched/created/published anything unless the connected service actually did it.
- Consequential/irreversible actions require confirmation where appropriate.

## 11. Home workspace
Main navigation:
- Home
- Chat
- Create
- Schedule
- More

Home quick actions:
- Get Ideas
- Plan & Schedule
- Grow
- Create Content
- Hashtags
- Tools

Patsy should give contextual guidance and point toward relevant controls.

## 12. Creation Studio — high specification
Creation Studio is a major product surface, not a placeholder.
- Editor/canvas.
- Import/upload user media.
- AI image generation when provider configured.
- 10-second video generation when provider configured.
- Crop, resize, rotate, positioning and transforms.
- Brightness, contrast, saturation, exposure and similar adjustments.
- Filters/effects.
- Text, stickers, shapes and overlays.
- Layer/order controls.
- Undo/redo/reset.
- Preview/export/save.
- Patsy assistant input at the bottom.
- Natural commands such as “make this brighter”, “remove the background”, “make it more colourful”, “turn this into a Reel”, “make a 10 second video”.
- Never show fake generated results when a provider is unavailable.
- Design so more AI tools can be added without rebuilding the whole screen.

## 13. DMs / Messenger
- Direct/private messaging.
- Conversation list.
- Individual conversations.
- Sending and basic message states.
- Authenticated access only.
- Privacy controls.
- Private messages must never appear in public feeds/search unless explicitly shared.
- Younger/protected modes must enforce stronger messaging/discovery restrictions.

## 14. Social/content workspace
- Content planning and drafting.
- Scheduling surfaces.
- Reusable drafts.
- Safe repurposing workflows.
- Creation/scheduling/social features remain separate from owner administration.
- Publishing integrations must clearly identify the account/service affected.
- Brand-aware content assistance.
- Current hashtag/trend information should be checked when relevant rather than blindly reusing old sets.

## 15. Brand knowledge Patsy should understand
DontWorryBePatsy is broader than grief: pet love, funny everyday life, community, advice, safety, stories, recommendations and remembrance all belong.
Voice: UK English; real, warm, friendly, cool, relatable and occasionally cheeky.
Core feeling: funny + real + warm + slightly cheeky + emotionally honest + inclusive + community-driven.

The approved brand logo is locked. Patsy is the grey dog. Darcy is black-and-white only and must never be made brown/tan.

## 16. Safety / age modes
- 16+ mode: full intended experience within applicable safety rules.
- Under-16 mode: stronger restrictions and safer defaults.
- Protected Mode: safer defaults when age is uncertain.
- Rules apply consistently across chat, search, creation, DMs and AI-powered areas.
- Do not rely on a label alone; selected mode must affect behaviour.
- Protect children/young users from inappropriate discovery, messaging and generated content.
- Minimise personal-data collection.

## 17. Privacy / data protection
- Collect only data required for a feature.
- Protect credentials, sessions, private messages and user content.
- Secure storage for tokens/secrets available to the app.
- No API keys/passwords/privileged credentials in source control or APK resources.
- Account deletion/data-management pathways when backend supports them.
- Clearly distinguish local-only and cloud-synchronised data.
- Do not send unnecessary private/profile data to AI providers.

## 18. Reference assets
The app package includes approved visual references under `app/src/main/assets/references/`, including:
- Rainbow onboarding/login/home storyboard.
- Patsy creator-assistant sticker sheet.
- Patsy creator collage.
- Rainbow brand reference.
- App specification.

These references are for preserving the approved identity and direction. They are not permission to redesign Patsy into a different dog.

## 19. Release acceptance checklist
A release is NOT finished just because screenshots look right.
- Fresh install opens.
- Experience mode works.
- Real username availability works.
- Account creation works.
- Logout works.
- Login works.
- Returning session works safely.
- Patsy responds through configured AI.
- Web search works through configured pathway.
- Patsy animates/reacts/guides.
- Home navigation works.
- Chat works.
- Creation Studio opens and controls work.
- AI image generation works when configured.
- 10-second video path works when configured.
- DMs work when backend configured.
- Owner tools are inaccessible to ordinary accounts even via direct navigation.
- Age-mode restrictions work.
- Offline/error states work.
- Release build compiles cleanly with no unresolved references/placeholders.

## 20. Master development rule
DO NOT downgrade the design, character, security model or functionality to the old prototype.
The Rainbow Workspace, real Patsy identity, animation architecture, functional authentication, owner-only security, AI/search, DMs and high-spec Creation Studio are core requirements.
