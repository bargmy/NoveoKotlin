import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

dependencies {
    implementation(project(":core:ui"))
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

compose.desktop {
    application {
        mainClass = "ir.hienob.noveo.desktop.DesktopMainKt"
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe)
            packageName = "Noveo"
            packageVersion = "1.0.0"
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
    }
}

kotlin {
    jvmToolchain(17)
}


val thinDesktopDistDir = layout.buildDirectory.dir("thinDesktop")

val syncThinDesktopDist by tasks.registering(Sync::class) {
    dependsOn(tasks.named("jar"))
    into(thinDesktopDistDir)

    into("lib") {
        from(configurations.runtimeClasspath)
        from(tasks.named<Jar>("jar"))
    }

    into("bin") {
        from(layout.projectDirectory.file("src/main/scripts/noveo-desktop.bat"))
        from(layout.projectDirectory.file("src/main/scripts/noveo-desktop"))
    }
}

tasks.register<Zip>("createThinDesktopZip") {
    group = "distribution"
    description = "Creates a desktop zip that uses system Java instead of bundling a JVM runtime."
    dependsOn(syncThinDesktopDist)
    archiveFileName.set("Noveo-1.0.0-thin-windows-x64.zip")
    destinationDirectory.set(layout.buildDirectory.dir("compose/binaries/thin"))
    from(thinDesktopDistDir)
}
