package com.baubaptest.features.onboarding.data.repository

import com.baubaptest.core.database.dao.UserDao
import com.baubaptest.core.database.entities.UserEntity
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountRepositoryImplTest {

    private lateinit var dao: UserDao
    private lateinit var repository: AccountRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = AccountRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `registerAccount returns success when insertUser succeeds`() = runTest {

        every { dao.insertUser(any<UserEntity>()) } returns 1L


        val result = repository.registerAccount(
            name = "Juan",
            email = "juan@mail.com",
            nip = "123456",
            curp = "AAAA900101HDFRRN00",
            phone = "5512345678"
        )


        assertTrue(result.isSuccess)
        verify(exactly = 1) { dao.insertUser(match {
            it.name == "Juan" &&
                    it.email == "juan@mail.com" &&
                    it.token == "123456" &&
                    it.curp == "AAAA900101HDFRRN00" &&
                    it.phone == "5512345678"
        }) }
        confirmVerified(dao)
    }

    @Test
    fun `registerAccount returns failure when insertUser throws`() = runTest {

        every { dao.insertUser(any<UserEntity>()) } throws RuntimeException("db error")

        val result = repository.registerAccount(
            name = "Ana",
            email = "ana@mail.com",
            nip = "654321",
            curp = "BBBB800202MDFTRN01",
            phone = "5587654321"
        )


        assertTrue(result.isFailure)
        assertEquals("db error", result.exceptionOrNull()?.message)
        verify(exactly = 1) { dao.insertUser(any<UserEntity>()) }
        confirmVerified(dao)
    }

    @Test
    fun `emailExists delegates to dao and returns value`() = runTest {

        every { dao.existsByEmail("exists@mail.com") } returns true
        every { dao.existsByEmail("no@mail.com") } returns false


        val a = repository.emailExists("exists@mail.com")
        val b = repository.emailExists("no@mail.com")


        assertTrue(a)
        assertFalse(b)
        verify(exactly = 1) { dao.existsByEmail("exists@mail.com") }
        verify(exactly = 1) { dao.existsByEmail("no@mail.com") }
        confirmVerified(dao)
    }

    @Test
    fun `curpExists delegates to dao and returns value`() = runTest {

        every { dao.existsByCurp("AAAA900101HDFRRN00") } returns true
        every { dao.existsByCurp("XXXX000000XXXXXX00") } returns false


        val a = repository.curpExists("AAAA900101HDFRRN00")
        val b = repository.curpExists("XXXX000000XXXXXX00")


        assertTrue(a)
        assertFalse(b)
        verify(exactly = 1) { dao.existsByCurp("AAAA900101HDFRRN00") }
        verify(exactly = 1) { dao.existsByCurp("XXXX000000XXXXXX00") }
        confirmVerified(dao)
    }
}