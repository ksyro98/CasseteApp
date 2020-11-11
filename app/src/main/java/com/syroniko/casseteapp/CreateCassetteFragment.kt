package com.syroniko.casseteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.syroniko.casseteapp.MainClasses.MainViewModel
import com.syroniko.casseteapp.TrackSearchFlow.SpotifyResultActivity


class CreateCassetteFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_cassette, container, false)

        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val resultsButton = view.findViewById<Button>(R.id.resultsButton)

        resultsButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            if (context != null) {
                SpotifyResultActivity.startActivity(requireContext(), searchQuery, viewModel.token)
            }
        }

        return view
    }

}