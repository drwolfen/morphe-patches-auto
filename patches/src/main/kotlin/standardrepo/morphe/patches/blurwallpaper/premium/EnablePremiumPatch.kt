/**
 * Copyright 2026 crackpipe
 * Auto-generated for apps.automan.blurwallpaper v3.0.0 — do not edit by hand.
 */

package standardrepo.morphe.patches.blurwallpaper.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Pro",
    description = "Auto-generated: unlocks Blurwallpaper Pro — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.BLURWALLPAPER)

    execute {
        ProcessResponseFingerprint.method.addInstruction(0, "const/4 p1, 0x0")
        ValidateResponseFingerprint.method.returnEarly()
        PerformLocalInstallerCheckFingerprint.method.returnEarly(true)
        SharedPrefsGetterFingerprint.method.addInstructions(0, """
                const-string v0, "start_blur"
                invoke-virtual {p0, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v0
                if-eqz v0, :skip_start_blur
                sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
                return-object v0
                :skip_start_blur
                nop
            """)
        OnReceivedFingerprint.method.returnEarly()
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
                invoke-virtual {v9}, Landroid/app/Activity;->getClass()Ljava/lang/Class;
                move-result-object v0
                invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v10}, Landroid/content/Intent;->getAction()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v10}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
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
                invoke-virtual {v10}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v0
                if-eqz v0, :comp
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :comp
                goto :skip
                :comp
                invoke-virtual {v10}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
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
                invoke-super {v9, v10, v11}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I)V
                return-void
                :skip
                const-string v3, "SWALLOWED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                return-void
            """)
        }
        StartActivityForResultFingerprint_sBundleV.method.apply {
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
                invoke-virtual {v9}, Landroid/app/Activity;->getClass()Ljava/lang/Class;
                move-result-object v0
                invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v10}, Landroid/content/Intent;->getAction()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v10}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
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
                invoke-virtual {v10}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v0
                if-eqz v0, :comp
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :comp
                goto :skip
                :comp
                invoke-virtual {v10}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
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
                invoke-super {v9, v10, v11, v12}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I Landroid/os/Bundle;)V
                return-void
                :skip
                const-string v3, "SWALLOWED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                return-void
            """)
        }
        EntitlementActiveFingerprint.method.apply {
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
            rcf.isAccessible = true
            rcf.setInt(impl, impl.registerCount + 18)
            removeInstructions(0, implementation!!.instructions.size)
            addInstructions(0, """
                new-instance v0, Ljava/util/HashMap;
                invoke-direct {v0}, Ljava/util/HashMap;-><init>()V
                new-instance v1, Lcom/revenuecat/purchases/EntitlementInfo;
                const-string v2, "pro"
                const/4 v3, 0x1
                const/4 v4, 0x1
                sget-object v5, Lcom/revenuecat/purchases/PeriodType;->NORMAL:Lcom/revenuecat/purchases/PeriodType;
                const/4 v6, 0x0
                const/4 v7, 0x0
                const/4 v8, 0x0
                sget-object v9, Lcom/revenuecat/purchases/Store;->PLAY_STORE:Lcom/revenuecat/purchases/Store;
                const-string v10, "pro"
                const/4 v11, 0x0
                const/4 v12, 0x0
                const/4 v13, 0x0
                const/4 v14, 0x0
                sget-object v15, Lcom/revenuecat/purchases/OwnershipType;->PURCHASED:Lcom/revenuecat/purchases/OwnershipType;
                const/4 v16, 0x0
                invoke-direct {v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16}, Lcom/revenuecat/purchases/EntitlementInfo;-><init>(Ljava/lang/String;ZZLcom/revenuecat/purchases/PeriodType;Ljava/util/Date;Ljava/util/Date;Ljava/util/Date;Lcom/revenuecat/purchases/Store;Ljava/lang/String;Ljava/lang/String;ZLjava/util/Date;Ljava/util/Date;Lcom/revenuecat/purchases/OwnershipType;Lorg/json/JSONObject;)V
                const-string v17, "pro"
                invoke-virtual {v0, v17, v1}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
                return-object v0
            """)
        }
        EntitlementGetFingerprint.method.apply {
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
            rcf.isAccessible = true
            rcf.setInt(impl, impl.registerCount + 16)
            removeInstructions(0, implementation!!.instructions.size)
            addInstructions(0, """
                new-instance v0, Lcom/revenuecat/purchases/EntitlementInfo;
                const-string v1, "pro"
                const/4 v2, 0x1
                const/4 v3, 0x1
                sget-object v4, Lcom/revenuecat/purchases/PeriodType;->NORMAL:Lcom/revenuecat/purchases/PeriodType;
                const/4 v5, 0x0
                const/4 v6, 0x0
                const/4 v7, 0x0
                sget-object v8, Lcom/revenuecat/purchases/Store;->PLAY_STORE:Lcom/revenuecat/purchases/Store;
                const-string v9, "pro"
                const/4 v10, 0x0
                const/4 v11, 0x0
                const/4 v12, 0x0
                const/4 v13, 0x0
                sget-object v14, Lcom/revenuecat/purchases/OwnershipType;->PURCHASED:Lcom/revenuecat/purchases/OwnershipType;
                const/4 v15, 0x0
                invoke-direct {v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15}, Lcom/revenuecat/purchases/EntitlementInfo;-><init>(Ljava/lang/String;ZZLcom/revenuecat/purchases/PeriodType;Ljava/util/Date;Ljava/util/Date;Ljava/util/Date;Lcom/revenuecat/purchases/Store;Ljava/lang/String;Ljava/lang/String;ZLjava/util/Date;Ljava/util/Date;Lcom/revenuecat/purchases/OwnershipType;Lorg/json/JSONObject;)V
                return-object v0
            """)
        }
        EntitlementInfoActiveFingerprint.method.apply {
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
            rcf.isAccessible = true
            rcf.setInt(impl, impl.registerCount + 1)
            removeInstructions(0, implementation!!.instructions.size)
            addInstructions(0, """
                const/4 v0, 0x1
                return v0
            """)
        }
    }
}
