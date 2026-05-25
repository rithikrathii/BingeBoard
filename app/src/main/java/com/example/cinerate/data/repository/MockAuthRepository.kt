package com.example.cinerate.data.repository

import com.example.cinerate.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthRepository @Inject constructor() : AuthRepository {

    private val users = mutableListOf(
        User(1, "Test User", "test@bingeboard.com", "test123")
    )

    private var currentUser: User? = null

    override fun login(email: String, password: String): Result<User> {
        val user = users.find { it.email == email && it.passwordHash == password }
        return if (user != null) {
            currentUser = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password"))
        }
    }

    override fun signup(fullName: String, email: String, password: String): Result<User> {
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

    override fun logout() {
        currentUser = null
    }

    override fun getCurrentUser(): User? = currentUser

    override fun isLoggedIn(): Boolean = currentUser != null
}
