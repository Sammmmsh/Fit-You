package com.business.fityou.ui.composables.meal


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CircleNotifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.business.fityou.domain.product.MealType
import com.business.fityou.ui.composables.common.MealsList
import com.business.fityou.R
import com.business.fityou.domain.product.Statistic
import com.business.fityou.ui.navigation.Screens
import com.business.fityou.viewmodel.UserViewModel
import com.business.fityou.viewmodel.WorkoutViewModel

@Composable
fun MealScreen(
    navController: NavController,
    viewModel: MealViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    workoutViewModel: WorkoutViewModel
) {
    val products by viewModel.products.collectAsState(initial = emptyList())
    val stats by  viewModel.stats.collectAsState()
    val user = workoutViewModel.user
    val authUser = userViewModel.signInState.data

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.Search.route) },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Meal")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item { HomeHeader(navController, user?.userName ?: authUser?.displayName ?: "User") }
            item { Spacer(modifier = Modifier.size(25.dp)) }
            item { CaloriesProgress(stats) }
            item { Spacer(modifier = Modifier.size(25.dp)) }
            item { MacroNutrimentsCard(stats) }
            item { Spacer(modifier = Modifier.size(25.dp)) }
            item { MealsList(navController, MealType.BREAKFAST, products, viewModel::removeProduct) }
            item { MealsList(navController, MealType.LUNCH, products, viewModel::removeProduct) }
            item { MealsList(navController, MealType.SNACK, products, viewModel::removeProduct) }
            item { MealsList(navController, MealType.DINER, products, viewModel::removeProduct) }
        }
    }
}

@Composable
fun HomeHeader(navController: NavController, userName: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = stringResource(R.string.home_header_user_photo),
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.onSurface, CircleShape)
                    .clickable {
                        navController.navigate(Screens.Profile.route)
                    }
            )
            Column(verticalArrangement = Arrangement.spacedBy((-4).dp, Alignment.CenterVertically)) {
                Text(text = stringResource(R.string.home_header_welcome), fontSize = 14.sp, color = MaterialTheme.colors.onBackground)
                Text(text = userName, fontSize = 18.sp, fontWeight = ExtraBold, color = MaterialTheme.colors.onBackground)
            }
        }
        Icon(
            imageVector = Icons.Outlined.CircleNotifications,
            contentDescription = stringResource(R.string.home_header_notifications),
            modifier = Modifier
                .size(34.dp)
                .clickable {}
        )
    }
}

@Composable
fun CaloriesProgress(stats: Statistic) {
    val left = 2500 - stats.totalCalories

    val progress: Float by animateFloatAsState(
        targetValue = stats.totalCalories.toFloat() / 2500f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing), label = ""
    )

    Column(verticalArrangement = Arrangement.Center) {
        LinearProgressIndicator(
            progress = progress,
            color = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .size(20.dp)
                .graphicsLayer {
                    shape = RoundedCornerShape(8.dp)
                    clip = true
                }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.home_stats_calories_total, stats.totalCalories), fontSize = 13.sp, fontWeight = Bold, color = MaterialTheme.colors.onBackground)
            Text(text = stringResource(R.string.home_stats_calories_left, left), fontSize = 13.sp, fontWeight = Bold, color = MaterialTheme.colors.onBackground)
        }
    }
}
