package com.example.bingeboard.data.model

data class Review(
    val id: Int,
    val movieId: Int,
    val reviewerName: String,
    val reviewerInitials: String,   // e.g. "MK"
    val date: String,               // e.g. "Dec 12, 2023"
    val rating: Int,                // out of 5
    val comment: String
)
