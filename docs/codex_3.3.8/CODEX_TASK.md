# Codex continuation task — Patsy backend

Work from this repository as the source of truth. Complete and verify the backend without regressing the Android client.

1. Install backend dependencies with `npm install`.
2. Start Postgres with `docker compose up -d postgres`.
3. Run `npm run build` and fix all TypeScript errors.
4. Add integration tests for registration, username/email collision, login by username/email, session expiry/revocation, password reset, email verification, owner authorization, DMs, schedules, AI provider-unconfigured states, and media upload authorization.
5. Wire the Android app to a configurable HTTPS backend base URL; local emulator URL may be `http://10.0.2.2:8787` only for development.
6. Replace local-only authentication when backend mode is enabled, while preserving a clearly labelled offline/dev mode only when explicitly configured.
7. Never put SMTP/OpenAI/owner secrets in the APK.
8. Add an operational owner bootstrap flow that requires authenticated infrastructure access and writes an audit event.
9. Configure production object storage and a real video provider through server-side adapters.
10. Build the Android release and run the acceptance checklist from the Master Specification.

Do not report an email, AI generation, web search, publication, or owner action as successful unless the server/provider confirms it.
