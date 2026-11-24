/**
 * Copyright 2026 crackpipe
 * Hand-written for ginlemon.flowerfree 6.6 build 026 (versionCode 660260).
 */

package standardrepo.morphe.patches.smartlauncher.ads

import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import standardrepo.morphe.compatibility.Compat

/**
 * Remove Smart Launcher's promotional surfaces (its only "ads").
 *
 * SL ships no third-party ad SDK: every monetisation surface is an upsell driven by the remote-config
 * snapshot that `am9.i(boolean)` builds into a `Lzl9;` object and publishes through
 * `c24.a(MutableStateFlow)`. Each flag is read straight out of the config store (`Lde4;`) before the
 * constructor call:
 *
 *   const-string v2, "enable_pro_banner"
 *   invoke-virtual {v0, v2}, Lde4;->b:(Ljava/lang/String;)Z      ; remote-config boolean
 *   move-result v29                                              ; -> Lzl9 field (Get Pro banner)
 *
 *   ... same for "scheduledPromotion"  -> periodic promo cards
 *            and "seasonalPromotion"    -> seasonal/Christmas promo card + notification worker
 *            and "sponsoredSearchUrl"   -> sponsored row in Smart Search results
 *
 * The patch overwrites the four `move-result` registers with false / "about:blank" right before they are
 * handed to the config constructor, so no consumer ever sees a truthy promo flag. This is independent of
 * the premium state, which means it also covers installs where Enable Pro cannot apply.
 *
 * Notes:
 *  - Replace the whole instruction rather than inserting one: every later index stays stable.
 *  - Use `const`, never `const/4`: move-result can target any register (this method uses v29), while
 *    const/4 only encodes registers < 16 and dexlib2's writer throws an empty-collection error otherwise.
 */
val removeAdsAndPromotionsPatch = bytecodePatch(
    name = "Remove Ads and Promotions",
    description = "Removes Smart Launcher's upsell surfaces: Get Pro banner, periodic/seasonal promos and sponsored search row.",
    default = true,
) {
    compatibleWith(Compat.SMARTLAUNCHER)

    execute {
        val configSync = mutableClassDefBy("Lam9;").methods.first { it.name == "i" && it.parameters.size == 1 }
        val impl = configSync.implementation!!
        val boolKeys = setOf("enable_pro_banner", "scheduledPromotion", "seasonalPromotion")
        val stringKey = "sponsoredSearchUrl"

        fun registerAfterMoveResult(startAt: Int, wantString: Boolean): Pair<Int, Int>? {
            val insns = impl.instructions.toList()
            var invokeAt = -1
            for (j in startAt until minOf(startAt + 4, insns.size)) {
                val ref = insns[j] as? ReferenceInstruction ?: continue
                if (ref.opcode != Opcode.INVOKE_VIRTUAL) continue
                val mr = ref.reference as? MethodReference ?: continue
                val wantedName = if (wantString) "e" else "b"
                val wantedRet = if (wantString) "Ljava/lang/String;" else "Z"
                if (mr.definingClass == "Lde4;" && mr.name == wantedName &&
                    mr.parameterTypes.size == 1 && mr.returnType == wantedRet
                ) {
                    invokeAt = j; break
                }
            }
            if (invokeAt < 0) return null
            for (j in invokeAt until minOf(invokeAt + 3, insns.size)) {
                val op = insns[j].opcode
                if ((!wantString && op == Opcode.MOVE_RESULT) || (wantString && op == Opcode.MOVE_RESULT_OBJECT)) {
                    val reg = (insns[j] as OneRegisterInstruction).registerA
                    return j to reg
                }
            }
            return null
        }

        // Overwrite the `move-result` that carries each promo flag instead of inserting instructions:
        // a replacement keeps every later instruction index stable, and the consumer only ever sees
        // false / blank URL.
        val foundBoolKeys = mutableSetOf<String>()
        var foundStringKey = false
        val seenStrings = mutableListOf<String>()
        run {
            val insns = impl.instructions.toList()
            for (i in insns.indices) {
                val ref = insns[i] as? ReferenceInstruction ?: continue
                if (ref.opcode != Opcode.CONST_STRING && ref.opcode != Opcode.CONST_STRING_JUMBO) continue
                val raw = ref.reference.toString()
                seenStrings += raw
                val key = raw.removeSurrounding("\"")
                when {
                    key in boolKeys -> registerAfterMoveResult(i + 1, wantString = false)?.let { (pos, reg) ->
                        configSync.replaceInstruction(pos, "const v$reg, 0x0")
                        foundBoolKeys += key
                    }
                    key == stringKey -> registerAfterMoveResult(i + 1, wantString = true)?.let { (pos, reg) ->
                        configSync.replaceInstruction(pos, "const-string v$reg, \"about:blank\"")
                        foundStringKey = true
                    }
                }
            }
        }

        check(foundBoolKeys.containsAll(boolKeys)) {
            "Missing promo boolean keys: ${boolKeys - foundBoolKeys}; insns=${impl.instructions.count()}"
        }
        check(foundStringKey) {
            "sponsoredSearchUrl read not found; strings=${seenStrings.take(30)}"
        }
    }
}
