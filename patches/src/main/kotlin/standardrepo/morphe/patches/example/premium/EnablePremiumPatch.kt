/**
 * Copyright 2026 crackpipe
 * Generated example — template reference for the pipeline generator.
 */

package standardrepo.morphe.patches.example.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Enables app features locked behind the subscription paywall (template demo)."
) {
    // SCOPED to the template's example package, NOT universal: a universal patch is applicable to
    // every package, and the Manager then lists this demo patch on EVERY app card — so each real
    // app showed two patches (its own + this demo). Scoping it keeps exactly ONE patch per app.
    // It stays usable as the gate's negative-control target: fingerprint present in the sample
    // APK, package declared only here.
    compatibleWith(Compat.EXAMPLE)

    execute {
        // Tolerant: the demo fingerprint does not exist in a real app's dex, so a strict .method
        // would throw; methodOrNull returns null when unmatched.
        HasUnlockFingerprint.methodOrNull?.returnEarly(true)
    }
}
