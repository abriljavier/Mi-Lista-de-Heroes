package com.abriljavier.milistadeheroes.dataclasses

import java.io.Serializable

data class Personaje(
    val pj_id: Int? = null,
    val name: String? = null,
    var imageUri: String? = null,
    var characterClass: Classe? = null,
    val race: Race? = null,
    var numLevel: Int? = 1,
    var hitPoints: Int? = 0,
    var competiences: MutableList<String> = mutableListOf(),
    var attributes: AttributesPJ? = null,
    var selectedAlignment: String? = null,
    var appearance: String? = null,
    var history: String? = null,
    var languages: String? = null,
    var notes: String? = null,
    var background: Background? = null,
    var age: String? = null,
    var featuresByLevel: MutableList<String> = mutableListOf()

): Serializable

data class AttributesPJ(
    val STR: Int? = null, val DEX: Int? = null, val CON: Int? = null, val INT: Int? = null, val WIS: Int? = null, val CHA: Int? = null
) : Serializable {}