package com.example.a4starter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

// modified from public repo
@SuppressLint("AppCompatCustomView")

class CanvasView : View {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

    // drawing
    var path: Path? = null
    var paths: ArrayList<Path?> = ArrayList()
    var paintbrush = Paint(Color.BLUE)

    // we save a lot of points because they need to be processed
    // during touch events e.g. ACTION_MOVE
    var x1 = 0f
    var y1 = 0f
    var p1_id = 0
    var p1_index = 0

    // store cumulative transformations
    // the inverse matrix is used to align points with the transformations - see below
    var currentMatrix = Matrix()
    var inverse = Matrix()

    var pointsOnPath = ArrayList<Pair<Float, Float>>()

    // capture touch events (down/move/up) to create a path/stroke that we draw later
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount == 1) {
            p1_id = event.getPointerId(0)
            p1_index = event.findPointerIndex(p1_id)

            // invert using the current matrix to account for pan/scale
            // inverts in-place and returns boolean
            inverse = Matrix()
            currentMatrix.invert(inverse)

            // mapPoints returns values in-place
            var inverted = floatArrayOf(event.getX(p1_index), event.getY(p1_index))
            inverse.mapPoints(inverted)
            x1 = inverted[0]
            y1 = inverted[1]
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    paths.clear()
                    path = Path()
                    pointsOnPath.clear()
                    paths.add(path)
                    path!!.moveTo(x1, y1)
                    val p = Pair(x1, y1)
                    pointsOnPath.add(p)
                }
                MotionEvent.ACTION_MOVE -> {
                    path!!.lineTo(x1, y1)
                    pointsOnPath.add(Pair(x1, y1))
                }
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // apply transformations from the event handler above
        canvas.concat(currentMatrix)

        // draw lines over it
        for (path in paths) {
            canvas.drawPath(path!!, paintbrush)
        }
    }

    fun clear(){
        paths.clear()
        invalidate()
    }

    // reference: https://stackoverflow.com/questions/5536066/convert-view-to-bitmap-on-android
    // permitted from https://piazza.com/class/ks6bce85sw85nu?cid=698
    fun generateBitmap(): Bitmap {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = background
        if (bgDrawable != null)
        //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        else
        //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        // draw the view on the canvas
        draw(canvas)
        return returnedBitmap
    }

    // constructor
    init {
        paintbrush.style = Paint.Style.STROKE
        paintbrush.strokeWidth = 5f
        invalidate()
    }
}
