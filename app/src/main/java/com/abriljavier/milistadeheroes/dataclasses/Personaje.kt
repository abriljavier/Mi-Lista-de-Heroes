package com.abriljavier.milistadeheroes.dataclasses

import android.health.connect.datatypes.CervicalMucusRecord.CervicalMucusAppearance
import java.io.Serializable

data class Personaje(
    val user: Users? = null,
    val name: String? = null,
    var imageUri: String? = null,
    var background: Background? = null,
    var characterClass: Classe? = null,
    val level: Level? = null,
    val race: Race? = null,
    var attributes: Attributes? = null,
    var selectedTrait: String? = null,
    var selectedIdeal: String? = null,
    var selectedBonds: String? = null,
    var selectedFlaws: String? = null,
    var selectedAligment: String? = null,
    var appearance: String? = null,
    var age: String? = null,
    var history: String? = null,
    var languages: String? = null,
    var notes: String? = null,
    var numLevel: Int? = 1,
    var features: MutableList<Feature> = mutableListOf(),
    var hitPoints: Int? = 0,
    var competiences: MutableList<String> = mutableListOf(),
): Serializable

data class Feature(
    val name: String,
    val description: String
) : Serializable