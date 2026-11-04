/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import standardrepo.morphe.compatibility.Compat

/**
 * Investing.com masks Fair Value with a placeholder when the account is not Pro.
 * `Lkze.a` reads `Predictions.b` twice (once for the value string, once for the row model)
 * without ever deriving it from price and upside.
 *
 * Both reads are replaced by price * (1 + upside):
 *   price object -> v13 (dead), price.a -> v9/v10 (the read's own pair),
 *   1.0 -> v12/v13, 1+upside -> v12/v13, result -> v9/v10.
 * The upside fraction v5/v6 is live at both sites and v9/v10 is where the original
 * `Double.valueOf(v9, v10)` consumer expects the value, so nothing else moves.
 */
val fixFairValuePatch = bytecodePatch(
    name = "Fix: Fair Value",
    description = "Recomputes the masked Fair Value as price * (1 + upside) from the parsed fields.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val m = mutableClassDefByOrNull("Lkze;")?.methods
            ?.firstOrNull { it.name == "a" && it.implementation != null }
            ?: error("FV: no Lkze.a mapper")

        fun siteIn(list: List<com.android.tools.smali.dexlib2.builder.BuilderInstruction>): Int? =
            list.indices.firstOrNull { idx ->
                val ins = list[idx]
                val ref = (ins as? ReferenceInstruction)?.reference as? FieldReference
                if (ins.opcode == Opcode.IGET_WIDE && ref?.name == "b" && ref.type == "D" &&
                    ref.definingClass.endsWith("/Predictions;")) true else false
            }

        val respT = "Lcom/fusionmedia/investing/feature/featured/stock/data/response/FeaturedStockResponse;"
        val priceT = "Lcom/fusionmedia/investing/feature/featured/stock/data/response/Price;"

        repeat(2) {
            val impl = m.implementation ?: error("FV: no impl")
            val i = siteIn(impl.instructions.toList()) ?: error("FV: site missing")
            m.replaceInstruction(
                i,
                "iget-object v13, v2, $respT->e:$priceT",
            )
            m.addInstructions(
                i + 1,
                """
                    iget-wide v9, v13, $priceT->a:D
                    mul-double v12, v9, v5
                    add-double v9, v9, v12
                """.trimIndent(),
            )
        }
    }
}
