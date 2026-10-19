/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.urbandroid.sleep v20261006 — do not edit by hand.
 */

package standardrepo.morphe.patches.sleep.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Auto-generated: unlocks Sleep Premium — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.SLEEP)

    execute {
        LambdashowTheAlertDialog0Fingerprint.method.returnEarly()
        ResolveFingerprint.method.returnEarly()
        Lambdarefresh3Fingerprint.method.returnEarly(true)
        Bindlambda1Fingerprint.method.returnEarly()
        IsSameAsLastFinishedRecordFingerprint.method.returnEarly(true)
        NestmskipUpdateFingerprint.method.returnEarly(true)
        IsHideSleepAnalysisFingerprint.method.returnEarly(true)
        IsTrackingEnabledForRecordFingerprint.method.returnEarly(true)
        FilterFingerprint.method.returnEarly(true)
        FilterFingerprintTagFilter.method.returnEarly(true)
        IsUnlockAckedFingerprint.method.returnEarly(true)
        HasUnlockFingerprint.method.returnEarly(true)
    }
}
