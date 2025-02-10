package com.cs446.petpal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.net.HttpURLConnection
import java.net.URL
import com.cs446.petpal.models.User
import org.json.JSONObject
import java.io.OutputStreamWriter

class NewSignUpViewModel : ViewModel() {

    fun registerUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var successfulRegister: Boolean
            try {
                val url = URL("http://10.0.2.2:3000/api/register")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; utf-8")
                    setRequestProperty("Accept", "application/json")

                    val jsonInputString = """
                    {
                        "firstName": "${user.firstName.value}",
                        "lastName": "${user.lastName.value}",
                        "address": "${user.address.value}",
                        "emailAddress": "${user.email.value}",
                        "password": "${user.password.value}",
                        "userType": "${user.userType.value}"
                    }
                    """.trimIndent()

                    OutputStreamWriter(outputStream).use { it.write(jsonInputString) }
                    val responseCode = responseCode
                    successfulRegister = responseCode == HttpURLConnection.HTTP_OK

                    if (successfulRegister) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        println("Response: $jsonResponse")
                        // TODO: implement navigation to homepage here
                    } else {
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Error Response: $response")
                    }
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                successfulRegister = false
            }
            onResult(successfulRegister)
        }
    }
}
