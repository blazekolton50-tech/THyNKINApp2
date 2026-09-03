# Auth / Email / Owner Backend Contract

The Android app must not perform privileged account or email operations using local-only state.

Suggested secure endpoints:

- `POST /auth/register/start` — validate username/email and return next-step state.
- `POST /auth/register/password` — create account after password policy validation.
- `POST /auth/email/verify/request` — request verification/confirmation email.
- `POST /auth/email/verify/confirm` — confirm email token.
- `POST /auth/login` — username or email + password.
- `POST /auth/password/forgot` — recovery request.
- `POST /auth/password/reset` — token-based reset.
- `GET /auth/session` — restore authenticated session securely.
- `GET /me` — ordinary profile.
- `GET /owner/me` — owner-only endpoint; backend checks role.
- `GET /owner/audit` — owner-only security audit information.

## Confirmation email requirement
The backend/email provider must return a reliable queued/sent result. The Android UI should show:

> Account setup complete. Your confirmation email has been sent to [masked email].

only when the provider confirms queueing/sending. Otherwise show an appropriate pending/error state.

Never put SMTP/API credentials in the Android app.

## Owner requirement
The owner role must be stored and enforced server-side. The app can hide/show owner screens based on the authenticated role, but the backend remains the authority.
