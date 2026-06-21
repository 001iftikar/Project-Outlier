package com.iftikar.outlier.core.data.di

import com.iftikar.outlier.core.data.repository.AuthRepositoryImpl
import com.iftikar.outlier.core.data.repository.UserProfileRepositoryImpl
import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.core.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository
}