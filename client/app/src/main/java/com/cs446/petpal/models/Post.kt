package com.cs446.petpal.models

import androidx.compose.runtime.MutableState

data class Post(
    val name: MutableState<String>,
    val city: MutableState<String>,
    val phone: MutableState<String>,
    val email: MutableState<String>,
    val description: MutableState<String>,
    val date: MutableState<String>,
    var postId: String? = null
)