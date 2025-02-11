package com.cs446.petpal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.cs446.petpal.models.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
class SignUpViewModel : ViewModel() {

    private val client = OkHttpClient()

    fun registerUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var successfulRegister: Boolean
            try {
                val json = JSONObject().apply {
                    put("firstName", user.firstName.value)
                    put("lastName", user.lastName.value)
                    put("address", user.address.value)
                    put("emailAddress", user.email.value)
                    put("password", user.password.value)
                    put("userType", user.userType.value)
                }

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
                        user.userId = jsonResponse.optString("userId") // Obtain userId from the response
                        println("Response: $jsonResponse")
                        // TODO: implement navigation to homepage here
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
