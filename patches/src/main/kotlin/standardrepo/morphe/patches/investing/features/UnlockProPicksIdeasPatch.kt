/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rc
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21s


/**
 * Unlock: ProPicks AI ideas (separate crack, folded into the main patch).
 *
 * The AI-picked ideas cards render a reduced, locked row for every pick — masked identity, no
 * price/return rows — and the pager pads that out with a "Get The Full List" CTA. The per-card
 * `identityLocked` boolean is the last ctor arg of the card model `gnc`; both mappers (Current
 * `p3f.a`, Previous `q3f.b`) feed it from the server's `is_locked`, which is always true for a
 * guest. Forcing that arg to 0 switches every card to the `identityUnlocked` layout: symbol,
 * name, price row and return row all render, and the CTA no longer pads the list.
 *
 * The real mapper is the method that actually constructs `gnc` — the same class also carries a
 * thin synthetic bridge of the same name that must be skipped.
 */
fun BytecodePatchContext.unlockProPicksIdeas() {
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
        java.io.File("/tmp/site.log").appendText("PP cls $type n=" + cd.methods.size + "\n")
        for (m in cd.methods) {
            if (m.name != name) continue
            val impl = m.implementation ?: continue
            java.io.File("/tmp/site.log").appendText("PP m " + m.name + " " + m.parameterTypes.size + " sz=" + runCatching { impl.instructions.size }.getOrElse { -1 } + "\n")
            val idx = impl.instructions.indexOfFirst {
                it.opcode.name == "invoke-direct/range" &&
                    (it as? ReferenceInstruction)?.reference?.toString() == ctor
            }
            if (idx < 0) continue
            // The last register of the range invoke is the `identityLocked` arg.
            java.io.File("/tmp/site.log").appendText("PP idx=$idx\n")
            val ri = impl.instructions[idx] as? Instruction3rc ?: continue
            val last = ri.startRegister + ri.registerCount - 1
            impl.addInstruction(idx, BuilderInstruction21s(Opcode.CONST_16, last, 0))
            break
        }
    }
}
