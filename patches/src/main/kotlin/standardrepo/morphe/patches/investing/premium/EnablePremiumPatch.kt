/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.fusionmedia.investing v6.52 — do not edit by hand.
 */

package standardrepo.morphe.patches.investing.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Pro",
    description = "Auto-generated: unlocks Investing Pro — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.INVESTING)

    execute {
        VerifySignatureMatchesFingerprint.method.returnEarly(true)
        VerifyIntegrityFingerprint.method.returnEarly()
        GetCurrencyIso4217CodeFingerprint.method.returnEarly(true)
        GetRevenueFingerprint.method.returnEarly(null as Void?)
        AFingerprint.method.returnEarly()
        AFingerprintLd8i.method.returnEarly()
        IFingerprint.method.returnEarly(true)
        AFingerprintLkzj.method.returnEarly(true)
        AFingerprintLlgd.method.returnEarly(true)
        AFingerprintLrz0.method.returnEarly(true)
        AFingerprintLw4b.method.returnEarly(true)
        AFingerprintLy4b.method.returnEarly(true)
    }
}
