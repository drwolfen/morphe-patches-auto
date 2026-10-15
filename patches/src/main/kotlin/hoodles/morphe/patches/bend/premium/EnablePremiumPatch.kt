/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.bowerydigital.bend v7.8.0 — do not edit by hand.
 */

package hoodles.morphe.patches.bend.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import hoodles.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Auto-generated: unlocks features gated by com.bowerydigital.bend paywall checks."
) {
    compatibleWith(Compat.BEND)

    execute {
        ProcessResponseFingerprint.method.returnEarly()
        ValidateResponseFingerprint.method.returnEarly()
        LaunchFingerprint.method.returnEarly()
    }
}
