package com.cs446.petpal.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs446.petpal.models.Pet
import com.cs446.petpal.models.Event
import com.cs446.petpal.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomepagePetsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val client = OkHttpClient()

    // If the user ID is null/empty, we use "TEST_USER" as a fallback
    private val currentUserId: String = userRepository.currentUser.value?.userId
        ?.takeIf { it.isNotBlank() }
        ?: "QZ44r2hBso9VWXHLfpJM"

    private val _myPetsList = MutableStateFlow<List<Pet>>(emptyList())
    val myPetsList: StateFlow<List<Pet>> = _myPetsList

    private val _sharedPetsList = MutableStateFlow<List<Pet>>(emptyList())
    val sharedPetsList: StateFlow<List<Pet>> = _sharedPetsList

    private val _allPetsList = MutableStateFlow<List<Pet>>(emptyList())
    val allPetsList: StateFlow<List<Pet>> = _allPetsList

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet


    private val _upcomingEvents = MutableStateFlow<List<Event>>(emptyList())
    val upcomingEvents: StateFlow<List<Event>> = _upcomingEvents

    init {
        // Fetch pets and upcoming events at initialization
        fetchAllPetsFromServer()
        fetchUpcomingEvents()
    }

    fun fetchAllPetsFromServer() {
        fetchMyPetsFromServer()
        fetchSharedPetsFromServer()

        GlobalScope.launch {
            combine(_myPetsList, _sharedPetsList) { myPets, sharedPets ->
                myPets + sharedPets
            }.collect { combined ->
                _allPetsList.value = combined
            }
        }
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
                        Log.e("HomepagePetsViewModel", "Error fetching my pets: ${response.code}")
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
                        val genderRaw = obj.optString("gender", "--")
                        val gender = if (genderRaw == "m") mutableStateOf("Male") else mutableStateOf("Female")
                        val birthday = obj.optString("birthday", "--")
                        val age = obj.optInt("age", 0)
                        val weight = obj.optDouble("weight", 0.0)
                        val insuranceProvider = obj.optString("insuranceProvider", "--")
                        val policyNumber = obj.optString("policyNumber", "--")
                        val medicationName = obj.optString("medicationName", "--")
                        val medicationDosage = obj.optString("medicationDosage", "--")

                        // Parse sharedUsers if needed
                        val sharedUsersAsJsonArray = obj.optJSONArray("sharedUsers")
                        var sharedUsers: Array<String> = emptyArray()
                        if (sharedUsersAsJsonArray != null && sharedUsersAsJsonArray.length() > 0) {
                            for (j in 0 until sharedUsersAsJsonArray.length()) {
                                val user = sharedUsersAsJsonArray.optString(j)
                                sharedUsers += user
                            }
                        }

                        val pet = Pet(
                            petId = id,
                            name = mutableStateOf(name),
                            animal = animal,
                            breed = breed,
                            gender = gender,
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
                        Log.e("HomepagePetsViewModel", "Error fetching shared pets: ${response.code}")
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
                        val genderRaw = obj.optString("gender", "--")
                        val gender = if (genderRaw == "m") mutableStateOf("Male") else mutableStateOf("Female")
                        val birthday = obj.optString("birthday", "--")
                        val age = obj.optInt("age", 0)
                        val weight = obj.optDouble("weight", 0.0)
                        val insuranceProvider = obj.optString("insuranceProvider", "--")
                        val policyNumber = obj.optString("policyNumber", "--")
                        val medicationName = obj.optString("medicationName", "--")
                        val medicationDosage = obj.optString("medicationDosage", "--")

                        // Parse sharedUsers
                        val sharedUsersAsJsonArray = obj.optJSONArray("sharedUsers")
                        var sharedUsers: Array<String> = emptyArray()
                        if (sharedUsersAsJsonArray != null && sharedUsersAsJsonArray.length() > 0) {
                            for (j in 0 until sharedUsersAsJsonArray.length()) {
                                val user = sharedUsersAsJsonArray.optString(j)
                                sharedUsers += user
                            }
                        }

                        val pet = Pet(
                            petId = id,
                            name = mutableStateOf(name),
                            animal = animal,
                            breed = breed,
                            gender = gender,
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
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectPet(petId: String) {
        _selectedPet.value = _allPetsList.value.find { it.petId == petId }
    }


    fun fetchUpcomingEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/events/$currentUserId")
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("HomepagePetsViewModel", "Error fetching events: ${response.code}")
                        return@use
                    }
                    val responseBody = response.body?.string() ?: "[]"
                    val eventsArray = JSONArray(responseBody)
                    val allEvents = mutableListOf<Event>()

                    for (i in 0 until eventsArray.length()) {
                        val obj = eventsArray.getJSONObject(i)
                        val eventId = obj.optString("eventId", "")
                        val description = obj.optString("description", "")
                        val startDate = obj.optString("startDate", "")
                        val endDate = obj.optString("endDate", "")
                        val startTime = obj.optString("startTime", "")
                        val endTime = obj.optString("endTime", "")
                        val location = obj.optString("location", "")

                        val event = Event(
                            eventId = eventId,
                            description = mutableStateOf(description),
                            startDate = mutableStateOf(startDate),
                            endDate = mutableStateOf(endDate),
                            startTime = mutableStateOf(startTime),
                            endTime = mutableStateOf(endTime),
                            location = mutableStateOf(location)
                        )
                        allEvents.add(event)
                    }

                    // Filter out past events & sort by computed timestamp
                    val now = System.currentTimeMillis()
                    val upcoming = allEvents
                        .filter { parseEventTimestamp(it.startDate.value, it.startTime.value) >= now }
                        .sortedBy { parseEventTimestamp(it.startDate.value, it.startTime.value) }
                        .take(3)

                    _upcomingEvents.value = upcoming
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("HomepagePetsViewModel", "Exception fetching events", e)
            }
        }
    }

    // Helper to parse date/time into a timestamp
    // Adjust if your server uses a different format, e.g. "MM-dd-yyyy h:mma"
    private fun parseEventTimestamp(dateStr: String, timeStr: String): Long {
        return try {
            val sdf = SimpleDateFormat("MM-dd-yyyy h:mma", Locale.US)
            val dateTimeString = "$dateStr $timeStr"
            sdf.parse(dateTimeString)?.time ?: Long.MAX_VALUE
        } catch (e: Exception) {
            Long.MAX_VALUE
        }
    }
}
