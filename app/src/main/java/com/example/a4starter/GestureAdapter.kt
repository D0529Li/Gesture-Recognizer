package com.example.a4starter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.graphics.Path
import android.widget.ImageView
import android.graphics.PathMeasure
import kotlin.math.*


class Gesture (var path: Path, var pointsOnPath: ArrayList<Pair<Float, Float>>, var name: String?, var bitmap: Bitmap?){
    init {
        pointsOnPath = ArrayList<Pair<Float, Float>>()
        val pm = PathMeasure(path, false)
        var distance = 0f
        val speed = pm.length / 128
        var counter = 0
        val pos = FloatArray(2)

        while (distance < pm.length && counter < 128) {
            pm.getPosTan(distance, pos, null)
            pointsOnPath.add(Pair(pos[0], pos[1]))
            counter++
            distance += speed
        }

        // Rotate
        var centerX = 0.0f
        var centerY = 0.0f
        for(point in pointsOnPath){
            centerX += point.first
            centerY += point.second
        }
        centerX /= pointsOnPath.size
        centerY /= pointsOnPath.size
        val angle = atan((centerX-pointsOnPath[0].first)/(centerY-pointsOnPath[0].second))

        val temp = ArrayList<Pair<Float, Float>>()
        temp.clear()
        for(point in pointsOnPath){
            temp.add(Pair(point.first * cos(angle) - point.second * sin(angle), point.first * sin(angle) + point.second * cos(angle)))
        }
        pointsOnPath.clear()
        pointsOnPath.addAll(temp)

        // Scale and translate
        temp.clear()
        for(point in pointsOnPath){
            temp.add(Pair(point.first - centerX, point.second - centerY))
        }
        pointsOnPath.clear()
        pointsOnPath.addAll(temp)

        var maxX = 0.0f
        var maxY = 0.0f
        for(point in pointsOnPath){
            maxX = max(abs(point.first), maxX)
            maxY = max(abs(point.second), maxY)
        }
        val desiredMaxX = 100.0f
        val desiredMaxY = 100.0f
        val scaleX = desiredMaxX / maxX
        val scaleY = desiredMaxY / maxY
        temp.clear()
        for(point in pointsOnPath){
            temp.add(Pair(point.first * scaleX, point.second * scaleY))
        }
        pointsOnPath.clear()
        pointsOnPath.addAll(temp)
    }
}

class GestureAdapter (context: Context, gestures: ArrayList<Gesture>,
                      private val model: SharedViewModel?,
                      private val scores: ArrayList<Float>? = null) : ArrayAdapter<Gesture>(context, 0, gestures) {

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        var view: View? = convertView
        val gesture = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.gesture_row, parent, false)
        }

        val gestureImage = view?.findViewById<ImageView>(R.id.gesture_image)
        gestureImage?.setImageBitmap(gesture?.bitmap)
        gestureImage?.layoutParams?.height = 200
        gestureImage?.adjustViewBounds = true

        val gestureName = view?.findViewById<TextView>(R.id.gesture_name)
        gestureName?.text = gesture?.name

        if(scores == null){
            val deleteImage = view?.findViewById<ImageView>(R.id.delete_image)
            deleteImage?.setImageResource(R.drawable.delete)
            deleteImage?.adjustViewBounds = true
            deleteImage?.setOnClickListener {
                //model?.deleteGesture(getItem(position))
                this.remove(getItem(position))
            }
        }
        else{
            val gestureScore = view?.findViewById<TextView>(R.id.gesture_score)
            gestureScore?.text = "%.1f".format(scores[position])
        }
        
        // Return the completed view to render on screen
        return view!!
    }
}
