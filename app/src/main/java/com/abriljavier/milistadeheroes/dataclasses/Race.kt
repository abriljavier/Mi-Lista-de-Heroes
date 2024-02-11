package com.abriljavier.milistadeheroes.dataclasses

import java.io.Serializable

data class Attributes(
    val STR: Int? = null, val DEX: Int? = null, val CON: Int? = null, val INT: Int? = null, val WIS: Int? = null, val CHA: Int? = null
) : Serializable {}

data class Features(
    val first: String? = null,
    val second: String? = null,
    val third: String? = null,
    val fourth: String? = null,
    val fifth: String? = null,
    val sixth: String? = null,
) : Serializable {}


data class Race(
    var id: Int? = null,
    val name: String? = null,
    val attributes: Attributes? = null,
    val size: String? = null,
    val speed: Int? = null,
    val languages: String? = null,
    val features: Features? = null
) : Serializable {}

