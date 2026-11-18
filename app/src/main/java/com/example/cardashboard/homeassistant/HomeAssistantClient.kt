package com.example.cardashboard.homeassistant

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class HomeAssistantClient(
    private val baseUrlProvider: () -> String,
    private val tokenProvider: () -> String
) {

    suspend fun callService(
        domain: String,
        service: String,
        entityId: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val base = baseUrlProvider().trimEnd('/')
            require(base.isNotBlank()) { "Home-Assistant-URL fehlt (Einstellungen)" }
            require(tokenProvider().isNotBlank()) { "Access Token fehlt (Einstellungen)" }

            val conn = (URL("$base/api/services/$domain/$service")
                .openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer ${tokenProvider()}")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            val payload = JSONObject().apply {
                if (!entityId.isNullOrBlank()) put("entity_id", entityId)
            }
            conn.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            runCatching { conn.inputStream.bufferedReader().use { it.readText() } }
            conn.disconnect()
            require(code in 200..299) { "HA antwortet mit HTTP $code" }
        }
    }

    suspend fun getState(entityId: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val base = baseUrlProvider().trimEnd('/')
            require(base.isNotBlank()) { "Home-Assistant-URL fehlt (Einstellungen)" }

            val conn = (URL("$base/api/states/$entityId")
                .openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Bearer ${tokenProvider()}")
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            val code = conn.responseCode
            val body = conn.inputStream.bufferedReader().readText()
            conn.disconnect()
            require(code in 200..299) { "HA antwortet mit HTTP $code" }
            JSONObject(body).getString("state")
        }
    }
}
