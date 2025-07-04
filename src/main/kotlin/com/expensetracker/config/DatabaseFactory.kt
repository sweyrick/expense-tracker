package com.expensetracker.config

import com.expensetracker.models.ExpenseResponse
import com.expensetracker.models.IncomeResponse
import com.expensetracker.models.UserResponse
import com.expensetracker.models.Users
import com.expensetracker.models.Incomes
import com.expensetracker.models.Expenses
import com.expensetracker.models.ExpenseCategory
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import io.ktor.server.config.ApplicationConfig
import com.expensetracker.repository.UserRepository
import com.expensetracker.repository.ExpenseRepository
import com.expensetracker.repository.IncomeRepository
import org.slf4j.LoggerFactory

object DatabaseFactory : UserRepository, ExpenseRepository, IncomeRepository {
    private val logger = LoggerFactory.getLogger("DatabaseFactory")
    private lateinit var dataSource: HikariDataSource

    fun init(config: ApplicationConfig) {
        logger.info("Initializing database connection...")
        val driverClassName = "org.postgresql.Driver"
        var jdbcURL: String?
        try {
            jdbcURL = config.propertyOrNull("db.jdbcurl")?.getString() ?: "jdbc:postgresql://localhost:5432/expensetracker"
            logger.info("JDBC URL being used: $jdbcURL")
        } catch (e: Exception) {
            logger.error("Exception while reading JDBC URL: ", e)
            println("Exception while reading JDBC URL: ${e.message}")
            throw e
        }
        val username = config.propertyOrNull("db.user")?.getString() ?: "postgres"
        val password = config.propertyOrNull("db.password")?.getString() ?: "password"

        // Only run Flyway migrations if not in test environment
        val isTestEnvironment = System.getProperty("test") == "true" || 
                               System.getProperty("TEST_ENV") == "true" ||
                               System.getenv("TEST_ENV") == "true" ||
                               jdbcURL.contains("testdb")
        
        if (!isTestEnvironment) {
            runMigrations(jdbcURL, username, password)
        }

        val hikariConfig = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcURL
            this.username = username
            this.password = password
            
            // Connection pool settings from config
            this.maximumPoolSize = config.propertyOrNull("db.pool.maximumPoolSize")?.getString()?.toInt() ?: 10
            this.minimumIdle = config.propertyOrNull("db.pool.minimumIdle")?.getString()?.toInt() ?: 2
            this.connectionTimeout = config.propertyOrNull("db.pool.connectionTimeout")?.getString()?.toLong() ?: 30000
            this.idleTimeout = config.propertyOrNull("db.pool.idleTimeout")?.getString()?.toLong() ?: 300000
            this.maxLifetime = config.propertyOrNull("db.pool.maxLifetime")?.getString()?.toLong() ?: 900000
            this.leakDetectionThreshold = config.propertyOrNull("db.pool.leakDetectionThreshold")?.getString()?.toLong() ?: 60000
            this.connectionTestQuery = "SELECT 1"
        }

        dataSource = HikariDataSource(hikariConfig)
        
        Database.connect(dataSource)
        
        logger.info("Database initialized successfully.")
    }

    private fun runMigrations(jdbcUrl: String, username: String, password: String) {
        val flyway = Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .load()
        try {
            flyway.migrate()
            logger.info("Database migrations completed successfully.")
        } catch (e: Exception) {
            logger.error("Migration failed: ${e.message}", e)
            throw e
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun createIncome(userId: String, amount: Double, description: String, isRecurring: Boolean, startDate: LocalDate, endDate: LocalDate? = null): String {
        return dbQuery {
            val insertStatement = Incomes.insert {
                it[id] = UUID.randomUUID()
                it[Incomes.userId] = UUID.fromString(userId)
                it[Incomes.amount] = amount.toBigDecimal()
                it[Incomes.description] = description
                it[Incomes.isRecurring] = isRecurring
                it[Incomes.startDate] = startDate
                it[Incomes.endDate] = endDate
            }
            insertStatement[Incomes.id].toString()
        }
    }

    suspend fun getIncomesByUserId(userId: String): List<IncomeResponse> {
        return dbQuery {
            (Incomes leftJoin Users)
                .select(Incomes.userId eq UUID.fromString(userId))
                .map { row ->
                    IncomeResponse(
                        id = row[Incomes.id].toString(),
                        amount = row[Incomes.amount].toDouble(),
                        description = row[Incomes.description],
                        isRecurring = row[Incomes.isRecurring],
                        startDate = row[Incomes.startDate],
                        userId = row[Incomes.userId].toString(),
                        username = row[Users.username]
                    )
                }
        }
    }

    suspend fun getAllIncomes(startDate: LocalDate? = null, endDate: LocalDate? = null): List<IncomeResponse> {
        return dbQuery {
            val baseQuery = (Incomes leftJoin Users)
            val filteredQuery = when {
                startDate != null && endDate != null -> {
                    baseQuery.select(Incomes.startDate.between(startDate, endDate))
                }
                startDate != null -> {
                    baseQuery.select(Incomes.startDate greaterEq startDate)
                }
                endDate != null -> {
                    baseQuery.select(Incomes.startDate lessEq endDate)
                }
                else -> baseQuery.selectAll()
            }
            filteredQuery.map { row ->
                IncomeResponse(
                    id = row[Incomes.id].toString(),
                    amount = row[Incomes.amount].toDouble(),
                    description = row[Incomes.description],
                    isRecurring = row[Incomes.isRecurring],
                    startDate = row[Incomes.startDate],
                    endDate = row[Incomes.endDate],
                    userId = row[Incomes.userId].toString(),
                    username = row[Users.username]
                )
            }
        }
    }

    suspend fun createExpense(userId: String, amount: Double, category: String, description: String, isRecurring: Boolean, startDate: LocalDate, endDate: LocalDate? = null): String {
        return dbQuery {
            val insertStatement = Expenses.insert {
                it[id] = UUID.randomUUID()
                it[Expenses.userId] = UUID.fromString(userId)
                it[Expenses.amount] = amount.toBigDecimal()
                it[Expenses.category] = category
                it[Expenses.description] = description
                it[Expenses.isRecurring] = isRecurring
                it[Expenses.startDate] = startDate
                it[Expenses.endDate] = endDate
            }
            insertStatement[Expenses.id].toString()
        }
    }

    suspend fun getExpensesByUserId(userId: String): List<ExpenseResponse> {
        return dbQuery {
            (Expenses leftJoin Users)
                .select(Expenses.userId eq UUID.fromString(userId))
                .map { row ->
                    ExpenseResponse(
                        id = row[Expenses.id].toString(),
                        amount = row[Expenses.amount].toDouble(),
                        category = ExpenseCategory.valueOf(row[Expenses.category]),
                        description = row[Expenses.description],
                        isRecurring = row[Expenses.isRecurring],
                        startDate = row[Expenses.startDate],
                        userId = row[Expenses.userId].toString(),
                        username = row[Users.username]
                    )
                }
        }
    }

    suspend fun getAllExpenses(startDate: LocalDate? = null, endDate: LocalDate? = null): List<ExpenseResponse> {
        return dbQuery {
            val baseQuery = (Expenses leftJoin Users)
            val filteredQuery = when {
                startDate != null && endDate != null -> {
                    baseQuery.select(Expenses.startDate.between(startDate, endDate))
                }
                startDate != null -> {
                    baseQuery.select(Expenses.startDate greaterEq startDate)
                }
                endDate != null -> {
                    baseQuery.select(Expenses.startDate lessEq endDate)
                }
                else -> baseQuery.selectAll()
            }
            filteredQuery.map { row ->
                ExpenseResponse(
                    id = row[Expenses.id].toString(),
                    amount = row[Expenses.amount].toDouble(),
                    category = ExpenseCategory.valueOf(row[Expenses.category]),
                    description = row[Expenses.description],
                    isRecurring = row[Expenses.isRecurring],
                    startDate = row[Expenses.startDate],
                    endDate = row[Expenses.endDate],
                    userId = row[Expenses.userId].toString(),
                    username = row[Users.username]
                )
            }
        }
    }

    suspend fun initializeTestData() {
        dbQuery {
            // Check if test user already exists
            val existingUser = Users.select { Users.username eq "testuser" }.singleOrNull()
            val userId = if (existingUser != null) {
                existingUser[Users.id].toString()
            } else {
                // Create test user only if it doesn't exist
                createUser("testuser", "test@example.com", "hashedpassword")
            }
            // Create test incomes (only if they don't exist)
            val existingIncomes = Incomes.select { Incomes.userId eq UUID.fromString(userId) }.count()
            if (existingIncomes == 0L) {
                createIncome(userId, 5000.0, "Salary", true, LocalDate.now())
                createIncome(userId, 500.0, "Freelance", false, LocalDate.now())
            }
            // Create test expenses with correct enum values (only if they don't exist)
            val existingExpenses = Expenses.select { Expenses.userId eq UUID.fromString(userId) }.count()
            if (existingExpenses == 0L) {
                createExpense(userId, 1200.0, "RENT", "Monthly rent payment", true, LocalDate.now())
                createExpense(userId, 200.0, "GROCERIES", "Groceries", true, LocalDate.now())
                createExpense(userId, 50.0, "ENTERTAINMENT", "Movie tickets", false, LocalDate.now())
            }
        }
    }

    suspend fun updateExpense(
        id: String,
        amount: Double,
        category: String,
        description: String,
        isRecurring: Boolean,
        startDate: LocalDate,
        endDate: LocalDate? = null
    ): Boolean = dbQuery {
        Expenses.update({ Expenses.id eq UUID.fromString(id) }) {
            it[Expenses.amount] = amount.toBigDecimal()
            it[Expenses.category] = category
            it[Expenses.description] = description
            it[Expenses.isRecurring] = isRecurring
            it[Expenses.startDate] = startDate
            it[Expenses.endDate] = endDate
        } > 0
    }

    suspend fun deleteExpense(id: String): Boolean = dbQuery {
        Expenses.deleteWhere { Expenses.id eq UUID.fromString(id) } > 0
    }

    suspend fun updateIncome(
        id: String,
        amount: Double,
        description: String,
        isRecurring: Boolean,
        startDate: LocalDate,
        endDate: LocalDate? = null
    ): Boolean = dbQuery {
        Incomes.update({ Incomes.id eq UUID.fromString(id) }) {
            it[Incomes.amount] = amount.toBigDecimal()
            it[Incomes.description] = description
            it[Incomes.isRecurring] = isRecurring
            it[Incomes.startDate] = startDate
            it[Incomes.endDate] = endDate
        } > 0
    }

    suspend fun deleteIncome(id: String): Boolean = dbQuery {
        Incomes.deleteWhere { Incomes.id eq UUID.fromString(id) } > 0
    }

    // UserRepository overrides
    override suspend fun createUser(username: String, email: String, passwordHash: String): String {
        return dbQuery {
            val insertStatement = Users.insert {
                it[id] = UUID.randomUUID()
                it[Users.username] = username
                it[Users.email] = email
                it[Users.passwordHash] = passwordHash
                it[createdAt] = LocalDateTime.now()
            }
            insertStatement[Users.id].toString()
        }
    }

    override suspend fun findUserByUsername(username: String): UserResponse? {
        return dbQuery {
            Users.select(Users.username eq username)
                .map { row ->
                    UserResponse(
                        id = row[Users.id].toString(),
                        username = row[Users.username],
                        email = row[Users.email],
                        createdAt = row[Users.createdAt].atOffset(ZoneOffset.UTC)
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun findUserByUsernameWithPassword(username: String): Pair<String, String>? {
        return dbQuery {
            Users.select(Users.username eq username)
                .map { row ->
                    Pair(
                        row[Users.id].toString(),
                        row[Users.passwordHash]
                    )
                }
                .singleOrNull()
        }
    }

    override suspend fun findUserById(id: String): UserResponse? {
        return dbQuery {
            Users.select(Users.id eq UUID.fromString(id))
                .map { row ->
                    UserResponse(
                        id = row[Users.id].toString(),
                        username = row[Users.username],
                        email = row[Users.email],
                        createdAt = row[Users.createdAt].atOffset(ZoneOffset.UTC)
                    )
                }
                .singleOrNull()
        }
    }

    // ExpenseRepository overrides
    override suspend fun createExpense(userId: String, request: com.expensetracker.models.ExpenseRequest): String =
        createExpense(userId, request.amount, request.category.name, request.description, request.isRecurring, request.startDate, request.endDate)
    override suspend fun getExpenses(userId: String, startDate: LocalDate?, endDate: LocalDate?): List<com.expensetracker.models.ExpenseResponse> =
        getAllExpenses(startDate, endDate).filter { it.userId == userId }
    override suspend fun updateExpense(id: String, userId: String, request: com.expensetracker.models.ExpenseRequest): Boolean =
        updateExpense(id, request.amount, request.category.name, request.description, request.isRecurring, request.startDate, request.endDate)
    override suspend fun deleteExpense(id: String, userId: String): Boolean = deleteExpense(id)

    // IncomeRepository overrides
    override suspend fun createIncome(userId: String, request: com.expensetracker.models.IncomeRequest): String =
        createIncome(userId, request.amount, request.description, request.isRecurring, request.startDate, request.endDate)
    override suspend fun getIncomes(userId: String, startDate: LocalDate?, endDate: LocalDate?): List<com.expensetracker.models.IncomeResponse> =
        getAllIncomes(startDate, endDate).filter { it.userId == userId }
    override suspend fun updateIncome(id: String, userId: String, request: com.expensetracker.models.IncomeRequest): Boolean =
        updateIncome(id, request.amount, request.description, request.isRecurring, request.startDate, request.endDate)
    override suspend fun deleteIncome(id: String, userId: String): Boolean = deleteIncome(id)
}
