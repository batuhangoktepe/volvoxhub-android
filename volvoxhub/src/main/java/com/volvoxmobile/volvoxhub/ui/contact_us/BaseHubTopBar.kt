package com.volvoxmobile.volvoxhub.ui.contact_us

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volvoxmobile.volvoxhub.R
import com.volvoxmobile.volvoxhub.common.extensions.safeClick
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme

@Composable
fun BaseHubTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleFontFamily: FontFamily,
    isTitleCentered: Boolean = false,
    onNavigateBackClick: () -> Unit,
    content: (@Composable () -> Unit)? = null,
    actionsButtons: (@Composable () -> Unit)? = null,
    isSpacerVisible: Boolean = true
) {
    val spacerPadding = if (isSpacerVisible) 8.dp else 16.dp
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VolvoxHubTheme.colors.topBarBackground)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = spacerPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_left),
                    contentDescription = "Navigation Back",
                    tint = VolvoxHubTheme.colors.topBarIconColor,
                    modifier = Modifier.safeClick {
                        onNavigateBackClick()
                    }
                )
                content?.let {
                    content()
                }
                title?.let {
                    val titleModifier = if (isTitleCentered) {
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp)
                    } else {
                        Modifier.padding(start = 10.dp)
                    }
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontFamily = titleFontFamily,
                        modifier = titleModifier,
                        textAlign = if (isTitleCentered) TextAlign.Center else TextAlign.Start,
                        color = VolvoxHubTheme.colors.topBarTextColor
                    )
                }
            }
            actionsButtons?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actionsButtons()
                }
            }
        }
        if (isSpacerVisible) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .size(1.dp)
                    .background(VolvoxHubTheme.colors.topBarSpacer)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewBaseTopBar(modifier: Modifier = Modifier) {
    VolvoxHubTheme {
        BaseHubTopBar(modifier, onNavigateBackClick = {}, content = {
            Text(
                "Atom AI",
                color = VolvoxHubTheme.colors.topBarTextColor,
                modifier = Modifier.padding(start = 6.dp)
            )
        }, actionsButtons = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
            )
        },
            isSpacerVisible = true,
            titleFontFamily = FontFamily.Monospace
        )
    }
}