package com.vietsub.server.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiService {
    suspend fun translateText(text: String, targetLang: String = "vi"): String {
        return withContext(Dispatchers.IO) {
            // TODO: Gọi Google Gemini API hoặc OpenAI GPT-4 API để dịch
            // Giả lập kết quả trả về
            "[Đã dịch sang $targetLang]: $text"
        }
    }
}
