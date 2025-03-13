package com.cs446.petpal.models

import androidx.compose.runtime.MutableState

data class Pet(
    val petId: String = "",
    val name: MutableState<String>,
    val animal: String,
    val breed: String,
    val gender: MutableState<String>,
    val age: MutableState<Int>,
    val birthday: String,
    val weight: MutableState<Double>,
    val insuranceProvider: MutableState<String>,
    val policyNumber: MutableState<String>,
    val medicationName: MutableState<String>,
    val medicationDosage: MutableState<String>,
    val sharedUsers: MutableState<Array<String>>? = null
)