package com.expensetracker.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object Users : UUIDTable() {
    val username = varchar("username", 100).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = datetime("created_at")
}

object Incomes : UUIDTable() {
    val userId = reference("user_id", Users.id)
    val amount = decimal("amount", 10, 2)
    val description = varchar("description", 255)
    val isRecurring = bool("is_recurring")
    val startDate = date("start_date")
    val endDate = date("end_date").nullable()
}

object Expenses : UUIDTable() {
    val userId = reference("user_id", Users.id)
    val amount = decimal("amount", 10, 2)
    val category = varchar("category", 50)
    val description = varchar("description", 255)
    val isRecurring = bool("is_recurring")
    val startDate = date("start_date")
    val endDate = date("end_date").nullable()
}
