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
    description = "Enables app features locked behind the subscription paywall."
) {
    // Deliberately NO compatibleWith(): a patch with no compatibility is UNIVERSAL, so the Manager
    // puts no app on the home screen for it (PatchBundleInfo.listedApps names no app) — the template's
    // demo patch is therefore never offered to users as an "Example App". It stays in the bundle so
    // the gate can use its fingerprint as the negative-control target.

    execute {
        // Tolerant: on a real app (sleep/duo/bend) the demo fingerprint does not exist, and a
        // universal patch is applicable to every package, so a strict .method would throw and
        // break every real patching run. methodOrNull returns null when unmatched.
        HasUnlockFingerprint.methodOrNull?.returnEarly(true)
    }
}
