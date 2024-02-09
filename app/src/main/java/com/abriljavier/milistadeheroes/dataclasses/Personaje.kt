package com.abriljavier.milistadeheroes.dataclasses

import java.io.Serializable

data class Personaje(
    val user: Users? = null,
    val name: String? = null,
    val background: Background? = null,
    var characterClass: Classe? = null,
    val level: Level? = null,
    val race: Race? = null,
    val attributes: Attributes? = null
): Serializable