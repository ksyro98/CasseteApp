package com.syroniko.casseteapp.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syroniko.casseteapp.R


class PhotoPickerBottomSheetFragment(val buttonListener: (which: Int) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_picker_bottom_sheet, container, false)

        val pickFileButton = view.findViewById<Button>(R.id.pick_file_button)
        val takePictureButton = view.findViewById<Button>(R.id.take_picture_button)

        pickFileButton.setOnClickListener {
            buttonListener(0)
            dismiss()
        }

        takePictureButton.setOnClickListener {
            buttonListener(1)
            dismiss()
        }

        return view
    }

}