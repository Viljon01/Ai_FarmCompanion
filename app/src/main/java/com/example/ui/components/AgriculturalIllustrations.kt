package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AgriEarthBrown
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriGoldenOchre
import com.example.ui.theme.AgriLineArtGreen
import com.example.ui.theme.AgriSageGreen
import com.example.ui.theme.AgriSkyBlue
import com.example.ui.theme.AgriTomatoRed
import com.example.ui.theme.AgriWarningAmber

/**
 * Subtle agricultural background with thin line-art rolling hills,
 * gentle furrow curves, and serene landscape silhouettes.
 * Very faint to keep maximum text readability.
 */
@Composable
fun SubtleFarmBackground(
    modifier: Modifier = Modifier,
    lineColor: Color = AgriForestGreen.copy(alpha = 0.05f)
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Top right gentle sun rays & foliage outline
        val sunPath = Path().apply {
            moveTo(w * 0.75f, 0f)
            quadraticTo(w * 0.9f, h * 0.08f, w, h * 0.04f)
        }
        drawPath(sunPath, color = lineColor, style = Stroke(width = 1.5f))

        // Rolling farm hill 1
        val hillPath1 = Path().apply {
            moveTo(0f, h * 0.78f)
            cubicTo(w * 0.25f, h * 0.74f, w * 0.65f, h * 0.82f, w, h * 0.76f)
        }
        drawPath(hillPath1, color = lineColor, style = Stroke(width = 1.2f))

        // Rolling farm hill 2
        val hillPath2 = Path().apply {
            moveTo(0f, h * 0.88f)
            cubicTo(w * 0.35f, h * 0.85f, w * 0.75f, h * 0.91f, w, h * 0.87f)
        }
        drawPath(hillPath2, color = lineColor, style = Stroke(width = 1.2f))

        // Subtle soil furrows in bottom corner
        for (i in 1..4) {
            val furrow = Path().apply {
                val yOffset = h * (0.91f + (i * 0.02f))
                moveTo(w * 0.1f * i, h)
                quadraticTo(w * (0.3f + i * 0.1f), yOffset, w, yOffset - 10f)
            }
            drawPath(furrow, color = lineColor, style = Stroke(width = 1f))
        }
    }
}

/**
 * Stage Hand-Drawn Agricultural Line-Art Icons
 */
@Composable
fun StageAgriIcon(
    stageNumber: Int,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    tintColor: Color = AgriForestGreen,
    fillColor: Color = AgriSageGreen.copy(alpha = 0.2f)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        when (stageNumber) {
            1 -> {
                // 01 PLAN: Sprout in furrow soil
                // Soil furrow base
                val soil = Path().apply {
                    moveTo(w * 0.15f, h * 0.75f)
                    quadraticTo(w * 0.5f, h * 0.70f, w * 0.85f, h * 0.75f)
                }
                drawPath(soil, color = tintColor, style = Stroke(width = 2f, cap = StrokeCap.Round))

                // Stem
                val stem = Path().apply {
                    moveTo(w * 0.5f, h * 0.72f)
                    quadraticTo(w * 0.48f, h * 0.45f, w * 0.5f, h * 0.32f)
                }
                drawPath(stem, color = tintColor, style = Stroke(width = 2.2f, cap = StrokeCap.Round))

                // Left Leaf
                val leftLeaf = Path().apply {
                    moveTo(w * 0.5f, h * 0.48f)
                    cubicTo(w * 0.35f, h * 0.48f, w * 0.25f, h * 0.38f, w * 0.28f, h * 0.28f)
                    cubicTo(w * 0.42f, h * 0.28f, w * 0.48f, h * 0.38f, w * 0.5f, h * 0.48f)
                }
                drawPath(leftLeaf, color = fillColor)
                drawPath(leftLeaf, color = tintColor, style = Stroke(width = 1.8f))

                // Right Leaf
                val rightLeaf = Path().apply {
                    moveTo(w * 0.5f, h * 0.40f)
                    cubicTo(w * 0.65f, h * 0.40f, w * 0.75f, h * 0.30f, w * 0.72f, h * 0.20f)
                    cubicTo(w * 0.58f, h * 0.20f, w * 0.52f, h * 0.30f, w * 0.5f, h * 0.40f)
                }
                drawPath(rightLeaf, color = fillColor)
                drawPath(rightLeaf, color = tintColor, style = Stroke(width = 1.8f))
            }
            2 -> {
                // 02 GROW: Vigorous branching tomato vine with drip drops
                val vine = Path().apply {
                    moveTo(w * 0.5f, h * 0.85f)
                    cubicTo(w * 0.45f, h * 0.65f, w * 0.55f, h * 0.45f, w * 0.5f, h * 0.18f)
                }
                drawPath(vine, color = tintColor, style = Stroke(width = 2.4f, cap = StrokeCap.Round))

                // Left branch
                val b1 = Path().apply {
                    moveTo(w * 0.48f, h * 0.58f)
                    quadraticTo(w * 0.3f, h * 0.50f, w * 0.2f, h * 0.54f)
                }
                drawPath(b1, color = tintColor, style = Stroke(width = 1.8f, cap = StrokeCap.Round))

                // Left leaf
                val l1 = Path().apply {
                    moveTo(w * 0.2f, h * 0.54f)
                    cubicTo(w * 0.15f, h * 0.42f, w * 0.32f, h * 0.38f, w * 0.38f, h * 0.48f)
                    close()
                }
                drawPath(l1, color = fillColor)
                drawPath(l1, color = tintColor, style = Stroke(width = 1.5f))

                // Right branch & leaf
                val b2 = Path().apply {
                    moveTo(w * 0.52f, h * 0.42f)
                    quadraticTo(w * 0.7f, h * 0.35f, w * 0.8f, h * 0.38f)
                }
                drawPath(b2, color = tintColor, style = Stroke(width = 1.8f, cap = StrokeCap.Round))

                val l2 = Path().apply {
                    moveTo(w * 0.8f, h * 0.38f)
                    cubicTo(w * 0.85f, h * 0.26f, w * 0.68f, h * 0.24f, w * 0.62f, h * 0.34f)
                    close()
                }
                drawPath(l2, color = fillColor)
                drawPath(l2, color = tintColor, style = Stroke(width = 1.5f))
            }
            3 -> {
                // 03 MONITOR: Smartphone inspection viewfinder focusing on leaf
                // Frame / phone viewfinder corners
                val cornerLen = w * 0.16f
                // Top-left
                drawLine(tintColor, Offset(w * 0.15f, h * 0.2f), Offset(w * 0.15f + cornerLen, h * 0.2f), strokeWidth = 2f)
                drawLine(tintColor, Offset(w * 0.15f, h * 0.2f), Offset(w * 0.15f, h * 0.2f + cornerLen), strokeWidth = 2f)
                // Top-right
                drawLine(tintColor, Offset(w * 0.85f, h * 0.2f), Offset(w * 0.85f - cornerLen, h * 0.2f), strokeWidth = 2f)
                drawLine(tintColor, Offset(w * 0.85f, h * 0.2f), Offset(w * 0.85f, h * 0.2f + cornerLen), strokeWidth = 2f)
                // Bottom-left
                drawLine(tintColor, Offset(w * 0.15f, h * 0.8f), Offset(w * 0.15f + cornerLen, h * 0.8f), strokeWidth = 2f)
                drawLine(tintColor, Offset(w * 0.15f, h * 0.8f), Offset(w * 0.15f, h * 0.8f - cornerLen), strokeWidth = 2f)
                // Bottom-right
                drawLine(tintColor, Offset(w * 0.85f, h * 0.8f), Offset(w * 0.85f - cornerLen, h * 0.8f), strokeWidth = 2f)
                drawLine(tintColor, Offset(w * 0.85f, h * 0.8f), Offset(w * 0.85f, h * 0.8f - cornerLen), strokeWidth = 2f)

                // Leaf in center
                val leaf = Path().apply {
                    moveTo(w * 0.35f, h * 0.65f)
                    cubicTo(w * 0.32f, h * 0.40f, w * 0.55f, h * 0.32f, w * 0.68f, h * 0.34f)
                    cubicTo(w * 0.66f, h * 0.60f, w * 0.48f, h * 0.68f, w * 0.35f, h * 0.65f)
                }
                drawPath(leaf, color = fillColor)
                drawPath(leaf, color = tintColor, style = Stroke(width = 1.8f))
                drawLine(tintColor, Offset(w * 0.35f, h * 0.65f), Offset(w * 0.64f, h * 0.37f), strokeWidth = 1.5f)
            }
            4 -> {
                // 04 PROTECT: Agricultural shield with risk alert indicator
                val shield = Path().apply {
                    moveTo(w * 0.5f, h * 0.15f)
                    lineTo(w * 0.82f, h * 0.28f)
                    quadraticTo(w * 0.82f, h * 0.60f, w * 0.5f, h * 0.85f)
                    quadraticTo(w * 0.18f, h * 0.60f, w * 0.18f, h * 0.28f)
                    close()
                }
                drawPath(shield, color = fillColor)
                drawPath(shield, color = tintColor, style = Stroke(width = 2f, join = StrokeJoin.Round))

                // Alert mark inside
                drawLine(tintColor, Offset(w * 0.5f, h * 0.34f), Offset(w * 0.5f, h * 0.54f), strokeWidth = 2.4f, cap = StrokeCap.Round)
                drawCircle(tintColor, radius = 2f, center = Offset(w * 0.5f, h * 0.66f))
            }
            5 -> {
                // 05 HARVEST: Woven basket with ripe round tomatoes
                // Basket bowl
                val basket = Path().apply {
                    moveTo(w * 0.2f, h * 0.5f)
                    lineTo(w * 0.8f, h * 0.5f)
                    lineTo(w * 0.72f, h * 0.82f)
                    lineTo(w * 0.28f, h * 0.82f)
                    close()
                }
                drawPath(basket, color = AgriGoldenOchre.copy(alpha = 0.25f))
                drawPath(basket, color = AgriEarthBrown, style = Stroke(width = 2f, join = StrokeJoin.Round))

                // Cross weave lines
                drawLine(AgriEarthBrown, Offset(w * 0.36f, h * 0.5f), Offset(w * 0.40f, h * 0.82f), strokeWidth = 1.2f)
                drawLine(AgriEarthBrown, Offset(w * 0.64f, h * 0.5f), Offset(w * 0.60f, h * 0.82f), strokeWidth = 1.2f)

                // Tomatoes on top
                drawCircle(AgriTomatoRed, radius = w * 0.14f, center = Offset(w * 0.4f, h * 0.42f))
                drawCircle(tintColor, radius = w * 0.14f, center = Offset(w * 0.4f, h * 0.42f), style = Stroke(width = 1.5f))
                drawCircle(AgriTomatoRed, radius = w * 0.15f, center = Offset(w * 0.62f, h * 0.40f))
                drawCircle(tintColor, radius = w * 0.15f, center = Offset(w * 0.62f, h * 0.40f), style = Stroke(width = 1.5f))
            }
            6 -> {
                // 06 SELL: Farmer handshake with fair market exchange
                // Two shaking hands outline
                val leftHand = Path().apply {
                    moveTo(w * 0.15f, h * 0.52f)
                    lineTo(w * 0.38f, h * 0.52f)
                    lineTo(w * 0.48f, h * 0.44f)
                    lineTo(w * 0.55f, h * 0.50f)
                    lineTo(w * 0.42f, h * 0.64f)
                    lineTo(w * 0.15f, h * 0.64f)
                }
                drawPath(leftHand, color = fillColor)
                drawPath(leftHand, color = tintColor, style = Stroke(width = 2f, join = StrokeJoin.Round))

                val rightHand = Path().apply {
                    moveTo(w * 0.85f, h * 0.50f)
                    lineTo(w * 0.62f, h * 0.50f)
                    lineTo(w * 0.52f, h * 0.58f)
                    lineTo(w * 0.45f, h * 0.52f)
                    lineTo(w * 0.58f, h * 0.38f)
                    lineTo(w * 0.85f, h * 0.38f)
                }
                drawPath(rightHand, color = tintColor, style = Stroke(width = 2f, join = StrokeJoin.Round))

                // Subtle rupee coin / fair value glyph on top
                drawCircle(AgriGoldenOchre.copy(alpha = 0.3f), radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.24f))
                drawCircle(tintColor, radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.24f), style = Stroke(width = 1.5f))
            }
        }
    }
}

/**
 * Hand-drawn agricultural line illustrations for "What Farmers Need" section:
 * 1. Guidance (Phone + crop leaf)
 * 2. Location (Field boundary pin)
 * 3. Early Warning (Alert badge + leaf)
 * 4. Cost & Profit (Rupee coin + sprout)
 * 5. Practical Selling (Farmer produce scale & handshake)
 */
@Composable
fun FarmerNeedAgriIcon(
    needType: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        when (needType) {
            "guidance" -> {
                // Smartphone outline with gentle sprout inside
                val phone = Path().apply {
                    moveTo(w * 0.25f, h * 0.15f)
                    lineTo(w * 0.75f, h * 0.15f)
                    lineTo(w * 0.75f, h * 0.85f)
                    lineTo(w * 0.25f, h * 0.85f)
                    close()
                }
                drawPath(phone, color = AgriSageGreen.copy(alpha = 0.15f))
                drawPath(phone, color = AgriLineArtGreen, style = Stroke(width = 2f, join = StrokeJoin.Round))

                // Leaf inside screen
                val leaf = Path().apply {
                    moveTo(w * 0.40f, h * 0.65f)
                    cubicTo(w * 0.35f, h * 0.42f, w * 0.55f, h * 0.35f, w * 0.60f, h * 0.38f)
                    cubicTo(w * 0.62f, h * 0.58f, w * 0.50f, h * 0.68f, w * 0.40f, h * 0.65f)
                }
                drawPath(leaf, color = AgriForestGreen.copy(alpha = 0.3f))
                drawPath(leaf, color = AgriForestGreen, style = Stroke(width = 1.8f))
            }
            "location" -> {
                // Map location pin rooted into soil furrows
                val pin = Path().apply {
                    moveTo(w * 0.5f, h * 0.72f)
                    cubicTo(w * 0.32f, h * 0.52f, w * 0.25f, h * 0.38f, w * 0.25f, h * 0.28f)
                    cubicTo(w * 0.25f, h * 0.14f, w * 0.75f, h * 0.14f, w * 0.75f, h * 0.28f)
                    cubicTo(w * 0.75f, h * 0.38f, w * 0.68f, h * 0.52f, w * 0.5f, h * 0.72f)
                    close()
                }
                drawPath(pin, color = AgriSageGreen.copy(alpha = 0.18f))
                drawPath(pin, color = AgriForestGreen, style = Stroke(width = 2f))
                drawCircle(AgriForestGreen, radius = 3.5f, center = Offset(w * 0.5f, h * 0.28f))

                // Soil furrow base
                drawLine(AgriEarthBrown, Offset(w * 0.2f, h * 0.85f), Offset(w * 0.8f, h * 0.85f), strokeWidth = 2f, cap = StrokeCap.Round)
            }
            "early_warning" -> {
                // Triangular alert with agricultural leaf
                val triangle = Path().apply {
                    moveTo(w * 0.5f, h * 0.15f)
                    lineTo(w * 0.88f, h * 0.80f)
                    lineTo(w * 0.12f, h * 0.80f)
                    close()
                }
                drawPath(triangle, color = AgriWarningAmber.copy(alpha = 0.15f))
                drawPath(triangle, color = AgriWarningAmber, style = Stroke(width = 2f, join = StrokeJoin.Round))

                // Exclamation
                drawLine(AgriWarningAmber, Offset(w * 0.5f, h * 0.38f), Offset(w * 0.5f, h * 0.56f), strokeWidth = 2.4f, cap = StrokeCap.Round)
                drawCircle(AgriWarningAmber, radius = 2.2f, center = Offset(w * 0.5f, h * 0.68f))
            }
            "cost_profit" -> {
                // Rupee symbol circle integrated with seedling
                drawCircle(AgriGoldenOchre.copy(alpha = 0.2f), radius = w * 0.36f, center = Offset(w * 0.5f, h * 0.5f))
                drawCircle(AgriForestGreen, radius = w * 0.36f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = 2f))

                // Rupee line art symbol ₹
                drawLine(AgriForestGreen, Offset(w * 0.35f, h * 0.35f), Offset(w * 0.65f, h * 0.35f), strokeWidth = 2f, cap = StrokeCap.Round)
                drawLine(AgriForestGreen, Offset(w * 0.35f, h * 0.44f), Offset(w * 0.60f, h * 0.44f), strokeWidth = 2f, cap = StrokeCap.Round)
                val curve = Path().apply {
                    moveTo(w * 0.44f, h * 0.35f)
                    quadraticTo(w * 0.62f, h * 0.48f, w * 0.42f, h * 0.58f)
                    lineTo(w * 0.64f, h * 0.72f)
                }
                drawPath(curve, color = AgriForestGreen, style = Stroke(width = 2f, cap = StrokeCap.Round))
            }
            "selling" -> {
                // Balance scale with fresh produce
                drawLine(AgriForestGreen, Offset(w * 0.5f, h * 0.2f), Offset(w * 0.5f, h * 0.8f), strokeWidth = 2f, cap = StrokeCap.Round)
                drawLine(AgriForestGreen, Offset(w * 0.22f, h * 0.32f), Offset(w * 0.78f, h * 0.32f), strokeWidth = 2f, cap = StrokeCap.Round)

                // Left pan
                val pan1 = Path().apply {
                    moveTo(w * 0.16f, h * 0.58f)
                    quadraticTo(w * 0.28f, h * 0.68f, w * 0.40f, h * 0.58f)
                    close()
                }
                drawPath(pan1, color = AgriSageGreen.copy(alpha = 0.25f))
                drawPath(pan1, color = AgriForestGreen, style = Stroke(width = 1.8f))
                drawLine(AgriForestGreen, Offset(w * 0.28f, h * 0.32f), Offset(w * 0.28f, h * 0.58f), strokeWidth = 1.2f)

                // Right pan
                val pan2 = Path().apply {
                    moveTo(w * 0.60f, h * 0.58f)
                    quadraticTo(w * 0.72f, h * 0.68f, w * 0.84f, h * 0.58f)
                    close()
                }
                drawPath(pan2, color = AgriSageGreen.copy(alpha = 0.25f))
                drawPath(pan2, color = AgriForestGreen, style = Stroke(width = 1.8f))
                drawLine(AgriForestGreen, Offset(w * 0.72f, h * 0.32f), Offset(w * 0.72f, h * 0.58f), strokeWidth = 1.2f)
            }
        }
    }
}

/**
 * Weather Agricultural Illustration (Replaces generic icons)
 * "Sunny: Farmer + field + sun"
 * "Rain: Farmer + crop + rain clouds"
 * "High humidity: Crop + clouds + moisture illustration"
 */
@Composable
fun AgriWeatherIllustration(
    weatherType: String, // "sunny", "rain", "humidity"
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        when (weatherType) {
            "sunny" -> {
                // Rising morning sun with soft warm rays
                val sunCenter = Offset(w * 0.68f, h * 0.32f)
                drawCircle(AgriGoldenOchre.copy(alpha = 0.25f), radius = w * 0.22f, center = sunCenter)
                drawCircle(AgriGoldenOchre, radius = w * 0.16f, center = sunCenter, style = Stroke(width = 2f))

                // Sun rays
                for (angle in 0..7) {
                    val rad = Math.toRadians(angle * 45.0)
                    val x1 = sunCenter.x + (Math.cos(rad) * w * 0.20f).toFloat()
                    val y1 = sunCenter.y + (Math.sin(rad) * w * 0.20f).toFloat()
                    val x2 = sunCenter.x + (Math.cos(rad) * w * 0.28f).toFloat()
                    val y2 = sunCenter.y + (Math.sin(rad) * w * 0.28f).toFloat()
                    drawLine(AgriGoldenOchre, Offset(x1, y1), Offset(x2, y2), strokeWidth = 1.8f, cap = StrokeCap.Round)
                }

                // Farmer silhouette + field furrow
                val field = Path().apply {
                    moveTo(0f, h * 0.82f)
                    cubicTo(w * 0.3f, h * 0.76f, w * 0.7f, h * 0.85f, w, h * 0.78f)
                }
                drawPath(field, color = AgriForestGreen, style = Stroke(width = 2f))

                // Simple respectful farmer silhouette standing
                drawCircle(AgriForestGreen, radius = 5f, center = Offset(w * 0.3f, h * 0.52f)) // head with turban shape
                drawLine(AgriForestGreen, Offset(w * 0.3f, h * 0.56f), Offset(w * 0.3f, h * 0.76f), strokeWidth = 2.5f, cap = StrokeCap.Round) // body
                drawLine(AgriForestGreen, Offset(w * 0.3f, h * 0.62f), Offset(w * 0.42f, h * 0.68f), strokeWidth = 2f, cap = StrokeCap.Round) // arm pointing to field
            }
            "rain" -> {
                // Cloud outlines
                val cloud = Path().apply {
                    moveTo(w * 0.25f, h * 0.38f)
                    cubicTo(w * 0.2f, h * 0.25f, w * 0.45f, h * 0.18f, w * 0.55f, h * 0.26f)
                    cubicTo(w * 0.65f, h * 0.16f, w * 0.85f, h * 0.24f, w * 0.82f, h * 0.38f)
                    cubicTo(w * 0.90f, h * 0.45f, w * 0.85f, h * 0.55f, w * 0.75f, h * 0.55f)
                    lineTo(w * 0.25f, h * 0.55f)
                    cubicTo(w * 0.15f, h * 0.52f, w * 0.15f, h * 0.40f, w * 0.25f, h * 0.38f)
                }
                drawPath(cloud, color = AgriSkyBlue.copy(alpha = 0.2f))
                drawPath(cloud, color = AgriSkyBlue, style = Stroke(width = 2f, join = StrokeJoin.Round))

                // Slanted gentle raindrops
                val rainDrops = listOf(
                    Offset(w * 0.30f, h * 0.64f) to Offset(w * 0.25f, h * 0.76f),
                    Offset(w * 0.45f, h * 0.62f) to Offset(w * 0.40f, h * 0.75f),
                    Offset(w * 0.60f, h * 0.65f) to Offset(w * 0.55f, h * 0.77f),
                    Offset(w * 0.75f, h * 0.62f) to Offset(w * 0.70f, h * 0.74f)
                )
                rainDrops.forEach { (start, end) ->
                    drawLine(AgriSkyBlue, start, end, strokeWidth = 1.8f, cap = StrokeCap.Round)
                }

                // Small crop leaf receiving rain
                val crop = Path().apply {
                    moveTo(w * 0.5f, h * 0.92f)
                    lineTo(w * 0.5f, h * 0.82f)
                    cubicTo(w * 0.35f, h * 0.80f, w * 0.32f, h * 0.70f, w * 0.45f, h * 0.72f)
                }
                drawPath(crop, color = AgriForestGreen, style = Stroke(width = 2f, cap = StrokeCap.Round))
            }
            else -> {
                // "High humidity: Crop + clouds + moisture swirls"
                // Soft vapor cloud
                val cloud = Path().apply {
                    moveTo(w * 0.3f, h * 0.28f)
                    cubicTo(w * 0.35f, h * 0.18f, w * 0.65f, h * 0.18f, w * 0.7f, h * 0.28f)
                    cubicTo(w * 0.85f, h * 0.34f, w * 0.8f, h * 0.48f, w * 0.7f, h * 0.48f)
                    lineTo(w * 0.3f, h * 0.48f)
                    close()
                }
                drawPath(cloud, color = AgriSkyBlue.copy(alpha = 0.2f))
                drawPath(cloud, color = AgriSkyBlue, style = Stroke(width = 1.8f))

                // Moisture vapor swirls
                val swirl = Path().apply {
                    moveTo(w * 0.35f, h * 0.62f)
                    cubicTo(w * 0.45f, h * 0.52f, w * 0.55f, h * 0.66f, w * 0.68f, h * 0.55f)
                }
                drawPath(swirl, color = AgriSkyBlue, style = Stroke(width = 1.5f, cap = StrokeCap.Round))

                // Tomato plant with water droplets on leaf
                val plant = Path().apply {
                    moveTo(w * 0.5f, h * 0.92f)
                    lineTo(w * 0.5f, h * 0.68f)
                }
                drawPath(plant, color = AgriForestGreen, style = Stroke(width = 2.2f, cap = StrokeCap.Round))

                // Leaf with dew drop
                val leaf = Path().apply {
                    moveTo(w * 0.5f, h * 0.75f)
                    cubicTo(w * 0.7f, h * 0.72f, w * 0.78f, h * 0.60f, w * 0.72f, h * 0.54f)
                    cubicTo(w * 0.58f, h * 0.58f, w * 0.52f, h * 0.68f, w * 0.5f, h * 0.75f)
                }
                drawPath(leaf, color = AgriSageGreen.copy(alpha = 0.3f))
                drawPath(leaf, color = AgriForestGreen, style = Stroke(width = 1.8f))

                // Dew droplet hanging
                drawCircle(AgriSkyBlue, radius = 3f, center = Offset(w * 0.74f, h * 0.64f))
            }
        }
    }
}

/**
 * Leaf + Subtle AI Spark Symbol
 * (Explicit requirement: "Instead of a robot, use a simple green leaf + subtle AI spark symbol")
 */
@Composable
fun LeafAiSparkIcon(
    modifier: Modifier = Modifier,
    size: Dp = 26.dp,
    tintColor: Color = AgriForestGreen
) {
    // Subtle gentle breath animation for spark
    val transition = rememberInfiniteTransition(label = "spark_breath")
    val sparkAlpha by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spark_alpha"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Delicate green leaf
        val leaf = Path().apply {
            moveTo(w * 0.22f, h * 0.82f)
            cubicTo(w * 0.12f, h * 0.52f, w * 0.38f, h * 0.22f, w * 0.68f, h * 0.20f)
            cubicTo(w * 0.72f, h * 0.55f, w * 0.52f, h * 0.85f, w * 0.22f, h * 0.82f)
            close()
        }
        drawPath(leaf, color = AgriSageGreen.copy(alpha = 0.28f))
        drawPath(leaf, color = tintColor, style = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Center vein
        drawLine(tintColor, Offset(w * 0.22f, h * 0.82f), Offset(w * 0.62f, h * 0.25f), strokeWidth = 1.4f, cap = StrokeCap.Round)

        // Subtle 4-point AI spark at top-right
        val sparkCenter = Offset(w * 0.78f, h * 0.26f)
        val sparkSize = w * 0.15f
        val sparkColor = AgriGoldenOchre.copy(alpha = sparkAlpha)

        // Vertical spark spike
        drawLine(
            sparkColor,
            Offset(sparkCenter.x, sparkCenter.y - sparkSize),
            Offset(sparkCenter.x, sparkCenter.y + sparkSize),
            strokeWidth = 1.6f,
            cap = StrokeCap.Round
        )
        // Horizontal spark spike
        drawLine(
            sparkColor,
            Offset(sparkCenter.x - sparkSize, sparkCenter.y),
            Offset(sparkCenter.x + sparkSize, sparkCenter.y),
            strokeWidth = 1.6f,
            cap = StrokeCap.Round
        )
        // Tiny center gleam
        drawCircle(sparkColor, radius = 1.5f, center = sparkCenter)
    }
}
