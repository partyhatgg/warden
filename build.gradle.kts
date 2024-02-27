plugins {
    java
}

group = "dev.partyhat"
version = "1.0.6"

subprojects {
    apply<JavaPlugin>()

    group = rootProject.group
    version = rootProject.version

    val bundle: Configuration by configurations.creating {
        configurations.implementation.get().extendsFrom(this)
    }

    tasks.jar {
        dependsOn(bundle)
        doLast {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            from(bundle.files.map { zipTree(it) })
        }
    }
}