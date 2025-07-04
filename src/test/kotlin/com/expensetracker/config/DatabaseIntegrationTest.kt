package com.expensetracker.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import io.ktor.server.config.MapApplicationConfig
import org.junit.jupiter.api.Disabled

@Disabled("Temporarily disabled due to Docker build failures")
@Testcontainers
class DatabaseIntegrationTest : FunSpec({
    beforeTest {
        // Ensure the container is started before the test
        if (!postgres.isRunning) {
            postgres.start()
        }
    }
    
    test("should connect to test database and create user") {
        // Use the Testcontainers-provided connection details
        val testConfig = MapApplicationConfig(
            "db.jdbcurl" to postgres.jdbcUrl,
            "db.user" to postgres.username,
            "db.password" to postgres.password
        )
        DatabaseFactory.init(testConfig)
        
        val random = Random.nextInt(100000, 999999)
        val userId = runBlocking {
            DatabaseFactory.createUser(
                "testuser_$random",
                "test_${random}@example.com",
                "hash"
            )
        }
        userId shouldNotBe null
    }
}) {
    companion object {
        @Container
        @JvmField
        val postgres = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("testdb")
            withUsername("testuser")
            withPassword("testpass")
        }
    }
} 