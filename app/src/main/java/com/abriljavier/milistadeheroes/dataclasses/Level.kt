package com.abriljavier.milistadeheroes.dataclasses

import java.io.Serializable

data class Level(
    val levelId: Int? = null,
    val classId: Int? = null,
    val level: Int? = null,
    var featureName: String? = null,
    var description: String? = null
) : Serializable {}