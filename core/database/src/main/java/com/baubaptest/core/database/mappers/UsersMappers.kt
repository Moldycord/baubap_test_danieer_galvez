package com.baubaptest.core.database.mappers

import com.baubaptest.core.database.entities.UserEntity
import com.baubaptest.core.model.User

fun UserEntity.toDomain() : User = User(
    id = id,
    name = name,
    email = email,
    isLoggedIn = isLoggedIn
)