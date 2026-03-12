plugins {
    kotlin("jvm")
    id("com.gradleup.shadow") version "9.2.2"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
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
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}
