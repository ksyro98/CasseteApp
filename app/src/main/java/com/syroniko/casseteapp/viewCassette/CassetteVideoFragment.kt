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

    private var videoId: String? = null
    private var trackName: String? = null
    private var cassetteId = ""
    private var senderId = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cassette_video, container, false)

        val youTubeImageView = view.findViewById<ImageView>(R.id.youTubeImageView)
        val youTubeButton = view.findViewById<Button>(R.id.youTubeButton)

        if(trackName != null) {
            getYTData(trackName!!)
        }
        else{
            val db = FirebaseFirestore.getInstance()

            db.collection("cassettes")
                .document(cassetteId)
                .get()
                .addOnSuccessListener { document ->
                    val trackMap = document.data?.get("track") as Map<*, *>
                    trackName = trackMap["trackName"] as String

                    getYTData(trackName!!)
                }
        }

        youTubeImageView.isEnabled = false
        youTubeImageView.setOnClickListener {
            if (videoId != null) {
                val ytAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ytAppWatchUrl + videoId))
                val ytWebIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ytWebWatchUrl + videoId))
                try {
                    startActivity(ytAppIntent)
                } catch (e: ActivityNotFoundException) {
                    startActivity(ytWebIntent)
                }
            }
            else{
                context?.toast("An error occurred while retrieving YouTube data.")
            }
        }

        youTubeButton.setOnClickListener{
            val ytIntent = Intent(Intent.ACTION_SEARCH)
            ytIntent.setPackage("com.google.android.youtube")
            ytIntent.putExtra("query", trackName)
            ytIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(ytIntent)
        }

        return view
    }

    private fun getYTData(trackName: String){
        val queue = Volley.newRequestQueue(context)

        Log.e(CassetteViewerActivity::class.java.simpleName, ytSearchUrlStart + trackName + ytSearchUrlEnd)

        val ytRequest = JsonObjectRequest(
            Request.Method.GET,
            ytSearchUrlStart + trackName.replace(" ", "+") + ytSearchUrlEnd,
            null,
            Response.Listener { response ->
                val items = response["items"] as JSONArray
                val firstSong = items.getJSONObject(0)
                val id = firstSong.getJSONObject("id")
                videoId = id.getString("videoId")
                youTubeImageView.isEnabled = true

                val snippet = firstSong.getJSONObject("snippet")
                val thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("medium")
                val imageUrl = thumbnail.getString("url")
                Log.d(CassetteViewerActivity::class.java.simpleName, imageUrl)
                Glide.with(this).load(imageUrl).into(youTubeImageView)
            },
            Response.ErrorListener { error ->
                Log.e(CassetteViewerActivity::class.java.simpleName, "Something went wrong... :(")
                Log.e(CassetteViewerActivity::class.java.simpleName, error.toString())
            })
        queue.add(ytRequest)
    }

    override fun getInitialCassetteData(cassetteId: String?, senderId: String?) {
        this.cassetteId = cassetteId.toString()
        this.senderId = senderId.toString()
    }

    override fun getCassetteDataFromDb(cassetteComment: String, trackName: String, trackId: String, trackPreviewUrl: String) {
        this.trackName = trackName
    }

}
