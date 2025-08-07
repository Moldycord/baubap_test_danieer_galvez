package com.baubaptest.features.onboarding.data.repository

import app.cash.turbine.test
import com.baubaptest.core.database.dao.UserDao
import com.baubaptest.core.database.entities.UserEntity
import com.baubaptest.core.model.CustomResult
import com.baubaptest.core.model.User
import com.baubaptest.features.onboarding.domain.repository.LoginRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.mockk.verifyOrder
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginRepositoryImplTest {

    private lateinit var dao: UserDao
    private lateinit var repository: LoginRepository

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = LoginRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    @Test
    fun `login emits Loading then Success when credentials are valid`() = runTest {
        val user = UserEntity(
            id = 1,
            name = "Juan",
            email = "juan@mail.com",
            token = "123456",
            curp = "AAAA900101HDFRRN00",
            phone = "5512345678",
            isLoggedIn = false
        )
        every { dao.getUserByCredentials("5512345678", "123456") } returns user
        every { dao.updateUser(user.copy(isLoggedIn = true)) } returns 1

        repository.login("5512345678", "123456").test {

            assertTrue(awaitItem() is CustomResult.Loading)

            val success = awaitItem()
            assertTrue(success is CustomResult.Success)
            awaitComplete()
        }

        verifyOrder {
            dao.getUserByCredentials("5512345678", "123456")
            dao.updateUser(match { it.isLoggedIn })
        }
    }

    @Test
    fun `login emits Loading then Error when user not found`() = runTest {
        every { dao.getUserByCredentials("nope", "000000") } returns null

        repository.login("nope", "000000").test {
            assertTrue(awaitItem() is CustomResult.Loading)
            val error = awaitItem()
            assertTrue(error is CustomResult.Error)
            assertEquals(
                "Usuario no encontrado, revisa tus credenciales",
                (error as CustomResult.Error).message
            )
            awaitComplete()
        }

        verify(exactly = 1) { dao.getUserByCredentials("nope", "000000") }
        verify(exactly = 0) { dao.updateUser(any()) }
    }

    @Test
    fun `login emits Loading then Error when dao throws`() = runTest {
        every { dao.getUserByCredentials(any(), any()) } throws RuntimeException("db crash")

        repository.login("x", "y").test {
            assertTrue(awaitItem() is CustomResult.Loading)
            val error = awaitItem()
            assertTrue(error is CustomResult.Error)
            assertEquals("Error while retrieving data", (error as CustomResult.Error).message)
            assertEquals("db crash", error.throwable?.message)
            awaitComplete()
        }

        verify(exactly = 1) { dao.getUserByCredentials("x", "y") }
        verify(exactly = 0) { dao.updateUser(any()) }
    }


    @Test
    fun `logout emits Result success when dao completes`() = runTest {
        every { dao.logoutAll() } returns 1

        repository.logout().test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            awaitComplete()
        }

        verify(exactly = 1) { dao.logoutAll() }
    }

    @Test
    fun `logout emits Result failure when dao throws`() = runTest {
        every { dao.logoutAll() } throws IllegalStateException("no db")

        repository.logout().test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("no db", result.exceptionOrNull()?.message)
            awaitComplete()
        }

        verify(exactly = 1) { dao.logoutAll() }
    }


    @Test
    fun `observeUser emits Success when dao emits logged in entity`() = runTest {
        val entity = UserEntity(
            id = 1,
            name = "Ana",
            email = "ana@mail.com",
            token = "222222",
            curp = "BBBB800202MDFTRN01",
            phone = "5587654321",
            isLoggedIn = true
        )
        every { dao.observeLoggedInUser() } returns flowOf(entity)

        repository.observeUser().test {
            val item = awaitItem()
            assertTrue(item is CustomResult.Success)
            val user = (item as CustomResult.Success<User>).data
            assertEquals("Ana", user.name)
            assertEquals("ana@mail.com", user.email)
            assertEquals(true, user.isLoggedIn)
            cancelAndConsumeRemainingEvents()
        }

        verify(exactly = 1) { dao.observeLoggedInUser() }
    }

    @Test
    fun `observeUser emits Error when dao emits null`() = runTest {
        every { dao.observeLoggedInUser() } returns flowOf(null)

        repository.observeUser().test {
            val item = awaitItem()
            assertTrue(item is CustomResult.Error)
            assertEquals("User not logged", (item as CustomResult.Error).message)
            cancelAndConsumeRemainingEvents()
        }

        verify(exactly = 1) { dao.observeLoggedInUser() }
    }

    @Test
    fun `observeUser emits Error when upstream throws`() = runTest {
        every { dao.observeLoggedInUser() } returns flow {
            throw RuntimeException("flow boom")
        }

        repository.observeUser().test {
            val item = awaitItem()
            assertTrue(item is CustomResult.Error)
            assertEquals("Error retrieving data", (item as CustomResult.Error).message)
            assertEquals("flow boom", item.throwable?.message)
            cancelAndConsumeRemainingEvents()
        }

        verify(exactly = 1) { dao.observeLoggedInUser() }
    }
}