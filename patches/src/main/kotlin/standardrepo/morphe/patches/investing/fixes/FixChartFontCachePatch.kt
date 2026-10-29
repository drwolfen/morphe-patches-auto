/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat
import standardrepo.morphe.patches.investing.util.bakeHolderValue

@Suppress("unused")
val fixChartFontCachePatch = bytecodePatch(
    name = "Fix: chart font-cache crash",
    description = "Bakes the chart Typeface cache's locale fields so the cache is built instead of " +
        "NPE-ing on a null list when a chart scrolls into view or Financial Data opens.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        // q4a's builder early-returns when static locale `b` equals the remembered `c`; with both
        // baked to the same default ("id") the Typeface ArrayList was never built.
        bakeHolderValue("Lq4a;", "b", "en")
        bakeHolderValue("Lq4a;", "c", "")
    }
}
