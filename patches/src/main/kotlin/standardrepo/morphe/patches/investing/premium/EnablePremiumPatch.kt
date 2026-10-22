/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.fusionmedia.investing v6.52 — do not edit by hand.
 */

package standardrepo.morphe.patches.investing.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Pro",
    description = "Auto-generated: unlocks Investing Pro — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.INVESTING)
    execute {
        VerifySignatureMatchesFingerprint.method.returnEarly(true)
        VerifyIntegrityFingerprint.method.returnEarly()
        GetCurrencyIso4217CodeFingerprint.method.returnEarly(true)
        GetRevenueFingerprint.method.returnEarly(null as Void?)
        AFingerprint.method.returnEarly()
        AFingerprintLe51.method.returnEarly()
        StartActivityForResultFingerprint.method.apply {
            val impl = implementation!!
            var rc: java.lang.reflect.Field? = null
            var cls: Class<*>? = impl.javaClass
            while (cls != null && rc == null) {
                try { rc = cls.getDeclaredField("registerCount") } catch (_: NoSuchFieldException) {}
                cls = cls.superclass
            }
            if (rc == null) {
                for (fld in impl.javaClass.declaredFields) {
                    if (fld.type == Int::class.javaPrimitiveType && fld.getInt(impl) == impl.registerCount) {
                        rc = fld; break
                    }
                }
            }
            val rcf = rc!!
            val oldRegs = impl.registerCount
            rcf.isAccessible = true
            rcf.setInt(impl, oldRegs + 8)
            removeInstructions(0, impl.instructions.size)
            addInstructions(0, """
                const-string v2, "crackpipe"
                invoke-virtual {v8}, Landroid/app/Activity;->getClass()Ljava/lang/Class;
                move-result-object v0
                invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getAction()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                if-eqz v0, :pkg
                const-string v1, "play.google.com"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-nez v1, :m1
                goto :skip
                :m1
                const-string v1, "market://"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-nez v1, :m2
                goto :skip
                :m2
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-eqz v1, :pkg
                goto :skip
                :pkg
                invoke-virtual {v9}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v0
                if-eqz v0, :comp
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :comp
                goto :skip
                :comp
                invoke-virtual {v9}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
                move-result-object v0
                if-eqz v0, :orig
                invoke-virtual {v0}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;
                move-result-object v0
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :orig
                goto :skip
                :orig
                const-string v3, "PASSED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-super {v8, v9, v10}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I)V
                return-void
                :skip
                const-string v3, "SWALLOWED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                return-void
            """)
        }
        StartActivityForResultFingerprintLgr3.method.apply {
            val impl = implementation!!
            var rc: java.lang.reflect.Field? = null
            var cls: Class<*>? = impl.javaClass
            while (cls != null && rc == null) {
                try { rc = cls.getDeclaredField("registerCount") } catch (_: NoSuchFieldException) {}
                cls = cls.superclass
            }
            if (rc == null) {
                for (fld in impl.javaClass.declaredFields) {
                    if (fld.type == Int::class.javaPrimitiveType && fld.getInt(impl) == impl.registerCount) {
                        rc = fld; break
                    }
                }
            }
            val rcf = rc!!
            val oldRegs = impl.registerCount
            rcf.isAccessible = true
            rcf.setInt(impl, oldRegs + 8)
            removeInstructions(0, impl.instructions.size)
            addInstructions(0, """
                const-string v2, "crackpipe"
                invoke-virtual {v8}, Landroid/app/Activity;->getClass()Ljava/lang/Class;
                move-result-object v0
                invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getAction()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                if-eqz v0, :pkg
                const-string v1, "play.google.com"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-nez v1, :m1
                goto :skip
                :m1
                const-string v1, "market://"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-nez v1, :m2
                goto :skip
                :m2
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-eqz v1, :pkg
                goto :skip
                :pkg
                invoke-virtual {v9}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v0
                if-eqz v0, :comp
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :comp
                goto :skip
                :comp
                invoke-virtual {v9}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
                move-result-object v0
                if-eqz v0, :orig
                invoke-virtual {v0}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;
                move-result-object v0
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :orig
                goto :skip
                :orig
                const-string v3, "PASSED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-super {v8, v9, v10, v11}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I Landroid/os/Bundle;)V
                return-void
                :skip
                const-string v3, "SWALLOWED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                return-void
            """)
        }
        InvokeSuspendFingerprint.method.addInstructions(1021, """
            sget-object v19, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
            return-object v19
            """)
        InvokeSuspendFingerprint.method.addInstructions(246, """
            sget-object v19, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
            return-object v19
            """)
    }
}
