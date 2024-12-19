package com.example.a4starter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class LibraryFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_library, container, false)

        val arrayOfGestures = ArrayList<Gesture>()
        var adapter = GestureAdapter(this.requireContext(), arrayOfGestures, mViewModel)
        val listView = root.findViewById<ListView>(R.id.library_gesture_list)
        listView.adapter = adapter

        mViewModel!!.gestures.observe(viewLifecycleOwner, { s:ArrayList<Gesture> -> run{
            listView.adapter = GestureAdapter(this.requireContext(), s, mViewModel)
        } })

        return root
    }

    fun deleteGesture(i: Int){

    }
}