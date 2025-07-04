package com.expensetracker

import io.ktor.server.testing.testApplication
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.application.install
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

fun setupTestAuth(app: Application) {
    app.install(Authentication) {
        jwt("auth-jwt") {
            realm = "ExpenseTracker"
            verifier(
                JWT.require(Algorithm.HMAC256("test-secret"))
                    .withAudience("ExpenseTracker")
                    .withIssuer("ExpenseTracker")
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (userId != null) {
                    com.expensetracker.models.UserPrincipal(userId)
                } else {
                    null
                }
            }
        }
    }
}

suspend fun ktorTest(applyRoutes: Application.() -> Unit, block: suspend ApplicationTestBuilder.() -> Unit) {
    testApplication {
        environment {
            config = MapApplicationConfig() // No ktor.application.modules set
        }
        application {
            setupTestAuth(this)
            applyRoutes()
        }
        block()
    }
} 