package standardrepo.morphe.patches.untappd.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import standardrepo.morphe.compatibility.Compat
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation


private val INSIDER_SITES = listOf(7999725, 8436028, 8436058, 13737754, 13737761, 11451580, 11451654, 11451721)
private val INSIDER_BUNDLE = "assets/index.android.bundle"

fun app.morphe.patcher.patch.ResourcePatchContext.unlockInsiderBundle() {
    decode(INSIDER_BUNDLE)
    val f = get(INSIDER_BUNDLE, false)
    val bytes = f.readBytes()
    println("INSIDER read=" + bytes.size + " magic=" + (bytes[0].toInt() and 255))
    var n = 0
    var i = 1
    while (i + 9 < bytes.size) {
        if (bytes[i] == 0x37.toByte() && bytes[i + 6] == 0x0F.toByte() &&
            bytes[i + 8] == 0x01.toByte() &&
            ((bytes[i + 4] == 0x88.toByte() && bytes[i + 5] == 0xED.toByte()) ||
             (bytes[i + 1] == 0x01.toByte() && bytes[i + 2] == 0x04.toByte() &&
              bytes[i + 9] == 0x06.toByte() &&
              (bytes[i + 7] == 0x08.toByte() ||
               bytes[i + 7] == 0x0C.toByte())))) {
            val dst = bytes[i + 7]
            bytes[i + 6] = 0x78.toByte()
            bytes[i + 7] = dst
            bytes[i + 8] = 0x78.toByte()
            bytes[i + 9] = dst
            n++
            i += 10
        } else {
            i++
        }
    }
    for (site in INSIDER_SITES) {
    if (site + 5 < bytes.size) {
        val dst = bytes[site + 1]
        val op = bytes[site].toInt()
        bytes[site] = 0x78.toByte()
        bytes[site + 2] = if (op == 55) 0x78.toByte() else dst
        bytes[site + 3] = dst
        n++
        if (op == 54) { bytes[site + 2] = 8.toByte(); bytes[site + 4] = dst }
        if (op == 55) { bytes[site + 4] = 0x78.toByte(); bytes[site + 5] = dst }
    }
}
println("INSIDER bundle patched=" + n)
}

val unlockInsiderBundlePatch = app.morphe.patcher.patch.resourcePatch(
    name = "Unlock Insider (bundle)",
    description = "Forces Insider state inside the Hermes bundle: user.is_supporter + the Insider-only 0.1 rating scale toggle.",
    false,
) {
    compatibleWith(Compat.UNTAPPD)
    execute { unlockInsiderBundle() }
}

fun BytecodePatchContext.unlockInsiderEntitlements() {
    // Grow the declared register count of a method so injected code has room.
    fun growRegisters(impl: Any, extra: Int) {
        try {
            var f: java.lang.reflect.Field? = null
            var k: Class<*>? = impl::class.java
            while (k != null && f == null) {
                try { f = k.getDeclaredField("registerCount") } catch (_: NoSuchFieldException) {}
                k = k.superclass
            }
            if (f == null) {
                for (fld in impl::class.java.declaredFields) {
                    if (fld.type == Int::class.javaPrimitiveType) { f = fld; break }
                }
            }
            f!!.isAccessible = true
            f.setInt(impl, f.getInt(impl) + extra)
        } catch (_: Throwable) {}
    }

    val infos = mutableClassDefByOrNull("Lcom/revenuecat/purchases/EntitlementInfos;")
    val info = mutableClassDefByOrNull("Lcom/revenuecat/purchases/EntitlementInfo;")
    val customerInfo = mutableClassDefByOrNull("Lcom/revenueat/purchases/CustomerInfo;")

    // isActive forced true.
    info?.methods?.firstOrNull { it.name == "isActive" }?.let { m ->
        val impl = m.implementation!!
        val block = """
            const/4 v1, 1
            return v1
        """.trimIndent()
        val orig = impl.instructions.size
        m.removeInstructions(0, orig)
        m.addInstructions(0, block)
    }

    // Fabricate an active `insider` entitlement in get().
    infos?.methods?.firstOrNull { it.name == "get" }?.let { m ->
        val impl = m.implementation!!
        growRegisters(impl, 16)
        val block = """
            const-string v14, "UNTAPPDPATCH"
            const-string v15, "EntitlementInfos.get"
            invoke-static {v14, v15}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
            new-instance v0, Lcom/revenuecat/purchases/EntitlementInfo;
            const-string v1, "insider"
            const/4 v2, 1
            const/4 v3, 1
            sget-object v4, Lcom/revenuecat/purchases/PeriodType;->NORMAL:Lcom/revenuecat/purchases/PeriodType;
            new-instance v5, Ljava/util/Date;
            invoke-direct {v5}, Ljava/util/Date;-><init>()V
            move-object v6, v5
            move-object v7, v5
            sget-object v8, Lcom/revenuecat/purchases/Store;->PLAY_STORE:Lcom/revenuecat/purchases/Store;
            const-string v9, "insider"
            const-string v10, ""
            const/4 v11, 0
            move-object v12, v5
            move-object v13, v5
            sget-object v14, Lcom/revenuecat/purchases/OwnershipType;->PURCHASED:Lcom/revenuecat/purchases/OwnershipType;
            new-instance v15, Lorg/json/JSONObject;
            invoke-direct {v15}, Lorg/json/JSONObject;-><init>()V
            invoke-direct/range {v0 .. v15}, Lcom/revenuecat/purchases/EntitlementInfo;-><init>(Ljava/lang/String;ZZLcom/revenuecat/purchases/PeriodType;Ljava/util/Date;Ljava/util/Date;Ljava/util/Date;Lcom/revenuecat/purchases/Store;Ljava/lang/String;Ljava/lang/String;ZLjava/util/Date;Ljava/util/Date;Lcom/revenuecat/purchases/OwnershipType;Lorg/json/JSONObject;)V
            return-object v0
        """.trimIndent()
        val orig = impl.instructions.size
        m.removeInstructions(0, orig)
        m.addInstructions(0, block)
    }

    // Same in getAll().
    infos?.methods?.firstOrNull { it.name == "getAll" }?.let { m ->
        val impl = m.implementation!!
        growRegisters(impl, 16)
        val block = """
            new-instance v0, Ljava/util/HashMap;
            invoke-direct {v0}, Ljava/util/HashMap;-><init>()V
            new-instance v1, Lcom/revenuecat/purchases/EntitlementInfo;
            const-string v2, "insider"
            const/4 v3, 1
            const/4 v4, 1
            sget-object v5, Lcom/revenuecat/purchases/PeriodType;->NORMAL:Lcom/revenuecat/purchases/PeriodType;
            new-instance v6, Ljava/util/Date;
            invoke-direct {v6}, Ljava/util/Date;-><init>()V
            move-object v7, v6
            move-object v8, v6
            sget-object v9, Lcom/revenuecat/purchases/Store;->PLAY_STORE:Lcom/revenuecat/purchases/Store;
            const-string v10, ""
            const-string v11, ""
            const/4 v12, 0
            move-object v13, v6
            move-object v14, v6
            sget-object v15, Lcom/revenuecat/purchases/OwnershipType;->PURCHASED:Lcom/revenuecat/purchases/OwnershipType;
            new-instance v16, Lorg/json/JSONObject;
            invoke-direct/range {v16 .. v16}, Lorg/json/JSONObject;-><init>()V
            invoke-direct/range {v1 .. v16}, Lcom/revenuecat/purchases/EntitlementInfo;-><init>(Ljava/lang/String;ZZLcom/revenuecat/purchases/PeriodType;Ljava/util/Date;Ljava/util/Date;Ljava/util/Date;Lcom/revenuecat/purchases/Store;Ljava/lang/String;Ljava/lang/String;ZLjava/util/Date;Ljava/util/Date;Lcom/revenuecat/purchases/OwnershipType;Lorg/json/JSONObject;)V
            invoke-interface {v0, v2, v1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
            return-object v0
        """.trimIndent()
        val orig = impl.instructions.size
        m.removeInstructions(0, orig)
        m.addInstructions(0, block)
    }


    // getActiveSubscriptions() -> HashSet containing `insider`.
    customerInfo?.methods?.firstOrNull { it.name == "getActiveSubscriptions" }?.let { m ->
        val impl = m.implementation!!
        val block = """
            const-string v1, "UNTAPPDPATCH"
            const-string v2, "CustomerInfo.getActiveSubscriptions"
            invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
            new-instance v1, Ljava/util/HashSet;
            invoke-direct {v1}, Ljava/util/HashSet;-><init>()V
            const-string v2, "insider"
            invoke-interface {v1, v2}, Ljava/util/Set;->add(Ljava/lang/Object;)Z
            return-object v1
        """.trimIndent()
        val orig = impl.instructions.size
        m.removeInstructions(0, orig)
        m.addInstructions(0, block)
    }

    // Flip the RN API payload flag.
    val net = mutableClassDefByOrNull { c -> c.type == "Lcom/facebook/react/modules/network/NetworkingModule;" && c.methods.any { mm -> mm.name == "readWithProgress" } }
    net?.methods?.firstOrNull { it.name == "readWithProgress" }?.let { m ->
        val impl = m.implementation!!
        val orig = impl.instructions.size; val sb = StringBuilder(); var idx = 0; var n = 0
        while (idx < orig) {
            val insn = impl.instructions[idx]
            if (insn.opcode == com.android.tools.smali.dexlib2.Opcode.CONST_STRING) {
                val ref = (insn as? com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c)?.reference as? com.android.tools.smali.dexlib2.iface.reference.StringReference
                if (ref != null && ref.string == "is_supporter") {
                    sb.append("const-string v10, \"is_supporter\"\nconst/4 v11, 1\n"); n++; idx += 2; continue
                }
            }
            sb.append(insn.toString()); sb.append("\n"); idx++
        }
        if (n > 0) { m.removeInstructions(0, orig); m.addInstructions(0, sb.toString()) }
    }
}

val unlockInsiderEntitlementsPatch = bytecodePatch(
    "Unlock Insider Entitlements",
    "Makes Insider active: RevenueCat entitlements are reported active and the JS supporter flag is forced true.",
    false
) {
    compatibleWith(Compat.UNTAPPD)
    dependsOn(unlockInsiderBundlePatch)
    execute { unlockInsiderEntitlements() }
}
