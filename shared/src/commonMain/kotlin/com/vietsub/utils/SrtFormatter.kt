package com.vietsub.utils

import com.vietsub.models.SubtitleItem

object SrtFormatter {

    /**
     * Chuyển danh sách SubtitleItem thành chuỗi file .srt tiêu chuẩn
     */
    fun encodeToSrt(items: List<SubtitleItem>): String {
        return items.joinToString("\n\n") { item ->
            "${item.id}\n${formatMsToSrtTime(item.startMs)} --> ${formatMsToSrtTime(item.endMs)}\n${item.text}"
        }
    }

    /**
     * Parse văn bản .srt trả về từ Server thành danh sách SubtitleItem
     */
    fun parseSrt(srtContent: String): List<SubtitleItem> {
        if (srtContent.isBlank()) return emptyList()
        val blocks = srtContent.trim().split(Regex("\n\\s*\n"))
        val result = mutableListOf<SubtitleItem>()

        for (block in blocks) {
            val lines = block.lines().map { it.trim() }.filter { it.isNotEmpty() }
            if (lines.size >= 3) {
                val id = lines[0].toIntOrNull() ?: (result.size + 1)
                val timeTokens = lines[1].split("-->").map { it.trim() }
                if (timeTokens.size == 2) {
                    val startMs = parseSrtTimeToMs(timeTokens[0])
                    val endMs = parseSrtTimeToMs(timeTokens[1])
                    val text = lines.subList(2, lines.size).joinToString("\n")
                    result.add(SubtitleItem(id, startMs, endMs, text))
                }
            }
        }
        return result
    }

    fun formatMsToSrtTime(ms: Long): String {
        val hours = ms / (1000 * 3600)
        val minutes = (ms / (1000 * 60)) % 60
        val seconds = (ms / 1000) % 60
        val millis = ms % 1000
        return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')},${millis.toString().padStart(3, '0')}"
    }

    private fun parseSrtTimeToMs(timeStr: String): Long {
        return try {
            val parts = timeStr.replace(',', '.').split(":")
            val hours = parts[0].toLong()
            val minutes = parts[1].toLong()
            val secondsWithMillis = parts[2].toDouble()
            (hours * 3600 + minutes * 60 + secondsWithMillis) * 1000
        }.toLong() catch (e: Exception) {
            0L
        }
    }
}
