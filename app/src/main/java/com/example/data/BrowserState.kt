package com.example.data

data class BrowserConfig(
    val url: String = "https://www.youtube.com/embed/jfKfPfyJRdk?autoplay=1&mute=1",
    val rawInputUrl: String = "https://youtu.be/jfKfPfyJRdk",
    val viewCount: Int = 6,
    val isMuted: Boolean = true,
    val autoReloadIntervalSeconds: Int = 0, // 0 = off
    val isDesktopMode: Boolean = false,
    val isSessionActive: Boolean = false,
    val elapsedSeconds: Long = 0L,
    val reloadTriggerTimestamp: Long = 0L,
    val historyUrls: List<String> = listOf(
        "https://youtu.be/jfKfPfyJRdk", // Lofi Girl beats
        "https://www.youtube.com/watch?v=5qap5aO4i9A", // Lofi hip hop
        "https://www.youtube.com/watch?v=DWcJFNfaw9c" // Relaxing nature
    )
)
