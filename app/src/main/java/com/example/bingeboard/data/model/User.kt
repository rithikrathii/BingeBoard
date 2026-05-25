package com.example.bingeboard.data.model

data class User(
    val id: Int,
    val fullName: String,
    val email: String,
    val passwordHash: String   // future: real hash; mock: store as-is
)
