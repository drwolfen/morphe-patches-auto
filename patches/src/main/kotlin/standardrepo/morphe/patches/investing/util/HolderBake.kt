/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.util

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

/**
 * Rewrite every read of a Pairip holder field to a const-string literal.
 *
 * Gutting the Pairip VM means holder `<clinit>`s never run, so each holder read is null at
 * runtime. The main patch bakes the whole holder set from `knownHolderValues`; a single-field
 * bake here is what a one-feature/fix sub-patch needs, without dragging the whole pass along.
 */
fun BytecodePatchContext.bakeHolderValue(holderType: String, field: String, value: String) {
    val lit = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
    classDefForEach { cd ->
        for (m in cd.methods) {
            val ins = m.implementation?.instructions?.toList() ?: continue
            if (ins.none { it.opcode.name == "sget-object" }) continue
            val target = mutableClassDefByOrNull(cd.type)?.methods
                ?.firstOrNull { it.name == m.name && it.returnType == m.returnType && it.parameterTypes == m.parameterTypes }
                ?: continue
            for ((idx, insn) in ins.withIndex()) {
                if (insn.opcode.name != "sget-object") continue
                val ref = (insn as? ReferenceInstruction)?.reference as? FieldReference ?: continue
                if (ref.definingClass != holderType || ref.name != field) continue
                val reg = (insn as OneRegisterInstruction).registerA
                target.replaceInstruction(idx, "const-string v$reg, $lit")
            }
        }
    }
}

/**
 * Force every store of `ownerType.field` to write false.
 *
 * Server responses carry per-datum `locked` booleans (the server sees no subscription), so the
 * app renders the lock whatever the client-side Pro gates answer. A `const/4` inserted in front
 * of each `iput-boolean` overwrites the parsed value with 0, so the field reads false everywhere
 * (UI, `copy`, `toString`) with no verifier-fragile register juggling.
 */
fun BytecodePatchContext.forceFalseField(ownerType: String, field: String) {
    classDefForEach { cd ->
        for (m in cd.methods) {
            val ins = m.implementation?.instructions?.toList() ?: continue
            val target = mutableClassDefByOrNull(cd.type)?.methods
                ?.firstOrNull { it.name == m.name && it.returnType == m.returnType && it.parameterTypes == m.parameterTypes }
                ?: continue
            val sites = ArrayList<Pair<Int, Int>>()
            for ((idx, insn) in ins.withIndex()) {
                if (insn.opcode.name != "iput-boolean") continue
                val ref = (insn as? ReferenceInstruction)?.reference as? FieldReference ?: continue
                if (ref.definingClass != ownerType || ref.name != field) continue
                sites.add(idx to (insn as OneRegisterInstruction).registerA)
            }
            for ((idx, reg) in sites.asReversed())
                target.addInstruction(idx, if (reg < 16) "const/4 v$reg, 0x0" else "const/16 v$reg, 0x0")
        }
    }
}
