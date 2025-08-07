package com.baubaptest.features.onboarding.presentation.views

import app.cash.turbine.test
import com.baubaptest.core.model.CustomResult
import com.baubaptest.core.model.UiState
import com.baubaptest.core.model.User
import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import com.baubaptest.features.onboarding.presentation.MainDispatcherRule
import com.baubaptest.features.onboarding.presentation.views.splash.SplashViewModel
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `authState become Success(true) when repo emits Success`() = runTest {  val upstream = MutableSharedFlow<CustomResult<User>>(replay = 0, extraBufferCapacity = 2)
        val repo = mockk<LoginRepository>()
        every { repo.observeUser() } returns upstream

        val vm = SplashViewModel(repo)

        vm.authState.test {

            assertTrue(awaitItem() is UiState.Loading)


            upstream.tryEmit(
                CustomResult.Success(User(1,"Ana","ana@mail.com","CURP","555", true))
            )
            advanceUntilIdle()

            val next = awaitItem()
            assertTrue(next is UiState.Success && next.data)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `authState becomes Success(false) when repo emits Error`() = runTest {
        val upstream = MutableSharedFlow<CustomResult<User>>(replay = 0, extraBufferCapacity = 1)
        val repo = mockk<LoginRepository>()
        every { repo.observeUser() } returns upstream

        val vm = SplashViewModel(repo)

        vm.authState.test {
            assertTrue(awaitItem() is UiState.Loading)

            upstream.tryEmit(CustomResult.Error("User not logged"))
            advanceUntilIdle()

            val next = awaitItem()
            assertTrue(next is UiState.Success && !next.data)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `authState becomes Success(false) when repo throws exception`() = runTest {

        val upstream = MutableSharedFlow<CustomResult<User>>(replay = 0)
        val repo = mockk<LoginRepository>()

        every { repo.observeUser() } returns upstream

        val vm = SplashViewModel(repo)

        vm.authState.test {
            assertTrue(awaitItem() is UiState.Loading)
            cancelAndConsumeRemainingEvents()
        }
    }
}