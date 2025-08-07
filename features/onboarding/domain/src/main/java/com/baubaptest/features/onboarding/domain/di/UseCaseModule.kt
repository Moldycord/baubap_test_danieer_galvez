package com.baubaptest.features.onboarding.domain.di

import com.baubaptest.features.onboarding.domain.repository.AccountRepository
import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import com.baubaptest.features.onboarding.domain.usecase.LoginUseCase
import com.baubaptest.features.onboarding.domain.usecase.RegisterAccountUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideRegisterAccountUseCase(
        repository: AccountRepository
    ) = RegisterAccountUseCase(repository)

    @Provides
    fun providesLoginUseCase(
        repository: LoginRepository
    ) = LoginUseCase(repository)
}