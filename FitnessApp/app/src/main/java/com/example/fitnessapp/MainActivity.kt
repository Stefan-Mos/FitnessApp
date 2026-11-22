package com.example.fitnessapp

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.data.FitnessDatabase
import com.example.fitnessapp.model.FitnessGoalEntity
import com.example.fitnessapp.viewmodel.FitnessUiState
import com.example.fitnessapp.viewmodel.FitnessViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FitnessViewModel by viewModels {
        FitnessViewModel.factory(
            FitnessDatabase.getInstance(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FitnessTrackerApp(viewModel)
        }
    }
}

@Composable
fun FitnessTrackerApp(viewModel: FitnessViewModel) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Home") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf("Home", "Progress", "Log", "Goals").forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Text(tab.take(1)) },
                        label = { Text(tab) }
                    )
                }
            }
        }
    ) { padding ->

        when (selectedTab) {
            "Home" -> HomeScreen(
                modifier = Modifier.padding(padding),
                state = state,
                viewModel = viewModel
            )

            "Progress" -> ProgressScreen(
                modifier = Modifier.padding(padding),
                state = state
            )

            "Log" -> LogWorkoutScreen(
                modifier = Modifier.padding(padding),
                viewModel = viewModel
            )

            "Goals" -> GoalsScreen(
                modifier = Modifier.padding(padding),
                goals = state.goals,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    state: FitnessUiState,
    viewModel: FitnessViewModel
) {
    var age by remember {
        mutableStateOf(state.profile?.age?.toString() ?: "")
    }

    var height by remember {
        mutableStateOf(state.profile?.heightCm?.toString() ?: "")
    }

    var weight by remember {
        mutableStateOf(state.profile?.weightKg?.toString() ?: "")
    }

    var error by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Fitness Tracker",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Text("Set your baseline and monitor your progress.")
        }

        if (state.profile == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.titleLarge
                        )

                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text("Age") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Height in cm") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("Weight in kg") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (error.isNotBlank()) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val parsedAge = age.toIntOrNull()
                                val parsedHeight = height.toDoubleOrNull()
                                val parsedWeight = weight.toDoubleOrNull()

                                if (
                                    parsedAge == null ||
                                    parsedHeight == null ||
                                    parsedWeight == null ||
                                    parsedAge <= 0 ||
                                    parsedHeight <= 0 ||
                                    parsedWeight <= 0
                                ) {
                                    error = "Enter positive numeric values for all fields."
                                } else {
                                    error = ""

                                    viewModel.saveProfile(
                                        parsedAge,
                                        parsedHeight,
                                        parsedWeight
                                    )
                                }
                            }
                        ) {
                            Text("Save profile")
                        }
                    }
                }
            }
        }

        item {
            Text("Workouts logged: ${state.workouts.size}")
            Text("Calories burned: ${state.workouts.sumOf { it.calories }}")
        }
    }

}

@Composable
fun ProgressScreen(
    modifier: Modifier,
    state: FitnessUiState
) {
    val bmi = state.profile?.let {
        calculateBmi(it.weightKg, it.heightCm)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "BMI: ${
                            bmi?.let {
                                "%.1f".format(it)
                            } ?: "Add your profile first"
                        }"
                    )

                    Text(
                        "Workout minutes: ${
                            state.workouts.sumOf { it.durationMinutes }
                        }"
                    )

                    Text(
                        "Calories burned: ${
                            state.workouts.sumOf { it.calories }
                        }"
                    )
                }
            }
        }

        item {
            Text(
                text = "Saved workouts",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(state.workouts) { workout ->
            Text(
                "${workout.name}: " +
                        "${workout.durationMinutes} minutes, " +
                        "${workout.calories} calories " +
                        "(${workout.date})"
            )

            HorizontalDivider()
        }
    }
}

@Composable
fun LogWorkoutScreen(
    modifier: Modifier,
    viewModel: FitnessViewModel
) {
    var name by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Log Workout",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Exercise name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration in minutes") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = calories,
            onValueChange = { calories = it },
            label = { Text("Calories burned") },
            modifier = Modifier.fillMaxWidth()
        )

        if (message.isNotBlank()) {
            Text(message)
        }

        Button(
            onClick = {
                val parsedDuration = duration.toIntOrNull()
                val parsedCalories = calories.toIntOrNull()

                if (
                    name.isBlank() ||
                    parsedDuration == null ||
                    parsedCalories == null ||
                    parsedDuration <= 0 ||
                    parsedCalories < 0
                ) {
                    message = "Enter valid workout information."
                } else {
                    viewModel.addWorkout(
                        name.trim(),
                        parsedDuration,
                        parsedCalories
                    )

                    name = ""
                    duration = ""
                    calories = ""
                    message = "Workout saved."
                }
            }
        ) {
            Text("Add workout")
        }
    }
}

@Composable
fun GoalsScreen(
    modifier: Modifier,
    goals: List<FitnessGoalEntity>,
    viewModel: FitnessViewModel
) {
    var description by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Goals",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Goal description") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                label = { Text("Target value") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            if (message.isNotBlank()) {
                Text(message)
            }

            Button(
                onClick = {
                    val parsedTarget = target.toIntOrNull()

                    if (
                        description.isBlank() ||
                        parsedTarget == null ||
                        parsedTarget <= 0
                    ) {
                        message = "Enter a valid goal and target."
                    } else {
                        viewModel.addGoal(
                            description.trim(),
                            parsedTarget
                        )

                        description = ""
                        target = ""
                        message = "Goal saved."
                    }
                }
            ) {
                Text("Add goal")
            }
        }

        items(goals) { goal ->

            var progressText by remember(
                goal.id,
                goal.currentValue
            ) {
                mutableStateOf(goal.currentValue.toString())
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Progress: ${goal.currentValue} / ${goal.target}"
                    )

                    Text(
                        text = "Period: ${goal.period}"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = progressText,
                        onValueChange = {
                            progressText = it
                        },
                        label = {
                            Text("Current value")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val newValue = progressText.toIntOrNull()

                            if (newValue != null) {
                                viewModel.updateGoalProgress(
                                    goal = goal,
                                    newValue = newValue
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save progress")
                    }

                    if (goal.currentValue >= goal.target) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Goal achieved!",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

fun calculateBmi(
    weightKg: Double,
    heightCm: Double
): Double {
    val heightMeters = heightCm / 100.0

    return weightKg / (heightMeters * heightMeters)
}