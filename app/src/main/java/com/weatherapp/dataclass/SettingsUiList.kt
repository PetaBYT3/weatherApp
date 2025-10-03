package com.weatherapp.dataclass

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class Settings(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val isSwitch: Boolean = false,
    val isChecked: Boolean = false,
    val onSwitchChange: (Boolean) -> Unit = {},

    //BottomSheet
    val isBottomSheet: Boolean = false,
    val openBottomSheet: (() -> Unit)? = null,

    val onItemClick: (() -> Unit)? = null
)

data class UsedTechnology(
    val icon: Int,
    val title: String,
    val description: String? = null
)

data class ContactDeveloper(
    val icon: Int? = null,
    val iconVector: ImageVector? = null,
    val title: String,
    val description: String? = null,
    val actionIcon: ImageVector? = null,
    val onItemClick: (() -> Unit)? = null
)
