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
     * Tải file video dạng Byte lên Ktor Server để trích xuất Whisper STT tự động
     */
    fun processVideoStt(fileBytes: ByteArray, fileName: String = "user_video.mp4") {
        if (fileBytes.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Không thể đọc dữ liệu file video!") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = "Đang tải video lên Server & bóc tách Whisper STT..."
                )
            }

            try {
                val srtContent = apiClient.uploadVideoAndGetSrt(fileBytes, fileName)
                val parsedSubtitles = SrtFormatter.parseSrt(srtContent)

                _uiState.update {
                    it.copy(
                        subtitles = parsedSubtitles,
                        isLoading = false,
                        successMessage = "Trích xuất phụ đề tự động thành công!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Lỗi trích xuất phụ đề từ máy chủ."
                    )
                }
            }
        }
    }

    /**
     * Dịch toàn bộ danh sách phụ đề bằng Gemini AI
     */
    fun translateAllSubtitles() {
        val currentSubtitles = _uiState.value.subtitles
        if (currentSubtitles.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val rawSrt = SrtFormatter.encodeToSrt(currentSubtitles)
                val translatedSrt = apiClient.translateSrtContent(rawSrt)
                val translatedItems = SrtFormatter.parseSrt(translatedSrt)

                _uiState.update {
                    it.copy(
                        subtitles = translatedItems,
                        isLoading = false,
                        successMessage = "Đã dịch thành công bằng Gemini AI!"
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
     * Xuất chuỗi .srt hoàn chỉnh
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
