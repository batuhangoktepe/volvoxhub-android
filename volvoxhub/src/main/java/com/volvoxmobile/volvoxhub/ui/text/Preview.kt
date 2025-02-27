package com.volvoxmobile.volvoxhub.ui.text

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.volvoxmobile.volvoxhub.ui.theme.HubTheme


@Composable
fun BBCodeTestScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Sample variable replacements
    val variables = mapOf(
        "username" to "JohnDoe123",
        "coins" to "500",
        "currency" to "BTC",
        "reward" to "250",
        "level" to "42",
        "rank" to "Diamond"
    )

    // Test cases with increasing complexity
    val testCases = listOf(
        TestCase(
            title = "Basic Formatting",
            bbcode = "Welcome to [color=red][b]VX Hub[/b][/color]!",
            description = "Simple bold text"
        ),
        TestCase(
            title = "URL Test",
            bbcode = "Visit our [url=https://example.com][color=red]website[/color][/url] for more information.",
            description = "Clickable link"
        ),
        TestCase(
            title = "Color Test",
            bbcode = "This text has [color=red]colored[/color] parts in it.",
            description = "Red colored text"
        ),
        TestCase(
            title = "Variable Replacement",
            bbcode = "Hello {username}, you have {coins} {currency} in your wallet.",
            description = "Variables replaced with values from the map"
        ),
        TestCase(
            title = "Nested Tags",
            bbcode = "Welcome [b]valued [color=gold]VIP[/color] member[/b]!",
            description = "Bold text with nested color tag"
        ),
        TestCase(
            title = "Complex Formatting",
            bbcode = "[b]Congratulations[/b]! You've reached [color=purple]Level {level}[/color] and earned [b][color=#FF5733]{reward} bonus[/color][/b] points!",
            description = "Multiple tags and variables combined"
        ),
        TestCase(
            title = "Multiple Links",
            bbcode = "Check our [url=https://example.com/profile]profile page[/url] or [url=https://example.com/help]help center[/url] for assistance.",
            description = "Multiple clickable links"
        ),
        TestCase(
            title = "Edge Case - Incomplete Tags",
            bbcode = "This has [b]incomplete tags and [color=blue]nested incomplete tags.",
            description = "Testing parser resilience with malformed BBCode"
        ),
        TestCase(
            title = "Edge Case - Overlapping Tags",
            bbcode = "This [b]has [color=green]overlapping[/b] tags[/color] test.",
            description = "Testing parser handling of improperly nested tags"
        ),
        TestCase(
            title = "Full Example",
            bbcode = "Welcome back, [b]{username}[/b]!\n\nYou are currently [color=#8A2BE2]Rank {rank}[/color] with [b][color=green]{coins} {currency}[/color][/b] in your wallet.\n\nCheck your [url=https://example.com/rewards]daily rewards[/url] to claim your [b][color=#FF5733]{reward} bonus points[/color][/b]!",
            description = "Comprehensive example with multiple formatting features"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "BBCode Formatting Test Cases",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        testCases.forEach { testCase ->
            TestCaseCard(
                testCase = testCase,
                variables = variables,
                onLinkClick = { url ->
                    Toast.makeText(context, "Link clicked: $url", Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class TestCase(
    val title: String,
    val bbcode: String,
    val description: String
)

@Composable
fun TestCaseCard(
    testCase: TestCase,
    variables: Map<String, String>,
    onLinkClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = testCase.title,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = testCase.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .border(1.dp, Color(0xFFE0E0E0))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Source:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(text = testCase.bbcode)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFFFFF))
                    .border(1.dp, Color(0xFFE0E0E0))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Rendered:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                VolvoxText(
                    text = testCase.bbcode,
                    variables = variables,
                    onLinkClick = onLinkClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BBCodeTestPreview() {
    HubTheme {
        BBCodeTestScreen()
    }
}