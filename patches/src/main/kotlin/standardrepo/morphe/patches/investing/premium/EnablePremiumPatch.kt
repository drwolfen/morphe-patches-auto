/**
 * Copyright 2026 crackpipe
 * Auto-generated for com.fusionmedia.investing v6.52 — do not edit by hand.
 */

package standardrepo.morphe.patches.investing.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.util.returnEarly
import standardrepo.morphe.compatibility.Compat
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstructions
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.builder.BuilderInstruction
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction10x
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction20t
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21s
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21t
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11x
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction31c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11n
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22x
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction23x
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22b
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction3rc
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction32x
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21t
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction31t
import com.android.tools.smali.dexlib2.builder.instruction.BuilderPackedSwitchPayload
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

val enablePremiumPatch = bytecodePatch(
    name = "Enable Pro",
    description = "Auto-generated: unlocks Investing Pro — the app's own paywall/tier gates are forced open."
) {
    compatibleWith(Compat.INVESTING)
    dependsOn(pairipManifestBypassPatch)

    execute {
        VerifySignatureMatchesFingerprint.method.returnEarly(true)
        VerifyIntegrityFingerprint.method.returnEarly()
        GetCurrencyIso4217CodeFingerprint.method.returnEarly(true)
        GetRevenueFingerprint.method.returnEarly(null as Void?)
        AFingerprint.method.returnEarly()
        AFingerprintLe51.method.returnEarly()
        StartActivityForResultFingerprint.method.apply {
            val impl = implementation!!
            var rc: java.lang.reflect.Field? = null
            var cls: Class<*>? = impl.javaClass
            while (cls != null && rc == null) {
                try { rc = cls.getDeclaredField("registerCount") } catch (_: NoSuchFieldException) {}
                cls = cls.superclass
            }
            if (rc == null) {
                for (fld in impl.javaClass.declaredFields) {
                    if (fld.type == Int::class.javaPrimitiveType && fld.getInt(impl) == impl.registerCount) {
                        rc = fld; break
                    }
                }
            }
            val rcf = rc!!
            val oldRegs = impl.registerCount
            rcf.isAccessible = true
            rcf.setInt(impl, oldRegs + 8)
            removeInstructions(0, impl.instructions.size)
            addInstructions(0, """
                const-string v2, "crackpipe"
                invoke-virtual {v8}, Landroid/app/Activity;->getClass()Ljava/lang/Class;
                move-result-object v0
                invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getAction()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                if-eqz v0, :pkg
                const-string v1, "play.google.com"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-nez v1, :m1
                goto :skip
                :m1
                const-string v1, "market://"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-nez v1, :m2
                goto :skip
                :m2
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-eqz v1, :pkg
                goto :skip
                :pkg
                invoke-virtual {v9}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v0
                if-eqz v0, :comp
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :comp
                goto :skip
                :comp
                invoke-virtual {v9}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
                move-result-object v0
                if-eqz v0, :orig
                invoke-virtual {v0}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;
                move-result-object v0
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :orig
                goto :skip
                :orig
                const-string v3, "PASSED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-super {v8, v9, v10}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I)V
                return-void
                :skip
                const-string v3, "SWALLOWED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                goto :orig
            """)
        }
        StartActivityForResultFingerprintLgr3.method.apply {
            val impl = implementation!!
            var rc: java.lang.reflect.Field? = null
            var cls: Class<*>? = impl.javaClass
            while (cls != null && rc == null) {
                try { rc = cls.getDeclaredField("registerCount") } catch (_: NoSuchFieldException) {}
                cls = cls.superclass
            }
            if (rc == null) {
                for (fld in impl.javaClass.declaredFields) {
                    if (fld.type == Int::class.javaPrimitiveType && fld.getInt(impl) == impl.registerCount) {
                        rc = fld; break
                    }
                }
            }
            val rcf = rc!!
            val oldRegs = impl.registerCount
            rcf.isAccessible = true
            rcf.setInt(impl, oldRegs + 8)
            removeInstructions(0, impl.instructions.size)
            addInstructions(0, """
                const-string v2, "crackpipe"
                invoke-virtual {v8}, Landroid/app/Activity;->getClass()Ljava/lang/Class;
                move-result-object v0
                invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getAction()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-virtual {v9}, Landroid/content/Intent;->getDataString()Ljava/lang/String;
                move-result-object v0
                invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                invoke-static {v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                if-eqz v0, :pkg
                const-string v1, "play.google.com"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-nez v1, :m1
                goto :skip
                :m1
                const-string v1, "market://"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-nez v1, :m2
                goto :skip
                :m2
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z
                move-result v1
                if-eqz v1, :pkg
                goto :skip
                :pkg
                invoke-virtual {v9}, Landroid/content/Intent;->getPackage()Ljava/lang/String;
                move-result-object v0
                if-eqz v0, :comp
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :comp
                goto :skip
                :comp
                invoke-virtual {v9}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;
                move-result-object v0
                if-eqz v0, :orig
                invoke-virtual {v0}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;
                move-result-object v0
                const-string v1, "com.android.vending"
                invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                move-result v1
                if-eqz v1, :orig
                goto :skip
                :orig
                const-string v3, "PASSED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                invoke-super {v8, v9, v10, v11}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent; I Landroid/os/Bundle;)V
                return-void
                :skip
                const-string v3, "SWALLOWED"
                invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
                return-void
            """)
        }
        // java.io.tmpdir is writable both on desktop JVM and inside the Android patcher.
        val tmpDir = System.getProperty("java.io.tmpdir") ?: "."
        classDefForEach { cd ->
            // Pairip's own classes are stripped too: with the native lib never loaded (VMRunner
            // <clinit> gutted) any surviving `invoke` reaches the native `executeVM` and throws
            // UnsatisfiedLinkError. `VMRunner.invoke` itself is among the callers, so it is nulled.
            val callers = cd.methods.filter { m ->
                m.implementation?.instructions?.any { ins ->
                    ins.opcode.name.startsWith("INVOKE") &&
                        (ins as? ReferenceInstruction)?.reference.let { ref ->
                            ref is MethodReference && ref.definingClass == "Lcom/pairip/VMRunner;" &&
                                ref.name == "invoke"
                        }
                } == true
            }
            if (callers.isEmpty()) return@classDefForEach
            val mc = mutableClassDefByOrNull(cd.type) ?: return@classDefForEach
            for (m in callers) {
                mc.methods.firstOrNull { it.name == m.name && it.returnType == m.returnType }?.returnEarly()
            }
        }
        classDefForEach { cd ->
            for (m in cd.methods.filter { it.name == "<clinit>" }) {
                val ins = m.implementation?.instructions?.toList() ?: continue
                val target = mutableClassDefByOrNull(cd.type)?.methods
                    ?.firstOrNull { it.name == "<clinit>" && it.returnType == m.returnType && it.parameterTypes == m.parameterTypes } ?: continue
                val sites = ArrayList<Int>()
                for ((idx, i) in ins.withIndex()) {
                    val ref = (i as? ReferenceInstruction)?.reference
                    if (ref is MethodReference && ref.definingClass == "Ljava/nio/charset/Charset;" &&
                        ref.name == "forName"
                    ) {
                        val prev = ins.getOrNull(idx - 1) ?: continue
                        if (prev.opcode.name != "sget-object") continue
                        sites.add(idx)
                    }
                }
                // Replace the holder-field read itself: `forName` takes a String, so the read is
                // always a String sget. Substituting a literal needs no branch and no label —
                // dexlib2 label binding inside an insertion is what trips ART's verifier.
                for (idx in sites.asReversed()) {
                    val prev = ins[idx - 1] as OneRegisterInstruction
                    target.replaceInstruction(idx - 1, "const-string v" + prev.registerA + ", \"UTF-8\"")
                }
            }
        }
        // InvestingPro gate: b8h.K/I/L take the user state flow and return the `isPro`
        // boolean; forcing them true unlocks every paywalled screen, including while
        // logged out (the user object is null, so the field read alone is not enough).
        runCatching {
            val bc = mutableClassDefByOrNull("Lb8h;")
            java.io.File(tmpDir + "/site.log").appendText("B8H " + (bc?.methods?.joinToString(";") { it.name + ":" + it.returnType } ?: "NULL") + "\n")
        }
        for (g in listOf("I", "K", "L")) {
            val bm = mutableClassDefByOrNull("Lb8h;")?.methods?.firstOrNull { it.name == g && it.returnType == "Z" }
            runCatching { java.io.File(tmpDir + "/site.log").appendText("B8H m=" + g + " found=" + (bm != null) + "\n") }
            bm?.returnEarly(true)
        }
        // PairIP signature gate: the Manager signs with its own key, so the pinned
        // cert hash never matches. Accept whatever signature is present.
        runCatching {
            mutableClassDefByOrNull("Lcom/pairip/SignatureCheck;")?.methods?.forEach { m ->
                if (m.returnType == "Z" && m.name == "verifySignatureMatches") m.returnEarly(true)
                if (m.name == "verifyIntegrity") m.returnEarly()
            }
            // libpairipcore's JNI_OnLoad derefs a stale pointer: never load it.
            mutableClassDefByOrNull("Lcom/pairip/VMRunner;")?.methods
                ?.forEach { m -> if (m.returnType == "V") m.returnEarly() }
            // PairIP VM entrypoint: loads libpairipcore.so (JNI_OnLoad segfaults).
            for (vn in listOf("Liog;", "La00;", "LaO0;")) {
                val vcd = mutableClassDefByOrNull(vn) ?: continue
                val vm2 = vcd.methods.firstOrNull { it.name == "invoke" && it.parameterTypes.isEmpty() }
                runCatching { java.io.File("/tmp/hdrk.txt").appendText("VM " + vn + " " + (vm2 != null) + "\n") }
                if (vm2 != null) {
                    val vi = vm2.implementation!!
                    vi.replaceInstruction(0, BuilderInstruction11n(Opcode.CONST_4, 0, 0))
                    vi.replaceInstruction(1, BuilderInstruction11x(Opcode.RETURN_OBJECT, 0))
                }
            }
        }
        // DEBUG: dump the news fetch failure stack to logcat (System.out shows in logcat).
        val newsRepo = mutableClassDefByOrNull("Lore;")?.methods?.firstOrNull { it.name == "a" && it.parameterTypes.size == 3 }
        // The news ArticleJsonAdapter throws when a required field is absent; give
        // 'title' (JSON name HEADLINE, Article field d / ctor arg v8) an empty default
        // by neutralising that throw.
        try {
            val na = mutableClassDefByOrNull("Lcom/fusionmedia/investing/feature/news/pager/articles/data/response/ArticlesResponse_ArticleJsonAdapter;")
            for (fm0 in na?.methods?.filter { it.name == "fromJson" } ?: emptyList()) {
                val fm = fm0
                val thisReg = (fm!!.implementation!!.registerCount) - 2
            val n2 = fm?.implementation?.instructions?.toList() ?: emptyList()
            var skipLbl: com.android.tools.smali.dexlib2.builder.Label? = null
            var titleLbl: com.android.tools.smali.dexlib2.builder.Label? = null
            val tAll = n2.indices.filter { n2[it].opcode.name == "throw" }
            runCatching {
                java.io.File(tmpDir + "/site.log").appendText("NATH " + tAll.joinToString(",") { i -> i.toString() + ":" + (maxOf(0, i - 12)..i).map { n2[it].opcode.name }.joinToString("|") } + "\n")
            }
            fun nop() = BuilderInstruction10x(Opcode.NOP)
            val e = { tag: String, x: Throwable -> runCatching { java.io.File(tmpDir + "/site.log").appendText("EERR " + tag + " " + x.toString().take(200) + " @ " + (x.stackTrace.take(4).joinToString(" | ") { it.toString() }) + "\n") } }
            // JSON uses the field name HEADLINE, not the @Json name "title": the
            // "id" option is never present in this JSON, so reuse its slot for
            // HEADLINE and re-point switch key 1 at the title case.
            val live0 = runCatching { fm!!.implementation!!.instructions }.getOrNull() ?: n2
            val swI = live0.indices.firstOrNull { live0[it].opcode.name == "packed-switch" }
            val plOld = live0.firstOrNull { it.opcode.name == "packed-switch-payload" } as? BuilderPackedSwitchPayload
            runCatching {
                val im = na?.methods?.firstOrNull { m -> m.name == "<init>" && m.implementation?.instructions?.any { (it as? ReferenceInstruction)?.reference?.toString() == "id" } == true }?.implementation
                runCatching { java.io.File("/tmp/hdrk.txt").appendText("im=" + (im != null) + " nm=" + (na?.methods?.count { it.name == "<init>" }) + "\n") }
                val ti2 = im?.instructions?.indices?.firstOrNull { a -> im.instructions[a].location.codeAddress == 0x0d } ?: -1
                runCatching { java.io.File("/tmp/hdrk.txt").appendText("ti2=" + ti2 + "\n") }
                if (ti2 > 0) {
                    val r = (im!!.instructions[ti2] as OneRegisterInstruction).registerA
                    im.replaceInstruction(ti2, BuilderInstruction21c(Opcode.CONST_STRING, r, ImmutableStringReference("HEADLINE")))
                }
                val th2 = n2.indexOfFirst { (it as? ReferenceInstruction)?.reference?.let { r -> r is com.android.tools.smali.dexlib2.iface.reference.FieldReference && r.name == "d" && r.type == "Ljava/lang/String;" } == true }
                if (th2 > 0) titleLbl = n2[th2].location.addNewLabel()
            }.onFailure { e("HDRK", it) }
            runCatching {
            if (swI != null && plOld != null) {
                val reg = (live0[swI] as BuilderInstruction31t).registerA
                val owner = HashMap<Int, com.android.tools.smali.dexlib2.builder.MethodLocation>()
                var curA = 0
                for (ins in live0) { val u = ins.codeUnits; for (d in 0 until u) owner[curA + d] = ins.location; curA += u }
                runCatching { java.io.File(tmpDir + "/site.log").appendText("SWT " + plOld.switchElements.joinToString(",") { el -> el.getKey().toString() + "@" + el.getTarget().codeAddress + ">" + ((owner[el.getTarget().codeAddress]?.let { it.codeAddress }) ?: -1) } + "\n") }
                val lbls = plOld.switchElements.map { el -> if (el.getKey() == 1 && titleLbl != null) titleLbl!! else if (el.getKey() == -1 && skipLbl != null) skipLbl!! else (owner[el.getTarget().codeAddress] ?: el.getTarget().location).addNewLabel() }
                    val sz = runCatching { fm!!.implementation!!.instructions.size }.getOrNull() ?: 0
                    val newPl = BuilderPackedSwitchPayload(plOld.switchElements.first().getKey(), lbls)
                    // Append payload + a trailing nop: a payload that is the very last
                    // instruction sits at address == size and the switch check fails.
                    runCatching { fm!!.implementation!!.addInstructions(sz, listOf(BuilderInstruction11x(Opcode.RETURN_OBJECT, 0), newPl, BuilderInstruction11x(Opcode.RETURN_OBJECT, 0))) }.onFailure { e("SWA", it) }
                    val plLbl = runCatching { newPl.location.addNewLabel() }.getOrNull()
                    if (plLbl != null) {
                        runCatching { fm!!.implementation!!.replaceInstruction(swI, BuilderInstruction31t(Opcode.PACKED_SWITCH, reg, plLbl)) }.onFailure { e("SW2", it) }
                    } else e("SWNL", Throwable("no payload label"))
                    runCatching { fm!!.implementation!!.instructions.size }.onFailure { e("FX2", it) }
                    // The old payload is now unreferenced but still sits in the code path,
                    // so falling into its data trips the verifier: nop it out.
                    var oldPlI = -1
                    var cn = 0
                    val szN = runCatching { fm!!.implementation!!.instructions.size }.getOrNull() ?: 0
                    for (k in 0 until szN) {
                        val ins = runCatching { fm!!.implementation!!.instructions[k] }.getOrNull() ?: break
                        if (ins.opcode.name == "packed-switch-payload") { oldPlI = k; cn = ins.codeUnits; break }
                    }
                    if (oldPlI >= 0 && cn > 0) runCatching { fm!!.implementation!!.removeInstructions(oldPlI, 1) }.onFailure { e("SWNO", it) }
                    runCatching { java.io.File(tmpDir + "/site.log").appendText("SWOK s=" + swI + " sz=" + sz + "\n") }
                } else runCatching { java.io.File(tmpDir + "/site.log").appendText("PLNONE\n") }
            }.onFailure { e("PL", it) }
            }
        } catch (t: Throwable) {
            runCatching { java.io.File(tmpDir + "/site.log").appendText("NAERR " + t + " @ " + (t.stackTrace.firstOrNull()?.toString() ?: "-") + "\n") }
        }

        // Dump the news request headers so we can see what the client actually sends.
        try {
            val j67 = mutableClassDefByOrNull("Lj67;")
            val hm = j67?.methods?.firstOrNull { it.name == "a" && it.parameterTypes.size == 1 }
            val hi = hm?.implementation?.instructions?.toList() ?: emptyList()
            val hr = hi.indexOfLast { it.opcode.name == "return-object" }
            runCatching { java.io.File(tmpDir + "/site.log").appendText("HDR c=" + (j67 != null) + " m=" + (hm != null) + " r=" + hr + " n=" + hi.size + "\n") }
            if (hr > 0) hm!!.addInstructions(hr, """
                const-string v1, "CPHDR"
                invoke-virtual {v3}, Ljava/lang/Object;->toString()Ljava/lang/String;
                move-result-object v0
                invoke-static {v1, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
            """)
        } catch (t: Throwable) {
            runCatching { java.io.File(tmpDir + "/site.log").appendText("HDRERR " + t + "\n") }
        }

        // DEBUG: list size + completion in the top-level response adapter.
        runCatching {
            val rCls = mutableClassDefByOrNull("Lcom/fusionmedia/investing/feature/news/pager/articles/data/response/ArticlesResponseJsonAdapter;")
            val rfm = rCls?.methods?.firstOrNull { it.name == "fromJson" }
            val rins = rfm?.implementation?.instructions?.toList()
            val ci = rins?.indexOfFirst { it.opcode.name == "check-cast" && ((it as? ReferenceInstruction)?.reference?.toString()?.contains("List") == true) } ?: -1
            val ri = rins?.indexOfFirst { it.opcode.name == "return-object" } ?: -1
            java.io.File(tmpDir + "/site.log").appendText("RLIST ci=" + ci + " ret=" + ri + " n=" + (rins?.size ?: -1) + "\n")
            if (ci > 0) rfm!!.implementation!!.addInstructions(ci + 1, listOf(
                BuilderInstruction35c(Opcode.INVOKE_INTERFACE, 1, 0, 0, 0, 0, 0, ImmutableMethodReference("Ljava/util/List;", "size", listOf(), "I")),
                BuilderInstruction11x(Opcode.MOVE_RESULT, 2),
                BuilderInstruction35c(Opcode.INVOKE_STATIC, 1, 2, 0, 0, 0, 0, ImmutableMethodReference("Ljava/lang/String;", "valueOf", listOf("I"), "Ljava/lang/String;")),
                BuilderInstruction11x(Opcode.MOVE_RESULT_OBJECT, 2),
                BuilderInstruction21c(Opcode.CONST_STRING, 4, ImmutableStringReference("CPL:")),
                BuilderInstruction35c(Opcode.INVOKE_STATIC, 2, 4, 2, 0, 0, 0, ImmutableMethodReference("Landroid/util/Log;", "d", listOf("Ljava/lang/String;", "Ljava/lang/String;"), "I"))
            ))
            if (ri > 0) rfm!!.implementation!!.addInstructions(ri, listOf(
                BuilderInstruction21c(Opcode.CONST_STRING, 4, ImmutableStringReference("CPE:")),
                BuilderInstruction35c(Opcode.INVOKE_STATIC, 2, 4, 4, 0, 0, 0, ImmutableMethodReference("Landroid/util/Log;", "d", listOf("Ljava/lang/String;", "Ljava/lang/String;"), "I"))
            ))
        }
        if (newsRepo != null) {
            val nins = newsRepo.implementation?.instructions?.toList() ?: emptyList()
            val at = nins.indexOfFirst { it.opcode.name == "iget-object" && ((it as? ReferenceInstruction)?.reference as? FieldReference)?.let { f -> f.name == "a" && f.definingClass.contains("Lh51") } == true }
            val dat = nins.indexOfFirst { it.opcode.name == "iget-object" && (it as? OneRegisterInstruction)?.registerA == 5 && ((it as? ReferenceInstruction)?.reference as? FieldReference)?.let { f -> f.name == "b" && f.definingClass.contains("ArticlesResponse\$Data") } == true }
            runCatching { java.io.File(tmpDir + "/site.log").appendText("LORE at=" + at + " dat=" + dat + "\n") }
            // Log to logcat (kotlin-stdlib's ConsoleKt is stripped from this app).
            // Earlier site first: its two instructions shift the later index by two.
            if (at > 0) newsRepo.addInstructions(at + 1, """
                const-string v7, "CPERR"
                invoke-static {v1}, Landroid/util/Log;->getStackTraceString(Ljava/lang/Throwable;)Ljava/lang/String;
                move-result-object v6
                invoke-static {v7, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
            """)
            if (dat > 0) newsRepo.addInstructions(dat + 5, """
                const-string v7, "CPDB:"
                invoke-static {v7, v5}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
            """)
        }

        mutableClassDefByOrNull("Landroidx/core/content/FileProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Lcom/yandex/mobile/ads/core/initializer/YandexAdsInitializeProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Lcom/facebook/FacebookContentProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Lcom/canhub/cropper/CropFileProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Lcom/google/firebase/provider/FirebaseInitProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Lcom/yandex/mobile/ads/features/debugpanel/data/local/DebugPanelFileProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Lcom/startapp/sdk/adsbase/StartAppInitProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Lio/appmetrica/analytics/internal/PreloadInfoContentProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Landroidx/startup/InitializationProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Lcom/facebook/internal/FacebookInitProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        mutableClassDefByOrNull("Lcom/squareup/picasso/PicassoProvider;")?.methods?.firstOrNull { it.name == "onCreate" && it.returnType == "Z" }?.returnEarly(true)
        val holders = HashSet<String>()
        classDefForEach { cd ->
            if (cd.methods.none { it.name == "<clinit>" } &&
                cd.fields.any { it.name.isNotEmpty() && it.type == "Ljava/lang/String;" }
            ) holders.add(cd.type)
        }
        // Pass 1: classify each holder field by the platform API its reads flow into, so the
        // default is chosen per field (a field used as SQL always gets SQL, a column always a
        // column name), then pass 2 rewrites the reads.
        val kind = HashMap<String, IntArray>()   // 0=svc 1=col 2=url 3=db 4=sql
        classDefForEach { cd ->
            for (m in cd.methods) {
                val ins = m.implementation?.instructions?.toList() ?: continue
                for ((idx, i) in ins.withIndex()) {
                    if (i.opcode.name != "sget-object") continue
                    val ref = (i as? ReferenceInstruction)?.reference as? FieldReference ?: continue
                    if (ref.definingClass !in holders || ref.type != "Ljava/lang/String;") continue
                    val k = kind.getOrPut(ref.definingClass + "#" + ref.name) { IntArray(5) }
                    for (j in idx + 1..idx + 12) {
                        val nx = ins.getOrNull(j) ?: continue
                        if (!nx.opcode.name.startsWith("invoke")) continue
                        val mr = (nx as? ReferenceInstruction)?.reference as? MethodReference ?: continue
                        when {
                            mr.name == "getSystemService" -> k[0]++
                            mr.name.startsWith("getColumnIndex") -> k[1]++
                            mr.name == "parse" -> k[2]++
                            mr.name == "<init>" && mr.definingClass.contains("SQLite") -> k[3]++
                            mr.name == "execSQL" || mr.name == "compileStatement" ||
                                mr.name == "rawQuery" || mr.name == "query" -> k[4]++
                        }
                    }
                }
            }
        }
        // Field names looked up reflectively: `<const-class X>` then `sget holder`, then
        // `getDeclaredField` + `sput` — the holder string must name an existing field of X.
        val reflect = HashMap<String, String>()
        classDefForEach { cd ->
            for (m in cd.methods) {
                val ins = m.implementation?.instructions?.toList() ?: continue
                for ((idx, i) in ins.withIndex()) {
                    if (i.opcode.name != "sget-object") continue
                    val ref = (i as? ReferenceInstruction)?.reference as? FieldReference ?: continue
                    if (ref.definingClass !in holders || ref.type != "Ljava/lang/String;") continue
                    val key = ref.definingClass + "#" + ref.name
                    if (reflect.containsKey(key)) continue
                    var cls = ""
                    for (j in idx - 1 downTo idx - 4) {
                        val c = ins.getOrNull(j) as? ReferenceInstruction ?: continue
                        if (c.opcode.name == "const-class") cls = (c.reference as? TypeReference)?.type ?: ""
                    }
                    if (cls.isEmpty()) continue
                    val owner = mutableClassDefByOrNull(cls) ?: continue
                    if (owner.fields.isEmpty()) continue
                    // `Unsafe.objectFieldOffset` needs an instance field; the offset itself is
                    // cached in a static one. Kotlin state machines expose `state$volatile`, so
                    // prefer the synthetic instance field, else the first instance field.
                    val inst = owner.fields.filter { (it.accessFlags and 0x8) == 0 }
                    var pick = owner.fields.firstOrNull { it.name.contains("$") || it.name.contains("state") }?.name ?: ""
                    if (inst.none { it.name == pick }) {
                        pick = inst.firstOrNull { it.type.startsWith("L") || it.type.startsWith("[") }?.name ?: inst.firstOrNull()?.name ?: ""
                    }
                    reflect[key] = pick
                }
            }
        }
        // Methods that hand a String on to a statement/query wrapper forward SQL text.
        val sqlFwd = HashSet<String>()
        classDefForEach { cd ->
            for (m in cd.methods) {
                val ins = m.implementation?.instructions?.toList() ?: continue
                for (i in ins) {
                    if (!i.opcode.name.startsWith("invoke")) continue
                    val mr = (i as? ReferenceInstruction)?.reference as? MethodReference ?: continue
                    if (mr.parameterTypes.contains("Ljava/lang/String;") &&
                        (mr.returnType == "Ls9a;" || mr.returnType == "Lxmo;" ||
                            mr.returnType == "Lymo;" || mr.returnType == "Lwmo;" || mr.returnType == "Lqmo;")
                    ) sqlFwd.add(cd.type + "#" + m.name + "#" + m.parameterTypes.joinToString(","))
                }
            }
        }
        val serviceNames = mapOf(
            "Landroid/view/WindowManager;" to "window",
            "Landroid/view/LayoutInflater;" to "layout_inflater",
            "Landroid/net/ConnectivityManager;" to "connectivity",
            "Landroid/net/wifi/WifiManager;" to "wifi",
            "Landroid/telephony/TelephonyManager;" to "phone",
            "Landroid/location/LocationManager;" to "location",
            "Landroid/media/AudioManager;" to "audio",
            "Landroid/app/NotificationManager;" to "notification",
            "Landroid/os/Vibrator;" to "vibrator",
            "Landroid/hardware/SensorManager;" to "sensor",
            "Landroid/app/ActivityManager;" to "activity",
            "Landroid/app/KeyguardManager;" to "keyguard",
            "Landroid/os/PowerManager;" to "power"
        )
        classDefForEach { cd ->
            for (m in cd.methods) {
                val ins = m.implementation?.instructions?.toList() ?: continue
                val target = mutableClassDefByOrNull(cd.type)?.methods
                    ?.firstOrNull {
                        it.name == m.name && it.returnType == m.returnType &&
                            it.parameterTypes == m.parameterTypes
                    } ?: continue
                val hits = ArrayList<Pair<Int, String>>()
                for ((idx, i) in ins.withIndex()) {
                    if (i.opcode.name != "sget-object") continue
                    val ref = (i as? ReferenceInstruction)?.reference as? FieldReference ?: continue
                    if (ref.definingClass !in holders || ref.type != "Ljava/lang/String;") continue
                    val k = kind[ref.definingClass + "#" + ref.name] ?: continue
                    hits.add(idx to when {
                        k[0] > 0 -> "svc"
                        k[2] > 0 -> "url"
                        k[3] > 0 -> "db"
                        k[1] > 0 -> "col"
                        k[4] > 0 -> "sql"
                        else -> ""
                    })
                }
                // A method that forwards a String into a statement wrapper treats its holder
                // string reads as SQL text (the register may travel several frames).
                val methodIsSql = sqlFwd.contains(cd.type + "#" + m.name + "#" + m.parameterTypes.joinToString(","))
                for ((idx, k) in hits.asReversed()) {
                    val r = (ins[idx] as OneRegisterInstruction).registerA
                    // Per-site evidence beats the field-level guess: follow the register forward
                    // and see which API actually consumes it (a field can serve both SQL and a
                    // column name; only the call site disambiguates).
                    var column = false
                    // A wrapper class's own <clinit> holds SQL text; a forward scan misses it
                    // because the value is cached in a static field first.
                    var sql = methodIsSql || (m.name == "<clinit>" && cd.type in setOf("Ls9a;", "Lxmo;", "Lymo;", "Lwmo;", "Lqmo;"))
                    var cast = ""
                    if (cd.type == "Lvcd;" && m.name == "d") runCatching { java.io.File(tmpDir + "/dbg.log").appendText("R " + r + " at " + idx + "\n") }
                    for (j in idx + 1..idx + 120) {
                        val nx = ins.getOrNull(j) ?: continue
                        if (!nx.opcode.name.startsWith("invoke")) continue
                        val mr = (nx as? ReferenceInstruction)?.reference as? MethodReference ?: continue
                        val regs = when (nx) {
                            is com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction ->
                                listOf(nx.registerC, nx.registerD, nx.registerE, nx.registerF, nx.registerG).take(nx.registerCount)
                            is com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction ->
                                (nx.startRegister until nx.startRegister + nx.registerCount).toList()
                            else -> emptyList()
                        }
                        if (cd.type == "Lvcd;" && m.name == "d") runCatching { java.io.File(tmpDir + "/dbg.log").appendText("  call " + mr.name + " " + mr.definingClass + " regs=" + regs + " params=" + mr.parameterTypes + " ret=" + mr.returnType + "\n") }
                        if (r !in regs) continue
                        if (mr.name.startsWith("getColumnIndex")) column = true
                        // Obfuscated DAO helpers take exactly one String (the SQL) and pass it on.
                        // Obfuscated DAO helpers take (db, sql) and return void.
                        if (mr.parameterTypes.contains("Ljava/lang/String;") && mr.returnType == "V" &&
                            mr.name != "parse" && mr.name != "equals" && mr.name != "startsWith" && mr.name != "contains"
                        ) sql = true
                        // Room-style DAO wrappers return a statement/query wrapper object.
                        if (mr.returnType == "Ls9a;" || mr.returnType == "Lxmo;" ||
                            mr.returnType == "Lymo;" || mr.returnType == "Lwmo;" ||
                            mr.returnType == "Lqmo;") sql = true
                        if (sqlFwd.contains(mr.definingClass + "#" + mr.name + "#" + mr.parameterTypes.joinToString(","))) sql = true
                        if (mr.name == "execSQL" || mr.name == "compileStatement" ||
                            mr.name == "rawQuery" || mr.name == "query") sql = true
                        // SQL is also passed through app wrappers whose ctor takes (db, sql).
                        if (mr.name == "<init>" && !mr.definingClass.contains("SQLite") &&
                            mr.definingClass != "Ljava/net/URL;" && mr.definingClass != "Landroid/net/Uri;" &&
                            mr.parameterTypes.size > 1 && mr.parameterTypes.contains("Ljava/lang/String;")
                        ) sql = true
                        if (mr.name == "getSystemService") {
                            for (q in j + 1..j + 6) {
                                val c = ins.getOrNull(q) as? ReferenceInstruction
                                if (c?.opcode?.name == "check-cast") cast = (c.reference as? TypeReference)?.type ?: ""
                            }
                        }
                    }
                    if (cd.type == "Lvcd;" && m.name == "d") runCatching { java.io.File(tmpDir + "/dbg.log").appendText("FLAGS col=" + column + " sql=" + sql + " cast=" + cast + " k=" + k + "\n") }
                    val reflectName = (ins[idx] as? ReferenceInstruction)?.let {
                        (it.reference as? FieldReference)?.let { f -> reflect[f.definingClass + "#" + f.name] }
                    } ?: ""
                    var pattern = false
                    for (q in idx + 1..idx + 4) {
                        val rf = (ins.getOrNull(q) as? ReferenceInstruction)?.reference?.toString() ?: ""
                        if (idx == 15 && cd.type == "Lue1;") runCatching { java.io.File("/tmp/pat.txt").appendText("NEXT " + q + " " + rf + "\n") }
                        if (rf.contains("Ls35;->a")) pattern = true
                    }
                    runCatching { java.io.File("/tmp/pat.txt").appendText(cd.type + " " + m.name + " idx=" + idx + " pat=" + pattern + "\n") }
                    val value = when {
                        pattern -> "yyyy-MM-dd'T'HH:mm:ss"
                        reflectName.isNotEmpty() -> reflectName
                        cast.isNotEmpty() -> serviceNames[cast] ?: "window"
                        column -> "id"
                        k == "url" -> "https://example.com"
                        sql -> "SELECT 1"
                        k == "db" -> "app.db"
                        else -> "id"
                    }
                    runCatching { java.io.File(tmpDir + "/site.log").appendText(cd.type + " " + m.name + " " + value + " " +
                        (ins[idx] as? ReferenceInstruction)?.let { ((it.reference as? FieldReference)?.definingClass ?: "?") + "#" + ((it.reference as? FieldReference)?.name ?: "?") } + "\n") }
                    target.replaceInstruction(idx, "const-string v" + r + ", \"" + value + "\"")
                }
            }
        }
    }
}
