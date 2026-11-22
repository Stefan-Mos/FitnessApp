package com.example.fitnessapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitnessapp.model.FitnessGoalEntity
import com.example.fitnessapp.model.UserProfileEntity
import com.example.fitnessapp.model.WorkoutEntity

@Database(
    entities = [
        UserProfileEntity::class,
        WorkoutEntity::class,
        FitnessGoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun fitnessDao(): FitnessDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        fun getInstance(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_tracker.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}