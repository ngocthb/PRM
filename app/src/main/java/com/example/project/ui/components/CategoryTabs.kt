import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*

data class CategoryItem(
    val id: Int,
    val name: String,
    val icon: ImageVector
)

@Composable
fun CategoryTabs(
    selectedCategoryId: Int?,
    onCategoryClick: (Int) -> Unit = {}
) {
    val categories = listOf(
        CategoryItem(1, "Clothes", Icons.Outlined.Checkroom),
        CategoryItem(2, "Electronics", Icons.Outlined.PhoneIphone),
        CategoryItem(3, "Shoes", Icons.Outlined.DirectionsRun),
        CategoryItem(4, "Watch", Icons.Outlined.Watch),
        CategoryItem(5, "Bag", Icons.Outlined.ShoppingBag),
        CategoryItem(6, "Jewelry", Icons.Outlined.Diamond),
        CategoryItem(7, "Cosmetics", Icons.Outlined.Face),
        CategoryItem(8, "Sports", Icons.Outlined.SportsSoccer)
    )

    var showAll by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Category", style = MaterialTheme.typography.titleMedium)
            Text(
                if (showAll) "Hide" else "See All",
                color = Color(0xFF6588E6),
                modifier = Modifier.clickable { showAll = !showAll }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (showAll) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(categories.size) { index ->
                    val category = categories[index]
                    CategoryItemView(
                        category = category,
                        isSelected = category.id == selectedCategoryId,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                categories.take(4).forEach { category ->
                    CategoryItemView(
                        category = category,
                        isSelected = category.id == selectedCategoryId,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItemView(
    category: CategoryItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF0F0F0)
                )
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) Color(0xFF6588E6) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = if (isSelected) Color(0xFF6588E6) else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            category.name,
            fontSize = 14.sp,
            color = if (isSelected) Color(0xFF6588E6) else Color.Gray
        )
    }
}
