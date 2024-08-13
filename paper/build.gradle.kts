repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    bundle(project(":common"))
}

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.bundle.get().files.map { zipTree(it) })
    }
}
