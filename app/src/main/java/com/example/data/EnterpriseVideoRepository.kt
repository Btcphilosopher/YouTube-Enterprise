package com.example.data

import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class EnterpriseVideoRepository private constructor() {

    private val scope = CoroutineScope(Dispatchers.Default)

    // Current User & Auth State
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_enterprise_lead",
            name = "Sarah Connor",
            handle = "@s_connor_infra",
            email = "sarah.connor@alphabet-enterprise.com",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            role = UserRole.VIEWER,
            orgId = "org_alpha_01",
            orgName = "Enterprise Global Media Corp",
            isVerified = true
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Active Role Mode in the app (Viewer, Creator Studio, Enterprise Portal, Admin Console)
    private val _currentRole = MutableStateFlow(UserRole.VIEWER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Core Data Entities
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()

    private val _shorts = MutableStateFlow<List<ShortVideoItem>>(emptyList())
    val shorts: StateFlow<List<ShortVideoItem>> = _shorts.asStateFlow()

    private val _liveStreams = MutableStateFlow<List<LiveStreamDetails>>(emptyList())
    val liveStreams: StateFlow<List<LiveStreamDetails>> = _liveStreams.asStateFlow()

    private val _liveChatMessages = MutableStateFlow<Map<String, List<LiveChatMessage>>>(emptyMap())
    val liveChatMessages: StateFlow<Map<String, List<LiveChatMessage>>> = _liveChatMessages.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _subscriptions = MutableStateFlow<List<ChannelSubscription>>(emptyList())
    val subscriptions: StateFlow<List<ChannelSubscription>> = _subscriptions.asStateFlow()

    private val _watchHistory = MutableStateFlow<List<WatchHistoryRecord>>(emptyList())
    val watchHistory: StateFlow<List<WatchHistoryRecord>> = _watchHistory.asStateFlow()

    private val _watchLaterIds = MutableStateFlow<Set<String>>(emptySet())
    val watchLaterIds: StateFlow<Set<String>> = _watchLaterIds.asStateFlow()

    private val _likedVideoIds = MutableStateFlow<Set<String>>(emptySet())
    val likedVideoIds: StateFlow<Set<String>> = _likedVideoIds.asStateFlow()

    // Creator Studio & Ingest State
    private val _activeUploadJob = MutableStateFlow<UploadPipelineJob?>(null)
    val activeUploadJob: StateFlow<UploadPipelineJob?> = _activeUploadJob.asStateFlow()

    // Moderation & Rights
    private val _moderationCases = MutableStateFlow<List<ModerationCase>>(emptyList())
    val moderationCases: StateFlow<List<ModerationCase>> = _moderationCases.asStateFlow()

    private val _rightsAssets = MutableStateFlow<List<RightsAssetRecord>>(emptyList())
    val rightsAssets: StateFlow<List<RightsAssetRecord>> = _rightsAssets.asStateFlow()

    // Ads & Campaigns
    private val _adCampaigns = MutableStateFlow<List<AdCampaign>>(emptyList())
    val adCampaigns: StateFlow<List<AdCampaign>> = _adCampaigns.asStateFlow()

    // System Observability & DevOps
    private val _systemHealth = MutableStateFlow<SystemHealthOverview>(SystemHealthOverview())
    val systemHealth: StateFlow<SystemHealthOverview> = _systemHealth.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(emptyList())
    val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

    init {
        seedInitialEnterpriseData()
    }

    fun setRole(newRole: UserRole) {
        _currentRole.value = newRole
        _currentUser.update { it.copy(role = newRole) }
        logAudit(
            action = "ROLE_SWITCH",
            resource = "IAM/Session",
            details = "User switched active operational view to $newRole"
        )
    }

    private fun logAudit(action: String, resource: String, details: String) {
        val entry = AuditLogEntry(
            actor = _currentUser.value.name,
            actorRole = _currentUser.value.role,
            action = action,
            resource = resource,
            details = details
        )
        _auditLogs.update { listOf(entry) + it.take(99) }
    }

    // ==========================================
    // SEED ENTERPRISE DATASET
    // ==========================================
    private fun seedInitialEnterpriseData() {
        val channelTechLab = Channel(
            id = "chan_tech_lab",
            ownerUserId = "user_tech_lab",
            name = "TECH LAB ENTERPRISE",
            handle = "@techlab_global",
            avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
            bannerUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=1200",
            description = "High-throughput distributed systems, global cloud architecture, low-latency streaming pipelines, and AI infra benchmarks.",
            subscriberCount = 2840000,
            isVerified = true,
            category = "Distributed Systems & Cloud",
            bannerColorStart = 0xFF0F172A,
            bannerColorEnd = 0xFF1E3A8A
        )

        val channelWorldScience = Channel(
            id = "chan_world_science",
            ownerUserId = "user_science",
            name = "WORLD SCIENCE & QUANTUM",
            handle = "@worldscience",
            avatarUrl = "https://images.unsplash.com/photo-1507413245164-6160d8298b31?w=150",
            bannerUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200",
            description = "Exploring quantum computing, astrophysics simulations, fusion energy, and next-gen computational materials science.",
            subscriberCount = 1920000,
            isVerified = true,
            category = "Science & Research",
            bannerColorStart = 0xFF18181B,
            bannerColorEnd = 0xFF581C87
        )

        val channelMusicStudio = Channel(
            id = "chan_music_studio",
            ownerUserId = "user_music",
            name = "MASTER AUDIO LABS",
            handle = "@masteraudio",
            avatarUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=150",
            bannerUrl = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=1200",
            description = "Spatial audio engineering, lossless Dolby Atmos production, acoustics mastering, and modular synthesizers.",
            subscriberCount = 890000,
            isVerified = true,
            category = "Audio Production",
            bannerColorStart = 0xFF27272A,
            bannerColorEnd = 0xFF991B1B
        )

        val channelTravelNow = Channel(
            id = "chan_travel_now",
            ownerUserId = "user_travel",
            name = "GLOBAL PERSPECTIVES 8K",
            handle = "@global8k",
            avatarUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=150",
            bannerUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=1200",
            description = "Cinema-grade 8K 60FPS HDR ultra-high dynamic range aerial documentaries from all seven continents.",
            subscriberCount = 1450000,
            isVerified = true,
            category = "Documentary & Nature",
            bannerColorStart = 0xFF042F2E,
            bannerColorEnd = 0xFF065F46
        )

        val channelCityMedia = Channel(
            id = "chan_city_media",
            ownerUserId = "user_city",
            name = "METROPOLIS ARCHITECTURE",
            handle = "@metropolis_design",
            avatarUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=150",
            bannerUrl = "https://images.unsplash.com/photo-1477959858617-67f30bc75b82?w=1200",
            description = "Megastructures, modern civil engineering, urban logistics, transit modeling, and skyline design.",
            subscriberCount = 670000,
            isVerified = true,
            category = "Architecture & Urbanism",
            bannerColorStart = 0xFF1C1917,
            bannerColorEnd = 0xFF44403C
        )

        _channels.value = listOf(channelTechLab, channelWorldScience, channelMusicStudio, channelTravelNow, channelCityMedia)

        // Seed Videos with Chapters, Captions, Renditions
        val vid1 = Video(
            id = "vid_arch_2026",
            channelId = channelTechLab.id,
            channelName = channelTechLab.name,
            channelAvatar = channelTechLab.avatarUrl,
            channelVerified = true,
            title = "Building Exabyte-Scale Real-Time Video Pipelines: AV1 & Global CDN Architecture",
            description = "Deep dive into how next-generation enterprise media infrastructures achieve sub-50ms ingestion latency, adaptive segment packaging, and dynamic multi-tier transcoding with custom hardware acceleration. We analyze HLS, MPEG-DASH, CMAF low-latency profiles, and edge cache invalidation strategies.",
            thumbnailGradientStart = 0xFF1E3A8A,
            thumbnailGradientEnd = 0xFF0F172A,
            thumbnailCategoryIcon = "cloud",
            thumbnailBadge = "4K 60FPS HDR",
            durationSeconds = 1485, // 24:45
            viewCount = 1240500,
            likeCount = 89400,
            uploadTimestamp = System.currentTimeMillis() - (86400000L * 3),
            category = "Cloud Architecture",
            tags = listOf("AV1", "CDN", "Distributed Systems", "Transcoding", "CMAF", "Video Streaming"),
            chapters = listOf(
                VideoChapter(0, "00:00 - Introduction & Scale Requirements", "High-level overview of global media streaming architecture"),
                VideoChapter(185, "03:05 - Ingest Protocols & Low-Latency RTMP/SRT", "Comparison between RTMP, SRT, and WebRTC for ingestion"),
                VideoChapter(540, "09:00 - Hardware Transcoding Clusters (AV1/HEVC)", "Deploying custom ASIC and GPU encoding matrix at scale"),
                VideoChapter(960, "16:00 - Dynamic CDN Tiering & Edge Caching", "Cache hit optimization and Anycast BGP routing"),
                VideoChapter(1320, "22:00 - Live Client Adaptive Bitrate Metrics", "DASH/HLS manifest manipulation and buffer health algorithms")
            ),
            captions = listOf(
                CaptionTrack("en", "English [Auto-generated 99.4% precision]", listOf(
                    CaptionCue(0, 15, "Welcome everyone to our deep-dive architectural session on enterprise video streaming."),
                    CaptionCue(16, 45, "Today we will analyze how to process petabytes of video per hour with AV1 encoding."),
                    CaptionCue(46, 120, "When an ingest stream arrives at the edge gateway, our first priority is packet validation."),
                    CaptionCue(121, 240, "Using low-latency CMAF chunks allows us to reach sub-second glass-to-glass latency globally.")
                )),
                CaptionTrack("es", "Spanish (Español)", listOf(
                    CaptionCue(0, 15, "Bienvenidos a nuestra sesión sobre arquitectura de video empresarial."),
                    CaptionCue(16, 45, "Hoy analizaremos cómo procesar petabytes de video por hora con codificación AV1.")
                )),
                CaptionTrack("ja", "Japanese (日本語)", listOf(
                    CaptionCue(0, 15, "エンタープライズ動画配信アーキテクチャのセッションへようこそ。"),
                    CaptionCue(16, 45, "本日はAV1エンコーディングを用いた大規模分散処理について解説します。")
                ))
            ),
            renditions = createStandardRenditions("vid_arch_2026", max4k = true),
            contentIdStatus = ContentIdStatus.CLEARED,
            moderationStatus = ModerationStatus.CLEAR
        )

        val vid2 = Video(
            id = "vid_quantum_sim",
            channelId = channelWorldScience.id,
            channelName = channelWorldScience.name,
            channelAvatar = channelWorldScience.avatarUrl,
            channelVerified = true,
            title = "1,000 Qubit Quantum Topological Processor: Full Lab Demonstration & Error Correction",
            description = "Exclusive lab walk-through of the cryogenic dilution refrigerator operating at 12 millikelvin. Watch real-time surface-code quantum error correction preserving logical qubits over 100 microseconds.",
            thumbnailGradientStart = 0xFF581C87,
            thumbnailGradientEnd = 0xFF18181B,
            thumbnailCategoryIcon = "science",
            thumbnailBadge = "4K HDR",
            durationSeconds = 1830, // 30:30
            viewCount = 890400,
            likeCount = 64200,
            uploadTimestamp = System.currentTimeMillis() - (86400000L * 7),
            category = "Quantum Science",
            tags = listOf("Quantum Computing", "Qubits", "Physics", "Cryogenics"),
            chapters = listOf(
                VideoChapter(0, "00:00 - Topological Qubits Explained", "Why non-Abelian anyons resist local environmental noise"),
                VideoChapter(360, "06:00 - The 12mK Dilution Refrigerator", "Inside the multi-stage pulse tube and mixing chamber"),
                VideoChapter(840, "14:00 - Microwave Control Synthesizers", "Direct digital synthesis for qubit state rotation pulses"),
                VideoChapter(1420, "23:40 - Benchmark Results & Coherence Graph", "Demonstrating quantum state fidelity above 99.9%")
            ),
            captions = listOf(
                CaptionTrack("en", "English", listOf(
                    CaptionCue(0, 20, "We are standing inside the high-precision quantum laboratory."),
                    CaptionCue(21, 80, "This 1000 qubit processor demonstrates fault-tolerant topological protection.")
                ))
            ),
            renditions = createStandardRenditions("vid_quantum_sim", max4k = true),
            contentIdStatus = ContentIdStatus.CLEARED,
            moderationStatus = ModerationStatus.CLEAR
        )

        val vid3 = Video(
            id = "vid_live_summit",
            channelId = channelTechLab.id,
            channelName = channelTechLab.name,
            channelAvatar = channelTechLab.avatarUrl,
            channelVerified = true,
            title = "🔴 [LIVE] Global Enterprise Media Summit 2026: Keynote & Real-Time Broadcast Demo",
            description = "Live interactive keynote on distributed transcode scheduling, automated copyright fingerprinting, AI-assisted content moderation, and real-time ad placement at scale.",
            thumbnailGradientStart = 0xFF991B1B,
            thumbnailGradientEnd = 0xFF18181B,
            thumbnailCategoryIcon = "live",
            thumbnailBadge = "LIVE NOW",
            durationSeconds = 0,
            viewCount = 38400,
            likeCount = 8420,
            uploadTimestamp = System.currentTimeMillis() - 3600000L,
            category = "Live Broadcast",
            tags = listOf("Live", "Summit", "Engineering", "Enterprise"),
            isLive = true,
            liveViewerCount = 14280,
            renditions = createStandardRenditions("vid_live_summit", max4k = false),
            contentIdStatus = ContentIdStatus.CLEARED,
            moderationStatus = ModerationStatus.CLEAR
        )

        val vid4 = Video(
            id = "vid_8k_nature",
            channelId = channelTravelNow.id,
            channelName = channelTravelNow.name,
            channelAvatar = channelTravelNow.avatarUrl,
            channelVerified = true,
            title = "Icelandic Volcanic Highlands in 8K 60FPS: Ultra Cinema Color & Spatial Sound",
            description = "Captured with 12K full-frame sensors and mastered for 8K HDR displays with wide color gamut (Rec.2020) and 3D ambisonic field recordings.",
            thumbnailGradientStart = 0xFF065F46,
            thumbnailGradientEnd = 0xFF022C22,
            thumbnailCategoryIcon = "nature",
            thumbnailBadge = "8K 60FPS",
            durationSeconds = 875, // 14:35
            viewCount = 2190000,
            likeCount = 182000,
            uploadTimestamp = System.currentTimeMillis() - (86400000L * 12),
            category = "Documentary",
            tags = listOf("8K", "HDR", "Iceland", "Cinematography", "Nature"),
            renditions = createStandardRenditions("vid_8k_nature", max4k = true, has8k = true),
            contentIdStatus = ContentIdStatus.CLEARED,
            moderationStatus = ModerationStatus.CLEAR
        )

        val vid5 = Video(
            id = "vid_spatial_audio",
            channelId = channelMusicStudio.id,
            channelName = channelMusicStudio.name,
            channelAvatar = channelMusicStudio.avatarUrl,
            channelVerified = true,
            title = "Mastering Immersive 7.1.4 Dolby Atmos: Studio Mix Breakdown & Stem Routing",
            description = "Step-by-step masterclass on spatial panning, object metadata placement, binaural head-related transfer function (HRTF) tuning, and dynamic range preservation.",
            thumbnailGradientStart = 0xFF831843,
            thumbnailGradientEnd = 0xFF3B0764,
            thumbnailCategoryIcon = "audio",
            thumbnailBadge = "Spatial Audio",
            durationSeconds = 1240,
            viewCount = 412000,
            likeCount = 31500,
            uploadTimestamp = System.currentTimeMillis() - (86400000L * 5),
            category = "Audio Production",
            tags = listOf("Spatial Audio", "Dolby Atmos", "Mastering", "Music"),
            renditions = createStandardRenditions("vid_spatial_audio", max4k = false),
            contentIdStatus = ContentIdStatus.CLEARED,
            moderationStatus = ModerationStatus.CLEAR
        )

        val vid6 = Video(
            id = "vid_tokyo_transit",
            channelId = channelCityMedia.id,
            channelName = channelCityMedia.name,
            channelAvatar = channelCityMedia.avatarUrl,
            channelVerified = true,
            title = "Engineering the World's Most Complex Metro: Tokyo Transit Automation & 3D Logistics",
            description = "Detailed structural and computational breakdown of automatic train control (ATC), subterranean tunnel boring machines, and real-time station flow modeling.",
            thumbnailGradientStart = 0xFF374151,
            thumbnailGradientEnd = 0xFF111827,
            thumbnailCategoryIcon = "architecture",
            thumbnailBadge = "4K 60FPS",
            durationSeconds = 1110,
            viewCount = 980000,
            likeCount = 74000,
            uploadTimestamp = System.currentTimeMillis() - (86400000L * 8),
            category = "Engineering",
            tags = listOf("Tokyo", "Subway", "Civil Engineering", "Automation"),
            renditions = createStandardRenditions("vid_tokyo_transit", max4k = true),
            contentIdStatus = ContentIdStatus.CLEARED,
            moderationStatus = ModerationStatus.CLEAR
        )

        _videos.value = listOf(vid1, vid2, vid3, vid4, vid5, vid6)

        // Seed Shorts
        val short1 = ShortVideoItem(
            id = "short_1",
            video = vid1.copy(
                id = "short_vid_1",
                title = "Why AV1 Compression is 30% Better Than HEVC in 60 seconds! ⚡",
                isShort = true,
                durationSeconds = 58
            ),
            audioTrackTitle = "Synthwave Tech Drive (Master Mix)",
            audioAuthor = "Tech Lab Audio",
            commentsCount = 1420,
            sharesCount = 5900,
            isLiked = true
        )

        val short2 = ShortVideoItem(
            id = "short_2",
            video = vid2.copy(
                id = "short_vid_2",
                title = "Look inside a real quantum dilution fridge! 🧊 -273.14°C",
                isShort = true,
                durationSeconds = 44
            ),
            audioTrackTitle = "Cosmic Sub-Bass Ambient",
            audioAuthor = "World Science Sound",
            commentsCount = 3890,
            sharesCount = 12400,
            isLiked = false
        )

        val short3 = ShortVideoItem(
            id = "short_3",
            video = vid4.copy(
                id = "short_vid_3",
                title = "8K drone dive into an active volcanic fissure! 🌋",
                isShort = true,
                durationSeconds = 35
            ),
            audioTrackTitle = "Thunder & Glacier Acoustic",
            audioAuthor = "Global Perspectives",
            commentsCount = 2180,
            sharesCount = 9800,
            isLiked = true
        )

        _shorts.value = listOf(short1, short2, short3)

        // Live stream details
        val liveDetails = LiveStreamDetails(
            streamId = "live_stream_01",
            videoId = vid3.id,
            streamKey = "live_key_yt_ent_9921_x9f",
            health = StreamHealthState.EXCELLENT,
            currentBitrateKbps = 8500,
            targetBitrateKbps = 8500,
            fps = 60,
            droppedFramesRate = 0.002,
            latencyMs = 1180,
            cpuUsagePercent = 29,
            isSlowModeActive = false,
            isSubscriberOnly = false
        )
        _liveStreams.value = listOf(liveDetails)

        // Live chat messages
        val initialChat = listOf(
            LiveChatMessage(
                streamId = liveDetails.streamId,
                userId = "user_mod_1",
                userName = "TechLab Moderator",
                userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                text = "Welcome everyone to the 2026 Enterprise Summit! Please keep Q&A questions focused on stream architecture.",
                isModerator = true,
                isPinned = true
            ),
            LiveChatMessage(
                streamId = liveDetails.streamId,
                userId = "user_dev_9",
                userName = "Alex Rivers",
                userAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                text = "Are you seeing better latency gains with CMAF or pure WebRTC for sub-50ms sync?",
                isVerified = true
            ),
            LiveChatMessage(
                streamId = liveDetails.streamId,
                userId = "user_vip_1",
                userName = "Marcus Cloud Corp",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                text = "Super excited for the AV1 hardware transcoding benchmarks!",
                isSuperChat = true,
                superChatAmount = "$50.00",
                superChatTierColor = 0xFFE11D48
            ),
            LiveChatMessage(
                streamId = liveDetails.streamId,
                userId = "user_viewer_42",
                userName = "Elena Petrova",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                text = "Audio and 4K stream quality looks pristine on 100Gbps connection.",
                isVerified = false
            )
        )
        _liveChatMessages.value = mapOf(liveDetails.streamId to initialChat)

        // Seed Video Comments
        val initialComments = listOf(
            Comment(
                id = "comm_1",
                videoId = vid1.id,
                userId = "user_eng_1",
                userName = "David Lin (Staff Infra Architect)",
                userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                text = "The breakdown of CMAF chunk chunking with chunked transfer encoding (CTE) at 09:00 was phenomenal. We saw a 40% reduction in buffering time when implementing this across European edge POPs.",
                timestampAgo = "2 days ago",
                likeCount = 384,
                isCreatorHearted = true,
                isPinned = true,
                replies = listOf(
                    CommentReply(
                        id = "rep_1",
                        commentId = "comm_1",
                        userId = channelTechLab.ownerUserId,
                        userName = "TECH LAB ENTERPRISE",
                        userAvatar = channelTechLab.avatarUrl,
                        text = "Thanks David! Next week we're releasing the open benchmark harness for CTE testing.",
                        timestampAgo = "1 day ago",
                        likeCount = 92,
                        isCreator = true
                    )
                )
            ),
            Comment(
                id = "comm_2",
                videoId = vid1.id,
                userId = "user_sec_2",
                userName = "Clara Evans",
                userAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150",
                text = "How are you handling token rotation for signed DRM manifests on low-latency DASH streams?",
                timestampAgo = "1 day ago",
                likeCount = 76,
                isCreatorHearted = false
            )
        )
        _comments.value = mapOf(vid1.id to initialComments)

        // Initial Subscriptions
        _subscriptions.value = listOf(
            ChannelSubscription(channelTechLab.id, NotificationLevel.ALL),
            ChannelSubscription(channelWorldScience.id, NotificationLevel.PERSONALISED)
        )

        // Initial Watch History
        _watchHistory.value = listOf(
            WatchHistoryRecord(vid1.id, System.currentTimeMillis() - 3600000, 480, vid1.durationSeconds),
            WatchHistoryRecord(vid2.id, System.currentTimeMillis() - 86400000, 920, vid2.durationSeconds)
        )

        // Playlists
        _playlists.value = listOf(
            Playlist(
                id = "play_arch_core",
                title = "Enterprise Video Architecture Masterclass",
                description = "Curated curriculum on media ingest, transcode clustering, and edge CDN delivery.",
                visibility = VideoVisibility.PUBLIC,
                ownerId = _currentUser.value.id,
                ownerName = _currentUser.value.name,
                videoIds = listOf(vid1.id, vid3.id, vid6.id)
            ),
            Playlist(
                id = "play_science_deep",
                title = "Quantum & Physics Simulations",
                description = "Lab demonstrations and computational benchmarks.",
                visibility = VideoVisibility.PUBLIC,
                ownerId = _currentUser.value.id,
                ownerName = _currentUser.value.name,
                videoIds = listOf(vid2.id, vid4.id)
            )
        )

        // Seed Moderation Cases
        _moderationCases.value = listOf(
            ModerationCase(
                id = "mod_case_101",
                targetContentId = "vid_sample_flagged_99",
                contentTitle = "Automated Hardware Benchmark Raw Ingest #442",
                channelName = "External Dev Partner",
                flaggedReason = "Automated classifier detected possible confidential unreleased hardware telemetry tag",
                riskClassification = "Moderate Risk (Policy Review)",
                riskScore = 0.68f,
                status = ModerationStatus.UNDER_REVIEW,
                autoMlConfidence = 0.91f
            ),
            ModerationCase(
                id = "mod_case_102",
                targetContentId = "comm_flagged_204",
                contentTitle = "Comment on Tech Lab Keynote",
                channelName = "System",
                flaggedReason = "Automated spam filter detected repetitive URL redirect scheme",
                riskClassification = "High Risk (Spam/Phishing)",
                riskScore = 0.96f,
                status = ModerationStatus.REMOVED,
                autoMlConfidence = 0.98f
            )
        )

        // Seed Rights & Content ID
        _rightsAssets.value = listOf(
            RightsAssetRecord(
                id = "right_asset_001",
                assetTitle = "Enterprise Global Theme - Master Audio Track",
                rightsHolder = "Alphabet Media Publishing LLC",
                territory = "Worldwide (All Territories)",
                licenseType = "Exclusive Commercial Soundtrack",
                fingerprintHash = "fp_sha256_7a99c011e4f",
                matchStatus = "0 Disputes, 4 Active Claim Matches (Monetized to Holder)",
                matchedVideoId = vid5.id,
                defaultClaimAction = RightsClaimAction.MONETIZE_TO_HOLDER
            ),
            RightsAssetRecord(
                id = "right_asset_002",
                assetTitle = "Quantum Key Distribution Visual 3D Assets",
                rightsHolder = "World Science Foundation",
                territory = "North America & Europe",
                licenseType = "Research Educational Commons (CC-BY-NC)",
                fingerprintHash = "fp_sha256_3b811ef299a",
                matchStatus = "1 Match (Track Only)",
                matchedVideoId = vid2.id,
                defaultClaimAction = RightsClaimAction.TRACK_METRICS
            )
        )

        // Seed Ad Campaigns
        _adCampaigns.value = listOf(
            AdCampaign(
                id = "ad_camp_01",
                advertiserName = "Google Cloud Enterprise",
                campaignName = "Vertex AI Infrastructure for High-Throughput Media",
                format = AdFormat.PRE_ROLL,
                title = "Scale Your Video AI Pipeline on Google Cloud",
                ctaText = "Start Free Tier",
                targetUrl = "https://cloud.google.com/vertex-ai",
                durationSeconds = 15,
                bidCpmUsd = 24.50,
                dailyBudgetUsd = 5000.0,
                totalSpendUsd = 42800.0,
                impressionsServed = 1840000,
                clicksReceived = 82900,
                targetCategories = listOf("Cloud Architecture", "Distributed Systems", "Technology")
            ),
            AdCampaign(
                id = "ad_camp_02",
                advertiserName = "Datadog Observability",
                campaignName = "End-to-End Live Stream Telemetry & SLO Tracking",
                format = AdFormat.MID_ROLL,
                title = "Monitor Video Dropped Frames & Ingest Health in Real Time",
                ctaText = "Explore Dashboards",
                targetUrl = "https://datadoghq.com",
                durationSeconds = 15,
                bidCpmUsd = 18.20,
                dailyBudgetUsd = 3000.0,
                totalSpendUsd = 21400.0,
                impressionsServed = 920000,
                clicksReceived = 41200,
                targetCategories = listOf("Technology", "Cloud Architecture")
            )
        )

        // Seed Observability & System Health
        _systemHealth.value = SystemHealthOverview(
            activeViewersTotal = 248900,
            activeLiveStreams = 58,
            transcodingQueueDepth = 12,
            globalCdnEdgeHitRatio = 99.2,
            originStorageUsedTb = 684.2,
            egressBandwidthGbps = 112.4,
            clusterServices = listOf(
                ObservabilityMetric("Identity & SSO (OAuth2/SAML)", "HEALTHY", 12, 0.001, 8, 3400),
                ObservabilityMetric("Ingest & Packaging (RTMP/SRT/CMAF)", "HEALTHY", 18, 0.004, 16, 8900),
                ObservabilityMetric("Distributed Transcoder (AV1/HEVC GPU)", "HEALTHY", 45, 0.012, 32, 1200),
                ObservabilityMetric("Recommendation & Ranking Engine", "HEALTHY", 28, 0.002, 24, 14200),
                ObservabilityMetric("Content ID Fingerprint Engine", "HEALTHY", 35, 0.008, 12, 2100),
                ObservabilityMetric("Live Chat Edge Relays", "HEALTHY", 8, 0.001, 20, 28000)
            )
        )
    }

    private fun createStandardRenditions(videoId: String, max4k: Boolean = true, has8k: Boolean = false): List<VideoRendition> {
        val list = mutableListOf(
            VideoRendition("144p", 256, 144, 120, "AV1", "DASH", "https://cdn.ytenterprise.internal/$videoId/144p.mpd"),
            VideoRendition("360p", 640, 360, 450, "AV1", "DASH", "https://cdn.ytenterprise.internal/$videoId/360p.mpd"),
            VideoRendition("720p 60fps", 1280, 720, 2200, "AV1", "DASH", "https://cdn.ytenterprise.internal/$videoId/720p.mpd"),
            VideoRendition("1080p 60fps", 1920, 1080, 4800, "AV1", "DASH", "https://cdn.ytenterprise.internal/$videoId/1080p.mpd")
        )
        if (max4k) {
            list.add(VideoRendition("4K HDR (2160p)", 3840, 2160, 14500, "AV1 / VP9", "DASH", "https://cdn.ytenterprise.internal/$videoId/4k.mpd"))
        }
        if (has8k) {
            list.add(VideoRendition("8K HDR (4320p)", 7680, 4320, 38000, "AV1 Master", "DASH", "https://cdn.ytenterprise.internal/$videoId/8k.mpd"))
        }
        return list
    }

    // ==========================================
    // REACTION / INTERACTION APIS
    // ==========================================
    fun toggleLikeVideo(videoId: String) {
        val isLiked = _likedVideoIds.value.contains(videoId)
        _likedVideoIds.update { if (isLiked) it - videoId else it + videoId }

        _videos.update { list ->
            list.map { v ->
                if (v.id == videoId) {
                    val newCount = if (isLiked) v.likeCount - 1 else v.likeCount + 1
                    v.copy(likeCount = newCount, isLikedByCurrentUser = !isLiked)
                } else v
            }
        }
        logAudit("LIKE_VIDEO", "Video/$videoId", if (isLiked) "Unliked video" else "Liked video")
    }

    fun toggleWatchLater(videoId: String) {
        val isSaved = _watchLaterIds.value.contains(videoId)
        _watchLaterIds.update { if (isSaved) it - videoId else it + videoId }

        _videos.update { list ->
            list.map { v ->
                if (v.id == videoId) v.copy(isSavedToWatchLater = !isSaved) else v
            }
        }
        logAudit("WATCH_LATER", "Video/$videoId", if (isSaved) "Removed from Watch Later" else "Added to Watch Later")
    }

    fun toggleSubscribe(channelId: String) {
        val isSubscribed = _subscriptions.value.any { it.channelId == channelId }
        if (isSubscribed) {
            _subscriptions.update { it.filterNot { sub -> sub.channelId == channelId } }
            _channels.update { list ->
                list.map { c -> if (c.id == channelId) c.copy(subscriberCount = c.subscriberCount - 1) else c }
            }
            logAudit("UNSUBSCRIBE", "Channel/$channelId", "User unsubscribed")
        } else {
            _subscriptions.update { it + ChannelSubscription(channelId, NotificationLevel.ALL) }
            _channels.update { list ->
                list.map { c -> if (c.id == channelId) c.copy(subscriberCount = c.subscriberCount + 1) else c }
            }
            logAudit("SUBSCRIBE", "Channel/$channelId", "User subscribed with ALL notifications")
        }
    }

    fun setSubscriptionNotification(channelId: String, level: NotificationLevel) {
        _subscriptions.update { list ->
            list.map { sub -> if (sub.channelId == channelId) sub.copy(notificationLevel = level) else sub }
        }
        logAudit("NOTIFICATION_PREF", "Channel/$channelId", "Notification preference updated to $level")
    }

    fun recordWatchProgress(videoId: String, progressSec: Int, totalDurationSec: Int) {
        _watchHistory.update { list ->
            val updated = list.filterNot { it.videoId == videoId }
            listOf(WatchHistoryRecord(videoId, System.currentTimeMillis(), progressSec, totalDurationSec)) + updated
        }
    }

    fun clearWatchHistory() {
        _watchHistory.value = emptyList()
        logAudit("CLEAR_HISTORY", "User/WatchHistory", "User purged watch history")
    }

    fun removeHistoryItem(videoId: String) {
        _watchHistory.update { it.filterNot { item -> item.videoId == videoId } }
    }

    fun addComment(videoId: String, text: String) {
        val newComment = Comment(
            videoId = videoId,
            userId = _currentUser.value.id,
            userName = _currentUser.value.name,
            userAvatar = _currentUser.value.avatarUrl,
            text = text,
            timestampAgo = "Just now",
            likeCount = 0
        )
        _comments.update { map ->
            val existing = map[videoId] ?: emptyList()
            map + (videoId to (listOf(newComment) + existing))
        }
        logAudit("POST_COMMENT", "Video/$videoId", "User posted comment: \"${text.take(30)}...\"")
    }

    fun toggleLikeComment(videoId: String, commentId: String) {
        _comments.update { map ->
            val list = map[videoId] ?: emptyList()
            val updated = list.map { c ->
                if (c.id == commentId) {
                    val wasLiked = c.isLiked
                    c.copy(
                        isLiked = !wasLiked,
                        likeCount = if (wasLiked) c.likeCount - 1 else c.likeCount + 1
                    )
                } else c
            }
            map + (videoId to updated)
        }
    }

    fun sendLiveChatMessage(streamId: String, text: String, superChatAmount: String? = null) {
        val user = _currentUser.value
        val msg = LiveChatMessage(
            streamId = streamId,
            userId = user.id,
            userName = user.name,
            userAvatar = user.avatarUrl,
            text = text,
            isSuperChat = superChatAmount != null,
            superChatAmount = superChatAmount,
            superChatTierColor = if (superChatAmount != null) 0xFFE11D48 else null,
            isModerator = user.role == UserRole.MODERATOR || user.role == UserRole.ENTERPRISE_ADMIN,
            isVerified = user.isVerified
        )
        _liveChatMessages.update { map ->
            val existing = map[streamId] ?: emptyList()
            map + (streamId to (existing + msg))
        }
    }

    fun deleteLiveChatMessage(streamId: String, messageId: String) {
        _liveChatMessages.update { map ->
            val list = map[streamId] ?: emptyList()
            val updated = list.map { if (it.id == messageId) it.copy(isDeleted = true, text = "[Message removed by moderator]") else it }
            map + (streamId to updated)
        }
        logAudit("LIVE_MODERATION", "Stream/$streamId", "Moderator removed message $messageId")
    }

    fun createPlaylist(title: String, description: String, initialVideoId: String? = null) {
        val newPlaylist = Playlist(
            title = title,
            description = description,
            visibility = VideoVisibility.PUBLIC,
            ownerId = _currentUser.value.id,
            ownerName = _currentUser.value.name,
            videoIds = if (initialVideoId != null) listOf(initialVideoId) else emptyList()
        )
        _playlists.update { listOf(newPlaylist) + it }
        logAudit("CREATE_PLAYLIST", "Playlist/${newPlaylist.id}", "Created playlist: $title")
    }

    fun addVideoToPlaylist(playlistId: String, videoId: String) {
        _playlists.update { list ->
            list.map { p ->
                if (p.id == playlistId && !p.videoIds.contains(videoId)) {
                    p.copy(videoIds = p.videoIds + videoId, updatedAt = System.currentTimeMillis())
                } else p
            }
        }
    }

    // ==========================================
    // UPLOAD PIPELINE SIMULATOR (9 STAGES)
    // ==========================================
    fun startUploadPipeline(fileName: String, fileSizeBytes: Long = 1845000000L) {
        val job = UploadPipelineJob(
            fileName = fileName,
            fileSizeBytes = fileSizeBytes,
            stage = UploadStage.FILE_VALIDATION,
            overallProgressPercent = 5,
            currentSubtask = "Validating container format & moov atom...",
            logs = listOf("[INIT] Ingest session opened for $fileName (1.84 GB)")
        )
        _activeUploadJob.value = job

        scope.launch {
            delay(800)
            // Virus Scan
            _activeUploadJob.update {
                it?.copy(
                    stage = UploadStage.VIRUS_SECURITY_SCAN,
                    overallProgressPercent = 18,
                    currentSubtask = "Scanning SHA-256 binary signatures & heuristic sandbox...",
                    logs = it.logs + "[SECURITY] ClamAV & Enterprise Sandbox: 0 threats detected (Clean)"
                )
            }
            delay(900)
            // Media Analysis
            _activeUploadJob.update {
                it?.copy(
                    stage = UploadStage.MEDIA_CODEC_ANALYSIS,
                    overallProgressPercent = 35,
                    currentSubtask = "Analyzing ffprobe stream: 3840x2160 AV1 Main Profile, 60.0 fps, 10-bit Rec.2020",
                    logs = it.logs + "[MEDIA] Codec: AV1 / Audio: 48kHz Opus 5.1 / Duration: 07:00"
                )
            }
            delay(1000)
            // Transcode
            _activeUploadJob.update {
                it?.copy(
                    stage = UploadStage.TRANSCODING_RENDITIONS,
                    overallProgressPercent = 60,
                    currentSubtask = "GPU Transcoding multi-bitrate ladder (144p, 360p, 720p, 1080p, 4K HDR)...",
                    generatedRenditions = listOf("144p", "360p", "720p60", "1080p60", "4K HDR"),
                    logs = it.logs + "[TRANSCODE] Distributed Worker Cluster completed 5 renditions via CMAF"
                )
            }
            delay(900)
            // Thumbnails
            _activeUploadJob.update {
                it?.copy(
                    stage = UploadStage.THUMBNAIL_GENERATION,
                    overallProgressPercent = 75,
                    currentSubtask = "Extracting optimal keyframes via aesthetic saliency scoring...",
                    thumbnailOptions = listOf("Frame @ 00:14", "Frame @ 02:40", "Frame @ 05:12", "Text Overlay Candidate"),
                    logs = it.logs + "[THUMBNAILS] 4 high-fidelity thumbnails generated & cached in S3"
                )
            }
            delay(800)
            // Captions
            _activeUploadJob.update {
                it?.copy(
                    stage = UploadStage.SPEECH_TO_TEXT_CAPTIONS,
                    overallProgressPercent = 88,
                    currentSubtask = "Speech-to-text neural transcription in progress (English, Spanish, Japanese)...",
                    generatedCaptionLanguages = listOf("English [Auto 99.2%]", "Spanish", "Japanese", "German"),
                    logs = it.logs + "[CAPTIONS] Timed WebVTT & TTML cues generated across 4 languages"
                )
            }
            delay(800)
            // Content ID Scan
            _activeUploadJob.update {
                it?.copy(
                    stage = UploadStage.CONTENT_ID_SCAN,
                    overallProgressPercent = 95,
                    currentSubtask = "Querying Content ID reference database (450M acoustic & visual hashes)...",
                    contentIdVerdict = "100% Cleared - No matching third-party copyright claims",
                    logs = it.logs + "[CONTENT_ID] Lawful fingerprint verification: PASS (Zero conflicts)"
                )
            }
            delay(600)
            // Ready
            _activeUploadJob.update {
                it?.copy(
                    stage = UploadStage.READY_FOR_PUBLISH,
                    overallProgressPercent = 100,
                    currentSubtask = "Media package ready for immediate publication or scheduling",
                    logs = it.logs + "[SUCCESS] Asset is verified and ready to go live"
                )
            }
        }
    }

    fun publishUploadedVideo(
        title: String,
        description: String,
        category: String,
        visibility: VideoVisibility,
        tags: List<String>
    ): Video {
        val job = _activeUploadJob.value
        val channel = _channels.value.first()
        val newVideo = Video(
            id = "vid_pub_${System.currentTimeMillis() % 100000}",
            channelId = channel.id,
            channelName = channel.name,
            channelAvatar = channel.avatarUrl,
            channelVerified = true,
            title = title.ifBlank { job?.fileName ?: "New Enterprise Video" },
            description = description.ifBlank { "High-definition enterprise media publication." },
            thumbnailGradientStart = 0xFF1E3A8A,
            thumbnailGradientEnd = 0xFF0F172A,
            thumbnailCategoryIcon = "cloud",
            thumbnailBadge = "4K HDR",
            durationSeconds = job?.detectedDurationSec ?: 420,
            viewCount = 1,
            likeCount = 0,
            uploadTimestamp = System.currentTimeMillis(),
            category = category.ifBlank { "Cloud Architecture" },
            visibility = visibility,
            tags = if (tags.isNotEmpty()) tags else listOf("Enterprise", "Cloud", "Video"),
            renditions = createStandardRenditions("vid_pub_custom", max4k = true),
            contentIdStatus = ContentIdStatus.CLEARED,
            moderationStatus = ModerationStatus.CLEAR
        )

        _videos.update { listOf(newVideo) + it }
        _activeUploadJob.value = null
        logAudit("PUBLISH_VIDEO", "Video/${newVideo.id}", "Published video \"$title\" with visibility $visibility")
        return newVideo
    }

    // ==========================================
    // CREATOR STUDIO METRICS & RETENTION
    // ==========================================
    fun getCreatorAnalytics(timeRangeLabel: String): CreatorAnalytics {
        return when (timeRangeLabel) {
            "24 HOURS" -> CreatorAnalytics(
                views28d = 18400,
                viewsChangePercent = 28.5,
                watchTimeHours28d = 2480.0,
                watchTimeChangePercent = 31.0,
                subscribers28d = 450,
                subscribersChangePercent = 15.2,
                revenueUsd28d = 890.00,
                revenueChangePercent = 24.1,
                impressions28d = 142000,
                ctrPercent28d = 8.6,
                averageViewDurationSec = 490
            )
            "7 DAYS" -> CreatorAnalytics(
                views28d = 124000,
                viewsChangePercent = 14.8,
                watchTimeHours28d = 16200.0,
                watchTimeChangePercent = 18.3,
                subscribers28d = 3600,
                subscribersChangePercent = 11.4,
                revenueUsd28d = 4820.00,
                revenueChangePercent = 16.0,
                impressions28d = 890000,
                ctrPercent28d = 7.9,
                averageViewDurationSec = 475
            )
            "90 DAYS" -> CreatorAnalytics(
                views28d = 1480000,
                viewsChangePercent = 25.4,
                watchTimeHours28d = 192000.0,
                watchTimeChangePercent = 28.9,
                subscribers28d = 44000,
                subscribersChangePercent = 19.8,
                revenueUsd28d = 58900.00,
                revenueChangePercent = 21.3,
                impressions28d = 11200000,
                ctrPercent28d = 7.2,
                averageViewDurationSec = 460
            )
            else -> CreatorAnalytics() // default 28 DAYS
        }
    }

    fun getAudienceRetentionPoints(): List<AudienceRetentionPoint> {
        return listOf(
            AudienceRetentionPoint(0, 100.0f, "100% - Video Start"),
            AudienceRetentionPoint(10, 88.5f, "Typical Hook Drop-off (-11.5%)"),
            AudienceRetentionPoint(25, 82.0f, "Intro Completed"),
            AudienceRetentionPoint(40, 79.5f, null),
            AudienceRetentionPoint(50, 84.0f, "⚡ Rewatch Spike: Benchmark Chart"),
            AudienceRetentionPoint(65, 76.0f, null),
            AudienceRetentionPoint(75, 71.5f, "Key Architecture Demo"),
            AudienceRetentionPoint(90, 64.0f, "Conclusion Wrap-up"),
            AudienceRetentionPoint(100, 52.0f, "End Screen CTA")
        )
    }

    // ==========================================
    // MODERATION & RIGHTS ACTIONS
    // ==========================================
    fun resolveModerationCase(caseId: String, newStatus: ModerationStatus, reasonNote: String) {
        _moderationCases.update { list ->
            list.map { c ->
                if (c.id == caseId) {
                    c.copy(
                        status = newStatus,
                        auditTrail = c.auditTrail + "Moderator decision: $newStatus ($reasonNote)"
                    )
                } else c
            }
        }
        logAudit("MODERATION_ACTION", "Case/$caseId", "Moderator set status to $newStatus: $reasonNote")
    }

    fun disputeRightsClaim(assetId: String, reason: String) {
        _rightsAssets.update { list ->
            list.map { a ->
                if (a.id == assetId) a.copy(disputeCount = a.disputeCount + 1, matchStatus = "Dispute in progress ($reason)") else a
            }
        }
        logAudit("RIGHTS_DISPUTE", "Asset/$assetId", "Dispute lodged: $reason")
    }

    // ==========================================
    // AD AUCTION SIMULATION
    // ==========================================
    fun runAdAuction(targetVideo: Video): AdCampaign? {
        val eligibleCampaigns = _adCampaigns.value.filter { camp ->
            camp.targetCategories.any { cat -> cat.equals(targetVideo.category, ignoreCase = true) || targetVideo.tags.contains(cat) }
        }
        // Score = bid * (1.0 + quality_score)
        val winning = eligibleCampaigns.maxByOrNull { it.bidCpmUsd } ?: _adCampaigns.value.firstOrNull()
        if (winning != null) {
            _adCampaigns.update { list ->
                list.map { c -> if (c.id == winning.id) c.copy(impressionsServed = c.impressionsServed + 1) else c }
            }
        }
        return winning
    }

    // ==========================================
    // BULK ENTERPRISE OPERATIONS
    // ==========================================
    fun bulkUpdateVisibility(videoIds: List<String>, visibility: VideoVisibility) {
        _videos.update { list ->
            list.map { if (videoIds.contains(it.id)) it.copy(visibility = visibility) else it }
        }
        logAudit("BULK_VISIBILITY", "Videos/${videoIds.size}_items", "Bulk updated visibility to $visibility")
    }

    fun bulkUpdateCategory(videoIds: List<String>, newCategory: String) {
        _videos.update { list ->
            list.map { if (videoIds.contains(it.id)) it.copy(category = newCategory) else it }
        }
        logAudit("BULK_CATEGORY", "Videos/${videoIds.size}_items", "Bulk assigned category: $newCategory")
    }

    // ==========================================
    // SEARCH & RECOMMENDATIONS ENGINE
    // ==========================================
    fun search(query: String, categoryFilter: String? = null, durationFilter: String? = null): List<Video> {
        val q = query.trim().lowercase()
        return _videos.value.filter { v ->
            val matchesQuery = if (q.isBlank()) true else {
                v.title.lowercase().contains(q) ||
                v.description.lowercase().contains(q) ||
                v.tags.any { it.lowercase().contains(q) } ||
                v.channelName.lowercase().contains(q) ||
                v.chapters.any { it.title.lowercase().contains(q) } ||
                v.captions.any { track -> track.cues.any { cue -> cue.text.lowercase().contains(q) } }
            }

            val matchesCategory = if (categoryFilter == null || categoryFilter == "All") true else {
                v.category.equals(categoryFilter, ignoreCase = true)
            }

            val matchesDuration = when (durationFilter) {
                "Under 4 min" -> v.durationSeconds in 1..240
                "4 - 20 min" -> v.durationSeconds in 241..1200
                "Over 20 min" -> v.durationSeconds > 1200
                else -> true
            }

            matchesQuery && matchesCategory && matchesDuration
        }
    }

    companion object {
        @Volatile
        private var instance: EnterpriseVideoRepository? = null

        fun getInstance(): EnterpriseVideoRepository {
            return instance ?: synchronized(this) {
                instance ?: EnterpriseVideoRepository().also { instance = it }
            }
        }
    }
}
