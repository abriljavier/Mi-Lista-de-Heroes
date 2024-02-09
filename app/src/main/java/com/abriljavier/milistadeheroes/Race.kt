package com.abriljavier.milistadeheroes

data class Attributes(
    val STR: Int, val DEX: Int, val CON: Int, val INT: Int, val WIS: Int, val CHA: Int
) {}

data class Features(
    val first: String?,
    val second: String?,
    val third: String?,
    val fourth: String?,
    val fifth: String?,
    val sixth: String?,
) {}


data class Race(
    val id: Int?,
    val name: String,
    val attributes: Attributes,
    val size: String,
    val speed: Int,
    val languages: String,
    val features: Features
) {}

