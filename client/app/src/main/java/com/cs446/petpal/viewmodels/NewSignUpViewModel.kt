package com.cs446.petpal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

import com.cs446.petpal.models.User

class NewSignUpViewModel : ViewModel() {

    fun registerUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = try {
                val url = URL("http://localhost:3000/api/register")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                connection.setRequestProperty("Accept", "application/json")

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

                connection.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = connection.responseCode
                responseCode == HttpsURLConnection.HTTP_OK
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            onResult(result)
        }
    }
}
