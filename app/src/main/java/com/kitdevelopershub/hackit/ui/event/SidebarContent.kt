package com.kitdevelopershub.hackit.ui.event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kitdevelopershub.hackit.R
import com.kitdevelopershub.hackit.ui.theme.HackitColors
import com.kitdevelopershub.hackit.ui.theme.Spacing

/**
 * サイドバー Drawer の中身（iOS `SidebarView` 相当）。
 * 現状の項目はサインアウトのみ。
 */
@Composable
fun SidebarContent(onSignOut: () -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(HackitColors.WarmOffWhite)
            .statusBarsPadding(),
    ) {
        IconButton(onClick = onClose, modifier = Modifier.padding(Spacing.SM)) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.sidebar_close),
                tint = HackitColors.Charcoal,
            )
        }
        Text(
            stringResource(R.string.event_menu),
            style = MaterialTheme.typography.headlineSmall,
            color = HackitColors.Charcoal,
            modifier = Modifier.padding(horizontal = Spacing.LG, vertical = Spacing.MD),
        )
        HorizontalDivider(color = HackitColors.LightGray)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSignOut)
                .padding(horizontal = Spacing.LG, vertical = Spacing.MD),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = HackitColors.StatusError,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(Spacing.MD))
            Text(
                stringResource(R.string.sidebar_sign_out),
                style = MaterialTheme.typography.bodyLarge,
                color = HackitColors.StatusError,
            )
        }
    }
}
