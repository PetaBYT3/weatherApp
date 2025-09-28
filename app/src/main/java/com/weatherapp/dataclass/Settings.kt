package com.weatherapp.dataclass

import androidx.compose.ui.graphics.vector.ImageVector

data class Settings(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val isSwitch: Boolean = false,
    val onSwitchChange: ((Boolean) -> Unit)? = null,

    //BottomSheet
    val isBottomSheet: Boolean = false,
    val openBottomSheet: (() -> Unit)? = null,

    val onItemClick: (() -> Unit)? = null
)
