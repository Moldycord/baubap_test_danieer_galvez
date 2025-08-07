package com.baubaptest.features.onboarding.domain.usecase

import com.baubaptest.features.onboarding.domain.repository.AccountRepository
import javax.inject.Inject

class RegisterAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(
        email: String,
        curp: String,
        nip: String,
        phone: String,
        fullName: String
    ): Result<Unit> {

        if (accountRepository.emailExists(email)) {
            return Result.failure(Exception("Ya existe una cuenta con este correo electrónico"))
        }

        if (accountRepository.curpExists(curp)) {
            return Result.failure(Exception("Ya existe una cuenta con este CURP"))
        }

        return accountRepository.registerAccount(fullName, email, nip, curp, phone)
    }
}