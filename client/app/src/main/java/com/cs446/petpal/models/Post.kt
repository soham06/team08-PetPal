package com.cs446.petpal.models

import androidx.compose.runtime.MutableState

data class Post(
    val name: MutableState<String>,
    val city: MutableState<String>,
    val phone: MutableState<String>,
    val email: MutableState<String>,
    val description: MutableState<String>,
    var postId: String? = null
) {
    data class Memento(val name: String,
                       val city: String,
                       val phone: String,
                       val email: String,
                       val description: String,
                       var postId: String?)

    fun save(): Memento {
        return Memento(
            name.value, city.value, phone.value, email.value, description.value, postId
        )
    }

    fun restore(memento: Memento) {
        name.value = memento.name
        city.value = memento.city
        phone.value = memento.phone
        email.value = memento.email
        description.value = memento.description
    }
}