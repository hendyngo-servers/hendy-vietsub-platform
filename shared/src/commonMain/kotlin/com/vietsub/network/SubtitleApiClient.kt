package com.vietsub.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class SubtitleApiClient(
    // 10.0.2.2 dùng cho Android Emulator truy cập Localhost.
    // Nếu chạy Desktop/iOS hoặc thiết bị thật, đổi thành IP thực tế của Server (vd: "http://192.168.1.10:8080")
    private val baseUrl: String = "http://10.0.2.2:8080" 
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                prettyPrint = true
            })
        }
    }

    /**
     * Gửi chuỗi nội dung SRT lên Ktor Backend để dịch thuật bằng Gemini AI
     */
    suspend fun translateSrtContent(srtContent: String): String {
        val response: HttpResponse = client.post("$baseUrl/api/v1/subtitle/translate") {
            contentType(ContentType.Text.Plain)
            setBody(srtContent)
        }

        if (response.status.isSuccess()) {
            return response.bodyAsText()
        } else {
            throw Exception("Lỗi gọi API Backend [${response.status.value}]: ${response.bodyAsText()}")
        }
    }
}
