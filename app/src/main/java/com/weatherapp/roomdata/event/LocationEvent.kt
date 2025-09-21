package com.weatherapp.roomdata.event

import com.weatherapp.roomdata.dataclass.Location

sealed interface LocationEvent {

    object SaveLocation: LocationEvent
    data class SetLocationName(val locationName: String): LocationEvent
    object ShowDialog: LocationEvent
    object HideDialog: LocationEvent
    data class DeleteLocation(val location: Location): LocationEvent
}