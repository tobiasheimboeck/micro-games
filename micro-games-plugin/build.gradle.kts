import java.io.FileInputStream
import java.util.Properties

plugins {
    kotlin("jvm")
    id("com.gradleup.shadow") version "9.2.2"
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven {
        name = "mclib"
        url = uri("https://maven.pkg.github.com/tobiasheimboeck/McLib")
        credentials {
            username = localProperties.getProperty("gpr.user") ?: System.getenv("GPR_USER") ?: ""
            password = localProperties.getProperty("gpr.key") ?: System.getenv("GPR_KEY") ?: ""
        }
    }
}

dependencies {
    implementation(project(":micro-games-api"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.hikaricp)
    implementation(libs.mariadb.java.client)
    
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.developertobi.mclib:mclib-api:1.0-SNAPSHOT")
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
    archiveFileName.set("micro-games.jar")
    mergeServiceFiles()
    archiveClassifier.set("")
    dependencies {
        exclude(dependency("org.jetbrains.kotlin:.*:.*"))
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}
