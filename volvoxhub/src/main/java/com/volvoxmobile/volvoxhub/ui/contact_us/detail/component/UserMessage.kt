package com.volvoxmobile.volvoxhub.ui.contact_us.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.formatTimestampToAmPm
import com.volvoxmobile.volvoxhub.ui.contact_us.detail.ContactMessageItem
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme

@Composable
fun UserMessage(
    modifier: Modifier = Modifier,
    message: ContactMessageItem,
    fonts: HubFonts
) {
    Row(
        modifier = modifier
            .widthIn(max = 300.dp, min = 100.dp)
            .heightIn(min = 45.dp)
            .background(
                VolvoxHubTheme.colors.userMessageBackground,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp
                )
            )
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = message.message,
                color = VolvoxHubTheme.colors.userMessageText,
                modifier = Modifier.padding(4.dp),
                fontFamily = fonts.messageText,
                fontSize = 14.sp
            )
        }
        message.time?.let {
            Text(
                text = formatTimestampToAmPm(message.time),
                fontFamily = fonts.messageDateText,
                fontSize = 8.sp,
                color = VolvoxHubTheme.colors.userMessageTime,
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(start = 4.dp)
            )
        }
    }
}