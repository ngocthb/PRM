package com.example.project.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.R
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.ThemeValue
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

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
            ShopMapScreen(
                onBack = { finish() } // finish Activity khi bấm back
            )
        }
    }
}

@Composable
fun ShopMapScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF5B5FEF)
    val shop = ShopLocation(
        name = "Shop Hà Nội",
        address = "123 Hoàn Kiếm, Hà Nội",
        phone = "0123456789",
        openHours = "8:00 - 22:00",
        latitude = 21.0278,
        longitude = 105.8342
    )

    // Fake user location gần Hà Nội
    val fakeUserLat = 21.0300
    val fakeUserLng = 105.8350
    val userLocation = remember { Point.fromLngLat(fakeUserLng, fakeUserLat) }

    var showRoute by remember { mutableStateOf(false) }
    var routeFeature by remember { mutableStateOf<Feature?>(null) }

    val shopMarkerIcon = rememberMarkerIcon()
    val userMarkerIcon = rememberIconImage(
        key = R.drawable.ic_location_blue,
        painter = painterResource(R.drawable.ic_location_blue)
    )

    // Lấy route từ Mapbox Directions API khi nhấn button
    LaunchedEffect(showRoute) {
        if (showRoute) {
            fetchRouteFromMapbox(
                start = listOf(fakeUserLng, fakeUserLat),
                destination = listOf(shop.longitude, shop.latitude)
            )?.let { feature ->
                routeFeature = feature
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(16.0)
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
            // Shop marker
            PointAnnotation(point = Point.fromLngLat(shop.longitude, shop.latitude)) {
                iconImage = shopMarkerIcon
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

            if (showRoute) {
                // User marker
                PointAnnotation(point = userLocation) {
                    iconImage = userMarkerIcon
                    textField = "Bạn ở đây"
                }

                // Route polyline
                routeFeature?.geometry()?.let { geometry ->
                    if (geometry is LineString) {
                        PolylineAnnotation(
                            points = geometry.coordinates()
                        ) {
                            lineColor = Color(0xFF3B82F6)  // màu xanh
                            lineWidth = 4.0
                            lineOpacity = 0.8
                        }
                    }
                }
            }
        }

        // Nút Back (trên trái)
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .padding(start = 16.dp, top = 34.dp)
                .size(48.dp)
                .background(Color(0xFF6588E6), CircleShape)
        ) {
         Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Button hiển thị vị trí người dùng + đường đi (dưới)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .align(Alignment.BottomCenter)
        ) {
            androidx.compose.material.Button(
                onClick = { showRoute = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), // chiều cao button giữ nguyên
                colors = ButtonDefaults.buttonColors(backgroundColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                androidx.compose.material.Text(
                    "Hiển thị đường đi",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

    }
}

@Composable
fun rememberMarkerIcon() = rememberIconImage(
    key = R.drawable.ic_location_red,
    painter = painterResource(R.drawable.ic_location_red)
)

// --- Fetch route từ Mapbox Directions API ---
suspend fun fetchRouteFromMapbox(
    start: List<Double>,
    destination: List<Double>,
    accessToken: String = "pk.eyJ1IjoibmdvY3RoYiIsImEiOiJjbWc5MjUweXUwOHFyMmlzOWd4Z3NiMGE2In0.L-dHQQXwD2hNzeNfXxddAw"
): Feature? = withContext(Dispatchers.IO) {
    try {
        val url =
            "https://api.mapbox.com/directions/v5/mapbox/driving/${start[0]},${start[1]};${destination[0]},${destination[1]}?geometries=geojson&access_token=$accessToken"
        val response = URL(url).readText()
        val json = JSONObject(response)
        val routes = json.getJSONArray("routes")
        if (routes.length() > 0) {
            val route = routes.getJSONObject(0)
            val geometry = route.getJSONObject("geometry")
            val coords = geometry.getJSONArray("coordinates")
            val linePoints = mutableListOf<Point>()
            for (i in 0 until coords.length()) {
                val point = coords.getJSONArray(i)
                linePoints.add(Point.fromLngLat(point.getDouble(0), point.getDouble(1)))
            }
            return@withContext Feature.fromGeometry(LineString.fromLngLats(linePoints))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    null
}
