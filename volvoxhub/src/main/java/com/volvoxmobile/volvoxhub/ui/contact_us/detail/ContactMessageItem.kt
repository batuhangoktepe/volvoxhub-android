package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import android.net.Uri
import androidx.compose.runtime.Stable

@Stable
data class ContactMessageItem(
    val author: Author,
    val message: String,
    val time: String? = null
)

@Stable
enum class Author() {
    GPT,
    USER
}