package com.vietsub.picker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
actual fun rememberVideoPickerLauncher(
    onVideoSelected: (pathOrUri: String) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onVideoSelected(it.toString()) }
    }
    
    return { launcher.launch("video/*") }
}
