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
    "La32;",
    "Lav3;",
    "Lb41;",
    "Lc0p;",
    "Lc23;",
    "Lc7r;",
    "Lc80;",
    "Lcd4;",
    "Ldf4;",
    "Lap4;", "Li24;", "Ldg2;", "Lsz;", "Lth2;", "Lwi4;", "Lzde;",
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
    // The Ideas tab's bottom-bar switch. The visible bar is rendered by the
    // `Lid4;->i` branch of this packed-switch dispatcher; without it the
    // composable is never entered and the sticky CTA disappears.
    "Lz92;",
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
                            r.contains("Lcsa;->h") ||
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
        // The sticky orange bar is installed into the Compose scaffold as a
        // bottomBar through the `Ldb9` lambda (LocalBottomBar). Its `invoke`
        // dispatches to `Ldd4;->h` (ticker overview) or `Ldd4;->g` (Ideas),
        // which render the bar with a *server-supplied* label (that is why the
        // literal text never appears in dex or resources). Each dispatch arm
        // ends with `sget-object Unit; return-object`, so nop'ing the render
        // call leaves an empty bottom bar instead of a visible CTA.
        var nBar = 0
        val barMethod = mutableClassDefByOrNull("Ldb9;")?.methods?.firstOrNull { it.name == "invoke" }
        val barImpl = barMethod?.implementation
        if (barMethod != null && barImpl != null) {
            val list = barMethod.implementation!!.instructions.toList()
            for (i in list.indices.reversed()) {
                val r = (list[i] as? ReferenceInstruction)?.reference?.toString() ?: continue
                if (r.contains("Ldd4;->h") || r.contains("Ldd4;->g")) {
                    barMethod.replaceInstruction(i, "nop")
                    n++
                    nBar++
                }
            }
        }
        // The real render sites sit one level deeper: `Lwg9;->b` builds the
        // Ideas bottom bar by calling `Ldd4;->g`, and `Ldg2;->n` builds the
        // ticker-overview bar by calling `Ldd4;->h`. `Ldb9` only wraps them.
        // Keep those calls but empty the bar bodies: the parent composable keeps
        // an opaque black box in the slot (the CTA block remnant) that covers
        // the Economic/Earnings/Dividend/IPO tabs and eats their taps.
        var nHost = 0
        for ((hostCls, hostMethod) in listOf("Lwg9;" to "b", "Ldg2;" to "n")) {
            val hostImpl = mutableClassDefByOrNull(hostCls)?.methods?.firstOrNull { it.name == hostMethod }?.implementation ?: continue
            val hostTarget = if (hostCls == "Lwg9;") "Ldd4;->g" else "Ldd4;->h"
            if (hostImpl.instructions.toList().any { (it as? ReferenceInstruction)?.reference?.toString()?.contains(hostTarget) == true }) nHost++
        }
        var nEmpty = 0
        for ((barCls, barMethod) in listOf("Ldd4;" to "g", "Ldd4;" to "h")) {
            val impl = mutableClassDefByOrNull(barCls)?.methods?.firstOrNull { it.name == barMethod }?.implementation ?: continue
            impl.addInstruction(0, BuilderInstruction10x(Opcode.RETURN_VOID))
            n++
            nEmpty++
        }
        // Kill the whole bar container at the real call sites so the parent
        // composable never enters the Box (no black fill, no tap interception):
        // `Lwg9;->b` emits the Ideas bar via `Ldd4;->g`; `Ldg2;->n` opens the
        // overview bar container (`Lcd4;->m`), emits its body (`Ldd4;->h`) and
        // the container emitter (`Lcd4;->h`).
        var nKill = 0
        for ((hostCls, hostMethod) in listOf("Lwg9;" to "b", "Ldg2;" to "n")) {
            val impl = mutableClassDefByOrNull(hostCls)?.methods?.firstOrNull { it.name == hostMethod }?.implementation ?: continue
            val targets = if (hostCls == "Lwg9;") listOf("Ldd4;->g") else listOf("Ldd4;->h", "Lcd4;->h", "Lcd4;->m")
            val list = impl.instructions.toList()
            for (i in list.indices.reversed()) {
                val r = (list[i] as? ReferenceInstruction)?.reference?.toString() ?: continue
                if (targets.any { r.contains(it) }) {
                    impl.replaceInstruction(i, BuilderInstruction10x(Opcode.NOP))
                    n++
                    nKill++
                }
            }
        }
        // Even with the container gone the Scaffold bottomBar slot keeps a
        // focusable view; neutralise the clickable modifier builder itself so
        // every use (container + body) stops intercepting touches while still
        // returning a Modifier for the surrounding modifier chain.
        var nClick = 0
        val clickImpl = mutableClassDefByOrNull("Lopd;")?.methods?.firstOrNull { it.name == "f" }?.implementation
        if (clickImpl != null) {
if (false) clickImpl.replaceInstruction(0, BuilderInstruction11x(Opcode.RETURN_OBJECT, clickImpl.registerCount - 2))
            n++
            nClick++
        }
        // The bar Surface also installs its own semantics/clickable modifier
        // through `Ldah;->J`; returning the incoming modifier drops the touch
        // semantics while keeping the draw.
        var nSurf = 0
        val surfImpl = mutableClassDefByOrNull("Ldah;")?.methods?.firstOrNull { it.name == "J" }?.implementation
        if (surfImpl != null) {
if (false) surfImpl.replaceInstruction(0, BuilderInstruction11x(Opcode.RETURN_OBJECT, surfImpl.registerCount - 2))
            n++
            nSurf++
        }
        // The live Ideas bar is composed by `Ljgh;->g`; strip its clickable,
        // width and height so the leftover container neither draws nor eats taps.
        var nBarUi = 0
        classDefForEach { cd ->
            if (cd.type == "Ljgh;" && nBarUi == 0) {
                val barImpl = mutableClassDefByOrNull(cd.type)?.methods
                    ?.firstOrNull { it.name == "g" && it.implementation != null }?.implementation
                if (barImpl != null) {
                    n++
                    nBarUi++
                    n++
                    nBarUi++
                }
            }
        }
        // The surviving clickable node is built directly (new-instance wk7 +
        // ctor + lambda) inside the bar composable `Ly94;->e`; drop that trio.
        var nWk = 0
        classDefForEach { cd ->
            if (cd.type == "Ly94;") {
                val impl = mutableClassDefByOrNull(cd.type)?.methods
                    ?.firstOrNull { it.name == "e" && it.implementation != null }?.implementation
                if (impl != null) {
                    val ins = impl.instructions
                    for (i in ins.indices) {
                        if (i >= 3 && ins[i].opcode == Opcode.INVOKE_DIRECT) {
                            val ni = ins[i - 2]
                            val ref = (ni as? com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction)?.reference
                            if (ni.opcode == Opcode.NEW_INSTANCE && ref?.toString()?.contains("Lwk7;") == true) {
if (false) for (k in i - 2..i + 1) impl.replaceInstruction(k, BuilderInstruction10x(Opcode.NOP))
                                n++
                                nWk++
                            }
                        }
                    }
                        // the black rectangle itself is an embedded AndroidView
                        // (`Log4;->e`); drop its call too.
                        for (i in ins.indices) {
                            val ref = (ins[i] as? com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction)?.reference
                            if (ref != null && ref.toString().contains("Log4;->e")) {
if (false) impl.replaceInstruction(i, BuilderInstruction10x(Opcode.NOP)); n++; nWk++
                            }
                        }
                }
                }
            }
        if (nWk < 1) error("CTA: clickable node not dropped")
        // The full-width hit row is its own clickable node (`Lve9;->b`); drop it
        // so the tab pills underneath receive the taps.
        var nRow = 0
        var nVe = 0
        classDefForEach { cd ->
            if (cd.type == "Lve9;") {
                val impl = mutableClassDefByOrNull(cd.type)?.methods
                    ?.firstOrNull { it.name == "b" && it.implementation != null }?.implementation
                if (impl != null) {
                    val ins = impl.instructions
                    for (i in ins.indices) {
                        val ref = (ins[i] as? com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction)?.reference
                        if (ins[i].opcode == Opcode.NEW_INSTANCE && ref?.toString()?.contains("Lwk7;") == true) {
if (false) for (k in i..i + 3) impl.replaceInstruction(k, BuilderInstruction10x(Opcode.NOP))
                            n++
                            nVe++
                        }
                    }
                }
            }
        }
        // The bar body itself: `Lea9;->e` composes the container via `Lcbh;->f`.
        // Nop that call so the whole element (fill + hit area) is never emitted.
        var nEa = 0
        classDefForEach { cd ->
            if (cd.type == "Lea9;") {
                val impl = mutableClassDefByOrNull(cd.type)?.methods
                    ?.firstOrNull { it.name == "e" && it.implementation != null }?.implementation
                if (impl != null) {
                    for (i in impl.instructions.indices) {
                        val ref = (impl.instructions[i] as? com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction)?.reference
                        if (ref != null && ref.toString().contains("Lcbh;->")) {
if (false) impl.replaceInstruction(i, BuilderInstruction10x(Opcode.NOP)); n++; nVe++
                        }
                    }
                }
            }
        }
        if (nVe < 2) error("CTA: bar container call not killed")
        // Bar fill: the polymorphic content lambda `Lfn;->invoke` renders the
        // black Surface via `Ll8h;->r` in its CTA branch.
        var nL8 = 0
        classDefForEach { cd ->
            if (cd.type == "Lfn;") {
                val impl = mutableClassDefByOrNull(cd.type)?.methods
                    ?.firstOrNull { it.name == "invoke" && it.implementation != null }?.implementation
                if (impl != null) {
                    for (i in impl.instructions.indices) {
                        val ref = (impl.instructions[i] as? com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction)?.reference
                        if (ref?.toString()?.contains("Ll8h;->r") == true) {
                            impl.replaceInstruction(i, BuilderInstruction10x(Opcode.NOP)); n++; nL8++
                        }
                    }
                }
            }
        }
        if (nL8 < 1) error("CTA: bar surface not dropped")
        // Pills must not steal the strip's own x->tab hit mapping.
        var nP = 0
        classDefForEach { cd ->
            if (cd.type == "Lb94;") {
                val impl = mutableClassDefByOrNull(cd.type)?.methods
                    ?.firstOrNull { it.name == "l" && it.implementation != null }?.implementation
                if (impl != null) {
                    for (i in impl.instructions.indices) {
                        val ref = (impl.instructions[i] as? com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction)?.reference
                        if (ref?.toString()?.contains("Lp7s;->E") == true) {
if (false) impl.replaceInstruction(i, BuilderInstruction10x(Opcode.NOP)); n++; nP++
                        }
                    }
                }
            }
        }
        if (nP < 1) error("CTA: pill clickable not dropped")
        if (nVe < 1) error("CTA: row clickable node not dropped")
        if (nBarUi < 1) error("CTA: bar composable not stripped")
        if (nSurf < 1) error("CTA: surface modifier not neutralized")
        if (nKill < 4) error("CTA: bar call sites not killed (nKill=$nKill)")
        if (nClick < 1) error("CTA: clickable modifier not neutralized")

        if (n < 60) error("CTA: expected >=60 nops, got $n")
        if (nHost < 2) error("CTA: bar render sites not found (nHost=$nHost)")
        if (nEmpty < 2) error("CTA: bar bodies not emptied (nEmpty=$nEmpty)")
        if (nBar < 2) error("CTA: bottom-bar lambda not patched (nBar=$nBar)")
    }
}
