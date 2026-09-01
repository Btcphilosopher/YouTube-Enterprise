package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun WatchScreen(
    video: Video,
    allVideos: List<Video>,
    comments: List<Comment>,
    isSubscribed: Boolean,
    onToggleSubscribe: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleWatchLater: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onLikeComment: (String, String) -> Unit,
    onVideoClick: (Video) -> Unit,
    onChannelClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentProgressSec by remember(video.id) { mutableStateOf(0) }
    var isPlaying by remember(video.id) { mutableStateOf(true) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var isTranscriptActive by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("watch_screen_root"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Player Component
        item {
            EnterpriseVideoPlayer(
                video = video,
                currentProgressSec = currentProgressSec,
                onProgressChange = { currentProgressSec = it },
                isPlaying = isPlaying,
                onPlayPauseToggle = { isPlaying = !isPlaying },
                onSeekChapter = { currentProgressSec = it }
            )
        }

        // Video Info Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${formatViewCount(video.viewCount)} views",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text("•", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                    Text(
                        text = formatAge(video.uploadTimestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Text(
                            text = video.category,
                            fontSize = 10.sp,
                            color = AccentBlue,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Channel Info & Subscribe Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AccentBlue)
                            .clickable { onChannelClick(video.channelId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = video.channelName.take(1),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = video.channelName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (video.channelVerified) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(AccentBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Verified",
                                        tint = Color.Black,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "2.84M subscribers",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = { onToggleSubscribe(video.channelId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSubscribed) DarkSurfaceElevated else Color(0xFFF1F1F1),
                            contentColor = if (isSubscribed) TextPrimary else Color(0xFF0F0F0F)
                        ),
                        border = if (isSubscribed) BorderStroke(1.dp, DarkSurfaceBorder) else null,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("subscribe_button")
                    ) {
                        Text(
                            text = if (isSubscribed) "Subscribed 🔔" else "Subscribe",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Pill Bar (Like, Dislike, Share, Save, Transcript)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Like / Dislike joined pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                modifier = Modifier
                                    .clickable { onToggleLike(video.id) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("like_button"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (video.isLikedByCurrentUser) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                                    contentDescription = "Like",
                                    tint = if (video.isLikedByCurrentUser) AccentBlue else TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = formatViewCount(video.likeCount),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(16.dp)
                                    .background(DarkSurfaceBorder)
                            )
                            IconButton(
                                onClick = { /* dislike feedback */ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ThumbDown,
                                    contentDescription = "Dislike",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Save / Watch Later
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (video.isSavedToWatchLater) AccentBlue.copy(alpha = 0.15f) else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (video.isSavedToWatchLater) AccentBlue else DarkSurfaceBorder),
                        modifier = Modifier.clickable { onToggleWatchLater(video.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (video.isSavedToWatchLater) Icons.Default.BookmarkAdded else Icons.Outlined.BookmarkAdd,
                                contentDescription = "Save",
                                tint = if (video.isSavedToWatchLater) AccentBlue else TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Save",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (video.isSavedToWatchLater) AccentBlue else TextPrimary
                            )
                        }
                    }

                    // Transcript Toggle Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isTranscriptActive) AccentCyan.copy(alpha = 0.15f) else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isTranscriptActive) AccentCyan else DarkSurfaceBorder),
                        modifier = Modifier.clickable { isTranscriptActive = !isTranscriptActive }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Subtitles,
                                contentDescription = "Transcript",
                                tint = if (isTranscriptActive) AccentCyan else TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Transcript",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTranscriptActive) AccentCyan else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Expandable Description Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDescriptionExpanded = !isDescriptionExpanded },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = video.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            maxLines = if (isDescriptionExpanded) 100 else 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isDescriptionExpanded) "Show less ▲" else "...more ▼",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )

                        if (isDescriptionExpanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = DarkSurfaceBorder)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tags: ${video.tags.joinToString(", ")}", fontSize = 11.sp, color = TextSecondary)
                            Text("License: ${video.license}", fontSize = 11.sp, color = TextSecondary)
                            Text("Content ID: Cleared & Protected", fontSize = 11.sp, color = AccentEmerald, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Transcript Search Panel (if toggled)
                if (isTranscriptActive && video.captions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchableTranscriptPanel(
                        captions = video.captions,
                        currentProgressSec = currentProgressSec,
                        onCueClick = { currentProgressSec = it }
                    )
                }

                // Chapters List (if available)
                if (video.chapters.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    VideoChaptersList(
                        chapters = video.chapters,
                        currentProgressSec = currentProgressSec,
                        onChapterClick = { currentProgressSec = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Comments Section
                ThreadedCommentsSection(
                    videoId = video.id,
                    comments = comments,
                    onAddComment = { text -> onAddComment(video.id, text) },
                    onLikeComment = { commentId -> onLikeComment(video.id, commentId) }
                )
            }
        }

        // Up Next Section Header
        item {
            Text(
                text = "Up Next & Enterprise Recommendations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Recommended Videos
        val recommendations = allVideos.filterNot { it.id == video.id }
        items(recommendations) { rec ->
            VideoCard(
                video = rec,
                onClick = { onVideoClick(rec) },
                onChannelClick = onChannelClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}
