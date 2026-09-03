# THyNK Donor Asset Intake Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a safe manifest-driven intake foundation so Drive/Gemini/AI Studio/archive assets can be catalogued without treating wrapper files, unknown licenses or stale prototypes as production-ready content.

**Architecture:** Add a pure Kotlin donor-asset model and validator under `com.patsy.app.studio.assets`. The production UI consumes only records whose availability is `VERIFIED`; all archive names, HTML wrappers and unavailable bytes remain `REFERENCE_ONLY`. This plan does not copy external archive contents into the APK.

**Tech Stack:** Kotlin/JVM unit tests, existing Android app module, Jetpack Compose only where the existing THyNK catalogue displays asset availability.

**Spec:** `docs/superpowers/specs/2026-08-31-thynk-donor-consolidation-design.md`

## Global Constraints

- Work only on `chatgpt/thynk-music-apk-2026-08-31` / PR #38 and keep the PR Draft.
- Run this only after the Media3 and global-navigation plans are GREEN.
- Do not weaken auth, age, Owner or Supabase RLS contracts.
- Do not copy Replit navigation, browser/Node architecture or unverified archive contents.
- Wrapper HTML/archive titles are evidence of a reference only, never evidence that contained bytes are production-ready.
- A production-ready donor record must have a stable ID, origin, category/subcategory, type, location/reference, licensing/origin status, duplicate status, and checksum when bytes are available.
- Only `VERIFIED` donor records may be surfaced as production-ready assets.

---

### Task 1: Define donor-asset truth model

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/assets/DonorAssetManifest.kt`
- Create: `app/src/test/java/com/patsy/app/studio/assets/DonorAssetManifestTest.kt`

**Interfaces:**
- Produces: `DonorAssetType`, `DonorAssetAvailability`, `DonorLicenseStatus`, `DonorDuplicateStatus`, `DonorAssetRecord`, `DonorAssetManifest`.

- [ ] **Step 1: Write failing model/validation tests**

```kotlin
package com.patsy.app.studio.assets

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DonorAssetManifestTest {
    @Test fun referenceOnlyRecordIsNotProductionReady() {
        val record = DonorAssetRecord(
            id = "drive-stage1-wrapper",
            origin = "Google Drive",
            category = "design",
            subcategory = "archive",
            type = DonorAssetType.ARCHIVE_REFERENCE,
            location = "drive:reference",
            licenseStatus = DonorLicenseStatus.UNKNOWN,
            duplicateStatus = DonorDuplicateStatus.UNKNOWN,
            checksumSha256 = null,
            availability = DonorAssetAvailability.REFERENCE_ONLY,
        )
        assertFalse(record.isProductionReady)
    }

    @Test fun verifiedRecordRequiresAcceptableOriginAndLocation() {
        val record = DonorAssetRecord(
            id = "verified-template-1",
            origin = "Owner supplied",
            category = "social",
            subcategory = "square-post",
            type = DonorAssetType.TEMPLATE,
            location = "res/raw/verified_template_1.json",
            licenseStatus = DonorLicenseStatus.VERIFIED_FOR_APP,
            duplicateStatus = DonorDuplicateStatus.UNIQUE,
            checksumSha256 = "abc123",
            availability = DonorAssetAvailability.VERIFIED,
        )
        assertTrue(record.isProductionReady)
    }
}
```

- [ ] **Step 2: Run tests and verify RED**

Run:
```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.assets.DonorAssetManifestTest'
```
Expected: FAIL because the donor manifest types do not yet exist.

- [ ] **Step 3: Implement the minimal truth model**

```kotlin
package com.patsy.app.studio.assets

enum class DonorAssetType { TEMPLATE, IMAGE, ILLUSTRATION, ICON, LOGO, FONT, AUDIO, VIDEO, CODE_REFERENCE, ARCHIVE_REFERENCE }
enum class DonorAssetAvailability { REFERENCE_ONLY, BYTES_AVAILABLE, VERIFIED, REJECTED }
enum class DonorLicenseStatus { UNKNOWN, OWNER_SUPPLIED, VERIFIED_FOR_APP, RESTRICTED }
enum class DonorDuplicateStatus { UNKNOWN, UNIQUE, DUPLICATE, SUPERSEDED }

data class DonorAssetRecord(
    val id: String,
    val origin: String,
    val category: String,
    val subcategory: String,
    val type: DonorAssetType,
    val location: String,
    val licenseStatus: DonorLicenseStatus,
    val duplicateStatus: DonorDuplicateStatus,
    val checksumSha256: String?,
    val availability: DonorAssetAvailability,
) {
    val isProductionReady: Boolean
        get() = id.isNotBlank() && origin.isNotBlank() && location.isNotBlank() &&
            availability == DonorAssetAvailability.VERIFIED &&
            licenseStatus == DonorLicenseStatus.VERIFIED_FOR_APP &&
            duplicateStatus == DonorDuplicateStatus.UNIQUE
}

data class DonorAssetManifest(val records: List<DonorAssetRecord>) {
    fun productionReady(): List<DonorAssetRecord> = records.filter { it.isProductionReady }
}
```

- [ ] **Step 4: Run targeted tests and full unit tests**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.assets.DonorAssetManifestTest'
./gradlew testDebugUnitTest
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/assets app/src/test/java/com/patsy/app/studio/assets
git commit -m "feat: add verified donor asset manifest model"
```

### Task 2: Seed reference-only audit records for known donor sources

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/assets/PatsyDonorSourceCatalog.kt`
- Modify: `app/src/test/java/com/patsy/app/studio/assets/DonorAssetManifestTest.kt`

**Interfaces:**
- Produces: `PatsyDonorSourceCatalog.referenceManifest`.

- [ ] **Step 1: Add a failing test proving wrappers cannot leak into production-ready results**

```kotlin
@Test fun knownDriveWrappersRemainReferenceOnly() {
    val manifest = PatsyDonorSourceCatalog.referenceManifest
    assertTrue(manifest.records.any { it.id == "drive-thynk-stage1-a" })
    assertTrue(manifest.records.any { it.id == "drive-stage2-real-setup" })
    assertTrue(manifest.productionReady().isEmpty())
}
```

- [ ] **Step 2: Run RED test**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.assets.DonorAssetManifestTest.knownDriveWrappersRemainReferenceOnly'
```
Expected: FAIL because `PatsyDonorSourceCatalog` does not exist.

- [ ] **Step 3: Add explicit reference records**

Create records for the currently audited donor sources using stable IDs, including the Stage 1 wrappers, Stage 2 setup, full 1110 wrapper, icons/illustrations/logos pack, Studio/Canva/video/photo/sound architecture pack, fonts pack, social/business/slides pack, Google AI Studio prompt, Replit prototype and the uploaded older Android ZIP. Set all to `REFERENCE_ONLY` unless actual bytes, origin/license and duplicate checks are subsequently verified.

```kotlin
object PatsyDonorSourceCatalog {
    val referenceManifest = DonorAssetManifest(
        records = listOf(
            reference("drive-thynk-stage1-a", "Google Drive", "patsy_thynk_stage1_real.zip wrapper"),
            reference("drive-stage2-real-setup", "Google Drive", "THyNK Stage 2 web-oriented setup"),
            reference("google-ai-studio-core", "Google AI Studio", "Patsy Creation Studio Core Implementation"),
            reference("replit-patsy-companion", "Replit", "Expo/React Native reference lane"),
            reference("uploaded-android-zip-old", "Conversation upload", "Older Android Studio recovery snapshot"),
        ),
    )

    private fun reference(id: String, origin: String, location: String) = DonorAssetRecord(
        id = id,
        origin = origin,
        category = "reference",
        subcategory = "audit",
        type = DonorAssetType.ARCHIVE_REFERENCE,
        location = location,
        licenseStatus = DonorLicenseStatus.UNKNOWN,
        duplicateStatus = DonorDuplicateStatus.UNKNOWN,
        checksumSha256 = null,
        availability = DonorAssetAvailability.REFERENCE_ONLY,
    )
}
```

- [ ] **Step 4: Run tests**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.assets.*'
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/assets/PatsyDonorSourceCatalog.kt app/src/test/java/com/patsy/app/studio/assets/DonorAssetManifestTest.kt
git commit -m "docs: encode audited donor sources as reference only"
```

### Task 3: Add promotion validator instead of manual availability flips

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/assets/DonorAssetVerifier.kt`
- Create: `app/src/test/java/com/patsy/app/studio/assets/DonorAssetVerifierTest.kt`

**Interfaces:**
- Produces: `DonorVerificationEvidence`, `DonorVerificationDecision`, `verifyDonorAsset(record, evidence)`.

- [ ] **Step 1: Write failing promotion tests**

```kotlin
@Test fun cannotVerifyWithoutBytesLicenseChecksumAndUniqueDecision() {
    val decision = verifyDonorAsset(referenceRecord, DonorVerificationEvidence(
        bytesAvailable = false,
        licenseApproved = true,
        checksumSha256 = null,
        duplicateStatus = DonorDuplicateStatus.UNIQUE,
    ))
    assertTrue(decision is DonorVerificationDecision.Rejected)
}

@Test fun completeEvidenceCanPromoteRecord() {
    val decision = verifyDonorAsset(referenceRecord, DonorVerificationEvidence(
        bytesAvailable = true,
        licenseApproved = true,
        checksumSha256 = "deadbeef",
        duplicateStatus = DonorDuplicateStatus.UNIQUE,
    ))
    assertTrue(decision is DonorVerificationDecision.Verified)
}
```

- [ ] **Step 2: Run RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.assets.DonorAssetVerifierTest'
```

- [ ] **Step 3: Implement fail-closed verification**

```kotlin
data class DonorVerificationEvidence(
    val bytesAvailable: Boolean,
    val licenseApproved: Boolean,
    val checksumSha256: String?,
    val duplicateStatus: DonorDuplicateStatus,
)

sealed interface DonorVerificationDecision {
    data class Verified(val record: DonorAssetRecord) : DonorVerificationDecision
    data class Rejected(val reasons: List<String>) : DonorVerificationDecision
}

fun verifyDonorAsset(record: DonorAssetRecord, evidence: DonorVerificationEvidence): DonorVerificationDecision {
    val reasons = buildList {
        if (!evidence.bytesAvailable) add("bytes unavailable")
        if (!evidence.licenseApproved) add("license/origin not approved")
        if (evidence.checksumSha256.isNullOrBlank()) add("checksum missing")
        if (evidence.duplicateStatus != DonorDuplicateStatus.UNIQUE) add("duplicate status not unique")
    }
    if (reasons.isNotEmpty()) return DonorVerificationDecision.Rejected(reasons)
    return DonorVerificationDecision.Verified(record.copy(
        checksumSha256 = evidence.checksumSha256,
        licenseStatus = DonorLicenseStatus.VERIFIED_FOR_APP,
        duplicateStatus = evidence.duplicateStatus,
        availability = DonorAssetAvailability.VERIFIED,
    ))
}
```

- [ ] **Step 4: Run all asset tests and full unit suite**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.assets.*'
./gradlew testDebugUnitTest
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/assets app/src/test/java/com/patsy/app/studio/assets
git commit -m "feat: fail closed when promoting donor assets"
```

### Task 4: Verify the foundation without exposing reference assets in UI

**Files:**
- No production UI file should need modification in this task.

- [ ] **Step 1: Run complete verification**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```
Expected: all exit 0.

- [ ] **Step 2: Confirm no reference-only donor is surfaced as production content**

```bash
git grep -n "PatsyDonorSourceCatalog" -- app/src/main/java
```
Expected: only the catalogue/verification foundation unless a later reviewed task deliberately consumes verified records.

- [ ] **Step 3: Record exact head and CI evidence**

Push the branch, wait for Patsy Consolidation CI, and report the starting SHA, ending SHA, changed files, RED tests, GREEN tests/builds and final CI run. Keep PR #38 Draft.
