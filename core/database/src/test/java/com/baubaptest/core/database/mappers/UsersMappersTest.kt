package com.baubaptest.core.database.mappers

import com.baubaptest.core.database.entities.UserEntity
import com.baubaptest.core.model.User
import junit.framework.TestCase.assertEquals
import kotlin.test.Test

class UsersMappersTest {

    @Test
    fun `toDomain maps all fields correctly`() {

        val userEntity = UserEntity(
            id = Math.random().toInt(),
            name = "John Doe",
            email = "john@example.com",
            curp = "CURP123456",
            phone = "5551234567",
            token = "1234",
            isLoggedIn = true
        )


        val domainUser: User = userEntity.toDomain()


        assertEquals(userEntity.id, domainUser.id)
        assertEquals(userEntity.name, domainUser.name)
        assertEquals(userEntity.email, domainUser.email)
        assertEquals(userEntity.curp, domainUser.curp)
        assertEquals(userEntity.phone, domainUser.phone)
        assertEquals(userEntity.isLoggedIn, domainUser.isLoggedIn)
    }
}