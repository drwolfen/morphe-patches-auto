/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.bowerydigital.bend v7.8.0 — do not edit by hand.
 */

package standardrepo.morphe.patches.bend.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Auto-generated: unlocks Bend Premium — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.BEND)

    execute {
        ProcessResponseFingerprint.method.addInstruction(0, "const/4 p1, 0x0")
        ValidateResponseFingerprint.method.returnEarly()
        VerifySignatureMatchesFingerprint.method.returnEarly(true)
        VerifyIntegrityFingerprint.method.returnEarly()
        GetStoreIdFromSystemFingerprint.method.returnEarly(null as Void?)
        CFingerprint.method.returnEarly(true)
        AFingerprint.method.returnEarly(true)
    }
}
