/**
 * Copyright 2026 crackpipe
 * Duolingo 6.105.1 - Max tier.
 */

package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22c
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Max",
    description = "Unlocks Duolingo Max: Max badge, subscription gates and immersive (Max) subscription."
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        // Force User.D0 (subscriber level) to GOLD at every User constructor's D0 write.
        // Inserting the sget-object right before the iput reuses the iput's own value
        // register, so no extra register is needed (cfa0.<init>(User) only has 2).
        for (method in mutableClassDefBy("Lcom/duolingo/data/user/User;").methods) {
            if (method.name != "<init>" || method.returnType != "V") continue
            val impl = method.implementation ?: continue
            val idx = impl.instructions.indexOfFirst { insn ->
                insn.opcode == Opcode.IPUT_OBJECT &&
                    insn is BuilderInstruction22c &&
                    (insn.reference as? FieldReference)?.let {
                        it.name == "D0" && it.definingClass == "Lcom/duolingo/data/user/User;"
                    } == true
            }
            if (idx < 0) continue
            val reg = (impl.instructions[idx] as BuilderInstruction22c).registerA
            method.addInstructions(
                idx,
                "sget-object v$reg, Lcom/duolingo/data/user/SubscriberLevel;->GOLD:Lcom/duolingo/data/user/SubscriberLevel;"
            )
        }
        SubscriptionTenureInitFingerprint.method.addInstructions(0, """
            const/4 p1, 0x3
sget-object p3, Lcom/duolingo/data/user/SubscriptionItemType;->IMMERSIVE_GOLD:Lcom/duolingo/data/user/SubscriptionItemType;
        """)
        HasSubscriptionFingerprint.method.returnEarly(true)

        // Max (GOLD) must also satisfy every Super-tier predicate: the app compares
        // User.D0 against SubscriberLevel.PREMIUM in several gates (e.g. video-call hook
        // routing / subscription state flows). Alias PREMIUM to the GOLD instance inside
        // <clinit> so `D0 == PREMIUM` is true while `== GOLD` stays true -> Max and Super
        // features are unlocked together without losing the MAX badge.
        val levelClass = mutableClassDefBy("Lcom/duolingo/data/user/SubscriberLevel;")
        val clinitMethod = levelClass.methods.firstOrNull { it.name == "<clinit>" }
        if (clinitMethod != null) {
            val impl = clinitMethod.implementation
            if (impl != null) {
                val retIdx = impl.instructions.indexOfLast { insn -> insn.opcode == Opcode.RETURN_VOID }
                if (retIdx >= 0) {
                    clinitMethod.addInstructions(
                        retIdx,
                        "sget-object v0, Lcom/duolingo/data/user/SubscriberLevel;->GOLD:Lcom/duolingo/data/user/SubscriberLevel;\nsput-object v0, Lcom/duolingo/data/user/SubscriberLevel;->PREMIUM:Lcom/duolingo/data/user/SubscriberLevel;"
                    )
                }
            }
        }
    }
}
