package com.cs446.petpal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import com.cs446.petpal.models.User

class LoginViewModel : ViewModel() {

    private val client = OkHttpClient()

    /**
     * Calls the login endpoint with the provided email and hashed password.
     * Returns a Boolean indicating success and an optional User instance on success.
     */
    fun loginUser(email: String, password: String, onResult: (Boolean, User?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var successfulLogin = false
            var user: User? = null
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
                        // Adjust the field names as per your API response.
                        user = User(
                            firstName = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("firstName")),
                            lastName = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("lastName")),
                            address = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("address")),
                            email = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("emailAddress")),
                            password = androidx.compose.runtime.mutableStateOf(""), // Not needed after login
                            userType = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("userType"))
                        )
                        user.userId = jsonResponse.optString("userId")
                    } else {
                        // Optionally log error details from response
                        println("Login error: ${response.body?.string()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulLogin = false
            }
            onResult(successfulLogin, user)
        }
    }
}
