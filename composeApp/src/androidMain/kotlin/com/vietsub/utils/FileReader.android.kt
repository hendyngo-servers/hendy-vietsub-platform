package com.vietsub.utils

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun rememberFileReader(): FileReader {
    val context = LocalContext.current
    return remember(context) {
        object : FileReader {
            override suspend fun readBytes(pathOrUri: String): ByteArray = withContext(Dispatchers.IO) {
                val uri = Uri.parse(pathOrUri)
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
            }
        }
    }
}
