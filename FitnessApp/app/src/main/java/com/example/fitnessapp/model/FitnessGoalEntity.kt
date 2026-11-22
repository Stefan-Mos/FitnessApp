package com.example.fitnessapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fitness_goals")
data class FitnessGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val description: String,
    val target: Int,
    val currentValue: Int = 0,
    val period: String = "Weekly"
)