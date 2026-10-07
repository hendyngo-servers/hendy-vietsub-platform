package com.vietsub.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vietsub.utils.SrtFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineEditorScreen(
    viewModel: TimelineEditorViewModel = viewModel { TimelineEditorViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Hiển thị thông báo Snackbar khi có lỗi hoặc thành công
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Biên Tập Phụ Đề (Timeline)", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    // Nút Dịch thuật bằng AI
                    IconButton(
                        onClick = { viewModel.translateAllSubtitles() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Dịch AI", tint = Color(0xFFFFD700))
                    }
                    // Nút Xuất file SRT
                    IconButton(onClick = { viewModel.exportSrt() }) {
                        Icon(Icons.Default.Save, contentDescription = "Lưu SRT")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.addSubtitle() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm phụ đề")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF121212))
        ) {
            if (uiState.subtitles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có phụ đề. Bấm + để thêm dòng mới.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.subtitles, key = { it.id }) { item ->
                        SubtitleBlockCard(
                            item = item,
                            onTextChange = { newText -> viewModel.updateSubtitleText(item.id, newText) },
                            onDelete = { viewModel.deleteSubtitle(item.id) }
                        )
                    }
                }
            }

            // Vùng Overlay hiển thị khi đang dịch thuật (Loading)
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Gemini AI đang dịch thuật phụ đề...", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtitleBlockCard(
    item: com.vietsub.models.SubtitleItem,
    onTextChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${item.id}  |  ${SrtFormatter.formatMsToSrtTime(item.startMs)} ➔ ${SrtFormatter.formatMsToSrtTime(item.endMs)}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color(0xFFFF5252))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = item.text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }
    }
}
