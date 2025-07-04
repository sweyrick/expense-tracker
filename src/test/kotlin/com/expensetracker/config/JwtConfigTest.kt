package com.expensetracker.config

import com.auth0.jwt.JWT
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.*

class JwtConfigTest : FunSpec({
    beforeSpec {
        val config = io.ktor.server.config.MapApplicationConfig().apply {
            put("jwt.secret", "test-secret")
            put("jwt.issuer", "test-issuer")
            put("jwt.audience", "test-audience")
            put("jwt.realm", "test-realm")
        }
        JwtConfig.init(config)
    }
    
    context("JWT Configuration") {
        test("should have valid realm") {
            JwtConfig.realm shouldNotBe null
            JwtConfig.realm shouldNotBe ""
        }
        
        test("should have valid verifier") {
            JwtConfig.verifier shouldNotBe null
        }
    }
    
    context("JWT Token Generation and Validation") {
        test("should generate valid token for user") {
            val userId = "testuser"
            val token = JwtConfig.makeToken(userId)
            
            token shouldNotBe null
            token shouldNotBe ""
            
            // Verify the token can be decoded
            val decodedJWT = JWT.decode(token)
            decodedJWT.subject shouldBe userId
        }
        
        test("should generate tokens with different values for different users") {
            val token1 = JwtConfig.makeToken("user1")
            val token2 = JwtConfig.makeToken("user2")
            
            token1 shouldNotBe token2
        }
        
        test("should generate different tokens for same user on different calls") {
            val token1 = JwtConfig.makeToken("user1")
            val token2 = JwtConfig.makeToken("user1")
            
            token1 shouldNotBe token2
        }
        
        test("should verify valid token") {
            val userId = "testuser"
            val token = JwtConfig.makeToken(userId)
            
            val verifiedJWT = JwtConfig.verifier.verify(token)
            verifiedJWT.subject shouldBe userId
        }
        
        test("should extract user ID from token") {
            val userId = "testuser"
            val token = JwtConfig.makeToken(userId)
            
            val decodedJWT = JWT.decode(token)
            val extractedUserId = decodedJWT.getClaim("userId").asString()
            
            extractedUserId shouldBe userId
        }
    }
    
    context("JWT Token Properties") {
        test("should have correct expiration time") {
            val token = JwtConfig.makeToken("testuser")
            val decodedJWT = JWT.decode(token)
            
            val now = Date()
            val expiration = decodedJWT.expiresAt
            
            expiration shouldNotBe null
            expiration!!.after(now) shouldBe true
        }
        
        test("should have correct issued at time") {
            val token = JwtConfig.makeToken("testuser")
            val decodedJWT = JWT.decode(token)
            
            val now = Date()
            val issuedAt = decodedJWT.issuedAt
            
            issuedAt shouldNotBe null
            issuedAt!!.before(now) || issuedAt.equals(now) shouldBe true
        }
        
        test("should have correct not before time") {
            val token = JwtConfig.makeToken("testuser")
            val decodedJWT = JWT.decode(token)
            
            val now = Date()
            val notBefore = decodedJWT.notBefore
            
            notBefore shouldNotBe null
            notBefore!!.before(now) || notBefore.equals(now) shouldBe true
        }
    }
    
    context("JWT Claims") {
        test("should include required claims") {
            val token = JwtConfig.makeToken("testuser")
            val decodedJWT = JWT.decode(token)
            
            decodedJWT.subject shouldNotBe null
            decodedJWT.expiresAt shouldNotBe null
            decodedJWT.issuedAt shouldNotBe null
            decodedJWT.notBefore shouldNotBe null
        }
        
        test("should include custom userId claim") {
            val userId = "testuser"
            val token = JwtConfig.makeToken(userId)
            val decodedJWT = JWT.decode(token)
            
            val customUserId = decodedJWT.getClaim("userId").asString()
            customUserId shouldBe userId
        }
    }
}) 