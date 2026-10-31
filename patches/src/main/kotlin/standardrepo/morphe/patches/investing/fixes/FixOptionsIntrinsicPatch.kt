/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import standardrepo.morphe.compatibility.Compat

@Suppress("unused")
val fixOptionsIntrinsicPatch = bytecodePatch(
    name = "Fix: Options intrinsic parse",
    description = "The options chain response omits `intrinsic` on every row, so the generated " +
        "adapter throws `Required value 'intrinsic' missing` and the Options tab dies with " +
        "\"Update failed\". Seeds the field with a boxed 0.0 and routes the missing-value branch " +
        "past the throw, so the chain renders.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val type = "Lcom/fusionmedia/investing/feature/options/data/response/OptionsItemResponseJsonAdapter;"
        val m = mutableClassDefByOrNull(type)?.methods?.firstOrNull { it.name == "fromJson" } ?: return@execute
        val ins = m.implementation!!.instructions

        val sw = ins.indexOfFirst { it.opcode.name == "packed-switch" }
        if (sw < 0) return@execute

        // `intrinsic` is the case whose throw block does `move-object/from16 v2, v60` (v60 <- v6
        // = "intrinsic"); it writes the ctor slot v13, which the common move block refills from
        // the field slot v37. Seed v37.
        val join = ins.indexOfFirst {
            it.opcode.name == "move-object/from16" &&
                (it as? TwoRegisterInstruction)?.registerA == 14 &&
                (it as? TwoRegisterInstruction)?.registerB == 38
        }
        if (join < 0) return@execute


        // Every missing-value guard in this adapter jumps to a shared throw block. Replace each
        // with `nop` so a null parsed value falls through to the common move block, which carries
        // the seeded field slots into the constructor. The throw blocks become dead code.
        val guards = (join until ins.size).filter { i ->
            ins[i].opcode.name == "if-eqz" &&
                (i + 1 until minOf(i + 20, ins.size)).any { ins[it].opcode.name == "throw" }
        }
        if (guards.isEmpty()) return@execute
        guards.forEach { m.replaceInstruction(it, "nop") }

        // v2/v3 are scratch before the switch, but the (dead) throw blocks read them as Strings,
        // so they are restored to "" after the seed; the move block re-points them at v25/v26.
        m.addInstructions(
            sw,
            """
            const-wide/16 v2, 0x0
            invoke-static {v2, v3}, Ljava/lang/Double;->valueOf(D)Ljava/lang/Double;
            move-result-object v2
            move-object/from16 v37, v2
            const-string v2, ""
            const-string v3, ""
            """.trimIndent(),
        )
    }
}
