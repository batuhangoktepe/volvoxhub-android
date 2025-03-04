package com.volvoxmobile.volvoxhub.ui.contact_us

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volvoxmobile.volvoxhub.R
import com.volvoxmobile.volvoxhub.ui.theme.HubColors
import com.volvoxmobile.volvoxhub.ui.theme.HubTheme

@Composable
fun BaseTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onNavigateBackClick: () -> Unit,
    content: (@Composable () -> Unit)? = null,
    actionsButtons: (@Composable () -> Unit)? = null,
    isSpacerVisible: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HubTheme.colors.topBarBackground)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_left),
                    contentDescription = "Navigation Back",
                    tint = HubTheme.colors.topBarIconColor,
                    modifier = Modifier.clickable {
                        onNavigateBackClick()
                    }
                )
                content?.let {
                    content()
                }
                title?.let {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                actionsButtons?.let {
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
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewBaseTopBar(modifier: Modifier = Modifier) {
    HubTheme {
        BaseTopBar(modifier, onNavigateBackClick = {}, content = {
            Text(
                "Atom AI",
                color = HubTheme.colors.topBarTextColor,
                modifier = Modifier.padding(start = 6.dp)
            )
        }, actionsButtons = {
            Icon(
                imageVector =Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                imageVector =Icons.Default.ArrowDropDown,
                contentDescription = null,
            )
        },
            isSpacerVisible = true
        )
    }
}