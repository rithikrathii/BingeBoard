package com.example.bingeboard.data.model

data class Review(
    val id: String = "",
    val movieId: String = "",
    val userId: String = "",
    val reviewerName: String = "",
    val reviewerInitials: String = "",
    val date: String = "",
    val rating: Int = 0,
    val comment: String = ""
)