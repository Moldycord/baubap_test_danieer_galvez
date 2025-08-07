package com.baubaptest.features.onboarding.domain.usecase

import app.cash.turbine.test
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
class LogoutUseCaseTest {

    @Test
    fun `delegates to repository and re-emits success`() = runTest {
        val repo = mockk<LoginRepository>()
        val useCase = LogoutUseCase(repo)

        val upstream = flowOf(Result.success(Unit))
        every { repo.logout() } returns upstream


        useCase().test {
            assertTrue(awaitItem().isSuccess)
            awaitComplete()
        }


        verify(exactly = 1) { repo.logout() }
    }

    @Test
    fun `delegates to repository and re-emits failure`() = runTest {
        val repo = mockk<LoginRepository>()
        val useCase = LogoutUseCase(repo)

        val exception = Exception("Logout error")
        val upstream = flowOf(Result.failure<Unit>(exception))
        every { repo.logout() } returns upstream

        useCase().test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            awaitComplete()
        }

        verify(exactly = 1) { repo.logout() }
    }
}
