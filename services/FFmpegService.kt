package com.vietsub.server.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FFmpegService {
    suspend fun renderHardsub(videoPath: String, srtPath: String, outputPath: String): Boolean {
        return withContext(Dispatchers.IO) {
            // TODO: Thực thi lệnh FFmpeg: 
            // ffmpeg -i videoPath -vf subtitles=srtPath outputPath
            println("Bắt đầu render hardsub cho $videoPath bằng $srtPath...")
            true // Trả về true nếu thành công
        }
    }
}
