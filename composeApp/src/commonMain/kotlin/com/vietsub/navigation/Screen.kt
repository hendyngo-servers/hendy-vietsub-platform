package com.vietsub.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Player : Screen("player_screen", "Trình Phát", Icons.Default.PlayCircle)
    data object Editor : Screen("editor_screen", "Biên Tập Sub", Icons.Default.Edit)
}
