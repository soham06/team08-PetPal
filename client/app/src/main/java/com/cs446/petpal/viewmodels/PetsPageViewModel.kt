package com.cs446.petpal.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs446.petpal.models.Pet
import com.cs446.petpal.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel()
class PetsPageViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {

    private val client = OkHttpClient()

    private val _myPetsList = MutableStateFlow<List<Pet>>(emptyList())
    val myPetsList: StateFlow<List<Pet>> = _myPetsList

    private val _sharedPetsList = MutableStateFlow<List<Pet>>(emptyList())
    val sharedPetsList: StateFlow<List<Pet>> = _sharedPetsList

    private var _allPetsList = MutableStateFlow<List<Pet>>(emptyList())

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet

    var currentUserId: String = userRepository.currentUser.value?.userId.toString();
    var currentUserEmail: String = userRepository.currentUser.value?.email?.value.toString();

    fun fetchAllPetsFromServer() {
        fetchMyPetsFromServer()
        fetchSharedPetsFromServer()
        GlobalScope.launch {
            combine(_myPetsList, _sharedPetsList) { list1, list2 ->
                list1 + list2
            }.collect { combinedList ->
                _allPetsList.value = combinedList
            }
        }
        println(_allPetsList.value)
    }

    fun fetchMyPetsFromServer() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/$currentUserId")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("PetsPageViewModel", "Error fetching pets: ${response.code}")
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
                        val sharedUsersAsJsonArray = obj.optJSONArray("sharedUsers")
                        var sharedUsers:Array<String> = emptyArray()

                        if (sharedUsersAsJsonArray != null) {
                            if (sharedUsersAsJsonArray.length() > 0) {
                                for (i in 0 until sharedUsersAsJsonArray.length()) {
                                    val user = sharedUsersAsJsonArray.optString(i)
                                    sharedUsers += user
                                }
                            }
                        }


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
                            medicationDosage = mutableStateOf(medicationDosage),
                            sharedUsers = mutableStateOf(sharedUsers)
                        )
                        petList.add(pet)
                    }

                    _myPetsList.value = petList
                    _selectedPet.value = petList.firstOrNull()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchSharedPetsFromServer() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/share/$currentUserId")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("PetsPageViewModel", "Error fetching shared pets: ${response.code}")
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
                        val sharedUsersAsJsonArray = obj.optJSONArray("sharedUsers")
                        var sharedUsers:Array<String> = emptyArray()

                        if (sharedUsersAsJsonArray != null) {
                            if (sharedUsersAsJsonArray.length() > 0) {
                                for (i in 0 until sharedUsersAsJsonArray.length()) {
                                    val user = sharedUsersAsJsonArray.optString(i)
                                    sharedUsers += user
                                }
                            }
                        }

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
                            medicationDosage = mutableStateOf(medicationDosage),
                            sharedUsers = mutableStateOf(sharedUsers)
                        )
                        petList.add(pet)
                    }

                    _sharedPetsList.value = petList
                    if (_myPetsList.value.isEmpty()) {
                        _selectedPet.value = petList.firstOrNull()
                    }
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
        viewModelScope.launch(Dispatchers.IO) {
            var successfulPetAdded = false
            var pet: Pet? = null
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
                    .url("http://10.0.2.2:3000/api/pets/$currentUserId")
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
                        fetchAllPetsFromServer()
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
                    .url("http://10.0.2.2:3000/api/pets/$petId")
                    .patch(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    success = response.isSuccessful
                    if (success) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
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

                        fetchAllPetsFromServer()
                    } else {
                        Log.e("PetsPageViewModel", "Failed to update pet: ${response.code}")
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
        _selectedPet.value = _allPetsList.value.find { it.petId == petId }
    }

    fun isSharedPetProfile(): Boolean {
        if (_selectedPet.value?.sharedUsers != null) {
            if (_selectedPet.value?.sharedUsers?.value?.contains(currentUserId) == true) {
                return true
            }
        }
        return false
    }

    fun deletePetForUser(petId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/$petId")
                    .delete()
                    .build()

                client.newCall(request).execute().use { response ->
                    val success = response.isSuccessful
                    if (success) {
                        fetchAllPetsFromServer()
                    }
                    onResult(success)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun sharePetToUser(
        petId: String,
        emailAddress: String,
        onResult: (Boolean, Pet?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (emailAddress == currentUserEmail) {
                onResult(false, _selectedPet.value)
                return@launch
            }

            var success: Boolean
            try {
                val json = JSONObject().apply {
                    put("emailAddress", emailAddress)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/share/$petId")
                    .patch(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    success = response.isSuccessful
                    if (success) {
                        fetchAllPetsFromServer()
                    } else {
                        Log.e("PetsPageViewModel", "Failed to share pet: ${response.code}")
                        success = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                success = false
            }
            onResult(success, _selectedPet.value)
        }
    }

    fun unsharePetToUser(
        petId: String,
        emailAddress: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var success: Boolean
            try {
                val json = JSONObject().apply {
                    put("emailAddress", emailAddress)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/pets/share/$petId")
                    .delete(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    success = response.isSuccessful
                    if (success) {
                        fetchAllPetsFromServer()
                    } else {
                        Log.e("PetsPageViewModel", "Failed to unshare pet: ${response.code}")
                        success = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                success = false
            }
            onResult(success)
        }
    }


    fun getUserEmailAddress(userId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var userEmailAddress: String = ""
            var success: Boolean = false
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/users/$userId")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        userEmailAddress = jsonResponse.optString("emailAddress")
                        success = true
                    } else {
                        success = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                success = false
            }
            onResult(success, userEmailAddress)
        }
    }
}


