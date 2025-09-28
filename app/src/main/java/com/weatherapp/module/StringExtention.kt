package com.weatherapp.module

fun String.capitalizeWords(): String {
    if (this.isBlank()) return this
    return this.split(" ").joinToString(" ") { it.capitalize() }
}