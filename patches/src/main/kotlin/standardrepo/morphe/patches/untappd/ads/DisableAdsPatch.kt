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
 * React Native over `react-native-applovin-max` (AppLovin MAX mediation) with Google Ads as an
 * adapter layer. Neutralizing the load entry points is the least invasive kill switch - the
 * components still mount and fire load callbacks, they just never fill.
 *
 * Both ad SDKs are covered:
 * - Google Ads: AdView.loadAd, static InterstitialAd.load, RewardedAd.load.
 * - AppLovin MAX: banner MaxAdViewImpl.loadAd, MaxInterstitialAd.loadAd, MaxRewardedAd.loadAd,
 *   MaxAppOpenAd.loadAd, native MaxNativeAdLoader.loadAd, plus the RN view managers' loadAd.
 *
 * SDK init (MobileAds / MAX) stays untouched so JS promises resolve instead of hanging.
 */
fun BytecodePatchContext.disableAds() {
    // Google Ads banner: AdView.loadAd(AdRequest) -> no-op.
    mutableClassDefByOrNull("Lcom/google/android/gms/ads/BaseAdView;")
        ?.methods?.filter { it.name == "loadAd" }
        ?.forEach { it.returnEarly() }

    // Google Ads interstitial: static load(...) -> no-op.
    mutableClassDefByOrNull("Lcom/google/android/gms/ads/interstitial/InterstitialAd;")
        ?.methods?.filter { it.name == "load" }
        ?.forEach { it.returnEarly() }

    // Google Ads rewarded video: static load(...) -> no-op.
    mutableClassDefByOrNull("Lcom/google/android/gms/ads/rewarded/RewardedAd;")
        ?.methods?.filter { it -> it.name == "load" }
        ?.forEach { it -> it.returnEarly() }

    // AppLovin MAX mediation SDK - Untappd's primary ad SDK (Google Ads is only an adapter layer).
    // No-op every format's load entry point plus the RN view managers' load helpers.
    listOf(
        "Lcom/applovin/impl/mediation/ads/MaxAdViewImpl;",
        "Lcom/applovin/mediation/ads/MaxInterstitialAd;",
        "Lcom/applovin/mediation/ads/MaxRewardedAd;",
        "Lcom/applovin/mediation/ads/MaxAppOpenAd;",
        "Lcom/applovin/mediation/nativeAds/MaxNativeAdLoader;",
        "Lcom/applovin/reactnative/AppLovinMAXAdViewManager;",
        "Lcom/applovin/reactnative/AppLovinMAXNativeAdViewManager;",
        "Lcom/applovin/reactnative/AppLovinMAXAdViewUiComponent;"
    ).forEach { cls ->
        mutableClassDefByOrNull(cls)
            ?.methods?.filter { it.name == "loadAd" || it.name == "loadAds" }
            ?.forEach { it.returnEarly() }
    }
}

val disableAdsPatch = bytecodePatch(
    "Disable ads",
    "Stops the Google Ads and AppLovin MAX banner, interstitial, native and rewarded load calls so no ad requests fill.",
    false
) {
    compatibleWith(Compat.UNTAPPD)
    execute { disableAds() }
}
