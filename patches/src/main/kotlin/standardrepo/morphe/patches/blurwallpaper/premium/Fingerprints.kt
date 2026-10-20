/**
 * Copyright 2026 crackpipe
 * Auto-generated for apps.automan.blurwallpaper v3.0.0 — do not edit by hand.
 */

package standardrepo.morphe.patches.blurwallpaper.premium

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

object PerformLocalInstallerCheckFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseClient;",
    returnType = "Z",
    name = "performLocalInstallerCheck"
)
