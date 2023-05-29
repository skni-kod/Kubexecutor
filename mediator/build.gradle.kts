val logbackVersion: String by project
val koinVersion: String by project
val jacksonVersion: String by project
val http4kVersion: String by project

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
    implementation("org.http4k:http4k-server-netty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    implementation("org.http4k:http4k-format-core:$http4kVersion")
    implementation("org.http4k:http4k-contract:$http4kVersion")
    implementation("org.http4k:http4k-client-okhttp:$http4kVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}