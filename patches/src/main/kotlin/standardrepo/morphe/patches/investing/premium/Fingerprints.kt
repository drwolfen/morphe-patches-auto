/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.fusionmedia.investing v6.52 — do not edit by hand.
 */

package standardrepo.morphe.patches.investing.premium

import app.morphe.patcher.Fingerprint

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

object GetCurrencyIso4217CodeFingerprint : Fingerprint(
    definingClass = "Lcom/appsflyer/internal/AFg1nSDK;",
    returnType = "Z",
    name = "getCurrencyIso4217Code"
)

object GetRevenueFingerprint : Fingerprint(
    definingClass = "Lcom/appsflyer/internal/AFg1mSDK;",
    returnType = "Ljava/util/Map;",
    name = "getRevenue"
)

object AFingerprint : Fingerprint(
    definingClass = "Le51;",
    returnType = "V",
    name = "a"
)
