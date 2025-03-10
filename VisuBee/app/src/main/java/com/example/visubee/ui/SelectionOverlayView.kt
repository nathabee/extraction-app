package com.example.visubee.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

class SelectionOverlayView(context: Context, private val bitmap: Bitmap) : View(context) {

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var isDragging = false

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                endX = startX
                endY = startY
                isDragging = true
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                endX = event.x
                endY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                isDragging = false
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isDragging) {
            canvas.drawRect(startX, startY, endX, endY, paint)
        }
    }

    fun getSelectedRegion(): Bitmap? {
        if (startX == endX || startY == endY) return null // No selection made

        // Convert to actual image coordinates
        val scaleX = bitmap.width / width.toFloat()
        val scaleY = bitmap.height / height.toFloat()

        val x = (startX * scaleX).toInt().coerceIn(0, bitmap.width)
        val y = (startY * scaleY).toInt().coerceIn(0, bitmap.height)
        val width = ((endX - startX) * scaleX).toInt().coerceIn(1, bitmap.width - x)
        val height = ((endY - startY) * scaleY).toInt().coerceIn(1, bitmap.height - y)

        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }
}
