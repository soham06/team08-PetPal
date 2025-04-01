package com.cs446.petpal.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import com.cs446.petpal.models.Post
import com.cs446.petpal.models.Pet
import com.cs446.petpal.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Locale

private fun convertDateFormat(dateStr: String): String {
    val inputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.US)
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val date = inputFormat.parse(dateStr) ?: return ""
    return outputFormat.format(date)
}

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    // USER VALUES
    val firstName: String
        get() = userRepository.currentUser.value?.firstName?.value ?: ""
    val lastName: String
        get() = userRepository.currentUser.value?.lastName?.value ?: ""
    val email: String
        get() = userRepository.currentUser.value?.email?.value ?: ""

    private val client = OkHttpClient()

    val posts = mutableStateListOf<Post>()
    val pets = mutableStateListOf<Pet>()

    // Get current user ID from the repository.
    var currentUserId: String = userRepository.currentUser.value?.userId.toString()
    // Determine if the current user is a PetSitter.
    val isPetSitter: Boolean = userRepository.currentUser.value?.userType?.value == "Pet Sitter"

    init {
      //  getPostsForUser()
    }

    // Retrieve posts from the backend.
    // For PetSitters: fetch all posts.
    // For PetOwners: fetch only posts created by the current user.
    fun getPostsForUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = if (isPetSitter) {
                    "http://10.0.2.2:3000/api/postings/all" //"http://10.0.2.2:3000/api/postings" // endpoint for all posts
                } else {
                    "http://10.0.2.2:3000/api/postings/$currentUserId" // endpoint for user-specific posts
                }
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val postsArray = JSONArray(responseBody)
                        val postsList = mutableListOf<Post>()
                        for (i in 0 until postsArray.length()) {
                            val postJson = postsArray.getJSONObject(i)
                            val post = Post(
                                name = androidx.compose.runtime.mutableStateOf(postJson.getString("name")),
                                city = androidx.compose.runtime.mutableStateOf(postJson.getString("city")),
                                phone = androidx.compose.runtime.mutableStateOf(postJson.getString("phone")),
                                email = androidx.compose.runtime.mutableStateOf(postJson.getString("email")),
                                description = androidx.compose.runtime.mutableStateOf(postJson.getString("description")),
                                date = androidx.compose.runtime.mutableStateOf(postJson.optString("date", "")),
                                petId = androidx.compose.runtime.mutableStateOf(postJson.optString("petId", ""))
                            )
                            // Store the postId from the backend.
                            post.postId = postJson.optString("postId")
                            postsList.add(post)
                        }
                        posts.clear()
                        posts.addAll(postsList)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Optionally, log or report the error.
            }
        }
    }

    // Function to fetch user's pets from backend
    fun getPetsForUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "http://10.0.2.2:3000/api/pets/$currentUserId"
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: "[]"
                        val jsonArray = JSONArray(responseBody)
                        val petList = mutableListOf<Pet>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            // Create a pet using your model. Note that sharedUsers is set to null.
                            val pet = Pet(
                                petId = obj.optString("petId", ""),
                                name = androidx.compose.runtime.mutableStateOf(obj.optString("name", "No Name")),
                                animal = obj.optString("animal", "--"),
                                breed = obj.optString("breed", "--"),
                                gender = androidx.compose.runtime.mutableStateOf(
                                    if (obj.optString("gender", "--") == "m") "Male" else "Female"
                                ),
                                age = androidx.compose.runtime.mutableStateOf(obj.optInt("age", 0)),
                                birthday = obj.optString("birthday", "--"),
                                weight = androidx.compose.runtime.mutableStateOf(obj.optDouble("weight", 0.0)),
                                insuranceProvider = androidx.compose.runtime.mutableStateOf(obj.optString("insuranceProvider", "--")),
                                policyNumber = androidx.compose.runtime.mutableStateOf(obj.optString("policyNumber", "--")),
                                medicationName = androidx.compose.runtime.mutableStateOf(obj.optString("medicationName", "--")),
                                medicationDosage = androidx.compose.runtime.mutableStateOf(obj.optString("medicationDosage", "--")),
                                sharedUsers = null // Adjust if needed.
                            )
                            petList.add(pet)
                        }
                        // Switch to the main thread for state updates.
                        viewModelScope.launch(Dispatchers.Main) {
                            pets.clear()
                            pets.addAll(petList)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Create a new post.
    // Only allow creation if the user is a PetOwner.
    fun createPost(
        name: String,
        city: String,
        phone: String,
        email: String,
        description: String,
        date: String,
        petId: String,
        callback: (Boolean, String?) -> Unit
    ) {
        // Prevent PetSitters from creating posts.
        if (isPetSitter) {
            callback(false, "Pet Sitters cannot create posts.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            var success = false
            try {
                val json = JSONObject().apply {
                    put("name", name)
                    put("city", city)
                    put("phone", phone)
                    put("email", email)
                    put("description", description)
                    put("date", date)
                    put("petId", petId)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/postings/$currentUserId")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    success = response.isSuccessful
                    if (success) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        val newPost = Post(
                            name = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("name")),
                            city = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("city")),
                            phone = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("phone")),
                            email = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("email")),
                            description = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("description")),
                            date = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("date", "")),
                            petId = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("petId", ""))
                        )
                        newPost.postId = jsonResponse.optString("postId")
                        // Update the UI on the main thread.
                        viewModelScope.launch(Dispatchers.Main) {
                            val exists = posts.any { it.postId == newPost.postId }
                            if (!exists) {
                                posts.add(newPost)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            callback(success, if (success) null else "Failed to create post")
        }
    }

    // Update an existing post.
    // Only allow updates if the user is a PetOwner.
    fun updatePost(
        postId: String,
        name: String,
        city: String,
        phone: String,
        email: String,
        description: String,
        date: String,
        petId: String,
        callback: (Boolean, String?) -> Unit
    ) {
        if (isPetSitter) {
            callback(false, "Pet Sitters cannot update posts.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            var success = false
            try {
                val json = JSONObject().apply {
                    put("name", name)
                    put("city", city)
                    put("phone", phone)
                    put("email", email)
                    put("description", description)
                    put("date", date)
                    put("petId", petId)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/postings/$postId")
                    .patch(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    success = response.isSuccessful
                    if (success) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        val updatedPost = Post(
                            name = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("name")),
                            city = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("city")),
                            phone = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("phone")),
                            email = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("email")),
                            description = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("description")),
                            date = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("date", "")),
                            petId = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("petId", ""))
                        )
                        updatedPost.postId = jsonResponse.optString("postId")
                        viewModelScope.launch(Dispatchers.Main) {
                            val index = posts.indexOfFirst { it.postId == postId }
                            if (index != -1) {
                                posts[index] = updatedPost
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            callback(success, if (success) null else "Failed to update post")
        }
    }

    // Delete a post.
    // Only allow deletion if the user is a PetOwner.
    fun deletePost(postId: String, callback: (Boolean) -> Unit) {
        if (isPetSitter) {
            callback(false)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            var success = false
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/postings/$postId")
                    .delete()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    success = response.isSuccessful
                    if (success) {
                        viewModelScope.launch(Dispatchers.Main) {
                            posts.removeAll { it.postId == postId }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            callback(success)
        }
    }
}
