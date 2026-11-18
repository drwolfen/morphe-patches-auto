/**
 * Copyright 2026 crackpipe
 * Duolingo 6.105.1 - Max tier.
 */

package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

val enablePremiumPatch = bytecodePatch(
    name = "Enable Max",
    description = "Unlocks Duolingo Max: Max badge, subscription gates and immersive (Max) subscription."
) {
    compatibleWith(Compat.DUOLINGO)

    execute {
        MaxUserInitFingerprint.method.apply {
            addInstructions(implementation!!.instructions.size - 1, """
                sget-object v1, Lcom/duolingo/data/user/SubscriberLevel;->GOLD:Lcom/duolingo/data/user/SubscriberLevel;
iput-object v1, p1, Lcom/duolingo/data/user/User;->D0:Lcom/duolingo/data/user/SubscriberLevel;
            """)
        }
        for (fld in mutableClassDefBy("Lcom/duolingo/data/user/User;").fields) {
            if (fld.name == "D0") fld.accessFlags = fld.accessFlags and 0x10.inv()
        }
        SubscriptionTenureInitFingerprint.method.addInstructions(0, """
            const/4 p1, 0x3
sget-object p3, Lcom/duolingo/data/user/SubscriptionItemType;->IMMERSIVE_GOLD:Lcom/duolingo/data/user/SubscriptionItemType;
        """)
        HasSubscriptionFingerprint.method.returnEarly(true)
    }
}
