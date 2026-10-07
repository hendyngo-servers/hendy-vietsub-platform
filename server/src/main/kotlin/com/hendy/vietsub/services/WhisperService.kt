package com.hendy.vietsub.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.File

class WhisperService(private val openAiApiKey: String) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun transcribeAudio(audioFile: File): String {
        val response = client.submitFormWithBinaryData(
            url = "https://api.openai.com/v1/audio/transcriptions",
            formData = formData {
                append("model", "whisper-1")
                // Chọn response_format là srt để nhận về chuẩn Subtitle luôn
                append("response_format", "srt") 
                append("file", audioFile.readBytes(), Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"${audioFile.name}\"")
                    append(HttpHeaders.ContentType, "audio/mpeg") // Tùy định dạng file
                })
            }
        ) {
            bearerAuth(openAiApiKey)
        }

        if (response.status.isSuccess()) {
            return response.bodyAsText() // Trả về text dạng .srt
        } else {
            throw Exception("Whisper API Error: ${response.status} - ${response.bodyAsText()}")
        }
    }
}
