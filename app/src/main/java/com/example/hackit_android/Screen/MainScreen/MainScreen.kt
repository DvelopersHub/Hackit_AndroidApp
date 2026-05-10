package com.example.hackit_android.Screen.MainScreen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // 状態の定義
    var isExpanded by remember { mutableStateOf(false) }

    // アイコンの回転アニメーション用 (tragetValue -> targetValue に修正)
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    // 画面をスクロール可能にする
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Hackit",
            fontSize = 40.sp,
            color = Color(0xFFFF7043),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "お好きな方法でログインしてください",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // メインボタン
        //LoginButton("Appleで続ける")
        LoginButton("Googleで続ける")
        LoginButton("GitHubで続ける")
        LoginButton("メールで続ける")

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "------------ または --------------",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 展開ボタンのRow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "その他のサインイン方法")
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.rotate(rotationAngle)
            )
        }

        // アニメーション付きで展開
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SubLoginButton("LINEで続ける")
                SubLoginButton("Xで続ける")
                SubLoginButton("Slackで続ける")
                SubLoginButton("Facebookで続ける")
                SubLoginButton("Discordで続ける")
            }
        }
    }
}

// -----------------------------------------------------------------
// 以下のヘルパー関数（プレハブのようなもの）もファイル内に入れてください
// -----------------------------------------------------------------

@Composable
fun LoginButton(text: String) {
    Button(
        onClick = { Log.d("MY_APP_DEBUG", "$text が押されました") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043))
    ) {
        Text(text = text)
    }
}

@Composable
fun SubLoginButton(text: String) {
    OutlinedButton(
        onClick = { Log.d("MY_APP_DEBUG", "$text が押されました") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = text, color = Color.DarkGray)
    }
}