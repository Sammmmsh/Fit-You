package com.business.fityou.ui.composables.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.business.fityou.R
import com.business.fityou.data.models.ExerciseItem
import com.business.fityou.ui.composables.home.SubHeading
import com.business.fityou.ui.theme.veryDarkBlue
import com.business.fityou.ui.theme.white
import com.business.fityou.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExerciseItemsDisplay(
    modifier: Modifier = Modifier,
    workoutViewModel: WorkoutViewModel
) {

    val exerciseList =
        workoutViewModel.workoutState.exerciseItems ?: emptyList<ExerciseItem>()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {

        itemsIndexed(
            items = exerciseList,
            key = { index, item -> "${item.name}_${index}" }
        ) { index, data ->
            val dismissState = rememberDismissState()

            LaunchedEffect(dismissState.targetValue) {
                if (dismissState.targetValue == DismissValue.DismissedToEnd) {
                    workoutViewModel.removeExercise(data)
                }
            }

            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.StartToEnd),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == DismissDirection.StartToEnd) 0.25f else 0.5f)
                },
                background = {}
            ) {

                ExerciseItemRow(data)

            }

        }

    }


}


@Composable
fun ExerciseItemRow(data: ExerciseItem) {

    val exerciseName = data.name
    val sets = data.sets
    val equipment = data.equipments

    Surface(
        color = veryDarkBlue,
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = exerciseName?.replaceFirstChar { it.uppercase() } ?: "Exercise",
                            color = white,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sets?.toString() ?: "0",
                            color = white,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val eqNameRes = equipment?.name
                        Text(
                            text = if (eqNameRes != null) stringResource(id = eqNameRes) else "",
                            color = white.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = stringResource(id = R.string.sets),
                            color = white.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }

                }
            }

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(),
                color = white.copy(alpha = 0.1f)
            )
        }
    }
}
