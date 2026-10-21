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
                const-string v5, "crackpipe"
                invoke-virtual {v8}, Landroid/app/Activity;->getClass()Ljava/lang/Class;
                move-result-object v3
                invoke-virtual {v3}, Ljava/lang/Class;->getName()Ljava/lang/String;
                move-result-object v3
                invoke-static {v3}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v3
                invoke-static {v5, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getAction()Ljava/lang/String;
                move-result-object v3
                invoke-static {v3}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v3
                invoke-static {v5, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v3
                invoke-static {v3}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v3
                invoke-static {v5, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                if-eqz v3, :pkg
                const-string v4, "play.google.com"
                invoke-virtual {v3, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v4
                if-nez v4, :m1
                goto :skip
                :m1
                const-string v4, "market://"
                invoke-virtual {v3, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v4
                if-nez v4, :m2
                goto :skip
                :m2
                const-string v4, "com.android.vending"
                invoke-virtual {v3, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v4
                if-eqz v4, :pkg
                goto :skip
                :pkg
                invoke-virtual {v9}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v3
                if-eqz v3, :comp
                const-string v4, "com.android.vending"
                invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v4
                if-eqz v4, :comp
                goto :skip
                :comp
                invoke-virtual {v9}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
                move-result-object v3
                if-eqz v3, :orig
                invoke-virtual {v3}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;
                move-result-object v3
                const-string v4, "com.android.vending"
                invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v4
                if-eqz v4, :orig
                goto :skip
                :orig
                const-string v6, "PASSED"
                invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-super {v8, v9, v10}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I)V
                return-void
                :skip
                const-string v6, "SWALLOWED"
                invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
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
                const-string v6, "crackpipe"
                invoke-virtual {v8}, Landroid/app/Activity;->getClass()Ljava/lang/Class;
                move-result-object v4
                invoke-virtual {v4}, Ljava/lang/Class;->getName()Ljava/lang/String;
                move-result-object v4
                invoke-static {v4}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v4
                invoke-static {v6, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getAction()Ljava/lang/String;
                move-result-object v4
                invoke-static {v4}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v4
                invoke-static {v6, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v4
                invoke-static {v4}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v4
                invoke-static {v6, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                if-eqz v4, :pkg
                const-string v5, "play.google.com"
                invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v5
                if-nez v5, :m1
                goto :skip
                :m1
                const-string v5, "market://"
                invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v5
                if-nez v5, :m2
                goto :skip
                :m2
                const-string v5, "com.android.vending"
                invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v5
                if-eqz v5, :pkg
                goto :skip
                :pkg
                invoke-virtual {v9}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v4
                if-eqz v4, :comp
                const-string v5, "com.android.vending"
                invoke-virtual {v4, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v5
                if-eqz v5, :comp
                goto :skip
                :comp
                invoke-virtual {v9}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
                move-result-object v4
                if-eqz v4, :orig
                invoke-virtual {v4}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;
                move-result-object v4
                const-string v5, "com.android.vending"
                invoke-virtual {v4, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v5
                if-eqz v5, :orig
                goto :skip
                :orig
                const-string v7, "PASSED"
                invoke-static {v6, v7}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-super {v8, v9, v10, v11}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I Landroid/os/Bundle;)V
                return-void
                :skip
                const-string v7, "SWALLOWED"
                invoke-static {v6, v7}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
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
