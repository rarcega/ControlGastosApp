package com.rarcega.controlgastos.data.local

import android.content.Context
import androidx.room.Room

actual fun createDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "controlgastos.db"
    ).build()
}
