/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11x
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.base.reference.BaseFieldReference
import standardrepo.morphe.compatibility.Compat

/**
 * The Ideas feed keeps a sticky orange `See All Pro Data` strip between the
 * feed and the tab bar even on the unlocked build.
 *
 * The strip is composed by the one-shot lambda `Ln8m;->invoke` (its stack is
 * `n8m -> cno -> cbh -> g9c -> ngp`), which builds the orange Surface card.
 * The label is server-supplied and never appears in resources.arsc or in any
 * dex string table. Returning Unit from the lambda drops the whole card while
 * the Ideas feed and the tab bar are composed by other lambdas.
 */
val hideSeeAllProDataCtaPatch = bytecodePatch(
    name = "Hide: See All Pro Data CTA",
    description = "Removes the orange 'See All Pro Data' bar from the Ideas feed.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        val cls = mutableClassDefByOrNull("Ln8m;")
            ?: throw Exception("CTA: bar lambda class Ln8m; missing")
        val method = cls.methods.firstOrNull { it.name == "invoke" }
            ?: throw Exception("CTA: bar lambda Ln8m;->invoke not found")
        val impl = method.implementation
            ?: throw Exception("CTA: Ln8m;->invoke has no body")

        val unitRef = object : BaseFieldReference() {
            override fun getDefiningClass() = "Lkotlin/Unit;"
            override fun getName() = "a"
            override fun getType() = "Lkotlin/Unit;"
        }
        val r = impl.registerCount - 3
        impl.addInstruction(1, BuilderInstruction11x(Opcode.RETURN_OBJECT, r))
        impl.addInstruction(0, BuilderInstruction21c(Opcode.SGET_OBJECT, r, unitRef))
    }
}
