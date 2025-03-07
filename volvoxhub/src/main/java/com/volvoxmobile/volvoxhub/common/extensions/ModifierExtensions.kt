package com.volvoxmobile.volvoxhub.common.extensions

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role

fun Modifier.safeClick(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    useIndication: Boolean = false,
    role: Role? = null,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = {
            ClickThrottler.throttleClick(
                onClick = {
                    onClick.invoke()
                },
                delayMillis = 900
            )
        },
        role = role,
        indication = if (useIndication) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() }
    )
}

@Composable
fun Modifier.clickableWithoutRipple(
    onClick: () -> Unit
): Modifier =
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )

object ClickThrottler {
    private var isThrottled = false
    private val handler = Handler(Looper.getMainLooper())

    fun throttleClick(
        onClick: () -> Unit,
        delayMillis: Long = 900
    ) {
        if (!isThrottled) {
            isThrottled = true
            onClick.invoke()
            handler.postDelayed({ isThrottled = false }, delayMillis)
        }
    }
}