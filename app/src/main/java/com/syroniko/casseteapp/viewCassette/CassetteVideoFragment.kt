package com.syroniko.casseteapp.viewCassette

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide

import com.syroniko.casseteapp.R


class CassetteVideoFragment : Fragment(), CassetteData {

    private val viewModel by activityViewModels<CassetteViewerViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cassette_video, container, false)

        val youTubeImageView = view.findViewById<ImageView>(R.id.youTubeImageView)
        val youTubeButton = view.findViewById<Button>(R.id.youTubeButton)

       viewModel.getTrackName {imageUrl->
           youTubeImageView.isEnabled = true
           Glide.with(this).load(imageUrl).into(youTubeImageView)
       }

        youTubeImageView.isEnabled = false

        youTubeImageView.setOnClickListener {
            viewModel.makeWebIntent {
                startActivity(it)
            }
        }

        youTubeButton.setOnClickListener{
            viewModel.ytResultsQuery()
        }

        return view
    }

}
