/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11x

/**
 * Fix: guest Ideas screen renders the Winners/Losers error card ("Loading Error") because the
 * section response has no usable `data`.
 *
 * The Moshi adapter treats `data` as required: key absent -> throw at `0043`, key present but
 * JSON null -> throw at `002c`. Both throws are replaced with a return of a freshly built
 * `Response(ArrayList())`, so the section parses as an empty (but successful) result instead of
 * falling into the error state.
 *
 * Method body keeps its original registers: registerCount is bumped by +9 so v7/v8 are spare,
 * the two param registers are relocated to v14/v15 (new top) and copied back into v5/v6 before
 * the untouched body runs.
 */
fun BytecodePatchContext.fixWinnersLosersEmpty() {
    val adapters = listOf(
        "Lcom/fusionmedia/investing/feature/search/explore/daily/winners/losers/data/response/DailyWinnersLosersResponseJsonAdapter;",
        "Lcom/fusionmedia/investing/feature/daily/winners/losers/data/response/DailyWinnersLosersResponseJsonAdapter;"
    )
    for (name in adapters) {
        val cls = mutableClassDefByOrNull(name) ?: continue
        val m = cls.methods.firstOrNull { it.name == "fromJson" && it.parameterTypes.size == 1 } ?: continue
        val impl = m.implementation ?: continue
        var rc: java.lang.reflect.Field? = null
        var c: Class<*>? = impl::class.java
        while (c != null && rc == null) {
            try { rc = c.getDeclaredField("registerCount") } catch (_: NoSuchFieldException) {}
            c = c.superclass
        }
        if (rc == null) continue
        rc.isAccessible = true
        rc.setInt(impl, impl.registerCount + 9)
        m.addInstructions(0, """
            move-object v5, v14
            move-object v6, v15
            new-instance v7, Ljava/util/ArrayList;
            invoke-direct {v7}, Ljava/util/ArrayList;-><init>()V
            new-instance v8, ${name}
            invoke-direct {v8, v7}, ${name}-><init>:(Ljava/util/List;)V
        """.trimIndent())
        val n = impl.instructions.size
        for (i in (n - 1) downTo 1) {
            val ins = impl.instructions[i]
            if (ins.opcode.name == "throw") {
                impl.replaceInstruction(i, BuilderInstruction11x(Opcode.RETURN_OBJECT, 8))
            }
        }
    }
}
