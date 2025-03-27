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
    // Using mutableStateListOf ensures recomposition when posts change.
    val posts = mutableStateListOf<Post>()
    // Get current user ID from the repository.
    var currentUserId: String = userRepository.currentUser.value?.userId.toString()
    // Determine if the current user is a PetSitter.
    val isPetSitter: Boolean = userRepository.currentUser.value?.userType?.value == "Pet Sitter"

    init {
        // Fetch posts when the ViewModel is created.
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
                                date = androidx.compose.runtime.mutableStateOf(postJson.optString("date", ""))
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

    // Create a new post.
    // Only allow creation if the user is a PetOwner.
    fun createPost(
        name: String,
        city: String,
        phone: String,
        email: String,
        description: String,
        date: String,
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
                            date = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("date", ""))
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
                            date = androidx.compose.runtime.mutableStateOf(jsonResponse.optString("date", ""))

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
