/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

/**
 * Fix: guest startup lands on the fake-signup / subscription-active screen.
 *
 * The startup nav builder `Lxh4` picks between two commands on the `subscription_active` flag
 * (`Ly5o.b`): guest -> `FakeSignUpNavigationCommand` (`Lvd8`), active -> `Lfm8` (the normal
 * startup destination). The built command list is consumed by the nav host directly, so skipping
 * the handler in `Lte0` is not enough - the command itself must change.
 *
 * Hooked at that single `sget-object Lvd8.a` in `Lxh4`: the field reference is rewritten to
 * `Lfm8.a`, so the guest startup emits the same command active subscribers get and the tabs are
 * the first screen.
 *
 * Trade-off: `Lvd8` is still emitted by in-app "Create account" entries; those keep working
 * through the untouched handler path.
 */
fun BytecodePatchContext.fixSkipSubscriptionScreen() {
    val tmpDir = System.getProperty("java.io.tmpdir") ?: "."
    val classes = listOf("Lxh4;", "Lte0;").mapNotNull { mutableClassDefByOrNull(it) }
    if (classes.isEmpty()) { java.io.File(tmpDir + "/site.log").appendText("SKIPERR cls null\n"); return }
    val target = ImmutableFieldReference("Lfm8;", "a", "Lfm8;")
    var nHit = 0
    for (cls in classes) {
        for (m in cls.methods) {
            val impl = m.implementation ?: continue
        val n = impl.instructions.size
        for (i in 0 until n) {
            val ins = impl.instructions[i]
            if (ins.opcode == Opcode.SGET_OBJECT &&
                (ins as? ReferenceInstruction)?.reference?.toString() == "Lvd8;->a:Lvd8;") {
                val reg = (ins as? OneRegisterInstruction)?.registerA ?: continue
                impl.replaceInstruction(i, BuilderInstruction21c(Opcode.SGET_OBJECT, reg, target)); nHit++
            }
        }
    }
    }
    val y5o = mutableClassDefByOrNull("Ly5o;")
    if (y5o != null) {
        for (m in y5o.methods) {
            if (m.name != "<init>") continue
            val impl = m.implementation ?: continue
            for (i in impl.instructions.indices) {
                val ins = impl.instructions[i]
                if (ins.opcode == Opcode.IPUT_BOOLEAN &&
                    (ins as? ReferenceInstruction)?.reference?.toString() == "Ly5o;->b:Z") {
                    impl.addInstruction(i, com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11n(Opcode.CONST_4, 2, 1))
                    nHit++
                    break
                }
            }
        }
    }
    java.io.File(tmpDir + "/site.log").appendText("SKIPOK " + nHit + "\n")
}
