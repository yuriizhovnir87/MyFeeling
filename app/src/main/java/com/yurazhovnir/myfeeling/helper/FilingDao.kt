package com.yurazhovnir.myfeeling.helper

import androidx.room.*
import com.yurazhovnir.myfeeling.model.Filing

@Dao
interface FilingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiling(filing: Filing)

    @Query("SELECT * FROM filing")
    suspend fun getAllFilings(): List<Filing>
}