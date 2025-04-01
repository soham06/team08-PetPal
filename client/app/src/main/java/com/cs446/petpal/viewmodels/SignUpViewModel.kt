package com.cs446.petpal.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.cs446.petpal.repository.UserRepository
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {

    private val client = OkHttpClient()

    fun registerUser(firstName: String, lastName: String, address: String, email: String,
                     hashedPassword: String, userType: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var successfulRegister: Boolean
            try {
                userRepository.createUser(
                    mutableStateOf(firstName),
                    mutableStateOf(lastName),
                    mutableStateOf(address),
                    mutableStateOf(email),
                    mutableStateOf(hashedPassword),
                    mutableStateOf(userType)
                )

                val json = JSONObject().apply {
                    put("firstName", userRepository.currentUser.value?.firstName?.value)
                    put("lastName", userRepository.currentUser.value?.lastName?.value)
                    put("address", userRepository.currentUser.value?.address?.value)
                    put("emailAddress", userRepository.currentUser.value?.email?.value)
                    put("password", userRepository.currentUser.value?.password?.value)
                    put("userType", userRepository.currentUser.value?.userType?.value)
                }

                println("json object: $json")

                val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/register")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    successfulRegister = response.isSuccessful

                    if (successfulRegister) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")

                        val userId = jsonResponse.optString("userId")
                        userRepository.setUserId(userId)

                        println("Response: $jsonResponse")
                        println("User: ${userRepository.currentUser.value}")
                    } else {
                        val errorBody = response.body?.string()
                        println("Error Response: $errorBody")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulRegister = false
            }
            onResult(successfulRegister)
        }
    }
}
