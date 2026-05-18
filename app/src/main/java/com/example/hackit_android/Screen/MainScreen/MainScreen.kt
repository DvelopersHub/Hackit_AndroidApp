package com.example.hackit_android.Screen.MainScreen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    navController: androidx.navigation.NavController,
    modifier: Modifier = Modifier
) {
    // 状態の定義（最初は閉じておく）
    var isExpanded by remember { mutableStateOf(false) }


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

        // ロゴテキスト
        Text(
            text = "Hackit",
            fontSize = 40.sp,
            color = Color(0xFFFF7043),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // サブタイトル
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

        // 区切り線テキスト
        Text(
            text = "------------ または --------------",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 展開ボタン
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "その他のサインイン方法",
                fontSize = 16.sp,
                color = Color.Black
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Black
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SubLoginButton("LINEで続ける")
                SubLoginButton("Xで続ける")
                SubLoginButton("Slackで続ける")
                SubLoginButton("Facebookで続ける")
                SubLoginButton("Discordで続ける")

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = {
                Log.d("MY_APP_DEBUG", "HomeScreenへ移動する")
                navController.navigate("home")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "テストボタン(Homeへ)")
        }
    }
}

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