/**
 * Copyright 2026 crackpipe
 * Auto-maintained app compatibility registry.
 */

package standardrepo.morphe.compatibility

import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Compat {
    // Template demo patch, SCOPED to com.example.app. It used to declare no compatibleWith()
    // (universal), which made it applicable to every package and put a second patch on every
    // app card; scoping keeps exactly ONE patch per app. Stays as the gate's negative-control
    // target (fingerprint present, package not declared by a real app patch).
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
        targets = listOf(AppTarget("20261006", 2311718, isExperimental = true))
    )

    val DUOLINGO = Compatibility(
        name = "Duolingo",
        packageName = "com.duolingo",
        appIconColor = 0x57CB01,
        targets = listOf( AppTarget("6.105.2", 2497, isExperimental = true), AppTarget("6.105.1", 2496, isExperimental = true), AppTarget("6.101.2", 2476, isExperimental = true), AppTarget("6.101.1", isExperimental = true) )
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

    val BLURWALLPAPER = Compatibility(
        name = "Blurwallpaper",
        packageName = "apps.automan.blurwallpaper",
        appIconColor = 0x497246,
        targets = listOf(AppTarget("3.0.0", 32, isExperimental = true))
    )

    val UNTAPPD = Compatibility(
        name = "Untappdllc",
        packageName = "com.untappdllc.app",
        appIconColor = 0xDFA200,
        targets = listOf(AppTarget("5.0.3", 2147102969, isExperimental = true))
    )

    // Entries are appended by the crackpipe generator:
    // val APP_NAME = Compatibility(
    //     name = "App Name",
    //     packageName = "com.example.app",
    //     appIconColor = 0xRRGGBB,
    //     targets = listOf(AppTarget("1.2.3"))
    // )
}
