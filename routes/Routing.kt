package com.vietsub.server.routes

import com.vietsub.server.services.AiService
import com.vietsub.server.services.FFmpegService
import com.vietsub.server.services.WhisperService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText

fun Application.configureRouting() {
    
    // Khởi tạo các services (có thể dùng Koin/Dagger để Dependency Injection sau này)
    val whisperService = WhisperService()
    val aiService = AiService()
    val ffmpegService = FFmpegService()

    routing {
        
        // 1. API: Upload Video và trích xuất phụ đề tự động (Pha 1)
        post("/api/v1/video/upload-and-stt") {
            val multipart = call.receiveMultipart()
            // TODO: Lưu file multipart vào thư mục tạm trên server
            val tempVideoPath = "/tmp/uploaded_video.mp4" 
            
            try {
                // Xử lý STT
                val srtContent = whisperService.extractSubtitles(tempVideoPath)
                call.respondText(srtContent, status = HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("Lỗi trích xuất: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }

        // 2. API: Dịch thuật toàn bộ file .srt (Pha 2)
        post("/api/v1/subtitle/translate") {
            val originalText = call.receiveText()
            val translatedText = aiService.translateText(originalText, "vi")
            call.respondText(translatedText, status = HttpStatusCode.OK)
        }

        // 3. API: Render Hardsub xuất ra file video mới (Pha 3)
        post("/api/v1/video/hardsub") {
            // Lấy parameters từ request (vd: videoId, srtData)
            val success = ffmpegService.renderHardsub("video.mp4", "sub.srt", "output.mp4")
            if (success) {
                call.respondText("Render thành công", status = HttpStatusCode.OK)
            } else {
                call.respondText("Render thất bại", status = HttpStatusCode.InternalServerError)
            }
        }

        // ==========================================
        // 4. WebSocket: Dịch thuật Live Stream (Pha 3)
        // Client gửi text (tiếng Anh) qua WebSocket -> Server dịch -> Trả về (tiếng Việt) ngay lập tức
        // ==========================================
        webSocket("/ws/v1/live-translate") {
            println("Client đã kết nối WebSocket!")
            
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val receivedText = frame.readText()
                    println("Nhận được từ Client: $receivedText")
                    
                    // Xử lý dịch thuật qua AI
                    val translated = aiService.translateText(receivedText)
                    
                    // Gửi kết quả ngược lại cho Client
                    send(Frame.Text(translated))
                }
            }
            
            println("Client đã ngắt kết nối WebSocket.")
        }
    }
}
