package com.example.cardashboard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val NAV_APPS = listOf(
    "Maps" to "com.google.android.apps.maps",
    "Magic Earth" to "com.magicearth"
)

@Composable
fun AppLauncherRow(
    onLaunch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616))
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Navi (Split-Screen):",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4FC3F7)
            )
            NAV_APPS.forEach { (label, packageName) ->
                Button(
                    onClick = { onLaunch(packageName) },
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242424)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text(label, maxLines = 1)
                }
            }
        }
    }
}
