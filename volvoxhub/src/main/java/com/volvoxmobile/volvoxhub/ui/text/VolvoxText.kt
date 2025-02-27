package com.volvoxmobile.volvoxhub.ui.text

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit

@Composable
fun VolvoxText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    variables: Map<String, String> = emptyMap(),
    onLinkClick: ((String) -> Unit)? = null
) {
    val parser = BBCodeParser()
    val parsedText = parser.parse(text, variables)

    if (parsedText.getStringAnnotations("URL", 0, parsedText.length).isNotEmpty()) {
        val uriHandler = LocalUriHandler.current

        ClickableText(
            text = parsedText,
            modifier = modifier,
            style = style.copy(fontSize = fontSize, color = if (color != Color.Unspecified) color else style.color),
            onClick = { offset ->
                parsedText.getStringAnnotations("URL", offset, offset).firstOrNull()?.let { annotation ->
                    onLinkClick?.invoke(annotation.item) ?: uriHandler.openUri(annotation.item)
                }
            }
        )
    } else {
        Text(
            text = parsedText,
            modifier = modifier,
            style = style.copy(fontSize = fontSize, color = if (color != Color.Unspecified) color else style.color)
        )
    }
}
