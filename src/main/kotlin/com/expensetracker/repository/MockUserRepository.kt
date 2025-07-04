package com.expensetracker.repository

import com.expensetracker.models.UserResponse
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MockUserRepository : UserRepository {
    private val users = mutableListOf<UserResponse>()
    private val passwords = mutableMapOf<String, String>() // username -> passwordHash
    private val mutex = Mutex()

    override suspend fun createUser(username: String, email: String, passwordHash: String): String = mutex.withLock {
        val id = UUID.randomUUID().toString()
        val user = UserResponse(
            id = id,
            username = username,
            email = email,
            createdAt = OffsetDateTime.now()
        )
        users.add(user)
        passwords[username] = passwordHash
        id
    }

    override suspend fun findUserByUsername(username: String): UserResponse? = mutex.withLock {
        users.find { it.username == username }
    }

    override suspend fun findUserById(id: String): UserResponse? = mutex.withLock {
        users.find { it.id == id }
    }

    override suspend fun findUserByUsernameWithPassword(username: String): Pair<String, String>? = mutex.withLock {
        val user = users.find { it.username == username } ?: return null
        val passwordHash = passwords[username] ?: return null
        Pair(user.id, passwordHash)
    }
} 