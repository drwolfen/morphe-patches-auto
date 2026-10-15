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
import app.morphe.patcher.patch.loadPatchesFromJar
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.zip.ZipFile
import kotlin.system.exitProcess

private val DEX_RE = Regex("""classes\d*\.dex""")

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
    require(args.size >= 4) { "usage: verifyPatch <apk> <libsDir> <expectedPackage> <expectedVersion>" }
    val apk = File(args[0])
    val libsDir = File(args[1])
    val expectedPackage = args[2]
    val expectedVersion = args[3]
    require(apk.isFile) { "GATE FAIL apk not found: ${apk.absolutePath}" }

    val mpp = libsDir.listFiles { f ->
        f.isFile && f.name.endsWith(".mpp") && !f.name.contains("javadoc") && !f.name.contains("sources")
    }?.minByOrNull { it.name }
    if (mpp == null) {
        println("GATE FAIL no .mpp bundle in ${libsDir.absolutePath}")
        exitProcess(4)
    }

    println("GATE start apk=${apk.name} bundle=${mpp.name} target=$expectedPackage@$expectedVersion")

    val loaded = loadPatchesFromJar(setOf(mpp))
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
        println("GATE FAIL no patch in bundle declares $expectedPackage@$expectedVersion (Compat drift)")
        exitProcess(5)
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

    println("GATE PASS $changed dex file(s) mutated, ${applicable.size} patch(es) clean")
}
