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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.x(deg : Float, r : Float) : Float = r * Math.cos(deg * Math.PI / 180).toFloat()
fun Float.y(deg : Float, r : Float) : Float = r * Math.sin(deg * Math.PI / 180).toFloat()
