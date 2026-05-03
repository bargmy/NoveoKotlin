import org.gradle.api.tasks.Exec

val patchNoveoChatUi by tasks.registering(Exec::class) {
    val patchScript = layout.projectDirectory.file("noveo_chat_ui_patches.py")
    inputs.file(patchScript)
    commandLine("python3", patchScript.asFile.absolutePath)
}

tasks.matching { it.name.startsWith("compile") || it.name.startsWith("pre") }.configureEach {
    dependsOn(patchNoveoChatUi)
}
