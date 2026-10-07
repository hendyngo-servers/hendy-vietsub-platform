package com.vietsub.data

import com.vietsub.models.SubtitleItem
import com.vietsub.network.SubtitleApiClient
import com.vietsub.utils.SrtFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SubtitleRepository(
    private val apiClient: SubtitleApiClient = SubtitleApiClient()
) {
    // 1. State lưu danh sách phụ đề
    private val _subtitles = MutableStateFlow<List<SubtitleItem>>(emptyList())
    val subtitles: StateFlow<List<SubtitleItem>> = _subtitles.asStateFlow()

    // 2. State lưu URL/Đường dẫn Video đang chọn
    private val _selectedVideoUrl = MutableStateFlow<String?>(null)
    val selectedVideoUrl: StateFlow<String?> = _selectedVideoUrl.asStateFlow()

    fun setVideoUrl(url: String?) {
        _selectedVideoUrl.value = url
    }

    fun updateSubtitleText(id: Int, newText: String) {
        _subtitles.update { list ->
            list.map { if (it.id == id) it.copy(text = newText) else it }
        }
    }

    fun addSubtitle() {
        _subtitles.update { list ->
            val lastEnd = list.lastOrNull()?.endMs ?: 0L
            val nextId = (list.maxOfOrNull { it.id } ?: 0) + 1
            list + SubtitleItem(
                id = nextId,
                startMs = lastEnd + 100,
                endMs = lastEnd + 3000,
                text = "Phụ đề mới..."
            )
        }
    }

    fun deleteSubtitle(id: Int) {
        _subtitles.update { list -> list.filterNot { it.id == id } }
    }

    suspend fun translateAllSubtitles() {
        val rawSrt = SrtFormatter.encodeToSrt(_subtitles.value)
        val translatedSrt = apiClient.translateSrtContent(rawSrt)
        _subtitles.value = SrtFormatter.parseSrt(translatedSrt)
    }

    suspend fun processVideoStt(fileBytes: ByteArray, fileName: String) {
        val srtResponse = apiClient.uploadVideoAndGetSrt(fileBytes, fileName)
        _subtitles.value = SrtFormatter.parseSrt(srtResponse)
    }

    fun exportSrt(): String {
        return SrtFormatter.encodeToSrt(_subtitles.value)
    }
}
