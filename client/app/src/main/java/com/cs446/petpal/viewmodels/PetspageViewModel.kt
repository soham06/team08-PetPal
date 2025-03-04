package com.cs446.petpal.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import com.cs446.petpal.models.Pet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

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

                    println(responseBody)

                    // Parse each pet JSON object
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        // Adjust field names if your server returns different ones (like "_id" or "breed" etc.)
                        val id = obj.optString("petId", "")
                        val name = obj.optString("name", "No Name")
                        val animal = obj.optString("animal", "--")
                        val breed = obj.optString("breed", "--")
                        val gender = obj.optString("gender", "--")
                        val birthday = obj.optString("birthday", "--")
                        val age = obj.optInt("age", 0)
                        val weight = obj.optDouble("weight", 0.0)
                        val insuranceProvider = obj.optString("insuranceProvider", "--")
                        val policyNumber = obj.optString("policyNumber", "--")
                        val medicationName = obj.optString("medicationName", "--")
                        val medicationDosage = obj.optString("medicationDosage", "--")
                        // Your server might not return age, weight, insurance, etc. We’ll default them.
                        // If you store them in your DB, parse them as well.
                        val pet = Pet(
                            petId = id,
                            name = mutableStateOf(name),
                            animal = animal,
                            breed = breed,
                            gender = if (gender == "m") mutableStateOf("Male") else mutableStateOf("Female"),
                            age = mutableIntStateOf(age), // or parse from 'birthday' if you want
                            birthday = birthday,
                            weight = mutableDoubleStateOf(weight),
                            insuranceProvider = mutableStateOf(insuranceProvider),
                            policyNumber = mutableStateOf(policyNumber),
                            medicationName = mutableStateOf(medicationName),
                            medicationDosage = mutableStateOf(medicationDosage)
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

    fun createPetForUser(
        name: String,
        animal: String,
        breed: String,
        gender: String,
        birthday: String,
        age: Int,
        weight: Double,
        insuranceProvider: String,
        insurancePolicyNumber: String,
        medicationName: String,
        medicationDosage: String,
        onResult: (Boolean, Pet?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO)
        {
            var successfulPetAdded = false
            var pet: Pet? = null
            val currentUserID = "CgL0tQ81vTFMGn2DyA9M"
            try {
                val json = JSONObject().apply {
                    put("name", name)
                    put("animal", animal)
                    put("breed", breed)
                    put("gender", gender)
                    put("birthday", birthday)
                    put("age", age)
                    put("weight", weight)
                    put("insuranceProvider", insuranceProvider)
                    put("policyNumber", insurancePolicyNumber)
                    put("medicationName", medicationName)
                    put("medicationDosage", medicationDosage)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//                // Build the POST request
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/$currentUserID") // REPLACE THIS TO ACTUALLY USE USERID
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    successfulPetAdded = response.isSuccessful
                    if (successfulPetAdded) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        println(jsonResponse)
                        pet = Pet(
                            petId = jsonResponse.optString("petId", ""),
                            name = mutableStateOf(jsonResponse.optString("name", "")),
                            animal = jsonResponse.optString("animal", "--"),
                            breed = jsonResponse.optString("breed", "--"),
                            gender = if (jsonResponse.optString("gender", "--") == "m") mutableStateOf("Male") else mutableStateOf("Female"),
                            age = mutableIntStateOf(jsonResponse.optInt("age", 0)),
                            birthday = jsonResponse.optString("birthday", "--"),
                            weight = mutableDoubleStateOf(jsonResponse.optDouble("weight", 0.0)),
                            insuranceProvider = mutableStateOf(jsonResponse.optString("insuranceProvider", "--")),
                            policyNumber = mutableStateOf(jsonResponse.optString("policyNumber", "--")),
                            medicationName = mutableStateOf(jsonResponse.optString("medicationName", "--")),
                            medicationDosage = mutableStateOf(jsonResponse.optString("medicationDosage", "--"))
                        )
                        fetchPetsFromServer(currentUserID)
                    }
                    else {
                        println("Adding pet failed: ${response.body?.string()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulPetAdded = false
            }
            onResult(successfulPetAdded, pet)
        }
    }

    fun selectPet(petId: String) {
        _selectedPet.value = _petsList.value.find { it.petId == petId }
    }
}
