package com.baubaptest.features.onboarding.domain.usecase

import com.baubaptest.core.model.CustomResult
import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    operator fun invoke(email: String, password: String): Flow<CustomResult<Unit>> {
        return repository.login(email, password)
    }
}
