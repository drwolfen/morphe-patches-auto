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
    description = "Auto-generated: unlocks features gated by com.urbandroid.sleep paywall checks."
) {
    compatibleWith(Compat.SLEEP)

    execute {
        IsUnlockAckedFingerprint.method.returnEarly(true)
        HasUnlockFingerprint.method.returnEarly(true)
        IsTrialFingerprint.method.returnEarly(false)
        DFingerprint.method.returnEarly(null as Void?)
    }
}
