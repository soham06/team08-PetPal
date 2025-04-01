package com.cs446.petpal.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.cs446.petpal.models.User

class UserRepository {
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> get() = _currentUser

    fun createUser(
        firstName: MutableState<String>,
        lastName: MutableState<String>,
        address: MutableState<String>,
        email: MutableState<String>,
        password: MutableState<String>,
        userType: MutableState<String>
    ) {
        _currentUser.value = User(
            firstName = firstName,
            lastName = lastName,
            address = address,
            email = email,
            password = password,
            userType = userType,
            userId = ""
        )
    }

    fun resetUser() {
        _currentUser.value = User(
            firstName = mutableStateOf(""),
            lastName = mutableStateOf(""),
            address = mutableStateOf(""),
            email = mutableStateOf(""),
            password = mutableStateOf(""),
            userType = mutableStateOf(""),
            userId = ""
        )
    }

    fun setUserId(newUserId: String) {
        val user = _currentUser.value
        if (user != null) {
            _currentUser.value = user.copy(userId = newUserId)
        }
    }
}