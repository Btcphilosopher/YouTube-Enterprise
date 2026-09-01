package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.EnterpriseVideoRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val repo: EnterpriseVideoRepository = EnterpriseVideoRepository.getInstance()
) : ViewModel() {

    val currentUser = repo.currentUser
    val currentRole = repo.currentRole
    val channels = repo.channels
    val videos = repo.videos
    val shorts = repo.shorts
    val liveStreams = repo.liveStreams
    val liveChatMessages = repo.liveChatMessages
    val comments = repo.comments
    val playlists = repo.playlists
    val subscriptions = repo.subscriptions
    val watchHistory = repo.watchHistory
    val watchLaterIds = repo.watchLaterIds
    val likedVideoIds = repo.likedVideoIds
    val activeUploadJob = repo.activeUploadJob
    val moderationCases = repo.moderationCases
    val rightsAssets = repo.rightsAssets
    val adCampaigns = repo.adCampaigns
    val systemHealth = repo.systemHealth
    val auditLogs = repo.auditLogs

    // Active Navigation / Selection State
    private val _selectedVideo = MutableStateFlow<Video?>(null)
    val selectedVideo: StateFlow<Video?> = _selectedVideo.asStateFlow()

    private val _selectedChannelId = MutableStateFlow<String?>(null)
    val selectedChannelId: StateFlow<String?> = _selectedChannelId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _showUploadModal = MutableStateFlow(false)
    val showUploadModal: StateFlow<Boolean> = _showUploadModal.asStateFlow()

    private val _selectedStudioTimeRange = MutableStateFlow("28 DAYS")
    val selectedStudioTimeRange: StateFlow<String> = _selectedStudioTimeRange.asStateFlow()

    // Navigation handlers
    fun selectVideo(video: Video) {
        _selectedVideo.value = video
        repo.recordWatchProgress(video.id, 0, video.durationSeconds)
    }

    fun clearSelectedVideo() {
        _selectedVideo.value = null
    }

    fun selectChannel(channelId: String) {
        _selectedChannelId.value = channelId
    }

    fun clearSelectedChannel() {
        _selectedChannelId.value = null
    }

    fun openSearch(query: String = "") {
        _searchQuery.value = query
        _isSearching.value = true
    }

    fun closeSearch() {
        _isSearching.value = false
        _searchQuery.value = ""
    }

    fun setRole(role: UserRole) {
        repo.setRole(role)
    }

    fun setStudioTimeRange(range: String) {
        _selectedStudioTimeRange.value = range
    }

    fun openUploadModal() {
        _showUploadModal.value = true
    }

    fun closeUploadModal() {
        _showUploadModal.value = false
    }

    // Repository Actions
    fun toggleLike(videoId: String) = repo.toggleLikeVideo(videoId)
    fun toggleWatchLater(videoId: String) = repo.toggleWatchLater(videoId)
    fun toggleSubscribe(channelId: String) = repo.toggleSubscribe(channelId)
    fun addComment(videoId: String, text: String) = repo.addComment(videoId, text)
    fun likeComment(videoId: String, commentId: String) = repo.toggleLikeComment(videoId, commentId)
    fun sendLiveChatMessage(streamId: String, text: String, superChatAmount: String?) = repo.sendLiveChatMessage(streamId, text, superChatAmount)
    fun deleteLiveChatMessage(streamId: String, msgId: String) = repo.deleteLiveChatMessage(streamId, msgId)
    fun clearHistory() = repo.clearWatchHistory()
    fun createPlaylist(title: String, desc: String) = repo.createPlaylist(title, desc)
    fun startUploadPipeline(fileName: String) = repo.startUploadPipeline(fileName)

    fun publishUploadedVideo(
        title: String,
        description: String,
        category: String,
        visibility: VideoVisibility,
        tags: List<String>
    ) {
        val newVideo = repo.publishUploadedVideo(title, description, category, visibility, tags)
        _showUploadModal.value = false
        selectVideo(newVideo)
    }

    fun resolveModeration(caseId: String, status: ModerationStatus, note: String) {
        repo.resolveModerationCase(caseId, status, note)
    }

    fun disputeRights(assetId: String, reason: String) {
        repo.disputeRightsClaim(assetId, reason)
    }

    fun getCreatorAnalytics() = repo.getCreatorAnalytics(_selectedStudioTimeRange.value)
    fun getAudienceRetentionPoints() = repo.getAudienceRetentionPoints()
}
