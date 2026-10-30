/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat

@Suppress("unused")
val fixHealthScorecardParsePatch = bytecodePatch(
    name = "Fix: Financial Health parse",
    description = "Pre-seeds the scorecard's numeric fields with a boxed 0.0 so a response missing " +
        "`minScore`/`score`/`maxScore` parses instead of throwing `Missing field in Scorecard` " +
        "and blanking Financial Health and the Health tab.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val type = "Lcom/fusionmedia/investing/feature/financial/health/instrument/mutual/data/response/FinancialHealthResponse_ScorecardJsonAdapter;"
        val m = mutableClassDefByOrNull(type)?.methods?.firstOrNull { it.name == "fromJson" } ?: return@execute
        val idx = m.implementation!!.instructions.indexOfFirst { it.opcode.name == "packed-switch" }
        if (idx < 0) return@execute
        // `minScore`(c), `score`(d) and `max_score`(e) are boxed Doubles in v16, v17 and v6; all
        // three start null, so a missing key trips the generated Missing-field throw. Seed each
        // with a boxed 0.0 — the case branches overwrite it with the real value when present.
        // v2/v3 are scratch here; v2 is restored to the `history` key the throw block needs.
        val block = ArrayList<String>()
        for (reg in listOf("v16", "v17", "v6")) {
            block += listOf(
                "const-wide/16 v2, 0x0",
                "invoke-static {v2, v3}, Ljava/lang/Double;->valueOf(D)Ljava/lang/Double;",
                "move-result-object v2",
                "move-object/from16 $reg, v2",
            )
        }
        block += listOf(
            "const-string v2, \"history\"",
            // v3 is the `d` double adapter the minScore case expects; const-wide stole its high
            // half, so reload it exactly as the code did before the switch.
            "iget-object v3, v0, $type->d:Lend;",
        )
        m.addInstructions(idx, block.joinToString("\n"))
    }
}
