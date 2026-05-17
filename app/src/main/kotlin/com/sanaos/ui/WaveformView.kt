package com.sanaos.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.sanaos.R
import kotlin.math.max
import kotlin.random.Random

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.neon_cyan)
        style = Paint.Style.FILL
    }
    private var rms: Float = 0f

    fun updateRms(value: Float) {
        rms = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bars = 5
        val spacing = width / (bars * 2f)
        val barWidth = spacing
        for (i in 0 until bars) {
            val amp = max(8f, rms * 6f + Random.nextFloat() * 12f)
            val left = spacing + i * spacing * 2
            val top = (height / 2f) - amp
            val right = left + barWidth
            val bottom = (height / 2f) + amp
            canvas.drawRoundRect(left, top, right, bottom, 8f, 8f, paint)
        }
    }
}
