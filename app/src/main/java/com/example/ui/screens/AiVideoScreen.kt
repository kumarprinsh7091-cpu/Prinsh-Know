package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.viewmodel.MainViewModel

@Composable
fun AiVideoScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val veoState by viewModel.veoState.collectAsState()
    val aspectRatios = listOf("16:9", "9:16")

    val samplePrompts = listOf(
        "Cinematic slow motion pan across sports motorcycle, engine exhaust heat haze, sunlight filtering through lush forest trees",
        "Rider accelerating along vibrant neon cyber road at dusk, cinematic shallow depth of field, 4k 60fps",
        "Dynamic camera drone flyover of highway motorcycle cruise, gentle camera sway and cinematic breeze"
    )

    // Animated zoom transition for simulated video playback
    val transition = rememberInfiniteTransition(label = "videoPan")
    val scaleAnim by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
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
                            .background(Color(0xFFE50914).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = Color(0xFFE50914),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Veo 3.1 Video Animator",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Model: veo-3.1-fast-generate-preview",
                            fontSize = 11.sp,
                            color = Color(0xFF00B4D8)
                        )
                    }
                }
            }
        }

        // PHOTO SELECTION & PREVIEW CANVAS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171720)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Source Image (Motorcycle Portrait):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val boxHeight = if (veoState.aspectRatio == "16:9") 200.dp else 280.dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(boxHeight)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                            .border(1.dp, Color(0xFF00B4D8).copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_prinsh_profile),
                            contentDescription = "Source Photo for Video",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (veoState.isVideoReady) Modifier.scale(scaleAnim) else Modifier
                                )
                        )

                        if (veoState.isVideoReady) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Veo Motion Preview Playing",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        if (veoState.isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.65f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = Color(0xFFE50914))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Generating Veo Video (${veoState.progressPercent}%)...",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ASPECT RATIO SELECTOR
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171720)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Aspect Ratio (Mandatory 16:9 or 9:16):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        aspectRatios.forEach { ratio ->
                            FilterChip(
                                selected = veoState.aspectRatio == ratio,
                                onClick = { viewModel.updateVeoAspectRatio(ratio) },
                                label = {
                                    Text(
                                        text = if (ratio == "16:9") "16:9 Landscape" else "9:16 Portrait",
                                        fontWeight = if (veoState.aspectRatio == ratio) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // PROMPT INPUT & PRESETS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171720)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Motion / Animation Prompt:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = veoState.prompt,
                        onValueChange = { viewModel.updateVeoPrompt(it) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Prompt Suggestions:",
                        fontSize = 11.sp,
                        color = Color(0xFFA0A0B0)
                    )
                    samplePrompts.forEach { p ->
                        Text(
                            text = "• $p",
                            fontSize = 11.sp,
                            color = Color(0xFF00B4D8),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateVeoPrompt(p) }
                                .padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // GENERATE BUTTON
        item {
            Button(
                onClick = {
                    viewModel.generateVeoVideo()
                    Toast.makeText(context, "Animating photo with Veo 3.1...", Toast.LENGTH_SHORT).show()
                },
                enabled = !veoState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (veoState.isLoading) "Processing..." else "Generate Video with Veo 3.1",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            if (veoState.statusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = veoState.statusMessage,
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
