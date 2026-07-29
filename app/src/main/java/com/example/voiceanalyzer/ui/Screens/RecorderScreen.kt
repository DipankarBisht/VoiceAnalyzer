package com.example.voiceanalyzer.ui.Screens

import android.Manifest
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voiceanalyzer.Visualizer
import com.example.voiceanalyzer.ViewModel.RecorderViewModel
import com.example.voiceanalyzer.ui.theme.ActionButton
import com.example.voiceanalyzer.ui.theme.AppBackground
import com.example.voiceanalyzer.ui.theme.BorderAccent
import com.example.voiceanalyzer.ui.theme.PrimaryButton
import com.example.voiceanalyzer.ui.theme.RecordingActive
import com.example.voiceanalyzer.ui.theme.SurfaceCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecorderScreen(viewModel : RecorderViewModel) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)


    val isRecording by viewModel.isRecording.collectAsState()
    val isPlay by viewModel.isplay.collectAsState()
    val amplitudes by viewModel.amplitude.collectAsState()
    val timerValue by viewModel.recordingTime.collectAsState()
    // Pulse animation setup

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )


    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = AppBackground ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(46.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = SurfaceCard
            ),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BorderAccent),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = time_converter(timerValue),
                fontSize = 54.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
// Wrap the visualizer in a weight-based Box inside your Column:
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
                .clip(RoundedCornerShape(45.dp))
                .border(width = 5.dp, color = BorderAccent,shape = RoundedCornerShape(45.dp))
                ,
            contentAlignment = Alignment.Center
        ) {
            Visualizer(amplitudes = amplitudes)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 1. DISCARD BUTTON
            if (isRecording) {
                IconButton(
                    onClick = {
                        viewModel.discard_recording(context)

                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(color =ActionButton)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Discard",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(28.dp))
            }

            // 2. MAIN RECORD BUTTON
            Box(contentAlignment = Alignment.Center,modifier = Modifier.size(124.dp)) {
                // Glowing Ring (pulsing animation)
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .size(88.dp * pulseScale) // Animating size
                            .clip(CircleShape)
                            .background(RecordingActive.copy(alpha = pulseAlpha))
                    )
                }

                IconButton(
                    onClick = {
                        if (!permissionState.status.isGranted) {
                            permissionState.launchPermissionRequest()
                        } else {
                            if (!isRecording) {
                                viewModel.start_recording(context)

                            } else {
                                viewModel.stop_recording(context)

                            }
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) Color(0xFFFF2D55)
                            else Color(0xFF6C63FF)
                        )
                ) {
                    Icon(
                        imageVector = if (!isRecording) Icons.Rounded.Mic else Icons.Rounded.Stop,
                        contentDescription = "Record",
                        tint = if (isRecording) Color.Black  else Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // 3. PAUSE BUTTON (Button 1) - Small, matching discard styling
            if (isRecording) {
                Spacer(modifier = Modifier.width(28.dp))
                IconButton(
                    onClick = {
                        if (isPlay) {
                            viewModel.pause_recording()
                        }
                        else {
                            viewModel.resume_recording()
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (isPlay) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Pause/Resume",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))


    }

}
fun time_converter(tms: Long): String{
    val tosec = tms/1000
    val sec = tosec%60
    val min = tosec/60
    return  "%02d:%02d".format(min,sec)

}