package com.baubaptest.features.onboarding.data.repository

import com.baubaptest.core.database.dao.UserDao
import com.baubaptest.core.database.mappers.toDomain
import com.baubaptest.core.model.CustomResult
import com.baubaptest.core.model.User
import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : LoginRepository {

    override fun login(
        email: String,
        password: String
    ): Flow<CustomResult<Unit>> = flow {
        emit(CustomResult.Loading)

        try {
            val user = userDao.getUserByCredentials(email, password)
            user?.let {
                val updatedUser = it.copy(isLoggedIn = true)
                userDao.updateUser(updatedUser)
                emit(CustomResult.Success(Unit))
            }
                ?: emit(CustomResult.Error(message = "Usuario no encontrado, revisa tus credenciales"))
        } catch (error: Exception) {
            emit(CustomResult.Error(message = "Error while retrieving data", error))
        }
    }.flowOn(Dispatchers.IO)

    override fun logout(): Flow<Result<Unit>> = flow {
        try {
            userDao.logoutAll()
            emit(Result.success(Unit))
        } catch (error: Exception) {
            emit(Result.failure(error))
        }
    }.flowOn(Dispatchers.IO)

    override fun observeUser(): Flow<CustomResult<User>> =
        userDao.observeLoggedInUser().map { entity ->
            if (entity != null)
                CustomResult.Success(entity.toDomain())
            else CustomResult.Error(message = "User not logged")
        }.catch {
            emit(CustomResult.Error(message = "Error retrieving data", it))
        }.flowOn(Dispatchers.IO)

}