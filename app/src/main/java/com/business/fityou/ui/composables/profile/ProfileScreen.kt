package com.business.fityou.ui.composables.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.business.fityou.R
import com.business.fityou.ui.composables.RegularButton
import com.business.fityou.ui.theme.darkBlue
import com.business.fityou.ui.theme.white
import com.business.fityou.viewmodel.UserViewModel
import com.business.fityou.viewmodel.WorkoutViewModel
import kotlin.reflect.KFunction0


private const val initialImageFloat = 170f

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    workoutViewModel: WorkoutViewModel
) {
    val user = workoutViewModel.user
    val statusBarHeight = 56.dp

    Surface(modifier = Modifier.fillMaxSize(), color = darkBlue) {
        CollapsibleHeader(
            scrollState = rememberScrollState(),
            profilePic = Icons.Rounded.Person,
            name = user?.userName ?: "Guest User",
            position = user?.userEmail ?: "No email provided",
            backgroundModifier = Modifier.background(darkBlue),
            heightRange = statusBarHeight to 200.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                RegularButton(
                    text = "Log Out",
                    onClick = { userViewModel.logOut() }
                )
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CollapsibleHeader(
    scrollState: ScrollState,
    profilePic: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    position: String,
    backgroundModifier: Modifier? = null, // Use this only to apply background color/gradient
    // First: Sticky header height
    // Second: Full header height
    heightRange: Pair<Dp, Dp>,
    content: @Composable () -> Unit
) {
    val bgModifier = backgroundModifier ?: Modifier.background(MaterialTheme.colors.background)
    val scrollHeight = with(LocalDensity.current) { scrollState.value.toDp() }
    val headerHeight = remember(scrollHeight) { derivedStateOf {
        if (heightRange.second - heightRange.first <= scrollHeight) heightRange.first
        else heightRange.second - scrollHeight
    } }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().verticalScroll(scrollState)) {
            Spacer(
                Modifier.fillMaxWidth()
                    .height(heightRange.second)
                    .then(bgModifier)
            )
            content()
        }

        Row(
            Modifier.fillMaxWidth()
                .height(headerHeight.value)
                .then(bgModifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = profilePic,
                contentDescription = null,
                tint = white,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .clip(CircleShape)
                    .border(1.dp, white, CircleShape)
            )

            Column(
                Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = name, color = white, style = MaterialTheme.typography.h6)
                Text(text = position, color = white.copy(0.7f), style = MaterialTheme.typography.body2)
            }
        }
    }
}
