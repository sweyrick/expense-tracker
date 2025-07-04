package com.expensetracker.repository

import com.expensetracker.models.IncomeRequest
import com.expensetracker.models.IncomeResponse
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MockIncomeRepository : IncomeRepository {
    private val incomes = mutableListOf<IncomeResponse>()
    private val mutex = Mutex()

    override suspend fun createIncome(userId: String, request: IncomeRequest): String = mutex.withLock {
        val id = UUID.randomUUID().toString()
        val income = IncomeResponse(
            id = id,
            amount = request.amount,
            description = request.description,
            isRecurring = request.isRecurring,
            startDate = request.startDate,
            endDate = request.endDate,
            userId = userId,
            username = "mockuser"
        )
        incomes.add(income)
        id
    }

    override suspend fun getIncomes(userId: String, startDate: LocalDate?, endDate: LocalDate?): List<IncomeResponse> = mutex.withLock {
        incomes.filter {
            it.userId == userId &&
            (startDate == null || !it.startDate.isBefore(startDate)) &&
            (endDate == null || !it.startDate.isAfter(endDate))
        }
    }

    override suspend fun updateIncome(id: String, userId: String, request: IncomeRequest): Boolean = mutex.withLock {
        val idx = incomes.indexOfFirst { it.id == id && it.userId == userId }
        if (idx == -1) return false
        incomes[idx] = incomes[idx].copy(
            amount = request.amount,
            description = request.description,
            isRecurring = request.isRecurring,
            startDate = request.startDate,
            endDate = request.endDate
        )
        true
    }

    override suspend fun deleteIncome(id: String, userId: String): Boolean = mutex.withLock {
        incomes.removeIf { it.id == id && it.userId == userId }
    }
} 