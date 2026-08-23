package com.iftikar.outlier.core.network.di

import com.iftikar.outlier.core.network.AuthApiService
import com.iftikar.outlier.core.network.AuthApiServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiServiceModule {
    @Singleton
    @Binds
    abstract fun bindAuthApiService(impl: AuthApiServiceImpl): AuthApiService
}