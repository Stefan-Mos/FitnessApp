package com.example.fitnessapp.data

import androidx.room.Update
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.model.FitnessGoalEntity
import com.example.fitnessapp.model.UserProfileEntity
import com.example.fitnessapp.model.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM workouts ORDER BY date DESC, id DESC")
    fun observeWorkouts(): Flow<List<WorkoutEntity>>

    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM fitness_goals ORDER BY id DESC")
    fun observeGoals(): Flow<List<FitnessGoalEntity>>

    @Insert
    suspend fun insertGoal(goal: FitnessGoalEntity)

    @Delete
    suspend fun deleteGoal(goal: FitnessGoalEntity)

    @Update
    suspend fun updateGoal(goal: FitnessGoalEntity)
}