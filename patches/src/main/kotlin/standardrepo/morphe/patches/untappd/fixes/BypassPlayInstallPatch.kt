/**
 * Copyright 2026 crackpipe
 * Hand-written for com.untappdllc.app 5.0.3.
 */
package standardrepo.morphe.patches.untappd.fixes

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat

/**
 * Bypass the Play-install check.
 *
 * The app's JS asks react-native-device-info for the installer package and, when it is not
 * `com.android.vending`, parks the UI behind a white card with a `market://details?id=...` link.
 * On a re-signed sideload the check is always negative, so onboarding never proceeds.
 *
 * Both RNDeviceInfo getters are rewritten: the sync getter returns `com.android.vending`, the
 * Promise getter resolves with it - a bare `returnEarly` would leave the JS promise pending.
 */
fun BytecodePatchContext.spoofPlayInstaller() {
    val mod = mutableClassDefByOrNull("Lcom/learnium/RNDeviceInfo/RNDeviceModule;") ?: return

    mod.methods.firstOrNull { it.name == "getInstallerPackageNameSync" && it.returnType == "Ljava/lang/String;" }
        ?.returnEarly("com.android.vending")

    mod.methods.firstOrNull {
        it.name == "getInstallerPackageName" && it.parameterTypes == listOf("Lcom/facebook/react/bridge/Promise;")
    }?.let { m ->
        val impl = m.implementation ?: return@let
        m.removeInstructions(0, impl.instructions.size)
        m.addInstructions(0, """
            const-string v1, "com.android.vending"
            invoke-interface {p1, v1}, Lcom/facebook/react/bridge/Promise;->resolve(Ljava/lang/Object;)V
            return-void
        """.trimIndent())
    }
}

val spoofPlayInstallerPatch = bytecodePatch(
    "Bypass Play install check",
    "Reports the installer as com.android.vending so the sideload block on onboarding never trips.",
    false
) {
    compatibleWith(Compat.UNTAPPD)
    execute { spoofPlayInstaller() }
}
