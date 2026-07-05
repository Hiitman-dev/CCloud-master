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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * A free-form HSV color picker: hue slider + saturation/value square + live
 * preview, so the user can choose literally any color instead of picking
 * from a fixed preset list.
 */
@Composable
fun ColorPickerCanvas(
    initialColor: Color,
    onColorConfirmed: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialHsv = remember(initialColor) {
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(
            (initialColor.red * 255).roundToInt(),
            (initialColor.green * 255).roundToInt(),
            (initialColor.blue * 255).roundToInt(),
            hsv
        )
        hsv
    }
    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    val currentColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value)))

    Column(modifier = modifier) {
        // Saturation / Value square for the currently selected hue
        val hueColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f)))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(hueColor)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val x = change.position.x.coerceIn(0f, size.width.toFloat())
                        val y = change.position.y.coerceIn(0f, size.height.toFloat())
                        saturation = x / size.width
                        value = 1f - (y / size.height)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        saturation = (offset.x / size.width).coerceIn(0f, 1f)
                        value = (1f - offset.y / size.height).coerceIn(0f, 1f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                // White (full saturation=0) -> transparent, left to right
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.White, Color.White.copy(alpha = 0f))
                    )
                )
                // Transparent -> Black, top to bottom
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black)
                    )
                )
                // Selection thumb
                val thumbX = saturation * size.width
                val thumbY = (1f - value) * size.height
                drawCircle(
                    color = Color.White,
                    radius = 10.dp.toPx(),
                    center = Offset(thumbX, thumbY),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color.Black.copy(alpha = 0.4f),
                    radius = 11.dp.toPx(),
                    center = Offset(thumbX, thumbY),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hue slider (rainbow)
        val hueColors = remember {
            (0..360 step 30).map { h ->
                Color(android.graphics.Color.HSVToColor(floatArrayOf(h.toFloat(), 1f, 1f)))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(hueColors))
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val x = change.position.x.coerceIn(0f, size.width.toFloat())
                        hue = (x / size.width) * 360f
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        hue = ((offset.x / size.width) * 360f).coerceIn(0f, 360f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(28.dp)) {
                val thumbX = (hue / 360f) * size.width
                drawCircle(
                    color = Color.White,
                    radius = size.height / 2f,
                    center = Offset(thumbX.coerceIn(size.height / 2f, size.width - size.height / 2f), size.height / 2f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preview + confirm
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(currentColor)
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
            Text(
                text = "#%06X".format(0xFFFFFF and currentColor.toArgb()),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { onColorConfirmed(currentColor) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Apply")
            }
        }
    }
}

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).roundToInt(),
    (red * 255).roundToInt(),
    (green * 255).roundToInt(),
    (blue * 255).roundToInt()
)
