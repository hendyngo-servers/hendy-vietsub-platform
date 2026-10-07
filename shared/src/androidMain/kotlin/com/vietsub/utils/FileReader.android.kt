package com.vietsub.utils

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidFileReader(private val context: Context) : FileReader {
    override suspend fun readBytes(pathOrUri: String): ByteArray = withContext(Dispatchers.IO) {
        val uri = Uri.parse(pathOrUri)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        } ?: throw IllegalArgumentException("Không thể mở file từ Uri: $pathOrUri")
    }
}

@Composable
actual fun rememberFileReader(): FileReader {
    val context = LocalContext.current
    return remember(context) { AndroidFileReader(context) }
}
