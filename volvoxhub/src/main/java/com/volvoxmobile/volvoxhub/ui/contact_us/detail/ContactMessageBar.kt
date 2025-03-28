package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.purchases.ui.revenuecatui.composables.AutoResizedText
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.contact_us.HubResources
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme

@Composable
fun ContactMessageBar(
    modifier: Modifier = Modifier,
    onSendMessage: (ContactMessageItem) -> Unit,
    messageList: List<ContactMessageItem>,
    isTyping: Boolean,
    fonts: HubFonts,
    hubResources: HubResources,
    isDarkTheme:Boolean
) {
    var textFieldValue by rememberSaveable { mutableStateOf("") }
    var textHeight by remember { mutableIntStateOf(0) }
    var singleLineHeight by remember { mutableIntStateOf(0) }
    var isOverflowing by remember { mutableStateOf(false) }
    val disabledSendButtonRes = if (isDarkTheme) hubResources.disabledSendButton else hubResources.lightDisabledSendButton
    Column(
        Modifier
            .background(VolvoxHubTheme.colors.background)
    ) {
        if (isTyping) {
            Text(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp, start = 24.dp),
                text = "Ai is typing...",
                color = VolvoxHubTheme.colors.isTypingColor,
                fontFamily = fonts.regular,
                fontSize = 12.sp
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .size(1.dp)
                .background(VolvoxHubTheme.colors.messageBarBorder)
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .background(
                    Color.Transparent
                )
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = if (isOverflowing) Alignment.Bottom else Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                    },
                    shape = RoundedCornerShape(40.dp),
                    placeholder = {
                        AutoResizedText(
                            text = Localizations.get(
                                LocalContext.current,
                                "write_your_message_here"
                            ),
                            style = TextStyle(
                                fontFamily = fonts.regular,
                                fontSize = 14.sp
                            ),
                            color = VolvoxHubTheme.colors.textFieldPlaceholder,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = fonts.medium,
                        fontSize = 14.sp,
                        color = VolvoxHubTheme.colors.textColor,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent, RoundedCornerShape(18.dp))
                        .heightIn(min = 43.dp)
                        .onGloballyPositioned { coordinates ->
                            val size = coordinates.size
                            if (singleLineHeight == 0) {
                                singleLineHeight = coordinates.size.height
                            }
                            textHeight = size.height
                            isOverflowing = textHeight > singleLineHeight
                        },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = VolvoxHubTheme.colors.textFieldBackground,
                        focusedContainerColor = VolvoxHubTheme.colors.textFieldBackground,
                        unfocusedIndicatorColor = VolvoxHubTheme.colors.textFieldBackground,
                        focusedIndicatorColor = VolvoxHubTheme.colors.textFieldBackground,
                        cursorColor = VolvoxHubTheme.colors.textFieldBackground
                    ),
                    singleLine = false,
                    maxLines = 3
                )
                Image(
                    painter = painterResource(if (textFieldValue.isNotEmpty()) hubResources.sendButton else disabledSendButtonRes),
                    contentDescription = null,
                    Modifier
                        .padding(horizontal = 16.dp)
                        .clickableWithoutRipple {
                            if (textFieldValue.isNotEmpty()) {
                                onSendMessage(
                                    ContactMessageItem(
                                        Author.USER,
                                        textFieldValue.trim(),
                                    )
                                )
                                textFieldValue = ""
                            }
                        }
                )
            }
        }
    }
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