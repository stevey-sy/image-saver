package com.sy.imagesaver.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sy.imagesaver.data.mapper.MediaUiModelMapper
import com.sy.imagesaver.domain.usecase.SearchMediaUseCase
import com.sy.imagesaver.domain.usecase.SaveMediaUseCase
import com.sy.imagesaver.presentation.model.MediaUiModel
import com.sy.imagesaver.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMediaUseCase: SearchMediaUseCase,
    private val saveMediaUseCase: SaveMediaUseCase,
    private val mediaRepository: MediaRepository,
    private val mediaUiModelMapper: MediaUiModelMapper
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()
    
    // 북마크된 아이템의 thumbnailUrl을 추적
    private val _bookmarkedThumbnailUrls = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedThumbnailUrls: StateFlow<Set<String>> = _bookmarkedThumbnailUrls.asStateFlow()
    
    init {
        // 앱 시작 시 기존 북마크된 아이템들 로드
        loadBookmarkedItems()
    }
    
    private fun loadBookmarkedItems() {
        viewModelScope.launch {
            try {
                val bookmarkedUrls = mediaRepository.getBookmarkedThumbnailUrls()
                _bookmarkedThumbnailUrls.value = bookmarkedUrls.toSet()
            } catch (e: Exception) {
                // 에러 처리 (선택사항)
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearchQuery() {
        _searchQuery.value = ""
        _error.value = null
    }
    
    fun searchMedia(query: String) {
        // PagingData는 Composable에서 Flow로 직접 구독하므로 별도 구현 필요 없음
        _error.value = null
    }
    
    fun getSearchResultFlow(query: String): Flow<PagingData<MediaUiModel>> {
        return if (query.isNotBlank()) {
            searchMediaUseCase.searchMediaPaged(query).cachedIn(viewModelScope)
        } else {
            kotlinx.coroutines.flow.flowOf(PagingData.empty())
        }
    }
    
    fun refreshSearch() {
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            searchMedia(query)
        }
    }
    
    fun clearSearchCache() {
        viewModelScope.launch {
            mediaRepository.clearSearchCache()
        }
    }
    
    fun getCacheInfo() {
        viewModelScope.launch {
            val cacheInfo = mediaRepository.getCacheInfo()
            // 디버그용 로그 출력
            println("Cache Info: $cacheInfo")
        }
    }
    
    fun saveMedia(mediaUiModel: MediaUiModel) {
        viewModelScope.launch {
            try {
                _saveStatus.value = SaveStatus.Saving
                
                // MediaUiModel을 Media로 변환
                val media = mediaUiModelMapper.toMedia(mediaUiModel)
                
                val id = saveMediaUseCase(media)
                _saveStatus.value = SaveStatus.Success("미디어가 저장되었습니다. (ID: $id)")
                
                // 저장 성공 시 해당 아이템을 북마크 목록에 추가
                _bookmarkedThumbnailUrls.value = _bookmarkedThumbnailUrls.value + mediaUiModel.thumbnailUrl
                
                // 3초 후 상태 초기화
                kotlinx.coroutines.delay(3000)
                _saveStatus.value = SaveStatus.Idle
                
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error("저장에 실패했습니다: ${e.message}")
                
                // 3초 후 상태 초기화
                kotlinx.coroutines.delay(3000)
                _saveStatus.value = SaveStatus.Idle
            }
        }
    }
    
    sealed class SaveStatus {
        object Idle : SaveStatus()
        object Saving : SaveStatus()
        data class Success(val message: String) : SaveStatus()
        data class Error(val message: String) : SaveStatus()
    }
}