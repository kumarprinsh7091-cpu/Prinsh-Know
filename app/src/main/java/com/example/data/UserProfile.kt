package com.example.data

data class UserProfile(
    val name: String = "Prinsh Kumar",
    val email: String = "prinshknow@gmail.com",
    val phone: String = "7084022653",
    val bio: String = "Passionate tech explorer & video creator. Building high-performance tools and riding through scenic routes.",
    val education: String = "New Central Public Academy - Intermediate PCM",
    val strengths: List<String> = listOf("Quick Learner", "Teamwork", "Communication", "Time Management"),
    val isLoggedIn: Boolean = true,
    val totalWatchSeconds: Long = 4820L,
    val sessionsCompleted: Int = 12
)
