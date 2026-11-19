package com.example.cardashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cardashboard.ui.components.AppLauncherRow
import com.example.cardashboard.ui.components.MediaPlayerCard
import com.example.cardashboard.ui.components.QuickActionsGrid
import com.example.cardashboard.ui.components.SettingsDialog
import com.example.cardashboard.ui.components.SpeedCard
import com.example.cardashboard.ui.theme.CarDashboardTheme

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val media by viewModel.mediaState.collectAsState()
    val speed by viewModel.speedKmh.collectAsState()
    val actions by viewModel.quickActions.collectAsState()
    val status by viewModel.statusMessage.collectAsState()
    val notifGranted by viewModel.notificationAccessGranted.collectAsState()
    val forceResizable by viewModel.forceResizable.collectAsState()

    var showSettings by remember { mutableStateOf(false) }

    CarDashboardTheme {
        Surface(Modifier.fillMaxSize(), color = Color.Black) {
            Column(Modifier.fillMaxSize().padding(10.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Car Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF4FC3F7)
                    )
                    if (!notifGranted) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFFAB40),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            "Benachrichtigungszugriff fehlt",
                            color = Color(0xFFFFAB40),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        )
                        TextButton(onClick = viewModel::openNotificationAccessSettings) {
                            Text("Zugriff erteilen")
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Einstellungen")
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (!forceResizable) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AspectRatio,
                            contentDescription = null,
                            tint = Color(0xFFFFAB40),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "Split-Screen: „Aktivitäten als größenveränderbar anpassen" in den Entwickleroptionen aktivieren (betrifft die Navi-App, danach Neustart).",
                            color = Color(0xFFFFAB40),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = viewModel::openDeveloperOptions) {
                            Text("Entwickleroptionen")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Row(
                    Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MediaPlayerCard(
                        state = media,
                        onToggle = viewModel::togglePlayPause,
                        onNext = viewModel::skipNext,
                        onPrevious = viewModel::skipPrevious,
                        onBack30 = viewModel::seekBack30,
                        onForward30 = viewModel::seekForward30,
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                    )
                    Column(
                        Modifier
                            .weight(3f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpeedCard(
                            speedKmh = speed,
                            modifier = Modifier
                                .weight(1.1f)
                                .fillMaxWidth()
                        )
                        QuickActionsGrid(
                            actions = actions,
                            onAction = viewModel::runQuickAction,
                            modifier = Modifier
                                .weight(2f)
                                .fillMaxWidth()
                        )
                        AppLauncherRow(
                            onLaunch = viewModel::launchAdjacent,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }
                }
                if (status != null) {
                    Text(
                        status!!,
                        color = Color(0xFFFFAB40),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
        if (showSettings) {
            SettingsDialog(
                initialBaseUrl = viewModel.haBaseUrl,
                initialToken = viewModel.haToken,
                initialActionsJson = viewModel.quickActionsJson,
                onDismiss = { showSettings = false },
                onSave = { url, token, json ->
                    viewModel.saveSettings(url, token, json)
                    showSettings = false
                }
            )
        }
    }
}
