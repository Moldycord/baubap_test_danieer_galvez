package com.baubaptest.features.onboarding.domain.repository

import kotlinx.coroutines.flow.Flow
import com.baubaptest.core.model.CustomResult
import com.baubaptest.core.model.User

interface LoginRepository {

    /**
     * Login to the app returns a Flow with the states
     * **/
     fun login(email: String, password: String): Flow<CustomResult<Unit>>

    /**
     * Close the session and clear the data
     * **/
    fun logout() : Flow<Result<Unit>>

    /***
     * Observes the state of the authentified user
     * */
    fun observeUser() : Flow<CustomResult<User>>
}