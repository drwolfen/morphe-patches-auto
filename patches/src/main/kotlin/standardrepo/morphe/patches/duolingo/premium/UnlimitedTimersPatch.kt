package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat

val unlimitedTimersPatch = bytecodePatch(
    name = "Unlimited Timers",
    description = "Sets timer boost durations and free-boost flags to Integer.MAX_VALUE / true.",
    default = true
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        TimerBoostsInitFingerprint.method.addInstructions(0, """
            const p1, 0x7fffffff
            const p2, 0x7fffffff
            const/4 p3, 0x1
        """)
        TimerBoostsSyntheticInitFingerprint.method.addInstructions(0, """
            const/4 p1, 0x7
            const p2, 0x7fffffff
            const p3, 0x7fffffff
            const/4 p4, 0x1
        """)
    }
}
