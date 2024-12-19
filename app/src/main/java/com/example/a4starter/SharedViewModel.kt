package com.example.a4starter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.math.sqrt

class SharedViewModel : ViewModel() {
    val gestures = MutableLiveData<ArrayList<Gesture>>()

    init {
        gestures.value = ArrayList<Gesture>()
    }

    fun addGesture(gesture: Gesture) {
        for(g in gestures.value!!){
            if(g.name == gesture.name){
                gestures.value?.remove(g)
                break
            }
        }
        gestures.value?.add(gesture)
    }

    fun deleteGesture(gesture: Gesture?){
        gestures.value?.remove(gesture)
    }

    fun calculateGesture(points: ArrayList<Pair<Float, Float>>): ArrayList<Pair<Gesture, Float>> {
        var scoreList = ArrayList<Pair<Gesture, Float>>()
        if(gestures.value != null){
            for(g: Gesture in gestures.value!!){
                var score = 0.0F
                val p = g.pointsOnPath
                for(k in points.indices){
                    if(k >= p.size) break
                    score += sqrt((points[k].first - p[k].first) * (points[k].first - p[k].first) + (points[k].second - p[k].second) * (points[k].second - p[k].second))
                }
                score /= points.size
                scoreList.add(Pair(g, score))
            }
        }
        val list = scoreList.sortedBy { it.second }
        var res = ArrayList<Pair<Gesture, Float>>()
        if(list.size >= 3){
            res.add(list[0])
            res.add(list[1])
            res.add(list[2])
        }
        else{
            res.addAll(list)
        }
        return res
    }
}