/**
 * Copyright 2026 crackpipe
 * Auto-maintained app compatibility registry.
 */

package hoodles.morphe.compatibility

import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Compat {
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

    // Entries are appended by the crackpipe generator:
    // val APP_NAME = Compatibility(
    //     name = "App Name",
    //     packageName = "com.example.app",
    //     appIconColor = 0xRRGGBB,
    //     targets = listOf(AppTarget("1.2.3"))
    // )
}
