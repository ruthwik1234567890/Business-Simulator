package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.TycoonDao
import com.example.data.entity.GameStateEntity
import com.example.data.entity.HoldingEntity

@Database(
    entities = [GameStateEntity::class, HoldingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TycoonDatabase : RoomDatabase() {
    abstract fun tycoonDao(): TycoonDao

    companion object {
        @Volatile
        private var INSTANCE: TycoonDatabase? = null

        fun getDatabase(context: Context): TycoonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TycoonDatabase::class.java,
                    "tycoon_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
