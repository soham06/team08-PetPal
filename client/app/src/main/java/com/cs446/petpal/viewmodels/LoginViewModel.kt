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

class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val client = OkHttpClient()

    /**
     * Calls the login endpoint with the provided email and hashed password.
     * Returns a Boolean indicating success and an optional User instance on success.
     */
    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var successfulLogin: Boolean
            try {
                // Build JSON payload
                val json = JSONObject().apply {
                    put("emailAddress", email)
                    put("password", password)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                // Build the POST request
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/login") // Adjust the endpoint as needed
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                // Execute the request synchronously on the IO dispatcher
                client.newCall(request).execute().use { response ->
                    successfulLogin = response.isSuccessful
                    if (successfulLogin) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")

                        // Create a User instance from the JSON response.
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
                        // Optionally log error details from response
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
