package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MainViewModel

@Composable
fun TtsAnnouncerScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val ttsState by viewModel.ttsState.collectAsState()
    val browserConfig by viewModel.browserConfig.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val voices = listOf("Kore", "Puck", "Fenrir", "Aoede")

    val templates = listOf(
        "Watchtime Status" to "Attention. Session is running with ${browserConfig.viewCount} active viewports. Total accumulated watch time is ${userProfile.totalWatchSeconds / 60} minutes. Keep up the great work!",
        "Creator Affirmation" to "Hello ${userProfile.name}! Consistent creation leads to incredible audience growth. Your watch hours are climbing every single minute.",
        "Pro Tips" to "Pro tip: Connect your Android device to high-speed fiber internet and keep your screen awake to maximize YouTube watch hours."
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E26)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF00B4D8).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color(0xFF00B4D8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Voice Watchtime Coach",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Model: gemini-3.8-flash-tts",
                            fontSize = 11.sp,
                            color = Color(0xFFFFB703)
                        )
                    }
                }
            }
        }

        // VOICE SELECTOR
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171720)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Select AI Voice:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        voices.forEach { v ->
                            FilterChip(
                                selected = ttsState.selectedVoice == v,
                                onClick = { viewModel.updateTtsVoice(v) },
                                label = { Text(v, fontWeight = if (ttsState.selectedVoice == v) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                }
            }
        }

        // TEXT INPUT & PRESETS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171720)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Speech Text to Convert:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ttsState.promptText,
                        onValueChange = { viewModel.updateTtsPrompt(it) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Quick Presets:",
                        fontSize = 11.sp,
                        color = Color(0xFFA0A0B0)
                    )
                    templates.forEach { (name, txt) ->
                        Text(
                            text = "📢 $name",
                            fontSize = 12.sp,
                            color = Color(0xFF00B4D8),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateTtsPrompt(txt) }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // PLAYBACK CONTROLS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.generateAndPlaySpeech()
                        Toast.makeText(context, "Speaking via gemini-3.8-flash-tts...", Toast.LENGTH_SHORT).show()
                    },
                    enabled = !ttsState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    if (ttsState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesizing...", color = Color.White)
                    } else {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Speak with TTS", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                if (ttsState.isPlaying) {
                    Button(
                        onClick = { viewModel.stopAudio() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A38)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop", tint = Color.White)
                    }
                }
            }

            if (ttsState.statusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ttsState.statusMessage,
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
