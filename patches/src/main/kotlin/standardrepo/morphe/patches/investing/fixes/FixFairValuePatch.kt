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
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import standardrepo.morphe.compatibility.Compat

/**
 * Investing.com masks Fair Value with a placeholder when the account is not Pro.
 * `Lkze.a` reads `Predictions.b` twice (once for the value string, once for the row model)
 * without ever deriving it from price and upside.
 *
 * Both reads are replaced by price * (1 + upside). Registers are picked from those dead
 * at both sites; v11 (String), v12 (String) and v13 (Lfej holder) are live through to the
 * `Lbg9` constructor and must not be touched:
 *   Price object -> v33, price.a -> v19/v20, product -> v34/v35, result -> v9/v10.
 * v9/v10 is where the read's own `Double.valueOf(v9, v10)` consumer expects the value.
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
                "iget-object v10, v2, $respT->e:$priceT",
            )
            m.addInstructions(
                i + 1,
                """
                    iget-wide v14, v10, $priceT->a:D
                    mul-double v9, v14, v5
                    add-double v9, v14, v9
        add-double v9, v9, v9
        iget v14, v1, Lhkc;->b:I
                """.trimIndent(),
            )
        }

        // Header path: `Lxwj.c` builds the `Lnlc` view model whose `e` (Predictions.e) is the
        // Fair Value actually rendered in the ticker header. That read is replaced by
        // price * (1 + upside), where price = Predictions.c and upside = the parsed `i` string.
        val predT = "Lcom/fusionmedia/investing/feature/featured/stock/data/response/Predictions;"
        val m2 = mutableClassDefByOrNull("Lxwj;")?.methods
            ?.firstOrNull { it.name == "c" && it.implementation != null }
            ?: error("FV: no Lxwj.c mapper")
        val insns = m2.implementation!!.instructions.toList()
        val start = insns.indexOfFirst { ins ->
            val ref = (ins as? ReferenceInstruction)?.reference as? FieldReference
            ins.opcode == Opcode.IGET_OBJECT && ref?.name == "e" &&
                ref.type == "Ljava/lang/Double;"
        }
        if (start < 0) error("FV: header site missing")
        val newCode = listOf(
            "mul-double v15, v5, v11",
            "add-double v15, v5, v15",
            "nop",
            "nop",
            "nop",
            "move-wide v15, v15",
        )
        for ((k, c) in newCode.withIndex()) m2.replaceInstruction(start + k, c)

        // Renderer `Lq7n` formats the header value from `Lnlc.e`; use the recomputed
        // `Lfaj.c` (which already carries price * (1 + upside)) instead.
        val m3 = mutableClassDefByOrNull("Lq7n;")?.methods
            ?.firstOrNull { m ->
                m.implementation?.instructions?.any { ins ->
                    val ref = (ins as? ReferenceInstruction)?.reference as? FieldReference
                    ins.opcode == Opcode.IGET_WIDE && ref?.name == "e" &&
                        ref.type == "D" && ref.definingClass == "Lnlc;"
                } == true
            } ?: error("FV: no Lq7n renderer")
        val idx = m3.implementation!!.instructions.toList().indexOfFirst { ins ->
            val ref = (ins as? ReferenceInstruction)?.reference as? FieldReference
            ins.opcode == Opcode.IGET_WIDE && ref?.name == "e" &&
                ref.type == "D" && ref.definingClass == "Lnlc;"
        }
        if (idx < 0) error("FV: renderer site missing")
        m3.replaceInstruction(idx, "iget-wide v8, v7, Lfaj;->c:D")

        // Mapper `Lalc.a` builds the rendered `Lkkc` card from `FairValue.a` (the masked
        // server value). `Lalc.e` is the `Lafj` computed holder; use its `c` (real FV) instead.
        val m5 = mutableClassDefByOrNull("Lalc;")?.methods
            ?.firstOrNull { m ->
                m.implementation?.instructions?.any { ins ->
                    val ref = (ins as? ReferenceInstruction)?.reference as? FieldReference
                    ins.opcode == Opcode.IGET_WIDE && ref?.name == "a" && ref.type == "D" &&
                        ref.definingClass.endsWith("Insight\$FairValue;")
                } == true
            } ?: error("FV: no Lalc mapper")
        val i5 = m5.implementation!!.instructions.toList().indexOfFirst { ins ->
            val ref = (ins as? ReferenceInstruction)?.reference as? FieldReference
            ins.opcode == Opcode.IGET_WIDE && ref?.name == "a" && ref.type == "D" &&
                ref.definingClass.endsWith("Insight\$FairValue;")
        }
        if (i5 < 0) error("FV: Lalc site missing")
        m5.addInstructions(i5 + 1, "add-double v13, v24, v26")
        m5.addInstructions(i5 + 1, "mul-double v26, v24, v13")
        m5.addInstructions(i5 + 1, "iget-wide v13, v2, Lcom/fusionmedia/investing/features/instrumentinsights/data/response/InstrumentInsightsResponse${'$'}Insight${'$'}FairValue;->b:D")
        m5.addInstructions(i5 +1, "move-wide/from16 v24, v13")

        // Probe: overwrite the `Lfej` value string built in `Lkze.a` with a constant so the
        // header's real value source can be identified empirically.
        val m4 = mutableClassDefByOrNull("Lkze;")?.methods
            ?.firstOrNull { m ->
                m.implementation?.instructions?.any { ins ->
                    ins.opcode == Opcode.NEW_INSTANCE &&
                        (ins as? ReferenceInstruction)?.reference?.toString() == "Lfej;"
                } == true
            } ?: error("FV: no Lfej site")
        val insList4 = m4.implementation!!.instructions.toList()
        val fIdx = insList4.indexOfFirst { ins ->
            ins.opcode == Opcode.NEW_INSTANCE &&
                (ins as? ReferenceInstruction)?.reference?.toString() == "Lfej;"
        }
        if (fIdx < 0) error("FV: Lfej site missing")
        val fmtIdx = (fIdx + 1 until insList4.size).firstOrNull { k ->
            val ins = insList4[k]
            val ref = (ins as? ReferenceInstruction)?.reference as? MethodReference
            ins.opcode.toString().startsWith("INVOKE") && ref?.name == "a" && ref.definingClass == "Lzlf;"
        } ?: error("FV: Lfej format missing")
        m4.replaceInstruction(fmtIdx + 1, "const-string v5, \"1234\"")
    }
}