# Patsy 3.3.8 — Backend implementation

This package adds the production backend contract requested by the Master Specification: secure authentication/session handling, email verification and password recovery, server-side owner authorization/audit, Patsy AI chat and web-search gateway, image-generation gateway, DMs, scheduling, and authenticated media upload.

The Android app still needs a deployed HTTPS backend URL and production credentials. No privileged provider credentials are embedded in the client.

The backend is intentionally provider-configurable: email and AI capabilities return explicit unavailable states when their server credentials are not configured rather than faking success.
