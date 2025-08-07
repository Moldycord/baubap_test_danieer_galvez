package com.baubaptest.features.onboarding.presentation.views.login

import app.cash.turbine.test
import com.baubaptest.core.model.CustomResult
import com.baubaptest.core.model.UiState
import com.baubaptest.features.onboarding.domain.usecase.LoginUseCase
import com.baubaptest.features.onboarding.presentation.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state is Empty`() = runTest {
        val useCase = mockk<LoginUseCase>()
        every { useCase.invoke(any(), any()) } returns flowOf()
        val vm = LoginViewModel(useCase)

        vm.loginState.test {
            val first = awaitItem()
            assertTrue(first is UiState.Empty)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `login maps Loading then Success`() = runTest {
        val useCase = mockk<LoginUseCase>()
        val upstream = flowOf(
            CustomResult.Loading,
            CustomResult.Success(Unit)
        )
        every { useCase.invoke("user@mail.com", "123456") } returns upstream

        val vm = LoginViewModel(useCase)

        vm.loginState.test {
            assertTrue(awaitItem() is UiState.Empty)

            vm.login("user@mail.com", "123456")
            advanceUntilIdle()

            val v2 = awaitItem()
            assertTrue(v2 is UiState.Success && (v2 as UiState.Success<Unit>).data == Unit)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `login maps Error`() = runTest {
        val useCase = mockk<LoginUseCase>()
        val upstream = flowOf(
            CustomResult.Loading,
            CustomResult.Error("Credenciales inválidas")
        )
        every { useCase.invoke("bad@mail.com", "000000") } returns upstream

        val vm = LoginViewModel(useCase)

        vm.loginState.test {
            assertTrue(awaitItem() is UiState.Empty)

            vm.login("bad@mail.com", "000000")
            advanceUntilIdle()

            val after1 = awaitItem()
            assertTrue(after1 is UiState.Error)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `resetLoginState sets state back to Empty`() = runTest {
        val useCase = mockk<LoginUseCase>()

        val shared = MutableSharedFlow<CustomResult<Unit>>(replay = 0, extraBufferCapacity = 2)
        every { useCase.invoke(any(), any()) } returns shared

        val vm = LoginViewModel(useCase)

        vm.loginState.test {

            assertTrue(awaitItem() is UiState.Empty)

            vm.login("a@b.com", "123456")
            shared.tryEmit(CustomResult.Loading)
            advanceUntilIdle()


            val loading = awaitItem()
            assertTrue(loading is UiState.Loading)

            vm.resetLoginState()
            val emptyAgain = awaitItem()
            assertTrue(emptyAgain is UiState.Empty)

            cancelAndConsumeRemainingEvents()
        }
    }
}
