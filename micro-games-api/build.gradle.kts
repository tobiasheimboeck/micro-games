import java.util.Properties
import java.io.FileInputStream

plugins {
    kotlin("jvm")
    `maven-publish`
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
    @Suppress("UNCHECKED_CAST")
    localProperties.forEach { entry: Map.Entry<*, *> ->
        project.extensions.extraProperties.set(entry.key.toString(), entry.value)
    }
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}




publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tobiashemboeck/micro-games")
            credentials {
                username = localProperties.getProperty("gpr.user") ?: System.getenv("GPR_USER") ?: ""
                password = localProperties.getProperty("gpr.key") ?: System.getenv("GPR_KEY") ?: ""
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

