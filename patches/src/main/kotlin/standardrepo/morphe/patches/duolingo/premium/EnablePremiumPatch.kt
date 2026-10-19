/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.duolingo v6.101.2 — do not edit by hand.
 */

package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Super",
    description = "Auto-generated: unlocks Duolingo Super — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        UcFingerprint.method.returnEarly(true)
        InvokeFingerprint.method.returnEarly(null as Void?)
        OnClickFingerprint.method.returnEarly()
        BFingerprint.method.returnEarly(true)
        IsFullEnergyRefillItemFingerprint.method.returnEarly(true)
        IsGoldSubscriptionFingerprint.method.returnEarly(true)
        IsIapItemFingerprint.method.returnEarly(true)
        IsIapReadyFingerprint.method.returnEarly(true)
        IsLiteSubscriptionFingerprint.method.returnEarly(true)
        IsPromoFingerprint.method.returnEarly(true)
        IsSubscriptionFingerprint.method.returnEarly(true)
        HasSubscriptionFingerprint.method.returnEarly(true)
    }
}
