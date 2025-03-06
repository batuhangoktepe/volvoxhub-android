package com.volvoxmobile.volvoxhub.ui.contact_us.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.contact_us.detail.ContactMessageItem
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme

@Composable
fun ContactMessage(
    modifier: Modifier = Modifier,
    message: ContactMessageItem,
    fonts: HubFonts
) {
    Column(
        modifier = modifier
            .wrapContentWidth()
            .padding(10.dp)
            .background(
                VolvoxHubTheme.colors.contactMessageBackground,
                shape = RoundedCornerShape(
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp,
                )
            ),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message.message,
                fontFamily = fonts.messageText,
                fontSize = 14.sp,
                color = VolvoxHubTheme.colors.textColor,
            )
        }
        Text(
            text = message.time ?: "",
            fontFamily = fonts.messageDateText,
            color = VolvoxHubTheme.colors.contactMessageTime,
            fontSize = 8.sp,
            modifier = Modifier.align(Alignment.End)
        )
    }
}