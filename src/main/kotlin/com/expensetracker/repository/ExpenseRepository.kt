package com.expensetracker.repository

import com.expensetracker.models.ExpenseRequest
import com.expensetracker.models.ExpenseResponse
import java.time.LocalDate

interface ExpenseRepository {
    suspend fun createExpense(userId: String, request: ExpenseRequest): String
    suspend fun getExpenses(userId: String, startDate: LocalDate? = null, endDate: LocalDate? = null): List<ExpenseResponse>
    suspend fun updateExpense(id: String, userId: String, request: ExpenseRequest): Boolean
    suspend fun deleteExpense(id: String, userId: String): Boolean
} 