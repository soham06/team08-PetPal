package com.cs446.petpal.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import com.google.firebase.Firebase


// TODO: update this code to use our backend api
class SignUpViewModel : ViewModel() {
    private val TAG = "EmailAuth"
    private val auth: FirebaseAuth = Firebase.auth

    val user = MutableStateFlow<FirebaseUser?>(null)

    val errorMessage = MutableStateFlow<String?>(null)

    fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    user.value = auth.currentUser
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    errorMessage.value = "Authentication failed."
                }
            }
    }

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    user.value = auth.currentUser
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    errorMessage.value = "Authentication failed."
                }
            }
    }

    fun signOut() {
        auth.signOut()
        user.value = null
    }
}
