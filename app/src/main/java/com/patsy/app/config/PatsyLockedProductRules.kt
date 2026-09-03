package com.patsy.app.config

/**
 * Owner-approved product invariants. These are implementation guardrails, not optional styling hints.
 * Material changes require explicit owner approval.
 *
 * The object/package names are legacy technical identifiers. The current app/global product brand is THyNK-IN.
 */
object PatsyLockedProductRules {
    object Branding {
        const val appName = "THyNK-IN"
        const val creationStudioName = "THyNK"
        const val tagline = "A LEGACY LED BY PAWS"
        const val signupCopy = "I'm Patsy. Your personal AI PetPal. Log in and I'll show you what I can do!"
        const val darkPremiumUi = true
        const val whitePrimaryTypography = true
        const val whiteButtonsWithDarkText = true
        const val restrainedRainbowAccent = true
        const val thirdPartySocialBrandNamesInUi = false
    }

    object Companion {
        const val realisticAppPatsyOnly = true
        const val pawMojiMayBeCartoon = true
        const val maxRealisticPatsyPerScreen = 1
        const val unboxedTransparentPresentation = true
        const val expressionChangesWithContext = true
        const val greetingCopyMustVary = true
    }

    object Navigation {
        const val homeIsNewsFeed = true
        val bottomNav = listOf("HOME", "THyNK", "CREATE", "PATSY_DMS", "PROFILE")
        const val continueDesignsVisible = true
        const val todayVisible = true
    }

    object MediaRetention {
        const val feedMediaDays = 90
        const val defaultDmDays = 3
        const val profileLockedImageLimit = 100
        const val profileLockedVideoLimit = 30
        const val generatedMediaShouldPreferDeviceStorage = true
    }

    object Under16 {
        const val socialLinkingAllowed = false
        const val dmPeersMustBeUnder16 = true
        const val mediaViewOnce = true
        const val mediaDeleteAfterView = true
        const val documentsAllowed = true
        const val templatesAllowed = true
        const val colouringAllowed = true
        const val uncertainAgeUsesChildProtections = true
    }

    object Safety {
        const val externalContentCanIssueInstructions = false
        const val authenticatedDeviceUserIsInstructionAuthority = true
        const val contextualIntentClassificationRequired = true
        const val benignDangerousTopicDiscussionAllowed = true
        const val maxSeriousMisuseStrikes = 3
        const val strikeOneTwoCooldownMinutes = 10
        const val strikeThreeRequiresSuspensionReviewPath = true
    }

    object RememberMe {
        const val useActualPatsyPawOutline = true
        const val openStateThinRainbowOutline = true
        const val savedStateFilledNeonRainbow = true
        const val activationPulseMs = 1000L
        const val filledPawReservedForRememberedState = true
    }

    object PawMojiKeyboard {
        const val blackKeyboard = true
        const val rainbowLetterTreatment = true
        const val normalEmojiAvailable = true
        const val dedicatedPawMojiTab = true
        const val eachPawMojiIsIndividualAsset = true
        const val spriteSheetIsRuntimeAsset = false
        const val approvedAssetsMustNotBeSilentlyReplaced = true
    }
}
