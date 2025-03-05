package com.cs446.petpal.viewmodels

import androidx.lifecycle.ViewModel
import com.cs446.petpal.repository.UserRepository
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class BarsViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {}
