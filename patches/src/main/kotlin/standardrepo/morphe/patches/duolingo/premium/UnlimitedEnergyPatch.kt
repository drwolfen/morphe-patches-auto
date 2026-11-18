package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat

val unlimitedEnergyPatch = bytecodePatch(
    name = "Unlimited Energy",
    description = "Sets energy current to max by mirroring max into current.",
    default = false
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        EnergyConfigInitFingerprint.method.addInstruction(0, "move p1, p2")
    }
}
