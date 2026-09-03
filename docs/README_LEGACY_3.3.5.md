# PATSY Android 3.3.5 — Master Handoff

This project is the consolidated Rainbow Workspace handoff for Patsy.

**Read first:** `PATSY_MASTER_REQUIREMENTS_3.3.5.md`

The master requirements cover the complete product direction: Patsy's real identity, personality, memory/permission rules, fonts/typography, rainbow visual system, animation, onboarding/authentication, separate owner profile and owner-only tools, AI + web search, Home/Chat/Create/Schedule/More, high-spec Creation Studio, DMs, social/content workflow, age modes, privacy and release acceptance.

## Important owner-security rule
The project contains an explicit `PatsyRole.OWNER` model and owner-authorisation interface. This is architectural separation, not a claim that local-only code can provide production security. In production, the trusted backend/authentication service must be the source of truth for the OWNER role. Never ship an owner password, API key or privileged credential inside the APK.

## Important Patsy rule
The app must use the supplied Patsy visual references as the identity source. Patsy is the real grey/white shaggy dog with darker ears and dark eyes. Do not substitute a generic dog or the brand logo mascot.

## Visual direction
Black / charcoal foundation + rainbow active system + white typography + rounded cards + rainbow progress/highlights + playful speech bubbles/paw guidance. Do not downgrade to the old plain prototype.

## Reference assets
See `app/src/main/assets/references/` for the supplied/approved visual references used to establish the Rainbow Workspace and Patsy assistant direction.

## Build
Open the root folder in Android Studio, sync Gradle, then run the `app` configuration.

## Production status
Local development account/authentication behaviour is not a substitute for a production backend. Before release, connect secure HTTPS authentication, server-side owner authorisation, password recovery, secure sessions, audit logging, AI/web-search proxying, DM backend and provider integrations.

## 3.3.5 master update
Patsy is explicitly the animated AI companion: she must move, point with her paws, talk with mouth/lip-sync animation, jump across the interface to guide the user, and react interactively. Owner identity/account flow, username -> immediate password setup, email linking and confirmation-email requirements are defined in `PATSY_MASTER_REQUIREMENTS_3.3.5.md` and `AUTH_EMAIL_OWNER_BACKEND_CONTRACT.md`.
