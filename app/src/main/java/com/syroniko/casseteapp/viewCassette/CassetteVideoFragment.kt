package com.syroniko.casseteapp.viewCassette

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.syroniko.casseteapp.MainClasses.toast

import com.syroniko.casseteapp.R
import kotlinx.android.synthetic.main.fragment_cassette_video.*
import org.json.JSONArray

/**
 * A simple [Fragment] subclass.
 */
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
