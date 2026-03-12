import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties
import java.io.FileInputStream

plugins {
    kotlin("jvm") version "2.2.21"
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

allprojects {
    group = "net.developertobi.game"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven {
            name = "mclib"
            url = uri("https://maven.pkg.github.com/tobiasheimboeck/McLib")
            credentials {
                username = localProperties.getProperty("gpr.user") ?: System.getenv("GPR_USER") ?: ""
                password = localProperties.getProperty("gpr.key") ?: System.getenv("GPR_KEY") ?: ""
            }
        }
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}


