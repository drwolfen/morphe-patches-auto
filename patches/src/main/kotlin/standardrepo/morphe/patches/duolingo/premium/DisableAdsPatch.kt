package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val disableAdsPatch = bytecodePatch(
    name = "Disable Ads",
    description = "Disables Duolingo ads by forcing AdsConfig.b() to false.",
    default = false
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        AdsConfigFingerprint.method.returnEarly(false)
    }
}
