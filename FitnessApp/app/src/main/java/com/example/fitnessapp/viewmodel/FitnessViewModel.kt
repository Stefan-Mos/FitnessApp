package com.example.fitnessapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.FitnessDatabase
import com.example.fitnessapp.data.FitnessRepository
import com.example.fitnessapp.model.FitnessGoalEntity
import com.example.fitnessapp.model.UserProfileEntity
import com.example.fitnessapp.model.WorkoutEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FitnessUiState(
    val profile: UserProfileEntity? = null,
    val workouts: List<WorkoutEntity> = emptyList(),
    val goals: List<FitnessGoalEntity> = emptyList()
)

class FitnessViewModel(
    private val repository: FitnessRepository
) : ViewModel() {

    val uiState: StateFlow<FitnessUiState> =
        combine(
            repository.profile,
            repository.workouts,
            repository.goals
        ) { profile, workouts, goals ->
            FitnessUiState(
                profile = profile,
                workouts = workouts,
                goals = goals
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FitnessUiState()
        )

    fun saveProfile(
        age: Int,
        heightCm: Double,
        weightKg: Double
    ) {
        viewModelScope.launch {
            repository.saveProfile(
                UserProfileEntity(
                    age = age,
                    heightCm = heightCm,
                    weightKg = weightKg
                )
            )
        }
    }

    fun addWorkout(
        name: String,
        durationMinutes: Int,
        calories: Int
    ) {
        viewModelScope.launch {
            repository.addWorkout(
                WorkoutEntity(
                    name = name,
                    durationMinutes = durationMinutes,
                    calories = calories,
                    date = java.time.LocalDate.now().toString()
                )
            )
        }
    }

    fun addGoal(
        description: String,
        target: Int,
        period: String = "Weekly"
    ) {
        viewModelScope.launch {
            repository.addGoal(
                FitnessGoalEntity(
                    description = description,
                    target = target,
                    period = period
                )
            )
        }
    }

    fun editGoal(
        goal: FitnessGoalEntity,
        description: String,
        target: Int,
        period: String
    ) {
        viewModelScope.launch {
            repository.updateGoal(
                goal.copy(
                    description = description,
                    target = target,
                    period = period
                )
            )
        }
    }

    fun updateGoalProgress(goal: FitnessGoalEntity, newValue: Int) {
        viewModelScope.launch {
            val safeValue = newValue.coerceAtLeast(0)

            repository.updateGoal(
                goal.copy(currentValue = safeValue)
            )
        }
    }

    companion object {
        fun factory(
            database: FitnessDatabase
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    return FitnessViewModel(
                        FitnessRepository(database.fitnessDao())
                    ) as T
                }
            }
    }
}