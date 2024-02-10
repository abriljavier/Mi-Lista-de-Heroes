package com.abriljavier.milistadeheroes.dataclasses

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
    var selectedFlaws: String? = null
): Serializable