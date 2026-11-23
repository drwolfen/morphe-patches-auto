/**
 * Copyright 2026 crackpipe
 * Hand-written for ginlemon.flowerfree 6.6 build 026 (versionCode 660260).
 */

package standardrepo.morphe.patches.smartlauncher.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import standardrepo.morphe.compatibility.Compat

/**
 * Enable Pro.
 *
 * Smart Launcher keeps its entitlement in four SharedPreferences booleans behind one
 * singleton (Lcn9;). Every gate funnels through three cheap predicates:
 *
 * - d()Z  "has_pro"     = unlockpro || lifetime.v6 || fp1 || subscription;
 * - c()Z                = lifetime.v6 || fp1 || subscription (menus, drawer panel, tips);
 * - e()Z                = subscription || fp1 (analytics + feature-pack surfaces).
 *
 * Returning true from all three unlocks the complete Pro set: smart widgets, blur effect,
 * wallpaper 3D perspective, ultra-immersive mode, folders in the app page, pop-up widgets,
 * cross-device licensing, and hides every "upgrade to Pro" banner and promo card.
 */
val enableProPatch = bytecodePatch(
    name = "Enable Pro",
    description = "Auto-generated: unlocks Smart Launcher Pro (all premium features, upsell banners gone).",
    default = true
) {
    compatibleWith(Compat.SMARTLAUNCHER)

    execute {
        ProLicenseStateFingerprint.method.returnEarly(true)
        LifetimeLicenseFingerprint.method.returnEarly(true)
        SubscriptionLicenseFingerprint.method.returnEarly(true)
    }
}
