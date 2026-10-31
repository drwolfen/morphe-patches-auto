/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rc
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21s
import standardrepo.morphe.compatibility.Compat

/**
 * Unlock: ProPicks AI values (own crack, separate from Enable Pro).
 *
 * The AI-picked cards render a reduced, locked row for every pick — masked identity, no
 * price/return rows — and the pager pads that out with a "Get The Full List" CTA. The per-card
 * `identityLocked` boolean is the last ctor arg of the card model `gnc`; both mappers (Current
 * `p3f.a`, Previous `q3f.b`) take it from the server's `is_locked`, which is true for everyone.
 * Forcing that arg to 0 switches every card to the `identityUnlocked` layout: symbol, name,
 * price row and return row all render, and the CTA stops padding the list.
 *
 * The real mapper is the method that actually constructs `gnc` — the same class also carries a
 * thin synthetic bridge of the same name that must be skipped.
 */
@Suppress("unused")
val unlockProPicksValuesPatch = bytecodePatch(
    name = "Unlock: ProPicks values",
    description = "Shows the real tickers and values on the AI ProPicks cards instead of the masked " +
        "layout, and removes the \"Get The Full List\" CTA.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val sites = listOf(
            "Lp3f;" to "a",
            "Lq3f;" to "b",
        )
        val ctor =
            "Lgnc;-><init>(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;" +
                "Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;ILjava/lang/String;" +
                "ILwn9;Z)V"
        for ((type, name) in sites) {
            val cd = mutableClassDefByOrNull(type) ?: continue
            for (m in cd.methods) {
                if (m.name != name) continue
                val impl = m.implementation ?: continue
                val idx = impl.instructions.indexOfFirst {
                    it.opcode.name == "invoke-direct/range" &&
                        (it as? ReferenceInstruction)?.reference?.toString() == ctor
                }
                if (idx < 0) continue
                // The last register of the range invoke is the `identityLocked` arg.
                val ri = impl.instructions[idx] as? Instruction3rc ?: continue
                val last = ri.startRegister + ri.registerCount - 1
                impl.addInstruction(idx, BuilderInstruction21s(Opcode.CONST_16, last, 0))
                break
            }
        }
    }
}
