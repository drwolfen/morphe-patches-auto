/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.fixes

import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat
import standardrepo.morphe.patches.investing.util.bakeHolderValue

@Suppress("unused")
val fixCashFlowEnumPatch = bytecodePatch(
    name = "Fix: cash-flow rating enum crash",
    description = "Bakes the cash-flow rating enum's FAIR constant name, so Moshi's reflective enum " +
        "adapter no longer throws Missing field when View More in Most Undervalued opens.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        // ns2 builds FAIR from this holder read. Its own `<init>(String,II)` hides that it is an
        // enum, so the enum-name heuristic missed it and the default "id" broke Class.getField.
        bakeHolderValue("Lio/appmetrica/analytics/billingv8/internal/NO/HqNexJL;", "vJb", "FAIR")
    }
}
