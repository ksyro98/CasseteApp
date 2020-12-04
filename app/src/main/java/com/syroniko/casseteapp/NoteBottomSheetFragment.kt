package com.syroniko.casseteapp

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syroniko.casseteapp.databinding.BottomSheetLayoutBinding
import com.syroniko.casseteapp.mainClasses.MainActivity
import com.syroniko.casseteapp.mainClasses.MainViewModel
import com.syroniko.casseteapp.profile.ProfileActivity
import com.syroniko.casseteapp.profile.ProfileViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NoteBottomSheetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoteBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var editText : EditText
    private val viewModel by activityViewModels<CreateCassetteViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val binding: BottomSheetLayoutBinding = DataBindingUtil.inflate(

            inflater, R.layout.bottom_sheet_layout, container, false
        )
        val view: View = binding.root
        val buttonMessageIsDone : TextView = view.findViewById(R.id.message_create_cassette_is_done_tv)
        buttonMessageIsDone.setOnClickListener{
            val manager = requireActivity().supportFragmentManager
            manager.beginTransaction().remove(this).commit()       }
        //    binding.lifecycleOwner = this.viewLifecycleOwner



       //     val viewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
    //        binding.bio = viewModel.user.value?.bio


         editText  = view.findViewById(R.id.leave_a_message_edit_text_bottom_sheet)
        editText.setText(viewModel.editTextLastWrittenText)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);


    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.editTextLastWrittenText= editText.text.toString()

    }

}