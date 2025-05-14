package com.yurazhovnir.myfeeling.helper

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yurazhovnir.myfeeling.model.Filing
import com.yurazhovnir.myfeeling.model.User

@Database(entities = [Filing::class, User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun filingDao(): FilingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_feeling.db"
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private fun clearDatabaseFile(context: Context) {
            try {
                val dbFile = context.getDatabasePath("my_feeling.db")
                if (dbFile.exists()) {
                    dbFile.delete()
                }
            } catch (e: Exception) {
                Log.e("Database", "Error deleting database file", e)
            }
        }
    }
}




