package com.cs446.petpal.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // -- Existing fetchPetsFromServer (unchanged) --
    fun fetchPetsFromServer(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/$userId")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("PetspageViewModel", "Error fetching pets: ${response.code}")
                        return@use
                    }
                    val responseBody = response.body?.string() ?: "[]"
                    val jsonArray = JSONArray(responseBody)
                    val petList = mutableListOf<Pet>()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
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

                        val pet = Pet(
                            petId = id,
                            name = mutableStateOf(name),
                            animal = animal,
                            breed = breed,
                            gender = if (gender == "m") mutableStateOf("Male") else mutableStateOf("Female"),
                            age = mutableIntStateOf(age),
                            birthday = birthday,
                            weight = mutableDoubleStateOf(weight),
                            insuranceProvider = mutableStateOf(insuranceProvider),
                            policyNumber = mutableStateOf(policyNumber),
                            medicationName = mutableStateOf(medicationName),
                            medicationDosage = mutableStateOf(medicationDosage)
                        )
                        petList.add(pet)
                    }

                    _petsList.value = petList
                    _selectedPet.value = petList.firstOrNull()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // -- Existing createPetForUser (unchanged) --
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
        viewModelScope.launch(Dispatchers.IO) {
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

                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/$currentUserID")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    successfulPetAdded = response.isSuccessful
                    if (successfulPetAdded) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
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
                        // Refresh pet list
                        fetchPetsFromServer(currentUserID)
                    } else {
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

    // -- NEW: updatePetForUser function --
    fun updatePetForUser(
        petId: String,
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
        viewModelScope.launch(Dispatchers.IO) {
            var success = false
            var updatedPet: Pet? = null
            try {
                // Build JSON body with updated fields
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

                // Build PATCH or PUT request (depends on your server)
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/$petId")
                    // .put(...) or .patch(...) depending on your backend
                    .patch(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    success = response.isSuccessful
                    if (success) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        // Build updatedPet from server response
                        updatedPet = Pet(
                            petId = jsonResponse.optString("petId", petId),
                            name = mutableStateOf(jsonResponse.optString("name", name)),
                            animal = jsonResponse.optString("animal", animal),
                            breed = jsonResponse.optString("breed", breed),
                            gender = if (jsonResponse.optString("gender", gender) == "m") mutableStateOf("Male") else mutableStateOf("Female"),
                            age = mutableIntStateOf(jsonResponse.optInt("age", age)),
                            birthday = jsonResponse.optString("birthday", birthday),
                            weight = mutableDoubleStateOf(jsonResponse.optDouble("weight", weight)),
                            insuranceProvider = mutableStateOf(jsonResponse.optString("insuranceProvider", insuranceProvider)),
                            policyNumber = mutableStateOf(jsonResponse.optString("policyNumber", insurancePolicyNumber)),
                            medicationName = mutableStateOf(jsonResponse.optString("medicationName", medicationName)),
                            medicationDosage = mutableStateOf(jsonResponse.optString("medicationDosage", medicationDosage))
                        )

                        val currentUserID = "CgL0tQ81vTFMGn2DyA9M"
                        fetchPetsFromServer(currentUserID)
                    } else {
                        Log.e("PetspageViewModel", "Failed to update pet: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                success = false
            }
            onResult(success, updatedPet)
        }
    }


    fun selectPet(petId: String) {
        _selectedPet.value = _petsList.value.find { it.petId == petId }
    }

    fun deletePetForUser(petId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Build the DELETE request to remove the pet
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/$petId")
                    .delete()
                    .build()

                client.newCall(request).execute().use { response ->
                    val success = response.isSuccessful
                    if (success) {
                        val currentUserID = "CgL0tQ81vTFMGn2DyA9M"
                        fetchPetsFromServer(currentUserID)
                    }
                    onResult(success)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

}


