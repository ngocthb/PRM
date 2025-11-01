import android.util.Log
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
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.DryCleaning
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.FaceRetouchingNatural
import androidx.compose.material.icons.outlined.ShoppingBag
import com.example.project.model.CategoryResponse

data class CategoryItem(
    val id: Int,
    val name: String,
    val icon: ImageVector
)

@Composable
fun CategoryTabs(
    categories: List<CategoryResponse>,
    selectedCategoryId: Int?,
    onCategoryClick: (Int) -> Unit
) {


    val iconForCategory: (Int) -> ImageVector = { id ->
        when (id) {
            1 -> Icons.Outlined.Checkroom    // Áo
            2 -> Icons.Outlined.DryCleaning  // Quần
            3 -> Icons.Outlined.DirectionsRun // Giày
            4 -> Icons.Outlined.FaceRetouchingNatural // Váy
            else -> Icons.Outlined.Category
        }
    }

    var showAll by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Category", style = MaterialTheme.typography.titleMedium , color = Color.Black)
            Text(
                if (showAll) "Hide" else "See All",
                color = Color(0xFF6588E6),
                modifier = Modifier.clickable { showAll = !showAll }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val visibleCategories = if (showAll) categories else categories.take(4)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            visibleCategories.forEach { category ->
                CategoryItemView(
                    category = CategoryItem(
                        id = category.categoryId,
                        name = category.categoryName,
                        icon = iconForCategory(category.categoryId)
                    ),
                    isSelected = category.categoryId == selectedCategoryId,
                    onClick = { onCategoryClick(category.categoryId) }
                )
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
