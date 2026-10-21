/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.bowerydigital.bend v7.8.0 — do not edit by hand.
 */

package standardrepo.morphe.patches.bend.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Auto-generated: unlocks Bend Premium — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.BEND)

    execute {
        ProcessResponseFingerprint.method.addInstruction(0, "const/4 p1, 0x0")
        ValidateResponseFingerprint.method.returnEarly()
        VerifySignatureMatchesFingerprint.method.returnEarly(true)
        VerifyIntegrityFingerprint.method.returnEarly()
        GetStoreIdFromSystemFingerprint.method.returnEarly(null as Void?)
        StartActivityForResultFingerprint.method.apply {
            val impl = implementation!!
            val rc = impl.javaClass.getDeclaredField("registerCount")
            rc.isAccessible = true
            rc.setInt(impl, impl.registerCount + 8)
            removeInstructions(0, impl.instructions.size)
            addInstructions(0, """
                invoke-virtual {v1}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v3
                if-eqz v3, :orig
                const-string v4, "play.google.com"
                invoke-virtual {v3, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v4
                if-eqz v4, :skip
                const-string v4, "market://"
                invoke-virtual {v3, v4}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v4
                if-eqz v4, :skip
                :orig
                invoke-super {v0, v1, v2}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I)V
                return-void
                :skip
                return-void
            """)
        }
        StartActivityForResultFingerprintLge1.method.apply {
            val impl = implementation!!
            val rc = impl.javaClass.getDeclaredField("registerCount")
            rc.isAccessible = true
            rc.setInt(impl, impl.registerCount + 8)
            removeInstructions(0, impl.instructions.size)
            addInstructions(0, """
                invoke-virtual {v1}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v4
                if-eqz v4, :orig
                const-string v5, "play.google.com"
                invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v5
                if-eqz v5, :skip
                const-string v5, "market://"
                invoke-virtual {v4, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v5
                if-eqz v5, :skip
                :orig
                invoke-super {v0, v1, v2, v3}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I Landroid/os/Bundle;)V
                return-void
                :skip
                return-void
            """)
        }
        CFingerprint.method.returnEarly(true)
        AFingerprint.method.returnEarly(true)
    }
}
