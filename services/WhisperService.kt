package com.vietsub.server.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WhisperService {
    suspend fun extractSubtitles(videoFilePath: String): String {
        return withContext(Dispatchers.IO) {
            // TODO: Chạy lệnh FFmpeg tách audio (.wav) từ video
            // TODO: Gửi file audio qua OpenAI Whisper API hoặc dùng Whisper C++ local
            // Giả lập trả về nội dung file .srt
            """
            1
            00:00:01,000 --> 00:00:04,000
            Chào mừng bạn đến với hệ thống Vietsub!
            """.trimIndent()
        }
    }
}
