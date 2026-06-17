package com.example.bingeboard.di

import com.example.bingeboard.data.repository.*
import com.example.bingeboard.data.remote.api.AuthApiService
import com.example.bingeboard.data.remote.api.MovieApiService
import com.example.bingeboard.data.remote.api.ReviewApiService
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Named
import javax.inject.Singleton

// This module tells Hilt how to build all our networking objects
// (Retrofit clients and API services). Everything here is a singleton,
// so the same instances are shared across the whole app.
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // A custom Gson parser that doesn't crash on bad data.
    // The movie dataset has some fields where a number is stored as a
    // broken string (e.g. year "1981è"), so we register custom handlers
    // for Int and Double that fall back to a safe default instead of throwing.
    private val safeGson = GsonBuilder()
        .registerTypeAdapter(Int::class.java, object : JsonDeserializer<Int> {
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Int {
                return try {
                    json.asInt // normal case: it's a clean number
                } catch (e: Exception) {
                    // otherwise strip out non-digits and try again, else 0
                    try {
                        val str = json.asString.filter { it.isDigit() }
                        if (str.isEmpty()) 0 else str.toInt()
                    } catch (e: Exception) { 0 }
                }
            }
        })
        .registerTypeAdapter(Double::class.java, object : JsonDeserializer<Double> {
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Double {
                return try {
                    json.asDouble
                } catch (e: Exception) { 0.0 }
            }
        })
        .create()

    // Retrofit client for the Movie Catalogue service (port 8001).
    // Uses our safeGson so corrupted movie data doesn't break parsing.
    // Note: 10.0.2.2 is how the Android emulator reaches the host machine's localhost.
    @Provides
    @Singleton
    @Named("movie")
    fun provideMovieRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8001/")
        .addConverterFactory(GsonConverterFactory.create(safeGson))
        .client(OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // log full requests/responses for debugging
            }).build())
        .build()

    // Retrofit client for the Auth service (port 8000). Uses the default
    // Gson since auth responses are clean and well-structured.
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
        .build()

    // Retrofit client for the Ratings & Reviews service (port 8002).
    @Provides
    @Singleton
    @Named("ratings")
    fun provideRatingsRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8002/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
        .build()

    // The next three turn each Retrofit client into a usable API service.
    // The @Named tags make sure each service gets the correct client.

    @Provides
    @Singleton
    fun provideMovieApiService(@Named("movie") retrofit: Retrofit): MovieApiService =
        retrofit.create(MovieApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("auth") retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideReviewApiService(@Named("ratings") retrofit: Retrofit): ReviewApiService =
        retrofit.create(ReviewApiService::class.java)
}

// This module connects our repository interfaces to their real
// implementations. Whenever something asks for a MovieRepository,
// Hilt hands it an ApiMovieRepository (and likewise for auth).
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        apiMovieRepository: ApiMovieRepository
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        apiAuthRepository: ApiAuthRepository
    ): AuthRepository
}