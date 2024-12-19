package com.example.a4starter

import android.app.AlertDialog
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class AdditionFragment : Fragment() {
    private var mViewModel: SharedViewModel? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val root: View = inflater.inflate(R.layout.fragment_addition, container, false)

        val addButton = root.findViewById<Button>(R.id.addition_add_button)
        val clearButton = root.findViewById<Button>(R.id.addition_clear_button)

        addButton.setOnClickListener {
            val addNewGestureDialog = AlertDialog.Builder(requireView().context)
            addNewGestureDialog.setTitle("Gesture Name")

            val text = EditText(requireView().context)
            addNewGestureDialog.setView(text)

            val canvas = root.findViewById<CanvasView>(R.id.addition_recognition_canvas)
            addNewGestureDialog.setPositiveButton("OK") { dialog, id ->
                if(root.findViewById<CanvasView>(R.id.addition_recognition_canvas).path != null){
                    val bitmap = canvas.generateBitmap()
                    mViewModel!!.addGesture(Gesture(canvas.path as Path, canvas.pointsOnPath, text.text.toString(), bitmap))
                }
                canvas.clear()
            }

            addNewGestureDialog.setNegativeButton("CANCEL") { dialog, id -> }
            addNewGestureDialog.show()
        }

        clearButton.setOnClickListener {
            root.findViewById<CanvasView>(R.id.addition_recognition_canvas).clear()
        }
        return root
    }


}
