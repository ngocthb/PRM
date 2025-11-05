package com.example.project.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.R
import com.example.project.model.StoreLocationResponse
import com.example.project.ui.viewmodel.StoreLocationViewModel
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class ShopMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShopMapScreen(onBack = { finish() })
        }
    }
}
@Composable
fun ShopMapScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF5B5FEF)

    // Fake user location (có thể thay bằng GPS sau)
    val fakeUserLat = 10.77653
    val fakeUserLng = 106.700981
    val userLocation = remember { Point.fromLngLat(fakeUserLng, fakeUserLat) }

    var showRoute by remember { mutableStateOf(false) }
    var routeFeature by remember { mutableStateOf<Feature?>(null) }

    val shopMarkerIcon = rememberMarkerIcon()
    val userMarkerIcon = rememberIconImage(
        key = R.drawable.ic_location_blue,
        painter = painterResource(R.drawable.ic_location_blue)
    )

    // ViewModel gọi API
    val storeViewModel: StoreLocationViewModel = viewModel()
    val storeState by storeViewModel.uiState.collectAsState()

    // Gọi API load dữ liệu khi mở màn hình
    LaunchedEffect(Unit) {
        storeViewModel.loadStoreLocations()
    }

    // Vẽ route (demo: từ user đến cửa hàng đầu tiên)
    LaunchedEffect(showRoute, storeState.locations) {
        if (showRoute && storeState.locations.isNotEmpty()) {
            val shop = storeState.locations.first()
            fetchRouteFromMapbox(
                start = listOf(fakeUserLng, fakeUserLat),
                destination = listOf(shop.longitude, shop.latitude)
            )?.let { feature ->
                routeFeature = feature
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val firstShop = storeState.locations.firstOrNull()

        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(14.0)
                    center(
                        firstShop?.let {
                            Point.fromLngLat(it.longitude, it.latitude)
                        } ?: userLocation
                    )
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
            // Hiển thị marker của từng cửa hàng từ API
            val coroutineScope = rememberCoroutineScope()
            storeState.locations.forEach { shop ->
                val shopPoint = Point.fromLngLat(shop.longitude, shop.latitude)

                PointAnnotation(
                    point = shopPoint,
                    onClick = {
                        coroutineScope.launch {
                            val route = fetchRouteFromMapbox(
                                start = listOf(userLocation.longitude(), userLocation.latitude()),
                                destination = listOf(shop.longitude, shop.latitude)
                            )
                            routeFeature = route
                            showRoute = route != null
                        }
                        true
                    }
                ) {
                    iconImage = shopMarkerIcon
                }
            }

            // --- Marker người dùng ---
            PointAnnotation(point = userLocation) {
                iconImage = userMarkerIcon
                iconSize = 1.2 // to hơn shop
                textField = "Bạn ở đây"
                textSize = 12.0
                textOffset = listOf(0.0, -2.0)
            }

            if (showRoute) {
                // Đường đi
                routeFeature?.geometry()?.let { geometry ->
                    if (geometry is LineString) {
                        PolylineAnnotation(points = geometry.coordinates()) {
                            lineColor = Color(0xFF3B82F6)
                            lineWidth = 4.0
                            lineOpacity = 0.8
                        }
                    }
                }
            }
        }

        // Nút Back
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

        // xóa đường đi
        if (showRoute) {
            Button(
                onClick = {
                    showRoute = false
                    routeFeature = null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 38.dp, end = 16.dp)
            ) {
                Text("Xóa đường đi", fontSize = 13.sp, fontWeight = FontWeight.Medium)
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
