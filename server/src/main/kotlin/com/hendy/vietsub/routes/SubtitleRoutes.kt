package com.hendy.vietsub.routes

import com.hendy.vietsub.services.WhisperService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.subtitleRoutes(whisperService: WhisperService) {
    post("/api/v1/subtitles/generate") {
        // Trong thực tế bạn sẽ nhận Multipart File từ Client.
        // Ở đây giả lập lấy file từ request body (để test)
        val audioBytes = call.receive<ByteArray>()
        
        // Lưu tạm file xuống server
        val tempFile = File.createTempFile("upload_", ".mp3")
        tempFile.writeBytes(audioBytes)

        try {
            // Gọi AI
            val srtContent = whisperService.transcribeAudio(tempFile)
            
            // Xóa file tạm
            tempFile.delete()
            
            // Trả file .srt về cho Client
            call.respondText(srtContent, ContentType.Text.Plain)
        } catch (e: Exception) {
            call.respondText(e.message ?: "Unknown Error", status = HttpStatusCode.InternalServerError)
        }
    }
}
