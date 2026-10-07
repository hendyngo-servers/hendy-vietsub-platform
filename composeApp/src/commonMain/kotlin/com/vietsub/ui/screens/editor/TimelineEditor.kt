package com.vietsub.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vietsub.models.SubtitleItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineEditorScreen(
    initialSubtitles: List<SubtitleItem> = emptyList(),
    onTranslateClick: (List<SubtitleItem>) -> Unit = {},
    onSaveClick: (List<SubtitleItem>) -> Unit = {}
) {
    val subtitles = remember { mutableStateListOf<SubtitleItem>().apply { addAll(initialSubtitles) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biên Tập Phụ Đề (Timeline)", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    // Nút dịch tự động AI
                    IconButton(onClick = { onTranslateClick(subtitles) }) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Dịch AI", tint = Color(0xFFFFD700))
                    }
                    // Nút lưu / xuất file SRT
                    IconButton(onClick = { onSaveClick(subtitles) }) {
                        Icon(Icons.Default.Save, contentDescription = "Lưu SRT")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val lastEnd = subtitles.lastOrNull()?.endMs ?: 0L
                    subtitles.add(
                        SubtitleItem(
                            id = subtitles.size + 1,
                            startMs = lastEnd + 100,
                            endMs = lastEnd + 3000,
                            text = "Phụ đề mới..."
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm phụ đề")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF121212))
        ) {
            if (subtitles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có phụ đề. Bấm + để thêm dòng mới.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(subtitles) { index, item ->
                        SubtitleBlockCard(
                            item = item,
                            onUpdate = { updatedItem -> subtitles[index] = updatedItem },
                            onDelete = { subtitles.removeAt(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtitleBlockCard(
    item: SubtitleItem,
    onUpdate: (SubtitleItem) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hiển thị mốc thời gian dạng 00:00:01,000
                Text(
                    text = "#${item.id}  |  ${formatMsToTime(item.startMs)} ➔ ${formatMsToTime(item.endMs)}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color(0xFFFF5252))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Khung nhập văn bản phụ đề
            OutlinedTextField(
                value = item.text,
                onValueChange = { newText -> onUpdate(item.copy(text = newText)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 3
            )
        }
    }
}

// Hàm hỗ trợ format Miliseconds sang chuỗi thời gian chuẩn Subtitle (MM:SS,mmm)
private fun formatMsToTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / (1000 * 60)) % 60
    val millis = ms % 1000
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')},${millis.toString().padStart(3, '0')}"
}
