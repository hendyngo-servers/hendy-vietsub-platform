package com.vietsub.utils

import androidx.compose.runtime.Composable

interface FileReader {
    suspend fun readBytes(pathOrUri: String): ByteArray
}

@Composable
expect fun rememberFileReader(): FileReader
