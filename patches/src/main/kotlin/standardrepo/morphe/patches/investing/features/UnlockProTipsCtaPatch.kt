/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import standardrepo.morphe.compatibility.Compat

@Suppress("unused")
val unlockProTipsCtaPatch = bytecodePatch(
    name = "Unlock: Pro Tips CTA",
    description = "Blanks the `Unlock All N Tips` CTA label the Pro Tips section builds from the " +
        "`unlock_all_tips` localized string, so the orange upgrade button no longer sits under the tips.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        // `Llbf.a` is the ProTips section mapper: it fetches the tips, then builds both CTA labels
        // from the localized holders `Lp8p;.c` ("Unlock All %NUMBER% Tips") and `Lp8p;.d`
        // ("View All %NUMBER% Tips"). The orange upgrade button is drawn from the first one.
        val cd = mutableClassDefByOrNull("Llbf;") ?: return@execute
        val m = cd.methods.firstOrNull { it.name == "a" } ?: return@execute
        val impl = m.implementation ?: return@execute
        val ins = impl.instructions.toList()
        val idx = ins.indexOfFirst {
            it.opcode.name == "sget-object" &&
                (it as? ReferenceInstruction)?.reference?.toString() == "Lp8p;->c:Lr0p;"
        }
        if (idx < 0) return@execute
        // The label lands in a register via the following `move-result-object`; blank it there so
        // the `%NUMBER%` substitution later in the block leaves an empty CTA label.
        val ret = ins.withIndex().firstOrNull { it.index > idx && it.value.opcode.name == "move-result-object" }
            ?: return@execute
        val reg = (ret.value as? OneRegisterInstruction)?.registerA ?: return@execute
        impl.addInstruction(ret.index + 1, BuilderInstruction21c(Opcode.CONST_STRING, reg, ImmutableStringReference("")))
    }
}
