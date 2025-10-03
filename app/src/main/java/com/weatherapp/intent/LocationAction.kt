package com.weatherapp.intent

import com.weatherapp.dataclass.LocationWithWeather
import com.weatherapp.roomdata.dataclass.Location

sealed interface LocationAction {
    data class GpsSettings(val newGpsSettings: Boolean) : LocationAction

    //Insert Action
    data object OpenBottomSheetInput : LocationAction
    data object DismissBottomSheetInput : LocationAction
    data class ConfirmInsertLocation(val insertLocation: Location) : LocationAction

    //Delete Action
    data class OpenBottomSheetDelete(val deleteLocation: LocationWithWeather) : LocationAction
    data object DismissBottomSheetDelete : LocationAction
    data object ConfirmDeleteLocation : LocationAction

    //Select Location
    data class OpenBottomSheetSelect(val selectLocation: LocationWithWeather) : LocationAction
    data object DismissBottomSheetSelect : LocationAction
    data object ConfirmSelectLocation : LocationAction

    //Get Weather Response For Every Location
    data class GetWeatherResponse(val locationData: Location): LocationAction

    //Location Permission
    data class ActionBottomSheetPermissionLocation(val isBottomSheetOpen: Boolean) : LocationAction
}