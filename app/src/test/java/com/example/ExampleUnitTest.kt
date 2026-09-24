package com.example

import com.example.ui.components.formatVideoUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testYouTubeUrlFormatting() {
        val shortUrl = "https://youtu.be/jfKfPfyJRdk"
        val embedUrl = formatVideoUrl(shortUrl, isMuted = true)
        assertTrue(embedUrl.contains("https://www.youtube.com/embed/jfKfPfyJRdk"))
        assertTrue(embedUrl.contains("mute=1"))
    }

    @Test
    fun testYouTubeWatchUrlFormatting() {
        val watchUrl = "https://www.youtube.com/watch?v=5qap5aO4i9A"
        val embedUrl = formatVideoUrl(watchUrl, isMuted = false)
        assertTrue(embedUrl.contains("https://www.youtube.com/embed/5qap5aO4i9A"))
        assertTrue(embedUrl.contains("mute=0"))
    }
}
