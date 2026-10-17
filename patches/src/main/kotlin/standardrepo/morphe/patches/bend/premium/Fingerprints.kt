/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.bowerydigital.bend v7.8.0 — do not edit by hand.
 */

package standardrepo.morphe.patches.bend.premium

import app.morphe.patcher.Fingerprint

object ProcessResponseFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseClient;",
    name = "processResponse"
)

object ValidateResponseFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseResponseHelper;",
    name = "validateResponse"
)

object VerifySignatureMatchesFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/SignatureCheck;",
    name = "verifySignatureMatches"
)

object VerifyIntegrityFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/SignatureCheck;",
    name = "verifyIntegrity"
)
