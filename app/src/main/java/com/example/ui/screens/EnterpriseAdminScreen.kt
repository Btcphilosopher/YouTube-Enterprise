package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.formatViewCount
import com.example.ui.theme.*

@Composable
fun EnterpriseAdminScreen(
    systemHealth: SystemHealthOverview,
    moderationCases: List<ModerationCase>,
    rightsAssets: List<RightsAssetRecord>,
    adCampaigns: List<AdCampaign>,
    auditLogs: List<AuditLogEntry>,
    onResolveModeration: (String, ModerationStatus, String) -> Unit,
    onDisputeRights: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("OBSERVABILITY", "MODERATION", "CONTENT ID", "ADS & AUCTION", "AUDIT LOGS")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("enterprise_admin_screen")
    ) {
        // Console Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = AccentAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enterprise Operations Center",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Global Infrastructure, Rights Management & Policy Enforcement",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Operational Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceElevated,
            contentColor = Color.White,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = if (selectedTab == index) YTRed else TextSecondary
                        )
                    }
                )
            }
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            when (selectedTab) {
                0 -> { // OBSERVABILITY
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminKpiCard("Active Viewers", "${formatViewCount(systemHealth.activeViewersTotal.toLong())}", AccentEmerald, Modifier.weight(1f))
                            AdminKpiCard("Active Live Streams", "${systemHealth.activeLiveStreams}", YTRed, Modifier.weight(1f))
                            AdminKpiCard("CDN Edge Hit", "${systemHealth.globalCdnEdgeHitRatio}%", AccentCyan, Modifier.weight(1f))
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminKpiCard("Transcode Queue", "${systemHealth.transcodingQueueDepth} jobs", AccentAmber, Modifier.weight(1f))
                            AdminKpiCard("Storage Stored", "${systemHealth.originStorageUsedTb} TB", AccentIndigo, Modifier.weight(1f))
                            AdminKpiCard("Egress Bandwidth", "${systemHealth.egressBandwidthGbps} Gbps", Color.White, Modifier.weight(1f))
                        }
                    }

                    item {
                        Text(
                            text = "Distributed Cluster Services & Telemetry",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(systemHealth.clusterServices) { svc ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(svc.serviceName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "p95 Latency: ${svc.p95LatencyMs}ms • Error rate: ${svc.errorRatePercent}% • Replicas: ${svc.activeReplicas}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Surface(
                                    color = AccentEmerald.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = svc.status,
                                        color = AccentEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> { // MODERATION
                    item {
                        Text(
                            text = "Flagged Content & AI Classifier Queue (${moderationCases.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(moderationCases) { c ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(c.contentTitle, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                                    Surface(
                                        color = if (c.status == ModerationStatus.CLEAR) AccentEmerald.copy(alpha = 0.2f) else YTRed.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = c.status.name,
                                            color = if (c.status == ModerationStatus.CLEAR) AccentEmerald else YTRed,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Reason: ${c.flaggedReason}", fontSize = 11.sp, color = TextSecondary)
                                Text("Risk: ${c.riskClassification} • ML Confidence: ${(c.autoMlConfidence * 100).toInt()}%", fontSize = 11.sp, color = AccentAmber)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onResolveModeration(c.id, ModerationStatus.CLEAR, "Moderator validated content meets guidelines") },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Approve / Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { onResolveModeration(c.id, ModerationStatus.REMOVED, "Removed due to enterprise security policy violation") },
                                        colors = ButtonDefaults.buttonColors(containerColor = YTRed),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Remove Content", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> { // CONTENT ID & RIGHTS
                    item {
                        Text(
                            text = "Reference Fingerprint Assets & Audio/Video Match Engine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(rightsAssets) { asset ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(asset.assetTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Rights Holder: ${asset.rightsHolder} (${asset.territory})", fontSize = 11.sp, color = TextSecondary)
                                Text("License: ${asset.licenseType}", fontSize = 11.sp, color = AccentCyan)
                                Text("Fingerprint: ${asset.fingerprintHash}", fontSize = 10.sp, color = TextTertiary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Status: ${asset.matchStatus}", fontSize = 11.sp, color = AccentEmerald, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = { onDisputeRights(asset.id, "Fair use research citation exception") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Test Dispute / Resolution Protocol", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                3 -> { // ADS & CAMPAIGNS
                    item {
                        Text(
                            text = "Enterprise Ad Marketplace & Real-Time Auction Engine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(adCampaigns) { camp ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(camp.advertiserName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Surface(color = AccentIndigo.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text(camp.format.name, color = AccentIndigo, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                                Text(camp.campaignName, fontSize = 12.sp, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Bid CPM: $${camp.bidCpmUsd} • Total Spend: $${camp.totalSpendUsd} • Impr: ${formatViewCount(camp.impressionsServed.toLong())}",
                                    fontSize = 11.sp,
                                    color = AccentAmber
                                )
                                Text(
                                    text = "Target Categories: ${camp.targetCategories.joinToString(", ")}",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                4 -> { // AUDIT LOGS
                    item {
                        Text(
                            text = "Immutable Enterprise Audit Trail",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(auditLogs) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(AccentIndigo, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(log.action, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AccentCyan)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("by ${log.actor}", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Text(log.details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Resource: ${log.resource} • IP: ${log.ipAddress}", fontSize = 9.sp, color = TextTertiary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminKpiCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
        }
    }
}
