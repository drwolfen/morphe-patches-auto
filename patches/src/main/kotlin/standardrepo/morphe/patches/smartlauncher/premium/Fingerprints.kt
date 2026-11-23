/**
 * Copyright 2026 crackpipe
 * Smart Launcher 6.6 build 026 (versionCode 660260) anchors.
 *
 * License state lives in a single obfuscated singleton, Lcn9; (the PremiumLicenseState),
 * built from four PurchasableItem wrappers around SharedPreferences booleans:
 *   unlockpro    -> "ginlemon.inapp.unlockpro"
 *   lifetime     -> "ginlemon.inapp.lifetime.v6"
 *   feature pack -> "ginlemon.inapp.fp1"
 *   subscription -> subscription SKU
 *
 * Public predicates on that singleton are the only gate every screen consults:
 *   c()Z = lifetime || featurePack || subscription
 *   d()Z = unlockpro || lifetime || featurePack || subscription  ("has_pro")
 *   e()Z = subscription || featurePack (subscription-only consumers)
 *   b()Lzm9; = LicenseStatus(hasSL3ProLicense, hasFeaturePack) snapshot
 * and f() exposes the same status reactively by combining the four preference flows.
 *
 * Forcing these true flips Pro state everywhere at once: smart widgets, blur effect,
 * wallpaper 3D perspective, ultra-immersive mode, app-page folders, pop-up widgets,
 * cross-device licensing, plus removal of every upgrade banner / promo surface that is
 * shown only while "has_pro" is false (this build ships no third-party ad SDK - the free
 * edition's ads are Smart Launcher's own upsell promos).
 */

package standardrepo.morphe.patches.smartlauncher.premium

import app.morphe.patcher.Fingerprint

/** Lcn9;->d()Z - global has_pro predicate. */
object ProLicenseStateFingerprint : Fingerprint(
    definingClass = "Lcn9;",
    name = "d",
    returnType = "Z"
)

/** Lcn9;->c()Z - lifetime / feature-pack license predicate used by menus and drawer panels. */
object LifetimeLicenseFingerprint : Fingerprint(
    definingClass = "Lcn9;",
    name = "c",
    returnType = "Z"
)

/** Lcn9;->e()Z - subscription-only predicate (analytics + feature-pack consumers). */
object SubscriptionLicenseFingerprint : Fingerprint(
    definingClass = "Lcn9;",
    name = "e",
    returnType = "Z"
)
