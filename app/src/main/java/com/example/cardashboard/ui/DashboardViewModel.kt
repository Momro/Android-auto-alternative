package com.example.cardashboard.ui

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardashboard.homeassistant.HomeAssistantClient
import com.example.cardashboard.location.LocationHandler
import com.example.cardashboard.media.MediaSessionHandler
import com.example.cardashboard.media.MediaUiState
import com.example.cardashboard.settings.QuickAction
import com.example.cardashboard.settings.SettingsStore
import com.example.cardashboard.settings.parseQuickActions
import com.example.cardashboard.system.ResizableHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = SettingsStore(application)
    private val mediaHandler = MediaSessionHandler(application)
    private val locationHandler = LocationHandler(application)
    private val haClient = HomeAssistantClient({ settings.haBaseUrl }, { settings.haToken })

    val mediaState: StateFlow<MediaUiState> = mediaHandler.state

    val speedKmh: StateFlow<Int> = locationHandler.speedKmh()
        .map { it.coerceAtLeast(0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _quickActions = MutableStateFlow(parseQuickActions(settings.quickActionsJson))
    val quickActions: StateFlow<List<QuickAction>> = _quickActions

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    private val _notificationAccessGranted =
        MutableStateFlow(MediaSessionHandler.isNotificationAccessGranted(application))
    val notificationAccessGranted: StateFlow<Boolean> = _notificationAccessGranted

    private val _forceResizable =
        MutableStateFlow(ResizableHelper.isForceResizableEnabled(application))
    val forceResizable: StateFlow<Boolean> = _forceResizable

    val haBaseUrl: String get() = settings.haBaseUrl
    val haToken: String get() = settings.haToken
    val quickActionsJson: String get() = settings.quickActionsJson

    init {
        mediaHandler.start()
    }

    fun togglePlayPause() = mediaHandler.togglePlayPause()
    fun skipNext() = mediaHandler.skipNext()
    fun skipPrevious() = mediaHandler.skipPrevious()
    fun seekBack30() = mediaHandler.seekBy(-30_000)
    fun seekForward30() = mediaHandler.seekBy(30_000)

    fun runQuickAction(action: QuickAction) {
        viewModelScope.launch {
            _statusMessage.value = "Sende: ${action.label} ..."
            val result = haClient.callService(action.domain, action.service, action.entityId)
            _statusMessage.value = result.fold(
                onSuccess = { "OK: ${action.label}" },
                onFailure = { "Fehler: ${it.message}" }
            )
        }
    }

    fun launchAdjacent(packageName: String) {
        val context = getApplication<Application>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent == null) {
            _statusMessage.value = "App nicht installiert: $packageName"
            return
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            _statusMessage.value = "Start fehlgeschlagen: ${e.message}"
        }
    }

    fun openNotificationAccessSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }

    fun refreshNotificationAccess() {
        _notificationAccessGranted.value =
            MediaSessionHandler.isNotificationAccessGranted(getApplication())
    }

    fun refreshForceResizable() {
        _forceResizable.value = ResizableHelper.isForceResizableEnabled(getApplication())
    }

    fun openDeveloperOptions() {
        ResizableHelper.openDeveloperOptions(getApplication())
    }

    fun onLocationPermissionChanged() {
        // Flow startet beim nächsten StateIn-Subscriber mit Permission neu.
    }

    fun saveSettings(baseUrl: String, token: String, actionsJson: String) {
        settings.haBaseUrl = baseUrl
        settings.haToken = token
        settings.quickActionsJson = actionsJson
        _quickActions.value = parseQuickActions(actionsJson)
    }

    override fun onCleared() {
        mediaHandler.stop()
        super.onCleared()
    }
}
