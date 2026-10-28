/*
 * Copyright 2026 crackpipe
 *
 * Stage-5 verification gate: applies this bundle's own compiled patches to the
 * target APK with the real Morphe Patcher on the JVM (no device, no manager),
 * then asserts (a) every applicable patch executed without exception and
 * (b) at least one dex file actually changed.
 *
 * Rationale: a patch whose fingerprint misses raises PatchException out of
 * Fingerprint.method, and a patch that mutates nothing produces byte-identical
 * dex. Both are silent failures in production, so both are hard gate failures
 * here. "Passes in our gate" == "works in Morphe".
 *
 * Run via:
 *   ./gradlew :patches:verifyPatch -PtargetApk=<apk> \
 *       -PexpectedPackage=<pkg> -PexpectedVersion=<ver>
 */
package standardrepo.morphe.gate

import app.morphe.patcher.Patcher
import app.morphe.patcher.PatcherConfig
import app.morphe.patcher.patch.Patch
import app.morphe.patcher.patch.loadPatchesFromJar
import app.morphe.patcher.patch.loadPatchesFromDex
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.reflect.Modifier
import java.net.URLClassLoader
import java.nio.ByteBuffer
import java.util.jar.JarFile
import java.util.zip.ZipFile
import kotlin.system.exitProcess

private val DEX_RE = Regex("""classes\d*\.dex""")

/** Every class descriptor carried by the bundle's `classes*.dex` entries — the exact set the
 * Manager enumerates through `loadPatchesFromDex` (DexReadWrite.extractDexEntries -> dexlib2). */
private fun dexClassDescriptors(mpp: File): Set<String> {
    val out = mutableSetOf<String>()
    ZipFile(mpp).use { z ->
        z.entries().toList()
            .filter { it.name.startsWith("classes") && it.name.endsWith(".dex") }
            .forEach { entry ->
                val bytes = z.getInputStream(entry).readBytes()
                DexBackedDexFile(null, ByteBuffer.wrap(bytes)).classes.forEach { out += it.type }
            }
    }
    return out
}

/** Class names in the bundle JAR that declare a patch exactly the way PatchLoader.Jar scans:
 * a static field or a zero-arg static method whose type is [Patch]. */
private fun patchDeclaringClasses(mpp: File): List<String> {
    val names = JarFile(mpp).use { jar ->
        jar.entries().toList()
            .filter { it.name.endsWith(".class") && !it.name.startsWith("META-INF/versions/") }
            .map { it.name.removeSuffix(".class").replace('/', '.') }
    }
    // The bundle is not on this task's classpath (PatchLoader builds its own loader), so make one
    // that can see the .mpp, parented on the loader that has the patcher/library classes.
    URLClassLoader(arrayOf(mpp.toURI().toURL()), Patch::class.java.classLoader).use { loader ->
        return names.filter { name ->
            runCatching {
                val c = Class.forName(name, false, loader)
                c.declaredFields.any { f ->
                    Modifier.isStatic(f.modifiers) && Patch::class.java.isAssignableFrom(f.type)
                } || c.declaredMethods.any { m ->
                    Modifier.isStatic(m.modifiers) && m.parameterCount == 0 &&
                        Patch::class.java.isAssignableFrom(m.returnType)
                }
            }.getOrDefault(false)
        }
    }
}

/** name -> raw bytes for every dex entry of [apk]. */
private fun dexDigests(apk: File): Map<String, ByteArray> {
    val out = LinkedHashMap<String, ByteArray>()
    ZipFile(apk).use { z ->
        val entries = z.entries()
        while (entries.hasMoreElements()) {
            val e = entries.nextElement()
            if (DEX_RE.matches(e.name)) out[e.name] = z.getInputStream(e).readBytes()
        }
    }
    return out
}

fun main(args: Array<String>) {
    require(args.size >= 4) { "usage: verifyPatch <apk> <libsDir> <expectedPackage> <expectedVersion> [expectedAppName]" }
    val apk = File(args[0])
    val libsDir = File(args[1])
    val expectedPackage = args[2]
    val expectedVersion = args[3]
    // Optional: the human app name the Compat must declare for the target package. Morphe labels
    // the app card in a source with this name and matches the source's app list by it, so a wrong
    // name (e.g. "Com" instead of "Duolingo") hides the patch from the user.
    val expectedAppName = args.getOrNull(4)?.takeUnless { it.isBlank() }
    require(apk.isFile) { "GATE FAIL apk not found: ${apk.absolutePath}" }

    val mpp = libsDir.listFiles { f ->
        f.isFile && f.name.endsWith(".mpp") && !f.name.contains("javadoc") && !f.name.contains("sources")
    }?.minByOrNull { it.name }
    if (mpp == null) {
        println("GATE FAIL no .mpp bundle in ${libsDir.absolutePath}")
        exitProcess(4)
    }

    println("GATE start apk=${apk.name} bundle=${mpp.name} target=$expectedPackage@$expectedVersion")

    // Manager parity gate: Manager loads patches with `loadPatchesFromDex`, whose dex extractor
    // (DexReadWrite.extractDexEntries) keeps ONLY zip entries whose name starts with `classes`
    // and ends with `.dex`. A .mpp carrying just `.class` files (the bare `jar` output, i.e.
    // before the plugin's D8 `buildAndroid` merge) makes the Manager list 0 patches and mark the
    // source failed (the "!" badge). `generated/.../signature.dex` does NOT count — it does not
    // start with `classes`. Assert the dex here so a stripped bundle never ships again.
    val dexEntryNames = ZipFile(mpp).use { z ->
        z.entries().toList().map { it.name }
            .filter { it.startsWith("classes") && it.endsWith(".dex") }
    }
    println("GATE bundle dex entries=${dexEntryNames.size} ${dexEntryNames}")
    if (dexEntryNames.isEmpty()) {
        println("GATE FAIL bundle has no classes*.dex — Manager's loadPatchesFromDex finds no patches")
        exitProcess(6)
    }

    val loaded = loadPatchesFromJar(setOf(mpp))

    // Stronger parity: the Manager's own loader, on the same artifact. Runs when android-all is
    // on this task's classpath (DexClassLoader is a dalvik class); a JVM-only quirk is reported
    // but does not fail the gate.
    val dexLoadedCount = runCatching { loadPatchesFromDex(setOf(mpp)).size }
    dexLoadedCount.onSuccess { n ->
        println("GATE dex-loader (Manager path) patches=$n")
        if (n != loaded.size) {
            println("GATE FAIL dex loader sees $n patches, jar loader sees ${loaded.size}")
            exitProcess(7)
        }
    }.onFailure { e ->
        println("GATE dex-loader (Manager path) unavailable: ${e::class.simpleName}: ${e.message?.lineSequence()?.firstOrNull()}")
    }

    // Parity: every class in the bundle JAR that declares a patch (static Patch field or
    // zero-arg static method returning Patch — exactly what PatchLoader.Jar scans) must exist
    // as a class in the bundle's `classes*.dex`. The Manager enumerates dex classes only, so a
    // patch present as .class but absent from the dex is invisible on Android -> 0 patches.
    val patchDeclarers = patchDeclaringClasses(mpp)
    val dexClasses = dexClassDescriptors(mpp)
    val missingFromDex = patchDeclarers.filter { name ->
        ("L" + name.replace('.', '/') + ";") !in dexClasses
    }
    println("GATE patch-declaring classes=${patchDeclarers.size} missingFromDex=${missingFromDex.size}")
    if (missingFromDex.isNotEmpty()) {
        println("GATE FAIL patch classes absent from classes.dex: $missingFromDex")
        exitProcess(8)
    }

    // Manager parity: PatchInfo.compatibleWith() returns TRUE whenever compatiblePackages == null,
    // i.e. a patch that declares no compatibility is UNIVERSAL and applies to every package. The
    // Manager's PatchBundleInfo.listedApps() likewise names no app for a universal patch. The gate
    // MUST model that exactly, else it green-lights a bundle the Manager then fails to run.
    val applicable = loaded.filter { p ->
        val compat = p.compatibility
        if (compat.isNullOrEmpty()) true
        else compat.any { c ->
            c.packageName == expectedPackage &&
                (c.targets.isEmpty() || c.targets.any { t -> t.version == null || t.version == expectedVersion })
        }
    }
    println("GATE bundle patches=${loaded.size} applicable=${applicable.size}")
    if (applicable.isEmpty()) {
        println("GATE FAIL no patch in bundle declares $expectedPackage — want 1 app patch")
        exitProcess(5)
    }

    // A patch may carry ONE companion patch (a resource patch: manifest bypass, asset swap) that
    // the main patch `dependsOn`s — `dependsOn` applies it regardless of its `default` flag. The
    // companion is scoped to the same app but MUST stay default=false, so exactly ONE patch is
    // what the Manager offers. Allow 1..2 applicable patches under that rule.
    println("GATE applicable patches=${applicable.map { p -> "${p.name}${if (p.default) "" else " (default=false)"}" }}")
    val offered = applicable.filter { it.default }
    if (offered.size != 1 || applicable.size > 2) {
        println("GATE FAIL ${applicable.size} applicable patches — want 1 main (+1 companion, default=false)")
        exitProcess(11)
    }
    if (applicable.size == 2 && applicable.count { !it.default } != 1) {
        println("GATE FAIL companion patch not declared default=false")
        exitProcess(11)
    }

    // Manager parity: the Manager titles the app card in a source with this Compatibility `name`
    // (SourceManagementDialogs.patchesByApp -> title = displayName ?: packageName) and searches the
    // source's app list on it, so a wrong name hides the patch from the user. Assert it is the app
    // name, not the vendor/TLD segment ("Com" for com.duolingo, "Urbandroid" for com.x.sleep).
    val declaredAppNames = loaded.flatMap { it.compatibility.orEmpty() }
        .filter { c -> c.packageName == expectedPackage }
        .mapNotNull { it.name?.takeUnless(String::isBlank) }
        .distinct()
    println("GATE Compat app names for $expectedPackage=${expectedAppName ?: ""}=$declaredAppNames")
    if (expectedAppName != null && expectedAppName.lowercase() !in declaredAppNames.map { it.lowercase() }) {
        println("GATE FAIL Compat name for $expectedPackage is $declaredAppNames, expected \"$expectedAppName\"")
        exitProcess(9)
    }

    val original = dexDigests(apk)
    println("GATE input dex files=${original.size}")

    val workspace = libsDir.parentFile.resolve("gate-tmp").apply { mkdirs() }
    val failures = mutableListOf<String>()
    val patched = LinkedHashMap<String, ByteArray>()

    Patcher(PatcherConfig(apkFile = apk, temporaryFilesPath = workspace)).use { patcher ->
        patcher += applicable.toSet()
        val results = runBlocking { patcher().toList() }
        for (r in results) {
            val ex = r.exception
            val label = r.patch.name ?: r.patch::class.simpleName
            if (ex != null) {
                failures += "$label -> ${ex.message?.lineSequence()?.firstOrNull() ?: ex::class.simpleName}"
                println("GATE trace $label:\n" + ex.stackTraceToString().take(4000))
            } else {
                println("GATE patch ok: $label")
            }
        }
        for (d in patcher.get().dexFiles) patched[d.name] = d.stream.readBytes()
    }

    if (failures.isNotEmpty()) {
        println("GATE FAIL ${failures.size} applicable patch(es) raised (fingerprint miss or mutation error):")
        failures.forEach { println("  - $it") }
        exitProcess(2)
    }

    val changed = patched.count { (name, bytes) -> original[name]?.contentEquals(bytes) != true }
    println("GATE output dex files=${patched.size} changed=$changed")
    if (changed == 0) {
        println("GATE FAIL patches ran cleanly but no dex byte changed (no-op patch)")
        exitProcess(3)
    }

    // Structural gate: every mutated method must still END in return/throw/goto. dexlib2 drops
    // the tail silently when a smali invoke lists more than the 5 registers a non-range invoke
    // can encode — the body then "flows through to end of code area" and ART rejects the class
    // with a VerifyError at load, which only shows up ON DEVICE. The JVM gate has no verifier.
    // dexlib2's Opcode.name is the lowercase smali mnemonic ("return-void"), not an enum label.
    // Payload pseudo-instructions are not control flow and legitimately trail a switch/fill.
    val terminators = setOf("return-void", "return-void-barrier", "return", "return-wide",
                            "return-object", "throw", "goto", "goto/16", "goto/32",
                            "packed-switch-payload", "sparse-switch-payload",
                            "array-payload")
    val dangling = mutableListOf<String>()
    for ((name, bytes) in patched) {
        if (original[name]?.contentEquals(bytes) == true) continue
        val df = DexBackedDexFile(null, ByteBuffer.wrap(bytes))
        for (cls in df.classes) for (meth in cls.methods) {
            val impl = meth.implementation ?: continue
            val it2 = impl.instructions.iterator()
            var last: com.android.tools.smali.dexlib2.iface.instruction.Instruction? = null
            while (it2.hasNext()) last = it2.next()
            if (last == null || last.opcode.name !in terminators) {
                dangling += "${meth.definingClass}->${meth.name}"
            }
        }
    }
    println("GATE structure: dangling methods=${dangling.size} ${dangling.take(5)}")
    if (dangling.isNotEmpty()) {
        println("GATE FAIL method body does not end in return/throw/goto: ${dangling.take(10)}")
        exitProcess(10)
    }

    println("GATE PASS $changed dex file(s) mutated, ${applicable.size} patch(es) clean")
}
