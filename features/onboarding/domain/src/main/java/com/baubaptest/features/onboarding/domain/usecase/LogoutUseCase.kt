package com.baubaptest.features.onboarding.domain.usecase

import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {

    operator fun invoke(): Flow<Result<Unit>> {
        return loginRepository.logout()
    }
}