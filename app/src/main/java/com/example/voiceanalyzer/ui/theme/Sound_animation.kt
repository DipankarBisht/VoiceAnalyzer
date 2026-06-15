package com.example.voiceanalyzer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Visualizer(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = 8f
        val gap = 6f
        val maxBars = (width / (barWidth + gap)).toInt()

        val displayAmplitudes = if (amplitudes.size > maxBars) {
            amplitudes.takeLast(maxBars)
        } else {
            List(maxBars - amplitudes.size) { 0.02f } + amplitudes
        }

        val brush = Brush.linearGradient(
            colors = listOf(Color(0xFF00FF87), Color(0xFF60EFFF)),
            start = Offset(0f, height / 2f),
            end = Offset(width, height / 2f)
        )

        displayAmplitudes.forEachIndexed { index, amp ->
            val normalizedAmp = (amp * 1.2f).coerceIn(0.02f, 1.0f)
            val barHeight = normalizedAmp * height
            val x = index * (barWidth + gap)
            val y = (height - barHeight) / 2f

            drawRoundRect(
                brush = brush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}