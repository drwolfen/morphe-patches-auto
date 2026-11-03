/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.extensions.InstructionExtensions.replaceInstructions
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import standardrepo.morphe.compatibility.Compat

/**
 * Investing.com ships a *masked* fair value for non-Pro accounts: the digits are all `1`
 * (`11111` -> `111.11`, `1111` -> `11.11`) and the percentage is always `+-111`.
 *
 * The real numbers are already in the response: `FeaturedStockResponse.e` carries the live
 * price and `Predictions.c` carries the upside as a fraction. The server derives the fair
 * value as `price * (1 + upside)`, so we recompute it locally and feed the computed value
 * into the analyst/valuation card model (`faj`) instead of the placeholder.
 *
 * The two `Predictions.b` reads in `Lkze.a` (the FeaturedStock -> overview mapper) are the
 * only places the masked fair value enters the model.
 */
@Suppress("unused")
val fixFairValuePatch = bytecodePatch(
    name = "Fix: Fair Value",
    description = "Replaces the masked Fair Value placeholder (111.11 / 11111) with a value computed from the live price and the response upside so the Fair Value card is accurate.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val m = mutableClassDefByOrNull("Lkze;")?.methods
            ?.firstOrNull { it.name == "a" && it.implementation != null }
            ?: error("FV: no Lkze.a(Lr54;)Ljava/lang/Object; mapper")
        val insns = m.implementation!!.instructions.toList()
        val sites = insns.indices.filter { i ->
            val ins = insns[i]
            val ref = (ins as? ReferenceInstruction)?.reference as? FieldReference
                ?: return@filter false
            ins.opcode == Opcode.IGET_WIDE && ref.name == "b" && ref.type == "D" &&
                ref.definingClass.endsWith("/Predictions;")
        }
        require(sites.size == 2) { "FV: expected 2 Predictions.b:D reads in Lkze.a, got ${sites.size}" }

        // fair = price + price * upside   (v2 = FeaturedStockResponse, v5 = Predictions.c)
        val block = """
            iget-object v34, v2, Lcom/fusionmedia/investing/feature/featured/stock/data/response/FeaturedStockResponse;->e:Lcom/fusionmedia/investing/feature/featured/stock/data/response/Price;
            iget-wide v24, v34, Lcom/fusionmedia/investing/feature/featured/stock/data/response/Price;->a:D
            mul-double v15, v24, v5
            add-double v9, v15, v24
        """.trimIndent()

        sites.forEach { i -> println("FVSITE $i ${insns[i]}") }
        sites.sortedDescending().forEach { m.replaceInstructions(it, block) }
    }
}
