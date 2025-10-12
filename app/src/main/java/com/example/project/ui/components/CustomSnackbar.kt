package com.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

enum class SnackbarType {
    SUCCESS,
    ERROR,
    INFO
}

@Composable
fun CustomSnackbarHost(
    hostState: SnackbarHostState
) {
    // Fullscreen container so we can position the host as an overlay at the top
    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = hostState,
            modifier = Modifier
                .align(Alignment.TopCenter)            // ép nằm trên cùng
                .statusBarsPadding()                   // tôn trọng status bar; đổi hoặc xoá nếu muốn sát mép trên
                .padding(top = 8.dp)                   // khoảng cách nhỏ từ status bar
                .fillMaxWidth()
                .zIndex(1f),                           // vẽ trên mọi nội dung khác
            snackbar = { data: SnackbarData ->
                // auto-dismiss sau 2s
                LaunchedEffect(data) {
                    delay(2000L)
                    data.dismiss()
                }

                val (bgColor, textColor) = when (data.visuals.actionLabel) {
                    SnackbarType.SUCCESS.name -> Pair(Color(0xFF4CAF50), Color.White) // Xanh
                    SnackbarType.ERROR.name -> Pair(Color(0xFFF44336), Color.White)   // Đỏ
                    SnackbarType.INFO.name -> Pair(Color(0xFFFFC107), Color.Black)    // Vàng
                    else -> Pair(SnackbarDefaults.color, Color.White)
                }

                Snackbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    containerColor = bgColor,
                    contentColor = textColor,
                    shape = SnackbarDefaults.shape
                ) {
                    Text(
                        text = data.visuals.message,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        )
    }
}