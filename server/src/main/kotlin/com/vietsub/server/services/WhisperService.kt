package com.vietsub.server.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import java.io.File

class WhisperService(
    private val apiKey: String = System.getenv("OPENAI_API_KEY") ?: "YOUR_OPENAI_API_KEY"
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun extractSubtitles(audioFile: File): String {
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "https://api.openai.com/v1/audio/transcriptions",
            formData = formData {
                append("model", "whisper-1")
                append("response_format", "srt") // Yêu cầu OpenAI trả trực tiếp chuỗi .srt
                append("file", audioFile.readBytes(), Headers.build {
                    append(HttpHeaders.ContentType, "audio/mpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"${audioFile.name}\"")
                })
            }
        ) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }

        if (response.status.isSuccess()) {
            return response.bodyAsText()
        } else {
            throw Exception("Whisper API Error [${response.status}]: ${response.bodyAsText()}")
        }
    }
}
