package com.vietsub.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberFileReader(): FileReader {
    return remember {
        object : FileReader {
            override suspend fun readBytes(pathOrUri: String): ByteArray = withContext(Dispatchers.IO) {
                val nsData = NSData.dataWithContentsOfFile(pathOrUri) ?: return@withContext ByteArray(0)
                val byteArray = ByteArray(nsData.length.toInt())
                byteArray.usePinned { pinned ->
                    memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
                }
                byteArray
            }
        }
    }
}
