package com.baubaptest.features.onboarding.presentation.views.home

import app.cash.turbine.test
import com.baubaptest.core.model.CustomResult
import com.baubaptest.core.model.UiState
import com.baubaptest.core.model.User
import com.baubaptest.features.onboarding.domain.usecase.LogoutUseCase
import com.baubaptest.features.onboarding.domain.usecase.ObserveLoggedInUserUseCase
import com.baubaptest.features.onboarding.presentation.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildVM(
        observeFlow: MutableSharedFlow<CustomResult<User>>? = null,
        logoutFlow: (() -> Any)? = null
    ): HomeViewModel {
        val observeUseCase = mockk<ObserveLoggedInUserUseCase>()
        val logoutUseCase = mockk<LogoutUseCase>()

        if (observeFlow != null) {
            every { observeUseCase.invoke() } returns observeFlow
        } else {

            every { observeUseCase.invoke() } returns emptyFlow()
        }

        if (logoutFlow != null) {
            @Suppress("UNCHECKED_CAST")
            every { logoutUseCase.invoke() } returns (logoutFlow.invoke() as kotlinx.coroutines.flow.Flow<Result<Unit>>)
        } else {
            every { logoutUseCase.invoke() } returns emptyFlow()
        }

        return HomeViewModel(observeUseCase, logoutUseCase)
    }

    @Test
    fun `loggedUserState initially emits Empty and then emits Success when there is a logged user`() = runTest {
        val upstream = MutableSharedFlow<CustomResult<User>>(replay = 0, extraBufferCapacity = 2)
        val vm = buildVM(observeFlow = upstream)

        vm.loggedUserState.test {

            val first = awaitItem()
            assertTrue(first is UiState.Empty)


            val user = User(1, "Ana", "ana@mail.com", "CURP", "555", true)
            upstream.tryEmit(CustomResult.Success(user))
            advanceUntilIdle()


            val next = awaitItem()
            assertTrue(next is UiState.Success)
            assertEquals(user, (next as UiState.Success<User>).data)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loggedUserState emits Error when observeUserUseCase produces Error`() = runTest {
        val upstream = MutableSharedFlow<CustomResult<User>>(replay = 0, extraBufferCapacity = 1)
        val vm = buildVM(observeFlow = upstream)

        vm.loggedUserState.test {
            assertTrue(awaitItem() is UiState.Empty)

            upstream.tryEmit(CustomResult.Error("User not logged"))
            advanceUntilIdle()

            val next = awaitItem()
            assertTrue(next is UiState.Error)
            assertEquals("User not logged", (next as UiState.Error).message)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `closeSession calls onLogout when logoutUseCase return success`() = runTest {

        val vm = buildVM(
            observeFlow = null,
            logoutFlow = { flowOf(Result.success(Unit)) }
        )

        var navigated = false

        vm.closeSession {
            navigated = true
        }

        advanceUntilIdle()
        assertTrue(navigated)
    }

    @Test
    fun `closeSession publishes UiState_Error when logoutUseCase returns failure`() = runTest {
        val vm = buildVM(
            observeFlow = null,
            logoutFlow = { flowOf(Result.failure<Unit>(IllegalStateException("boom"))) }
        )

        vm.loggedUserState.test {
            assertTrue(awaitItem() is UiState.Empty)

            vm.closeSession { /* No call here */ }
            advanceUntilIdle()

            val next = awaitItem()
            assertTrue(next is UiState.Error)
            assertEquals("boom", (next as UiState.Error).message)

            cancelAndConsumeRemainingEvents()
        }
    }
}