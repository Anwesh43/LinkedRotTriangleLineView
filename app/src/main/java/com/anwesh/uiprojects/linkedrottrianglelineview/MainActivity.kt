package com.anwesh.uiprojects.linkedrottrianglelineview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.rottrianglelineview.RotTriangleLineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RotTriangleLineView.create(this)
    }
}
