package com.expensetracker

import com.expensetracker.repository.MockUserRepository
import com.expensetracker.repository.UserRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.ktor.server.application.application
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.routing.routing
import com.expensetracker.routes.authRoutes
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import com.expensetracker.ktorTest

class ApplicationTest : FunSpec({
    lateinit var userRepository: UserRepository

    beforeTest {
        userRepository = MockUserRepository()
    }

    test("should return hello world") {
        ktorTest({
            this.routing {
                get("/") {
                    call.respondText("Expense Tracker API is running!")
                }
                authRoutes(userRepository)
            }
        }) {
            client.get("/").apply {
                this.status shouldBe HttpStatusCode.OK
                bodyAsText() shouldBe "Expense Tracker API is running!"
            }
        }
    }
}) 