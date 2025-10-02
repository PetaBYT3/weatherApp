@file:OptIn(ExperimentalMaterial3Api::class)

package com.weatherapp.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Api
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
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
import androidx.compose.runtime.LaunchedEffect
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.weatherapp.R
import com.weatherapp.dataclass.ContactDeveloper
import com.weatherapp.dataclass.Settings
import com.weatherapp.dataclass.UsedTechnology
import com.weatherapp.intent.SettingsAction
import com.weatherapp.state.SettingsState
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.utilities.NotificationPermissionHandler
import com.weatherapp.utilities.TextContent
import com.weatherapp.utilities.TextTitle
import com.weatherapp.utilities.copyToClipboard
import com.weatherapp.utilities.intentApp
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

@SuppressLint("PermissionLaunchedDuringComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
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
        val permissionState = rememberPermissionState(
            permission = android.Manifest.permission.POST_NOTIFICATIONS
        )

        val settingsList = listOf(
            Settings(
                icon = Icons.Rounded.Notifications,
                title = "Notification",
                description = "Allow notification permission to get weather info in notification or status bar",
                isSwitch = true,
                isChecked = uiState.notification,
                onSwitchChange = {
                    if (uiState.notification) {
                        when {
                            permissionState.status.isGranted -> {
                                onAction(SettingsAction.SetNotification(true))
                            }
                            permissionState.status.shouldShowRationale -> {

                            }
                            !permissionState.status.shouldShowRationale -> {
                                permissionState.launchPermissionRequest()
                            }
                        }
                    } else {
                        onAction(SettingsAction.SetNotification(false))
                    }
                }
            ),
            Settings(
                icon = Icons.Rounded.AcUnit,
                title = "Degree",
                description = uiState.degree.toString(),
                isBottomSheet = true,
                onItemClick = { onAction(SettingsAction.OpenDegreeBottomSheet(true)) },
            ),
            Settings(
                icon = Icons.Rounded.WindPower,
                title = "Wind",
                description = uiState.wind.toString(),
                isBottomSheet = true,
                onItemClick = { onAction(SettingsAction.OpenWindBottomSheet(true)) },
            ),
            Settings(
                icon = Icons.Rounded.Timer,
                title = "Refresh Weather Count Down",
                description = "${uiState.refreshCountDown}s",
                isBottomSheet = true,
                onItemClick = { onAction(SettingsAction.OpenCountDownBottomSheet(true)) },
            ),
            Settings(
                icon = Icons.Rounded.Info,
                title = "Info",
                description = "About app",
                isBottomSheet = true,
                onItemClick = { onAction(SettingsAction.OpenAboutBottomSheet(true)) },
            )
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(settingsList) { settingsOption ->
                Card(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).fillMaxWidth(),
                    onClick = {
                        settingsOption.onItemClick?.invoke()
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
                                    checked = settingsOption.isChecked,
                                    onCheckedChange = {
                                        settingsOption.onSwitchChange
                                    }
                                )
                            }
                            if (settingsOption.isBottomSheet) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowDownward,
                                    contentDescription = null
                                )
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
                        modifier = Modifier.fillMaxWidth(),
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

    val usedTechnologyList = listOf(
        UsedTechnology(
            icon = R.drawable.kotlin,
            title = "Kotlin"
        ),
        UsedTechnology(
            icon = R.drawable.jetpackcompose,
            title = "Jetpack Compose"
        ),
        UsedTechnology(
            icon = R.drawable.xml,
            title = "XML"
        )
    )
    val poweredByList = listOf(
        ContactDeveloper(
            iconVector = Icons.Rounded.Api,
            title = "Weather API",
            actionIcon = Icons.Rounded.ArrowForward,
            onItemClick = {
                intentApp(
                    webUrl = "https://www.weatherapi.com/",
                    appPackage = "com.android.chrome",
                    context = context
                )
            }
        ),
        ContactDeveloper(
            icon = R.drawable.gemini,
            title = "Gemini Generative AI"
        )
    )

    val contactDeveloperList = listOf(
        ContactDeveloper(
            iconVector = Icons.Rounded.Email,
            title = "E-Mail",
            description = "andreahussanini.2103@gmail.com",
            actionIcon = Icons.Rounded.ContentCopy,
            onItemClick = {
                copyToClipboard(
                    label = "E-Mail",
                    text = "andreahussanini.2103@gmail.com",
                    toast = "E-Mail Copied !",
                    context = context
                )
            }
        ),
        ContactDeveloper(
            icon = R.drawable.linkedin,
            title = "LinkedIn",
            description = "https://www.linkedin.com/in/andrea-hussanini-274223218/",
            actionIcon = Icons.Rounded.ArrowForward,
            onItemClick = {
                intentApp(
                    webUrl = "https://www.linkedin.com/in/andrea-hussanini-274223218/",
                    appPackage = "com.linkedin.android",
                    context = context
                )
            }
        ),
        ContactDeveloper(
            icon = R.drawable.github,
            title = "GitHub",
            description = "https://github.com/PetaBYT3",
            actionIcon = Icons.Rounded.ArrowForward,
            onItemClick = {
                intentApp(
                    webUrl = "https://github.com/PetaBYT3",
                    appPackage = "com.github.android",
                    context = context
                )
            }
        ),
        ContactDeveloper(
            icon = R.drawable.instagram,
            title = "Instagram",
            description = "https://www.instagram.com/_andre.kt/",
            actionIcon = Icons.Rounded.ArrowForward,
            onItemClick = {
                intentApp(
                    webUrl = "https://www.instagram.com/_andre.kt/",
                    appPackage = "com.instagram.android",
                    context = context
                )
            }
        ),
        ContactDeveloper(
            icon = R.drawable.tiktok,
            title = "TikTok",
            description = "https://www.tiktok.com/@xliicxiv",
            actionIcon = Icons.Rounded.ArrowForward,
            onItemClick = {
                intentApp(
                    webUrl = "https://www.tiktok.com/@xliicxiv",
                    appPackage = "com.ss.android.ugc.trill",
                    context = context
                )
            }
        )
    )

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
                    .weight(1f)
                    .verticalScroll(scrollState, true)
            ) {
                TextTitle(text = "Powered By")
                Spacer(Modifier.height(10.dp))
                poweredByList.forEach { poweredBy ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                        onClick = { poweredBy.onItemClick?.invoke() }
                    ) {
                        Row(
                            modifier = Modifier.padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (poweredBy.icon != null) {
                                Icon(
                                    painter = painterResource(poweredBy.icon),
                                    contentDescription = null
                                )
                            }
                            if (poweredBy.iconVector != null) {
                                Icon(
                                    imageVector = poweredBy.iconVector,
                                    contentDescription = null
                                )
                            }
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = poweredBy.title,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.weight(1f))
                            if (poweredBy.actionIcon != null) {
                                Icon(
                                    imageVector = poweredBy.actionIcon,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(10.dp))
                TextTitle(text = "Used Technology")
                Spacer(Modifier.height(10.dp))
                usedTechnologyList.forEach { usedTechnology ->
                    Card(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(usedTechnology.icon),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(10.dp))
                            TextContent(text = usedTechnology.title)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
                Spacer(Modifier.height(10.dp))
                TextTitle(text = "Contact Developer")
                Spacer(Modifier.height(10.dp))
                contactDeveloperList.forEach { contactDeveloper ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                        onClick = { contactDeveloper.onItemClick?.invoke() }
                    ) {
                        Row(
                            modifier = Modifier.padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (contactDeveloper.icon != null) {
                                Icon(
                                    painter = painterResource(contactDeveloper.icon),
                                    contentDescription = null
                                )
                            }
                            if (contactDeveloper.iconVector != null) {
                                Icon(
                                    imageVector = contactDeveloper.iconVector,
                                    contentDescription = null
                                )
                            }
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = contactDeveloper.title,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = contactDeveloper.actionIcon!!,
                                contentDescription = null
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
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

