package com.example.voiceanalyzer

import android.R
import android.graphics.Color.blue
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun RecordingsScreen(
    playerManager: PlayerManager,
    repository: AudioRepository,
    onAnalyzeClick: (String) -> Unit  // ← add this

) {
    val isPlaying by playerManager.isPlaying.collectAsState()
    val currentUri by playerManager.currentUri.collectAsState()
    val playbackState by playerManager.playbackState.collectAsState()

    var recordings by remember { mutableStateOf(listOf<AudioFile>()) }
    var recordingToDelete by remember { mutableStateOf<AudioFile?>(null) }
    var recordingToRename by remember { mutableStateOf<AudioFile?>(null) }
    var renameText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        recordings = repository.fetchSavedRecordings()
    }

    val selectedFile = recordings.firstOrNull { it.uri == currentUri }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )  {
            Text(
                text = "Saved Audio Memos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (recordings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recorded tracks found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(recordings) { file ->
                        RecordingRowItem(
                            audioFile = file,
                            isPlaying = currentUri == file.uri && isPlaying,
                            onClick = {
                                if (currentUri == file.uri) {
                                    if (isPlaying) playerManager.pause()
                                    else playerManager.resume()
                                } else {
                                    playerManager.play(file.uri)
                                }
                            },
                            onDeleteClick = { recordingToDelete = file },
                            onRenameClick = {
                                recordingToRename = file
                                renameText = file.name.removeSuffix(".m4a")
                            },
                            onAnalyzeClick = { onAnalyzeClick(file.filePath) } // ← add this
                        )
                    }
                    item { Spacer(modifier = Modifier.height(200.dp)) }
                }
            }
        }

        // Mini player overlaid at the bottom
        AnimatedVisibility(
            visible = selectedFile != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            selectedFile?.let {
                MiniPlayer(
                    audioFile = it,
                    isPlaying = isPlaying,
                    playbackState = playbackState,
                    onPlayPause = {
                        if (isPlaying) playerManager.pause() else playerManager.resume()
                    },
                    onSeek = { pos -> playerManager.seekTo(pos) },
                    onClose = { playerManager.stop() }
                )
            }
        }
    }

    // Delete Dialog
    if (recordingToDelete != null) {
        AlertDialog(
            onDismissRequest = { recordingToDelete = null },
            title = { Text("Delete Recording") },
            text = { Text("Are you sure you want to delete \"${recordingToDelete?.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    recordingToDelete?.let { file ->
                        if (currentUri == file.uri) playerManager.stop()
                        repository.deleteRecording(file)
                        recordings = repository.fetchSavedRecordings()
                    }
                    recordingToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordingToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Rename Dialog
    if (recordingToRename != null) {
        AlertDialog(
            onDismissRequest = { recordingToRename = null },
            title = { Text("Rename Recording") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    label = { Text("Name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    recordingToRename?.let { file ->
                        val trimmed = renameText.trim()
                        if (trimmed.isNotEmpty()) {
                            repository.renameRecording(file, trimmed)
                            recordings = repository.fetchSavedRecordings()
                        }
                    }
                    recordingToRename = null
                }) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { recordingToRename = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordingRowItem(
    audioFile: AudioFile,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRenameClick: () -> Unit,
    onAnalyzeClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
            // Left border accent line: glows emerald/cyan when playing, otherwise transparent/slate
            .border(
                width = if (isPlaying) 2.dp else 1.dp,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                // 1. PLAY STATE INDICATOR
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPlaying) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = if (isPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 2. FILE DETAILS & METADATA
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = audioFile.name.removeSuffix(".m4a"), // Remove extension suffix for clean look
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(audioFile.durationMs),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // File Size Indicator
                        Text(
                            text = formatSize(audioFile.sizeInBytes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        // Recording Date
                        Text(
                            text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                .format(Date(audioFile.dateAdded)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 3. EXPLICIT MORE-OPTIONS BUTTON
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 4. DROPDOWN OPTION MENU
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Rename") },
                    onClick = {
                        showMenu = false
                        onRenameClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rename"
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Analyze", color = Color(0xFF6C63FF)) },
                    onClick = {
                        showMenu = false
                        onAnalyzeClick()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_info_details),
                            contentDescription = "Analyze",
                            tint = Color(0xFF6C63FF)
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                    onClick = {
                        showMenu = false
                        onDeleteClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}

// 5. HELPER FUNCTION TO FORMAT FILE SIZE
fun formatSize(sizeInBytes: Long): String {
    if (sizeInBytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(sizeInBytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(Locale.getDefault(), "%.1f %s", sizeInBytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / (1000 * 60)) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
