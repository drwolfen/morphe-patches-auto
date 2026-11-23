/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat

/**
 * The Ideas feed ("Stocks Picked by AI") appends an orange `See All Pro Data` banner.
 *
 * It is NOT a Compose card: the view hierarchy shows it as a native
 * `com.fusionmedia.investing.feature.web.promo.ui.PromoWebView` hosted through a
 * Compose `ViewFactoryHolder` (measured ~y1900-2054, inner WebView 154px tall).
 * Hiding that WebView collapses the orange paint; no opaque overlay band is needed,
 * so nothing darkens/clips other screens and no clicks are swallowed.
 *
 * The class has exactly one constructor `<init>(Context, Function1)`. After its
 * super call completes we set visibility GONE on itself (`p0`) using `p1` as scratch.
 */
val hideSeeAllProDataCtaPatch = bytecodePatch(
    name = "Hide: See All Pro Data CTA",
    description = "Collapses the orange \"See All Pro Data\" promo WebView so the banner no longer renders.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val cls = mutableClassDefByOrNull("Lcom/fusionmedia/investing/feature/web/promo/ui/PromoWebView;")
            ?: throw Exception("PromoWebView class missing")

        var hooked = 0
        for (m in cls.methods) {
            if (m.name != "<init>") continue
            val impl = m.implementation ?: continue
            val retIdx = impl.instructions.toList().indexOfLast { it.opcode.name == "return-void" }
            if (retIdx < 0) continue
            // v3 = this, v4 = scratch (both already live in this constructor).
            m.addInstructions(
                retIdx,
                """
                    const/16 v4, 0x8
                    invoke-virtual {v3, v4}, Landroid/view/View;->setVisibility(I)V
                """.trimIndent(),
            )
            hooked++
        }

        java.io.File(System.getProperty("java.io.tmpdir") ?: ".", "site.log")
            .appendText("PROMOWEBVIEW_HIDE sites=$hooked\n")
    }
}
