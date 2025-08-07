package com.baubaptest.features.onboarding.domain.usecase

import com.baubaptest.features.onboarding.domain.repository.AccountRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterAccountUseCaseTest {

    private lateinit var repository: AccountRepository
    private lateinit var useCase: RegisterAccountUseCase

    private val email = "juan@mail.com"
    private val curp = "AAAA900101HDFRRN00"
    private val nip = "123456"
    private val phone = "5512345678"
    private val fullName = "Juan Pérez"

    @Before
    fun setup() {
        repository = mockk()
        useCase = RegisterAccountUseCase(repository)
    }

    @After
    fun tearDown() {
        confirmVerified(repository)
    }

    @Test
    fun `returns failure when email already exists`() = runTest {

        coEvery { repository.emailExists(email) } returns true


        val result = useCase(
            email = email,
            curp = curp,
            nip = nip,
            phone = phone,
            fullName = fullName
        )


        assertTrue(result.isFailure)
        assertEquals(
            "Ya existe una cuenta con este correo electrónico",
            result.exceptionOrNull()?.message
        )

        coVerify(exactly = 1) { repository.emailExists(email) }
        coVerify(exactly = 0) { repository.curpExists(any()) }
        coVerify(exactly = 0) { repository.registerAccount(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `returns failure when curp already exists`() = runTest {

        coEvery { repository.emailExists(email) } returns false
        coEvery { repository.curpExists(curp) } returns true


        val result = useCase(
            email = email,
            curp = curp,
            nip = nip,
            phone = phone,
            fullName = fullName
        )


        assertTrue(result.isFailure)
        assertEquals(
            "Ya existe una cuenta con este CURP",
            result.exceptionOrNull()?.message
        )

        coVerify(exactly = 1) { repository.emailExists(email) }
        coVerify(exactly = 1) { repository.curpExists(curp) }
        coVerify(exactly = 0) { repository.registerAccount(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `returns success when neither email nor curp exists and repository registers`() = runTest {

        coEvery { repository.emailExists(email) } returns false
        coEvery { repository.curpExists(curp) } returns false
        coEvery { repository.registerAccount(fullName, email, nip, curp, phone) } returns Result.success(Unit)


        val result = useCase(
            email = email,
            curp = curp,
            nip = nip,
            phone = phone,
            fullName = fullName
        )

        assertTrue(result.isSuccess)

        coVerify(exactly = 1) { repository.emailExists(email) }
        coVerify(exactly = 1) { repository.curpExists(curp) }
        coVerify(exactly = 1) { repository.registerAccount(fullName, email, nip, curp, phone) }
    }

    @Test
    fun `returns failure when repository registerAccount fails`() = runTest {

        coEvery { repository.emailExists(email) } returns false
        coEvery { repository.curpExists(curp) } returns false
        coEvery { repository.registerAccount(fullName, email, nip, curp, phone) } returns
                Result.failure(IllegalStateException("DB error"))


        val result = useCase(
            email = email,
            curp = curp,
            nip = nip,
            phone = phone,
            fullName = fullName
        )


        assertTrue(result.isFailure)
        assertEquals("DB error", result.exceptionOrNull()?.message)

        coVerify(exactly = 1) { repository.emailExists(email) }
        coVerify(exactly = 1) { repository.curpExists(curp) }
        coVerify(exactly = 1) { repository.registerAccount(fullName, email, nip, curp, phone) }
    }
}