package com.volvoxmobile.volvoxhub.ui.ban

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle

data class BannedPopupConfig(
    val titleText: String = "Account Banned",
    val messageText: String = "Your account has been banned due to violation of terms.",
    val buttonText: String = "Contact",
    val imagePainter: Painter,
    val titleTextStyle: TextStyle,
    val messageTextStyle: TextStyle,
    val buttonTextStyle: TextStyle
)

