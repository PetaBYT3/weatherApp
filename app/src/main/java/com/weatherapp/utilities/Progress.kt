package com.weatherapp.utilities

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.weatherapp.intent.HomeAction
import com.weatherapp.state.HomeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay
import java.util.concurrent.TimeUnit

@Composable
fun CustomProgressBar(
    uiState: HomeState,
    onAction: (HomeAction) -> Unit
) {
    var progress by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    var duration by rememberSaveable {
        mutableIntStateOf(0)
    }

    if (uiState.isCountDownStart == true) {
        onAction(HomeAction.CountDownProgress(1f))
        duration = 4500
    } else {
        onAction(HomeAction.CountDownProgress(0f))
        duration = 500
    }

    var countDown by rememberSaveable {
        mutableIntStateOf(1000)
    }

    LaunchedEffect(Unit) {
        while (countDown > 0) {
            delay(100)
            countDown -= 1
        }
    }

    val size by animateFloatAsState(
        targetValue = uiState.countDownProgress!!,
        tween(
            durationMillis = duration,
            delayMillis = 200,
            easing = LinearEasing
        )
    )

    Column(
        modifier = Modifier.clip(RoundedCornerShape(50)).fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(size)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.onBackground)
                    .animateContentSize()
            )
        }
    }
    Text(text = countDown.toString())
}

@Composable
fun VerticalProgressBar(
    uiState: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val progress = "0.${uiState.weatherData?.current?.humidity}"

    val size by animateFloatAsState(
        targetValue = progress.toFloat(),
        tween(
            durationMillis = 1000,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        )
    )

    Column(
        modifier = Modifier.clip(RoundedCornerShape(50)).fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(size)
                    .background(MaterialTheme.colorScheme.onBackground)
                    .animateContentSize()
            )
        }
    }
}