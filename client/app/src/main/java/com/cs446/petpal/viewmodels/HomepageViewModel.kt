package com.cs446.petpal.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs446.petpal.models.Pet
import com.cs446.petpal.models.Event
import com.cs446.petpal.observer.EventSubject
import com.cs446.petpal.observer.EventsObserver
import com.cs446.petpal.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomepagePetsViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    private val eventSubject: EventSubject = EventSubject(userRepository.currentUser.value?.userId ?: "")
    val observer = EventsObserver()
    private val _registrationToken = mutableStateOf("")
    val registrationToken: State<String> = _registrationToken
    val events: State<List<Event>> get() = observer.events
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
        fetchAllPetsFromServer()
        eventSubject.attach(observer) // Register as an observer
        fetchUpcomingEvents()
        fetchFCMToken()
    }

    fun fetchFCMToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("PushNotification", "FCM Token: $token")
                _registrationToken.value = token
            } catch (e: Exception) {
                Log.e("PushNotification", "FCM token error", e)
            }
        }
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
        eventSubject.fetchEvents(registrationToken)
        // Filter out past events & sort by computed timestamp
        val now = System.currentTimeMillis()
        println("Events: ${observer.getEvents()}")
        println("Line: $events")
        val upcoming = events.value
            .filter { parseEventTimestamp(it.startDate.value, it.startTime.value) >= now }
            .sortedBy { parseEventTimestamp(it.startDate.value, it.startTime.value) }
            .take(3)

        _upcomingEvents.value = upcoming
    }

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
