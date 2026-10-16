group = "standardrepo.morphe"

// Resolved by crackpipe at build time (`-PpublishSource=...`) so the published manifest
// never carries a placeholder owner/repo or the wrong forge host.
val publishSource: String = providers.gradleProperty("publishSource").orNull
    ?: System.getenv("CRACKPIPE_PUBLISH_SOURCE")
    ?: "git@github.com:OWNER/REPO.git"

patches {
    about {
        name = "Crackpipe Auto Patches"
        description = "Automatically generated patches for use with Morphe"
        source = publishSource
        author = "crackpipe"
        contact = "na"
        website = "https://morphe.software"
        license = "GPLv3"
    }
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/MorpheApp/registry")
        credentials {
            username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
            password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
        }
    }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Used by JsonGenerator.
    implementation(libs.gson)
    // Required due to smali, or build fails. Can be removed once smali is bumped.
    implementation(libs.guava)
    implementation(libs.morphe.patches.library)

    // Used to read exported symbol from ELF libraries
    implementation(libs.jelf)

    implementation(libs.kotlinx.serialization.json)
    // Stage-5 gate runner (VerifyMain) drives Patcher's coroutine Flow.
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.native.lib.loader) {
        // bundled version clashes with newer runtime dependency
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    compileOnly(project(":patches:stub"))
    compileOnly(libs.android.all)
    compileOnly(libs.slf4j.api)
}

// android-all (robolectric) on the GATE JVM classpath only: it supplies dalvik.system.DexClassLoader
// so the gate can drive the Manager's own `loadPatchesFromDex`. Kept out of the .mpp (compileOnly).
val gateRuntime by configurations.creating
dependencies.add(gateRuntime.name, libs.android.all)

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("app.morphe.util.PatchListGeneratorKt")
    }

    // Stage-5 gate (crackpipe): apply this bundle's patches to a real APK with the
    // Morphe Patcher on the JVM and fail on fingerprint miss / no-op mutation.
    // Usage: ./gradlew :patches:verifyPatch -PtargetApk=... -PexpectedPackage=... -PexpectedVersion=...
    // dependsOn `buildAndroid` (NOT `build`): buildAndroid is the task that merges the D8
    // classes.dex into the .mpp, and `build` -> `jar` would re-write the jar and strip that
    // dex again. Manager loads patches with loadPatchesFromDex -> the gate must read the
    // same dex-bearing artifact the Manager downloads.
    register<JavaExec>("verifyPatch") {
        description = "Applies compiled patches to a target APK via Morphe Patcher (JVM fingerprint gate)."

        dependsOn("buildAndroid")


        classpath = sourceSets["main"].runtimeClasspath + gateRuntime
        mainClass.set("standardrepo.morphe.gate.VerifyMainKt")
        args = listOf(
            providers.gradleProperty("targetApk").orNull
                ?: throw GradleException("verifyPatch requires -PtargetApk=<path>"),
            layout.buildDirectory.dir("libs").get().asFile.absolutePath,
            providers.gradleProperty("expectedPackage").orNull
                ?: throw GradleException("verifyPatch requires -PexpectedPackage=<pkg>"),
            providers.gradleProperty("expectedVersion").orNull
                ?: throw GradleException("verifyPatch requires -PexpectedVersion=<ver>"),
            providers.gradleProperty("expectedAppName").orNull.orEmpty(),
        )
    }
    // Used by gradle-semantic-release-plugin.
    publish {
        dependsOn("generatePatchesList")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }

    jvmToolchain(17)
}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

plugins {
    kotlin("plugin.serialization") version "2.4.10"
}
