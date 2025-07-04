package com.expensetracker.routes

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class RoutesTest : FunSpec({
    
    context("Route Functions") {
        test("should have authRoutes function") {
            // Test that the authRoutes function exists using reflection
            val authRoutesClass = Class.forName("com.expensetracker.routes.AuthRoutesKt")
            val authRoutesMethod = authRoutesClass.methods.find { it.name == "authRoutes" }
            authRoutesMethod shouldNotBe null
        }
        
        test("should have expenseRoutes function") {
            // Test that the expenseRoutes function exists using reflection
            val expenseRoutesClass = Class.forName("com.expensetracker.routes.ExpenseRoutesKt")
            val expenseRoutesMethod = expenseRoutesClass.methods.find { it.name == "expenseRoutes" }
            expenseRoutesMethod shouldNotBe null
        }
        
        test("should have incomeRoutes function") {
            // Test that the incomeRoutes function exists using reflection
            val incomeRoutesClass = Class.forName("com.expensetracker.routes.IncomeRoutesKt")
            val incomeRoutesMethod = incomeRoutesClass.methods.find { it.name == "incomeRoutes" }
            incomeRoutesMethod shouldNotBe null
        }
    }
}) 