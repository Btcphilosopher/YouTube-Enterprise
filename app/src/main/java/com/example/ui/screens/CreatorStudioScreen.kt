package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun CreatorStudioScreen(
    videos: List<Video>,
    analytics: CreatorAnalytics,
    retentionPoints: List<AudienceRetentionPoint>,
    selectedTimeRange: String,
    onTimeRangeChange: (String) -> Unit,
    onOpenUploadWorkspace: () -> Unit,
    onVideoClick: (Video) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("creator_studio_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Creator Studio Enterprise",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Channel Analytics, Audience Retention & Transcode Control",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onOpenUploadWorkspace,
                    colors = ButtonDefaults.buttonColors(containerColor = YTRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("studio_upload_button")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload Master", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Time Range Filter Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("24 HOURS", "7 DAYS", "28 DAYS", "90 DAYS").forEach { range ->
                    FilterChip(
                        selected = selectedTimeRange == range,
                        onClick = { onTimeRangeChange(range) },
                        label = { Text(range, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = YTRed,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Metric KPI Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudioMetricCard(
                        title = "Views ($selectedTimeRange)",
                        value = formatViewCount(analytics.views28d.toLong()),
                        changePercent = analytics.viewsChangePercent,
                        icon = Icons.Default.Visibility,
                        modifier = Modifier.weight(1f)
                    )
                    StudioMetricCard(
                        title = "Watch Time (Hours)",
                        value = String.format("%.1fK", analytics.watchTimeHours28d / 1000.0),
                        changePercent = analytics.watchTimeChangePercent,
                        icon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudioMetricCard(
                        title = "Subscribers Gained",
                        value = "+${formatViewCount(analytics.subscribers28d.toLong())}",
                        changePercent = analytics.subscribersChangePercent,
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                    StudioMetricCard(
                        title = "Est. Revenue",
                        value = String.format("$%.2f", analytics.revenueUsd28d),
                        changePercent = analytics.revenueChangePercent,
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Audience Retention Chart
        item {
            AudienceRetentionGraph(points = retentionPoints)
        }

        // Channel Video Library & Content Management Table
        item {
            Text(
                text = "Published Media Assets & Pipeline Status (${videos.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(videos) { video ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVideoClick(video) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(video.thumbnailGradientStart)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatDuration(video.durationSeconds),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = DarkSurfaceElevated,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = video.visibility.name,
                                    fontSize = 9.sp,
                                    color = AccentIndigo,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Text("${formatViewCount(video.viewCount)} views", fontSize = 11.sp, color = TextSecondary)
                            Text("•", fontSize = 11.sp, color = TextSecondary)
                            Text("${formatViewCount(video.likeCount)} likes", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    // Content ID badge
                    Surface(
                        color = AccentEmerald.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Protected", color = AccentEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioMetricCard(
    title: String,
    value: String,
    changePercent: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Icon(icon, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = AccentEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "+${changePercent}% vs typical",
                    color = AccentEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
