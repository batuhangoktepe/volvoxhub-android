package com.volvoxmobile.volvoxhub.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volvoxmobile.volvoxhub.R
import com.volvoxmobile.volvoxhub.common.extensions.safeClick
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.ui.theme.regularFont

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(vertical = 13.dp)
            .safeClick {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.padding(end = 12.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_google),
            contentDescription = "Google Icon"
        )
        Text(
            text = Localizations.getHub(context, "log_in_with_google"),
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Preview
@Composable
fun PreviewGoogleSignInButton(modifier: Modifier = Modifier) {
    GoogleSignInButton(modifier = Modifier) {}
}