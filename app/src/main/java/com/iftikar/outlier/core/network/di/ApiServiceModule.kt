package com.iftikar.outlier.core.network.di

import com.iftikar.outlier.core.network.api.AuthApiService
import com.iftikar.outlier.core.network.api.UserApiService
import com.iftikar.outlier.core.network.impl.AuthApiServiceImpl
import com.iftikar.outlier.core.network.impl.UserApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiServiceModule {
    @Singleton
    @Binds
    abstract fun bindAuthApiService(impl: AuthApiServiceImpl): AuthApiService

    @Singleton
    @Binds
    abstract fun bindUserApiService(impl: UserApiServiceImpl): UserApiService
}