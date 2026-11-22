package com.example.fitnessapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,

    val age: Int,
    val heightCm: Double,
    val weightKg: Double
)