/**
 * Copyright 2026 crackpipe
 * Auto-generated for apps.automan.blurwallpaper v3.0.0 — do not edit by hand.
 */

package standardrepo.morphe.patches.blurwallpaper.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Pro",
    description = "Auto-generated: unlocks Blurwallpaper Pro — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.BLURWALLPAPER)

    execute {
        ProcessResponseFingerprint.method.addInstruction(0, "const/4 p1, 0x0")
        ValidateResponseFingerprint.method.returnEarly()
        PerformLocalInstallerCheckFingerprint.method.returnEarly(true)
    }
}
