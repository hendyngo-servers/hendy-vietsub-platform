package com.vietsub.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.posix.memcpy

class IosFileReader : FileReader {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun readBytes(pathOrUri: String): ByteArray = withContext(Dispatchers.IO) {
        val nsUrl = if (pathOrUri.startsWith("/")) {
            NSURL.fileURLWithPath(pathOrUri)
        } else {
            NSURL.URLWithString(pathOrUri)
        } ?: throw IllegalArgumentException("Đường dẫn file iOS không hợp lệ: $pathOrUri")

        val data = NSData.dataWithContentsOfURL(nsUrl)
            ?: throw IllegalArgumentException("Không thể đọc dữ liệu file từ: $pathOrUri")

        val length = data.length.toInt()
        val byteArray = ByteArray(length)
        if (length > 0) {
            byteArray.usePinned { pinned ->
                memcpy(pinned.addressOf(0), data.bytes, data.length)
            }
        }
        byteArray
    }
}

@Composable
actual fun rememberFileReader(): FileReader {
    return remember { IosFileReader() }
}
