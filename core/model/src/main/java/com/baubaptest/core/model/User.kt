package com.baubaptest.core.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val curp: String,
    val phone: String,
    val isLoggedIn: Boolean = false
)
