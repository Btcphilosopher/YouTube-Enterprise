package com.example.model

import java.util.UUID

enum class UserRole {
    VIEWER,
    CREATOR,
    ENTERPRISE_ADMIN,
    MODERATOR,
    ADVERTISER
}

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val handle: String,
    val email: String,
    val avatarUrl: String,
    val role: UserRole = UserRole.VIEWER,
    val orgId: String? = "org-yt-enterprise",
    val orgName: String? = "Alphabet Enterprise Media",
    val isVerified: Boolean = true
)

data class Organisation(
    val id: String,
    val name: String,
    val domain: String,
    val tier: String = "Enterprise Core Tier 4",
    val ssoEnabled: Boolean = true,
    val storageQuotaGb: Double = 50000.0,
    val storageUsedGb: Double = 18420.5,
    val bandwidthQuotaTb: Double = 500.0,
    val bandwidthUsedTb: Double = 142.3,
    val memberCount: Int = 850
)

data class Channel(
    val id: String = UUID.randomUUID().toString(),
    val ownerUserId: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val bannerUrl: String,
    val description: String,
    val subscriberCount: Long,
    val isVerified: Boolean = true,
    val category: String = "Engineering & Technology",
    val links: List<String> = listOf("https://enterprise.internal/docs", "https://github.com/enterprise-video"),
    val featuredVideoId: String? = null,
    val bannerColorStart: Long = 0xFF1E1B4B,
    val bannerColorEnd: Long = 0xFF312E81
)

enum class VideoVisibility {
    PUBLIC,
    UNLISTED,
    PRIVATE,
    ORGANISATION_ONLY
}

enum class ContentIdStatus {
    CLEARED,
    MONETIZED_BY_OWNER,
    COPYRIGHT_CLAIM_ACTIVE,
    POLICY_REVIEW_REQUIRED
}

enum class ModerationStatus {
    CLEAR,
    LIMITED,
    UNDER_REVIEW,
    REMOVED,
    APPEALED
}

data class VideoChapter(
    val timestampSec: Int,
    val title: String,
    val description: String = ""
)

data class CaptionCue(
    val startSec: Int,
    val endSec: Int,
    val text: String
)

data class CaptionTrack(
    val languageCode: String,
    val languageName: String,
    val cues: List<CaptionCue>
)

data class VideoRendition(
    val resolutionLabel: String, // "144p", "360p", "720p", "1080p 60fps", "4K HDR", "8K"
    val width: Int,
    val height: Int,
    val bitrateKbps: Int,
    val codec: String, // "AV1", "VP9", "HEVC", "H.264"
    val format: String, // "DASH", "HLS"
    val streamUrl: String
)

data class Video(
    val id: String = UUID.randomUUID().toString(),
    val channelId: String,
    val channelName: String,
    val channelAvatar: String,
    val channelVerified: Boolean = true,
    val title: String,
    val description: String,
    val thumbnailGradientStart: Long = 0xFF1F2937,
    val thumbnailGradientEnd: Long = 0xFF111827,
    val thumbnailCategoryIcon: String = "code",
    val thumbnailBadge: String = "4K HDR",
    val durationSeconds: Int,
    val viewCount: Long,
    val likeCount: Long,
    val dislikeCount: Long = 42,
    val uploadTimestamp: Long = System.currentTimeMillis() - 86400000L * 2,
    val category: String,
    val language: String = "English",
    val visibility: VideoVisibility = VideoVisibility.PUBLIC,
    val license: String = "Standard Enterprise Media License (SEM-2026)",
    val tags: List<String> = listOf("Architecture", "Cloud", "Distributed Systems"),
    val chapters: List<VideoChapter> = emptyList(),
    val captions: List<CaptionTrack> = emptyList(),
    val renditions: List<VideoRendition> = emptyList(),
    val contentIdStatus: ContentIdStatus = ContentIdStatus.CLEARED,
    val moderationStatus: ModerationStatus = ModerationStatus.CLEAR,
    val isLive: Boolean = false,
    val liveViewerCount: Int = 0,
    val isShort: Boolean = false,
    val isLikedByCurrentUser: Boolean = false,
    val isSavedToWatchLater: Boolean = false
)

data class ShortVideoItem(
    val id: String = UUID.randomUUID().toString(),
    val video: Video,
    val audioTrackTitle: String,
    val audioAuthor: String,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean = false
)

enum class StreamHealthState {
    EXCELLENT,
    GOOD,
    DEGRADED,
    CRITICAL
}

data class LiveStreamDetails(
    val streamId: String = UUID.randomUUID().toString(),
    val videoId: String,
    val streamKey: String = "live_ent_stream_8917491_secure",
    val ingestEndpoint: String = "rtmp://ingest.lon1.ytenterprise.net/live",
    val health: StreamHealthState = StreamHealthState.EXCELLENT,
    val currentBitrateKbps: Int = 8500,
    val targetBitrateKbps: Int = 8500,
    val fps: Int = 60,
    val droppedFramesRate: Double = 0.01,
    val latencyMs: Int = 1250,
    val cpuUsagePercent: Int = 34,
    val isSlowModeActive: Boolean = false,
    val slowModeSeconds: Int = 5,
    val isSubscriberOnly: Boolean = false
)

data class LiveChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val streamId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val text: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isSuperChat: Boolean = false,
    val superChatAmount: String? = null,
    val superChatTierColor: Long? = null,
    val isModerator: Boolean = false,
    val isVerified: Boolean = false,
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false
)

data class CommentReply(
    val id: String = UUID.randomUUID().toString(),
    val commentId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val text: String,
    val timestampAgo: String,
    val likeCount: Int,
    val isCreator: Boolean = false
)

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val videoId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val text: String,
    val timestampAgo: String,
    val likeCount: Int,
    val isCreatorHearted: Boolean = false,
    val isPinned: Boolean = false,
    val replies: List<CommentReply> = emptyList(),
    val isLiked: Boolean = false
)

data class Playlist(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val visibility: VideoVisibility = VideoVisibility.PUBLIC,
    val ownerId: String,
    val ownerName: String,
    val videoIds: List<String>,
    val updatedAt: Long = System.currentTimeMillis()
)

data class WatchHistoryRecord(
    val videoId: String,
    val watchedTimestamp: Long,
    val progressSeconds: Int,
    val totalDurationSeconds: Int
)

enum class NotificationLevel {
    ALL,
    PERSONALISED,
    NONE
}

data class ChannelSubscription(
    val channelId: String,
    val notificationLevel: NotificationLevel = NotificationLevel.ALL,
    val subscribedTimestamp: Long = System.currentTimeMillis()
)

enum class AdFormat {
    PRE_ROLL,
    MID_ROLL,
    POST_ROLL,
    BANNER_OVERLAY,
    SPONSORED_CARD
}

data class AdCampaign(
    val id: String = UUID.randomUUID().toString(),
    val advertiserName: String,
    val campaignName: String,
    val format: AdFormat = AdFormat.PRE_ROLL,
    val title: String,
    val ctaText: String,
    val targetUrl: String,
    val durationSeconds: Int = 15,
    val bidCpmUsd: Double,
    val dailyBudgetUsd: Double,
    val totalSpendUsd: Double,
    val impressionsServed: Long,
    val clicksReceived: Long,
    val targetCategories: List<String> = listOf("Technology", "Cloud", "Business")
)

data class CreatorAnalytics(
    val views28d: Long = 482000,
    val viewsChangePercent: Double = 18.4,
    val watchTimeHours28d: Double = 62400.0,
    val watchTimeChangePercent: Double = 22.1,
    val subscribers28d: Long = 14200,
    val subscribersChangePercent: Double = 9.8,
    val revenueUsd28d: Double = 18640.50,
    val revenueChangePercent: Double = 14.2,
    val impressions28d: Long = 3450000,
    val ctrPercent28d: Double = 7.4,
    val averageViewDurationSec: Int = 465,
    val grossRevenue: Double = 24850.00,
    val platformDeductions: Double = 6209.50,
    val membershipRevenue: Double = 4120.00,
    val adRevenue: Double = 13200.50,
    val tipsSuperChatRevenue: Double = 1320.00
)

data class AudienceRetentionPoint(
    val percentageOffset: Int, // 0, 10, 20, ..., 100
    val retentionPercent: Float, // 100.0% down to e.g. 45%
    val note: String? = null
)

data class ModerationCase(
    val id: String = UUID.randomUUID().toString(),
    val targetContentId: String,
    val contentTitle: String,
    val channelName: String,
    val flaggedReason: String,
    val riskClassification: String, // "High Risk", "Moderate", "Safe"
    val riskScore: Float, // 0.0 to 1.0
    val status: ModerationStatus = ModerationStatus.UNDER_REVIEW,
    val autoMlConfidence: Float = 0.94f,
    val auditTrail: List<String> = listOf("Automated ML Flagged", "Queued for Tier-2 Human Review"),
    val createdAt: Long = System.currentTimeMillis() - 7200000L
)

enum class RightsClaimAction {
    MONETIZE_TO_HOLDER,
    BLOCK_TERRITORY,
    TRACK_METRICS,
    RESTRICT_EMBED
}

data class RightsAssetRecord(
    val id: String = UUID.randomUUID().toString(),
    val assetTitle: String,
    val rightsHolder: String,
    val territory: String = "Worldwide (240 territories)",
    val licenseType: String = "Master Audio/Visual Enterprise Right",
    val fingerprintHash: String = "fp_sha256_9b83a0018f3d4e8c71",
    val matchStatus: String = "1 Active Video Match (Auto-Monetized)",
    val matchedVideoId: String? = null,
    val defaultClaimAction: RightsClaimAction = RightsClaimAction.MONETIZE_TO_HOLDER,
    val disputeCount: Int = 0
)

data class ObservabilityMetric(
    val serviceName: String,
    val status: String = "HEALTHY",
    val p95LatencyMs: Int,
    val errorRatePercent: Double,
    val activeReplicas: Int,
    val throughputQps: Int
)

data class SystemHealthOverview(
    val activeViewersTotal: Long = 184500,
    val activeLiveStreams: Int = 42,
    val transcodingQueueDepth: Int = 7,
    val globalCdnEdgeHitRatio: Double = 98.6,
    val originStorageUsedTb: Double = 412.8,
    val egressBandwidthGbps: Double = 84.5,
    val clusterServices: List<ObservabilityMetric> = emptyList()
)

data class AuditLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val actor: String,
    val actorRole: UserRole,
    val action: String,
    val resource: String,
    val details: String,
    val ipAddress: String = "192.168.1.104"
)

enum class UploadStage {
    IDLE,
    FILE_VALIDATION,
    VIRUS_SECURITY_SCAN,
    MEDIA_CODEC_ANALYSIS,
    TRANSCODING_RENDITIONS,
    THUMBNAIL_GENERATION,
    SPEECH_TO_TEXT_CAPTIONS,
    CONTENT_ID_SCAN,
    READY_FOR_PUBLISH,
    PUBLISHED
}

data class UploadPipelineJob(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val fileSizeBytes: Long,
    val stage: UploadStage = UploadStage.IDLE,
    val overallProgressPercent: Int = 0,
    val currentSubtask: String = "",
    val detectedResolution: String = "3840x2160 (4K UHD)",
    val detectedCodec: String = "AV1 / Opus",
    val detectedDurationSec: Int = 420,
    val detectedFps: Int = 60,
    val generatedRenditions: List<String> = emptyList(),
    val thumbnailOptions: List<String> = emptyList(),
    val selectedThumbnailIdx: Int = 0,
    val generatedCaptionLanguages: List<String> = emptyList(),
    val contentIdVerdict: String = "No copyright conflicts found (100% Match Cleared)",
    val logs: List<String> = emptyList()
)
