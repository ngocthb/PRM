package com.example.project.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.project.R
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.ThemeValue
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.remember

data class ShopLocation(
    val name: String,
    val address: String,
    val phone: String,
    val openHours: String,
    val latitude: Double,
    val longitude: Double
)

class ShopMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Ví dụ chỉ hiển thị 1 shop ở Hà Nội

            ShopMapScreen()
        }
    }
}

@Composable
fun ShopMapScreen() {
    val context = LocalContext.current
    val shop = ShopLocation(
        name = "Shop Hà Nội",
        address = "123 Hoàn Kiếm, Hà Nội",
        phone = "0123456789",
        openHours = "8:00 - 22:00",
        latitude = 21.0278,
        longitude = 105.8342
    )

    // --- tạo icon trước, trong Composable ---
    val markerIcon = rememberMarkerIcon()

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(14.0)
                center(Point.fromLngLat(shop.longitude, shop.latitude))
            }
        },
        style = {
            MapboxStandardStyle(
                standardStyleState = rememberStandardStyleState {
                    configurationsState.theme = ThemeValue.MONOCHROME
                }
            )
        }
    ) {
        PointAnnotation(point = Point.fromLngLat(shop.longitude, shop.latitude)) {
            iconImage = markerIcon
            textField = shop.name
            interactionsState.onClicked {
                Toast.makeText(
                    context,
                    "${shop.name}\n${shop.address}\n${shop.phone}",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }
}

@Composable
fun rememberMarkerIcon() = rememberIconImage(
    key = R.drawable.ic_location_red,
    painter = painterResource(R.drawable.ic_location_red)
)