/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import standardrepo.morphe.compatibility.Compat

/**
 * Remove the "Subscription is Active" screen shown at every app start.
 *
 * The screen is composed by the tab content host `Lb41.e`: it calls `Lqpp.b()Z` and composes the
 * subscription screen (`Lb41.p`) when that returns true, otherwise the normal tab content
 * (`Lb41.q`). `Lqpp` is the per-screen subscription state holder; `b()` ORs the two boolean state
 * flows in `Larg.d`/`Larg.e` that the server subscription state sets. Making `Lqpp.b()` return
 * false keeps every tab on its normal content and the screen never renders.
 *
 * Also keeps guest startup off the fake-signup navigation command: `Lxh4` picks `Lvd8`
 * (`FakeSignUpNavigationCommand`) for a subscribed guest and `Lfm8` for the normal startup
 * destination; that field reference is rewritten to `Lfm8` in the emitter and in the nav host
 * handler `Lte0`, so startup lands on the normal destination and in-app "Create account" entries
 * keep working. `Ly5o.b` (`subscription_active`) is pinned to true so the subscription UI variant
 * is used consistently.
 */
val hideSubscriptionActiveScreenPatch = bytecodePatch(
    name = "Remove: Subscription is Active screen",
    description = "Removes the Subscription is Active screen shown at startup and enters the app directly.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        // Hard gate: the screen never composes.
        val qpp = mutableClassDefByOrNull("Lqpp;") ?: error("SUB: no Lqpp state holder")
        val show = qpp.methods.firstOrNull {
            it.name == "b" && it.returnType == "Z" && it.parameterTypes.isEmpty() && it.implementation != null
        } ?: error("SUB: no Lqpp.b()Z")
        show.returnEarly(false)

        // Guest startup emits the normal destination command instead of FakeSignUp.
        val target = ImmutableFieldReference("Lfm8;", "a", "Lfm8;")
        for (clsName in listOf("Lxh4;", "Lte0;")) {
            val cls = mutableClassDefByOrNull(clsName) ?: continue
            for (m in cls.methods) {
                val impl = m.implementation ?: continue
                for (i in impl.instructions.indices) {
                    val ins = impl.instructions[i]
                    if (ins.opcode == Opcode.SGET_OBJECT &&
                        (ins as? ReferenceInstruction)?.reference?.toString() == "Lvd8;->a:Lvd8;"
                    ) {
                        val reg = (ins as? OneRegisterInstruction)?.registerA ?: continue
                        m.replaceInstruction(i, BuilderInstruction21c(Opcode.SGET_OBJECT, reg, target))
                    }
                }
            }
        }

        // subscription_active -> true (v2 holds the ctor's b parameter and is dead after its iput).
        val y5o = mutableClassDefByOrNull("Ly5o;")
        if (y5o != null) {
            for (m in y5o.methods) {
                if (m.name != "<init>") continue
                val impl = m.implementation ?: continue
                for (i in impl.instructions.indices) {
                    val ins = impl.instructions[i]
                    if (ins.opcode == Opcode.IPUT_BOOLEAN &&
                        (ins as? ReferenceInstruction)?.reference?.toString() == "Ly5o;->b:Z"
                    ) {
                        m.addInstructions(i, "const/4 v2, 0x1")
                        break
                    }
                }
            }
        }
    }
}
