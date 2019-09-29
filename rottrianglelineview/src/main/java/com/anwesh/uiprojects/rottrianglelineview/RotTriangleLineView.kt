package com.anwesh.uiprojects.rottrianglelineview

/**
 * Created by anweshmishra on 29/09/19.
 */

import android.view.View
import android.content.Context
import android.view.MotionEvent
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Color

val nodes : Int = 5
val lines : Int = 4
val scGap : Float = 0.01f
val strokeFactor : Float = 90f
val foreColor : Int = Color.parseColor("#E65100")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 40
val sizeFactor : Float = 2.9f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.x(deg : Float) : Float = this * Math.cos(deg * Math.PI / 180).toFloat()
fun Float.y(deg : Float) : Float = this * Math.sin(deg * Math.PI / 180).toFloat()


fun Canvas.drawTriPath(size : Float, deg : Float, paint : Paint) {
    val x : Float = size.x(deg)
    val y : Float = size.y(deg)
    val path : Path = Path()
    path.moveTo(0f, 0f)
    path.lineTo(x, 0f)
    path.lineTo(x, -y)
    path.lineTo(0f, 0f)
    drawPath(path, paint)
}

fun Canvas.drawRotTriLine(i : Int, size : Float, sc : Float, paint : Paint) {
    val deg : Float = 45f * sc.divideScale(i, lines)
    save()
    rotate(deg)
    drawLine(0f, 0f, size, 0f, paint)
    restore()
    drawTriPath(size / 2, deg, paint)
}

fun Canvas.drawRotTriLines(size : Float, sc : Float, paint : Paint) {
    for (j in 0..(lines - 1)) {
        drawRotTriLine(j, size, sc, paint)
    }
}

fun Canvas.drawRTLNode(i : Int, sc : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawRotTriLines(size, sc, paint)
    restore()
}

class RotTriangleLineView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }
}
