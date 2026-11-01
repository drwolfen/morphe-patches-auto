/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import standardrepo.morphe.compatibility.Compat

/**
 * The Instrument Insights response carries the real fair value in `j:D`, but the mapper passes
 * `c:I` — a server-side placeholder that is always `11111` (rendered `111.11`) — into the
 * Prediction model. Repoint it at `j`, scaled to the cents int the UI expects.
 */
@Suppress("unused")
val fixFairValuePatch = bytecodePatch(
    name = "Fix: Fair Value",
    description = "Replaces the constant `111.11` fair value with the real `fairValue` from the " +
        "instrument-insights response so the Fair Value tile and the Key Statistics row are accurate.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val m = mutableClassDefByOrNull("Lc2f;")?.methods?.firstOrNull { m ->
            m.implementation?.instructions?.any { it.refersTo("Lngc;.c:I") } == true
        } ?: return@execute
        val insns = m.implementation!!.instructions
        val idx = insns.indexOfFirst {
            it.opcode.name == "iget" && it.toString().contains("c:I")
        }
        if (idx < 0) return@execute
        m.replaceInstruction(
            idx,
            """
            iget-wide v9, v11, Lngc;.j:D
            double-to-int v9, v9
            const/16 v8, #int 100
            mul-int/2addr v9, v8
            """.trimIndent(),
        )
    }
}

/** True when this instruction's field reference is `descriptor` (e.g. `Lngc;.c:I`). */
private fun com.android.tools.smali.dexlib2.iface.instruction.Instruction.refersTo(desc: String): Boolean =
    (this as? ReferenceInstruction)?.reference?.toString()?.contains(desc.substringBefore(':')) == true
