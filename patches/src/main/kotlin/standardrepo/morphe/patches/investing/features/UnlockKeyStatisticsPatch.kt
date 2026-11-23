/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat
import standardrepo.morphe.patches.investing.util.forceFalseField

/** Forces the server `isLocked` flag off on every Key Statistics metric (folded into Enable Pro). */
fun BytecodePatchContext.unlockKeyStatistics() {
    // `KeyStatisticsResponse$Metric` fields: a=action, b=define, c=isPro, d=isLocked,
    // e=id, f=value. `isPro` stays true so the "P" badge keeps marking the Pro rows.
    forceFalseField(
        "Lcom/fusionmedia/investing/feature/keystatistics/stats/data/response/KeyStatisticsResponse\$Metric;",
        "d",
    )
}

@Suppress("unused")
val unlockKeyStatisticsPatch = bytecodePatch(
    name = "Unlock: Key Statistics",
    description = "Forces the server `isLocked` flag off on every Key Statistics metric " +
        "(`KeyStatisticsResponse\$Metric.d`), so the orange `Unlock` pill next to the Pro rows " +
        "(Fair Value, analyst targets) is not drawn.",
) {
    compatibleWith(Compat.INVESTING)
    execute { unlockKeyStatistics() }
}
