package com.hendy.vietsub.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hendy.vietsub.models.SubtitleItem

@Composable
fun TimelineEditor(
    subtitles: List<SubtitleItem>,
    onSubtitleChange: (String, String) -> Unit // Truyền ID và Text mới
) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E1E)).padding(16.dp)) {
        Text(
            text = "Hendy Vietsub - Timeline Editor",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(subtitles, key = { it.id }) { sub ->
                SubtitleRow(subtitle = sub, onTextChange = { newText ->
                    onSubtitleChange(sub.id, newText)
                })
            }
        }
    }
}

@Composable
fun SubtitleRow(subtitle: SubtitleItem, onTextChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        backgroundColor = Color(0xFF2D2D2D),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hiển thị thời gian
            Column(modifier = Modifier.width(120.dp)) {
                Text(text = formatTime(subtitle.startTimeMs), color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                Text(text = "-->", color = Color.Gray, fontSize = 12.sp)
                Text(text = formatTime(subtitle.endTimeMs), color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Ô nhập liệu cho phụ đề dịch
            OutlinedTextField(
                value = subtitle.translatedText,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    cursorColor = Color(0xFF4CAF50),
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray
                ),
                maxLines = 3
            )
        }
    }
}

// Hàm tiện ích format milliseconds sang dạng MM:SS.ms
fun formatTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val ms = timeMs % 1000
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}.${ms.toString().padStart(3, '0')}"
}
