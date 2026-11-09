/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.Label
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction30t
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

/**
 * Fix: guest startup lands on the fake-signup / subscription-active screen.
 *
 * The guest session has no subscription flag, so the startup nav graph emits
 * `FakeSignUpNavigationCommand` (`Lvd8`) and the handler pushes the full-screen subscribe page on
 * top of the tabs (system Back reveals the tabs underneath - proof the tabs are the real start
 * destination).
 *
 * Hooked at the single `Lte0;emit` branch that compares the command against `Lvd8.a`
 * (`Intrinsics.c` -> `if-eqz`) and then navigates via `Lgxg;.e`. A `goto/32` to the first
 * `Lkotlin/Unit` epilogue after that `if-eqz` is inserted right after the `if-eqz` itself, so the
 * branch body is skipped: the command is consumed (no unknown-command logging) but nothing is
 * pushed.
 *
 * Trade-off: in-app "Create account" entries emit the same command, so they no longer open the
 * page either (guest-only build; deliberate).
 */
fun BytecodePatchContext.fixSkipSubscriptionScreen() {
    val cls = mutableClassDefByOrNull("Lte0;") ?: return
    val m = cls.methods.firstOrNull { it.name == "emit" } ?: return
    val impl = m.implementation ?: return
    val instrs = impl.instructions

    val sIdx = instrs.indexOfFirst {
        it.opcode.name == "sget-object" &&
            (it as? ReferenceInstruction)?.reference?.toString() == "Lvd8;.a:Lvd8;"
    }
    if (sIdx < 0) return

    val ifIdx = (sIdx until minOf(sIdx + 12, instrs.size)).firstOrNull {
        instrs[it].opcode.name == "if-eqz"
    } ?: return

    val unitIdx = (ifIdx + 1 until instrs.size).firstOrNull {
        instrs[it].opcode.name == "sget-object" &&
            (instrs[it] as? ReferenceInstruction)?.reference?.toString() ==
            "Lkotlin/Unit;.a:Lkotlin/Unit;"
    } ?: return

    val lbl: Label = instrs[unitIdx].location.addNewLabel()
    impl.addInstruction(ifIdx + 1, BuilderInstruction30t(Opcode.GOTO_32, lbl))
}
