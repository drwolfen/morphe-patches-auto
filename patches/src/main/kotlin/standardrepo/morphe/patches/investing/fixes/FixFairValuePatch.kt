/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import standardrepo.morphe.compatibility.Compat

/**
 * Investing.com ships a *masked* fair value for non-Pro accounts: digits are all `1`
 * (11111 -> 111.11) and the percentage is always +-111.
 *
 * The real numbers are in the response: `FeaturedStockResponse.e:Price` carries the live
 * price and `Predictions.c:D` carries the upside as a fraction; the server derives the fair
 * value as price * (1 + upside). We recompute it and feed it into the analyst card model.
 */
@Suppress("unused")
val fixFairValuePatch = bytecodePatch(
    name = "Fix: Fair Value",
    description = "Replaces the masked Fair Value placeholder with a value computed from the live price and upside.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val m = mutableClassDefByOrNull("Lkze;")?.methods
            ?.firstOrNull { it.name == "a" && it.implementation != null }
            ?: error("FV: no Lkze.a mapper")
        val insns = m.implementation!!.instructions
        val sites = insns.indices.filter { i ->
            (insns[i] as? ReferenceInstruction)?.reference?.toString()
                ?.endsWith("Predictions;->b:D") == true
        }
        if (sites.size != 2) error("FV: expected 2 Predictions->b:D reads, got ${sites.size}")

        sites.sortedDescending().forEach { i ->
            m.replaceInstruction(i, "iget-object v34, v2, Lcom/fusionmedia/investing/feature/featured/stock/data/response/FeaturedStockResponse;->e:Lcom/fusionmedia/investing/feature/featured/stock/data/response/Price;")
            m.addInstructions(
                i + 1,
                """
                iget-wide v24, v34, Lcom/fusionmedia/investing/feature/featured/stock/data/response/Price;->a:D
                mul-double v19, v24, v5
                add-double v9, v19, v24
                """.trimIndent(),
            )
        }
    }
}
