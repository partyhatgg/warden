plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(bundle("net.dv8tion:JDA:5.0.0-beta.9")!!)
    api(bundle("com.fasterxml.jackson.core:jackson-databind:2.15.2")!!)
    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly("com.google.guava:guava:31.1-jre")
}