package com.abriljavier.milistadeheroes

data class Class(
    val classId: Int?,
    val className: String,
    val hitDie: String,
    val savingThrowProficiencies: String,
    val abilitiesProficiencies: String,
    val armorWeaponProficiencies: String
) {
}