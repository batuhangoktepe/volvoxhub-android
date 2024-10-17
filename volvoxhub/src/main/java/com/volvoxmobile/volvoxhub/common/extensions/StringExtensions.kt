package com.volvoxmobile.volvoxhub.common.extensions

import android.net.Uri
import android.text.Html
import android.text.Spanned
import android.webkit.MimeTypeMap

fun String.toHtml(): Spanned {
    return Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT);
}

fun String.removeWhiteSpaces(): String {
    return this.replace("\\s".toRegex(), "")
}

fun String?.asUri(): Uri? {
    try {
        return Uri.parse(this)
    } catch (_: Exception) {
    }
    return null
}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { f -> f.uppercase() } }

fun formatNumberWithDots(number: Int): String {
    return String.format("%,d", number).replace(',', '.')
}

fun String.mimeType(): String {
    var type = "image/jpeg"
    val extension = MimeTypeMap.getFileExtensionFromUrl(this);
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()
    }

    return type
}
