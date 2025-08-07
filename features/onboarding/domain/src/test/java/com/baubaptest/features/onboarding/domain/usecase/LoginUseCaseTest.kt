package com.baubaptest.features.onboarding.domain.usecase

import app.cash.turbine.test
import com.baubaptest.core.model.CustomResult
import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginUseCaseTest {

    @Test
    fun `delegates to repository and re-emits values`() = runTest {
        // given
        val repo = mockk<LoginRepository>()
        val useCase = LoginUseCase(repo)

        val email = "user@mail.com"
        val password = "123456"

        val upstream = flowOf(
            CustomResult.Loading,
            CustomResult.Success(Unit)
        )
        every { repo.login(email, password) } returns upstream

        // when & then
        useCase(email, password).test {
            assertTrue(awaitItem() is CustomResult.Loading)
            assertTrue(awaitItem() is CustomResult.Success)
            awaitComplete()
        }


        verify(exactly = 1) { repo.login(email, password) }
    }

    @Test
    fun `re-emits error from repository`() = runTest {
        val repo = mockk<LoginRepository>()
        val useCase = LoginUseCase(repo)

        val email = "bad@mail.com"
        val password = "000000"

        val upstream = flowOf<CustomResult<Unit>>(
            CustomResult.Loading,
            CustomResult.Error("Credenciales inválidas")
        )
        every { repo.login(email, password) } returns upstream

        useCase(email, password).test {
            assertTrue(awaitItem() is CustomResult.Loading)
            val err = awaitItem()
            assertTrue(err is CustomResult.Error)
            awaitComplete()
        }

        verify(exactly = 1) { repo.login(email, password) }
    }
}