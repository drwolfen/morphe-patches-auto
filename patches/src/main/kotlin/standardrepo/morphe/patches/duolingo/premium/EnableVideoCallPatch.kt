package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enableVideoCallPatch = bytecodePatch(
    name = "Enable Video Calling",
    description = "Enables Duolingo video call (Max) features.",
    default = false
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        IsImmersiveFingerprint.method.returnEarly(true)
        HasVideoCallFingerprint.method.returnEarly(true)
    }
}
