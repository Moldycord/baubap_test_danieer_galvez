package com.baubaptest.features.onboarding.presentation.views.register

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.baubaptest.features.onboarding.domain.usecase.RegisterAccountUseCase
import com.baubaptest.features.onboarding.presentation.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildVM(useCase: RegisterAccountUseCase): RegisterViewModel =
        RegisterViewModel(useCase)

    @Test
    fun `initial state is default and status Idle`() = runTest {
        val uc = mockk<RegisterAccountUseCase>()
        val vm = buildVM(uc)

        assertEquals(RegisterUiState(), vm.uiState.value)
        assertTrue(vm.registerStatus.value is RegisterStatus.Idle)
    }

    @Test
    fun `setters update state - fullName, email, phone(max10), curp(uppercase)`() = runTest {
        val uc = mockk<RegisterAccountUseCase>()
        val vm = buildVM(uc)

        vm.onFullNameChanged("Juan Pérez")
        vm.onEmailChanged("juan@mail.com")
        vm.onPhoneChanged("551234567890")
        vm.onCurpChanged("aaxx900101hdfrrn00")

        val s = vm.uiState.value
        assertEquals("Juan Pérez", s.fullName)
        assertEquals("juan@mail.com", s.email)
        assertEquals("5512345678", s.phone)
        assertEquals("AAXX900101HDFRRN00", s.curp)
    }

    @Test
    fun `onRegister emits Loading then Success and calls onSuccess`() = runTest {
        val uc = mockk<RegisterAccountUseCase>()
        coEvery {
            uc.invoke(
                email = "mail@x.com",
                curp = "CURP900101HDFRRN00",
                nip = "123456",
                phone = "5512345678",
                fullName = "Juan"
            )
        } returns Result.success(Unit)

        val vm = buildVM(uc)

        vm.onFullNameChanged("Juan")
        vm.onEmailChanged("mail@x.com")
        vm.onPhoneChanged("5512345678")
        vm.onCurpChanged("curp900101hdfrrn00")

        var successCalled = false

        turbineScope {
            vm.registerStatus.test {

                assertTrue(awaitItem() is RegisterStatus.Idle)

                vm.onRegister(nip = "123456") { successCalled = true }

                val loading = awaitItem()
                assertTrue(loading is RegisterStatus.Loading)

                val success = awaitItem()
                assertTrue(success is RegisterStatus.Success)

                assertTrue(successCalled)
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `onRegister emits Loading then Error with message`() = runTest {
        val uc = mockk<RegisterAccountUseCase>()
        coEvery { uc.invoke(any(), any(), any(), any(), any()) } returns
                Result.failure(Exception("Correo ya registrado"))

        val vm = buildVM(uc)
        vm.onFullNameChanged("Ana")
        vm.onEmailChanged("ana@mail.com")
        vm.onPhoneChanged("5599999999")
        vm.onCurpChanged("bbbb800202mdftrn01")

        turbineScope {
            vm.registerStatus.test {
                assertTrue(awaitItem() is RegisterStatus.Idle)

                vm.onRegister(nip = "654321") { fail("onSuccess no debe llamarse en Error") }

                val loading = awaitItem()
                assertTrue(loading is RegisterStatus.Loading)

                val error = awaitItem()
                assertTrue(error is RegisterStatus.Error)
                assertEquals("Correo ya registrado", (error as RegisterStatus.Error).message)

                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `resetStatus sets status back to Idle`() = runTest {
        val uc = mockk<RegisterAccountUseCase>()
        coEvery { uc.invoke(any(), any(), any(), any(), any()) } returns
                Result.failure(Exception("X"))

        val vm = buildVM(uc)

        vm.registerStatus.test {

            assertTrue(awaitItem() is RegisterStatus.Idle)

            vm.onRegister("123456") {  }

            assertTrue(awaitItem() is RegisterStatus.Loading)
            assertTrue(awaitItem() is RegisterStatus.Error)

            vm.resetStatus()
            assertTrue(awaitItem() is RegisterStatus.Idle)

            cancelAndConsumeRemainingEvents()
        }
    }
}
