package com.example.bingeboard.data.repository

import com.example.bingeboard.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthRepository @Inject constructor() : AuthRepository {

    private val users = mutableListOf(
        User(1, "Test User", "test@bingeboard.com", "test123")
    )

    private var currentUser: User? = null

    override suspend fun login(email: String, password: String): Result<User> {
        val user = users.find { it.email == email && it.passwordHash == password }
        return if (user != null) {
            currentUser = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password"))
        }
    }

    override suspend fun signup(fullName: String, email: String, password: String): Result<User> {
        if (users.any { it.email == email }) {
            return Result.failure(Exception("An account with this email already exists"))
        }
        val newUser = User(
            id = users.size + 1,
            fullName = fullName,
            email = email,
            passwordHash = password
        )
        users.add(newUser)
        return Result.success(newUser)
    }

    override suspend fun logout() {
        currentUser = null
    }

    override suspend fun getCurrentUser(): User? = currentUser

    override suspend fun isLoggedIn(): Boolean = currentUser != null
}
