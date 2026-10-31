/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.BytecodePatchContext
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.Label
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction10x
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21t
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

/**
 * Fix: guest watchlist add ("Update failed, try again later").
 *
 * The guest session has no portfolio at all: the create request answers 200 but the follow-up
 * `get_all_portfolios_new` returns `portfolios: []`, so the star add aborts with the toast. The
 * server *does* accept an arbitrary portfolio id (verified: id=1 answers 2xx), so the fix is
 * client-side — synthesize one watchlist portfolio whenever the parsed list is empty.
 *
 * Hooked at `PortfoliosResponse_ScreenDataJsonAdapter.fromJson`, the single choke point every
 * portfolios response goes through. `Portfolio` ctor is
 * `(J, String, String, String, I, Z, List)`: (id, name, type, "", 0, isNew, instruments).
 * `isNew=false` makes the star add straight to id 1 instead of opening the chooser picker.
 *
 * Branch targets are dexlib2 `Label`s bound to real instruction locations: smali `:label` text
 * inside an insertion binds to stale addresses (the `if-*` landed on unrelated code).
 */
fun BytecodePatchContext.fixGuestWatchlistAdd() {
    val adapter =
        "Lcom/fusionmedia/investing/api/service/watchlist/state/response/PortfoliosResponse_ScreenDataJsonAdapter;"
    val portfolio =
        "Lcom/fusionmedia/investing/api/service/watchlist/state/response/PortfoliosResponse\$Portfolio;"
    val m = mutableClassDefByOrNull(adapter)?.methods
        ?.firstOrNull { it.name == "fromJson" && it.parameterTypes.size == 1 }
        ?: return
    val impl = m.implementation ?: return

    // The method has no spare locals and its 2 param registers sit at v6/v7. Bump the register
    // count by 9 so the `Portfolio` ctor call (8 words) fits in v8..v16, then relocate the params
    // to v6/v7 so the untouched original body keeps working.
    var rc: java.lang.reflect.Field? = null
    var cls: Class<*>? = impl::class.java
    while (cls != null && rc == null) {
        try { rc = cls.getDeclaredField("registerCount") } catch (_: NoSuchFieldException) {}
        cls = cls.superclass
    }
    if (rc == null) {
        for (fld in impl::class.java.declaredFields) {
            if (fld.type == Int::class.javaPrimitiveType && fld.getInt(impl) == impl.registerCount) {
                rc = fld; break
            }
        }
    }
    val rcf = rc ?: return
    rcf.isAccessible = true
    rcf.setInt(impl, impl.registerCount + 9)
    m.addInstructions(0, """
        move-object/from16 v6, v15
        move-object/from16 v7, v16
    """.trimIndent())

    val castIdx = impl.instructions.indexOfFirst {
        it.opcode.name == "check-cast" &&
            (it as? ReferenceInstruction)?.reference?.toString() == "Ljava/util/List;"
    }
    if (castIdx < 0) return

    // `:chk` is anchored on a nop the mklist code is inserted in front of; `:cont` is the original
    // null-check instruction the whole block falls through into.
    val nop = BuilderInstruction10x(Opcode.NOP)
    impl.addInstruction(castIdx + 1, nop)
    val lblCont: com.android.tools.smali.dexlib2.builder.Label =
        impl.instructions[castIdx + 2].location.addNewLabel()
    val lblChk: com.android.tools.smali.dexlib2.builder.Label = nop.location.addNewLabel()

    impl.addInstruction(castIdx + 1, com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21t(Opcode.IF_NEZ, 0, lblChk))

    // v0 = parsed list (null only if the key was absent), v1 = the `portfolio` key string the
    // ScreenData ctor still needs, v2 = scratch, v8..v16 = the ctor call.
    val mkList = listOf(
        "new-instance v0, Ljava/util/ArrayList;",
        "invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V",
    )
    m.addInstructions(castIdx + 2, mkList.joinToString("\n"))

    val nopIdx = castIdx + 2 + mkList.size
    m.addInstructions(nopIdx + 1, """
        invoke-interface {v0}, Ljava/util/List;->size()I
        move-result v2
    """.trimIndent())
    impl.addInstruction(
        nopIdx + 3,
        com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21t(Opcode.IF_NEZ, 2, lblCont),
    )
    m.addInstructions(nopIdx + 4, """
        new-instance v8, $portfolio
        const-wide/16 v9, 0x1
        const-string v11, "My Watchlist"
        const-string v12, "watchlist"
        const-string v13, ""
        const/4 v14, 0x0
        const/4 v15, 0x0
        new-instance v16, Ljava/util/ArrayList;
        invoke-direct/range {v16 .. v16}, Ljava/util/ArrayList;-><init>()V
        invoke-direct/range {v8 .. v16}, $portfolio-><init>(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;IZLjava/util/List;)V
        invoke-interface {v0, v8}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    """.trimIndent())
}
