package com.volvoxmobile.volvoxhub.ui.ban

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.volvoxmobile.volvoxhub.R
import com.volvoxmobile.volvoxhub.ui.theme.HubTheme

@Composable
fun BannedPopup(
    isVisible: Boolean,
    config: BannedPopupConfig,
) {
    if (isVisible) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = config.imagePainter,
                        contentDescription = "Banned",
                        modifier =
                            Modifier
                                .size(100.dp)
                                .padding(bottom = 16.dp),
                    )
                    Text(
                        text = config.titleText,
                        style = config.titleTextStyle,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Text(
                        text = config.messageText,
                        style = config.messageTextStyle,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        onClick = { },
                    ) {
                        Text(text = config.buttonText, style = config.buttonTextStyle)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BannedPopupPreview() {
    HubTheme {
        BannedPopup(
            isVisible = true,
            config =
                BannedPopupConfig(
                    imagePainter = painterResource(id = R.drawable.ic_ban),
                    titleTextStyle = MaterialTheme.typography.bodyLarge,
                    messageTextStyle = MaterialTheme.typography.bodySmall,
                    buttonTextStyle = MaterialTheme.typography.labelLarge,
                ),
        )
    }
}
