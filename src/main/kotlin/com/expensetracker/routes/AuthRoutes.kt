package com.expensetracker.routes

import com.expensetracker.config.JwtConfig
import com.expensetracker.models.AuthResponse
import com.expensetracker.models.UserLoginRequest
import com.expensetracker.models.UserRegistrationRequest
import com.expensetracker.models.UserPrincipal
import com.expensetracker.repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.security.MessageDigest
import org.slf4j.LoggerFactory

// Shared authentication wrapper for consistent auth across all routes
fun Route.withAuth(block: Route.() -> Unit) {
    authenticate("auth-jwt") {
        block()
    }
}

// Shared function to get authenticated user
suspend fun getAuthenticatedUser(call: io.ktor.server.application.ApplicationCall, userRepository: UserRepository): com.expensetracker.models.UserResponse? {
    val principal = call.principal<UserPrincipal>()
    if (principal == null) {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
        return null
    }
    
    val userId = principal.userId
    val user = userRepository.findUserById(userId)
    
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "User not found"))
        return null
    }
    
    return user
}

fun Route.authRoutes(userRepository: UserRepository) {
    val logger = LoggerFactory.getLogger("AuthRoutes")
    route("/auth") {
        post("/register") {
            try {
                val request = call.receive<UserRegistrationRequest>()
                logger.info("Received registration request: username={}, email={}", request.username, request.email)
                // Validate registration code
                val validRegistrationCode = call.application.environment.config.propertyOrNull("auth.registrationCode")?.getString() ?: "FAMILY2024"
                if (request.registrationCode != validRegistrationCode) {
                    logger.warn("Invalid registration code: {} (expected: {})", request.registrationCode, validRegistrationCode)
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Invalid registration code"))
                    return@post
                }
                // Check if user already exists
                val existingUser = userRepository.findUserByUsername(request.username)
                if (existingUser != null) {
                    logger.warn("Username already exists: {}", request.username)
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Username already exists"))
                    return@post
                }
                // Hash password (in production, use proper password hashing)
                val passwordHash = hashPassword(request.password)
                // Create user
                val userId = userRepository.createUser(request.username, request.email, passwordHash)
                val user = userRepository.findUserById(userId)
                if (user != null) {
                    // Generate JWT token
                    val token = JwtConfig.makeToken(userId)
                    logger.info("User registered successfully: {}", request.username)
                    call.respond(HttpStatusCode.Created, AuthResponse(token = token, user = user))
                } else {
                    logger.error("Failed to create user after registration: {}", request.username)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create user"))
                }
            } catch (e: Exception) {
                logger.error("Exception during registration: {}", e.message, e)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request data"))
            }
        }
        
        post("/login") {
            try {
                val request = call.receive<UserLoginRequest>()
                
                // Find user by username with password hash
                val userData = userRepository.findUserByUsernameWithPassword(request.username)
                if (userData == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                    return@post
                }
                
                val (userId, storedPasswordHash) = userData
                
                // Verify password
                val providedPasswordHash = hashPassword(request.password)
                if (storedPasswordHash != providedPasswordHash) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                    return@post
                }
                
                // Get user details
                val user = userRepository.findUserById(userId)
                if (user == null) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "User not found"))
                    return@post
                }
                
                // Generate JWT token
                val token = JwtConfig.makeToken(userId)
                
                call.respond(AuthResponse(token = token, user = user))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request data"))
            }
        }
    }
}

private fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
} 