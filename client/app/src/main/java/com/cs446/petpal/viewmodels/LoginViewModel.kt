package com.cs446.petpal.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import com.cs446.petpal.repository.UserRepository
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class LoginViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {

    private val client = OkHttpClient()

    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var successfulLogin: Boolean
            try {
                val json = JSONObject().apply {
                    put("emailAddress", email)
                    put("password", password)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/login")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    successfulLogin = response.isSuccessful
                    if (successfulLogin) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")

                        val retFirstName = jsonResponse.optString("firstName")
                        val retLastName = jsonResponse.optString("lastName")
                        val retAddress = jsonResponse.optString("address")
                        val retEmail = jsonResponse.optString("emailAddress")
                        val retHashedPassword = jsonResponse.optString("password")
                        val retUserType = jsonResponse.optString("userType")
                        val retUserId = jsonResponse.optString("userId")
                        userRepository.createUser(
                            mutableStateOf(retFirstName),
                            mutableStateOf(retLastName),
                            mutableStateOf(retAddress),
                            mutableStateOf(retEmail),
                            mutableStateOf(retHashedPassword),
                            mutableStateOf(retUserType)
                        )
                        userRepository.setUserId(retUserId)

                        println("Response: $jsonResponse")
                        println("User: ${userRepository.currentUser.value}")
                    } else {
                        println("Login error: ${response.body?.string()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulLogin = false
            }
            onResult(successfulLogin)
        }
    }
}
