package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import com.example.ui.theme.*

// ==========================================
// CHAPTERS & SEARCHABLE TRANSCRIPTS
// ==========================================
@Composable
fun VideoChaptersList(
    chapters: List<VideoChapter>,
    currentProgressSec: Int,
    onChapterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmarks,
                    contentDescription = null,
                    tint = AccentAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Video Chapters (${chapters.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                chapters.forEach { chapter ->
                    val isActive = currentProgressSec >= chapter.timestampSec
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) DarkSurfaceElevated else Color.Transparent)
                            .clickable { onChapterClick(chapter.timestampSec) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isActive) YTRed else DarkSurfaceBorder
                        ) {
                            Text(
                                text = formatDuration(chapter.timestampSec),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) MaterialTheme.colorScheme.onSurface else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (chapter.description.isNotBlank()) {
                                Text(
                                    text = chapter.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchableTranscriptPanel(
    captions: List<CaptionTrack>,
    currentProgressSec: Int,
    onCueClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val activeTrack = captions.firstOrNull() ?: return

    val filteredCues = remember(searchQuery, activeTrack) {
        if (searchQuery.isBlank()) activeTrack.cues else {
            activeTrack.cues.filter { it.text.contains(searchQuery, ignoreCase = true) }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Searchable Transcript",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    color = DarkSurfaceBorder,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = activeTrack.languageName,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search input inside transcript
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search transcript keywords (e.g., 'AV1', 'latency')...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filteredCues.forEach { cue ->
                    val isCurrent = currentProgressSec in cue.startSec..cue.endSec
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurrent) Color(0x33FF0033) else Color.Transparent)
                            .clickable { onCueClick(cue.startSec) }
                            .padding(6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = formatDuration(cue.startSec),
                            color = AccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(42.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = cue.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// COMMENTS SECTION
// ==========================================
@Composable
fun ThreadedCommentsSection(
    videoId: String,
    comments: List<Comment>,
    onAddComment: (String) -> Unit,
    onLikeComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newCommentText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comments (${comments.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Top comments", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Comment input box
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AccentIndigo),
                contentAlignment = Alignment.Center
            ) {
                Text("U", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                placeholder = { Text("Add an enterprise comment...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("comment_input_field"),
                trailingIcon = {
                    if (newCommentText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onAddComment(newCommentText)
                                newCommentText = ""
                            },
                            modifier = Modifier.testTag("submit_comment_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = YTRed)
                        }
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Comments list
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            comments.forEach { comment ->
                CommentItemRow(
                    comment = comment,
                    onLike = { onLikeComment(comment.id) }
                )
            }
        }
    }
}

@Composable
private fun CommentItemRow(
    comment: Comment,
    onLike: () -> Unit
) {
    var isRepliesExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comment.userName.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.userName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = comment.timestampAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }

                if (comment.isPinned) {
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = AccentAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pinned by creator",
                            fontSize = 10.sp,
                            color = AccentAmber,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Actions: Like, Reply, Creator Heart
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLike, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (comment.isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = "Like",
                            tint = if (comment.isLiked) YTRed else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (comment.likeCount > 0) {
                        Text(
                            text = comment.likeCount.toString(),
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (comment.isCreatorHearted) {
                        Surface(
                            color = Color(0x33FF0033),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Creator Heart",
                                tint = YTRed,
                                modifier = Modifier
                                    .padding(3.dp)
                                    .size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    if (comment.replies.isNotEmpty()) {
                        TextButton(
                            onClick = { isRepliesExpanded = !isRepliesExpanded },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text(
                                text = if (isRepliesExpanded) "Hide replies" else "${comment.replies.size} replies",
                                color = AccentIndigo,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Expanded Replies
                if (isRepliesExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        comment.replies.forEach { reply ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkSurfaceElevated, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(YTRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(reply.userName.take(1), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(reply.userName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        if (reply.isCreator) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Surface(color = YTRed, shape = RoundedCornerShape(2.dp)) {
                                                Text("CREATOR", color = Color.White, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 3.dp))
                                            }
                                        }
                                    }
                                    Text(reply.text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// MODERATED LIVE CHAT
// ==========================================
@Composable
fun ModeratedLiveChatBox(
    streamId: String,
    messages: List<LiveChatMessage>,
    onSendMessage: (String, String?) -> Unit,
    onDeleteMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    var showSuperChatDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(YTRed, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Chat & Q&A",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    color = DarkSurfaceBorder,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Moderation Active",
                        fontSize = 10.sp,
                        color = AccentEmerald,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chat stream list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    if (msg.isSuperChat) {
                        // Super Chat Highlight Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(msg.superChatTierColor ?: 0xFFE11D48)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(msg.userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text(msg.superChatAmount ?: "$10.00", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                                Text(msg.text, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (msg.isModerator) {
                                Surface(color = AccentIndigo, shape = RoundedCornerShape(2.dp)) {
                                    Text("MOD", color = Color.White, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = "${msg.userName}: ",
                                fontWeight = FontWeight.Bold,
                                color = if (msg.isModerator) AccentIndigo else TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = msg.text,
                                color = if (msg.isDeleted) TextTertiary else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Send chat bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showSuperChatDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = "SuperChat", tint = AccentAmber)
                }

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Chat publicly...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    trailingIcon = {
                        if (messageText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    onSendMessage(messageText, null)
                                    messageText = ""
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = YTRed)
                            }
                        }
                    }
                )
            }
        }
    }

    if (showSuperChatDialog) {
        AlertDialog(
            onDismissRequest = { showSuperChatDialog = false },
            title = { Text("Send Enterprise SuperChat") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Support the creator and pin your question in high priority stream queue.")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("$5.00", "$20.00", "$50.00", "$100.00").forEach { amount ->
                            Button(
                                onClick = {
                                    onSendMessage("Enterprise Broadcast Priority Question: $amount", amount)
                                    showSuperChatDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentRose)
                            ) {
                                Text(amount, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSuperChatDialog = false }) { Text("Cancel") }
            }
        )
    }
}
