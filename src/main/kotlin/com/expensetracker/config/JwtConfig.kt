package com.expensetracker.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*
import io.ktor.server.config.ApplicationConfig

object JwtConfig {
    lateinit var realm: String
    private lateinit var secret: String
    private lateinit var issuer: String
    private lateinit var audience: String
    private var expirationHours: Int = 24
    lateinit var verifier: com.auth0.jwt.JWTVerifier

    fun init(config: ApplicationConfig) {
        secret = config.propertyOrNull("jwt.secret")?.getString() ?: "your-secret-key"
        issuer = config.propertyOrNull("jwt.issuer")?.getString() ?: "ExpenseTracker"
        audience = config.propertyOrNull("jwt.audience")?.getString() ?: "ExpenseTracker"
        realm = config.propertyOrNull("jwt.realm")?.getString() ?: "ExpenseTracker"
        expirationHours = config.propertyOrNull("jwt.expirationHours")?.getString()?.toInt() ?: 24
        verifier = com.auth0.jwt.JWT.require(com.auth0.jwt.algorithms.Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }

    fun makeToken(userId: String): String = com.auth0.jwt.JWT.create()
        .withSubject(userId)
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withIssuedAt(java.util.Date(System.currentTimeMillis()))
        .withNotBefore(java.util.Date(System.currentTimeMillis()))
        .withExpiresAt(java.util.Date(System.currentTimeMillis() + (expirationHours * 60 * 60 * 1000))) // Configurable hours
        .withJWTId(java.util.UUID.randomUUID().toString()) // Add unique JWT ID
        .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(secret))
}