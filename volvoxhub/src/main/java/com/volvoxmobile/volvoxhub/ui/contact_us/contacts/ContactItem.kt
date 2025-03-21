package com.volvoxmobile.volvoxhub.ui.contact_us.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volvoxmobile.volvoxhub.common.extensions.safeClick
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme

@Composable
fun ContactItem(
    modifier: Modifier = Modifier,
    contactTitle: String,
    contactDescription: String,
    contactDate: String,
    isSeen: Boolean,
    titleFamily: FontFamily,
    descriptionFamily: FontFamily,
    dateFamily: FontFamily,
    onContactClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp,horizontal = 24.dp)
            .wrapContentHeight()
            .background(color = VolvoxHubTheme.colors.background)
            .safeClick {
                onContactClick()
            },
        verticalAlignment = Alignment.Top
    ) {
        if (isSeen.not()){
            Box(
                modifier = Modifier
                    .padding(end = 8.dp, top = 10.dp)
                    .size(6.dp)
                    .background(
                        color =  Color.Magenta,
                        shape = CircleShape
                    )
            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxHeight()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = Localizations.get(context, TICKETCATEGORIES.valueOf(contactTitle).title),
                    fontSize = 14.sp,
                    fontFamily = titleFamily,
                    color = VolvoxHubTheme.colors.textColor
                )
                Text(
                    text = contactDate,
                    fontSize = 10.sp,
                    fontFamily = dateFamily,
                    color = VolvoxHubTheme.colors.contactDateColor
                )
            }
            Text(
                text = contactDescription,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = descriptionFamily,
                color = VolvoxHubTheme.colors.contactDescriptionColor
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewContactItem(modifier: Modifier = Modifier) {
    ContactItem(
        modifier = modifier,
        contactTitle = "Chat Introduction and Greetings",
        contactDescription = "Lorem ipsum dolor sit amet consectetur...",
        contactDate = "09.24 AM",
        titleFamily = FontFamily.Serif,
        descriptionFamily = FontFamily.SansSerif,
        dateFamily = FontFamily.Serif,
        isSeen = true
    ) {}
}