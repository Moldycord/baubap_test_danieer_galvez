package com.baubaptest.features.onboarding.data.repository

import com.baubaptest.core.database.dao.UserDao
import com.baubaptest.core.database.entities.UserEntity
import com.baubaptest.core.model.User
import com.baubaptest.features.onboarding.domain.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val dao: UserDao
) : AccountRepository {

    override suspend fun registerAccount(
        name: String,
        email: String,
        nip: String,
        curp: String,
        phone: String
    ): Result<Unit> {
        return try {
            val account = UserEntity(
                name = name,
                email = email,
                token = nip,
                curp = curp,
                phone = phone
            )
            dao.insertUser(account)
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    override suspend fun emailExists(email: String): Boolean = withContext(Dispatchers.IO) {
        dao.existsByEmail(email)
    }

    override suspend fun curpExists(curp: String): Boolean = withContext(Dispatchers.IO) {
        dao.existsByCurp(curp)
    }

}