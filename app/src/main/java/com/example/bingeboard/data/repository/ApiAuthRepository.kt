package com.example.bingeboard.data.repository

import com.example.bingeboard.data.local.TokenDataStore
import com.example.bingeboard.data.model.User
import com.example.bingeboard.data.remote.api.AuthApiService
import com.example.bingeboard.data.remote.dto.LoginRequest
import com.example.bingeboard.data.remote.dto.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiAuthRepository @Inject constructor(
    private val api: AuthApiService,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    private var currentUser: User? = null

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            tokenDataStore.saveToken(response.accessToken)
            val userResponse = api.getCurrentUser("Bearer ${response.accessToken}")
            val user = User(
                id = userResponse.id.toString(),
                fullName = userResponse.full_name ?: userResponse.email.substringBefore("@"),
                email = userResponse.email,
                passwordHash = ""
            )
            currentUser = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signup(fullName: String, email: String, password: String): Result<User> {
        return try {
            val response = api.register(RegisterRequest(
                email = email,
                password = password,
                full_name = fullName
            ))
            val user = User(
                id = response.id.toString(),
                fullName = fullName,
                email = response.email,
                passwordHash = ""
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
        currentUser = null
    }

    override suspend fun getCurrentUser(): User? {
        if (currentUser != null) return currentUser

        val token = tokenDataStore.getToken() ?: return null
        return try {
            val userResponse = api.getCurrentUser("Bearer $token")
            val user = User(
                id = userResponse.id.toString(),
                fullName = userResponse.full_name ?: userResponse.email.substringBefore("@"),
                email = userResponse.email,
                passwordHash = ""
            )
            currentUser = user
            user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        val token = tokenDataStore.getToken()
        return !token.isNullOrEmpty()
    }
}