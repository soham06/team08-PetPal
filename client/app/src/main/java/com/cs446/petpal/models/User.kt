package com.cs446.petpal.models

import androidx.compose.runtime.MutableState

data class User(
    val firstName: MutableState<String>,
    val lastName: MutableState<String>,
    val address: MutableState<String>,
    val email: MutableState<String>,
    val password: MutableState<String>,
    val userType: MutableState<String>,
//    var userId: String?,
)
