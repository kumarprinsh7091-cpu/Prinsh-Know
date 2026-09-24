package com.example.data

import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiService {
    private const val TAG = "GeminiApiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isBlank() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Feature: Convert text to speech using gemini-3.8-flash-tts
     */
    suspend fun generateSpeech(
        text: String,
        voiceName: String = "Kore"
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(Exception("Gemini API key is not configured in Secrets"))
        }

        try {
            val url = "$BASE_URL/models/gemini-3.8-flash-tts:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", text) })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().apply { put("AUDIO") })
                    put("speechConfig", JSONObject().apply {
                        put("voiceConfig", JSONObject().apply {
                            put("prebuiltVoiceConfig", JSONObject().apply {
                                put("voiceName", voiceName)
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "TTS Error: ${response.code} - $responseBody")
                return@withContext Result.failure(Exception("API returned ${response.code}: $responseBody"))
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            for (i in 0 until (parts?.length() ?: 0)) {
                val part = parts?.optJSONObject(i)
                val inlineData = part?.optJSONObject("inlineData")
                if (inlineData != null) {
                    val base64Data = inlineData.optString("data", "")
                    if (base64Data.isNotEmpty()) {
                        val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
                        return@withContext Result.success(audioBytes)
                    }
                }
            }

            Result.failure(Exception("No audio bytes received in response"))
        } catch (e: Exception) {
            Log.e(TAG, "generateSpeech failed", e)
            Result.failure(e)
        }
    }

    /**
     * Feature: Animate images into video using veo-3.1-fast-generate-preview
     * Aspect ratio: 16:9 or 9:16
     */
    suspend fun generateVeoVideo(
        prompt: String,
        aspectRatio: String = "16:9",
        imageBytes: ByteArray? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(Exception("Gemini API key is not configured in Secrets"))
        }

        try {
            val url = "$BASE_URL/models/veo-3.1-fast-generate-preview:generateVideos?key=$apiKey"

            val jsonBody = JSONObject().apply {
                put("prompt", prompt)
                put("config", JSONObject().apply {
                    put("numberOfVideos", 1)
                    put("resolution", "720p")
                    put("aspectRatio", aspectRatio)
                })
                if (imageBytes != null && imageBytes.isNotEmpty()) {
                    val base64Img = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    put("image", JSONObject().apply {
                        put("imageBytes", base64Img)
                        put("mimeType", "image/jpeg")
                    })
                }
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Veo Error: ${response.code} - $responseBody")
                return@withContext Result.failure(Exception("API ${response.code}: $responseBody"))
            }

            val rootJson = JSONObject(responseBody)
            val operationName = rootJson.optString("name", "")
            if (operationName.isNotEmpty()) {
                return@withContext Result.success("Operation started: $operationName")
            }

            Result.success("Video generated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "generateVeoVideo failed", e)
            Result.failure(e)
        }
    }

    /**
     * Feature: Maps Grounding using gemini-3.5-flash with googleMaps tool
     */
    suspend fun findCreatorSpotsWithMaps(
        query: String = "YouTube Creator Studio, High Speed WiFi Internet Cafe, and Coworking spaces"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(Exception("Gemini API key is not configured in Secrets"))
        }

        try {
            val url = "$BASE_URL/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "Find top YouTube creator studios, high-speed fiber internet cafes, and video editing spaces for: $query. Provide exact addresses, ratings, and operating highlights.")
                            })
                        })
                    })
                })
                put("tools", JSONArray().apply {
                    put(JSONObject().apply {
                        put("googleMaps", JSONObject())
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Maps Grounding Error: ${response.code} - $responseBody")
                return@withContext Result.failure(Exception("API ${response.code}: $responseBody"))
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            val textBuilder = StringBuilder()
            for (i in 0 until (parts?.length() ?: 0)) {
                val part = parts?.optJSONObject(i)
                val text = part?.optString("text", "") ?: ""
                if (text.isNotEmpty()) {
                    textBuilder.append(text).append("\n")
                }
            }

            val resultText = textBuilder.toString().trim()
            if (resultText.isNotEmpty()) {
                Result.success(resultText)
            } else {
                Result.failure(Exception("No places returned from Maps Grounding"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "findCreatorSpotsWithMaps failed", e)
            Result.failure(e)
        }
    }
}
