package com.example.bingeboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type")   val tokenType: String
)

data class UserResponse(
    @SerializedName("id")         val id: Int,
    @SerializedName("email")      val email: String,
    @SerializedName("role")       val role: String,
    @SerializedName("is_active")  val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String
)

data class RegisterResponse(
    @SerializedName("id")         val id: Int,
    @SerializedName("email")      val email: String,
    @SerializedName("role")       val role: String,
    @SerializedName("is_active")  val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String
)
