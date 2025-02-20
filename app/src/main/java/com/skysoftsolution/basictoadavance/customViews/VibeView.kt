package com.skysoftsolution.basictoadavance.customViews
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class VibeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paint for drawing the circle
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4CAF50") // Default green color
        style = Paint.Style.FILL
    }

    // Circle properties
    private var circleRadius = 100f  // Initial radius
    private var maxRadius = 150f     // Max radius for pulsating effect
    private var minRadius = 50f      // Min radius
    private var pulseAnimator: ValueAnimator? = null

    init {
        setupAnimation()
    }

    // Set up pulsating animation
    private fun setupAnimation() {
        pulseAnimator = ValueAnimator.ofFloat(minRadius, maxRadius).apply {
            duration = 1500 // Duration of animation
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()

            addUpdateListener {
                circleRadius = it.animatedValue as Float
                invalidate() // Redraw the view
            }

            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get the center of the view
        val centerX = width / 2f
        val centerY = height / 2f

        // Draw the pulsating circle
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pulseAnimator?.cancel() // Stop the animation to prevent memory leaks
    }
}
