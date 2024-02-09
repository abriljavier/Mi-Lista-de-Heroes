package com.abriljavier.milistadeheroes

data class PersonalityTrait(
    val id: Int,
    val description: String
)

data class Ideal(
    val id: Int,
    val description: String
)

data class Link(
    val id: Int,
    val description: String
)

data class Flaw(
    val id: Int,
    val description: String
)

data class Traits(
    val personalityTraits: Map<Int, String>,
    val ideals: Map<Int, String>,
    val links: Map<Int, String>,
    val flaws: Map<Int, String>
)

data class Background(
    val backgroundId: Int,
    val bgName: String,
    val competencies: String,
    val tools: String?,
    val languages: Int,
    val items: String,
    val traits: Traits
)
