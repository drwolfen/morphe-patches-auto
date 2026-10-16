/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.duolingo v6.101.1 — do not edit by hand.
 */

package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Auto-generated: unlocks features gated by com.duolingo paywall checks."
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        IsPromoFingerprint.method.returnEarly(true)
        IsGoldSubscriptionFingerprint.method.returnEarly(true)
        IsSubscriptionFingerprint.method.returnEarly(true)
        HasSubscriptionFingerprint.method.returnEarly(true)
    }
}
