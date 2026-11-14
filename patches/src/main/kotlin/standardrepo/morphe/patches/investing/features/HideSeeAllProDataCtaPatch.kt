/**
 * Copyright 2026 crackpipe
 */
package standardrepo.morphe.patches.investing.features

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.ReferenceType
import com.android.tools.smali.dexlib2.base.reference.BaseFieldReference
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11x
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction10x
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import standardrepo.morphe.compatibility.Compat

/**
 * The Ideas feed ("Stocks Picked by AI") appends a sticky orange `See All Pro Data`
 * bar below the strategy cards even on a patched (Pro) build.
 *
 * `Lych` builds the Ideas header/feed items; `La32` is the bottom-bar host that
 * switches on the paywall state object and renders the CTA through `Ljd4.o`.
 * Both call sites are void, so swapping each one for a `nop` removes the bar
 * without disturbing the surrounding layout.
 */
private val CTA_HOSTS = listOf(
    "La1q;",
    "Lfn;",
    "La32;",
    "Lav3;",
    "Lb41;",
    "Lc0p;",
    "Lc23;",
    "Lc7r;",
    "Lc80;",
    "Lcd4;",
    "Ldf4;",
    "Lap4;", "Li24;", "Ldg2;",
    "Lz92;", "Lsz;", "Lth2;", "Lwi4;", "Lzde;",
    "Leal;",
    "Lef4;",
    "Lez2;",
    "Lf55;",
    "Lff4;",
    "Lfzp;",
    "Lgf4;",
    "Lhsb;",
    "Lifh;",
    "Ljb4;",
    "Ljy3;",
    "Ljy4;",
    "Ljd4;",
    "Ll8h;",
    "Lw84;",
    "Lm6k;",
    "Lm74;",
    "Lmf4;",
    "Loa2;",
    "Loo0;",
    "Lpms;",
    "Lrx4;",
    "Lu31;",
    "Luo;",
    "Lur0;",
    "Lus;",
    "Luv4;",
    "Lvv4;",
    "Lw54;",
    "Lw74;",
    "Lx79;",
    "Lxhr;",
    "Lych;",
    "Lyzh;",
    "Lzd1;",
    "La7b;",
    "Lagm;",
    "Latb;",
    "Laud;",
    "Lcsa;",
    "Ldsa;",
    "Lea9;",
    "Lesa;",
    "Lf4s;",
    "Lfsa;",
    "Lhah;",
    "Lj5b;",
    "Lm9d;",
    "Lmzr;",
    "Lmob;",
    "Low4;",
    "Lqu4;",
    "Lsxj;",
    "Ltvr;",
    "Lu35;",
    "Luho;",
    "Lvj;",
    "Lz22;",
    "Lz31;",
    "Lmc7;",
    "Lnsa;",
    "Lp7s;",
    "Lo4i;",
    "Lu3s;",
    "Ldsa;",
)

val hideSeeAllProDataCtaPatch = bytecodePatch(
    name = "Hide: See All Pro Data CTA",
    description = "Removes the orange \"See All Pro Data\" bar from the Ideas feed.",
) {
    compatibleWith(Compat.INVESTING)

    execute {
        var n = 0
        val RX = Regex("Ljd4;->[a-z]")
        for (cn in CTA_HOSTS) {
            val cls = mutableClassDefByOrNull(cn) ?: continue
            for (mm in cls.methods) {
                val impl = mm.implementation ?: continue
                val list = impl.instructions.toList()
                for (i in list.indices.reversed()) {
                    val ins = list[i]
                    val r = (ins as? ReferenceInstruction)?.reference?.toString()
                    if (
                        r != null && (
                            RX.containsMatchIn(r) ||
                            r.contains("Lx31;->c") ||
                        r?.contains("Lid4;->i") == true || r?.contains("Lid4;->b") == true || r?.contains("Lid4;->c") == true || r?.contains("Lid4;->r")==true || r?.contains("Lid4;->d") == true ||
                        r?.contains("Ljd4;->G") == true ||
                            r.contains("Lcsa;->h") || r.contains("Lcsa;->t") ||
                            r.contains("Lu54;->p") || r.contains("Lu54;->d")
                        )
                    ) {
                        mm.replaceInstruction(i, "nop")
                        n++
                    }
                }
            }
        }
        // The Ideas CTA content lambda (Lpbc) draws nothing once its invoke returns Unit.
        val cta = mutableClassDefByOrNull("Lpbc;")
        val inv = cta?.methods?.firstOrNull { it.name == "invoke" }
        val impl = inv?.implementation
        if (impl != null) {
            val unitRef = object : BaseFieldReference() {
                override fun getDefiningClass() = "Lkotlin/Unit;"
                override fun getName() = "a"
                override fun getType() = "Lkotlin/Unit;"
            }
            impl.addInstruction(1, BuilderInstruction11x(Opcode.RETURN_OBJECT, 3))
            impl.addInstruction(0, BuilderInstruction21c(Opcode.SGET_OBJECT, 3, unitRef))
        }
        val dbg = mutableClassDefByOrNull("Lfn;")
        var dc = 0
        val sample = ArrayList<String>()
        if (dbg != null) {
            for (mm in dbg.methods) {
                val im = mm.implementation ?: continue
                for (ins in im.instructions) {
                    val rr = (ins as? ReferenceInstruction)?.reference?.toString() ?: continue
                    if (rr.contains("Lcsa")) { dc++; if (sample.size < 5) sample.add(rr) }
                }
            }
        }
        error("LFNDBG found=" + (dbg != null) + " csaRefs=" + dc + " sample=" + sample.joinToString(" | "))
        if (n == 62) error("CTA: expected >62 nops")

    }
}
