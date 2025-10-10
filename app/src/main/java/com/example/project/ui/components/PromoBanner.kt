package com.example.project.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.R
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay


@Composable
fun PromoBanner() {
    val banners = listOf(
        BannerData(
            R.drawable.ic_app_sale,
            "Big Sale",
            "Get trendy fashion with discounts up to 50%"
        ),
        BannerData(
            R.drawable.ic_app_sale,
            "New Arrivals",
            "Check out the latest products this season"
        ),
        BannerData(
            R.drawable.ic_app_sale,
            "Special Offer",
            "Buy 1 get 1 free on selected items"
        )
    )

    val pagerState = rememberPagerState()

    // Auto-scroll every 3s
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column {
        HorizontalPager(
            count = banners.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) { page ->
            val banner = banners[page]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF25345D))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = banner.imageRes),
                        contentDescription = "Promo",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            banner.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            banner.description,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Indicator dots
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
            activeColor = Color(0xFF6588E6),
            inactiveColor = Color.Gray
        )
    }
}

data class BannerData(
    val imageRes: Int,
    val title: String,
    val description: String
)
