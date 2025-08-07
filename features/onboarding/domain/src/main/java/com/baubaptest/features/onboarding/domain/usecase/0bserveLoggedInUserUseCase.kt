package com.baubaptest.features.onboarding.domain.usecase

import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import javax.inject.Inject

class ObserveLoggedInUserUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    operator fun invoke() = loginRepository.observeUser()
}