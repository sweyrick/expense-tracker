package com.expensetracker

import com.expensetracker.config.DatabaseFactory
import com.expensetracker.config.JwtConfig
import com.expensetracker.models.UserPrincipal
import com.expensetracker.routes.authRoutes
import com.expensetracker.routes.expenseRoutes
import com.expensetracker.routes.incomeRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import com.expensetracker.repository.UserRepository
import com.expensetracker.repository.ExpenseRepository
import com.expensetracker.repository.IncomeRepository
import org.slf4j.LoggerFactory

fun Application.module() {
    val logger = LoggerFactory.getLogger("Application")
    logger.info("Starting ExpenseTracker application...")
    install(ContentNegotiation) {
        json()
    }
    
    JwtConfig.init(environment.config)
    install(Authentication) {
        jwt("auth-jwt") {
            realm = JwtConfig.realm
            verifier(JwtConfig.verifier)
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (userId != null) {
                    UserPrincipal(userId)
                } else {
                    null
                }
            }
        }
    }
    
    DatabaseFactory.init(environment.config)
    
    // Create real repository implementations using DatabaseFactory
    val userRepository: UserRepository = DatabaseFactory as UserRepository
    val expenseRepository: ExpenseRepository = DatabaseFactory as ExpenseRepository
    val incomeRepository: IncomeRepository = DatabaseFactory as IncomeRepository
    
    routing {
        logger.info("Registering routes...")
        get("/") {
            call.respondText("Expense Tracker API is running!")
        }
        
        authRoutes(userRepository)
        expenseRoutes(expenseRepository, userRepository)
        incomeRoutes(incomeRepository, userRepository)
    }
}
