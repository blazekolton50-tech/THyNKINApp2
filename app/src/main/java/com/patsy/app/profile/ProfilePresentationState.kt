package com.patsy.app.profile

data class ProfileMetric(
    val label: String,
    val count: Long?,
) {
    val formattedValue: String?
        get() = count?.coerceAtLeast(0L)?.toString()
}

data class ProfileProjectRef(
    val id: String,
    val title: String,
    val thumbnailUri: String? = null,
)

data class ProfilePresentationState(
    val displayName: String,
    val username: String,
    val bio: String? = null,
    val avatarUri: String? = null,
    val metrics: List<ProfileMetric> = listOf(
        ProfileMetric("Projects", null),
        ProfileMetric("Followers", null),
        ProfileMetric("Following", null),
        ProfileMetric("Likes", null),
    ),
    val recentProjects: List<ProfileProjectRef> = emptyList(),
    val savedProjects: List<ProfileProjectRef> = emptyList(),
)

enum class ProfileQuickAction {
    IMAGE,
    VIDEO,
    DOCUMENT,
    TEMPLATE,
    MORE,
}

fun defaultProfileQuickActions(): List<ProfileQuickAction> = listOf(
    ProfileQuickAction.IMAGE,
    ProfileQuickAction.VIDEO,
    ProfileQuickAction.DOCUMENT,
    ProfileQuickAction.TEMPLATE,
    ProfileQuickAction.MORE,
)

enum class ProfileMenuSection {
    ACCOUNT,
    PREFERENCES,
    SUPPORT,
}

enum class ProfileMenuDestination {
    SCHEDULING,
    CALENDAR,
    FRIENDS,
    LOCKED_MEDIA,
    THYNK_STORAGE,
    SETTINGS,
    ACCOUNT_INFO,
    THEME,
    NOTIFICATIONS,
    HELP_SUPPORT,
    ABOUT_PATSY,
    LOG_OUT,
}

data class ProfileMenuItem(
    val destination: ProfileMenuDestination,
    val label: String,
    val subtitle: String? = null,
    val destructive: Boolean = false,
    val grantsOwnerAuthority: Boolean = false,
)

data class ProfileMenuGroup(
    val section: ProfileMenuSection,
    val items: List<ProfileMenuItem>,
)

fun expandedProfileMenu(): List<ProfileMenuGroup> = listOf(
    ProfileMenuGroup(
        ProfileMenuSection.ACCOUNT,
        listOf(
            ProfileMenuItem(ProfileMenuDestination.SCHEDULING, "Scheduling", "Plan your posts"),
            ProfileMenuItem(ProfileMenuDestination.CALENDAR, "Calendar", "Events & reminders"),
            ProfileMenuItem(ProfileMenuDestination.FRIENDS, "Friends", "Connect & collaborate"),
            ProfileMenuItem(ProfileMenuDestination.LOCKED_MEDIA, "Locked Media", "Private & secure"),
            ProfileMenuItem(ProfileMenuDestination.THYNK_STORAGE, "THyNK Storage", "Put-Away Portal"),
        ),
    ),
    ProfileMenuGroup(
        ProfileMenuSection.PREFERENCES,
        listOf(
            ProfileMenuItem(ProfileMenuDestination.SETTINGS, "Settings", "App preferences"),
            ProfileMenuItem(ProfileMenuDestination.ACCOUNT_INFO, "Account Info", "Personal details"),
            ProfileMenuItem(ProfileMenuDestination.THEME, "Theme", "Dark (Default)"),
            ProfileMenuItem(ProfileMenuDestination.NOTIFICATIONS, "Notifications", "Manage alerts"),
        ),
    ),
    ProfileMenuGroup(
        ProfileMenuSection.SUPPORT,
        listOf(
            ProfileMenuItem(ProfileMenuDestination.HELP_SUPPORT, "Help & Support", "Get help anytime"),
            ProfileMenuItem(ProfileMenuDestination.ABOUT_PATSY, "About Patsy"),
            ProfileMenuItem(ProfileMenuDestination.LOG_OUT, "Log Out", destructive = true),
        ),
    ),
)
