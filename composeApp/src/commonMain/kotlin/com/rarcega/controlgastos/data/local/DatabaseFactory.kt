package com.rarcega.controlgastos.data.local

import android.content.Context
import androidx.room.Room

expect fun createDatabase(context: Context): AppDatabase
