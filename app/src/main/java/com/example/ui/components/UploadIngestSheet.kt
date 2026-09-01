package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun UploadIngestWorkspace(
    uploadJob: UploadPipelineJob?,
    onStartUpload: (String) -> Unit,
    onPublish: (String, String, String, VideoVisibility, List<String>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Cloud Architecture") }
    var visibility by remember { mutableStateOf(VideoVisibility.PUBLIC) }
    var tagsInput by remember { mutableStateOf("Distributed Systems, AV1, Enterprise") }
    var selectedThumbnailIdx by remember { mutableStateOf(0) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = YTRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Enterprise Ingest & Transcode Pipeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uploadJob == null || uploadJob.stage == UploadStage.IDLE) {
                // Initial File Drop Picker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBg)
                        .border(1.dp, Brush.linearGradient(listOf(YTRed, AccentIndigo)), RoundedCornerShape(12.dp))
                        .clickable {
                            onStartUpload("enterprise_keynote_4k_hdr_master.mov")
                        }
                        .testTag("upload_dropzone_area"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = YTRed,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Select Enterprise Media Asset",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Supports MP4, MOV (ProRes), WebM, MKV up to 8K HDR",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onStartUpload("enterprise_keynote_4k_hdr_master.mov") },
                            colors = ButtonDefaults.buttonColors(containerColor = YTRed),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Browse Master Files", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Active 9-Stage Progress Pipeline
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uploadJob.fileName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${uploadJob.overallProgressPercent}%",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = YTRed
                        )
                    }

                    LinearProgressIndicator(
                        progress = { uploadJob.overallProgressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = YTRed,
                        trackColor = DarkSurfaceBorder
                    )

                    // Pipeline Stage Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val stages = listOf(
                            "1. Validate" to (uploadJob.overallProgressPercent >= 10),
                            "2. Security" to (uploadJob.overallProgressPercent >= 20),
                            "3. Codec Analysis" to (uploadJob.overallProgressPercent >= 35),
                            "4. Transcode Multi-ladder" to (uploadJob.overallProgressPercent >= 60),
                            "5. Thumbnails" to (uploadJob.overallProgressPercent >= 75),
                            "6. Captions STT" to (uploadJob.overallProgressPercent >= 88),
                            "7. Content ID" to (uploadJob.overallProgressPercent >= 95),
                            "8. Ready" to (uploadJob.overallProgressPercent == 100)
                        )
                        items(stages) { (name, completed) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (completed) AccentEmerald.copy(alpha = 0.2f) else DarkSurfaceBorder
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (completed) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = name,
                                        fontSize = 10.sp,
                                        color = if (completed) AccentEmerald else TextSecondary,
                                        fontWeight = if (completed) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Terminal Logs Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .background(DarkBg, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn {
                            items(uploadJob.logs) { log ->
                                Text(
                                    text = log,
                                    color = AccentCyan,
                                    fontSize = 10.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Thumbnail Selection Engine
                    if (uploadJob.thumbnailOptions.isNotEmpty()) {
                        Text("Select Video Thumbnail Variant", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uploadJob.thumbnailOptions.forEachIndexed { idx, opt ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .clickable { selectedThumbnailIdx = idx },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (selectedThumbnailIdx == idx) YTRed.copy(alpha = 0.3f) else DarkSurfaceElevated,
                                    border = if (selectedThumbnailIdx == idx) androidx.compose.foundation.BorderStroke(1.dp, YTRed) else null
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(opt, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Metadata Editing Form
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Video Title") },
                        placeholder = { Text("e.g. Distributed Video Architecture") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_title_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Video Description & Chapters") },
                        placeholder = { Text("Enter detailed synopsis...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    // Visibility Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            VideoVisibility.PUBLIC,
                            VideoVisibility.ORGANISATION_ONLY,
                            VideoVisibility.UNLISTED,
                            VideoVisibility.PRIVATE
                        ).forEach { vis ->
                            FilterChip(
                                selected = visibility == vis,
                                onClick = { visibility = vis },
                                label = { Text(vis.name.replace("_", " "), fontSize = 10.sp) }
                            )
                        }
                    }

                    // Publish Button
                    Button(
                        onClick = {
                            val finalTitle = title.ifBlank { uploadJob.fileName }
                            onPublish(
                                finalTitle,
                                description,
                                category,
                                visibility,
                                tagsInput.split(",").map { it.trim() }
                            )
                        },
                        enabled = uploadJob.overallProgressPercent >= 90,
                        colors = ButtonDefaults.buttonColors(containerColor = YTRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("publish_video_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uploadJob.overallProgressPercent == 100) "Publish to Platform 🚀" else "Processing Assets (${uploadJob.overallProgressPercent}%)...",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
