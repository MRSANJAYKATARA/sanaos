package com.sanaos.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply { isAntiAlias = true; color = 0xFF4CC9F0.toInt() }
    private var rms: Float = 0f

    fun setRms(value: Float) {
        rms = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val columns = 32
        val colWidth = w / columns
        for (i in 0 until columns) {
            val normalized = (rms / 10f).coerceIn(0f, 1f)
            val colHeight = normalized * h * (0.3f + (i % 5) * 0.1f)
            val left = i * colWidth + colWidth * 0.2f
            val right = (i + 1) * colWidth - colWidth * 0.2f
            val top = h - colHeight
            canvas.drawRoundRect(left, top, right, h, 6f, 6f, paint)
        }
    }
}
