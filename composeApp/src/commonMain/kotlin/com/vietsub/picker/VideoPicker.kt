package com.vietsub.picker

import androidx.compose.runtime.Composable

@Composable
expect fun rememberVideoPickerLauncher(
    onVideoSelected: (pathOrUri: String) -> Unit
): () -> Unit
