package com.example.ui.screens

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DesktopMac
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.VideoTileWebView
import com.example.viewmodel.MainViewModel

@Composable
fun BrowserScreen(
    viewModel: MainViewModel,
    onNavigateToMultiGrid: () -> Unit
) {
    val context = LocalContext.current
    val browserConfig by viewModel.browserConfig.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showRateDialog by remember { mutableStateOf(false) }
    var showMoreAppsDialog by remember { mutableStateOf(false) }
    var showPreviewModal by remember { mutableStateOf(false) }
    var ratingStars by remember { mutableStateOf(5) }

    val reloadIntervals = listOf(0 to "Off", 30 to "30s", 60 to "60s", 120 to "2m", 300 to "5m")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F5))
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Welcome ticker from screenshot
            Text(
                text = "Welcome to our app. If you like this app, please share & rate 5 stars! ⭐️",
                fontSize = 12.sp,
                color = Color(0xFF424242),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        // RED HERO CARD (exact visual match to screenshot)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE50914)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF1E88E5), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo box
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_logo),
                            contentDescription = "App Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Red header title
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Multi\nWatchtime And Views\nBrowser Pro",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "By ${userProfile.name}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFFEBEE)
                        )
                    }
                }
            }
        }

        // VIDEO LINK INPUT with Clipboard paste icon
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFF1E88E5), RoundedCornerShape(10.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    OutlinedTextField(
                        value = browserConfig.rawInputUrl,
                        onValueChange = { viewModel.setInputUrl(it) },
                        placeholder = {
                            Text(
                                "Enter video link here...",
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    if (browserConfig.rawInputUrl.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setInputUrl("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }

                    // Paste from clipboard button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = clipboard.primaryClip
                            if (clipData != null && clipData.itemCount > 0) {
                                val pasted = clipData.getItemAt(0).text?.toString() ?: ""
                                if (pasted.isNotEmpty()) {
                                    viewModel.setInputUrl(pasted)
                                    viewModel.applyUrl(pasted)
                                    Toast.makeText(context, "Link pasted!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste from clipboard",
                            tint = Color(0xFF212121),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // ACTION BUTTONS: Rate App, More App, Preview (From screenshot!)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Rate App (Black button)
                Button(
                    onClick = { showRateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text(
                        text = "Rate App",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // More App (Black button)
                Button(
                    onClick = { showMoreAppsDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text(
                        text = "More App",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Preview (Yellow / Amber button)
                Button(
                    onClick = {
                        viewModel.applyUrl(browserConfig.rawInputUrl)
                        showPreviewModal = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text(
                        text = "Preview",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // VIEW COUNT BUTTONS GRID (6 VIEWS, 10 VIEWS, 15 VIEWS, 21 VIEWS) from screenshot!
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ViewCountButton(
                        count = 6,
                        isSelected = browserConfig.viewCount == 6,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.applyUrl(browserConfig.rawInputUrl)
                            viewModel.setViewCount(6)
                            onNavigateToMultiGrid()
                        }
                    )
                    ViewCountButton(
                        count = 10,
                        isSelected = browserConfig.viewCount == 10,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.applyUrl(browserConfig.rawInputUrl)
                            viewModel.setViewCount(10)
                            onNavigateToMultiGrid()
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ViewCountButton(
                        count = 15,
                        isSelected = browserConfig.viewCount == 15,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.applyUrl(browserConfig.rawInputUrl)
                            viewModel.setViewCount(15)
                            onNavigateToMultiGrid()
                        }
                    )
                    ViewCountButton(
                        count = 21,
                        isSelected = browserConfig.viewCount == 21,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.applyUrl(browserConfig.rawInputUrl)
                            viewModel.setViewCount(21)
                            onNavigateToMultiGrid()
                        }
                    )
                }
            }
        }

        // CONTROLS & TIMERS CARD
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFFE50914)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Auto-Reload Interval",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        reloadIntervals.forEach { (sec, label) ->
                            FilterChip(
                                selected = browserConfig.autoReloadIntervalSeconds == sec,
                                onClick = { viewModel.setAutoReloadInterval(sec) },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (browserConfig.isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = Color(0xFF00B4D8)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Mute All Video Tabs", fontSize = 13.sp)
                        }
                        Switch(
                            checked = browserConfig.isMuted,
                            onCheckedChange = { viewModel.toggleMute(it) }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (browserConfig.isDesktopMode) Icons.Default.DesktopMac else Icons.Default.Laptop,
                                contentDescription = null,
                                tint = Color(0xFF757575)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Desktop User-Agent", fontSize = 13.sp)
                        }
                        Switch(
                            checked = browserConfig.isDesktopMode,
                            onCheckedChange = { viewModel.toggleDesktopMode(it) }
                        )
                    }
                }
            }
        }

        // LIVE WATCHTIME ANALYTICS BANNER
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E26)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatBadge(
                        label = "Active Views",
                        value = "${browserConfig.viewCount} screens",
                        icon = Icons.Default.Visibility,
                        color = Color(0xFF00B4D8)
                    )
                    StatBadge(
                        label = "Session Time",
                        value = formatElapsed(browserConfig.elapsedSeconds),
                        icon = Icons.Default.Timer,
                        color = Color(0xFFFFB703)
                    )
                    StatBadge(
                        label = "Total Boosted",
                        value = "${userProfile.totalWatchSeconds / 60} mins",
                        icon = Icons.Default.PlayArrow,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }

        // SAMPLE TEST LINKS
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Quick Presets / History:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF616161)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    browserConfig.historyUrls.forEach { histUrl ->
                        Text(
                            text = histUrl,
                            fontSize = 12.sp,
                            color = Color(0xFF1976D2),
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setInputUrl(histUrl)
                                    viewModel.applyUrl(histUrl)
                                    Toast
                                        .makeText(context, "Loaded URL", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Rate App Dialog
    if (showRateDialog) {
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            title = { Text("Rate Multi WatchTime Pro") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("How would you rate your watchtime experience?")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        for (i in 1..5) {
                            IconButton(onClick = { ratingStars = i }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$i stars",
                                    tint = if (i <= ratingStars) Color(0xFFFFB300) else Color.LightGray
                                )
                            }
                        }
                    }
                    Text(
                        text = "Developed by Prinsh Kumar",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showRateDialog = false
                    Toast.makeText(context, "Thank you for the $ratingStars star rating!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // More Apps Dialog
    if (showMoreAppsDialog) {
        AlertDialog(
            onDismissRequest = { showMoreAppsDialog = false },
            title = { Text("More Apps by Prinsh Kumar") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🚀 Speed Views Browser Max\nDual engine video acceleration with proxy rotator.")
                    Text("⚡ YouTube Analytics & Tag Explorer\nReal-time keywords & video SEO optimizer.")
                    Text("🎙️ AI Voice Over & Studio Script\nPowered by Gemini TTS & Veo Video generation.")
                }
            },
            confirmButton = {
                Button(onClick = { showMoreAppsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Preview Modal Dialog
    if (showPreviewModal) {
        AlertDialog(
            onDismissRequest = { showPreviewModal = false },
            title = { Text("Single Viewport Preview") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    VideoTileWebView(
                        tileIndex = 0,
                        url = browserConfig.url,
                        isMuted = browserConfig.isMuted,
                        isDesktopMode = browserConfig.isDesktopMode,
                        reloadKey = browserConfig.reloadTriggerTimestamp,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showPreviewModal = false
                    onNavigateToMultiGrid()
                }) {
                    Text("Open in Multi-Grid")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPreviewModal = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun ViewCountButton(
    count: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF0077B6) else Color(0xFF00B4D8)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.height(54.dp)
    ) {
        Text(
            text = "$count VIEWS",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun StatBadge(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Text(
            text = label,
            color = Color(0xFFA0A0B0),
            fontSize = 10.sp
        )
    }
}

private fun formatElapsed(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
