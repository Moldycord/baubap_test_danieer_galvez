package com.baubaptest.features.onboarding.domain.usecase

import app.cash.turbine.test
import com.baubaptest.core.model.CustomResult
import com.baubaptest.core.model.User
import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveLoggedInUserUseCaseTest {

    @Test
    fun `delegates to repository and emits the same values`() = runTest {
        val repo = mockk<LoginRepository>()
        val useCase = ObserveLoggedInUserUseCase(repo)

        val user = User(
            id = 1,
            name = "Ana",
            email = "ana@mail.com",
            curp = "BBBB800202MDFTRN01",
            phone = "5587654321",
            isLoggedIn = true
        )

        val upstream = flowOf<CustomResult<User>>(
            CustomResult.Success(user),
            CustomResult.Error("User not logged")
        )
        every { repo.observeUser() } returns upstream


        useCase().test {
            val first = awaitItem()
            assertTrue(first is CustomResult.Success)
            assertEquals("Ana", (first as CustomResult.Success).data.name)

            val second = awaitItem()
            assertTrue(second is CustomResult.Error)
            assertEquals("User not logged", (second as CustomResult.Error).message)

            awaitComplete()
        }


        verify(exactly = 1) { repo.observeUser() }
    }
}