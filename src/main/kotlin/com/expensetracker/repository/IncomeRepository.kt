package com.expensetracker.repository

import com.expensetracker.models.IncomeRequest
import com.expensetracker.models.IncomeResponse
import java.time.LocalDate

interface IncomeRepository {
    suspend fun createIncome(userId: String, request: IncomeRequest): String
    suspend fun getIncomes(userId: String, startDate: LocalDate? = null, endDate: LocalDate? = null): List<IncomeResponse>
    suspend fun updateIncome(id: String, userId: String, request: IncomeRequest): Boolean
    suspend fun deleteIncome(id: String, userId: String): Boolean
} 