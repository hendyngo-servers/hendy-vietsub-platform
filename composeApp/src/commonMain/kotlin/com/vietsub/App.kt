package com.vietsub

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.vietsub.ui.AppNavigation

@Composable
fun App() {
    // Áp dụng Dark Theme mặc định cho ứng dụng xử lý Video
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        AppNavigation()
    }
}
