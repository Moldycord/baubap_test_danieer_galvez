package com.baubatest.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.baubaptest.core.database.dao.UserDao
import com.baubaptest.core.database.database.AppDatabase
import com.baubaptest.core.database.entities.UserEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.userDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertUser_and_getByCredentials_success() = runTest {
        val u = UserEntity(
            id = 0,
            name = "Juan Pérez",
            email = "juan@mail.com",
            token = "123456",
            curp = "AAAA900101HDFRRN00",
            phone = "5512345678",
            isLoggedIn = false
        )
        dao.insertUser(u)

        val found = dao.getUserByCredentials("5512345678", "123456")
        assertNotNull(found)
        assertEquals("juan@mail.com", found?.email)

        val foundByCurp = dao.getUserByCredentials("AAAA900101HDFRRN00", "123456")
        assertNotNull(foundByCurp)
        assertEquals("Juan Pérez", foundByCurp?.name)
    }

    @Test
    fun existsByEmail_and_existsByCurp_work() {
        val u = UserEntity(
            id = 0,
            name = "Ana",
            email = "ana@mail.com",
            token = "654321",
            curp = "BBBB800202MDFTRN01",
            phone = "5587654321",
            isLoggedIn = false
        )
        dao.insertUser(u)

        assertTrue(dao.existsByEmail("ana@mail.com"))
        assertFalse(dao.existsByEmail("no@mail.com"))

        assertTrue(dao.existsByCurp("BBBB800202MDFTRN01"))
        assertFalse(dao.existsByCurp("XXXX000000XXXXXX00"))
    }

    @Test
    fun updateUser_setsLoggedIn_and_observeLoggedInUser_emits() = runTest {
        val u = UserEntity(
            id = 0,
            name = "Carlos",
            email = "carlos@mail.com",
            token = "111111",
            curp = "CCCC700303HDFABC02",
            phone = "5599999999",
            isLoggedIn = false
        )
        val newId = dao.insertUser(u).toInt()


        val updated = u.copy(id = newId, isLoggedIn = true)
        val rows = dao.updateUser(updated)
        assertEquals(1, rows)


        val logged = dao.observeLoggedInUser().first()
        assertNotNull(logged)
        assertEquals("Carlos", logged?.name)
        assertTrue(logged!!.isLoggedIn)
    }

    @Test
    fun logoutAll_clearsLoggedInFlag() = runTest {
        val a = UserEntity(0, "A", "a@mail.com", "111111", "AAAA900101HDFRRN00", "5511111111", true)
        val b =
            UserEntity(0, "B", "b@mail.com", "222222", "BBBB900101HDFRRN00", "5522222222", false)
        dao.insertUser(a)
        dao.insertUser(b)

        val affected = dao.logoutAll()
        assertTrue(affected >= 1)

        val logged = dao.observeLoggedInUser().first()
        assertNull(logged)
    }
}