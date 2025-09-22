package com.weatherapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.weatherapp.dataclass.NavigationItem
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.page.HomePage
import com.weatherapp.page.ProfilePage
import com.weatherapp.page.SettingsPage

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen() {
    val navigationItemList = listOf(
        NavigationItem("Home", Icons.Rounded.Home),
        NavigationItem("Profile", Icons.Rounded.Person),
        NavigationItem("Settings", Icons.Rounded.Settings)
    )
    var selectedNavigation by rememberSaveable() {
        mutableIntStateOf(0)
    }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Weather App") },
                actions = {
                    IconButton(
                        onClick = {
                            val intentEdit = Intent(context, EditActivity::class.java)
                            context.startActivity(intentEdit)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                navigationItemList.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedNavigation == index,
                        onClick = {
                            selectedNavigation = index
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = "Icon"
                            )
                        },
                        label = {
                            Text(
                                text = item.label
                            )
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        ContentScreen(
            Modifier.padding(innerPadding),
            innerPadding,
            selectedNavigation
        )
    }
}

@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    selectedNavigation: Int,
) {
    Column(
        modifier = modifier
    ) {
        when(selectedNavigation) {
            0 -> HomePage()
            1 -> ProfilePage()
            2 -> SettingsPage()
        }
    }
}