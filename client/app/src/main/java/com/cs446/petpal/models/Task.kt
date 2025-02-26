package com.cs446.petpal.models

import androidx.compose.runtime.MutableState

data class Task(
    val description: MutableState<String>,
    val status: MutableState<String>,
    var userId: String? = "",
    var taskId: String = "",
)