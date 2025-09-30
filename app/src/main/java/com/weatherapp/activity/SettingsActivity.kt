@file:OptIn(ExperimentalMaterial3Api::class)

package com.weatherapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Space
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WindPower
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import androidx.core.net.toUri
import com.google.android.gms.common.internal.StringResourceValueReader
import com.weatherapp.R
import com.weatherapp.utilities.copyToClipboard
import com.weatherapp.utilities.intentApp

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

    if (uiState.bottomSheetWind) {
        BottomSheetWind(uiState = uiState, onAction = viewModel::onAction)
    }

    if (uiState.bottomSheetAbout) {
        BottomSheetAbout(uiState = uiState, onAction = viewModel::onAction)
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
                icon = Icons.Rounded.AcUnit,
                title = "Degree",
                description = uiState.degree.toString(),
                isBottomSheet = true,
                openBottomSheet = { onAction(SettingsAction.OpenDegreeBottomSheet(true)) },
            ),
            Settings(
                icon = Icons.Rounded.WindPower,
                title = "Wind",
                description = uiState.wind.toString(),
                isBottomSheet = true,
                openBottomSheet = { onAction(SettingsAction.OpenWindBottomSheet(true)) },
            ),
            Settings(
                icon = Icons.Rounded.Timer,
                title = "Refresh Weather Count Down",
                description = "${uiState.refreshCountDown}s",
                isBottomSheet = true,
                openBottomSheet = { onAction(SettingsAction.OpenCountDownBottomSheet(true)) },
            ),
            Settings(
                icon = Icons.Rounded.Info,
                title = "Info",
                description = "About app",
                isBottomSheet = true,
                openBottomSheet = { onAction(SettingsAction.OpenAboutBottomSheet(true)) },
            )
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

    val options = listOf("Celcius", "Fahrenheit")
    var selectedDegree by rememberSaveable {
        mutableStateOf(uiState.degree.toString())
    }
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
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDegree == option,
                            onClick = {
                                selectedDegree = option
                            }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                onClick = {
                    onAction(SettingsAction.SetDegree(selectedDegree))
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(SettingsAction.OpenDegreeBottomSheet(false))
                    }
                }
            ) {
                Text(text = "Set Degree Format")
            }
        }
    }
}

@Composable
private fun BottomSheetWind(
    uiState: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val options = listOf("MPH", "KPH")
    var selectedWind by rememberSaveable {
        mutableStateOf(uiState.wind.toString())
    }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onAction(SettingsAction.OpenWindBottomSheet(false))
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 0.dp)
        ) {
            Text(
                text = "Wind Format",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedWind == option,
                            onClick = {
                                selectedWind = option
                            }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                onClick = {
                    onAction(SettingsAction.SetWind(selectedWind))
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(SettingsAction.OpenWindBottomSheet(false))
                    }
                }
            ) {
                Text(text = "Set Wind Format")
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
                valueRange = 5f..60f,
                steps = 10
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

@Composable
private fun BottomSheetAbout(
    uiState: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val packageManager = context.packageManager

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onAction(SettingsAction.OpenAboutBottomSheet(false))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 0.dp)
        ) {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState, true)
            ) {
                TextTitle(text = "Used Technology")
                Spacer(Modifier.height(10.dp))
                Row {
                    Card(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.kotlin),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(10.dp))
                            TextContent(text = "Kotlin")
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Card(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.jetpackcompose),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(10.dp))
                            TextContent(text = "Jetpack Compose")
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                TextTitle(text = "Contact Developer")
                Spacer(Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                    onClick = {
                        copyToClipboard(
                            label = "E-Mail",
                            text = "andreahussanini.2103@gmail.com",
                            context = context
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = "E-Mail",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                    onClick = {
                        intentApp(
                            webUrl = "https://www.linkedin.com/in/andrea-hussanini-274223218/",
                            appPackage = "com.linkedin.android",
                            context = context
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.linkedin),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = "LinkedIn",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                    onClick = {
                        intentApp(
                            webUrl = "https://github.com/PetaBYT3",
                            appPackage = "com.github.android",
                            context = context
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.github),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = "GitHub",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                    onClick = {
                        intentApp(
                            webUrl = "https://www.instagram.com/_andre.kt/",
                            appPackage = "com.instagram.lite",
                            context = context
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.instagram),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = "Instagram",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                    onClick = {
                        intentApp(
                            webUrl = "https://www.tiktok.com/@xliicxiv",
                            appPackage = "com.ss.android.ugc.trill",
                            context = context
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.tiktok),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = "TikTok",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                onClick = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(SettingsAction.OpenAboutBottomSheet(false))
                    }
                }
            ) {
                Text(text = "Close")
            }
        }
    }
}

