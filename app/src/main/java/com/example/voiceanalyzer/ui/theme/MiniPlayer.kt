package com.example.voiceanalyzer.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.ui.graphics.Color
import com.example.voiceanalyzer.Data.Media.AudioFile
import com.example.voiceanalyzer.Data.Media.PlaybackState
import com.example.voiceanalyzer.ui.Screens.formatTime


@Composable
fun MiniPlayer(
    audioFile: AudioFile,
    isPlaying: Boolean,
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onClose: () -> Unit
) {
    val totalDuration = playbackState.totalDurationMs.coerceAtLeast(1)
    var isDragging by remember { mutableStateOf(false) }
    var sliderPos by remember { mutableStateOf(0f) }

    LaunchedEffect(playbackState.currentPositionMs) {
        if (!isDragging) sliderPos = playbackState.currentPositionMs.toFloat()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Margin so it floats
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceCard) // Translucent background
            .border(
                width = 1.dp,
                color = BorderAccent,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
    )  {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "NOW PLAYING",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = audioFile.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close")
                }

            }

            Slider(
                value = sliderPos.coerceIn(0f, totalDuration.toFloat()),
                onValueChange = {
                    isDragging = true
                    sliderPos = it
                },
                onValueChangeFinished = {
                    isDragging = false
                    onSeek(sliderPos.toInt())
                },
                valueRange = 0f..totalDuration.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryButton,
                    activeTrackColor = PrimaryButton,
                    inactiveTrackColor = BorderAccent
                )
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formatTime(sliderPos.toLong()),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTime(totalDuration.toLong()),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

            }
        }
    }
}