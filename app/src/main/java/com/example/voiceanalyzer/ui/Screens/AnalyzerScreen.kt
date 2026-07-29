package com.example.voiceanalyzer.ui.Screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Color as AndroidColor
import android.widget.Toast
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.ContentCopy
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.voiceanalyzer.Data.Local.Analysis_data
import com.example.voiceanalyzer.ViewModel.AnalyzerViewModel
import com.example.voiceanalyzer.Data.Local.*
import com.example.voiceanalyzer.ui.theme.AppBackground
import com.example.voiceanalyzer.ui.theme.BorderAccent
import com.example.voiceanalyzer.ui.theme.PrimaryButton
import com.example.voiceanalyzer.ui.theme.SurfaceCard
import com.example.voiceanalyzer.ui.theme.TextPrimary
import com.example.voiceanalyzer.ui.theme.TextSecondary
import com.example.voiceanalyzer.ui.theme.VisualizerWaveCyan

// ─── Main Screen ─────────────────────────────────────
@Composable
fun AnalyzerScreen(audioFilePath: String? = null,
                   viewModel: AnalyzerViewModel
) {
    val result by viewModel.result.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    val history by viewModel.history.collectAsState()
    val selectedHistoryResult by viewModel.selectedHistoryResult.collectAsState()

    LaunchedEffect(audioFilePath) {
        if (audioFilePath != null) {
            viewModel.analysis_audiofile(audioFilePath)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppBackground )
    ) {
        when {
            isLoading -> LoadingView()
            errorMsg != null -> ErrorView(errorMsg!!) {
                if (audioFilePath != null) {
                    viewModel.analysis_audiofile( audioFilePath)

                }
            }
            result != null -> ResultsViewWithBack(result!!) { viewModel.clearResult() }
            selectedHistoryResult != null -> ResultsViewWithBack(selectedHistoryResult!!) {
                viewModel.clearSelectedHistoryResult()
            }
            else -> HistoryListView(
                history = history,
                onItemClick = { viewModel.selectHistoryItem(it) },
                onDeleteClick = { viewModel.deleteHistoryItem(it) }
            )
        }
    }
}

// ─── Loading View ────────────────────────────────────
@Composable
fun LoadingView() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_alpha"
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = PrimaryButton,
            modifier = Modifier.size(60.dp),
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Analyzing your voice...",
            color = TextPrimary.copy(alpha = alpha),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "This may take 1-2 minutes",
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}

// ─── Empty View ──────────────────────────────────────
@Composable
fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No Audio Selected",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Record or select an audio file to analyze",
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}

// ─── Error View ──────────────────────────────────────
@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = Color(0xFFF44336),
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = Color(0xFFF44336), fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryButton
            )
        ) {
            Text("Retry")
        }
    }
}

// ─── Results View ────────────────────────────────────
@Composable
fun ResultsView(result: Analysis_data) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Overall Score (Circular Progress Gauge)
        OverallScoreCircular(result.overall_score, result.overall_grade, result.duration_seconds)

        Spacer(modifier = Modifier.height(16.dp))

        // Radar Chart
        RadarScoreChart(result)

        Spacer(modifier = Modifier.height(16.dp))

        // Transcription
        TranscriptionCard(result.transcription)

        Spacer(modifier = Modifier.height(16.dp))

        // Emotional Tone Card
        EmotionalToneCard(result.emotional_tone)

        Spacer(modifier = Modifier.height(16.dp))

        // Detailed Analysis header
        Text(
            "Detailed Analysis",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Grid Layout for detailed metrics
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                GridMetricCard("Clarity", Icons.Default.VolumeUp, result.clarity.score, result.clarity.feedback, Color(0xFF4CAF50))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                GridMetricCard("Fluency", Icons.Default.Speed, result.fluency.score, result.fluency.feedback, Color(0xFF2196F3))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                GridMetricCard("Grammar(Not Working )", Icons.Default.MenuBook, result.grammar.score, result.grammar.feedback, Color(0xFFFF9800))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                GridMetricCard("Confidence", Icons.Default.GraphicEq, result.confidence.score, result.confidence.feedback, Color(0xFF9C27B0))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fluency Details Grid Box
        FluencyDetailsGrid(result.fluency)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ─── Overall Score Card (Circular Radial Ring) ──────────
@Composable
fun OverallScoreCircular(score: Float, grade: String, duration: Float) {
    var animationPlayed by remember { mutableStateOf(false) }
    val curScore = animateFloatAsState(
        targetValue = if (animationPlayed) score else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "overall_score_animation"
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF2E2C54))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "Overall Performance",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                GradeBadge(grade)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Duration: ${String.format("%.1f", duration)}s",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                Canvas(modifier = Modifier.size(100.dp)) {
                    drawCircle(
                        color = BorderAccent,
                        radius = size.minDimension / 2,
                        style = Stroke(
                            width = 8.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                    drawArc(
                        color = PrimaryButton, // Neon purple arc
                        startAngle = -90f,
                        sweepAngle = (curScore.value / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(
                            width = 8.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${curScore.value.toInt()}",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/100",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ─── Radar Chart Card ────────────────────────────────
@Composable
fun RadarScoreChart(result: Analysis_data) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16152B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderAccent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Score Overview",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                factory = { context ->
                    RadarChart(context).apply {

                        // Data
                        val entries = listOf(
                            RadarEntry(result.clarity.score),
                            RadarEntry(result.fluency.score),
                            RadarEntry(result.grammar.score),
                            RadarEntry(result.confidence.score),
                            RadarEntry(result.emotional_tone.confidence * 100)
                        )

                        val dataSet = RadarDataSet(entries, "Your Score").apply {
                            color = AndroidColor.parseColor("#6C63FF")
                            fillColor = AndroidColor.parseColor("#6C63FF")
                            setDrawFilled(true)
                            fillAlpha = 80
                            lineWidth = 2f
                            setDrawHighlightCircleEnabled(true)
                            setDrawHighlightIndicators(false)
                        }

                        data = RadarData(dataSet).apply {
                            setValueTextColor(AndroidColor.WHITE)
                            setValueTextSize(8f)
                        }

                        // Labels
                        xAxis.valueFormatter = IndexAxisValueFormatter(
                            listOf("Clarity", "Fluency", "Grammar", "Confidence", "Tone")
                        )
                        xAxis.textColor = AndroidColor.WHITE
                        xAxis.textSize  = 12f

                        yAxis.apply {
                            setLabelCount(5, true)
                            textColor   = AndroidColor.WHITE
                            textSize    = 8f
                            axisMinimum = 0f
                            axisMaximum = 100f
                            setDrawLabels(true)
                        }

                        // Style
                        setBackgroundColor(AndroidColor.TRANSPARENT)
                        legend.textColor  = AndroidColor.WHITE
                        description.isEnabled = false
                        isRotationEnabled     = true

                        // Modern mesh lines styling
                        webColor = AndroidColor.parseColor("#3A3075")
                        webColorInner = AndroidColor.parseColor("#252052")
                        webLineWidth = 1f
                        webLineWidthInner = 1f
                        webAlpha = 180

                        animateXY(1000, 1000)
                        invalidate()
                    }
                }
            )
        }
    }
}

// ─── Grade Badge ─────────────────────────────────────
@Composable
fun GradeBadge(grade: String) {
    val color = when (grade) {
        "Excellent" -> Color(0xFF4CAF50)
        "Good"      -> Color(0xFF2196F3)
        "Average"   -> Color(0xFFFF9800)
        else        -> Color(0xFFF44336)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(grade, color = color, fontWeight = FontWeight.Bold)
    }
}

// ─── Grid Metric Card (2x2 Child Component) ─────────────
@Composable
fun GridMetricCard(
    title: String,
    icon: ImageVector,
    score: Float,
    feedback: String,
    accentColor: Color
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val progress = animateFloatAsState(
        targetValue = if (animationPlayed) score / 100f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "metric_score_animation"
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16152B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF2E2C54))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${score.toInt()}",
                    color = accentColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "score",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = Color(0xFF222046)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feedback,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                maxLines = 3
            )
        }
    }
}

// ─── Transcription Card with Copy Button ──────────────
@Composable
fun TranscriptionCard(text: String) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16152B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF2E2C54))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Transcription",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(text))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Text",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text, color = TextPrimary, fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
}

// ─── Emotional Tone Card (Accent Stripe Left) ──────────
@Composable
fun EmotionalToneCard(tone: EmotionalTone) {
    val toneColor = when (tone.tone) {
        "Confident"    -> Color(0xFF4CAF50)
        "Enthusiastic" -> Color(0xFFFFEB3B)
        "Neutral"      -> Color(0xFF2196F3)
        "Hesitant"     -> Color(0xFFFF9800)
        "Nervous"      -> Color(0xFFF44336)
        "Monotone"     -> Color(0xFF9E9E9E)
        else           -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16152B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF2E2C54))
    ) {
        // Change from intrinsicSize(IntrinsicSize.Min) to height(IntrinsicSize.Min)
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Accent left bar (uses the row's height)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(toneColor)
            )
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(toneColor.copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hearing,
                        contentDescription = null,
                        tint = toneColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Voice Tone Profile",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        tone.tone,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        tone.suggestion,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// ─── Fluency Details Side-by-Side Grid ─────────────────
@Composable
fun FluencyDetailsGrid(fluency: FluencyDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16152B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF2E2C54))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Speech Metrics",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FluencyStatBox("Pace (WPM)", "${fluency.wpm.toInt()}", "Ideal:110-150", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                FluencyStatBox("Fillers Words", "${fluency.filler_count}", "um, uh, like...", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                FluencyStatBox("Pauses", "${fluency.pause_count}", "0.3s silence", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FluencyStatBox(label: String, value: String, hint: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF222046), shape = RoundedCornerShape(12.dp))
            .border(1.dp, BorderAccent, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = VisualizerWaveCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(hint, color = TextSecondary, fontSize = 9.sp, maxLines = 1)
        }
    }
}

// ─── History List View (No Emoji) ──────────────────────
@Composable
fun HistoryListView(
    history: List<HistoryEntity>,
    onItemClick: (HistoryEntity) -> Unit,
    onDeleteClick: (HistoryEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Analysis History",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No analyses yet", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Analyze a recording to see results here", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(history) { item ->
                    HistoryItemCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        onDelete = { onDeleteClick(item) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun HistoryItemCard(item: HistoryEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    val dateStr = remember(item.timestamp) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16152B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF2E2C54))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(50.dp).clip(CircleShape)
                    .background(scoreColor(item.result.overall_score).copy(alpha = 0.2f))
            ) {
                Text(
                    "${item.result.overall_score.toInt()}",
                    color = scoreColor(item.result.overall_score),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.fileName.removeSuffix(".m4a"), color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(dateStr, color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.result.emotional_tone.tone, color = Color(0xFF6C63FF), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary)
            }
        }
    }
}

fun scoreColor(score: Float): Color {
    return when {
        score >= 90 -> Color(0xFF4CAF50)
        score >= 75 -> Color(0xFF2196F3)
        score >= 60 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}

@Composable
fun ResultsViewWithBack(result: Analysis_data, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analysis Result", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        ResultsView(result)
    }
}
