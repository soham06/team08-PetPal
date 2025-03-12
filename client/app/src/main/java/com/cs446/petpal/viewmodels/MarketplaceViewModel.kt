package com.cs446.petpal.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cs446.petpal.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MarketplaceViewModel @Inject constructor() : ViewModel() {

    // Local list to store posts. Since we're using Compose, mutableStateListOf
    // ensures that any changes trigger recomposition where needed.
    val posts = mutableStateListOf<Post>()

    /**
     * Creates a new marketplace post and adds it to the posts list.
     *
     * @param name The name for the post.
     * @param city The city for the post.
     * @param phone The phone number for the post.
     * @param email The email for the post.
     * @param description The description of the post.
     * @param callback A callback that returns a success flag and an optional error message.
     */
    fun createPost(
        name: String,
        city: String,
        phone: String,
        email: String,
        description: String,
        callback: (Boolean, String?) -> Unit
    ) {
        try {
            // Create a new Post instance using mutableStateOf for each field.
            val newPost = Post(
                name = mutableStateOf(name),
                city = mutableStateOf(city),
                phone = mutableStateOf(phone),
                email = mutableStateOf(email),
                description = mutableStateOf(description)
            )
            // Add the new post to the posts list.
            posts.add(newPost)
            // Callback with success.
            callback(true, null)
        } catch (e: Exception) {
            // If any exception occurs, callback with failure.
            callback(false, e.message)
        }
    }
}
