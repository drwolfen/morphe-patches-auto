/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.bowerydigital.bend v7.8.0 — do not edit by hand.
 */

package standardrepo.morphe.patches.bend.premium

import app.morphe.patcher.Fingerprint

object ProcessResponseFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseClient;",
    returnType = "V",
    name = "processResponse"
)

object ValidateResponseFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseResponseHelper;",
    returnType = "V",
    name = "validateResponse"
)

object VerifySignatureMatchesFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/SignatureCheck;",
    returnType = "Z",
    name = "verifySignatureMatches"
)

object VerifyIntegrityFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/SignatureCheck;",
    returnType = "V",
    name = "verifyIntegrity"
)

object GetStoreIdFromSystemFingerprint : Fingerprint(
    definingClass = "Lcom/adjust/sdk/StoreInfoUtil;",
    returnType = "Ljava/lang/String;",
    name = "getStoreIdFromSystem"
)
