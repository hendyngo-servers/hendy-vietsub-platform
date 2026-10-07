package com.vietsub.server.routes

import com.vietsub.server.services.WhisperService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.videoRouting(whisperService: WhisperService) {
    post("/api/v1/video/upload-and-stt") {
        val multipart = call.receiveMultipart()
        var tempFile: File? = null

        try {
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val fileBytes = part.streamProvider().readBytes()
                    val fileName = part.originalFileName ?: "uploaded_video.mp4"
                    
                    // Tạo file tạm trên server để xử lý
                    tempFile = File.createTempFile("stt_", "_$fileName")
                    tempFile?.writeBytes(fileBytes)
                }
                part.dispose()
            }

            if (tempFile != null && tempFile!!.exists()) {
                // Gọi Whisper Service bóc tách phụ đề thành chuỗi .srt
                val srtContent = whisperService.extractSubtitles(tempFile!!)
                call.respondText(srtContent, status = HttpStatusCode.OK)
            } else {
                call.respondText("Không nhận được dữ liệu file video", status = HttpStatusCode.BadRequest)
            }
        } catch (e: Exception) {
            call.respondText("Lỗi xử lý Whisper STT: ${e.message}", status = HttpStatusCode.InternalServerError)
        } finally {
            // Xóa file tạm sau khi đã xử lý xong
            tempFile?.delete()
        }
    }
}
