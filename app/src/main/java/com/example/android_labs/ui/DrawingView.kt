package com.example.android_labs.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.android_labs.data.DrawingPath

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var backgroundBitmap: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private var currentColor = Color.BLACK
    private var currentStrokeWidth = 5f
    private var currentPath = Path()
    private val paths = mutableListOf<DrawingPath>()
    private val borderPadding = 10f

    private val borderPaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.moveTo(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                paths.add(DrawingPath(Path(currentPath), currentColor, currentStrokeWidth))
                currentPath.reset()
                invalidate()
            }
        }
        return true
    }

    fun setColor(color: Int) {
        currentColor = color
    }

    fun setBrushSize(size: Float) {
        currentStrokeWidth = size
    }

    fun setBackgroundBitmap(bitmap: Bitmap?) {
        backgroundBitmap = bitmap?.let {
            Bitmap.createScaledBitmap(it, width, height, true)
        }
        invalidate()
    }

    fun clearAll() {
        paths.clear()
        currentPath.reset()
        invalidate()
    }

    fun getBitmap(): Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        Canvas(this).apply {
            backgroundBitmap?.let { drawBitmap(it, null, Rect(0, 0, width, height), null) }
            paths.forEach { path ->
                paint.color = path.color
                paint.strokeWidth = path.strokeWidth
                drawPath(path.path, paint)
            }
            paint.color = currentColor
            paint.strokeWidth = currentStrokeWidth
            drawPath(currentPath, paint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        backgroundBitmap?.let { canvas.drawBitmap(it, null, Rect(0, 0, width, height), null) }
        canvas.drawRect(borderPadding, borderPadding,
            width - borderPadding, height - borderPadding, borderPaint)

        paths.forEach { path ->
            paint.color = path.color
            paint.strokeWidth = path.strokeWidth
            canvas.drawPath(path.path, paint)
        }
        paint.color = currentColor
        paint.strokeWidth = currentStrokeWidth
        canvas.drawPath(currentPath, paint)
    }
}