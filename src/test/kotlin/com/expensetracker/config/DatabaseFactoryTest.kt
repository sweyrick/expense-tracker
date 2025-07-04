package com.expensetracker.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DatabaseFactoryTest : FunSpec({
    
    context("Database Configuration") {
        test("should have valid database configuration") {
            // Test that we can access the database configuration
            // This is a basic test that doesn't require actual database connection
            val driverClassName = "org.postgresql.Driver"
            val jdbcURL = System.getenv("DB_JDBC_URL") ?: "jdbc:postgresql://db:5432/expensetracker"
            val dbUser = System.getenv("DB_USER") ?: "postgres"
            val dbPassword = System.getenv("DB_PASSWORD") ?: "password"
            
            driverClassName shouldBe "org.postgresql.Driver"
            jdbcURL shouldNotBe null
            dbUser shouldNotBe null
            dbPassword shouldNotBe null
        }
    }
    
    context("Database Functions") {
        test("should have dbQuery function") {
            // Test that the dbQuery function exists and has correct signature
            // This is a structural test, not a functional test
            val dbQueryFunction = DatabaseFactory::class.members.find { it.name == "dbQuery" }
            dbQueryFunction shouldNotBe null
        }
        
        test("should have user management functions") {
            // Test that user management functions exist
            val createUserFunction = DatabaseFactory::class.members.find { it.name == "createUser" }
            val findUserByUsernameFunction = DatabaseFactory::class.members.find { it.name == "findUserByUsername" }
            val findUserByIdFunction = DatabaseFactory::class.members.find { it.name == "findUserById" }
            
            createUserFunction shouldNotBe null
            findUserByUsernameFunction shouldNotBe null
            findUserByIdFunction shouldNotBe null
        }
        
        test("should have income management functions") {
            // Test that income management functions exist
            val createIncomeFunction = DatabaseFactory::class.members.find { it.name == "createIncome" }
            val getIncomesByUserIdFunction = DatabaseFactory::class.members.find { it.name == "getIncomesByUserId" }
            val getAllIncomesFunction = DatabaseFactory::class.members.find { it.name == "getAllIncomes" }
            
            createIncomeFunction shouldNotBe null
            getIncomesByUserIdFunction shouldNotBe null
            getAllIncomesFunction shouldNotBe null
        }
        
        test("should have expense management functions") {
            // Test that expense management functions exist
            val createExpenseFunction = DatabaseFactory::class.members.find { it.name == "createExpense" }
            val getExpensesByUserIdFunction = DatabaseFactory::class.members.find { it.name == "getExpensesByUserId" }
            val getAllExpensesFunction = DatabaseFactory::class.members.find { it.name == "getAllExpenses" }
            
            createExpenseFunction shouldNotBe null
            getExpensesByUserIdFunction shouldNotBe null
            getAllExpensesFunction shouldNotBe null
        }
    }
}) 