plugins {
    java
}

group = "dev.partyhat"
version = "1.0.6"

subprojects {
    apply<JavaPlugin>()

    group = rootProject.group
    version = rootProject.version

    val include: Configuration by configurations.creating {
        configurations.implementation.get().extendsFrom(this)
    }

    tasks.jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependsOn(include)
        from(include.files.map { zipTree(it) })
    }
}