package com.cs446.petpal.viewmodels

import android.util.Log
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
import org.json.JSONArray
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class TaskspageViewModel: ViewModel() {
    private val client = OkHttpClient()
    private val _tasks = mutableStateOf<List<Task>>(emptyList())
    val tasks: State<List<Task>> = _tasks

    init {
        getTasksForUser()
    }

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
                    .url("http://10.0.2.2:3000/api/tasks/PcjsCSow5nnbIFo5cowm") // REPLACE THIS TO ACTUALLY USE USERID
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
                            description = mutableStateOf(jsonResponse.optString("description")),
                            status = mutableStateOf(jsonResponse.optString("status")),
                        )
                        task.taskId = jsonResponse.optString("taskId")
                        val updatedTasks = _tasks.value.toMutableList() // Create a mutable copy
                        updatedTasks.add(task!!)
                        _tasks.value = updatedTasks
                    }
                    else {
                        // Optionally log error details from response
                        println("Adding task failed: ${response.body?.string()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulTaskAdded = false
            }
            onResult(successfulTaskAdded, task)
        }
    }

    fun deleteTaskForUser(taskId: String, onResult: (Boolean, Task?) -> Unit) {
        Log.d("TASKID", taskId)
        viewModelScope.launch(Dispatchers.IO)
        {
            var successfulTaskDeleted = false
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/tasks/$taskId") // REPLACE THIS TO ACTUALLY USE USERID
                    .delete()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    successfulTaskDeleted = response.isSuccessful
                    if (successfulTaskDeleted) {
                        _tasks.value = _tasks.value.filterNot { it.taskId == taskId }
                    }
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                successfulTaskDeleted = false
            }
        }
    }
    fun getTasksForUser() {
        viewModelScope.launch(Dispatchers.IO)
        {
            var successfulTaskRetrived = false
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/tasks/PcjsCSow5nnbIFo5cowm") // REPLACE THIS TO ACTUALLY USE USERID
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    successfulTaskRetrived = response.isSuccessful
                    if(successfulTaskRetrived) {
                        val responseBody = response.body?.string()
                        val tasksArray = JSONArray(responseBody)

                        val tasks = mutableListOf<Task>()
                        for (i in 0 until tasksArray.length()) {
                            val taskJson = tasksArray.getJSONObject(i)
                            val task = Task(
                                description = mutableStateOf(taskJson.getString("description")),
                                status = mutableStateOf(taskJson.getString("status")),
                            )
                            task.taskId = taskJson.optString("taskId")
                            tasks.add(task)
                        }
                        _tasks.value = tasks
                    }
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                successfulTaskRetrived = false
            }
        }
    }
}