package com.weatherapp.intent

import com.weatherapp.roomdata.dataclass.Location

sealed interface LocationAction {
    data class GpsSettings(val newGpsSettings: Boolean) : LocationAction
    data class InsertLocation(val newInsertLocation: Location) : LocationAction
    data class DeleteLocation(val newDeleteLocation: Location) : LocationAction
}