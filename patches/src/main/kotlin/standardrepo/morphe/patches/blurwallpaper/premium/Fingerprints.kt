/**
 * Copyright 2026 crackpipe
 * Auto-generated for apps.automan.blurwallpaper v3.0.0 - do not edit by hand.
 */

package standardrepo.morphe.patches.blurwallpaper.premium

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import app.morphe.patcher.opcode
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.AccessFlags

object ProcessResponseFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseClient;",
    returnType = "V",
    name = "processResponse"
)

object ValidateResponseFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseResponseHelper;",
    returnType = "V",
    name = "validateResponse"
)

object PerformLocalInstallerCheckFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PRIVATE),
    returnType = "Z",
    parameters = listOf(),
    filters = listOf(string("com.android.vending"), string("Local install check failed due to wrong installer.")),
    name = "performLocalInstallerCheck"
)

object SharedPrefsGetterFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Ljava/lang/String;", "Ljava/lang/Object;"),
    filters = listOf(string("flip_clock"), opcode(Opcode.INVOKE_INTERFACE)),
)

object OnReceivedFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "V",
    parameters = listOf("Lcom/revenuecat/purchases/CustomerInfo;"),
    filters = listOf(string("customerInfo"), string("start_blur")),
    name = "onReceived"
)

object StartActivityForResultFingerprint : Fingerprint(
    definingClass = "Lԩ/އ;",
    returnType = "V",
    parameters = listOf("Landroid/content/Intent;", "I"),
    name = "startActivityForResult"
)

object StartActivityForResultFingerprint_sBundleV : Fingerprint(
    definingClass = "Lԩ/އ;",
    returnType = "V",
    parameters = listOf("Landroid/content/Intent;", "I", "Landroid/os/Bundle;"),
    name = "startActivityForResult"
)
