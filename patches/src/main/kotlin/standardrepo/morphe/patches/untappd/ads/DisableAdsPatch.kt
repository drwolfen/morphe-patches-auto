/**
 * Copyright 2026 crackpipe
 * Hand-written for com.untappdllc.app 5.0.3.
 */
package standardrepo.morphe.patches.untappd.ads

import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

/**
 * Disable ads.
 *
 * React Native over `react-native-google-mobile-ads`: the banner component builds a native AdView
 * and calls `loadAd`; interstitials and rewarded videos are preloaded via the static `load`
 * methods and later shown. Neutralizing the load methods is the least invasive kill switch - the
 * components still mount and fire load callbacks, they just never fill.
 *
 * `MobileAds.initialize` stays untouched so the SDK remains initialized and JS promises resolve
 * instead of hanging.
 */
fun BytecodePatchContext.disableAds() {
    // Banner: AdView.loadAd(AdRequest) -> no-op.
    mutableClassDefByOrNull("Lcom/google/android/gms/ads/BaseAdView;")
        ?.methods?.filter { it.name == "loadAd" }
        ?.forEach { it.returnEarly() }

    // Interstitial: static load(...) -> no-op.
    mutableClassDefByOrNull("Lcom/google/android/gms/ads/interstitial/InterstitialAd;")
        ?.methods?.filter { it.name == "load" }
        ?.forEach { it.returnEarly() }

    // Rewarded video: static load(...) -> no-op.
    mutableClassDefByOrNull("Lcom/google/android/gms/ads/rewarded/RewardedAd;")
        ?.methods?.filter { it.name == "load" }
        ?.forEach { it.returnEarly() }
}

val disableAdsPatch = bytecodePatch(
    "Disable ads",
    "Stops the Google Mobile Ads banner and interstitial loads so no ad requests fill.",
    false
) {
    compatibleWith(Compat.UNTAPPD)
    execute { disableAds() }
}
