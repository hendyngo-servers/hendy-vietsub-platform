package com.vietsub.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DesktopFileReader : FileReader {
    override suspend fun readBytes(pathOrUri: String): ByteArray = withContext(Dispatchers.IO) {
        val file = File(pathOrUri)
        if (!file.exists()) {
            throw IllegalArgumentException("Không tìm thấy file: $pathOrUri")
        }
        file.readBytes()
    }
}

@Composable
actual fun rememberFileReader(): FileReader {
    return remember { DesktopFileReader() }
}
