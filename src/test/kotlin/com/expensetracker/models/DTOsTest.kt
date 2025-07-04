package com.expensetracker.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

class DTOsTest : FunSpec({
    
    context("UserResponse") {
        test("should create UserResponse with correct values") {
            val createdAt = OffsetDateTime.now(ZoneOffset.UTC)
            val userResponse = UserResponse(
                id = "test-id",
                username = "testuser",
                email = "test@example.com",
                createdAt = createdAt
            )
            
            userResponse.id shouldBe "test-id"
            userResponse.username shouldBe "testuser"
            userResponse.email shouldBe "test@example.com"
            userResponse.createdAt shouldBe createdAt
        }
    }
    
    context("IncomeResponse") {
        test("should create IncomeResponse with correct values") {
            val startDate = LocalDate.now()
            val incomeResponse = IncomeResponse(
                id = "income-id",
                amount = 1000.0,
                description = "Salary",
                isRecurring = true,
                startDate = startDate,
                userId = "user-id",
                username = "testuser"
            )
            
            incomeResponse.id shouldBe "income-id"
            incomeResponse.amount shouldBe 1000.0
            incomeResponse.description shouldBe "Salary"
            incomeResponse.isRecurring shouldBe true
            incomeResponse.startDate shouldBe startDate
            incomeResponse.userId shouldBe "user-id"
            incomeResponse.username shouldBe "testuser"
        }
    }
    
    context("ExpenseResponse") {
        test("should create ExpenseResponse with correct values") {
            val startDate = LocalDate.now()
            val expenseResponse = ExpenseResponse(
                id = "expense-id",
                amount = 100.0,
                category = ExpenseCategory.GROCERIES,
                description = "Groceries",
                isRecurring = false,
                startDate = startDate,
                userId = "user-id",
                username = "testuser"
            )
            
            expenseResponse.id shouldBe "expense-id"
            expenseResponse.amount shouldBe 100.0
            expenseResponse.category shouldBe ExpenseCategory.GROCERIES
            expenseResponse.description shouldBe "Groceries"
            expenseResponse.isRecurring shouldBe false
            expenseResponse.startDate shouldBe startDate
            expenseResponse.userId shouldBe "user-id"
            expenseResponse.username shouldBe "testuser"
        }
    }
    
    context("UserLoginRequest") {
        test("should create UserLoginRequest with correct values") {
            val loginRequest = UserLoginRequest(
                username = "testuser",
                password = "password123"
            )
            
            loginRequest.username shouldBe "testuser"
            loginRequest.password shouldBe "password123"
        }
    }
    
    context("UserRegistrationRequest") {
        test("should create UserRegistrationRequest with correct values") {
            val registerRequest = UserRegistrationRequest(
                username = "newuser",
                email = "newuser@example.com",
                password = "password123",
                registrationCode = "CODE123"
            )
            
            registerRequest.username shouldBe "newuser"
            registerRequest.email shouldBe "newuser@example.com"
            registerRequest.password shouldBe "password123"
            registerRequest.registrationCode shouldBe "CODE123"
        }
    }
    
    context("IncomeRequest") {
        test("should create IncomeRequest with correct values") {
            val startDate = LocalDate.now()
            val incomeRequest = IncomeRequest(
                amount = 5000.0,
                description = "Monthly salary",
                isRecurring = true,
                startDate = startDate
            )
            
            incomeRequest.amount shouldBe 5000.0
            incomeRequest.description shouldBe "Monthly salary"
            incomeRequest.isRecurring shouldBe true
            incomeRequest.startDate shouldBe startDate
        }
    }
    
    context("ExpenseRequest") {
        test("should create ExpenseRequest with correct values") {
            val startDate = LocalDate.now()
            val expenseRequest = ExpenseRequest(
                amount = 100.0,
                category = ExpenseCategory.GROCERIES,
                description = "Groceries",
                isRecurring = false,
                startDate = startDate
            )
            
            expenseRequest.amount shouldBe 100.0
            expenseRequest.category shouldBe ExpenseCategory.GROCERIES
            expenseRequest.description shouldBe "Groceries"
            expenseRequest.isRecurring shouldBe false
            expenseRequest.startDate shouldBe startDate
        }
    }
    
    context("AuthResponse") {
        test("should create AuthResponse with correct values") {
            val user = UserResponse(
                id = "user-id",
                username = "testuser",
                email = "test@example.com",
                createdAt = OffsetDateTime.now(ZoneOffset.UTC)
            )
            
            val authResponse = AuthResponse(
                token = "jwt-token",
                user = user
            )
            
            authResponse.token shouldBe "jwt-token"
            authResponse.user shouldBe user
        }
    }
    
    context("UserPrincipal") {
        test("should create UserPrincipal with correct values") {
            val userPrincipal = UserPrincipal("user-id")
            
            userPrincipal.userId shouldBe "user-id"
        }
    }
}) 