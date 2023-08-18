package com.udemy.a7minuteworkout

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HistoryDao {

    // Add Data
    @Insert
    // Function using coroutines to insert exercises
    suspend fun insert(historyEntity: HistoryEntity)

    //Query for all dates
    @Query("Select * from `history-table`")
    // Function using coroutines to retrieve all dates
    fun fetchAllDates():Flow<List<HistoryEntity>>
}