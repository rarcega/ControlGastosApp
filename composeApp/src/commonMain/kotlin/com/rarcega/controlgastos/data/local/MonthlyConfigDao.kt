package com.rarcega.controlgastos.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyConfigDao {
    @Query("SELECT * FROM monthly_config WHERE month = :month AND year = :year")
    fun getConfig(month: Int, year: Int): Flow<MonthlyConfigEntity?>

    @Query("SELECT * FROM monthly_config WHERE month = :month AND year = :year")
    suspend fun getConfigOnce(month: Int, year: Int): MonthlyConfigEntity?

    @Upsert
    suspend fun upsertConfig(config: MonthlyConfigEntity)
}
