package com.syroniko.casseteapp.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.databinding.FragmentBioBottomSheetBinding
import com.syroniko.casseteapp.databinding.FragmentProfileBinding
import com.syroniko.casseteapp.mainClasses.MainActivity
import com.syroniko.casseteapp.mainClasses.MainViewModel

class BioBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentBioBottomSheetBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_bio_bottom_sheet, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this.viewLifecycleOwner

        if(activity is MainActivity){
            val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
            binding.bio = viewModel.user.bio
        }
        else if (activity is ProfileActivity){
            val viewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
            binding.bio = viewModel.user.value?.bio
        }


        return view
    }

}