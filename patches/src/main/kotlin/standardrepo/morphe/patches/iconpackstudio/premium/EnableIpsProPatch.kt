/**
 * Copyright 2026 crackpipe
 * Hand-written for ginlemon.iconpackstudio 2.5 build 003 (versionCode 250030).
 */

package standardrepo.morphe.patches.iconpackstudio.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import standardrepo.morphe.compatibility.Compat

/**
 * Enable Icon Pack Studio Pro.
 *
 * IPS derives its entire license state from one Kotlin combine transform,
 * IPSPurchaseRepository$licenseStateFlow$1.invokeSuspend(...), which folds three boolean flows
 * (lifetime purchase, subscription, promo/debug unlock) into a LicenseStatus(hasProAccess, ...)
 * value object (Lly1;). Every consumer - the GET PRO button, paywall screens, cloud sync and the
 * pro-only icon editing effects - reads that single flow.
 *
 * Replacing the body with a freshly built Lly1(true, true) keeps the reactive flow intact while
 * pinning premium access on, so no Play Billing round trip is needed for Pro to appear active.
 */
val enableIpsProPatch = bytecodePatch(
    name = "Enable Pro",
    description = "Unlocks Icon Pack Studio Pro (all premium features).",
    default = true
) {
    compatibleWith(Compat.ICONPACKSTUDIO)

    execute {
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
            new-instance v0, Lly1;
            const/4 v1, 0x1
            const/4 v2, 0x1
            invoke-direct {v0, v1, v2}, Lly1;-><init>(ZZ)V
            return-object v0
        """.trimIndent()

        val holder = mutableClassDefBy(
            "Lginlemon/flower/ipsLicense/billing/IPSPurchaseRepository\$licenseStateFlow\$1;"
        )
        val combine = holder.methods.first {
            it.name == "invokeSuspend" && it.parameters.size == 1
        }
        val impl: MutableMethodImplementation = combine.implementation!!
        growRegisters(impl, 4)
        val orig = impl.instructions.count()
        combine.removeInstructions(0, orig)
        combine.addInstructions(0, block)

        check(combine.implementation!!.instructions.count() > 0) { "IPS license combine not patched" }
    }
}
