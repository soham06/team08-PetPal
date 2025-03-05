package com.cs446.petpal.viewmodels

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import com.cs446.petpal.repository.UserRepository

@HiltViewModel
class ProfilePageViewModel @Inject constructor(
    val userRepository: UserRepository,
    ) : ViewModel() {
    val firstName: String
        get() = userRepository.currentUser.value?.firstName?.value ?: ""
    val lastName: String
        get() = userRepository.currentUser.value?.lastName?.value ?: ""
    val address: String
        get() = userRepository.currentUser.value?.address?.value ?: ""
    val email: String
        get() = userRepository.currentUser.value?.email?.value ?: ""
    val userType: String
        get() = userRepository.currentUser.value?.userType?.value ?: ""
    }
