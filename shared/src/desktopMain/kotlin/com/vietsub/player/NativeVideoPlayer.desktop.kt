package com.vietsub.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import kotlinx.coroutines.delay
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent

@Composable
actual fun NativeVideoPlayer(
    url: String,
    modifier: Modifier,
    onTimeUpdate: (currentMs: Long) -> Unit
) {
    val mediaPlayerComponent = remember { EmbeddedMediaPlayerComponent() }

    LaunchedEffect(url) {
        mediaPlayerComponent.mediaPlayer().media().start(url)
    }

    // Polling thời gian video cho Desktop
    LaunchedEffect(mediaPlayerComponent) {
        while (true) {
            val mediaPlayer = mediaPlayerComponent.mediaPlayer()
            if (mediaPlayer.status().isPlaying) {
                onTimeUpdate(mediaPlayer.status().time())
            }
            delay(100)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerComponent.mediaPlayer().controls().stop()
            mediaPlayerComponent.release()
        }
    }

    SwingPanel(
        factory = { mediaPlayerComponent },
        modifier = modifier
    )
}
