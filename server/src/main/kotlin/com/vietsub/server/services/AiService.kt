package com.vietsub.server.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class GeminiRequest(val contents: List<Content>) {
    @Serializable
    data class Content(val parts: List<Part>)
    @Serializable
    data class Part(val text: String)
}

@Serializable
private data class GeminiResponse(val candidates: List<Candidate> = emptyList()) {
    @Serializable
    data class Candidate(val content: Content? = null)
    @Serializable
    data class Content(val parts: List<Part> = emptyList())
    @Serializable
    data class Part(val text: String = "")
}

class AiService(
    private val apiKey: String = System.getenv("GEMINI_API_KEY") ?: "YOUR_GEMINI_API_KEY"
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun translateText(text: String, targetLang: String = "Vietnamese"): String {
        val prompt = """
            You are a professional video subtitler and translator. 
            Translate the following subtitle text into $targetLang. 
            Maintain the line breaks, tone, and context. Do not add explanations or notes.
            
            Text to translate:
            $text
        """.trimIndent()

        val requestBody = GeminiRequest(
            contents = listOf(
                GeminiRequest.Content(
                    parts = listOf(GeminiRequest.Part(text = prompt))
                )
            )
        )

        val response: HttpResponse = client.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent") {
            parameter("key", apiKey)
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        if (response.status.isSuccess()) {
            val geminiResponse = Json.decodeFromString<GeminiResponse>(response.bodyAsText())
            return geminiResponse.candidates.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text?.trim()
                ?: text
        } else {
            throw Exception("Gemini API Error [${response.status}]: ${response.bodyAsText()}")
        }
    }
}
