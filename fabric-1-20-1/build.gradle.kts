plugins {
    id("fabric-loom") version "1.5-SNAPSHOT"
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.10:v2")
    modImplementation("net.fabricmc:fabric-loader:0.15.7")

    bundle(project(":common"))

    configurations.bundle.get().resolvedConfiguration.resolvedArtifacts.forEach {
        when (val component = it.id.componentIdentifier) {
            is ProjectComponentIdentifier -> include(project(component.projectPath))
            else -> include(it.moduleVersion.id.toString())
        }
    }

    include(implementation(annotationProcessor("io.github.llamalad7:mixinextras-fabric:0.2.2")!!)!!)
    modImplementation(include("net.kyori:adventure-platform-fabric:5.9.0")!!)
}

