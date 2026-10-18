/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.fusionmedia.investing v6.52 — do not edit by hand.
 */

package standardrepo.morphe.patches.investing.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Auto-generated: unlocks features gated by com.fusionmedia.investing paywall checks."
) {
    compatibleWith(Compat.INVESTING)

    execute {
        VerifySignatureMatchesFingerprint.method.returnEarly(true)
        VerifyIntegrityFingerprint.method.returnEarly()
        GetCurrencyIso4217CodeFingerprint.method.returnEarly(true)
        GetRevenueFingerprint.method.returnEarly(null as Void?)
    }
}
