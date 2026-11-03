/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstructions
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction23x
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import standardrepo.morphe.compatibility.Compat

/**
 * Investing.com masks Fair Value with a placeholder (111.11) for non-Pro accounts.
 * FeaturedStockResponse.e is the live price, Predictions.c is the upside fraction.
 * fair value = price * (1 + upside).
 */
val fixFairValuePatch = bytecodePatch(
    name = "Fix: Fair Value",
    description = "Recomputes the masked Fair Value from price and upside.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val m = mutableClassDefByOrNull("Lkze;")?.methods
            ?.firstOrNull { it.name == "a" && it.implementation != null }
            ?: error("FV: no Lkze.a mapper")
        val insns = m.implementation!!.instructions.toList()
        val sites = insns.indices.filter { i ->
            val ins = insns[i]
            val ref = (ins as? ReferenceInstruction)?.reference as? FieldReference
                ?: return@filter false
            ins.opcode == Opcode.IGET_WIDE && ref.name == "b" && ref.type == "D" &&
                ref.definingClass.endsWith("/Predictions;")
        }
        require(sites.size == 2) { "FV expected 2 sites, got ${sites.size}" }

        val resp = "Lcom/fusionmedia/investing/feature/featured/stock/data/response/FeaturedStockResponse;"
        val price = "Lcom/fusionmedia/investing/feature/featured/stock/data/response/Price;"
        val priceRef = ImmutableFieldReference(resp, "e", price)
        val valueRef = ImmutableFieldReference(price, "a", "D")

        sites.sortedDescending().forEach { i ->
            m.replaceInstructions(i, listOf(BuilderInstruction22c(Opcode.IGET_OBJECT, 11, 2, priceRef)))
            m.addInstruction(i + 1, BuilderInstruction22c(Opcode.IGET_WIDE, 12, 11, valueRef))
            m.addInstruction(i + 2, BuilderInstruction23x(Opcode.MUL_DOUBLE, 14, 12, 5))
            m.addInstruction(i + 3, BuilderInstruction23x(Opcode.ADD_DOUBLE, 9, 14, 12))
        }
    }
}
