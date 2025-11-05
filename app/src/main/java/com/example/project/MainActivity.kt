package com.example.project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.project.navigation.AppNavHost
import com.example.project.ui.theme.ProjectTheme

class MainActivity : ComponentActivity() {

    // Đăng ký các quyền yêu cầu
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // Quyền thông báo đã cấp hay không
        }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // Quyền vị trí đã cấp hay không
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Kiểm tra và yêu cầu quyền truy cập thông báo (dành cho Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Kiểm tra và yêu cầu quyền truy cập vị trí
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Nếu chưa có quyền, yêu cầu cấp quyền
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {
            ProjectTheme {
                AppNavHost()
            }
        }
    }
}
