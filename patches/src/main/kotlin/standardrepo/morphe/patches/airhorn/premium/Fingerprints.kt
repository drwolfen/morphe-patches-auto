/**
 * Copyright 2026 crackpipe
 * Air Horn 3.11 (versionCode 40) anchors.
 *
 * The app stores its one-time "remove_ads_one_time_purchase" entitlement in a
 * SharedPreferences boolean ("purchasecode") and exposes it through the same private
 * predicate on every screen: getPurchaseValueFromPref()Z. Forcing that predicate true
 * flips every consumer at once - banner container hidden, no AdView built or loaded,
 * interstitial skipped, "remove ads" menu entry hidden.
 */

package standardrepo.morphe.patches.airhorn.premium

import app.morphe.patcher.Fingerprint

object MainActivityPurchasePrefFingerprint : Fingerprint(
    definingClass = "Lapps/infinite/airhorn/MainActivity;",
    name = "getPurchaseValueFromPref",
    returnType = "Z"
)

object MainActivity2PurchasePrefFingerprint : Fingerprint(
    definingClass = "Lapps/infinite/airhorn/MainActivity2;",
    name = "getPurchaseValueFromPref",
    returnType = "Z"
)

object MainActivity3PurchasePrefFingerprint : Fingerprint(
    definingClass = "Lapps/infinite/airhorn/MainActivity3;",
    name = "getPurchaseValueFromPref",
    returnType = "Z"
)

object MainMenuPurchasePrefFingerprint : Fingerprint(
    definingClass = "Lapps/infinite/airhorn/MainMenu;",
    name = "getPurchaseValueFromPref",
    returnType = "Z"
)

object ChangePickerPurchasePrefFingerprint : Fingerprint(
    definingClass = "Lapps/infinite/airhorn/ChangePicker;",
    name = "getPurchaseValueFromPref",
    returnType = "Z"
)

object PlayPickerPurchasePrefFingerprint : Fingerprint(
    definingClass = "Lapps/infinite/airhorn/PlayPicker;",
    name = "getPurchaseValueFromPref",
    returnType = "Z"
)

object SoundPickerPurchasePrefFingerprint : Fingerprint(
    definingClass = "Lapps/infinite/airhorn/SoundPicker;",
    name = "getPurchaseValueFromPref",
    returnType = "Z"
)

object IntertistialPurchasePrefFingerprint : Fingerprint(
    definingClass = "Lapps/infinite/airhorn/Intertistial;",
    name = "getPurchaseValueFromPref",
    returnType = "Z"
)

object IntroPurchasePrefFingerprint : Fingerprint(
    definingClass = "Lapps/infinite/airhorn/Intro;",
    name = "getPurchaseValueFromPref",
    returnType = "Z"
)
