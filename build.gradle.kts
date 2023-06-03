plugins {
    java
}

group = "live.mcparty"
version = "1.0-SNAPSHOT"

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
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    implementation("net.dv8tion:JDA:5.0.0-beta.9")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}

//def targetJavaVersion = 17
//java {
//    def javaVersion = JavaVersion.toVersion(targetJavaVersion)
//    sourceCompatibility = javaVersion
//    targetCompatibility = javaVersion
//    if (JavaVersion.current() < javaVersion) {
//        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
//    }
//}
//
//tasks.withType(JavaCompile).configureEach {
//    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
//        options.release = targetJavaVersion
//    }
//}
//
//processResources {
//    def props = [version: version]
//    inputs.properties props
//            filteringCharset 'UTF-8'
//    filesMatching('plugin.yml') {
//        expand props
//    }
//}
