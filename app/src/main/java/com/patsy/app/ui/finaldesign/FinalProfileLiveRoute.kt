package com.patsy.app.ui.finaldesign

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.patsy.app.auth.PublicSession
import com.patsy.app.profile.ProfileDataResult
import com.patsy.app.profile.ProfileQuickAction
import com.patsy.app.profile.ProfileServiceBindings

@Composable
fun FinalProfileLiveRoute(
    session: PublicSession,
    emailVerified: Boolean,
    ownerAccessChecked: Boolean,
    canViewOwnerProfile: Boolean,
    canViewOwnerTools: Boolean,
    onQuickAction: (ProfileQuickAction) -> Unit,
    onOpenOwnerProfile: () -> Unit,
    onOpenOwnerTools: () -> Unit,
    onUnauthorized: () -> Unit,
    onSignOut: () -> Unit,
) {
    var screenState by remember(session.sessionId) {
        mutableStateOf(
            finalProfileScreenState(
                displayName = session.username,
                username = session.username,
            ),
        )
    }

    LaunchedEffect(session.sessionId) {
        when (val result = ProfileServiceBindings.profileDataService.load(session)) {
            is ProfileDataResult.Loaded -> {
                screenState = FinalProfileScreenState(presentation = result.presentation)
            }
            ProfileDataResult.Unauthorized -> onUnauthorized()
            ProfileDataResult.Unavailable -> Unit // keep truthful session-only fallback, never mock data
        }
    }

    FinalProfileScreen(
        state = screenState,
        emailVerified = emailVerified,
        ownerAccessChecked = ownerAccessChecked,
        canViewOwnerProfile = canViewOwnerProfile,
        canViewOwnerTools = canViewOwnerTools,
        onQuickAction = onQuickAction,
        onOpenOwnerProfile = onOpenOwnerProfile,
        onOpenOwnerTools = onOpenOwnerTools,
        onSignOut = onSignOut,
    )
}
