@file:kotlin.jvm.JvmName("AndroidDatabaseFactory")
package com.rarcega.controlgastos.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import com.rarcega.controlgastos.AndroidApp

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context = AndroidApp.instance
    val dbFile = context.getDatabasePath("controlgastos.db")
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}
