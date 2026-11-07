/**
 * Copyright 2026 crackpipe
 * Hand-written for com.untappdllc.app 5.0.3.
 */
package standardrepo.morphe.patches.untappd

import app.morphe.patcher.patch.booleanOption
import app.morphe.patcher.patch.BytecodePatch
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat
import standardrepo.morphe.patches.untappd.ads.disableAds
import standardrepo.morphe.patches.untappd.fixes.spoofPlayInstaller
import standardrepo.morphe.patches.untappd.premium.unlockInsiderEntitlements
import standardrepo.morphe.patches.untappd.premium.unlockInsiderBundlePatch

/**
 * Overall Untappd patch.
 *
 * Every feature is also a standalone patch; this one carries them as booleans so a build can pick
 * any combination in the patcher UI. All options default on: a plain apply yields ad-free Insider.
 */
val untappdPatch: BytecodePatch = bytecodePatch(
    name = "Untappd",
    description = "..."
) {
    compatibleWith(Compat.UNTAPPD)
    // The Insider bundle rewrite is a resource patch; the dependency keeps it running when the user
    // selects ONLY this overall patch in the Manager.
    dependsOn(unlockInsiderBundlePatch)
    booleanOption("unlockInsider", true, emptyMap(), "Unlock Insider", "Fake a live RevenueCat insider entitlement so Insider gates and paywalls open.")
    booleanOption("bypassPlayCheck", true, emptyMap(), "Bypass Play install check", "Report the installer as com.android.vending so the sideload block never trips.")
    booleanOption("disableAds", true, emptyMap(), "Disable ads", "No-op the banner and interstitial load calls so no ads fill.")
    execute {
        val o = untappdPatch.options
        if (o["unlockInsider"]!!.value as Boolean) unlockInsiderEntitlements()
        if (o["bypassPlayCheck"]!!.value as Boolean) spoofPlayInstaller()
        if (o["disableAds"]!!.value as Boolean) disableAds()
    }
}
