@file:kotlin.jvm.JvmName("CommonDatabaseFactory")
package com.rarcega.controlgastos.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

fun createDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(true)
        .build()
}
