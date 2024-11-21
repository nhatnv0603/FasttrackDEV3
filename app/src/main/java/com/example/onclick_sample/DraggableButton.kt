package com.example.onclick_sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DraggableButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLUE
        style = Paint.Style.FILL
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }

    private var buttonX = 200f // Initial X position
    private var buttonY = 200f // Initial Y position
    private val buttonRadius = 100f // Radius of the button

    private var isDragging = false // Flag to track drag state
    private var touchOffsetX = 0f
    private var touchOffsetY = 0f
    private var isLongPressed = false // Flag to track long press state
    private var isBlockButton = false // Flag to track long press state

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            if (isWithinButton(e.x, e.y)) {
                isLongPressed = !isLongPressed // Toggle the state
                isBlockButton = !isBlockButton
                paint.color = if (isLongPressed) Color.RED else Color.BLUE // Change color
                Log.d("DraggableButton", "Long press detected INSIDE button. isLongPressed=$isLongPressed")
                invalidate() // Redraw the button
            } else {
                Log.d("DraggableButton", "Long press detected OUTSIDE button. Ignored.")
            }
        }
    })

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the button as a circle
        canvas.drawCircle(buttonX, buttonY, buttonRadius, paint)

        // Draw text inside the button
        paint.color = Color.WHITE
        canvas.drawText("Drag", buttonX, buttonY + 15f, paint)

        paint.color = if (isLongPressed) Color.RED else Color.BLUE // Reset the paint color
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event) // Forward touch events to GestureDetector

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
//                isLongPressed = false // Reset state
//                paint.color = Color.BLUE // Reset color to default
                val dx = event.x - buttonX
                val dy = event.y - buttonY
                if (dx * dx + dy * dy <= buttonRadius * buttonRadius) {
                    isDragging = true
                    touchOffsetX = dx
                    touchOffsetY = dy
                    Log.d("DraggableButton", "ACTION_DOWN: x=${event.x}, y=${event.y}")
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging && !isBlockButton) {
                    buttonX = event.x - touchOffsetX
                    buttonY = event.y - touchOffsetY
                    Log.d("DraggableButton", "ACTION_MOVE: x=${event.x}, y=${event.y}")
                    invalidate() // Redraw the view
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    isDragging = false
                    Log.d("DraggableButton", "ACTION_UP: x=${event.x}, y=${event.y}")
                    invalidate() // Redraw the view
                    return true
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                Log.d("DraggableButton", "ACTION_CANCEL")
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // Check if the touch event is within the button's bounds
    private fun isWithinButton(x: Float, y: Float): Boolean {
        val dx = x - buttonX
        val dy = y - buttonY
        return dx * dx + dy * dy <= buttonRadius * buttonRadius
    }
}
