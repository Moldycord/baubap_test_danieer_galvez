package com.baubaptest.features.onboarding.domain.repository

import com.baubaptest.core.model.User

interface AccountRepository {

   suspend fun registerAccount(
        name: String,
        email: String,
        nip: String,
        curp: String,
        phone: String
    ): Result<Unit>

    suspend fun emailExists(email: String): Boolean

    suspend fun curpExists(curp: String): Boolean

}