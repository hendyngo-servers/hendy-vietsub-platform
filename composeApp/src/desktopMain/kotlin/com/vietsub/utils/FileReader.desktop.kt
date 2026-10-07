package com.vietsub.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
actual fun rememberFileReader(): FileReader {
    return remember {
        object : FileReader {
            override suspend fun readBytes(pathOrUri: String): ByteArray = withContext(Dispatchers.IO) {
                File(pathOrUri).readBytes()
            }
        }
    }
}
