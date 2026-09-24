package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoTileWebView(
    tileIndex: Int,
    url: String,
    isMuted: Boolean,
    isDesktopMode: Boolean,
    reloadKey: Long,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Transform URL to clean embed URL if it's YouTube
    val processedUrl = remember(url, isMuted) {
        formatVideoUrl(url, isMuted)
    }

    LaunchedEffect(reloadKey) {
        if (reloadKey > 0) {
            webViewRef?.reload()
        }
    }

    LaunchedEffect(isMuted) {
        val muteJs = if (isMuted) {
            "document.querySelectorAll('video, audio').forEach(el => { el.muted = true; el.volume = 0; });"
        } else {
            "document.querySelectorAll('video, audio').forEach(el => { el.muted = false; el.volume = 1; });"
        }
        webViewRef?.evaluateJavascript(muteJs, null)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        mediaPlaybackRequiresUserGesture = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        if (isDesktopMode) {
                            userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                        }
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                            val initJs = """
                                try {
                                    ${if (isMuted) "document.querySelectorAll('video, audio').forEach(el => { el.muted = true; el.volume = 0; });" else ""}
                                    document.querySelectorAll('video').forEach(v => v.play());
                                } catch(e){}
                            """.trimIndent()
                            view?.evaluateJavascript(initJs, null)
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            return false
                        }
                    }
                    loadUrl(processedUrl)
                    webViewRef = this
                }
            },
            update = { wv ->
                if (wv.url != processedUrl) {
                    wv.loadUrl(processedUrl)
                }
            }
        )

        DisposableEffect(Unit) {
            onDispose {
                webViewRef?.stopLoading()
                webViewRef?.destroy()
                webViewRef = null
            }
        }

        // Overlay status header on top of the tile
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp),
            shape = RoundedCornerShape(4.dp),
            color = Color.Black.copy(alpha = 0.7f)
        ) {
            Text(
                text = "Screen #${tileIndex + 1}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        // Action icon: Reload this tile
        IconButton(
            onClick = { webViewRef?.reload() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reload Screen",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(2.dp)
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun formatVideoUrl(rawUrl: String, isMuted: Boolean): String {
    val trimmed = rawUrl.trim()
    if (trimmed.isEmpty()) return "https://www.youtube.com/embed/jfKfPfyJRdk?autoplay=1&mute=${if (isMuted) 1 else 0}"

    val muteParam = if (isMuted) "1" else "0"

    // YouTube formats:
    // https://youtu.be/ID
    // https://www.youtube.com/watch?v=ID
    // https://www.youtube.com/embed/ID
    // https://www.youtube.com/shorts/ID
    val videoId = when {
        trimmed.contains("youtu.be/") -> {
            trimmed.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
        }
        trimmed.contains("watch?v=") -> {
            trimmed.substringAfter("watch?v=").substringBefore("&")
        }
        trimmed.contains("/shorts/") -> {
            trimmed.substringAfter("/shorts/").substringBefore("?").substringBefore("&")
        }
        trimmed.contains("/embed/") -> {
            trimmed.substringAfter("/embed/").substringBefore("?").substringBefore("&")
        }
        else -> null
    }

    return if (!videoId.isNullOrEmpty()) {
        "https://www.youtube.com/embed/$videoId?autoplay=1&mute=$muteParam&loop=1&playlist=$videoId&enablejsapi=1"
    } else {
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }
}
