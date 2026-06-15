package com.example.bingeboard.data.remote.api

import com.example.bingeboard.data.remote.dto.ReviewDto
import com.example.bingeboard.data.remote.dto.ReviewRequest
import retrofit2.http.*

interface ReviewApiService {
    @GET("reviews/{movieId}")
    suspend fun getReviews(@Path("movieId") movieId: String): List<ReviewDto>

    @POST("reviews")
    suspend fun addReview(
        @Header("Authorization") token: String,
        @Body review: ReviewRequest
    ): ReviewDto

    @DELETE("reviews/{reviewId}")
    suspend fun deleteReview(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: String
    )
}