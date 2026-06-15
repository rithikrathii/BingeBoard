package com.example.bingeboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email")     val email: String,
    @SerializedName("password")  val password: String,
    @SerializedName("full_name") val full_name: String? = null
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type")   val tokenType: String
)

data class UserResponse(
    @SerializedName("id")         val id: Int,
    @SerializedName("email")      val email: String,
    @SerializedName("full_name")  val full_name: String? = null,
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

data class ReviewDto(
    val id: String = "",
    val movie_id: String = "",
    val user_id: String = "",
    val user_name: String = "",
    val text: String = "",
    val rating: Int = 0,
    val created_at: String = "",
    val updated_at: String = ""
)

data class ReviewRequest(
    val movie_id: String,
    val text: String,
    val user_name: String = "",
    val rating: Int = 0
)