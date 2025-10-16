package com.example.project.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.project.R
import me.leolin.shortcutbadger.ShortcutBadger

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
    fun showCartBadgeNotification(context: Context, count: Int) {
        val channelId = "cart_badge_channel"
        val notificationId = 1001

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo channel cho Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Cart Badge",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Cart badge notifications" }
            manager.createNotificationChannel(channel)
        }

        // Notification để hiển thị badge
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Cart items")
            .setContentText("You have $count items in your cart")
            .setSmallIcon(R.drawable.ic_cart)
            .setNumber(count) // số lượng hiển thị trên badge
            .setAutoCancel(true)
            .build()

        manager.notify(notificationId, notification)

        // ShortcutBadger: hiển thị badge trên launcher (nếu launcher hỗ trợ)
        ShortcutBadger.applyCount(context, count)
    }
}
