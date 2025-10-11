package com.example.project.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.project.R

object NotificationUtils {

    private const val CHANNEL_ID = "cart_channel"
    private const val CHANNEL_NAME = "Cart Notifications"
    private const val CHANNEL_DESC = "Notifications for cart updates"
    private const val NOTIFICATION_ID = 1001

    /**
     * Hiển thị notification giỏ hàng với badge
     * @param context context của Activity hoặc Application
     * @param cartCount số sản phẩm trong giỏ
     */
    fun showCartBadgeNotification(context: Context, cartCount: Int) {
        // 1️⃣ Kiểm tra quyền POST_NOTIFICATIONS trên Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Chưa có quyền, không hiển thị notification
                return
            }
        }

        // 2️⃣ Tạo Notification Channel cho Android 8+ (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                setShowBadge(true) // hiển thị badge trên app icon
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // 3️⃣ Tạo Notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cart) // icon giỏ hàng
            .setContentTitle("Giỏ hàng của bạn")
            .setContentText("Bạn có $cartCount sản phẩm trong giỏ")
            .setNumber(cartCount) // số hiển thị trên badge
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)

        // 4️⃣ Hiển thị Notification
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }
}
