/**
 * Copyright 2026 crackpipe
 * Generated example — template reference for the pipeline generator.
 */

package hoodles.morphe.patches.example.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import hoodles.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Enables app features locked behind the subscription paywall."
) {
    compatibleWith(Compat.EXAMPLE)

    execute {
        HasUnlockFingerprint.method.returnEarly(true)
    }
}
