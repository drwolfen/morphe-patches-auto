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
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22c
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21t
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.Opcode
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
        // DEBUG: dump the news fetch failure stack to logcat (System.out shows in logcat).
        val newsRepo = mutableClassDefByOrNull("Lore;")?.methods?.firstOrNull { it.name == "a" && it.parameterTypes.size == 3 }
        // The news ArticleJsonAdapter throws when a required field is absent; give
        // 'title' (JSON name HEADLINE, Article field d / ctor arg v8) an empty default
        // by neutralising that throw.
        try {
            val na = mutableClassDefByOrNull("Lcom/fusionmedia/investing/feature/news/pager/articles/data/response/ArticlesResponse_ArticleJsonAdapter;")
            val fm = na?.methods?.firstOrNull { it.name == "fromJson" }
            val n2 = fm?.implementation?.instructions?.toList() ?: emptyList()
            val tAll = n2.indices.filter { n2[it].opcode.name == "throw" }
            runCatching {
                java.io.File(tmpDir + "/site.log").appendText("NATH " + tAll.joinToString(",") { i -> i.toString() + ":" + (maxOf(0, i - 12)..i).map { n2[it].opcode.name }.joinToString("|") } + "\n")
            }
            val nop = BuilderInstruction10x(Opcode.NOP)
            val e = { tag: String, x: Throwable -> runCatching { java.io.File(tmpDir + "/site.log").appendText("EERR " + tag + " " + x.toString().take(200) + " @ " + (x.stackTrace.firstOrNull()?.toString() ?: "-") + "\n") } }
            val ti = tAll.firstOrNull { i ->
                i > 8 && n2[i - 1].opcode.name == "move-result-object" &&
                    n2[i - 8].opcode.name == "check-cast" && (n2[i - 8] as? OneRegisterInstruction)?.registerA == 6
            }
            runCatching { java.io.File(tmpDir + "/site.log").appendText("NA ti=" + ti + "\n") }
            // There are two validation rounds (l then f), each throwing for title.
            val tis = n2.indices.filter { i ->
                i > 8 && n2[i].opcode.name == "throw" && n2[i - 1].opcode.name == "move-result-object" &&
                    n2[i - 2].opcode.name == "invoke-static" &&
                    (n2[i - 2] as? Instruction35c)?.let { it.registerC == 15 && it.registerD == 13 } == true
            }
            runCatching { java.io.File(tmpDir + "/site.log").appendText("T21 " + n2.indices.filter { (n2[it] as? Instruction21t)?.registerA == 16 }.map { it.toString() + ":" + (n2[it] as? Instruction21t)?.codeOffset + ":" + n2[it].opcode.name }.take(6).joinToString(",") + "\n") }
            val i149 = n2.indices.firstOrNull { (n2[it] as? Instruction21t)?.let { i2 -> n2[it].opcode.name == "if-eqz" && i2.registerA == 16 && (n2[it] as? OffsetInstruction)?.codeOffset == 0x4a } == true }
            runCatching { java.io.File(tmpDir + "/site.log").appendText("TIS " + tis.size + " i149=" + i149 + "\n") }
            if (i149 != null) runCatching { fm!!.implementation!!.addInstructions(i149, listOf(BuilderInstruction21c(Opcode.CHECK_CAST, 16, ImmutableTypeReference("Ljava/lang/Long;")))) }.onFailure { e("C0", it) }
            for ((tOld, t) in tis.zip(if (i149 != null) tis.map { if (it >= i149) it + 2 else it } else tis).asReversed()) {
                runCatching { java.io.File(tmpDir + "/site.log").appendText("TK t=" + t + " c2=" + n2[t - 2].opcode.name + " c8=" + (if (t > 8) n2[t - 8].opcode.name else "-") + " regA8=" + (if (t > 8) ((n2[t - 8] as? OneRegisterInstruction)?.registerA?.toString() ?: "-") else "-") + "\n") }
                // Same-size replacements keep the code size, so the switch payload
                // offsets (which dexlib does not re-point) stay valid. In the loop the
                // value slot is v8; after the constructor the name register v7 is the
                // one the verifier expects to be a String.
                val inLoop = tOld > 8 && n2[tOld - 8].opcode.name == "check-cast"
                val beforeSz = n2.size
                // The first field slot v16 is seeded from the const/4 zero at 0x0a and
                // verifies as Object, which the longValue() call at the exit rejects.
                // Seed it as a wide zero instead so the slot is Long-typed from the start.
                val i16 = n2.indices.firstOrNull { runCatching { n2[it].location?.codeAddress }.getOrNull() == 0x1f }
                if (false) runCatching { fm!!.replaceInstruction(i16!!, "const-wide/32 v16, 0") }.onFailure { e("W0", it) }
                if (inLoop) {
                    // Hop to the shared tail (0x120 -> 0x89 -> 0x13): the tail re-enters
                    // the loop head, which re-igets the adapters, and v7/v8 stay untouched.
                    // Numeric operands are not supported by the inline compiler, so the
                    // target has to be a Label.
                    val loopIdx = n2.indices.firstOrNull { runCatching { n2[it].location?.codeAddress }.getOrNull() == 0x120 } ?: -1
                    val lbl = runCatching { (n2.getOrNull(loopIdx) as? BuilderInstruction)?.location?.labels?.firstOrNull() }.getOrNull() ?: (if (loopIdx >= 0) fm!!.implementation!!.newLabelForIndex(loopIdx) else null)
                    if (lbl != null) {
                        runCatching { fm!!.implementation!!.replaceInstruction(t - 2, nop) }.onFailure { e("L0c", it) }
                        runCatching { fm!!.implementation!!.replaceInstruction(t - 1, nop) }.onFailure { e("L0", it) }
                        runCatching { fm!!.implementation!!.replaceInstruction(t, BuilderInstruction20t(Opcode.GOTO_16, lbl)) }.onFailure { e("L1", it) }
                    }
                } else {
                    // Validation block: its throw becomes a fall-through. The title block
                    // must hand the next (id) block its own name constants, otherwise the
                    // merge sees the adapter types and reports a Conflict.
                    val isTitle = n2[tOld - 2].let { (it as? Instruction35c)?.let { i2 -> i2.registerC == 15 && i2.registerD == 13 } == true }
                    if (isTitle) {
                        runCatching { fm!!.implementation!!.replaceInstruction(t - 2, BuilderInstruction21c(Opcode.CONST_STRING, 8, ImmutableStringReference("id"))) }.onFailure { e("E1", it) }
                        runCatching { fm!!.implementation!!.replaceInstruction(t - 1, BuilderInstruction21c(Opcode.CONST_STRING, 7, ImmutableStringReference("news_ID"))) }.onFailure { e("E2", it) }
                        runCatching { fm!!.implementation!!.replaceInstruction(t, nop) }.onFailure { e("E3", it) }
                    } else {
                        // Last block: three nops keep the six-unit block size.
                        runCatching { fm!!.implementation!!.replaceInstructions(t - 2, listOf(nop, nop, nop)) }.onFailure { e("E4", it) }
                        runCatching { fm!!.implementation!!.replaceInstruction(t - 1, nop) }.onFailure { e("E5", it) }
                        runCatching { fm!!.implementation!!.replaceInstruction(t, nop) }.onFailure { e("E6", it) }
                    }
                }
            }
        } catch (t: Throwable) {
            runCatching { java.io.File(tmpDir + "/site.log").appendText("NAERR " + t + "\n") }
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
                    val value = when {
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
