package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volvoxmobile.volvoxhub.R
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.contact_us.detail.component.ContactMessage
import com.volvoxmobile.volvoxhub.ui.contact_us.detail.component.UserMessage
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme

@Composable
fun ContactMessages(
    modifier: Modifier = Modifier,
    messageList: List<ContactMessageItem>,
    fonts: HubFonts
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    if (messageList.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(VolvoxHubTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.padding(top = 10.dp),
                painter = painterResource(R.drawable.ic_contact_bot),
                contentDescription = "Bot"
            )
            Text(
                modifier = Modifier.offset(y = (-24).dp),
                text = Localizations.get(context,"how_can_ı_help_you"),
                fontSize = 16.sp,
                fontFamily = fonts.semiBold,
                color = VolvoxHubTheme.colors.contactDetailHelpTextColor
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(VolvoxHubTheme.colors.background)
                .verticalScroll(scrollState)
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp),
        ) {
            messageList.forEach {
                when (it.author) {
                    Author.GPT -> ContactMessage(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 24.dp),
                        message = it,
                        fonts = fonts
                    )

                    Author.USER -> UserMessage(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 24.dp),
                        message = it,
                        fonts = fonts
                    )
                }
            }
        }
    }
}