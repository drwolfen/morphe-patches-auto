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
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
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
                return-void
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
        InvokeSuspendFingerprint.method.addInstructions(1021, """
            sget-object v19, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
            return-object v19
            """)
        InvokeSuspendFingerprint.method.addInstructions(246, """
            sget-object v19, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
            return-object v19
            """)
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
                        k[1] > 0 && k[4] == 0 -> "col"
                        k[4] > 0 -> "sql"
                        else -> ""
                    })
                }
                for ((idx, k) in hits.asReversed()) {
                    val r = (ins[idx] as OneRegisterInstruction).registerA
                    var value = "SELECT 1"
                    when (k) {
                        "svc" -> {
                            var cast = ""
                            for (j in idx + 1..idx + 12) {
                                val c = ins.getOrNull(j) as? ReferenceInstruction ?: continue
                                if (c.opcode.name == "check-cast") cast = (c.reference as? TypeReference)?.type ?: ""
                            }
                            value = serviceNames[cast] ?: "window"
                        }
                        "col" -> value = "id"
                        "url" -> value = "https://example.com"
                        "db" -> value = "app.db"
                    }
                    target.replaceInstruction(idx, "const-string v" + r + ", \"" + value + "\"")
                }
            }
        }
    }
}
