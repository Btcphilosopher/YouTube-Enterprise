package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun EnterpriseVideoPlayer(
    video: Video,
    currentProgressSec: Int,
    onProgressChange: (Int) -> Unit,
    isPlaying: Boolean,
    onPlayPauseToggle: () -> Unit,
    modifier: Modifier = Modifier,
    adCampaign: AdCampaign? = null,
    onSeekChapter: (Int) -> Unit = {}
) {
    var showControls by remember { mutableStateOf(true) }
    var selectedQuality by remember { mutableStateOf("1080p 60fps") }
    var selectedSpeed by remember { mutableStateOf(1.0f) }
    var isCaptionsEnabled by remember { mutableStateOf(true) }
    var selectedCaptionLang by remember { mutableStateOf("English") }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showStatsForNerds by remember { mutableStateOf(false) }
    var isTheatreMode by remember { mutableStateOf(false) }

    // Ad state simulation
    var adPlaying by remember(video.id) { mutableStateOf(adCampaign != null) }
    var adCountdownSec by remember { mutableStateOf(5) }

    // Auto-hide controls after 4 seconds
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000)
            showControls = false
        }
    }

    // Ad countdown timer
    LaunchedEffect(adPlaying) {
        if (adPlaying) {
            while (adCountdownSec > 0) {
                delay(1000)
                adCountdownSec--
            }
        }
    }

    // Real-time playback timer simulation
    LaunchedEffect(isPlaying, video.id, adPlaying) {
        if (isPlaying && !adPlaying && !video.isLive) {
            while (true) {
                delay((1000 / selectedSpeed).toLong())
                if (currentProgressSec < video.durationSeconds) {
                    onProgressChange(currentProgressSec + 1)
                }
            }
        }
    }

    // Find active caption cue
    val activeCue = remember(currentProgressSec, isCaptionsEnabled, selectedCaptionLang, video) {
        if (!isCaptionsEnabled) null else {
            val track = video.captions.firstOrNull { it.languageName.contains(selectedCaptionLang, ignoreCase = true) }
                ?: video.captions.firstOrNull()
            track?.cues?.firstOrNull { currentProgressSec >= it.startSec && currentProgressSec <= it.endSec }?.text
        }
    }

    // Active Chapter
    val currentChapter = remember(currentProgressSec, video.chapters) {
        video.chapters.lastOrNull { currentProgressSec >= it.timestampSec }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(if (isTheatreMode) 21f / 9f else 16f / 9f)
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { showControls = !showControls }
            .testTag("video_player_container")
    ) {
        // Player Surface Canvas (simulated video render buffer)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Render rich video gradient
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(video.thumbnailGradientStart),
                        Color(video.thumbnailGradientEnd),
                        Color.Black
                    ),
                    center = Offset(w / 2, h / 2),
                    radius = w * 0.7f
                )
            )

            // Animated subtle stream raster lines
            val lineY = (System.currentTimeMillis() % 2000L / 2000f) * h
            drawLine(
                color = Color(0x10FFFFFF),
                start = Offset(0f, lineY),
                end = Offset(w, lineY),
                strokeWidth = 2.dp.toPx()
            )
        }

        // Live Synchronized Subtitles / Closed Captions Overlay
        if (!activeCue.isNullOrBlank() && !adPlaying) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (showControls) 64.dp else 24.dp)
                    .padding(horizontal = 24.dp)
                    .background(Color(0xCC000000), RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = activeCue,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Ad Overlay
        if (adPlaying && adCampaign != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Surface(
                        color = AccentAmber,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "AD • 1 of 1",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = adCampaign.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = adCampaign.advertiserName,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Skip Ad Button or countdown
                Button(
                    onClick = {
                        if (adCountdownSec == 0) {
                            adPlaying = false
                        }
                    },
                    enabled = adCountdownSec == 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (adCountdownSec == 0) YTRed else Color(0x88000000)
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    if (adCountdownSec > 0) {
                        Text("Skip in ${adCountdownSec}s", color = Color.White)
                    } else {
                        Text("Skip Ad ⏭", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Stats for Nerds Overlay
        if (showStatsForNerds) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                color = Color(0xDD000000),
                shape = RoundedCornerShape(6.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("STREAM TELEMETRY (HLS/DASH)", color = AccentCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Video ID: ${video.id}", color = Color.White, fontSize = 9.sp)
                    Text("Codec: AV1 Main (av01.0.08M.10) / Opus 48kHz", color = Color.White, fontSize = 9.sp)
                    Text("Resolution: $selectedQuality @ ${selectedSpeed}x", color = Color.White, fontSize = 9.sp)
                    Text("Bitrate: 8,450 Kbps • Buffer Health: 32.4s", color = AccentEmerald, fontSize = 9.sp)
                    Text("Dropped Frames: 0 / 14,280 (0.00%)", color = Color.White, fontSize = 9.sp)
                }
            }
        }

        // Top & Center & Bottom Controls Overlay
        AnimatedVisibility(
            visible = showControls && !adPlaying,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
            ) {
                // Top Control Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current Chapter badge
                    if (currentChapter != null) {
                        Surface(
                            color = Color(0x88000000),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmarks,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentChapter.title,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Closed Captions Toggle
                        IconButton(onClick = { isCaptionsEnabled = !isCaptionsEnabled }) {
                            Icon(
                                imageVector = if (isCaptionsEnabled) Icons.Default.ClosedCaption else Icons.Outlined.ClosedCaptionDisabled,
                                contentDescription = "Captions",
                                tint = if (isCaptionsEnabled) AccentAmber else Color.White
                            )
                        }

                        // Stats for nerds toggle
                        IconButton(onClick = { showStatsForNerds = !showStatsForNerds }) {
                            Icon(
                                imageVector = Icons.Default.QueryStats,
                                contentDescription = "Telemetry",
                                tint = if (showStatsForNerds) AccentCyan else Color.White
                            )
                        }

                        // Settings dialog
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Playback Settings",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Center Play/Pause & Quick Skip
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onProgressChange(maxOf(0, currentProgressSec - 10)) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x55000000), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Replay 10s",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    IconButton(
                        onClick = { onPlayPauseToggle() },
                        modifier = Modifier
                            .size(60.dp)
                            .background(YTRed, CircleShape)
                            .testTag("player_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    IconButton(
                        onClick = { onProgressChange(minOf(video.durationSeconds, currentProgressSec + 10)) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x55000000), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Bottom Timeline & Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Timeline Slider with Chapter indicators
                    if (!video.isLive) {
                        Slider(
                            value = currentProgressSec.toFloat(),
                            onValueChange = { onProgressChange(it.toInt()) },
                            valueRange = 0f..video.durationSeconds.toFloat().coerceAtLeast(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = YTRed,
                                activeTrackColor = YTRed,
                                inactiveTrackColor = Color(0x66FFFFFF)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .testTag("player_timeline_slider")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Timestamp / Live badge
                        if (video.isLive) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(YTRed, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE • ${formatViewCount(video.liveViewerCount.toLong())} viewers",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "${formatDuration(currentProgressSec)} / ${formatDuration(video.durationSeconds)}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Theatre / Fullscreen mode toggle
                        Row {
                            IconButton(onClick = { isTheatreMode = !isTheatreMode }) {
                                Icon(
                                    imageVector = if (isTheatreMode) Icons.Default.FullscreenExit else Icons.Default.AspectRatio,
                                    contentDescription = "Theatre Mode",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Playback Settings Modal Sheet
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = "Player Settings & Renditions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Streaming Quality (Adaptive Bitrate)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Auto", "720p", "1080p 60fps", "4K HDR", "8K").forEach { quality ->
                            FilterChip(
                                selected = selectedQuality == quality,
                                onClick = { selectedQuality = quality },
                                label = { Text(quality, fontSize = 10.sp) }
                            )
                        }
                    }

                    Text("Playback Speed", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            FilterChip(
                                selected = selectedSpeed == speed,
                                onClick = { selectedSpeed = speed },
                                label = { Text("${speed}x", fontSize = 10.sp) }
                            )
                        }
                    }

                    Text("Caption Language", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("English", "Spanish", "Japanese").forEach { lang ->
                            FilterChip(
                                selected = selectedCaptionLang == lang,
                                onClick = { selectedCaptionLang = lang },
                                label = { Text(lang, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Apply", color = YTRed, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
