package com.example.cardashboard.media

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class MediaUiState(
    val appName: String = "",
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val art: Bitmap? = null,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L
)

class MediaSessionHandler(private val context: Context) {

    private val sessionManager: MediaSessionManager =
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private val listenerComponent = ComponentName(context, MediaNotificationListener::class.java)
    private val mainHandler = Handler(Looper.getMainLooper())

    private var controller: MediaController? = null

    private val controllerCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) = publish()
        override fun onMetadataChanged(metadata: MediaMetadata?) = publish()
        override fun onSessionDestroyed() = attach()
    }

    private val _state = MutableStateFlow(MediaUiState())
    val state: StateFlow<MediaUiState> = _state.asStateFlow()

    private var ticker: Job? = null

    @Synchronized
    fun start() {
        if (ticker != null) return
        ticker = CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
            while (isActive) {
                attach()
                publish()
                delay(1000)
            }
        }
    }

    fun stop() {
        ticker?.cancel()
        ticker = null
        detach()
    }

    @Synchronized
    private fun attach() {
        val sessions = try {
            sessionManager.getActiveSessions(listenerComponent)
        } catch (e: SecurityException) {
            emptyList()
        }
        val next = sessions.firstOrNull { it.playbackState != null }
        if (next != controller) {
            detach()
            controller = next
            next?.registerCallback(controllerCallback, mainHandler)
            publish()
        }
    }

    @Synchronized
    private fun detach() {
        controller?.unregisterCallback(controllerCallback)
        controller = null
    }

    @Synchronized
    private fun publish() {
        val c = controller
        if (c == null) {
            _state.value = MediaUiState()
            return
        }
        val md = c.metadata
        val ps = c.playbackState
        _state.value = MediaUiState(
            appName = appLabel(c.packageName),
            isPlaying = ps?.state == PlaybackState.STATE_PLAYING,
            title = md?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "",
            artist = md?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                ?: md?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                ?: "",
            album = md?.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: "",
            art = md?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: md?.getBitmap(MediaMetadata.METADATA_KEY_ART),
            positionMs = ps?.position ?: 0L,
            durationMs = md?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        )
    }

    private fun appLabel(packageName: String): String = try {
        val info = context.packageManager.getApplicationInfo(packageName, 0)
        context.packageManager.getApplicationLabel(info).toString()
    } catch (e: Exception) {
        packageName
    }

    fun togglePlayPause() {
        val c = controller ?: return
        if (c.playbackState?.state == PlaybackState.STATE_PLAYING) {
            c.transportControls.pause()
        } else {
            c.transportControls.play()
        }
    }

    fun skipNext() = controller?.transportControls?.skipToNext()

    fun skipPrevious() = controller?.transportControls?.skipToPrevious()

    fun seekBy(deltaMs: Long) {
        val c = controller ?: return
        val target = ((c.playbackState?.position ?: 0L) + deltaMs).coerceAtLeast(0L)
        c.transportControls.seekTo(target)
    }

    companion object {
        fun isNotificationAccessGranted(context: Context): Boolean {
            val enabled = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return false
            return enabled.contains(context.packageName)
        }
    }
}
