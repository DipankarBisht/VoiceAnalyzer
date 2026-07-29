package com.example.voiceanalyzer.ui.Screens

import android.R
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
import com.example.voiceanalyzer.Data.Media.AudioFile
import com.example.voiceanalyzer.ui.theme.MiniPlayer
import com.example.voiceanalyzer.ViewModel.RecordingViewModel
import com.example.voiceanalyzer.ui.theme.ActionButton
import com.example.voiceanalyzer.ui.theme.AppBackground
import com.example.voiceanalyzer.ui.theme.BorderAccent
import com.example.voiceanalyzer.ui.theme.PrimaryButton
import com.example.voiceanalyzer.ui.theme.SurfaceCard

@Composable
fun RecordingsScreen(
    viewModel: RecordingViewModel,
    onAnalyzeClick: (String) -> Unit

) {
    val isPlaying by viewModel.isplaying_audio.collectAsState()
    val currentUri by viewModel.current_uri.collectAsState()
    val playbackState by viewModel.playbackstate.collectAsState()
    val recordings by viewModel.recording.collectAsState()


    var recordingToDelete by remember { mutableStateOf<AudioFile?>(null) }
    var recordingToRename by remember { mutableStateOf<AudioFile?>(null) }
    var renameText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.load_recordings()
    }

    val selectedFile = recordings.firstOrNull { it.uri == currentUri }
 
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
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
                                    if (isPlaying) viewModel.pause_audio()
                                    else viewModel.resume_audio()
                                } else {
                                    viewModel.play_audio(file.uri)
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
                        if (isPlaying) viewModel.pause_audio() else viewModel.resume_audio()
                    },
                    onSeek = { pos -> viewModel.seekto(pos) },
                    onClose = { viewModel.stop_audio() }
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
                        if (currentUri == file.uri) viewModel.stop_audio()
                        viewModel.delete_audio(file)
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
                            viewModel.rename_audio(file, trimmed)
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
                color = if (isPlaying) PrimaryButton else BorderAccent.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying)
                ActionButton
            else
                SurfaceCard
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
                            if (isPlaying) PrimaryButton
                            else Color(0xFF1D1B36)
                        )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = if (isPlaying) Color.White
                        else PrimaryButton,
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
                        color =  Color(0xFFF1EFF7),
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
                            color = Color(0xFF8E8B9E)
                        )
                        // File Size Indicator
                        Text(
                            text = formatSize(audioFile.sizeInBytes),
                            style = MaterialTheme.typography.bodyMedium,
                            color  = Color(0xFF8E8B9E).copy(alpha = 0.7f)
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
                    text = { Text("Analyze", color = PrimaryButton) },
                    onClick = {
                        showMenu = false
                        onAnalyzeClick()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_info_details),
                            contentDescription = "Analyze",
                            tint = PrimaryButton
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
