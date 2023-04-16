val kotlin_version: String by project
val logback_version = "1.4.6"
val koin_version = "3.4.0"
val kotlin_logging_version = "3.0.5"
val jackson_version = "2.14.2"
val http4k_version = "4.41.4.0"

plugins {
    kotlin("jvm") version "1.8.10"
    id("io.ktor.plugin") version "2.2.4"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

group = "pl.edu.prz.kod"
version = "0.0.1"
application {
    mainClass.set("pl.edu.prz.kod.application.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("org.http4k:http4k-bom:$http4k_version")
    implementation("org.http4k:http4k-core:$http4k_version")
    implementation("org.http4k:http4k-server-netty:$http4k_version")
    implementation("org.http4k:http4k-format-jackson:$http4k_version")
    implementation("org.http4k:http4k-format-core:$http4k_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.github.microutils:kotlin-logging:$kotlin_logging_version")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

ktor {
    fatJar {
        archiveFileName.set("kubexecutor.jar")
    }
}