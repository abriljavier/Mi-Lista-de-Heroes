package com.abriljavier.milistadeheroes.dataclasses

import java.io.Serializable

data class PersonalityTrait(
    val id: Int? = null,
    val description: String? = null
)

data class Ideal(
    val id: Int? = null,
    val description: String? = null
) : Serializable

data class Link(
    val id: Int? = null,
    val description: String? = null
) : Serializable

data class Flaw(
    val id: Int? = null,
    val description: String? = null
) : Serializable

data class Traits(
    var personalityTraits: Map<Int, String>? = null,
    var ideals: Map<Int, String>? = null,
    var links: Map<Int, String>? = null,
    var flaws: Map<Int, String>? = null
) : Serializable

data class Background(
    val backgroundId: Int? = null,
    val bgName: String? = null,
    val competencies: String? = null,
    val tools: String? = null,
    val languages: String? = null,
    val items: String? = null,
    var traits: Traits? = null
) : Serializable
