package com.baubaptest.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var email: String = "",
    var token: String = "",
    var curp: String = "",
    var phone: String = "",
    var isLoggedIn: Boolean = false
)