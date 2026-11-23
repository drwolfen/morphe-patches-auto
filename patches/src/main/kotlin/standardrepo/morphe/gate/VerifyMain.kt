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

    // Any number of applicable patches may be default=true (the Manager preselects each one and
    // the user can toggle it); at least one must be, so the Manager always offers a default.
    println("GATE applicable patches=${applicable.map { p -> "${p.name}${if (p.default) "" else " (default=false)"}" }}")
    if (applicable.none { it.default }) {
        println("GATE FAIL no applicable patch is default=true")
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
    var patchedResourcesApk: File? = null
    var patchedOtherResources: File? = null

    // Resource patches only run when the config opts into resource decoding; the default is NONE,
    // which silently skips every resourcePatch (their execute() never fires). The setter is
    // `internal` (mangled), so reach it reflectively.
    val config = PatcherConfig(apkFile = apk, temporaryFilesPath = workspace)
    runCatching {
        val m = PatcherConfig::class.java.getDeclaredMethod(
            "setResourceMode\$morphe_patcher",
            app.morphe.patcher.resource.ResourceMode::class.java,
        )
        m.isAccessible = true
        m.invoke(config, app.morphe.patcher.resource.ResourceMode.FULL)
    }.onFailure { println("GATE resource mode unavailable: ${it::class.simpleName}: ${it.message}") }
    println("resourceMode=FULL requested")

    Patcher(config).use { patcher ->
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
        val result = patcher.get()
        for (d in result.dexFiles) patched[d.name] = d.stream.readBytes()
        result.resources?.let { res ->
            patchedResourcesApk = res.resourcesApk
            patchedOtherResources = res.otherResources
        }
    }

    if (failures.isNotEmpty()) {
        println("GATE FAIL ${failures.size} applicable patch(es) raised (fingerprint miss or mutation error):")
        failures.forEach { println("  - $it") }
        exitProcess(2)
    }

    // Resource parity: a resource patch that executes but writes nothing is a silent no-op in the
    // shipped APK (bundle bytes identical). Assert the patcher emitted a patched resources APK and
    // that the RN bundle inside it actually differs from the input.
    if (applicable.any { it is app.morphe.patcher.patch.ResourcePatch }) {
        val resApk = patchedResourcesApk
        println("GATE resourcesApk=" + (resApk?.absolutePath ?: "null") + " isFile=" + (resApk?.isFile ?: false))
        if (resApk == null || !resApk.isFile) {
            println("GATE FAIL resource patch ran but produced no resources.apk")
            exitProcess(12)
        }
        val outDir = workspace.resolve("patched").apply { mkdirs() }
        val outRes = outDir.resolve("resources.apk")
        if (resApk.canonicalPath != outRes.canonicalPath) resApk.copyTo(outRes, overwrite = true)
        patchedOtherResources?.takeIf { it.isDirectory }?.let { src ->
            val dst = outDir.resolve("other")
            if (src.canonicalPath != dst.canonicalPath) src.copyRecursively(dst, overwrite = true)
        }
        val before = ZipFile(apk).use { z ->
            z.getEntry("assets/index.android.bundle")?.let { z.getInputStream(it).readBytes() }
        }
        val after = ZipFile(outRes).use { z ->
            z.getEntry("assets/index.android.bundle")?.let { z.getInputStream(it).readBytes() }
        } ?: patchedOtherResources?.resolve("assets/index.android.bundle")?.takeIf { it.isFile }?.readBytes()
        if (before == null && after == null ) {
            println("GATE resource bundle absent in target and in patched output (non-bundle app)")
        } else {
            if (after == null ) {
                println("GATE FAIL patched bundle absent from resources.apk and in patcher other tree")
                exitProcess(14)
            }
            println("GATE resource bundle bytes before=${before?.size} after=${after.size} changed=${before != null && !before.contentEquals(after)}")
            if (before != null && before.contentEquals(after)) {
                println("GATE FAIL resource patch produced an unchanged bundle")
                exitProcess(13)
            }
        }
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

    // Optional dump of the patched dex for byte-level inspection by external scripts; the
    // patcher deletes its own temp files, so nothing is inspectable after this process exits.
    args.getOrNull(5)?.takeUnless { it.isBlank() }?.let { dir ->
        val out = File(dir).apply { mkdirs() }
        patched.forEach { (name, bytes) -> out.resolve(name).writeBytes(bytes) }
        println("GATE dumped dex to ${out.absolutePath} files=${patched.size}")
    }

    println("GATE PASS $changed dex file(s) mutated, ${applicable.size} patch(es) clean")
}
