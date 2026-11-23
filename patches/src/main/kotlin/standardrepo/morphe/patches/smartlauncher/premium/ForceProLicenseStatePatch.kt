/**
 * Copyright 2026 crackpipe
 * Hand-written for ginlemon.flowerfree 6.6 build 026 (versionCode 660260).
 */

package standardrepo.morphe.patches.smartlauncher.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import standardrepo.morphe.compatibility.Compat

/**
 * Force Pro license state.
 *
 * The three predicates in Enable Pro cover every direct boolean gate, but two consumers read
 * the aggregated LicenseStatus object (Lzm9; - hasSL3ProLicense + hasFeaturePack) instead:
 *
 * - Lcn9;->b()Lzm9;  synchronous snapshot used by HomeScreen/analytics call sites;
 * - Lan9;->invokeSuspend(...)Object; the combine transform behind Lcn9;->f()Flow, which merges
 *   the four raw preference flows and re-emits false when Play billing recovery rewrites them.
 *
 * Replacing both bodies with a freshly built LicenseStatus(true, true) keeps the reactive flow
 * and the snapshot in permanent Pro state, so trial countdowns, restore-purchase prompts and
 * promo cards driven by that flow never come back after a cold start or billing refresh.
 */
private fun BytecodePatchContext.forceLicenseStatusTrue() {
    fun growRegisters(impl: Any, extra: Int) {
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
            f.setInt(impl, f.getInt(impl) + extra)
        } catch (_: Throwable) {}
    }

    val block = """
        new-instance v0, Lzm9;
        const/4 v1, 0x1
        const/4 v2, 0x1
        invoke-direct {v0, v1, v2}, Lzm9;-><init>(ZZ)V
        return-object v0
    """.trimIndent()

    // Lcn9;->b()Lzm9; - synchronous LicenseStatus snapshot.
    mutableClassDefByOrNull("Lcn9;")
        ?.methods?.firstOrNull { it.name == "b" && it.returnType == "Lzm9;" }
        ?.let { m ->
            val impl = m.implementation!!
            growRegisters(impl, 16)
            val orig = impl.instructions.size
            m.removeInstructions(0, orig)
            m.addInstructions(0, block)
        }

    // Lan9;->invokeSuspend(Ljava/lang/Object;)Ljava/lang/Object; - flow combine transform.
    mutableClassDefByOrNull("Lan9;")
        ?.methods?.firstOrNull { it.name == "invokeSuspend" && it.parameters.size == 1 }
        ?.let { m ->
            val impl = m.implementation!!
            growRegisters(impl, 16)
            val orig = impl.instructions.size
            m.removeInstructions(0, orig)
            m.addInstructions(0, block)
        }
}

val forceProLicenseStatePatch = bytecodePatch(
    name = "Force Pro License State",
    description = "Auto-generated: keeps the reactive/snapshot license status permanently Pro (no trial countdown, restore prompts or promo flow).",
    default = true
) {
    compatibleWith(Compat.SMARTLAUNCHER)

    execute { forceLicenseStatusTrue() }
}
