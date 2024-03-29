val http4kVersion: String by project

plugins {
    kotlin("jvm") version "1.8.10"
}

group = "pl.prz.edu.kod"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
}