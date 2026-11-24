/**
 * Copyright 2026 crackpipe
 * Hand-written for ginlemon.flowerfree 6.6 build 026 (versionCode 660260).
 */

package standardrepo.morphe.patches.smartlauncher.onboarding

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import standardrepo.morphe.compatibility.Compat

/**
 * Skip the first-run onboarding wizard.
 *
 * Two gates are neutralised, because OnboardingActivity alone is not enough:
 *
 * 1. The preference descriptor itself. `Lgo8;<clinit>` builds every settings key as a
 *    `Lbo8;` wrapper carrying its name and default value:
 *
 *      new-instance v0, Lbo8;
 *      const-string v1, "WizardComplete"
 *      <loads false>
 *      invoke-direct {v0, v1, v2}, Lbo8;-><init>(Ljava/lang/String;Z)V
 *      sput-object v0, Lgo8;->o1:Lbo8;
 *
 *    Every consumer - OnboardingActivity.onCreate, HomeScreen.onResume, SLApp init and the
 *    analytics helper - reads this descriptor through `La71;->b(Context)`, which falls back to
 *    its stored default when the key is unset. Re-writing o1 as a fresh descriptor whose default
 *    is TRUE makes all of them see "wizard complete" without touching SharedPreferences.
 *
 * 2. The launch branch in OnboardingActivity.onCreate (belt-and-braces):
 *
 *      invoke-virtual {v6}, Ljava/lang/Boolean;->booleanValue()Z
 *      move-result v6
 *      if-eqz v6, <skip>        ; not complete -> continue into wizard UI
 *      finish(); navigate HomeScreen()
 *
 *    Nop-ing that branch guarantees the activity falls through to finish() + HomeScreen even on
 *    installs where the descriptor read is cached elsewhere.
 */
val forceSkipOnboardingPatch = bytecodePatch(
    name = "Force Skip Onboarding",
    description = "Skips Smart Launcher's first-run onboarding wizard (WizardComplete gate).",
    default = true
) {
    compatibleWith(Compat.SMARTLAUNCHER)

    execute {
        fun growRegisters(impl: MutableMethodImplementation, extra: Int): Int {
            try {
                var f: java.lang.reflect.Field? = null
                var k: Class<*>? = impl::class.java
                while (k != null && f == null) {
                    try { f = k.getDeclaredField("registerCount") } catch (_: NoSuchFieldException) {}
                    k = k.superclass
                }
                if (f == null) {
                    for (fld in impl::class.java.declaredFields) {
                        if (fld.type == Int::class.javaPrimitiveType) { f = fld; break }
                    }
                }
                f!!.isAccessible = true
                val grown = f.getInt(impl) + extra
                f.setInt(impl, grown)
                return grown
            } catch (_: Throwable) {
                return impl.registerCount
            }
        }

        // --- Gate 1: WizardComplete preference descriptor default -> true -------------------
        val prefs = mutableClassDefBy("Lgo8;")
        val clinit = prefs.methods.first { it.name == "<clinit>" && it.parameters.isEmpty() }
        val cImpl = clinit.implementation!!
        val regs = growRegisters(cImpl, 4)
        var injectAt = -1
        val insns = cImpl.instructions.toList()
        for (i in insns.indices) {
            val inst = insns[i] as? ReferenceInstruction ?: continue
            if (inst.opcode != Opcode.CONST_STRING &&
                inst.opcode != Opcode.CONST_STRING_JUMBO) continue
            if (!inst.reference.toString().endsWith("WizardComplete")) continue
            // The matching sput-object follows within a handful of instructions.
            for (j in i + 1 until minOf(i + 6, insns.size)) {
                val after = insns[j] as? ReferenceInstruction ?: continue
                if (after.opcode == Opcode.SPUT_OBJECT &&
                    after.reference.toString() == "Lgo8;->o1:Lbo8;") {
                    injectAt = j
                    break
                }
            }
            if (injectAt >= 0) break
        }
        check(injectAt >= 0) { "WizardComplete descriptor assignment not found in Lgo8;<clinit>" }

        val objReg = regs - 1
        val strReg = regs - 2
        val boolReg = regs - 3
        clinit.addInstructions(
            injectAt + 1,
            """
                new-instance v$objReg, Lbo8;
                const-string v$strReg, "WizardComplete"
                const/4 v$boolReg, 0x1
                invoke-direct {v$objReg, v$strReg, v$boolReg}, Lbo8;-><init>(Ljava/lang/String;Z)V
                sput-object v$objReg, Lgo8;->o1:Lbo8;
            """.trimIndent()
        )

        // --- Gate 2: OnboardingActivity.onCreate launch branch -> fall through --------------
        val activity = mutableClassDefBy("Lginlemon/flower/onboarding/experimental/OnboardingActivity;")
        val onCreate = activity.methods.first { it.name == "onCreate" && it.parameters.size == 1 }
        val impl = onCreate.implementation!!
        var patched = 0
        val instructions = impl.instructions.toList()
        for (i in 1 until instructions.size) {
            // Pattern: move-result right after Boolean.booleanValue(), then the if-eqz gate.
            if (instructions[i - 1].opcode != Opcode.MOVE_RESULT) continue
            if (instructions[i].opcode != Opcode.IF_EQZ) continue
            val prevInvoke = i >= 2 &&
                (instructions[i - 2] as? ReferenceInstruction)?.let {
                    it.opcode == Opcode.INVOKE_VIRTUAL &&
                        it.reference.toString() == "Ljava/lang/Boolean;->booleanValue()Z"
                } ?: false
            if (!prevInvoke) continue
            onCreate.replaceInstruction(i, "nop")
            patched++
        }
        check(patched > 0) { "WizardComplete branch not found in OnboardingActivity.onCreate" }
    }
}
