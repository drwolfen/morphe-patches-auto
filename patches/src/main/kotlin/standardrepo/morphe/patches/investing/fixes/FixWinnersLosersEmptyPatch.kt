/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.Opcode

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
        val listT = com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference("Ljava/util/ArrayList;")
        val respName = name.removeSuffix("JsonAdapter;") + ";"
        val respT = com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference(respName)
        val listCtor = com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference("Ljava/util/ArrayList;", "<init>", emptyList(), "V")
        val respCtor = com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference(respName, "<init>", listOf("Ljava/util/List;"), "V")
        var k = 0
        impl.addInstruction(k++, com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction12x(Opcode.MOVE_OBJECT, 5, 14))
        impl.addInstruction(k++, com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction12x(Opcode.MOVE_OBJECT, 6, 15))
        impl.addInstruction(k++, com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c(Opcode.NEW_INSTANCE, 7, listT))
        impl.addInstruction(k++, com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c(Opcode.INVOKE_DIRECT, 1, 7, 0, 0, 0, 0, listCtor))
        impl.addInstruction(k++, com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c(Opcode.NEW_INSTANCE, 8, respT))
        impl.addInstruction(k++, com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c(Opcode.INVOKE_DIRECT, 2, 8, 7, 0, 0, 0, respCtor))
        val n = impl.instructions.size
        for (i in (n - 1) downTo 1) {
            val ins = impl.instructions[i]
            if (ins.opcode.name == "throw") {
                impl.replaceInstruction(i, com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11x(Opcode.RETURN_OBJECT, 8))
            }
        }
    }
}
