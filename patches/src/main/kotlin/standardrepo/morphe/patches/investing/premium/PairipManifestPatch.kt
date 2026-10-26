/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.fusionmedia.investing v6.52 — do not edit by hand.
 */

package standardrepo.morphe.patches.investing.premium

import app.morphe.patcher.patch.resourcePatch

val pairipManifestBypassPatch = resourcePatch(
    name = "PairIP manifest bypass",
    description = "Replaces the PairIP Application class with com.fusionmedia.investing.InvestingApplication, drops LicenseActivity and CHECK_LICENSE.",
    default = false
) {
    execute {
        document("AndroidManifest.xml").use { doc ->
            val app = doc.getElementsByTagName("application").item(0) as org.w3c.dom.Element
            app.setAttribute("android:name", "com.pairip.application.Application")
            // Debuggable so `run-as` can read the app's SQLite schema on a re-signed build.
            app.setAttributeNS("http://schemas.android.com/apk/res/android", "android:debuggable", "true")
            val acts = doc.getElementsByTagName("activity")
            for (i in acts.length - 1 downTo 0) {
                val el = acts.item(i) as? org.w3c.dom.Element ?: continue
                if (el.getAttribute("android:name").contains("LicenseActivity"))
                    el.parentNode.removeChild(el)
            }
            val perms = doc.getElementsByTagName("uses-permission")
            for (i in perms.length - 1 downTo 0) {
                val el = perms.item(i) as? org.w3c.dom.Element ?: continue
                if (el.getAttribute("android:name").contains("CHECK_LICENSE"))
                    el.parentNode.removeChild(el)
            }
        }
    }
}
