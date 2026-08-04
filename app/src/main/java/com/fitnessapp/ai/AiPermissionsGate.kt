package com.fitnessapp.ai

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCard
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary

/** Returns the set of permissions required for AI hardware integration on the current API level. */
fun requiredAiPermissions(): List<String> = buildList {
    add(Manifest.permission.CAMERA)
    add(Manifest.permission.ACTIVITY_RECOGNITION)
    add(Manifest.permission.BODY_SENSORS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

/**
 * Checks whether all required AI permissions are currently granted.
 */
fun Context.allAiPermissionsGranted(): Boolean {
    return requiredAiPermissions().all { permission ->
        checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Drop-in composable that requests CAMERA, STORAGE, and SENSOR permissions in a single
 * multi-permission launcher without disrupting the UI thread.
 *
 * @param onAllGranted Called when every required permission is granted.
 * @param onDenied Called with the list of denied permissions.
 * @param content The UI to render once permissions are acquired (or check state inline).
 */
@Composable
fun AiPermissionsGate(
    onAllGranted: () -> Unit = {},
    onDenied: (List<String>) -> Unit = {},
    content: @Composable (allGranted: Boolean) -> Unit
) {
    val context = LocalContext.current
    val permissionStates = remember { mutableStateMapOf<String, Boolean>() }

    val permissions = remember { requiredAiPermissions() }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        results.forEach { (perm, granted) -> permissionStates[perm] = granted }
        val denied = results.filterValues { !it }.keys.toList()
        if (denied.isEmpty()) onAllGranted() else onDenied(denied)
    }

    // Sync initial grant state
    LaunchedEffect(Unit) {
        permissions.forEach { perm ->
            permissionStates[perm] =
                context.checkSelfPermission(perm) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        val needRequest = permissions.filter { permissionStates[it] != true }
        if (needRequest.isNotEmpty()) {
            launcher.launch(needRequest.toTypedArray())
        } else {
            onAllGranted()
        }
    }

    val allGranted = permissions.all { permissionStates[it] == true }
    content(allGranted)
}

/**
 * Compact UI card listing the three AI permission groups and their grant status.
 * Renders a "Grant Permissions" button if any are missing.
 */
@Composable
fun AiPermissionStatusCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val permissions = remember { requiredAiPermissions() }
    val states = remember { mutableStateMapOf<String, Boolean>() }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results -> results.forEach { (p, g) -> states[p] = g } }

    LaunchedEffect(Unit) {
        permissions.forEach { p ->
            states[p] = context.checkSelfPermission(p) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    val allGranted = permissions.all { states[it] == true }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            "AI Hardware Permissions",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        PermissionRow("Camera", Icons.Default.CameraAlt, AccentOrange,
            states[Manifest.permission.CAMERA] == true)
        Spacer(modifier = Modifier.height(8.dp))

        val storageGranted = if (Build.VERSION.SDK_INT >= 33)
            states[Manifest.permission.READ_MEDIA_IMAGES] == true
        else
            states[Manifest.permission.READ_EXTERNAL_STORAGE] == true

        PermissionRow("Local Storage", Icons.Default.FolderOpen, AccentBlue, storageGranted)
        Spacer(modifier = Modifier.height(8.dp))

        PermissionRow("Body Sensors", Icons.Default.Sensors, AccentGreen,
            states[Manifest.permission.BODY_SENSORS] == true)

        if (!allGranted) {
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = { launcher.launch(permissions.toTypedArray()) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Grant AI Permissions", color = BackgroundDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PermissionRow(label: String, icon: ImageVector, color: Color, granted: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, color = TextSecondary)
        }
        Text(
            text = if (granted) "✓ Granted" else "✗ Denied",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (granted) AccentGreen else AccentOrange
        )
    }
}
