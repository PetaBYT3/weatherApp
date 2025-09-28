@file:OptIn(ExperimentalMaterial3Api::class)

package com.weatherapp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.dataclass.Settings
import com.weatherapp.intent.SettingsAction
import com.weatherapp.state.SettingsState
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.utilities.TextContent
import com.weatherapp.utilities.TextTitle
import com.weatherapp.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                MainScreenCore()
            }
        }
    }
}

@Composable
private fun MainScreenCore(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MainScreen(uiState = uiState, onAction = viewModel::onAction)

    if (uiState.bottomSheetCountDown) {
        BottomSheetCountDown(uiState = uiState, onAction = viewModel::onAction)
    }

    if (uiState.bottomSheetDegree) {
        BottomSheetDegree(uiState = uiState, onAction = viewModel::onAction)
    }
}

@Composable
private fun MainScreen(
    uiState: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            (context as? ComponentActivity)?.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                }
            )
        },
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            onAction = onAction
        )
    }
}

@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    uiState: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 15.dp)
            .fillMaxWidth()
    ) {
        val settingsList = listOf(
            Settings(
                icon = Icons.Rounded.Add,
                title = "Degree",
                description = uiState.degree.toString(),
                isBottomSheet = true,
                openBottomSheet = { onAction(SettingsAction.OpenDegreeBottomSheet(true)) },
            ),
            Settings(
                icon = Icons.Rounded.Timer,
                title = "Refresh Weather Count Down",
                description = "${uiState.refreshCountDown}s",
                isBottomSheet = true,
                openBottomSheet = { onAction(SettingsAction.OpenCountDownBottomSheet(true)) },
            ),
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(settingsList) { settingsOption ->
                Card(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).fillMaxWidth(),
                    onClick = {
                        settingsOption.onItemClick
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = settingsOption.icon,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            TextTitle(text = settingsOption.title)
                            Spacer(modifier = Modifier.height(5.dp))
                            TextContent(text = settingsOption.description)
                        }
                        Column(
                            modifier = Modifier.width(50.dp).fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (settingsOption.isSwitch) {
                                Switch(
                                    checked = true,
                                    onCheckedChange = {
                                        settingsOption.onSwitchChange
                                    }
                                )
                            }
                            if (settingsOption.isBottomSheet) {
                                IconButton(
                                    onClick = {
                                        settingsOption.openBottomSheet?.invoke()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDownward,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun BottomSheetDegree(
    uiState: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onAction(SettingsAction.OpenDegreeBottomSheet(false))
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 0.dp)
        ) {
            Text(
                text = "Degree Format",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            RadioButton(
                selected = true,
                onClick = {
                    onAction(SettingsAction.OpenDegreeBottomSheet(false))
                },
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                onClick = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(SettingsAction.OpenDegreeBottomSheet(false))
                    }
                }
            ) {
                Text(text = "Set Count Down")
            }
        }
    }
}

@Composable
private fun BottomSheetCountDown(
    uiState: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onAction(SettingsAction.OpenCountDownBottomSheet(false))
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 0.dp)
        ) {
            Text(
                text = "Refresh Count Down Weather",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            val sliderState = rememberSliderState(
                value = uiState.refreshCountDown!!.toFloat(),
                valueRange = 5f..10f,
                steps = 4
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${sliderState.value.toInt()}s",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Slider(
                    state = sliderState,
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                onClick = {
                    onAction(SettingsAction.SetCountDown(sliderState.value.toInt()))
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(SettingsAction.OpenCountDownBottomSheet(false))
                    }
                }
            ) {
                Text(text = "Set Count Down")
            }
        }
    }
}
