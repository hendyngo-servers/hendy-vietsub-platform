package com.vietsub.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.play
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL
import platform.UIKit.UIView

@Composable
actual fun NativeVideoPlayer(
    url: String,
    modifier: Modifier,
    onTimeUpdate: (currentMs: Long) -> Unit
) {
    val player = remember(url) {
        val nsUrl = NSURL.URLWithString(url) ?: return@remember AVPlayer()
        AVPlayer(playerItem = AVPlayerItem(uRL = nsUrl)).apply {
            play()
        }
    }

    // Cập nhật vị trí thời gian real-time trên iOS
    DisposableEffect(player) {
        val interval = CMTimeMakeWithSeconds(0.1, 1000) // 100ms
        val observer = player.addPeriodicTimeObserverForInterval(interval, queue = null) { time ->
            val seconds = platform.CoreMedia.CMTimeGetSeconds(time)
            if (!seconds.isNaN()) {
                onTimeUpdate((seconds * 1000).toLong())
            }
        }

        onDispose {
            player.removeTimeObserver(observer)
        }
    }

    UIKitView(
        factory = {
            val controller = AVPlayerViewController().apply {
                this.player = player
                showsPlaybackControls = true
            }
            controller.view
        },
        modifier = modifier
    )
}
