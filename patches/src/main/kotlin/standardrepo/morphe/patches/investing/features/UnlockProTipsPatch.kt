/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat
import standardrepo.morphe.patches.investing.util.forceFalseField

@Suppress("unused")
val unlockProTipsPatch = bytecodePatch(
    name = "Unlock: Pro Tips",
    description = "Forces the server `locked` flag off on Pro Tips, so the tip text renders instead of the lock.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        // ProTip `b` is the locked flag in both the overview and the details feature.
        forceFalseField("Lcom/fusionmedia/investing/feature/protips/data/response/ProTipsResponse\$ProTip;", "b")
        forceFalseField("Lcom/fusionmedia/investing/feature/protips/details/data/response/ProTipsResponse\$ProTip;", "b")
        forceFalseField("Lcom/fusionmedia/investing/feature/instrument/overview/pro/tips/data/response/ProTipsResponse\$ProTip;", "b")
    }
}
