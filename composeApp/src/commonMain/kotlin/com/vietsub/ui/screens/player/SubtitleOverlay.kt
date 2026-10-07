package com.vietsub.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vietsub.models.SubtitleItem
import com.vietsub.player.NativeVideoPlayer

@Composable
fun SubtitleOverlayScreen(
    videoUrl: String,
    subtitles: List<SubtitleItem>
) {
    var currentPlaybackMs by remember { mutableStateOf(0L) }

    // Tự động tìm câu phụ đề ứng với thời gian hiện tại của video
    val currentSubtitle = remember(currentPlaybackMs, subtitles) {
        subtitles.find { currentPlaybackMs in it.startMs..it.endMs }?.text ?: ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Trình phát video Native đa nền tảng
        NativeVideoPlayer(
            url = videoUrl,
            modifier = Modifier.fillMaxSize(),
            onTimeUpdate = { timeMs -> currentPlaybackMs = timeMs }
        )

        // Lớp phủ phụ đề (Subtitle Overlay UI)
        if (currentSubtitle.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = currentSubtitle,
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}
