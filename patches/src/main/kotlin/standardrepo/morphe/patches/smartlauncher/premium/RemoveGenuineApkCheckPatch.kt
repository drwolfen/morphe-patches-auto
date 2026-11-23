/**
 * Copyright 2026 crackpipe
 *
 * Smart Launcher 6.6 build 026 (versionCode 660260) self-integrity gate.
 *
 * SLApp's init coroutine hashes its own signing certificate with SHA-1 hex and compares it to the
 * two store fingerprints of the official build:
 *   0C6F28FE9BDE3EA3A44DD1D4AD444D496BA487A9  (Google Play)
 *   DFAAB993DE750D7B5430BBD4217472311D94F9D0  (other store / vendor channel)
 *
 * On mismatch it logs `E SLTool: Not genuine apk. This may not stop humans but may stop machines.`
 * and calls System.exit(0) before the first activity starts, so every re-signed build (all patched
 * builds are re-signed with a local key) dies at launch without a crash trace - logcat only shows:
 *   I emon.flowerfree: System.exit called, status: 0
 *   Process ginlemon.flowerfree ... has died: fg TOP
 *
 * The gate is anchored on its own log message rather than an obfuscated class name (`Lix;` in this
 * build), then the exit call that follows it is neutralised so patched builds keep running.
 */

package standardrepo.morphe.patches.smartlauncher.premium

import app.morphe.patcher.StringComparisonType
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import standardrepo.morphe.compatibility.Compat

@Suppress("unused")
val removeGenuineApkCheckPatch = bytecodePatch(
    name = "Remove Genuine APK Check",
    description = "Smart Launcher refuses to run a build whose signing certificate is not the " +
        "official store key and exits silently at launch (`SLTool: Not genuine apk`). Patched " +
        "builds are always re-signed, so this disables that self-integrity gate.",
) {
    compatibleWith(Compat.SMARTLAUNCHER)

    execute {
        val marker = "Not genuine apk"
        var patchedMethods = 0

        classDefByStrings(marker, StringComparisonType.CONTAINS).forEach { classDef ->
            val mutableClass = mutableClassDefBy(classDef)
            mutableClass.methods.forEach { m ->
                val impl = m.implementation ?: return@forEach
                val ins = impl.instructions

                // Locate the gate's own log message, then walk forward to the exit call that it guards.
                val msgIdx = ins.indexOfFirst {
                    it.opcode.name.startsWith("const-string") &&
                        ((it as? ReferenceInstruction)?.reference as? StringReference)
                            ?.string?.contains(marker) == true
                }
                if (msgIdx < 0) return@forEach

                val exitIdx = (msgIdx until minOf(msgIdx + 12, ins.size)).firstOrNull { i ->
                    val instr = ins[i]
                    val ref = (instr as? ReferenceInstruction)?.reference
                    instr.opcode.name.startsWith("invoke-static") &&
                        ref is MethodReference &&
                        ref.definingClass == "Ljava/lang/System;" &&
                        ref.name == "exit"
                }
                if (exitIdx == null) return@forEach

                // System.exit(int) returns void and consumes its int argument; `nop` keeps the
                // register state intact, so execution simply falls through to the init sequence.
                m.replaceInstruction(exitIdx, "nop")
                patchedMethods++
            }
        }

        check(patchedMethods > 0) { "Remove Genuine APK Check: genuine-APK gate not found" }
    }
}
