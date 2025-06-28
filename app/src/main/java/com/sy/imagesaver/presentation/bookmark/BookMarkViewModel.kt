package com.sy.imagesaver.presentation.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.imagesaver.data.mapper.MediaEntityMapper
import com.sy.imagesaver.domain.usecase.GetBookmarkedMediaUseCase
import com.sy.imagesaver.presentation.model.MediaUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookMarkViewModel @Inject constructor(
    private val getBookmarkedMediaUseCase: GetBookmarkedMediaUseCase,
    private val mediaEntityMapper: MediaEntityMapper
) : ViewModel() {
    
    private val _bookmarkedMedia = MutableStateFlow<List<MediaUiModel>>(emptyList())
    val bookmarkedMedia: StateFlow<List<MediaUiModel>> = _bookmarkedMedia.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadBookmarkedMedia()
    }
    
    fun loadBookmarkedMedia() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                getBookmarkedMediaUseCase.getAllBookmarkedMedia()
                    .map { mediaEntities ->
                        mediaEntityMapper.toMediaUiModelList(mediaEntities)
                    }
                    .collect { mediaUiModels ->
                        _bookmarkedMedia.value = mediaUiModels
                        _isLoading.value = false
                    }
                    
            } catch (e: Exception) {
                _error.value = "북마크된 미디어를 불러오는데 실패했습니다: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun loadBookmarkedMediaByType(type: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                getBookmarkedMediaUseCase.getBookmarkedMediaByType(type)
                    .map { mediaEntities ->
                        mediaEntityMapper.toMediaUiModelList(mediaEntities)
                    }
                    .collect { mediaUiModels ->
                        _bookmarkedMedia.value = mediaUiModels
                        _isLoading.value = false
                    }
                    
            } catch (e: Exception) {
                _error.value = "북마크된 미디어를 불러오는데 실패했습니다: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun refreshBookmarkedMedia() {
        loadBookmarkedMedia()
    }
    
    fun clearError() {
        _error.value = null
    }
}

