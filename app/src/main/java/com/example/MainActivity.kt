package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.MainViewModel
import com.example.ui.components.UploadIngestWorkspace
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val shorts by viewModel.shorts.collectAsState()
    val channels by viewModel.channels.collectAsState()
    val liveStreams by viewModel.liveStreams.collectAsState()
    val liveChatMessages by viewModel.liveChatMessages.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()
    val watchLaterIds by viewModel.watchLaterIds.collectAsState()
    val likedVideoIds by viewModel.likedVideoIds.collectAsState()
    val selectedVideo by viewModel.selectedVideo.collectAsState()
    val selectedChannelId by viewModel.selectedChannelId.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showUploadModal by viewModel.showUploadModal.collectAsState()
    val activeUploadJob by viewModel.activeUploadJob.collectAsState()
    val moderationCases by viewModel.moderationCases.collectAsState()
    val rightsAssets by viewModel.rightsAssets.collectAsState()
    val adCampaigns by viewModel.adCampaigns.collectAsState()
    val systemHealth by viewModel.systemHealth.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val selectedStudioTimeRange by viewModel.selectedStudioTimeRange.collectAsState()

    var selectedNavTab by remember { mutableStateOf(0) }
    var showRoleMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (selectedVideo == null && !isSearching && selectedChannelId == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBg)
                ) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Red brand logo container
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(YTRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "ENTERPRISE",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.3).sp,
                                    color = TextPrimary
                                )
                            }
                        },
                        actions = {
                            // Search Button
                            IconButton(
                                onClick = { viewModel.openSearch("") },
                                modifier = Modifier.testTag("top_search_button")
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFFE1E1E1),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Role Switcher Dropdown
                            Box {
                                FilterChip(
                                    selected = true,
                                    onClick = { showRoleMenu = true },
                                    label = {
                                        Text(
                                            text = currentRole.name.replace("_", " "),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (currentRole) {
                                                UserRole.CREATOR -> Icons.Default.VideoCameraFront
                                                UserRole.ENTERPRISE_ADMIN -> Icons.Default.AdminPanelSettings
                                                UserRole.MODERATOR -> Icons.Default.Shield
                                                UserRole.ADVERTISER -> Icons.Default.Campaign
                                                else -> Icons.Default.Person
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = AccentBlue
                                        )
                                    },
                                    trailingIcon = {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(14.dp))
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DarkSurfaceElevated,
                                        selectedLabelColor = TextPrimary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = true,
                                        borderColor = DarkSurfaceBorder
                                    )
                                )

                                DropdownMenu(
                                    expanded = showRoleMenu,
                                    onDismissRequest = { showRoleMenu = false },
                                    modifier = Modifier
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                ) {
                                    UserRole.values().forEach { role ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = role.name.replace("_", " "),
                                                    fontWeight = if (currentRole == role) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (currentRole == role) AccentBlue else TextPrimary
                                                )
                                            },
                                            onClick = {
                                                viewModel.setRole(role)
                                                showRoleMenu = false
                                                if (role == UserRole.CREATOR) selectedNavTab = 3
                                                if (role == UserRole.ENTERPRISE_ADMIN) selectedNavTab = 4
                                            }
                                        )
                                    }
                                }
                            }

                            // User Profile Avatar - High-contrast cyan/blue with dark initials
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp, start = 8.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AccentBlue)
                                    .clickable { selectedNavTab = 2 },
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = currentUser.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                                Text(
                                    text = if (initials.isNotEmpty()) initials else "JD",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = DarkBg
                        )
                    )
                    // 1px Border Bottom
                    HorizontalDivider(color = DarkSurfaceBorder, thickness = 1.dp)
                }
            }
        },
        bottomBar = {
            if (selectedVideo == null && !isSearching) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = DarkSurfaceBorder, thickness = 1.dp)
                    NavigationBar(
                        containerColor = DarkBg,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(64.dp)
                    ) {
                        NavigationBarItem(
                            selected = selectedNavTab == 0,
                            onClick = {
                                selectedNavTab = 0
                                viewModel.clearSelectedChannel()
                            },
                            icon = { Icon(if (selectedNavTab == 0) Icons.Default.Home else Icons.Outlined.Home, contentDescription = "Home", modifier = Modifier.size(22.dp)) },
                            label = { Text("Home", fontSize = 10.sp, fontWeight = if (selectedNavTab == 0) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = TextPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )

                        NavigationBarItem(
                            selected = selectedNavTab == 1,
                            onClick = {
                                selectedNavTab = 1
                                viewModel.clearSelectedChannel()
                            },
                            icon = { Icon(if (selectedNavTab == 1) Icons.Default.PlayCircle else Icons.Outlined.PlayCircle, contentDescription = "Shorts", modifier = Modifier.size(22.dp)) },
                            label = { Text("Shorts", fontSize = 10.sp, fontWeight = if (selectedNavTab == 1) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = TextPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )

                        // Upload Quick Action (+) with circular border
                        NavigationBarItem(
                            selected = false,
                            onClick = { viewModel.openUploadModal() },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .border(2.dp, TextSecondary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Upload", tint = TextPrimary, modifier = Modifier.size(20.dp))
                                }
                            },
                            label = { Text("Upload", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                unselectedIconColor = TextPrimary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )

                        NavigationBarItem(
                            selected = selectedNavTab == 3,
                            onClick = {
                                selectedNavTab = 3
                                viewModel.clearSelectedChannel()
                            },
                            icon = { Icon(if (selectedNavTab == 3) Icons.Default.Analytics else Icons.Outlined.Analytics, contentDescription = "Studio", modifier = Modifier.size(22.dp)) },
                            label = { Text("Studio", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = TextPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )

                        NavigationBarItem(
                            selected = selectedNavTab == 4,
                            onClick = {
                                selectedNavTab = 4
                                viewModel.clearSelectedChannel()
                            },
                            icon = { Icon(if (selectedNavTab == 4) Icons.Default.AdminPanelSettings else Icons.Outlined.AdminPanelSettings, contentDescription = "Console", modifier = Modifier.size(22.dp)) },
                            label = { Text("Ops", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = TextPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )

                        NavigationBarItem(
                            selected = selectedNavTab == 2,
                            onClick = {
                                selectedNavTab = 2
                                viewModel.clearSelectedChannel()
                            },
                            icon = { Icon(if (selectedNavTab == 2) Icons.Default.VideoLibrary else Icons.Outlined.VideoLibrary, contentDescription = "Library", modifier = Modifier.size(22.dp)) },
                            label = { Text("Library", fontSize = 10.sp, fontWeight = if (selectedNavTab == 2) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = TextPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Router
            when {
                // Video Watch Screen Active
                selectedVideo != null -> {
                    val currentVid = selectedVideo!!
                    if (currentVid.isLive) {
                        val details = liveStreams.firstOrNull { it.videoId == currentVid.id }
                        val streamMsgs = liveChatMessages[details?.streamId ?: ""] ?: emptyList()
                        LiveWatchScreen(
                            video = currentVid,
                            liveDetails = details,
                            chatMessages = streamMsgs,
                            onSendMessage = { text, sc ->
                                details?.let { viewModel.sendLiveChatMessage(it.streamId, text, sc) }
                            },
                            onDeleteMessage = { msgId ->
                                details?.let { viewModel.deleteLiveChatMessage(it.streamId, msgId) }
                            },
                            onChannelClick = { chId -> viewModel.selectChannel(chId) }
                        )
                    } else {
                        val videoComments = comments[currentVid.id] ?: emptyList()
                        val isSubscribed = subscriptions.any { it.channelId == currentVid.channelId }
                        WatchScreen(
                            video = currentVid,
                            allVideos = videos,
                            comments = videoComments,
                            isSubscribed = isSubscribed,
                            onToggleSubscribe = { chId -> viewModel.toggleSubscribe(chId) },
                            onToggleLike = { vidId -> viewModel.toggleLike(vidId) },
                            onToggleWatchLater = { vidId -> viewModel.toggleWatchLater(vidId) },
                            onAddComment = { vidId, text -> viewModel.addComment(vidId, text) },
                            onLikeComment = { vidId, cId -> viewModel.likeComment(vidId, cId) },
                            onVideoClick = { vid -> viewModel.selectVideo(vid) },
                            onChannelClick = { chId -> viewModel.selectChannel(chId) },
                            onBack = { viewModel.clearSelectedVideo() }
                        )
                    }
                }

                // Channel Screen Active
                selectedChannelId != null -> {
                    val ch = channels.firstOrNull { it.id == selectedChannelId }
                    if (ch != null) {
                        val chVideos = videos.filter { it.channelId == ch.id }
                        val isSub = subscriptions.any { it.channelId == ch.id }
                        ChannelScreen(
                            channel = ch,
                            channelVideos = chVideos,
                            isSubscribed = isSub,
                            onToggleSubscribe = { id -> viewModel.toggleSubscribe(id) },
                            onVideoClick = { vid -> viewModel.selectVideo(vid) },
                            onBack = { viewModel.clearSelectedChannel() }
                        )
                    }
                }

                // Search Screen Active
                isSearching -> {
                    SearchScreen(
                        initialQuery = searchQuery,
                        allVideos = videos,
                        onVideoClick = { vid ->
                            viewModel.closeSearch()
                            viewModel.selectVideo(vid)
                        },
                        onChannelClick = { chId ->
                            viewModel.closeSearch()
                            viewModel.selectChannel(chId)
                        },
                        onBack = { viewModel.closeSearch() }
                    )
                }

                // Primary Bottom Navigation Tabs
                else -> {
                    when (selectedNavTab) {
                        0 -> HomeScreen(
                            videos = videos,
                            shorts = shorts,
                            watchHistory = watchHistory,
                            onVideoClick = { vid -> viewModel.selectVideo(vid) },
                            onShortClick = { shortItem -> selectedNavTab = 1 },
                            onChannelClick = { chId -> viewModel.selectChannel(chId) }
                        )
                        1 -> ShortsScreen(
                            shorts = shorts,
                            onChannelClick = { chId -> viewModel.selectChannel(chId) }
                        )
                        2 -> LibraryScreen(
                            user = currentUser,
                            videos = videos,
                            watchHistory = watchHistory,
                            watchLaterIds = watchLaterIds,
                            likedVideoIds = likedVideoIds,
                            playlists = playlists,
                            onVideoClick = { vid -> viewModel.selectVideo(vid) },
                            onClearHistory = { viewModel.clearHistory() },
                            onCreatePlaylist = { name, desc -> viewModel.createPlaylist(name, desc) }
                        )
                        3 -> CreatorStudioScreen(
                            videos = videos,
                            analytics = viewModel.getCreatorAnalytics(),
                            retentionPoints = viewModel.getAudienceRetentionPoints(),
                            selectedTimeRange = selectedStudioTimeRange,
                            onTimeRangeChange = { range -> viewModel.setStudioTimeRange(range) },
                            onOpenUploadWorkspace = { viewModel.openUploadModal() },
                            onVideoClick = { vid -> viewModel.selectVideo(vid) }
                        )
                        4 -> EnterpriseAdminScreen(
                            systemHealth = systemHealth,
                            moderationCases = moderationCases,
                            rightsAssets = rightsAssets,
                            adCampaigns = adCampaigns,
                            auditLogs = auditLogs,
                            onResolveModeration = { caseId, status, note -> viewModel.resolveModeration(caseId, status, note) },
                            onDisputeRights = { assetId, reason -> viewModel.disputeRights(assetId, reason) }
                        )
                    }
                }
            }

            // Upload Modal Overlay
            if (showUploadModal) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xCC000000))
                        .clickable { viewModel.closeUploadModal() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.clickable(enabled = false) {}) {
                        UploadIngestWorkspace(
                            uploadJob = activeUploadJob,
                            onStartUpload = { fileName -> viewModel.startUploadPipeline(fileName) },
                            onPublish = { title, desc, cat, vis, tags ->
                                viewModel.publishUploadedVideo(title, desc, cat, vis, tags)
                            },
                            onDismiss = { viewModel.closeUploadModal() }
                        )
                    }
                }
            }
        }
    }
}
