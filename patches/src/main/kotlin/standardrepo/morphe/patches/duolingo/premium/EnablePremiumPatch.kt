/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.duolingo v6.101.2 — do not edit by hand.
 */

package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Super",
    description = "Auto-generated: unlocks Duolingo Super — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        InitFingerprint.method.apply {
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
            rcf.setInt(impl, oldRegs + 4)
            addInstructions(implementation!!.instructions.size - 1, """
                const/4 v0, 0x1
                iput-boolean v0, p1, Lcom/duolingo/data/user/User;->Q0:Z
                iput-boolean v0, p1, Lcom/duolingo/data/user/User;->y:Z
                iput-boolean v0, p1, Lcom/duolingo/data/user/User;->P0:Z
                sget-object v0, Lcom/duolingo/data/user/SubscriberLevel;->PREMIUM:Lcom/duolingo/data/user/SubscriberLevel;
                iput-object v0, p1, Lcom/duolingo/data/user/User;->A0:Lcom/duolingo/data/user/SubscriberLevel;
            """)
        }
        for (fld in mutableClassDefBy("Lcom/duolingo/data/user/User;").fields) {
            if (fld.name in listOf("Q0", "y", "A0", "P0")) {
                fld.accessFlags = fld.accessFlags and 0x10.inv()
            }
        }
        InitFingerprintEnergyConfig.method.addInstruction(0, "move p1, p2")
        InitFingerprintEnergyConfig_IIJJJIJV.method.addInstruction(0, "move p1, p2")
        UcFingerprint.method.returnEarly(true)
        InvokeFingerprint.method.returnEarly(null as Void?)
        OnClickFingerprint.method.returnEarly()
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
                invoke-virtual {v1}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v3
                if-eqz v3, :pkg
                const-string v4, "play.google.com"
                invoke-virtual {v3, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v4
                if-eqz v4, :skip
                const-string v4, "market://"
                invoke-virtual {v3, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v4
                if-eqz v4, :skip
                const-string v4, "com.android.vending"
                invoke-virtual {v3, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v4
                if-eqz v4, :skip
                :pkg
                invoke-virtual {v1}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v3
                if-eqz v3, :comp
                const-string v4, "com.android.vending"
                invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v4
                if-eqz v4, :skip
                :comp
                invoke-virtual {v1}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
                move-result-object v3
                if-eqz v3, :orig
                invoke-virtual {v3}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;
                move-result-object v3
                const-string v4, "com.android.vending"
                invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v4
                if-eqz v4, :skip
                :orig
                invoke-super {v0, v1, v2}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I)V
                return-void
                :skip
                return-void
            """)
        }
        StartActivityForResultFingerprintComponentActivity.method.apply {
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
                invoke-virtual {v1}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v4
                if-eqz v4, :pkg
                const-string v5, "play.google.com"
                invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v5
                if-eqz v5, :skip
                const-string v5, "market://"
                invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v5
                if-eqz v5, :skip
                const-string v5, "com.android.vending"
                invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v5
                if-eqz v5, :skip
                :pkg
                invoke-virtual {v1}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v4
                if-eqz v4, :comp
                const-string v5, "com.android.vending"
                invoke-virtual {v4, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v5
                if-eqz v5, :skip
                :comp
                invoke-virtual {v1}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
                move-result-object v4
                if-eqz v4, :orig
                invoke-virtual {v4}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;
                move-result-object v4
                const-string v5, "com.android.vending"
                invoke-virtual {v4, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v5
                if-eqz v5, :skip
                :orig
                invoke-super {v0, v1, v2, v3}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I Landroid/os/Bundle;)V
                return-void
                :skip
                return-void
            """)
        }
        OnClickFingerprintLj13.method.addInstructions(422, """
            return-void
            """)
        OnClickFingerprintLj13.method.addInstructions(346, """
            return-void
            """)
        OnClickFingerprintLj13.method.addInstructions(334, """
            return-void
            """)
        OnClickFingerprintLj13.method.addInstructions(322, """
            return-void
            """)
        OnClickFingerprintLj13.method.addInstructions(310, """
            return-void
            """)
        OnClickFingerprintLj13.method.addInstructions(300, """
            return-void
            """)
        OnClickFingerprintLj13.method.addInstructions(244, """
            return-void
            """)
        OnClickFingerprintLj13.method.addInstructions(172, """
            return-void
            """)
        InvokeFingerprintLh5.method.addInstructions(278, """
            sget-object v13, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
            return-object v13
            """)
        InvokeFingerprintLh5.method.addInstructions(46, """
            sget-object v13, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
            return-object v13
            """)
        InvokeFingerprintLh5.method.addInstructions(28, """
            sget-object v13, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
            return-object v13
            """)
        InvokeFingerprintLh5.method.addInstructions(15, """
            sget-object v13, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
            return-object v13
            """)
        IsPromoFingerprint.method.returnEarly(true)
    }
}
