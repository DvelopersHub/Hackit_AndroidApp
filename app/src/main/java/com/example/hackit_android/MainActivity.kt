package com.example.hackit_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hackit_android.ui.theme.Hackit_AndroidTheme
import com.example.hackit_android.Screen.MainScreen.MainScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hackit_AndroidTheme {
                // Modifier.fillMaxSize() で画面いっぱいに広げる
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // MainScreenを呼び出す。
                    // padding(innerPadding) を指定することで、ステータスバーなどの被りを防ぎます。
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

//@Composable
//fun MainScreen(modifier: Modifier = Modifier) {
//    // 状態（クリックされたかどうか）を保持する変数
//    var message by remember { mutableStateOf("ボタンを押してね") }
//
//    // Column は要素を垂直（縦）に並べます
//    Column(
//        modifier = modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center, // 上下中央
//        horizontalAlignment = Alignment.CenterHorizontally // 左右中央
//    ) {
//        Text(text = message, modifier = Modifier.padding(bottom = 16.dp))
//
//        // これがボタンのUIです
//        Button(onClick = {
//            // クリックされた時の処理
//            message = "ハック完了！"
//        }) {
//            Text(text = "ハックする")
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Hackit_AndroidTheme {
        MainScreen()
    }
}