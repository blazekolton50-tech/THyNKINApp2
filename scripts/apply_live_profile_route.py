from pathlib import Path

PATH = Path("app/src/main/java/com/patsy/app/FinalMainActivity.kt")
text = PATH.read_text(encoding="utf-8")

import_line = "import com.patsy.app.ui.finaldesign.FinalProfileLiveRoute\n"
if import_line not in text:
    anchor = "import com.patsy.app.ui.finaldesign.FinalProfileScreen\n"
    if anchor not in text:
        raise SystemExit("Profile route import anchor missing")
    text = text.replace(anchor, anchor + import_line, 1)

old = '''                                    FinalAppPage.PROFILE -> FinalProfileScreen(
                                        state = finalProfileScreenState(
                                            displayName = session?.username ?: "Your profile",
                                            username = session?.username ?: "",
                                        ),
                                        emailVerified = session?.emailVerified == true,
                                        ownerAccessChecked = ownerAccessChecked,
                                        canViewOwnerProfile = FinalOwnerAccessPolicy.canOpen(
                                            ownerProfileGrant,
                                            OwnerCapability.VIEW_OWNER_PROFILE,
                                        ),
                                        canViewOwnerTools = FinalOwnerAccessPolicy.canOpen(
                                            ownerToolsGrant,
                                            OwnerCapability.VIEW_OWNER_TOOLS,
                                        ),
                                        onQuickAction = { page = FinalAppPage.THYNK },
                                        onOpenOwnerProfile = {
                                            scope.launch {
                                                val refreshed = refreshOwnerAccess().first
                                                if (
                                                    FinalOwnerAccessPolicy.canOpen(
                                                        refreshed,
                                                        OwnerCapability.VIEW_OWNER_PROFILE,
                                                    )
                                                ) {
                                                    page = FinalAppPage.OWNER_PROFILE
                                                }
                                            }
                                        },
                                        onOpenOwnerTools = {
                                            scope.launch {
                                                val refreshed = refreshOwnerAccess().second
                                                if (
                                                    FinalOwnerAccessPolicy.canOpen(
                                                        refreshed,
                                                        OwnerCapability.VIEW_OWNER_TOOLS,
                                                    )
                                                ) {
                                                    page = FinalAppPage.OWNER_TOOLS
                                                }
                                            }
                                        },
                                        onSignOut = ::signOut,
                                    )
'''

new = '''                                    FinalAppPage.PROFILE -> {
                                        val activeSession = session
                                        if (activeSession == null) {
                                            FinalProtectedAccessState(
                                                message = "Your profile requires a verified signed-in session.",
                                                onSignOut = ::signOut,
                                            )
                                        } else {
                                            FinalProfileLiveRoute(
                                                session = activeSession,
                                                emailVerified = activeSession.emailVerified,
                                                ownerAccessChecked = ownerAccessChecked,
                                                canViewOwnerProfile = FinalOwnerAccessPolicy.canOpen(
                                                    ownerProfileGrant,
                                                    OwnerCapability.VIEW_OWNER_PROFILE,
                                                ),
                                                canViewOwnerTools = FinalOwnerAccessPolicy.canOpen(
                                                    ownerToolsGrant,
                                                    OwnerCapability.VIEW_OWNER_TOOLS,
                                                ),
                                                onQuickAction = { page = FinalAppPage.THYNK },
                                                onOpenOwnerProfile = {
                                                    scope.launch {
                                                        val refreshed = refreshOwnerAccess().first
                                                        if (
                                                            FinalOwnerAccessPolicy.canOpen(
                                                                refreshed,
                                                                OwnerCapability.VIEW_OWNER_PROFILE,
                                                            )
                                                        ) {
                                                            page = FinalAppPage.OWNER_PROFILE
                                                        }
                                                    }
                                                },
                                                onOpenOwnerTools = {
                                                    scope.launch {
                                                        val refreshed = refreshOwnerAccess().second
                                                        if (
                                                            FinalOwnerAccessPolicy.canOpen(
                                                                refreshed,
                                                                OwnerCapability.VIEW_OWNER_TOOLS,
                                                            )
                                                        ) {
                                                            page = FinalAppPage.OWNER_TOOLS
                                                        }
                                                    }
                                                },
                                                onUnauthorized = { page = FinalAppPage.PROTECTED },
                                                onSignOut = ::signOut,
                                            )
                                        }
                                    }
'''

live_route_markers = (
    "FinalAppPage.PROFILE -> {",
    "FinalProfileLiveRoute(",
    "onUnauthorized = { page = FinalAppPage.PROTECTED },",
)
live_route_present = all(marker in text for marker in live_route_markers)

if not live_route_present:
    if old not in text:
        raise SystemExit("Expected Profile route block missing; refusing blind shell rewrite")
    text = text.replace(old, new, 1)

PATH.write_text(text, encoding="utf-8")
print("Live authenticated Profile data host integrated")
