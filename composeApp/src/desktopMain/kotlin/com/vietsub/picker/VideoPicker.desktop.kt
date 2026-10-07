package com.vietsub.picker

import androidx.compose.runtime.Composable
import java.awt.FileDialog
import java.awt.Frame

@Composable
actual fun rememberVideoPickerLauncher(
    onVideoSelected: (pathOrUri: String) -> Unit
): () -> Unit {
    return {
        val dialog = FileDialog(null as Frame?, "Chọn file Video", FileDialog.LOAD).apply {
            filenameFilter = java.io.FilenameFilter { _, name ->
                val lower = name.lowercase()
                lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".mov") || lower.endsWith(".avi")
            }
            isVisible = true
        }

        if (dialog.directory != null && dialog.file != null) {
            val fullPath = dialog.directory + dialog.file
            onVideoSelected(fullPath)
        }
    }
}
