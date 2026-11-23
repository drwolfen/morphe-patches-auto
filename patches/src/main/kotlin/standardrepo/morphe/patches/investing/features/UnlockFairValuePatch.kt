/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat
import standardrepo.morphe.patches.investing.util.forceFalseField

/** Forces the server `locked` flag off on every Fair Value datum so values render (folded into Enable Pro). */
fun BytecodePatchContext.unlockFairValue() {
    // Overview cell + Popular/Ideas column models.
    listOf("FairValue", "FairValueLabel", "FairValueUpside", "AnalystPriceTarget", "AnalystPriceTargetUpside")
        .forEach { forceFalseField("Lcom/fusionmedia/investing/feature/popular/screen/details/data/response/$it;", "a") }
    // Fair Value page.
    forceFalseField("Lcom/fusionmedia/investing/feature/fairvalue/data/response/FairValueResponse;", "k")
    forceFalseField("Lcom/fusionmedia/investing/feature/fair/value/details/data/response/FairValueResponse;", "i")
    // Ideas detail models.
    val ideas = "Lcom/fusionmedia/investing/feature/most/under/over/valued/details/data/response/"
    forceFalseField(ideas + "FairValueResponse;", "b")
    forceFalseField(ideas + "FairValueUpsideResponse;", "b")
    forceFalseField(ideas + "AnalystConsensusResponse;", "a")
    forceFalseField(ideas + "AnalystPriceTargetResponse;", "b")
    forceFalseField(ideas + "AnalystPriceTargetCurrencyCodeResponse;", "b")
}

@Suppress("unused")
val unlockFairValuePatch = bytecodePatch(
    name = "Unlock: Fair Value",
    description = "Forces the server `locked` flag off on every Fair Value datum (overview cell, " +
        "Fair Value page, Popular/Ideas columns, analyst target and consensus) so the values render.",
) {
    compatibleWith(Compat.INVESTING)
    execute { unlockFairValue() }
}
