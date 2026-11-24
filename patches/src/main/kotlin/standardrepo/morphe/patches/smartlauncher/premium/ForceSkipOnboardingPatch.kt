/**
 * Copyright 2026 crackpipe
 * Hand-written for ginlemon.flowerfree 6.6 build 026 (versionCode 660260).
 */

package standardrepo.morphe.patches.smartlauncher.onboarding

import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import standardrepo.morphe.compatibility.Compat

/**
 * Skip the first-run onboarding wizard.
 *
 * OnboardingActivity.onCreate reads the boolean preference "WizardComplete"
 * (Lgo8;->o1, a Lbo8; wrapper) and branches on it:
 *
 *   invoke-virtual {v6}, Ljava/lang/Boolean;->booleanValue()Z
 *   move-result v6
 *   if-eqz v6, <skip>          ; not complete -> continue into the wizard UI
 *   finish(); navigate HomeScreen()
 *
 * When the flag is false the activity keeps presenting the rotating onboarding pages, which on
 * some installs (missing GMS / sideloaded signature) never advance because the analytics handshake
 * ("onboarding" / "Onboading Experimental Start") stalls. Neutralising that branch to a nop makes
 * onCreate always fall through to finish() + HomeScreen navigation, so a freshly installed patched
 * build lands directly on the home screen.
 */
val forceSkipOnboardingPatch = bytecodePatch(
    name = "Force Skip Onboarding",
    description = "Skips Smart Launcher's first-run onboarding wizard (WizardComplete gate).",
    default = true
) {
    compatibleWith(Compat.SMARTLAUNCHER)

    execute {
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
