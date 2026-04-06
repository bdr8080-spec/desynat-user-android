package com.desynatuser.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class StoryImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    var segmentCount = 0
    var gapAngle = 10f
    var strokeWidth = 6f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#6E4527")
        strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {

        val center = width / 2f
        val gap = 8f

        val hasStory = segmentCount > 0

        // ✅ If no story → full image
        val imageRadius = if (hasStory) {
            center - strokeWidth - gap
        } else {
            center
        }

        val path = Path().apply {
            addCircle(center, center, imageRadius, Path.Direction.CW)
        }

        canvas.save()
        canvas.clipPath(path)

        super.onDraw(canvas)

        canvas.restore()

        if (!hasStory) return

        // ✅ Draw ring only when story exists
        val rect = RectF(
            center - imageRadius - gap,
            center - imageRadius - gap,
            center + imageRadius + gap,
            center + imageRadius + gap
        )

        val sweepAngle = (360f - (segmentCount * gapAngle)) / segmentCount

        paint.strokeWidth = strokeWidth

        var startAngle = -90f
        for (i in 0 until segmentCount) {
            canvas.drawArc(rect, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle + gapAngle
        }
    }
}