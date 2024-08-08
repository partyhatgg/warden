plugins {
    id("fabric-loom") version "1.7.3"
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.10:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.0")

    bundle(project(":common"))

    configurations.bundle.get().resolvedConfiguration.resolvedArtifacts.forEach {
        when (val component = it.id.componentIdentifier) {
            is ProjectComponentIdentifier -> include(project(component.projectPath))
            else -> include(it.moduleVersion.id.toString())
        }
    }

    modImplementation(include("net.kyori:adventure-platform-fabric:5.9.0")!!)
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand("version" to project.version)
        }
    }

    remapJar {
        archiveBaseName = "Warden"
    }
}
