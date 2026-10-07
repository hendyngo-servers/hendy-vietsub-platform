package com.vietsub.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.interop.LocalUIViewController
import platform.Foundation.NSURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTType
import platform.darwin.NSObject

@Composable
actual fun rememberVideoPickerLauncher(
    onVideoSelected: (pathOrUri: String) -> Unit
): () -> Unit {
    val uiViewController = LocalUIViewController.current

    return {
        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTType.movie, UTType.video),
            asCopy = true
        )
        picker.delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                url?.path?.let { onVideoSelected(it) }
            }
        }
        uiViewController.presentViewController(picker, animated = true, completion = null)
    }
}
