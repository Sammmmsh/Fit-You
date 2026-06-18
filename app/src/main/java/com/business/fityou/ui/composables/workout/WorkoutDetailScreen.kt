package com.business.fityou.ui.composables.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.business.fityou.R
import com.business.fityou.data.models.Exercise
import com.business.fityou.data.models.Equipment
import com.business.fityou.data.models.equipments
import com.business.fityou.ui.composables.FloatingAddButton
import com.business.fityou.ui.composables.RegularButton
import com.business.fityou.ui.composables.home.WorkoutInfo
import com.business.fityou.ui.composables.home.Heading
import com.business.fityou.ui.composables.home.SubHeading
import com.business.fityou.ui.composables.home.Title
import com.business.fityou.ui.theme.darkBlue
import com.business.fityou.ui.theme.holoGreen
import com.business.fityou.ui.theme.lightBlue
import com.business.fityou.util.DifficultyLevels.Companion.Intermediate
import com.business.fityou.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkoutDetailScreen(
    navController: NavHostController,
    workoutViewModel: WorkoutViewModel
) = with(workoutViewModel) {

    LaunchedEffect(Unit) {
        if (workoutPlanState.workoutPlan == null) {
            getWorkoutPlan()
        }
        getWorkouts()
    }

    val state = workoutState
    val workoutPlan = workoutPlanState.workoutPlan
    var openDialog by remember { mutableStateOf(false) }
    var exBoxExpanded by remember { mutableStateOf(false) }
    var selectedExercise by remember(userExercisesList) {
        mutableStateOf(if (userExercisesList.isNotEmpty()) userExercisesList[0] else Exercise())
    }
    val equipmentList = equipments().toSet().toList()
    var eqBoxExpanded by remember { mutableStateOf(false) }
    var selectedEquipment by remember { mutableStateOf(if (equipmentList.isNotEmpty()) equipmentList[0] else Equipment()) }
    var setAmount by remember { mutableStateOf(1) }

    if (openDialog) {
        Dialog(
            onDismissRequest = { openDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),

        )
        {
            Surface(
                modifier = Modifier.width(300.dp),
                color = lightBlue,
                shape = RoundedCornerShape(40.dp)
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(30.dp)

                ) {
                    SubHeading(text = stringResource(R.string.add_exercise_heading), color = holoGreen)

                    ExposedDropdownMenuBox(
                        expanded = exBoxExpanded,
                        onExpandedChange = {
                            exBoxExpanded = !exBoxExpanded
                        }
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedExercise.name ?: "",
                            onValueChange = { value ->
                                selectedExercise = userExercisesList.firstOrNull { it.name == value } ?: Exercise()
                            },
                            label = { Text(stringResource(id = R.string.exercise), color = holoGreen) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = exBoxExpanded,

                                    )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                trailingIconColor = holoGreen,
                                focusedTrailingIconColor = holoGreen,
                                disabledTrailingIconColor = holoGreen
                            ),
                            textStyle = TextStyle(fontSize = 20.sp)

                        )
                        ExposedDropdownMenu(
                            expanded = exBoxExpanded,
                            onDismissRequest = {
                                exBoxExpanded = false
                            }
                        ) {
                            userExercisesList.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedExercise = selectionOption
                                        exBoxExpanded = false
                                    }
                                ) {
                                    Text(text = selectionOption.name!!)
                                }
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = eqBoxExpanded,
                        onExpandedChange = {
                            eqBoxExpanded = !eqBoxExpanded
                        }
                    ) {
                        val equipmentNameRes = selectedEquipment.name
                        TextField(
                            readOnly = true,
                            value = if (equipmentNameRes != null) stringResource(equipmentNameRes) else "",
                            onValueChange = { },
                            label = { Text(stringResource(id = R.string.equipment), color = holoGreen) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = eqBoxExpanded,

                                    )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                trailingIconColor = holoGreen,
                                focusedTrailingIconColor = holoGreen,
                                disabledTrailingIconColor = holoGreen
                            ),
                            textStyle = TextStyle(fontSize = 20.sp)
                        )
                        ExposedDropdownMenu(
                            expanded = eqBoxExpanded,
                            onDismissRequest = {
                                eqBoxExpanded = false
                            }
                        ) {
                            equipmentList.forEach { selectionOption ->
                                val optionNameRes = selectionOption.name
                                DropdownMenuItem(
                                    onClick = {
                                        selectedEquipment = selectionOption
                                        eqBoxExpanded = false
                                    }
                                ) {
                                    Text(text = if (optionNameRes != null) stringResource(optionNameRes) else "")
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(30.dp)
                    ) {
                        SubHeading(text = stringResource(id = R.string.sets), modifier = Modifier)

                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.spacedBy(
                                20.dp,
                                Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            IconButton(onClick = {
                                if (setAmount > 0) {
                                    setAmount--
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_remove),
                                    contentDescription = null,
                                    tint = holoGreen,
                                    modifier = Modifier.size(25.dp)
                                )
                            }

                            Title(text = setAmount.toString())

                            IconButton(
                                onClick = { setAmount++ },
                            )
                            {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircle,
                                    contentDescription = null,
                                    tint = holoGreen,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                    }

                    RegularButton(
                        text = stringResource(id = R.string.add),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            if (selectedExercise.name != null) {
                                openDialog = false
                                addExerciseToWorkout(
                                    exerciseName = selectedExercise.name!!,
                                    equipments = selectedEquipment, sets = setAmount
                                )
                            }
                        }
                    )
                }

            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = darkBlue,
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Heading(
                text = workoutPlan?.name?.replaceFirstChar { it.uppercase() } ?: "Workout",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            WorkoutInfo(
                duration = workoutPlan?.duration?.toString() ?: "0",
                difficulty = workoutPlan?.difficulty ?: Intermediate,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                ExerciseItemsDisplay(
                    modifier = Modifier.fillMaxSize(),
                    workoutViewModel = workoutViewModel
                )
            }

            FloatingAddButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                onClick = { openDialog = true }
            )
        }


    }

}




