package com.expensetracker.repository

import com.expensetracker.models.ExpenseRequest
import com.expensetracker.models.ExpenseResponse
import com.expensetracker.models.ExpenseCategory
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MockExpenseRepository : ExpenseRepository {
    private val expenses = mutableListOf<ExpenseResponse>()
    private val mutex = Mutex()

    override suspend fun createExpense(userId: String, request: ExpenseRequest): String = mutex.withLock {
        val id = UUID.randomUUID().toString()
        val expense = ExpenseResponse(
            id = id,
            amount = request.amount,
            category = request.category,
            description = request.description,
            isRecurring = request.isRecurring,
            startDate = request.startDate,
            endDate = request.endDate,
            userId = userId,
            username = "mockuser"
        )
        expenses.add(expense)
        id
    }

    override suspend fun getExpenses(userId: String, startDate: LocalDate?, endDate: LocalDate?): List<ExpenseResponse> = mutex.withLock {
        expenses.filter {
            it.userId == userId &&
            (startDate == null || !it.startDate.isBefore(startDate)) &&
            (endDate == null || !it.startDate.isAfter(endDate))
        }
    }

    override suspend fun updateExpense(id: String, userId: String, request: ExpenseRequest): Boolean = mutex.withLock {
        val idx = expenses.indexOfFirst { it.id == id && it.userId == userId }
        if (idx == -1) return false
        expenses[idx] = expenses[idx].copy(
            amount = request.amount,
            category = request.category,
            description = request.description,
            isRecurring = request.isRecurring,
            startDate = request.startDate,
            endDate = request.endDate
        )
        true
    }

    override suspend fun deleteExpense(id: String, userId: String): Boolean = mutex.withLock {
        expenses.removeIf { it.id == id && it.userId == userId }
    }
} 