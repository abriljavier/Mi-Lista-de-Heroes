package com.abriljavier.milistadeheroes.dataclasses

import java.io.Serializable

data class Level(
    val levelId: Int? = null,
    val classId: Int? = null,
    val level: Int? = null,
    val featureName: String? = null,
    val description: String? = null
) : Serializable {}