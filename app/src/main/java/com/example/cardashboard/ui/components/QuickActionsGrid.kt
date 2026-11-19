package com.example.cardashboard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cardashboard.settings.QuickAction

fun iconFor(name: String): ImageVector = when (name) {
    "Garage" -> Icons.Default.Garage
    "ElectricCar" -> Icons.Default.ElectricCar
    "DriveEta" -> Icons.Default.DriveEta
    "Lightbulb" -> Icons.Default.Lightbulb
    else -> Icons.Default.ToggleOn
}

@Composable
fun QuickActionsGrid(
    actions: List<QuickAction>,
    onAction: (QuickAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616))
    ) {
        Column(Modifier.fillMaxSize().padding(10.dp)) {
            Text(
                "Home Assistant",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4FC3F7)
            )
            Spacer(Modifier.height(6.dp))
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                actions.chunked(2).forEach { row ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { action ->
                            Button(
                                onClick = { onAction(action) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF242424)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        iconFor(action.icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = Color(0xFF4FC3F7)
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        action.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
