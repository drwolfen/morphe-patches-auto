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
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11n
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21s
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
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
                        val nxt = list.getOrNull(i + 1)
                        val isRes = nxt != null && nxt.opcode.name.startsWith("move-result")
                        if (isRes && nxt!!.opcode == Opcode.MOVE_RESULT_OBJECT) {
                            // object result is consumed by the UI: keep the call
                        } else {
                            if (isRes) {
                                val reg = (nxt!! as OneRegisterInstruction).registerA
                                when (nxt.opcode.name) {
                                    "move-result-wide" -> impl.replaceInstruction(i + 1, BuilderInstruction21s(Opcode.CONST_WIDE_16, reg, 0))
                                    else -> impl.replaceInstruction(i + 1, BuilderInstruction21s(Opcode.CONST_16, reg, 0))
                                }
                            }
                            mm.replaceInstruction(i, "nop")
                            n++
                        }
                    }
                }
            }
        }
        // The bar background is a SolidColor brush built from zmo's colour field; zero the
        // colour long (v5/v6) at the Lzmo;.<init> call inside cno.a so the bar paints transparent
        // while keeping its layout and slot table intact.
        val cno = mutableClassDefByOrNull("Lcno;")
        val cnoA = cno?.methods?.firstOrNull { it.name == "a" && it.implementation != null }
        val implA = cnoA?.implementation
        if (implA != null) {
            val insA = implA.instructions.toList()
            for (i in insA.indices) {
                val r = (insA[i] as? ReferenceInstruction)?.reference?.toString()
                if (r != null && r.startsWith("Lzmo;-><init>")) {
                    implA.addInstruction(i, BuilderInstruction21s(Opcode.CONST_WIDE_16, 5, 0))
                    break
                }
            }
        }
        // The bar background is painted from zmo's colour field d; zero it inside the ctor so
        // every instance paints transparent regardless of which cno.a branch constructed it.
        val zmo = mutableClassDefByOrNull("Lzmo;")
        val zmoCtor = zmo?.methods?.firstOrNull { it.name == "<init>" && it.implementation != null }
        val implZ = zmoCtor?.implementation
        if (implZ != null) {
            val insZ = implZ.instructions.toList()
            for (i in insZ.indices) {
                val r = (insZ[i] as? ReferenceInstruction)?.reference?.toString()
                if (r != null && r.startsWith("Lzmo;->d")) {
                    implZ.addInstruction(i, BuilderInstruction21s(Opcode.CONST_WIDE_16, 3, 0))
                    break
                }
            }
        }
        // The sticky bar's background brush is built from the same colour arg; zero it at the
        // Ly3s.c brush factory call so the bar paints transparent.
        val implB = cnoA?.implementation
        if (implB != null) {
            val insB = implB.instructions.toList()
            for (i in insB.indices) {
                val r = (insB[i] as? ReferenceInstruction)?.reference?.toString()
                if (r != null && r.startsWith("Ly3s;->c")) {
                    implB.addInstruction(i, BuilderInstruction21s(Opcode.CONST_WIDE_16, 5, 0))
                    break
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
        java.io.File(System.getProperty("java.io.tmpdir") ?: ".", "site.log")
            .appendText("CTANOP " + n + "\n")


    }
}
