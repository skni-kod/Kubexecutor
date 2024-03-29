val logbackVersion: String by project
val koinVersion: String by project
val jacksonVersion: String by project
val http4kVersion: String by project
val googleApiClientVersion: String by project
val javaJwtVersion: String by project
val flywayVersion: String by project
val postgresqlVersion: String by project
val hikariVersion: String by project
val exposedVersion: String by project

plugins {
    kotlin("jvm") version "1.8.10"
}

group = "pl.edu.prz.kod"
version = "0.0.1"

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "pl.edu.prz.kod.mediator.application.ApplicationKt"
    }
    archiveFileName.set("mediator.jar")

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("org.http4k:http4k-bom:$http4kVersion")
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-security-oauth:$http4kVersion")
    implementation("org.http4k:http4k-server-netty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    implementation("org.http4k:http4k-format-core:$http4kVersion")
    implementation("org.http4k:http4k-contract:$http4kVersion")
    implementation("org.http4k:http4k-client-okhttp:$http4kVersion")
    implementation("com.google.api-client:google-api-client:$googleApiClientVersion")
    implementation("com.auth0:java-jwt:$javaJwtVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

//    Database
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}