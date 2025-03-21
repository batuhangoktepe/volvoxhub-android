package com.volvoxmobile.volvoxhub.ui.text

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
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

    val uriHandler = LocalUriHandler.current

    var textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }

    val annotatedString = remember(parsedText) {
        buildAnnotatedString {

            append(parsedText.text)
            parsedText.spanStyles.forEach { spanStyle ->
                addStyle(spanStyle.item, spanStyle.start, spanStyle.end)
            }
            parsedText.paragraphStyles.forEach { paragraphStyle ->
                addStyle(paragraphStyle.item, paragraphStyle.start, paragraphStyle.end)
            }

            parsedText.getStringAnnotations("URL", 0, parsedText.length).forEach { annotation ->

                addStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline
                    ),
                    start = annotation.start,
                    end = annotation.end
                )

                addStringAnnotation(
                    tag = "URL",
                    annotation = annotation.item,
                    start = annotation.start,
                    end = annotation.end
                )
            }
        }
    }

    val hasLinks = annotatedString.getStringAnnotations("URL", 0, annotatedString.length).isNotEmpty()
    val clickableModifier = if (hasLinks) {
        Modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val layoutResult = textLayoutResultState.value ?: return@detectTapGestures
                val position = layoutResult.getOffsetForPosition(offset)

                annotatedString.getStringAnnotations("URL", position, position)
                    .firstOrNull()?.let { annotation ->
                        onLinkClick?.invoke(annotation.item) ?: uriHandler.openUri(annotation.item)
                    }
            }
        }
    } else {
        Modifier
    }

    Text(
        text = annotatedString,
        modifier = modifier.then(clickableModifier),
        style = style.copy(
            fontSize = fontSize,
            color = if (color != Color.Unspecified) color else style.color
        ),
        onTextLayout = { textLayoutResultState.value = it }
    )
}
