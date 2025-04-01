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

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val lastName: String
        get() = userRepository.currentUser.value?.lastName?.value ?: ""
    val firstName: String
        get() = userRepository.currentUser.value?.firstName?.value ?: ""
    val email: String
        get() = userRepository.currentUser.value?.email?.value ?: ""

    private val client = OkHttpClient()

    val posts = mutableStateListOf<Post>()
    val pets = mutableStateListOf<Pet>()

    var currentUserId: String = userRepository.currentUser.value?.userId.toString()
    val isPetSitter: Boolean = userRepository.currentUser.value?.userType?.value == "Pet Sitter"

    private val deletedPostsMementos = mutableListOf<Post.Memento>()
    private var curIndex = -1

    fun getPostsForUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = if (isPetSitter) {
                    "http://10.0.2.2:3000/api/postings/all"
                } else {
                    "http://10.0.2.2:3000/api/postings/$currentUserId"
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
                            post.postId = postJson.optString("postId")
                            postsList.add(post)
                        }
                        posts.clear()
                        posts.addAll(postsList)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPetsForUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = if (isPetSitter) {
                    "http://10.0.2.2:3000/api/pets/all"
                } else {
                    "http://10.0.2.2:3000/api/pets/$currentUserId"
                }
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
                                sharedUsers = null
                            )
                            petList.add(pet)
                        }
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
        if (isPetSitter) {
            callback(false, "Pet Sitters cannot create posts.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            var success = false
            var postId: String? = null
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
                        postId = jsonResponse.optString("postId")
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
            callback(success, if (success) postId else "Failed to create post")
        }
    }

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

    fun deletePost(postId: String, isRedoing: Boolean = false, callback: (Boolean) -> Unit) {
        if (isPetSitter) {
            callback(false)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            var success = false
            try {
                if (!isRedoing) {
                    saveDeletedPost(postId)
                }

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

    fun undoDeletePost(): Boolean {
        if (curIndex < 0) {
            println("cannot undo")
            return false
        }

        val lastDeletedPost = deletedPostsMementos[curIndex]
        curIndex--
        createPost(
            lastDeletedPost.name,
            lastDeletedPost.city,
            lastDeletedPost.phone,
            lastDeletedPost.email,
            lastDeletedPost.description,
            lastDeletedPost.date,
            lastDeletedPost.petId,
        ) { success, errorMsg ->
            if(success) {
                var oldMemento = deletedPostsMementos[curIndex + 1]
                var newMemento = oldMemento.copy(postId = errorMsg)
                deletedPostsMementos[curIndex + 1] = newMemento
            }
        }
        return true
    }

    fun saveDeletedPost(postId: String) {
        val postToDelete = posts.find { it.postId == postId }
        postToDelete?.let {
            while (curIndex < deletedPostsMementos.size - 1) {
                deletedPostsMementos.removeAt(deletedPostsMementos.lastIndex)
            }
            deletedPostsMementos.add(it.save())
            curIndex = deletedPostsMementos.size - 1
        }
    }

    fun redoDeletePost(): Boolean {
        if (curIndex >= deletedPostsMementos.size - 1) {
            println("Cannot redo")
            return false
        }

        curIndex++
        val postToDelete = deletedPostsMementos[curIndex]
        deletePost(postToDelete.postId!!, true) {}
        return true
    }
}
