package com.baubaptest.features.onboarding.data.repository

import com.baubaptest.core.database.dao.UserDao
import com.baubaptest.core.database.mappers.toDomain
import com.baubaptest.core.model.Result
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
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        try {
            val user = userDao.getUserByCredentials(email, password)
            user?.let {
                val updatedUser = it.copy(isLoggedIn = true)
                userDao.updateUser(updatedUser)
                emit(Result.Success(Unit))
            } ?:
            emit(Result.Error(message = "Usuario no encontrado, revisa tus credenciales"))
        } catch (error: Exception) {
            emit(Result.Error(message = "Error while retrieving data", error))
        }
    }.flowOn(Dispatchers.IO)

    override fun logout(): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            userDao.logoutAll()
            emit(Result.Success(Unit))
        } catch (error: Exception) {
            emit(Result.Error(message = "Error updating data", error))
        }
    }.flowOn(Dispatchers.IO)

    override fun observeUser(): Flow<Result<User>> =
        userDao.observeLoggedInUser().map { entity ->
            if (entity != null)
                Result.Success(entity.toDomain())
            else Result.Error(message = "User not logged")
        }.catch {
            emit(Result.Error(message = "Error retrieving data", it))
        }.flowOn(Dispatchers.IO)

}