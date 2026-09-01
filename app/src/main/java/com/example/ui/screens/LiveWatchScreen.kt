package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun LiveWatchScreen(
    video: Video,
    liveDetails: LiveStreamDetails?,
    chatMessages: List<LiveChatMessage>,
    onSendMessage: (String, String?) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTelemetryDetails by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("live_watch_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Live Player
        item {
            EnterpriseVideoPlayer(
                video = video,
                currentProgressSec = 0,
                onProgressChange = {},
                isPlaying = true,
                onPlayPauseToggle = {}
            )
        }

        // Real-Time Ingest Telemetry Ribbon
        if (liveDetails != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { showTelemetryDetails = !showTelemetryDetails },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AccentEmerald, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HEALTH: ${liveDetails.health.name}",
                                color = AccentEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${liveDetails.currentBitrateKbps} Kbps • ${liveDetails.fps} FPS • ${liveDetails.latencyMs}ms glass-to-glass",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Icon(
                            imageVector = if (showTelemetryDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (showTelemetryDetails) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                            HorizontalDivider(color = DarkSurfaceBorder)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Ingest Protocol: SRT / CMAF Low Latency", fontSize = 10.sp, color = Color.White)
                            Text("Stream Key: ${liveDetails.streamKey}", fontSize = 10.sp, color = TextSecondary)
                            Text("Dropped Frames Rate: ${(liveDetails.droppedFramesRate * 100)}%", fontSize = 10.sp, color = AccentEmerald)
                            Text("Origin Transcoder CPU: ${liveDetails.cpuUsagePercent}%", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Stream Header & Channel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(YTRed)
                            .clickable { onChannelClick(video.channelId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(video.channelName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(video.channelName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("${formatViewCount(video.liveViewerCount.toLong())} live viewers", fontSize = 11.sp, color = YTRed, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = YTRed),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Subscribed 🔔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Moderated Live Chat Box
                ModeratedLiveChatBox(
                    streamId = liveDetails?.streamId ?: "default_stream",
                    messages = chatMessages,
                    onSendMessage = { text, sc -> onSendMessage(text, sc) },
                    onDeleteMessage = onDeleteMessage
                )
            }
        }
    }
}
