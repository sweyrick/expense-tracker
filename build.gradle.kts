import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.postgresql)
    implementation(libs.hikaricp)

    implementation(libs.logback.classic)
    implementation(libs.slf4j.api)
    implementation(libs.java.jwt)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)

    // Test dependencies
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.ktor.server.auth)
    testImplementation(libs.ktor.server.auth.jwt)
}

// Suppress deprecation warnings in Kotlin compilation

// For Kotlin DSL
kotlin {
    sourceSets.all {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
            // suppressWarnings is not supported in Gradle Kotlin DSL
        }
    }
}

// For Java compilation (if needed)
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:none")
}

// For Gradle itself
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.allWarningsAsErrors = false
    // To suppress warnings in CLI output, use: gradle build --warning-mode=none
}

// Configure tests
tasks.test {
    useJUnitPlatform()
    exclude("**/DatabaseIntegrationTest*")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}
