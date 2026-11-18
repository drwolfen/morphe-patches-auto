package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val zeroGemCostPatch = bytecodePatch(
    name = "Zero Gem Cost",
    description = "Sets every shop item gem price to 0.",
    default = true
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        ShopItemInitFingerprint.method.addInstruction(0, "const/4 p3, 0x0")
        ShopItemPriceFingerprint.method.returnEarly(0)
        ShopItemIAPItemPriceFingerprint.method.returnEarly(0)
    }
}
