package com.abriljavier.milistadeheroes.dataclasses

import java.io.Serializable

data class Classe(
    val classId: Int? = null,
    val className: String? = null,
    val hitDie: String? = null,
    val savingThrowProficiencies: String? = null,
    val abilitiesProficiencies: String? = null,
    val armorWeaponProficiencies: String? = null
) : Serializable {
}