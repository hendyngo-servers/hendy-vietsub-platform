com.vietsub.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vietsub.models.SubtitleItem
import com.vietsub.picker.rememberVideoPickerLauncher
import com.vietsub.utils.SrtFormatter
import com.vietsub.utils.rememberFileReader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineEditorScreen(
    viewModel: TimelineEditorViewModel = viewModel { TimelineEditorViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // FileReader hook đa nền tảng
    val fileReader = rememberFileReader()

    // VideoPicker launcher
    val openVideoPicker = rememberVideoPickerLauncher { pathOrUri ->
        coroutineScope.launch {
            val fileBytes = fileReader.readBytes(pathOrUri)
            viewModel.processVideoStt(fileBytes = fileBytes, fileName = "user_video.mp4")
        }
    }

    // Hiển thị thông báo Snackbar
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
                title = { Text("Biên Tập Phụ Đề", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    // Nút Tạo Sub từ Video bằng Whisper STT
                    IconButton(
                        onClick = openVideoPicker,
                        enabled = !uiState.isLoading
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Tạo Sub từ Video", tint = MaterialTheme.colorScheme.primary)
                    }
                    // Nút Dịch AI Gemini
                    IconButton(
                        onClick = { viewModel.translateAllSubtitles() },
                        enabled = !uiState.isLoading && uiState.subtitles.isNotEmpty()
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Dịch AI", tint = Color(0xFFFFD700))
                    }
                    // Nút Xuất SRT
                    IconButton(
                        onClick = { viewModel.exportSrt() },
                        enabled = uiState.subtitles.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Xuất SRT")
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có phụ đề.\nBấm + để thêm dòng mới hoặc chọn icon Upload để tạo tự động.",
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
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

            // Màn hình mờ khi đang tải / xử lý
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.successMessage ?: "Đang xử lý...",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
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
    onTextChange: (String) -> Unit,
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
