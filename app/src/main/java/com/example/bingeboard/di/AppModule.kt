package com.example.bingeboard.di

import com.example.bingeboard.data.repository.*
import com.example.bingeboard.data.remote.api.AuthApiService
import com.example.bingeboard.data.remote.api.MovieApiService
import com.example.bingeboard.data.remote.api.ReviewApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("movie")
    fun provideMovieRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8001/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
        .build()

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