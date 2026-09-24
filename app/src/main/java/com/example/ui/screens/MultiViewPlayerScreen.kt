package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VideoTileWebView
import com.example.viewmodel.MainViewModel

@Composable
fun MultiViewPlayerScreen(
    viewModel: MainViewModel,
    onBackToBrowser: () -> Unit
) {
    val browserConfig by viewModel.browserConfig.collectAsState()
    val viewCounts = listOf(6, 10, 15, 21)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121217))
    ) {
        // Multi-View Control Header
        Surface(
            color = Color(0xFF1E1E26),
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (browserConfig.isSessionActive) Color(0xFF10B981) else Color.Gray,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (browserConfig.isSessionActive) "LIVE BOOSTING" else "PAUSED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Running ${browserConfig.viewCount} Views",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "Timer: ${formatTime(browserConfig.elapsedSeconds)} | Auto-Reload: ${if (browserConfig.autoReloadIntervalSeconds > 0) "${browserConfig.autoReloadIntervalSeconds}s" else "Off"}",
                            color = Color(0xFFA0A0B0),
                            fontSize = 11.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Play/Pause Session
                        IconButton(
                            onClick = { viewModel.toggleSession(!browserConfig.isSessionActive) }
                        ) {
                            Icon(
                                imageVector = if (browserConfig.isSessionActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Toggle session",
                                tint = if (browserConfig.isSessionActive) Color(0xFFFFB703) else Color(0xFF10B981)
                            )
                        }

                        // Mute/Unmute
                        IconButton(
                            onClick = { viewModel.toggleMute(!browserConfig.isMuted) }
                        ) {
                            Icon(
                                imageVector = if (browserConfig.isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Toggle Audio",
                                tint = Color(0xFF00B4D8)
                            )
                        }

                        // Reload All
                        IconButton(
                            onClick = { viewModel.reloadAllViews() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reload all",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Quick View Count Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Views:",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    viewCounts.forEach { count ->
                        FilterChip(
                            selected = browserConfig.viewCount == count,
                            onClick = { viewModel.setViewCount(count) },
                            label = { Text("$count", fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // WebViews Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(browserConfig.viewCount) { index ->
                VideoTileWebView(
                    tileIndex = index,
                    url = browserConfig.url,
                    isMuted = browserConfig.isMuted,
                    isDesktopMode = browserConfig.isDesktopMode,
                    reloadKey = browserConfig.reloadTriggerTimestamp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                )
            }
        }

        // Bottom status footer
        Surface(
            color = Color(0xFF141419),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tip: Keep screen on & connect to WiFi for best watchtime boost.",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
                Button(
                    onClick = onBackToBrowser,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Settings", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
