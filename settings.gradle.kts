pluginManagement {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net")
    }
}

rootProject.name = "Warden"
include(
    "common",
    "paper",
    "fabric-1-20-1"
)
