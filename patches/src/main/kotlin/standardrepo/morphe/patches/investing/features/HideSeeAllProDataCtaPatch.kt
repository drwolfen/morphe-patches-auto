package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat

val hideSeeAllProDataCtaPatch = bytecodePatch(
    name = "Hide: See All Pro Data CTA",
    description = "Hides the orange See All Pro Data bar from the Ideas feed.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val ca = mutableClassDefByOrNull("Lcom/fusionmedia/investing/feature/main/tabs/ui/ContentActivity;")
        if (ca != null) {
            for (mm in ca.methods) {
                if (mm.name != "onCreate" || mm.implementation == null) continue
                val at = minOf(106, mm.implementation!!.instructions.size - 2)
                val ins = listOf(
                    "new-instance v4, Landroid/widget/FrameLayout\$LayoutParams;",
                    "const/4 v6, -1",
                    "invoke-direct {v4, v6, v6}, Landroid/widget/FrameLayout\$LayoutParams;-><init>(II)V",
                    "const/16 v6, 200",
                    "iput v6, v4, Landroid/view/ViewGroup\$LayoutParams;->height:I",
                    "const/16 v6, 80",
                    "iput v6, v4, Landroid/widget/FrameLayout\$LayoutParams;->gravity:I",
                    "const/16 v6, 240",
                    "iput v6, v4, Landroid/view/ViewGroup\$MarginLayoutParams;->bottomMargin:I",
                    "new-instance v5, Landroid/view/View;",
                    "invoke-direct {v5, v7}, Landroid/view/View;-><init>(Landroid/content/Context;)V",
                    "const v6, -16777216",
                    "invoke-virtual {v5, v6}, Landroid/view/View;->setBackgroundColor(I)V",
                    "const/4 v6, 0x0",
                    "invoke-virtual {v5, v6}, Landroid/view/View;->setClickable(Z)V",
                    "invoke-virtual {v5, v6}, Landroid/view/View;->setFocusable(Z)V",
                    "invoke-virtual {v7}, Landroid/app/Activity;->getWindow()Landroid/view/Window;",
                    "move-result-object v6",
                    "invoke-virtual {v6}, Landroid/view/Window;->getDecorView()Landroid/view/View;",
                    "move-result-object v6",
                    "check-cast v6, Landroid/view/ViewGroup;",
                    "invoke-virtual {v6, v5, v4}, Landroid/view/ViewGroup;->addView(Landroid/view/View;Landroid/view/ViewGroup\$LayoutParams;)V",
                )
                for ((i3, s3) in ins.withIndex()) mm.addInstruction(at + i3, s3)
            }
        }
    }
}
