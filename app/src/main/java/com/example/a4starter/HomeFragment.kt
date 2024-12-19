package com.example.a4starter

import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class HomeFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle? ): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        
        val okButton = root.findViewById<Button>(R.id.home_ok_button)
        val clearButton = root.findViewById<Button>(R.id.home_clear_button)

        okButton.setOnClickListener {
            val canvas = root.findViewById<CanvasView>(R.id.home_recognition_canvas)
            val gesture = Gesture(canvas.path as Path, canvas.pointsOnPath, null, null)
            val matchedGestureScoreList = mViewModel!!.calculateGesture(gesture.pointsOnPath)
            val matchedGestureList = ArrayList<Gesture>()
            val matchedScoreList = ArrayList<Float>()
            for(pair in matchedGestureScoreList){
                matchedGestureList.add(pair.first)
                matchedScoreList.add(pair.second)
            }
            root.findViewById<ListView>(R.id.home_gesture_list).adapter = GestureAdapter(this.requireContext(), matchedGestureList, mViewModel, matchedScoreList)
        }

        clearButton.setOnClickListener {
            root.findViewById<CanvasView>(R.id.home_recognition_canvas).clear()
            root.findViewById<ListView>(R.id.home_gesture_list).adapter = GestureAdapter(this.requireContext(), ArrayList<Gesture>(), null, null)
        }

        return root
    }
}