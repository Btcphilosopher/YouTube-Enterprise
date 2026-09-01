package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Video
import com.example.ui.components.VideoCard
import com.example.ui.components.formatDuration
import com.example.ui.theme.*

@Composable
fun SearchScreen(
    initialQuery: String,
    allVideos: List<Video>,
    onVideoClick: (Video) -> Unit,
    onChannelClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "4K / 8K HDR", "Live", "Has Transcript", "Cloud", "Quantum")

    val searchResults = remember(searchQuery, selectedFilter, allVideos) {
        val q = searchQuery.trim().lowercase()
        allVideos.filter { video ->
            val matchesQuery = if (q.isBlank()) true else {
                video.title.lowercase().contains(q) ||
                video.description.lowercase().contains(q) ||
                video.channelName.lowercase().contains(q) ||
                video.tags.any { it.lowercase().contains(q) } ||
                video.chapters.any { it.title.lowercase().contains(q) } ||
                video.captions.any { track -> track.cues.any { cue -> cue.text.lowercase().contains(q) } }
            }

            val matchesFilter = when (selectedFilter) {
                "4K / 8K HDR" -> video.thumbnailBadge.contains("4K") || video.thumbnailBadge.contains("8K")
                "Live" -> video.isLive
                "Has Transcript" -> video.captions.isNotEmpty()
                "Cloud" -> video.category.contains("Cloud", ignoreCase = true)
                "Quantum" -> video.category.contains("Quantum", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("search_screen")
    ) {
        // Search Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search videos, transcripts, chapters...", fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input_field"),
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )
        }

        // Filter Pills
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { f ->
                FilterChip(
                    selected = selectedFilter == f,
                    onClick = { selectedFilter = f },
                    label = { Text(f, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = YTRed,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Results Count Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${searchResults.size} results found",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Results List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(searchResults) { video ->
                // Check if matched inside transcript
                val q = searchQuery.trim().lowercase()
                val matchedCue = if (q.isNotBlank()) {
                    video.captions.firstOrNull()?.cues?.firstOrNull { it.text.lowercase().contains(q) }
                } else null

                Column {
                    VideoCard(
                        video = video,
                        onClick = { onVideoClick(video) },
                        onChannelClick = onChannelClick
                    )

                    if (matchedCue != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = AccentCyan.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Subtitles, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Matches transcript @ ${formatDuration(matchedCue.startSec)}: \"${matchedCue.text}\"",
                                    fontSize = 11.sp,
                                    color = AccentCyan,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
