package com.example.cardashboard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay30
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cardashboard.media.MediaUiState

@Composable
fun MediaPlayerCard(
    state: MediaUiState,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onBack30: () -> Unit,
    onForward30: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.art != null) {
                    Image(
                        bitmap = state.art.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Icon(
                        Icons.Default.Album,
                        contentDescription = null,
                        modifier = Modifier.size(96.dp),
                        tint = Color(0xFF4FC3F7)
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        state.title.ifBlank { "Kein Titel" },
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        state.artist.ifBlank { "Unbekannter Künstler" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (state.album.isNotBlank()) {
                        Text(
                            state.album,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (state.appName.isNotBlank()) {
                        Text(
                            "via ${state.appName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF4FC3F7)
                        )
                    }
                }
            }
            val progress =
                if (state.durationMs > 0) state.positionMs.toFloat() / state.durationMs.toFloat() else 0f
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4FC3F7),
                trackColor = Color(0xFF333333)
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipPrevious, null, Modifier.size(40.dp))
                }
                IconButton(onClick = onBack30, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.Replay30, null, Modifier.size(36.dp))
                }
                IconButton(onClick = onToggle, modifier = Modifier.size(72.dp)) {
                    Icon(
                        if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        null,
                        Modifier.size(56.dp),
                        tint = Color(0xFF4FC3F7)
                    )
                }
                IconButton(onClick = onForward30, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.Forward30, null, Modifier.size(36.dp))
                }
                IconButton(onClick = onNext, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.SkipNext, null, Modifier.size(40.dp))
                }
            }
        }
    }
}
