package com.cs446.petpal.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs446.petpal.models.Event
import com.cs446.petpal.models.Pet
import com.cs446.petpal.observer.EventSubject
import com.cs446.petpal.observer.EventsObserver
import com.cs446.petpal.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
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

    private val currentUserId: String = userRepository.currentUser.value?.userId
        ?.takeIf { it.isNotBlank() }
        ?: "QZ44r2hBso9VWXHLfpJM"

    private val _myPetsList = MutableStateFlow<List<Pet>>(emptyList())
    val myPetsList: StateFlow<List<Pet>> = _myPetsList

    private val _sharedPetsList = MutableStateFlow<List<Pet>>(emptyList())
    val sharedPetsList: StateFlow<List<Pet>> = _sharedPetsList

    init {
        fetchAllPetsFromServer()
        eventSubject.attach(observer)
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

    fun fetchUpcomingEvents() {
        eventSubject.fetchEvents(registrationToken)
    }
}
