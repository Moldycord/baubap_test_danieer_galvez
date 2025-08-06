package com.baubaptest.core.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.baubaptest.core.database.entities.UserEntity
import com.baubaptest.core.database.dao.UserDao

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
