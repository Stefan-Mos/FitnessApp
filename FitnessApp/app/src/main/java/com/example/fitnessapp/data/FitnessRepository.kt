package com.example.fitnessapp.data

import com.example.fitnessapp.model.FitnessGoalEntity
import com.example.fitnessapp.model.UserProfileEntity
import com.example.fitnessapp.model.WorkoutEntity

class FitnessRepository(
    private val dao: FitnessDao
) {
    val profile = dao.observeProfile()

    val workouts = dao.observeWorkouts()

    val goals = dao.observeGoals()

    suspend fun saveProfile(profile: UserProfileEntity) {
        dao.saveProfile(profile)
    }

    suspend fun addWorkout(workout: WorkoutEntity) {
        dao.insertWorkout(workout)
    }

    suspend fun deleteWorkout(workout: WorkoutEntity) {
        dao.deleteWorkout(workout)
    }

    suspend fun addGoal(goal: FitnessGoalEntity) {
        dao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: FitnessGoalEntity) {
        dao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: FitnessGoalEntity) {
        dao.deleteGoal(goal)
    }
}