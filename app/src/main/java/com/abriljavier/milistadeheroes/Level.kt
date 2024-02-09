package com.abriljavier.milistadeheroes

data class Level(
    val levelId: Int? = null,
    val classId: Int,
    val level: Int,
    val featureName: String,
    val description: String
) {}