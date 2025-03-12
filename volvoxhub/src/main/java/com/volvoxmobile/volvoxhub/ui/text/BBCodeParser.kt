package com.volvoxmobile.volvoxhub.ui.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

class BBCodeParser {
    private sealed class Node {
        data class Text(val content: String) : Node()
        data class Bold(val children: List<Node>) : Node()
        data class Url(val url: String, val children: List<Node>) : Node()
        data class Color(val color: String, val children: List<Node>) : Node()
    }

    private sealed class Token {
        data class Text(val content: String) : Token()
        data class TagOpen(val name: String, val attributes: Map<String, String> = emptyMap()) : Token()
        data class TagClose(val name: String) : Token()
    }
    fun parse(input: String, variables: Map<String, String> = emptyMap()): AnnotatedString {
        var processedText = input
        variables.forEach { (key, value) ->
            processedText = processedText.replace("{$key}", value)
        }

        val tokens = tokenize(processedText)

        val rootNodes = parseTokens(tokens)

        return renderNodes(rootNodes)
    }

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var currentIndex = 0

        val tagPattern = Regex("\\[(/?)(b|url|color)(=([^\\]]+))?\\]")

        while (currentIndex < input.length) {
            val match = tagPattern.find(input, currentIndex)

            if (match != null && match.range.first == currentIndex) {
                val isClosing = match.groupValues[1] == "/"
                val tagName = match.groupValues[2]
                val hasAttribute = match.groupValues[3].isNotEmpty()
                val attributeValue = if (hasAttribute) match.groupValues[4] else ""

                if (isClosing) {
                    tokens.add(Token.TagClose(tagName))
                } else {
                    val attributes = if (hasAttribute) {
                        mapOf("value" to attributeValue)
                    } else {
                        emptyMap()
                    }
                    tokens.add(Token.TagOpen(tagName, attributes))
                }

                currentIndex = match.range.last + 1
            } else {
                val nextTagPosition = match?.range?.first ?: input.length
                if (nextTagPosition > currentIndex) {
                    tokens.add(Token.Text(input.substring(currentIndex, nextTagPosition)))
                    currentIndex = nextTagPosition
                }
            }
        }

        return tokens
    }
    private fun parseTokens(tokens: List<Token>): List<Node> {
        val result = mutableListOf<Node>()
        var index = 0

        fun parseTag(): Pair<Node, Int> {
            val openTag = tokens[index] as Token.TagOpen
            val tagName = openTag.name
            val children = mutableListOf<Node>()
            index++

            while (index < tokens.size) {
                val token = tokens[index]

                when (token) {
                    is Token.Text -> {
                        children.add(Node.Text(token.content))
                        index++
                    }
                    is Token.TagOpen -> {
                        val (childNode, newIndex) = parseTag()
                        children.add(childNode)
                        index = newIndex
                    }
                    is Token.TagClose -> {
                        if (token.name == tagName) {
                            index++
                            break
                        } else {
                            children.add(Node.Text("[/${token.name}]"))
                            index++
                        }
                    }
                }
            }

            return when (tagName) {
                "b" -> Node.Bold(children) to index
                "url" -> Node.Url(openTag.attributes["value"] ?: "", children) to index
                "color" -> Node.Color(openTag.attributes["value"] ?: "", children) to index
                else -> Node.Text("[$tagName]") to index
            }
        }

        while (index < tokens.size) {
            when (val token = tokens[index]) {
                is Token.Text -> {
                    result.add(Node.Text(token.content))
                    index++
                }
                is Token.TagOpen -> {
                    val (node, newIndex) = parseTag()
                    result.add(node)
                    index = newIndex
                }
                is Token.TagClose -> {
                    result.add(Node.Text("[/${token.name}]"))
                    index++
                }
            }
        }

        return result
    }

    private fun renderNodes(nodes: List<Node>): AnnotatedString {
        return buildAnnotatedString {
            renderNodesInternal(nodes)
        }
    }

    private fun AnnotatedString.Builder.renderNodesInternal(nodes: List<Node>) {
        for (node in nodes) {
            when (node) {
                is Node.Text -> append(node.content)
                is Node.Bold -> {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    renderNodesInternal(node.children)
                    pop()
                }
                is Node.Url -> {
                    pushStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline))
                    pushStringAnnotation("URL", node.url)
                    renderNodesInternal(node.children)
                    pop()
                    pop()
                }
                is Node.Color -> {
                    pushStyle(SpanStyle(color = parseColor(node.color)))
                    renderNodesInternal(node.children)
                    pop()
                }
            }
        }
    }

    private fun parseColor(colorValue: String): Color {
        if (colorValue.startsWith("#")) {
            return try {
                Color(android.graphics.Color.parseColor(colorValue))
            } catch (e: Exception) {
                Color.Black
            }
        }

        return when (colorValue.lowercase()) {
            "red" -> Color.Red
            "green" -> Color.Green
            "blue" -> Color.Blue
            "black" -> Color.Black
            "white" -> Color.White
            "yellow" -> Color.Yellow
            "cyan" -> Color.Cyan
            "magenta" -> Color.Magenta
            "gray", "grey" -> Color.Gray
            else -> Color.Black
        }
    }
}
