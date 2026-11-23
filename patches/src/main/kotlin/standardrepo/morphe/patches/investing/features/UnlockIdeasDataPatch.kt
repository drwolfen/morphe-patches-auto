/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat
import standardrepo.morphe.patches.investing.util.forceFalseField

/** Forces the server `locked` flag off on Idea rows and their ratings (folded into Enable Pro). */
fun BytecodePatchContext.unlockIdeasData() {
    // `b` is the per-row locked flag on an Idea.
    forceFalseField("Lcom/fusionmedia/investing/features/instrumentinsights/data/response/InstrumentInsightsResponse\$Insight;", "b")
    // Rating columns on the idea detail.
    val ideas = "Lcom/fusionmedia/investing/feature/most/under/over/valued/details/data/response/"
    listOf("GrowthRatingResponse", "ProfitRatingResponse", "CashFlowRatingResponse")
        .forEach { forceFalseField(ideas + "$it;", "a") }
}

@Suppress("unused")
val unlockIdeasDataPatch = bytecodePatch(
    name = "Unlock: Ideas data",
    description = "Forces the server `locked` flag off on Idea rows and their Growth/Profit/Cash-Flow " +
        "ratings, so the Most Undervalued list and idea details show data instead of locks.",
) {
    compatibleWith(Compat.INVESTING)
    execute { unlockIdeasData() }
}
