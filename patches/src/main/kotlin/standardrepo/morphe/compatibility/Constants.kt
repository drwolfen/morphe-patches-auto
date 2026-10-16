/**
 * Copyright 2026 crackpipe
 * Auto-maintained app compatibility registry.
 */

package standardrepo.morphe.compatibility

import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Compat {
    // Template demo patch. The example patch deliberately ships no compatibleWith(), so this
    // entry is reference-only — Morphe never offers com.example.app as an app. It stays as the
    // gate's negative-control target (fingerprint present, package not declared).
    val EXAMPLE = Compatibility(
        name = "Example App",
        packageName = "com.example.app",
        appIconColor = 0xFF0000,
        targets = listOf(AppTarget("1.0.0"))
    )

    val SLEEP = Compatibility(
        name = "Urbandroid",
        packageName = "com.urbandroid.sleep",
        appIconColor = 0x263400,
        targets = listOf(AppTarget("20261006"))
    )

    val DUOLINGO = Compatibility(
        name = "Com",
        packageName = "com.duolingo",
        appIconColor = 0xC75863,
        targets = listOf(AppTarget("6.101.1", isExperimental = true))
    )

    val BEND = Compatibility(
        name = "Bowerydigital",
        packageName = "com.bowerydigital.bend",
        appIconColor = 0x09069D,
        targets = listOf(AppTarget("7.8.0", isExperimental = true))
    )

    // Entries are appended by the crackpipe generator:
    // val APP_NAME = Compatibility(
    //     name = "App Name",
    //     packageName = "com.example.app",
    //     appIconColor = 0xRRGGBB,
    //     targets = listOf(AppTarget("1.2.3"))
    // )
}
