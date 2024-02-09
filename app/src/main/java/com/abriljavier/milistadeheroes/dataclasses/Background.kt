package com.abriljavier.milistadeheroes.dataclasses

data class PersonalityTrait(
    val id: Int? = null,
    val description: String? = null
)

data class Ideal(
    val id: Int? = null,
    val description: String? = null
)

data class Link(
    val id: Int? = null,
    val description: String? = null
)

data class Flaw(
    val id: Int? = null,
    val description: String? = null
)

data class Traits(
    val personalityTraits: Map<Int, String>? = null,
    val ideals: Map<Int, String>? = null,
    val links: Map<Int, String>? = null,
    val flaws: Map<Int, String>? = null
)

data class Background(
    val backgroundId: Int? = null,
    val bgName: String? = null,
    val competencies: String? = null,
    val tools: String? = null,
    val languages: Int? = null,
    val items: String? = null,
    val traits: Traits? = null
)