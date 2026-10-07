import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.hendy.vietsub.models.SubtitleItem
import com.hendy.vietsub.ui.TimelineEditor

fun main() = application {
    // Dữ liệu giả lập (Mock Data) để hiển thị giao diện
    var subtitles by remember {
        mutableStateOf(
            listOf(
                SubtitleItem("1", 0L, 2500L, "Hello everyone", "Xin chào mọi người"),
                SubtitleItem("2", 2600L, 5000L, "Welcome to Hendy Vietsub", "Chào mừng đến với Hendy Vietsub"),
                SubtitleItem("3", 5100L, 8000L, "Today we will learn Kotlin", "Hôm nay chúng ta sẽ học Kotlin")
            )
        )
    }

    Window(onCloseRequest = ::exitApplication, title = "Hendy Vietsub Platform") {
        TimelineEditor(
            subtitles = subtitles,
            onSubtitleChange = { id, newText ->
                // Cập nhật lại state khi user gõ phím
                subtitles = subtitles.map { 
                    if (it.id == id) it.copy(translatedText = newText) else it 
                }
            }
        )
    }
}
