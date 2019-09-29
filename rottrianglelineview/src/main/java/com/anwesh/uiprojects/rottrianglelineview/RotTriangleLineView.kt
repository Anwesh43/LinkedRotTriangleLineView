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
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = foreColor 
    save()
    translate(w / 2, gap * (i + 1))
    drawRotTriLines(size, sc, paint)
    restore()
}

class RotTriangleLineView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class RTLNode(var i : Int, val state : State = State()) {

        private var prev : RTLNode? = null
        private var next : RTLNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = RTLNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawRTLNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : RTLNode {
            var curr : RTLNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class RotTriangleLine(var i : Int) {

        private val root : RTLNode = RTLNode(0)
        private var curr : RTLNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : RotTriangleLineView) {

        private val animator : Animator = Animator(view)
        private val rtl : RotTriangleLine = RotTriangleLine(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            rtl.draw(canvas, paint)
            animator.animate {
                rtl.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            rtl.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : RotTriangleLineView {
            val view : RotTriangleLineView = RotTriangleLineView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
