package com.vietsub.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vietsub.data.SubtitleRepository
import com.vietsub.models.SubtitleItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PlayerViewModel(
    private val repository: SubtitleRepository
) : ViewModel() {

    val subtitles: StateFlow<List<SubtitleItem>> = repository.subtitles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videoUrl: StateFlow<String?> = repository.selectedVideoUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onVideoSelected(url: String) {
        repository.setVideoUrl(url)
    }
}
