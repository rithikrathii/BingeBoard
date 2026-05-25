package com.example.bingeboard.data.repository

import com.example.bingeboard.data.model.User

interface AuthRepository {
    fun login(email: String, password: String): Result<User>
    fun signup(fullName: String, email: String, password: String): Result<User>
    fun logout()
    fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
}
