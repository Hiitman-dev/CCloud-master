package com.pira.ccloud.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ColorPickerCanvas(
    initialColor: Color,
    onColorConfirmed: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(GlassCorners.Card))
            .glassSurface(
                shape = RoundedCornerShape(GlassCorners.Card),
                tint = rememberGlassTint()
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tap to pick a custom color",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(initialColor)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .pointerInput(Unit) {
                        detectTapGestures { showDialog = true }
                    }
            )
        }
    }

    if (showDialog) {
        ColorPickerDialog(
            initialColor = initialColor,
            onColorConfirmed = { color ->
                onColorConfirmed(color)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun ColorPickerDialog(
    initialColor: Color,
    onColorConfirmed: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(initialColor.toArgb(), hsv)
    var hue by remember { mutableStateOf(hsv[0]) }
    var saturation by remember { mutableStateOf(hsv[1]) }
    var brightness by remember { mutableStateOf(hsv[2]) }
    val currentColor = Color.hsl(hue, saturation, brightness)

    GlassAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick Color", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(GlassCorners.Button))
                        .background(currentColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(GlassCorners.Button))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Hue", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                HueWheel(hue, { hue = it }, Modifier.fillMaxWidth().height(200.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Brightness", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                BrightnessSlider(hue, saturation, brightness, { brightness = it }, Modifier.fillMaxWidth().height(36.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Saturation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                SaturationSlider(hue, saturation, { saturation = it }, Modifier.fillMaxWidth().height(36.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = { onColorConfirmed(currentColor) },
                shape = RoundedCornerShape(GlassCorners.Button),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun HueWheel(hue: Float, onHueChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val dx = change.position.x - centerX
                    val dy = change.position.y - centerY
                    val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
                    onHueChange(((angle + 360) % 360).toFloat())
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val dx = offset.x - centerX
                    val dy = offset.y - centerY
                    val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
                    onHueChange(((angle + 360) % 360).toFloat())
                }
            }
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = min(centerX, centerY) - 8f
        val strokeWidth = 24f

        for (angle in 0 until 360) {
            val color = Color.hsl(angle.toFloat(), 1f, 0.5f)
            drawArc(
                color = color,
                startAngle = angle.toFloat() - 1f,
                sweepAngle = 3f,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
        }

        val indicatorAngle = Math.toRadians(hue.toDouble())
        val indicatorX = centerX + (radius * cos(indicatorAngle)).toFloat()
        val indicatorY = centerY + (radius * sin(indicatorAngle)).toFloat()

        drawCircle(color = Color.White, radius = 12f, center = Offset(indicatorX, indicatorY))
        drawCircle(color = Color.hsl(hue, 1f, 0.5f), radius = 9f, center = Offset(indicatorX, indicatorY))
    }
}

@Composable
private fun BrightnessSlider(hue: Float, saturation: Float, brightness: Float, onBrightnessChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(GlassCorners.Button))
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    onBrightnessChange((change.position.x / size.width).coerceIn(0.1f, 1f))
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onBrightnessChange((offset.x / size.width).coerceIn(0.1f, 1f))
                }
            }
    ) {
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(Color.Black, Color.hsl(hue, saturation, 0.5f), Color.White)),
            cornerRadius = CornerRadius(12f, 12f),
            size = size
        )
        val ix = brightness * size.width
        drawLine(color = Color.White, start = Offset(ix, 0f), end = Offset(ix, size.height), strokeWidth = 4f)
        drawCircle(color = Color.White, radius = 10f, center = Offset(ix, size.height / 2f))
        drawCircle(color = Color.hsl(hue, saturation, brightness), radius = 7f, center = Offset(ix, size.height / 2f))
    }
}

@Composable
private fun SaturationSlider(hue: Float, saturation: Float, onSaturationChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(GlassCorners.Button))
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    onSaturationChange((change.position.x / size.width).coerceIn(0f, 1f))
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onSaturationChange((offset.x / size.width).coerceIn(0f, 1f))
                }
            }
    ) {
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(Color.hsl(hue, 0f, 0.5f), Color.hsl(hue, 1f, 0.5f))),
            cornerRadius = CornerRadius(12f, 12f),
            size = size
        )
        val ix = saturation * size.width
        drawLine(color = Color.White, start = Offset(ix, 0f), end = Offset(ix, size.height), strokeWidth = 4f)
        drawCircle(color = Color.White, radius = 10f, center = Offset(ix, size.height / 2f))
        drawCircle(color = Color.hsl(hue, saturation, 0.5f), radius = 7f, center = Offset(ix, size.height / 2f))
    }
}

private fun Color.toArgb(): Int {
    return android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}
