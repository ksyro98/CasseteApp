package com.syroniko.casseteapp.MainClasses

import android.R.attr.data
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.databinding.FragmentProfileBinding
import io.opencensus.stats.ViewData


class ProfileFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.userName = viewModel.user.name
        binding.userSongSentNumber = viewModel.user.songsSent.toString()
        binding.userSongsReceivedNumber = viewModel.user.songsAccepted.toString()
        binding.userFriendsNumber = viewModel.user.friends.size.toString()

        return view

    }

}