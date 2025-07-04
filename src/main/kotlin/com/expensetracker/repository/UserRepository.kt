package com.expensetracker.repository

import com.expensetracker.models.UserResponse

interface UserRepository {
    suspend fun createUser(username: String, email: String, passwordHash: String): String
    suspend fun findUserByUsername(username: String): UserResponse?
    suspend fun findUserById(id: String): UserResponse?
    suspend fun findUserByUsernameWithPassword(username: String): Pair<String, String>?
} 