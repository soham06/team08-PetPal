package com.cs446.petpal.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

data class Pet(
    val id: String,
    val name: String,
    val gender: String,
    val age: Int,
    val birthdate: String,
    val weight: Double,
    val insuranceProvider: String,
    val policyNumber: String,
    val medicationName: String,
    val medicationDosage: String
)

class PetspageViewModel : ViewModel() {

    private val client = OkHttpClient()

    // List of pets that drives the UI
    private val _petsList = MutableStateFlow<List<Pet>>(emptyList())
    val petsList: StateFlow<List<Pet>> = _petsList

    // Currently selected pet
    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet

    /**
     * Fetch pets for a given user from the server:
     * GET /api/pets/:userId
     */
    fun fetchPetsFromServer(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Build the GET request
                val request = Request.Builder()
                    // If running on Android emulator, use 10.0.2.2 for localhost
                    .url("http://10.0.2.2:3000/api/pets/$userId")
                    .get()
                    .build()

                // Execute synchronously on the IO dispatcher
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("PetspageViewModel", "Error fetching pets: ${response.code}")
                        return@use
                    }
                    val responseBody = response.body?.string() ?: "[]"
                    val jsonArray = JSONArray(responseBody)
                    val petList = mutableListOf<Pet>()

                    // Parse each pet JSON object
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        // Adjust field names if your server returns different ones (like "_id" or "breed" etc.)
                        val id = obj.optString("_id", "")
                        val name = obj.optString("name", "No Name")
                        val gender = obj.optString("gender", "--")
                        val birthday = obj.optString("birthday", "--")
                        // Your server might not return age, weight, insurance, etc. We’ll default them.
                        // If you store them in your DB, parse them as well.
                        val pet = Pet(
                            id = id,
                            name = name,
                            gender = if (gender == "m") "Male" else "Female",
                            age = 0, // or parse from 'birthday' if you want
                            birthdate = birthday,
                            weight = 0.0,
                            insuranceProvider = "--",
                            policyNumber = "--",
                            medicationName = "--",
                            medicationDosage = "--"
                        )
                        petList.add(pet)
                    }

                    // Update the StateFlows to recompose the UI
                    _petsList.value = petList
                    _selectedPet.value = petList.firstOrNull()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectPet(petId: String) {
        _selectedPet.value = _petsList.value.find { it.id == petId }
    }
}
