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
        name = "Sleep",
        packageName = "com.urbandroid.sleep",
        appIconColor = 0x7EC381,
        targets = listOf(AppTarget("20261006", 2311725, isExperimental = true))
    )

    val DUOLINGO = Compatibility(
        name = "Duolingo",
        packageName = "com.duolingo",
        appIconColor = 0x57CB01,
        targets = listOf( AppTarget("6.101.2", 2476, isExperimental = true), AppTarget("6.101.1", isExperimental = true) )
    )

    val BEND = Compatibility(
        name = "Bend",
        packageName = "com.bowerydigital.bend",
        appIconColor = 0x1573A7,
        targets = listOf(AppTarget("7.8.0", 215, isExperimental = true))
    )

    val INVESTING = Compatibility(
        name = "Investing",
        packageName = "com.fusionmedia.investing",
        appIconColor = 0x20282E,
        targets = listOf(AppTarget("6.52", 1660, isExperimental = true))
    )

    // Entries are appended by the crackpipe generator:
    // val APP_NAME = Compatibility(
    //     name = "App Name",
    //     packageName = "com.example.app",
    //     appIconColor = 0xRRGGBB,
    //     targets = listOf(AppTarget("1.2.3"))
    // )
}
