val logback_version = "1.4.6"
val koin_version = "3.4.0"
val jackson_version = "2.14.2"
val http4k_version = "4.41.4.0"

plugins {
    kotlin("jvm") version "1.8.10"
}

group = "pl.edu.prz.kod"
version = "0.0.1"

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "pl.edu.prz.kod.mediator.ApplicationKt"
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
    implementation("org.http4k:http4k-bom:$http4k_version")
    implementation("org.http4k:http4k-core:$http4k_version")
    implementation("org.http4k:http4k-server-netty:$http4k_version")
    implementation("org.http4k:http4k-format-jackson:$http4k_version")
    implementation("org.http4k:http4k-format-core:$http4k_version")
    implementation("org.http4k:http4k-client-okhttp:$http4k_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}