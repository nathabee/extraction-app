package com.example.extractionkotlinapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color  // ✅ Added for color customization

@Composable
fun NavigationDrawer(onCloseDrawer: () -> Unit, onSettingsClick: () -> Unit) {
    Surface( // ✅ ADDED: Solid background for better contrast
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface, // Change to Color.Black for dark theme
        tonalElevation = 8.dp // Adds subtle shadow effect
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Menu",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface, // ✅ Ensures good contrast
                style = MaterialTheme.typography.headlineMedium
            )
            HorizontalDivider()

            DrawerItem("Home", Icons.Default.Home, onCloseDrawer)
            DrawerItem("Settings", Icons.Default.Settings, onSettingsClick)
            DrawerItem("Gallery", Icons.Default.Search, onCloseDrawer) // ✅ FIXED: Changed to `Icons.Default.Photo`
        }
    }
}

@Composable
fun DrawerItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface, // ✅ Ensures icon visibility
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface) // ✅ Ensures text visibility
    }
}
