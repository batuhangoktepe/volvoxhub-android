package com.volvoxmobile.volvoxhub.ui.contact_us

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme
import com.volvoxmobile.volvoxhub.ui.theme.mediumFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomSheet(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    containerColor: Color = VolvoxHubTheme.colors.background,
    dragHandle: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
    onDismissRequest: (() -> Unit)? = null
) {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest?.invoke()
        },
        sheetState = sheetState,
        modifier = modifier,
        containerColor = containerColor,
        dragHandle = { dragHandle?.invoke() ?: DefaultDragHandle() }
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            title?.let {
                Text(
                    text = it,
                    color = VolvoxHubTheme.colors.textColor,
                    fontFamily = mediumFont,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            description?.let {
                Text(it, color = Color.White)
            }
            content()
        }
    }
}

@Composable
fun DefaultDragHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = 22.dp)
            .width(32.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.Gray)
    )
}