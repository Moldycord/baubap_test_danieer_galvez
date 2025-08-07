package com.baubaptest.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.baubaptest.core.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity): Long

    @Query("""
    SELECT * FROM users 
    WHERE (phone = :identifier OR curp = :identifier) 
    AND token = :password 
    LIMIT 1
    """)
    fun getUserByCredentials(
        identifier: String,
        password: String
    ): UserEntity?

    @Query("SELECT COUNT(*) > 0 FROM users WHERE email = :email")
    fun existsByEmail(email: String): Boolean

    @Query("SELECT COUNT(*) > 0 FROM users WHERE curp = :curp")
    fun existsByCurp(curp: String): Boolean


    @Update
    fun updateUser(user: UserEntity): Int

    @Query("UPDATE users SET isLoggedIn = 0")
    fun logoutAll(): Int

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun observeLoggedInUser(): Flow<UserEntity?>
}