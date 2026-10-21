/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.duolingo v6.101.2 - do not edit by hand.
 */

package standardrepo.morphe.patches.duolingo.premium

import app.morphe.patcher.Fingerprint

object InitFingerprint : Fingerprint(
    definingClass = "Ls990;",
    returnType = "V",
    parameters = listOf("Lcom/duolingo/data/user/User;"),
    name = "<init>"
)

object ToStringFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/data/user/User;",
    returnType = "Ljava/lang/String;",
    name = "toString"
)

object InitFingerprintEnergyConfig : Fingerprint(
    definingClass = "Lcom/duolingo/core/energy/models/EnergyConfig;",
    returnType = "V",
    parameters = listOf("I", "I", "I", "J", "J", "J", "I", "J", "Z"),
    name = "<init>"
)

object InitFingerprintEnergyConfig_IIJJJIJV : Fingerprint(
    definingClass = "Lcom/duolingo/core/energy/models/EnergyConfig;",
    returnType = "V",
    parameters = listOf("I", "I", "J", "J", "J", "I", "J"),
    name = "<init>"
)

object UcFingerprint : Fingerprint(
    definingClass = "Lcom/bytedance/sdk/openadsdk/eu/uc/uc/qtm;",
    returnType = "Z",
    name = "uc"
)

object InvokeFingerprint : Fingerprint(
    definingClass = "Lj0x;",
    returnType = "Ljava/lang/Object;",
    name = "invoke"
)

object OnClickFingerprint : Fingerprint(
    definingClass = "Lry80;",
    returnType = "V",
    name = "onClick"
)

object StartActivityForResultFingerprint : Fingerprint(
    definingClass = "Landroidx/activity/ComponentActivity;",
    returnType = "V",
    parameters = listOf("Landroid/content/Intent;", "I"),
    name = "startActivityForResult"
)

object StartActivityForResultFingerprintComponentActivity : Fingerprint(
    definingClass = "Landroidx/activity/ComponentActivity;",
    returnType = "V",
    parameters = listOf("Landroid/content/Intent;", "I", "Landroid/os/Bundle;"),
    name = "startActivityForResult"
)

object OnClickFingerprintLj13 : Fingerprint(
    definingClass = "Lj13;",
    returnType = "V",
    name = "onClick"
)

object InvokeFingerprintLh5 : Fingerprint(
    definingClass = "Lh5;",
    returnType = "Ljava/lang/Object;",
    name = "invoke"
)

object IsPromoFingerprint : Fingerprint(
    definingClass = "Lcom/duolingo/core/sessionend/api/SessionEndMessageType;",
    returnType = "Z",
    name = "isPromo"
)
