package com.example.cinerate.di

import com.example.cinerate.data.repository.AuthRepository
import com.example.cinerate.data.repository.MockAuthRepository
import com.example.cinerate.data.repository.MockMovieRepository
import com.example.cinerate.data.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        mockMovieRepository: MockMovieRepository
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        mockAuthRepository: MockAuthRepository
    ): AuthRepository
}
