/**
 * Copyright 2026 crackpipe
 * Hand-written for apps.infinite.airhorn 3.11 (versionCode 40).
 */

package standardrepo.morphe.patches.airhorn.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

/**
 * Remove ads / unlock the one-time purchase.
 *
 * Air Horn monetises with a single non-consumable Google Play SKU,
 * "remove_ads_one_time_purchase", cached as a SharedPreferences boolean ("purchasecode").
 * Every screen reads it through its own private getPurchaseValueFromPref()Z predicate:
 *
 * - onCreate hides adContainerView and skips building/loading the AdView banner;
 * - showInterstitial navigates straight to MainMenu instead of showing an interstitial;
 * - MainMenu hides the "remove ads" entry and re-lays out the menu.
 *
 * So one hook per class covers both halves of the feature: no ads AND owned-purchase UI.
 */
val removeAdsPatch = bytecodePatch(
    name = "Remove Ads",
    description = "Auto-generated: unlocks Air Horn Premium (ads off, purchase owned).",
    default = true
) {
    compatibleWith(Compat.AIRHORN)

    execute {
        MainActivityPurchasePrefFingerprint.method.returnEarly(true)
        MainActivity2PurchasePrefFingerprint.method.returnEarly(true)
        MainActivity3PurchasePrefFingerprint.method.returnEarly(true)
        MainMenuPurchasePrefFingerprint.method.returnEarly(true)
        ChangePickerPurchasePrefFingerprint.method.returnEarly(true)
        PlayPickerPurchasePrefFingerprint.method.returnEarly(true)
        SoundPickerPurchasePrefFingerprint.method.returnEarly(true)
        IntertistialPurchasePrefFingerprint.method.returnEarly(true)
        IntroPurchasePrefFingerprint.method.returnEarly(true)
    }
}
