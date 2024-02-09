package com.abriljavier.milistadeheroes

data class Users (
    val user_id: Int,
    val username: String,
    val password: String,
    val pc_id: Int?,
){
}