package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import android.net.Uri
import androidx.compose.runtime.Stable

@Stable
data class ContactMessageItem(
    val author: Author,
    val message: String,
    val mediaUri: Uri? = null,
    val imageUrl: String? = null,
    val time: String? = null,
)

@Stable
enum class Author(val title: String) {
    GPT("Atom AI"),
    USER("You")
}