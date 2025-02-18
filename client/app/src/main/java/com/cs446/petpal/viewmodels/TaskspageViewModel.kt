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
import com.cs446.petpal.models.Task
import com.cs446.petpal.models.User

class TaskspageViewModel: ViewModel() {
    private val client = OkHttpClient()

    fun createTaskForUser(description: String, status: String, onResult: (Boolean, Task?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO)
        {
            var successfulTaskAdded = false
            var task: Task? = null
            try {
                val json = JSONObject().apply {
                    put("description", description)
                    put("status", status)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                // Build the POST request
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/createTaskForUser") // Adjust the endpoint as needed
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    successfulTaskAdded = response.isSuccessful
                    if (successfulTaskAdded) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        task = Task(
                            description = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("description")),
                            status = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("status")),
                        )
                    }
                    else {
                        // Optionally log error details from response
                        println("Login error: ${response.body?.string()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulTaskAdded = false
            }
            onResult(successfulTaskAdded, task)
        }
    }
}