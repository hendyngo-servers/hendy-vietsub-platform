package com.vietsub.ui.screens.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vietsub.models.SubtitleItem
import com.vietsub.network.SubtitleApiClient
import com.vietsub.utils.SrtFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimelineEditorUiState(
    val subtitles: List<SubtitleItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val exportedSrtContent: String? = null
)

class TimelineEditorViewModel(
    private val apiClient: SubtitleApiClient = SubtitleApiClient()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineEditorUiState())
    val uiState: StateFlow<TimelineEditorUiState> = _uiState.asStateFlow()

    fun loadInitialSubtitles(initialItems: List<SubtitleItem>) {
        _uiState.update { it.copy(subtitles = initialItems) }
    }

    fun updateSubtitleText(id: Int, newText: String) {
        _uiState.update { state ->
            val updated = state.subtitles.map { if (it.id == id) it.copy(text = newText) else it }
            state.copy(subtitles = updated)
        }
    }

    fun addSubtitle() {
        _uiState.update { state ->
            val lastEnd = state.subtitles.lastOrNull()?.endMs ?: 0L
            val nextId = (state.subtitles.maxOfOrNull { it.id } ?: 0) + 1
            val newItem = SubtitleItem(
                id = nextId,
                startMs = lastEnd + 100,
                endMs = lastEnd + 3000,
                text = "Phụ đề mới..."
            )
            state.copy(subtitles = state.subtitles + newItem)
        }
    }

    fun deleteSubtitle(id: Int) {
        _uiState.update { state ->
            state.copy(subtitles = state.subtitles.filterNot { it.id == id })
        }
    }

    /**
     * Gọi Ktor API Client để dịch toàn bộ dòng phụ đề bằng Gemini AI
     */
    fun translateAllSubtitles() {
        val currentSubtitles = _uiState.value.subtitles
        if (currentSubtitles.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 1. Chuyển List<SubtitleItem> thành chuỗi SRT
                val rawSrt = SrtFormatter.encodeToSrt(currentSubtitles)
                
                // 2. Gửi request sang Ktor Server
                val translatedSrt = apiClient.translateSrtContent(rawSrt)
                
                // 3. Parse chuỗi kết quả nhận về thành List<SubtitleItem>
                val translatedItems = SrtFormatter.parseSrt(translatedSrt)

                _uiState.update {
                    it.copy(
                        subtitles = translatedItems,
                        isLoading = false,
                        successMessage = "Đã dịch thành công bằng AI!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Không thể kết nối đến máy chủ dịch thuật."
                    )
                }
            }
        }
    }

    /**
     * Xuất ra chuỗi file .srt hoàn chỉnh
     */
    fun exportSrt(): String {
        val srtContent = SrtFormatter.encodeToSrt(_uiState.value.subtitles)
        _uiState.update { 
            it.copy(
                exportedSrtContent = srtContent,
                successMessage = "Đã xuất file SRT thành công!" 
            ) 
        }
        return srtContent
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
